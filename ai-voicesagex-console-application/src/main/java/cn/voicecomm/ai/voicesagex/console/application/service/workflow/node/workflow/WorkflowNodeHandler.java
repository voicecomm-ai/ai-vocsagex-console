package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.workflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.converter.WorkflowConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.UploadFilesMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowsExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentGroup;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunner;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunnerContext;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNode.McpParam;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsExperiencePo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/9/10 14:12
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class WorkflowNodeHandler extends BaseNodeHandler {

  private final ApplicationService applicationService;

  private final WorkflowConverter workflowConverter;

  private final GraphRunner graphRunner;
  private final WorkflowsExperienceMapper workflowsExperienceMapper;
  private final UploadFilesMapper uploadFilesMapper;

  /**
   * 运行
   *
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);

    WorkflowNode nodeData = JSONUtil.toBean(jsonObject, WorkflowNode.class);
    Integer workflowAppId = nodeData.getAppId();
    CommonRespDto<Boolean> respDto = applicationService.checkOnShelf(workflowAppId);
    if (respDto.getData() == null) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error(respDto.getMsg())
          .error_type("WorkflowNodeError")
          .build();
    }
    List<McpParam> param = nodeData.getParam();
    JSONObject args = new JSONObject();
    if (param != null) {
      for (McpParam p : param) {
        if (p == null) {
          continue;
        }
        String name = p.getName();
        if (name == null) {
          continue;
        }

        Object rawValue = p.getValue();
        if (rawValue == null) {
          if (p.getRequired()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 为空")
                .error_type("McpNodeError")
                .build();
          }
          args.set(name, null);
          continue;
        }

        Object finalValue;

        // 先把模板变量替换成字符串（仅对 String 或需要模板处理的值）
        if ("Variable".equalsIgnoreCase(p.getValue_type())) {
          String instr = String.valueOf(rawValue);
          SegmentGroup segmentGroup = variablePool.convertTemplate(instr);
          Set<Segment> collect = segmentGroup.getValue().stream().filter(Objects::nonNull)
              .collect(Collectors.toSet());
          if (p.getType().equalsIgnoreCase("File")) {
            File file = (File) segmentGroup.getValue().getFirst().getValue();
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileType", file.getExtension());
            fileMap.put("name", file.getFilename());
            fileMap.put("size", file.getSize());
            fileMap.put("transfer_method", file.getTransfer_method().getValue());
            fileMap.put("type", file.getType());
            fileMap.put("upload_file_id", Integer.parseInt(file.getRelated_id()));
            finalValue = JSONUtil.parseObj(fileMap);
          } else if (p.getType().equalsIgnoreCase("array[file]")) {
            List<File> fileList = (List<File>) segmentGroup.getValue().getFirst().getValue();
            List<JSONObject> fileListJson = new ArrayList<>();
            fileList.forEach(file -> {
              Map<String, Object> fileMap = new HashMap<>();
              fileMap.put("fileType", file.getExtension());
              fileMap.put("name", file.getFilename());
              fileMap.put("size", file.getSize());
              fileMap.put("transfer_method", file.getTransfer_method().getValue());
              fileMap.put("type", file.getType());
              fileMap.put("upload_file_id", Integer.parseInt(file.getRelated_id()));
              fileListJson.add(JSONUtil.parseObj(fileMap));
            });
            finalValue = JSONUtil.parseArray(fileListJson);
          } else {
            if (CollUtil.isEmpty(collect) && p.getRequired()) {
              return NodeRunResult.builder()
                  .status(WorkflowNodeExecutionStatus.FAILED)
                  .error("参数 " + name + " 不能为空")
                  .error_type("McpNodeError")
                  .build();
            }
            finalValue = segmentGroup.getText();
          }
        } else {
          finalValue = rawValue; // 常量直接取
        }
        if (finalValue == null || "".equals(finalValue)) {
          if (p.getRequired()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 不能为空")
                .error_type("McpNodeError")
                .build();
          }
          continue;
        }

        // 再根据 type 强转为对应的数据类型
        switch (p.getType().toLowerCase()) {
          case "number":
            try {
              if (finalValue instanceof Number) {
                args.set(name, finalValue);
              } else {
                args.set(name, new BigDecimal(finalValue.toString()));
              }
            } catch (Exception e) {
              log.warn("参数 {} 转换为 number 失败，值: {}", name, finalValue, e);
              args.set(name, finalValue);
            }
            break;
          case "array[string]":
          case "array[number]":
          case "array[object]":
            try {
              if (finalValue instanceof Collection) {
                args.set(name, finalValue);
              } else {
                // 尝试把 JSON 数组字符串转为 List
                args.set(name, JSONUtil.parseArray(finalValue.toString()));
              }
            } catch (Exception e) {
              log.warn("参数 {} 转换为 array 失败，值: {}", name, finalValue, e);
              args.set(name, finalValue);
            }
            break;
          case "boolean":
            args.set(name, Boolean.parseBoolean(finalValue.toString()));
            break;
          case "object":
            args.set(name, JSONUtil.parseObj(finalValue));
            break;
          case "string":
          default:
            args.set(name, finalValue);
            break;
        }
      }
    }
    WorkflowsExperiencePo workflowsExperiencePo = workflowsExperienceMapper.selectOne(
        Wrappers.<WorkflowsExperiencePo>lambdaQuery()
            .eq(WorkflowsExperiencePo::getApp_id, workflowAppId));
    WorkflowPo workflowPo = workflowConverter.experiencePoToWorkflow(workflowsExperiencePo);
    CommonRespDto<GraphRunnerContext> contextCommonRespDto = graphRunner.runWorkflow(
        UUID.randomUUID().toString(), workflowPo, args, ApplicationStatusEnum.EXPERIENCE,
        workflowAppId);
    if (!contextCommonRespDto.isOk()) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(args)
          .error(contextCommonRespDto.getMsg())
          .error_type("WorkflowNodeError")
          .retry_index(0)
          .build();
    }
    if (contextCommonRespDto.getData().getInterrupted().get()) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(args)
          .error(contextCommonRespDto.getData().getError())
          .error_type("WorkflowNodeError")
          .retry_index(0)
          .build();
    }
    GraphRunnerContext graphRunnerContext = contextCommonRespDto.getData();
    // 根据时间整理成LinkedHashMap
    LinkedHashMap<String, Object> outputMap = new LinkedHashMap<>();
    // 按 insertTime 排序
    graphRunnerContext.getOutputMapWithTime().entrySet().stream()
        .sorted(Comparator.comparing(e -> e.getValue().insertTime()))
        .forEach(e -> outputMap.put(e.getKey(), e.getValue().value()));
    List<File> files = new ArrayList<>();
    // 遍历 outputMap，把 File 类型提取
    Iterator<Entry<String, Object>> iterator = outputMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> entry = iterator.next();
      if (entry.getValue() instanceof File) {
        files.add((File) entry.getValue());
        iterator.remove(); // 从 outputMap 中删除
      }
    }
    LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("text", JSONUtil.toJsonStr(outputMap));
    outputs.put("files", files);
    outputs.put("json", CollUtil.newArrayList(outputMap));
    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .inputs(args)
        .process_data(null)
        .outputs(outputs)
        .build();
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    WorkflowNode nodeData = JSONUtil.toBean(jsonObject, WorkflowNode.class);

    List<VariableSelector> variableSelectors = new ArrayList<>();
    if (CollUtil.isNotEmpty(nodeData.getParam())) {
      for (McpParam param : nodeData.getParam()) {
        if ("Variable".equals(param.getValue_type())) {
          Object value = param.getValue();
          if (value == null) {
            continue;
          }
          VariableTemplateParser parser = new VariableTemplateParser(value.toString());
          variableSelectors.addAll(parser.extractVariableSelectors());
        }
      }
    }
    for (VariableSelector selector : variableSelectors) {
      map.put(
          selector.getVariable(),
          new ArrayList<>(selector.getValueSelector())
      );
    }

    return map.entrySet().stream()
        .collect(Collectors.toMap(
            entry -> node.getStr("id") + "." + entry.getKey(),
            Map.Entry::getValue
        ));
  }
}

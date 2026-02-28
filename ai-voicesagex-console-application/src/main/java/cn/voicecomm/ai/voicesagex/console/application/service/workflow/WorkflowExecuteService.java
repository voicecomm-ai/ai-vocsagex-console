package cn.voicecomm.ai.voicesagex.console.application.service.workflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionTriggeredFrom;
import cn.voicecomm.ai.voicesagex.console.application.converter.WorkflowConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowNodeExecutionsMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowsExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowsPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.SystemVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunner;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.NodeHandlerMapping;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.FileRebuildUtils;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.SseEmitterManager;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableLoader;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExecuteService {

  private final WorkflowMapper workflowMapper;

  private final WorkflowConverter workflowConverter;
  private final WorkflowNodeExecutionsMapper workflowNodeExecutionsMapper;

  private final FileRebuildUtils fileRebuildUtils;

  private final SseEmitterManager sseEmitterManager;

  private final GraphRunner graphRunner;
  private final WorkflowsPublishHistoryMapper workflowsPublishHistoryMapper;
  private final WorkflowsExperienceMapper workflowsExperienceMapper;
  private final ApplicationService applicationService;

  @DubboReference
  public BackendUserService backendUserService;


  public CommonRespDto<WorkflowNodeExecutionsDto> singleNodeRun(Integer appId, String nodeId,
      JSONObject userInput) {
    try {
      CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(appId);
      if (!dtoCommonRespDto.isOk()) {
        return CommonRespDto.error(dtoCommonRespDto.getMsg());
      }
      // 获取工作流
      WorkflowPo workflowPo = workflowMapper.selectOne(
          Wrappers.<WorkflowPo>lambdaQuery().eq(WorkflowPo::getApp_id, appId));
      if (workflowPo == null) {
        return CommonRespDto.error("未找到对应工作流");
      }
      String workflowRunId = UUID.randomUUID().toString();
      JSONObject graph = JSONUtil.parseObj(workflowPo.getGraph());
      // 根据节点ID中提取节点配置
      JSONArray nodes = JSONUtil.getByPath(graph, "nodes", JSONUtil.createArray());
      Optional<Object> nodeOpt = nodes.stream().filter(nodeObj -> {
        JSONObject nodeJson = (JSONObject) nodeObj;
        return nodeId.equals(nodeJson.getStr("id"));
      }).findFirst();
      if (nodeOpt.isEmpty()) {
        return CommonRespDto.error("未找到对应节点");
      }
      JSONObject node = (JSONObject) nodeOpt.get();
      // 获取节点类型
      String nodeType = JSONUtil.getByPath(node, "data.type", "");

      BaseNodeHandler nodeHandler = NodeHandlerMapping.getNodeHandler(nodeType);
      // 初始化变量池
      VariablePool variablePool = new VariablePool().setEnvironment_variables(
          JSONUtil.toList(JSONUtil.parseArray(workflowPo.getEnvironment_variables()),
              Variable.class));
      if (NodeType.START.getValue().equals(nodeType)) {
        StartNode startNodeData = JSONUtil.toBean(JSONUtil.getByPath(node, "data", ""),
            StartNode.class);
        // 重建用户输入中的文件
        Map<String, Object> userInputs = fileRebuildUtils.rebuildFileForUserInputsInStartNode("",
            startNodeData, userInput);
        variablePool._setupVariablePool("", new ArrayList<>(), UserAuthUtil.getUserId(), userInputs,
            workflowPo, new ArrayList<>(), nodeType, "");
      } else {
        variablePool.initPool(SystemVariable.empty(), userInput,
            JSONUtil.toList(workflowPo.getEnvironment_variables(), Variable.class),
            new ArrayList<>());
      }

      Map<String, List<String>> variableMapping = nodeHandler._extractVariableSelectorToVariableMapping(
          node, graph, nodeId);
      // 在此处加载来自草稿的丢失变量，然后将其设置为VARIABLE_POOL
      VariableLoader.loadIntoVariablePool(null, variablePool, variableMapping, userInput);

      mappingUserInputsToVariablePool(variableMapping, userInput, variablePool);

      long startTime = System.currentTimeMillis();

      NodeRunResult nodeRunResult = nodeHandler.run(variablePool, node, graph, workflowRunId,
          appId);

      WorkflowNodeExecutionsDto workflowNodeExecutionsDto = handleNodeRunResult(appId, node,
          nodeRunResult, startTime, workflowRunId);
      if (workflowNodeExecutionsDto != null && workflowNodeExecutionsDto.getCreatedBy() != null) {
        BackendUserDto backendUserDto = backendUserService.getUserInfo(
            workflowNodeExecutionsDto.getCreatedBy()).getData();
        workflowNodeExecutionsDto.setExecutor_name(backendUserDto.getAccount());
        workflowNodeExecutionsDto.setElapsed_time(
            (System.currentTimeMillis() - startTime) / 1000.00);

        if (workflowNodeExecutionsDto.getNode_type().equals(NodeType.LOOP.getValue())
            || workflowNodeExecutionsDto.getNode_type().equals(NodeType.ITERATION.getValue())) {
          List<WorkflowNodeExecutionsPo> poList = workflowNodeExecutionsMapper.selectList(
              Wrappers.<WorkflowNodeExecutionsPo>lambdaQuery()
                  .eq(WorkflowNodeExecutionsPo::getWorkflow_run_id, workflowRunId)
                  .eq(WorkflowNodeExecutionsPo::getPredecessor_node_id, nodeId)
          );
          List<WorkflowNodeExecutionsDto> dtoList = workflowConverter.nodeExecutionPoToDto(poList);
          if (CollUtil.isEmpty(dtoList)) {
            return CommonRespDto.success(workflowNodeExecutionsDto);
          }
          // 先按 loop_index 分组
          Map<Integer, List<WorkflowNodeExecutionsDto>> loopIndexMap = dtoList.stream()
              .collect(Collectors.groupingBy(WorkflowNodeExecutionsDto::getLoop_index));
          if (workflowNodeExecutionsDto.getLoopList() == null) {
            workflowNodeExecutionsDto.setLoopList(new ArrayList<>());
          }

          // 遍历每个 loop_index，按顺序组装
          loopIndexMap.keySet().stream().sorted().forEach(loopIndex -> {
            List<WorkflowNodeExecutionsDto> childList = loopIndexMap.get(loopIndex);
            // 按 createTime 排序
            childList.sort(Comparator.comparing(WorkflowNodeExecutionsDto::getCreateTime));

            // 创建当前轮壳（拷贝父节点，初始化 loopList）
            Optional<WorkflowNodeExecutionsDto> first = childList.stream()
                .filter(dto -> dto.getNode_type().equals(workflowNodeExecutionsDto.getNode_type()))
                .findFirst();
            WorkflowNodeExecutionsDto loopShell = new WorkflowNodeExecutionsDto();
            if (first.isPresent()) {
              loopShell = first.get();
            }
            List<WorkflowNodeExecutionsDto> list = childList.stream()
                .filter(e -> !e.getNode_type().equals(workflowNodeExecutionsDto.getNode_type()))
                .toList();
            // 把本轮子节点放入壳的 loopList
            loopShell.getLoopList().addAll(list);

            // 放入父节点 loopList
            workflowNodeExecutionsDto.getLoopList().add(loopShell);
          });
        }
      }
      return CommonRespDto.success(workflowNodeExecutionsDto);
    } catch (Exception e) {
      log.error("节点运行失败:{}", e.getMessage(), e);
      return CommonRespDto.error(e.getMessage());
    }
  }

  /**
   * 工作流运行
   *
   * @param workflowRunId 运行ID
   * @param appId         应用ID
   * @param userInputs    用户输入
   * @param statusEnum
   * @return 运行结果
   */
  public void draftWorkflowRun(String workflowRunId, Integer appId, JSONObject userInputs,
      ApplicationStatusEnum statusEnum) {
    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(appId);
    if (!dtoCommonRespDto.isOk()) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().id(workflowRunId).name("error").data(dtoCommonRespDto.getMsg()));
      return;
    }
    WorkflowPo workflowPo = null;
    if (statusEnum.equals(ApplicationStatusEnum.DRAFT)) {
      workflowPo = workflowMapper.selectOne(
          Wrappers.<WorkflowPo>lambdaQuery().eq(WorkflowPo::getApp_id, appId));
    } else if (statusEnum.equals(ApplicationStatusEnum.PUBLISHED)) {
      WorkflowsPublishHistoryPo workflowsPublishHistoryPo = workflowsPublishHistoryMapper.selectOne(
          Wrappers.<WorkflowsPublishHistoryPo>lambdaQuery()
              .eq(WorkflowsPublishHistoryPo::getApp_id, appId)
              .orderByDesc(WorkflowsPublishHistoryPo::getCreate_time)
              .last("LIMIT 1"));
      workflowPo = workflowConverter.publishPoToWorkflow(workflowsPublishHistoryPo);
    } else if (statusEnum.equals(ApplicationStatusEnum.EXPERIENCE)) {
      WorkflowsExperiencePo workflowsExperiencePo = workflowsExperienceMapper.selectOne(
          Wrappers.<WorkflowsExperiencePo>lambdaQuery()
              .eq(WorkflowsExperiencePo::getApp_id, appId));
      workflowPo = workflowConverter.experiencePoToWorkflow(workflowsExperiencePo);
    }
    if (workflowPo == null) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().id(workflowRunId).name("error").data("未找到工作流！"));
      return;
    }
    try {
      graphRunner.runWorkflow(workflowRunId, workflowPo, userInputs, statusEnum, appId);
    } catch (Exception e) {
      log.error("执行流程异常{}", e.getMessage(), e);
    }
  }

  private WorkflowNodeExecutionsDto handleNodeRunResult(Integer appId, JSONObject node,
      NodeRunResult nodeRunResult, Long startTime, String workflowRunId) {
    Integer toolAppId =
        node.getJSONObject("data").getStr("type").equals("agent") || node.getJSONObject("data")
            .getStr("type").equals("workflow") ? node.getJSONObject("data").getInt("appId")
            : null;
    if (node.getJSONObject("data").getStr("type").equals("mcp")) {
      toolAppId = node.getJSONObject("data").getInt("mcp_id");
    }
    WorkflowNodeExecutionsPo build = WorkflowNodeExecutionsPo.builder().id(null).app_id(appId)
        .workflow_id(appId).workflow_run_id(workflowRunId)
        .triggered_from(WorkflowNodeExecutionTriggeredFrom.SINGLE_STEP.getValue()).index(1)
        .node_execution_id(node.getStr("id")).node_id(node.getStr("id"))
        .node_type(node.getJSONObject("data").getStr("type"))
        .title(node.getJSONObject("data").getStr("title"))
        .inputs(JacksonUtil.toJsonStr(nodeRunResult.getInputs()))
        .process_data(JacksonUtil.toJsonStr(nodeRunResult.getProcess_data()))
        .outputs(JacksonUtil.toJsonStr(nodeRunResult.getOutputs()))
        .status(nodeRunResult.getStatus().getValue()).error(nodeRunResult.getError())
        .elapsed_time((System.currentTimeMillis() - startTime) / 1000.00)
        .execution_metadata(JacksonUtil.toJsonStr(nodeRunResult.getMetadata()))
        .toolAppId(toolAppId)
        .finished_at(LocalDateTime.now()).createdBy(UserAuthUtil.getUserId()).build();
    workflowNodeExecutionsMapper.insert(build);
    return workflowConverter.nodeExecutionPoToDto(build);
  }


  /**
   * 将用户输入映射到变量池
   *
   * @param variableMapping 变量映射关系
   * @param userInputs      用户输入
   * @param variablePool    变量池
   */
  public void mappingUserInputsToVariablePool(Map<String, List<String>> variableMapping,
      JSONObject userInputs, VariablePool variablePool) {
    for (Entry<String, List<String>> entry : variableMapping.entrySet()) {
      String nodeVariable = entry.getKey();
      List<String> variableSelector = entry.getValue();

      // 从节点变量中获取节点ID和变量键
      String[] nodeVariableList = nodeVariable.split("\\.");
      if (nodeVariableList.length < 1) {
        throw new IllegalArgumentException("Invalid node variable: " + nodeVariable);
      }

      // 构建节点变量键（去掉第一个元素后的所有元素用点连接）
      String nodeVariableKey = Arrays.stream(nodeVariableList).skip(1)
          .collect(Collectors.joining("."));

      // 检查变量是否存在于用户输入中或变量池中
      boolean existsInVariablePool = variablePool.get(variableSelector) != null;

      // 环境变量已存在于变量池中，不从用户输入获取
      if (existsInVariablePool) {
        continue;
      }

      // 从变量选择器中获取变量节点ID
      String variableNodeId = variableSelector.getFirst();
      List<String> variableKeyList = new ArrayList<>(
          variableSelector.subList(1, variableSelector.size()));

      // 获取输入值
      Object inputValue = userInputs.get("#" + StrUtil.join(".", variableSelector) + "#");
      if (inputValue == null) {
        inputValue = userInputs.get(nodeVariableKey);
      }
      // 处理文件类型的输入值
      if (fileRebuildUtils.isFileMapping(inputValue)) {
        inputValue = fileRebuildUtils.buildFromMapping((Map<String, Object>) inputValue, "", null,
            false);
      }
      if (fileRebuildUtils.isFileMappingList(inputValue)) {
        inputValue = fileRebuildUtils.buildFromMappings((List<Map<String, Object>>) inputValue, "",
            null, false);
      }

      // 将变量和值添加到变量池
      List<String> fullVariableSelector = new ArrayList<>();
      fullVariableSelector.add(variableNodeId);
      fullVariableSelector.addAll(variableKeyList);
      variablePool.add(fullVariableSelector, inputValue);
    }
  }


}

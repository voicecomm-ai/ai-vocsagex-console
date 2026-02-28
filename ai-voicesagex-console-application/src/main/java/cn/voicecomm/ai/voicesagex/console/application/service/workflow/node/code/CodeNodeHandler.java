package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.CodeNodeResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayFileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code.CodeNode.CodeVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code.CodeNode.Outputs;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code.CodeNode.OutputsType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 开始节点
 *
 * @author wangf
 * @date 2025/8/1 下午 4:27
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class CodeNodeHandler extends BaseNodeHandler {


  @Value("${algoUrlPrefix}${chat.codeExecute}")
  private String codeExecuteUrl;


  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    CodeNode nodeData = getNode(nodeCanvas, CodeNode.class);
    log.info("开始执行Code节点处理，节点数据: {}", JSONUtil.toJsonStr(nodeData));
    // 获取代码语言
    String codeLanguage = nodeData.getCode_language();

    // 获取要执行的代码
    String code = nodeData.getCode();

    // 初始化变量映射，用于存储传递给代码执行器的变量
    Map<String, Object> variables = new HashMap<>();

    if (CollUtil.isNotEmpty(nodeData.getVariables())) {
      // 遍历节点数据中的所有变量选择器
      for (CodeVariable variableSelector : nodeData.getVariables()) {
        // 获取变量名称
        String variableName = variableSelector.getVariable();

        // 从变量池中根据选择器获取变量值
        Segment variable = variablePool.get(variableSelector.getValue_selector());

        if (variable == null) {
          log.error("变量{}不存在", variableName);
          continue;
        }
        // 根据变量类型进行不同的处理
        if (variable instanceof ArrayFileSegment) {
          // 如果是文件数组段，将每个文件转换为字典形式
          ArrayFileSegment arrayFileSegment = (ArrayFileSegment) variable;
          if (arrayFileSegment.getValue() != null) {
            List<Map<String, Object>> fileDictList = new ArrayList<>();
            for (File file : (List<File>) arrayFileSegment.getValue()) {
              fileDictList.add(BeanUtil.beanToMap(file));
            }
            variables.put(variableName, fileDictList);
          } else {
            variables.put(variableName, null);
          }
        } else {
          // 对于其他类型的变量，转换为对象形式
          variables.put(variableName, variable.toObject());
        }
      }
    }

    JSONObject outputSchema = getOutputSchema(nodeData);

    // 执行代码
    try {
      // 调用代码执行器执行工作流代码模板
      JSONObject reqJson = JSONUtil.createObj()
          .putOnce("language", codeLanguage)
          .putOnce("code", code)
          .putOnce("input_variables", variables)
          .putOnce("output_schema", outputSchema);
      log.info("code节点请求 url：{}，参数：{}", codeExecuteUrl,
          JSONUtil.toJsonStr(reqJson));
      String post = HttpUtil.post(codeExecuteUrl, JSONUtil.toJsonStr(reqJson));
      log.info("code节点执行结果：{}", JSONUtil.toJsonStr(post));
      CodeNodeResponse codeNodeResponse = JSONUtil.toBean(post, CodeNodeResponse.class);
      if (codeNodeResponse.getCode() != 1000) {
        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.FAILED)
            .inputs(variables)
            .error(StrUtil.subAfter(codeNodeResponse.getMsg(), ":", false))
            .error_type("CodeNodeError")
            .retry_index(0)
            .build();
      }

      // 转换执行结果以匹配输出模式
      Map<String, Object> result = new HashMap<>();
      for (Map.Entry<String, Outputs> entry : nodeData.getOutputs().entrySet()) {
        result.put(entry.getKey(), codeNodeResponse.getData().get(entry.getKey()));
      }

      // 返回成功的结果，包含输入变量和输出结果
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.SUCCEEDED)
          .inputs(variables)
          .outputs(result)
          .build();

    } catch (Exception e) {
      // 捕获代码执行错误或代码节点错误
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(variables)
          .error(e.getMessage())
          .error_type("CodeNodeError")
          .retry_index(0)
          .build();
    }
  }

  /**
   * 根据节点输出数据生成JSON Schema格式的输出结构描述
   *
   * @param nodeData 节点数据对象，包含输出参数信息
   * @return 返回符合JSON Schema规范的输出结构描述对象，如果节点没有输出则返回null
   */
  private static JSONObject getOutputSchema(CodeNode nodeData) {
    if (CollUtil.isEmpty(nodeData.getOutputs())) {
      return null;
    }
    // 输出参数构建为jsonSchema
    JSONObject outputSchema = JSONUtil.createObj();
    outputSchema.putOnce("type", "object");
    JSONObject properties = JSONUtil.createObj();
    JSONArray required = JSONUtil.createArray();
    // 遍历所有输出参数，构建properties和required字段
    nodeData.getOutputs().forEach((key, value) -> {
      JSONObject property = JSONUtil.createObj();
      // 处理数组类型的输出参数
      String valueType = value.getType().toLowerCase();
      if (valueType.startsWith("array")) {
        property.putOnce("type", "array");
        if (OutputsType.fromValue(valueType) == OutputsType.ARRAY_OBJECT) {
          property.putOnce("items", JSONUtil.createObj().putOnce("type", "object"));
        } else if (OutputsType.fromValue(valueType) == OutputsType.ARRAY_STRING) {
          property.putOnce("items", JSONUtil.createObj().putOnce("type", "string"));
        } else {
          property.putOnce("items", JSONUtil.createObj().putOnce("type", "number"));
        }
      } else {
        // 处理非数组类型的输出参数
        property.putOnce("type", valueType);
      }
      properties.putOnce(key, property);
      required.add(key);
    });
    outputSchema.putOnce("properties", properties);
    outputSchema.putOnce("required", required);
    return outputSchema;
  }


  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    CodeNode codeNode = getNode(node, CodeNode.class);
    // 转换变量选择器为映射关系
    Map<String, List<String>> variableMappings = new HashMap<>();
    if (CollUtil.isEmpty(codeNode.getVariables())) {
      return variableMappings;
    }
    for (CodeVariable codeVariable : codeNode.getVariables()) {
      String key = JSONUtil.getByPath(node, "id") + "." + codeVariable.getVariable();
      variableMappings.put(key, codeVariable.getValue_selector());
    }
    return variableMappings;
  }


}

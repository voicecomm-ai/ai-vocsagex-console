package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.NodeCanvas;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.agent.AgentNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner.VariableAssignerNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code.CodeNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.documentextractor.DocumentExtractorNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end.EndNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http.HttpRequestNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration.IterationNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration.IterationStartNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.llm.LLMNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopStartNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.parameterextractor.ParameterExtractorNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.questionclassifier.QuestionClassifierNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.variableaggregator.VariableAggregatorNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.workflow.WorkflowNode;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class NodeTypeUtil {

  // 常量定义
  public static final String LATEST_VERSION = "latest";

  /**
   * 节点类型到版本化节点类的映射 对应Python中的NODE_TYPE_CLASSES_MAPPING
   */
  public static final Map<String, Map<String, Class<? extends BaseNode>>> NODE_TYPE_CLASSES_MAPPING = new HashMap<>();

  static {
    // START节点映射
    Map<String, Class<? extends BaseNode>> startMapping = new HashMap<>();
    startMapping.put(LATEST_VERSION, StartNode.class);
    startMapping.put("1", StartNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.START.getValue(), startMapping);

    // END节点映射
    Map<String, Class<? extends BaseNode>> endMapping = new HashMap<>();
    endMapping.put(LATEST_VERSION, EndNode.class);
    endMapping.put("1", EndNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.END.getValue(), endMapping);

//    // ANSWER节点映射
//    Map<String, Class<? extends BaseNode>> answerMapping = new HashMap<>();
//    answerMapping.put(LATEST_VERSION, AnswerNode.class);
//    answerMapping.put("1", AnswerNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.ANSWER, answerMapping);

    // LLM节点映射
    Map<String, Class<? extends BaseNode>> llmMapping = new HashMap<>();
    llmMapping.put(LATEST_VERSION, LLMNode.class);
    llmMapping.put("1", LLMNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.LLM.getValue(), llmMapping);

    // KNOWLEDGE_RETRIEVAL节点映射
    Map<String, Class<? extends BaseNode>> knowledgeRetrievalMapping = new HashMap<>();
    knowledgeRetrievalMapping.put(LATEST_VERSION, KnowledgeRetrievalNode.class);
    knowledgeRetrievalMapping.put("1", KnowledgeRetrievalNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.KNOWLEDGE_RETRIEVAL.getValue(),
        knowledgeRetrievalMapping);

    // IF_ELSE节点映射
    Map<String, Class<? extends BaseNode>> ifElseMapping = new HashMap<>();
    ifElseMapping.put(LATEST_VERSION, IfElseNode.class);
    ifElseMapping.put("1", IfElseNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.IF_ELSE.getValue(), ifElseMapping);
//
    // CODE节点映射
    Map<String, Class<? extends BaseNode>> codeMapping = new HashMap<>();
    codeMapping.put(LATEST_VERSION, CodeNode.class);
    codeMapping.put("1", CodeNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.CODE.getValue(), codeMapping);
//
//    // TEMPLATE_TRANSFORM节点映射
//    Map<String, Class<? extends BaseNode>> templateTransformMapping = new HashMap<>();
//    templateTransformMapping.put(LATEST_VERSION, TemplateTransformNode.class);
//    templateTransformMapping.put("1", TemplateTransformNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.TEMPLATE_TRANSFORM, templateTransformMapping);

    // QUESTION_CLASSIFIER节点映射
    Map<String, Class<? extends BaseNode>> questionClassifierMapping = new HashMap<>();
    questionClassifierMapping.put(LATEST_VERSION, QuestionClassifierNode.class);
    questionClassifierMapping.put("1", QuestionClassifierNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.QUESTION_CLASSIFIER.getValue(),
        questionClassifierMapping);
//
    // HTTP_REQUEST节点映射
    Map<String, Class<? extends BaseNode>> httpRequestMapping = new HashMap<>();
    httpRequestMapping.put(LATEST_VERSION, HttpRequestNode.class);
    httpRequestMapping.put("1", HttpRequestNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.HTTP_REQUEST.getValue(), httpRequestMapping);

    // MCP节点映射
    Map<String, Class<? extends BaseNode>> mcpMapping = new HashMap<>();
    mcpMapping.put(LATEST_VERSION, McpNode.class);
    mcpMapping.put("1", McpNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.MCP.getValue(), mcpMapping);
//
//    // TOOL节点映射
//    Map<String, Class<? extends BaseNode>> toolMapping = new HashMap<>();
//    toolMapping.put(LATEST_VERSION, ToolNode.class);
//    toolMapping.put("1", ToolNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.TOOL, toolMapping);
//
    // VARIABLE_AGGREGATOR节点映射
    Map<String, Class<? extends BaseNode>> variableAggregatorMapping = new HashMap<>();
    variableAggregatorMapping.put(LATEST_VERSION, VariableAggregatorNode.class);
    variableAggregatorMapping.put("1", VariableAggregatorNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.VARIABLE_AGGREGATOR.getValue(),
        variableAggregatorMapping);
//
//    // LEGACY_VARIABLE_AGGREGATOR节点映射
//    Map<String, Class<? extends BaseNode>> legacyVariableAggregatorMapping = new HashMap<>();
//    legacyVariableAggregatorMapping.put(LATEST_VERSION, VariableAggregatorNode.class);
//    legacyVariableAggregatorMapping.put("1", VariableAggregatorNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.LEGACY_VARIABLE_AGGREGATOR, legacyVariableAggregatorMapping);
//
    // ITERATION节点映射
    Map<String, Class<? extends BaseNode>> iterationMapping = new HashMap<>();
    iterationMapping.put(LATEST_VERSION, IterationNode.class);
    iterationMapping.put("1", IterationNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.ITERATION.getValue(), iterationMapping);

    // ITERATION_START节点映射
    Map<String, Class<? extends BaseNode>> iterationStartMapping = new HashMap<>();
    iterationStartMapping.put(LATEST_VERSION, IterationStartNode.class);
    iterationStartMapping.put("1", IterationStartNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.ITERATION_START.getValue(), iterationStartMapping);
//
//    // WORKFLOW节点映射
    Map<String, Class<? extends BaseNode>> workflowMapping = new HashMap<>();
    workflowMapping.put(LATEST_VERSION, WorkflowNode.class);
    workflowMapping.put("1", WorkflowNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.WORKFLOW.getValue(), workflowMapping);

    // agent节点映射
    Map<String, Class<? extends BaseNode>> agentMapping = new HashMap<>();
    agentMapping.put(LATEST_VERSION, AgentNode.class);
    agentMapping.put("1", AgentNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.AGENT.getValue(), agentMapping);

//    // LOOP节点映射
    Map<String, Class<? extends BaseNode>> loopMapping = new HashMap<>();
    loopMapping.put(LATEST_VERSION, LoopNode.class);
    loopMapping.put("1", LoopNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.LOOP.getValue(), loopMapping);
//
//    // LOOP_START节点映射
    Map<String, Class<? extends BaseNode>> loopStartMapping = new HashMap<>();
    loopStartMapping.put(LATEST_VERSION, LoopStartNode.class);
    loopStartMapping.put("1", LoopStartNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.LOOP_START.getValue(), loopStartMapping);
//
//    // LOOP_END节点映射
//    Map<String, Class<? extends BaseNode>> loopEndMapping = new HashMap<>();
//    loopEndMapping.put(LATEST_VERSION, LoopEndNode.class);
//    loopEndMapping.put("1", LoopEndNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.LOOP_END, loopEndMapping);
//
    // PARAMETER_EXTRACTOR节点映射
    Map<String, Class<? extends BaseNode>> parameterExtractorMapping = new HashMap<>();
    parameterExtractorMapping.put(LATEST_VERSION, ParameterExtractorNode.class);
    parameterExtractorMapping.put("1", ParameterExtractorNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.PARAMETER_EXTRACTOR.getValue(),
        parameterExtractorMapping);
//
//    // VARIABLE_ASSIGNER节点映射（支持版本）
    Map<String, Class<? extends BaseNode>> variableAssignerMapping = new HashMap<>();
    variableAssignerMapping.put(LATEST_VERSION, VariableAssignerNode.class);
    variableAssignerMapping.put("1", VariableAssignerNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.VARIABLE_ASSIGNER.getValue(), variableAssignerMapping);
//
    // DOCUMENT_EXTRACTOR节点映射
    Map<String, Class<? extends BaseNode>> documentExtractorMapping = new HashMap<>();
    documentExtractorMapping.put(LATEST_VERSION, DocumentExtractorNode.class);
    documentExtractorMapping.put("1", DocumentExtractorNode.class);
    NODE_TYPE_CLASSES_MAPPING.put(NodeType.DOCUMENT_EXTRACTOR.getValue(), documentExtractorMapping);
//
//    // LIST_OPERATOR节点映射
//    Map<String, Class<? extends BaseNode>> listOperatorMapping = new HashMap<>();
//    listOperatorMapping.put(LATEST_VERSION, ListOperatorNode.class);
//    listOperatorMapping.put("1", ListOperatorNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.LIST_OPERATOR, listOperatorMapping);
//
//    // AGENT节点映射
//    Map<String, Class<? extends BaseNode>> agentMapping = new HashMap<>();
//    agentMapping.put(LATEST_VERSION, AgentNode.class);
//    agentMapping.put("1", AgentNode.class);
//    NODE_TYPE_CLASSES_MAPPING.put(NodeType.AGENT, agentMapping);
  }

  /**
   * 根据节点类型和版本获取节点类
   *
   * @param nodeType 节点类型
   * @param version  版本号
   * @return 节点类
   */
  public static Class<? extends BaseNode> getNodeClass(String nodeType, String version) {
    Map<String, Class<? extends BaseNode>> versionMapping = NODE_TYPE_CLASSES_MAPPING.get(nodeType);
    if (versionMapping == null) {
      throw new IllegalArgumentException("Unknown node type: " + nodeType);
    }

    Class<? extends BaseNode> nodeClass = versionMapping.get(StrUtil.blankToDefault(version, "1"));
    if (nodeClass == null) {
      throw new IllegalArgumentException(
          "Unknown version: " + version + " for node type: " + nodeType);
    }
    return nodeClass;
  }

  public static <T> T getNode(JSONObject node, Class<T> clazz) {
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    return JSONUtil.toBean(jsonObject, clazz);
  }

  public static NodeCanvas getNodeCanvas(JSONObject node) {
    // 获取节点类型
    String nodeType = node.getByPath("data.type", String.class);
    if (nodeType == null) {
      return null;
    }
    // 根据节点类型获取节点类
    JSONObject nodeData = node.getJSONObject("data");
    NodeCanvas nodeCanvas = JSONUtil.toBean(node, NodeCanvas.class);
    Class<? extends BaseNode> aClass = NODE_TYPE_CLASSES_MAPPING.get(nodeType).get("1");
    BaseNode bean = JSONUtil.toBean(nodeData, aClass);
    nodeCanvas.setData(bean);
    return nodeCanvas;
  }

  public static JSONObject getNodeCanvasJson(JSONObject node) {
    return JSONUtil.parseObj(getNodeCanvas(node));
  }


}

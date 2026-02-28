package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.agent.AgentNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner.VariableAssignerNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code.CodeNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.documentextractor.DocumentExtractorNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end.EndNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http.HttpRequestNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration.IterationNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration.IterationStartNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.llm.LLMNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopStartNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.parameterextractor.ParameterExtractorNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.questionclassifier.QuestionClassifierNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.variableaggregator.VariableAggregatorNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.workflow.WorkflowNodeHandler;
import java.util.HashMap;
import java.util.Map;

public class NodeHandlerMapping {

  public static Map<String, Class<? extends BaseNodeHandler>> nodeHandlerMapping = new HashMap<>();

  /*
  START("start", "开始节点"),
  END("end", "结束"),
  ANSWER("answer", "答案"),
  LLM("llm", "LLM"),
  KNOWLEDGE_RETRIEVAL("knowledge-retrieval", "知识检索"),
  IF_ELSE("if-else", "条件分支"),
  CODE("code", "代码执行"),
  TEMPLATE_TRANSFORM("template-transform", "模板转换"),
  QUESTION_CLASSIFIER("question-classifier", "意图分类"),
  HTTP_REQUEST("http-request", "HTTP请求"),
  TOOL("tool", "工具"),
  VARIABLE_AGGREGATOR("variable-aggregator", "变量聚合器"),
  LEGACY_VARIABLE_AGGREGATOR("variable-assigner", "旧版变量聚合"),
  LOOP("loop", "循环"),
  LOOP_START("loop-start", "循环开始"),
  LOOP_END("loop-end", "循环结束"),
  ITERATION("iteration", "迭代"),
  ITERATION_START("iteration-start", "迭代开始"),
  PARAMETER_EXTRACTOR("parameter-extractor", "参数提取"),
  VARIABLE_ASSIGNER("assigner", "变量赋值"),
  DOCUMENT_EXTRACTOR("document-extractor", "文档抽取"),
  LIST_OPERATOR("list-operator", "列表操作"),
  AGENT("agent", "智能体");
   */
  static {
    nodeHandlerMapping.put("start", StartNodeHandler.class);
    nodeHandlerMapping.put("llm", LLMNodeHandler.class);
    nodeHandlerMapping.put("question-classifier", QuestionClassifierNodeHandler.class);
    nodeHandlerMapping.put("knowledge-retrieval", KnowledgeRetrievalNodeHandler.class);
    nodeHandlerMapping.put("document-extractor", DocumentExtractorNodeHandler.class);
    nodeHandlerMapping.put("if-else", IfElseNodeHandler.class);
    nodeHandlerMapping.put("code", CodeNodeHandler.class);
    nodeHandlerMapping.put("end", EndNodeHandler.class);
    nodeHandlerMapping.put("http-request", HttpRequestNodeHandler.class);
    nodeHandlerMapping.put("parameter-extractor", ParameterExtractorNodeHandler.class);
    nodeHandlerMapping.put("mcp", McpNodeHandler.class);
    nodeHandlerMapping.put("loop", LoopNodeHandler.class);
    nodeHandlerMapping.put("loop-start", LoopStartNodeHandler.class);
    nodeHandlerMapping.put("assigner", VariableAssignerNodeHandler.class);
    nodeHandlerMapping.put("variable-aggregator", VariableAggregatorNodeHandler.class);
    nodeHandlerMapping.put("iteration", IterationNodeHandler.class);
    nodeHandlerMapping.put("iteration-start", IterationStartNodeHandler.class);
    nodeHandlerMapping.put("workflow", WorkflowNodeHandler.class);
    nodeHandlerMapping.put("agent", AgentNodeHandler.class);
  }

  public static BaseNodeHandler getNodeHandler(String nodeType) {
    Class<? extends BaseNodeHandler> aClass = nodeHandlerMapping.get(nodeType);
    return SpringUtil.getBean(aClass);
  }


  public static BaseNodeHandler getNodeHandler(JSONObject nodeCanvas) {
    String nodeType = JSONUtil.getByPath(nodeCanvas, "data.type", "");
    return getNodeHandler(nodeType);
  }


}

package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;


import lombok.Getter;

@Getter
public enum NodeType {

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
  AGENT("agent", "智能体"),
  WORKFLOW("workflow", "工作流"),
  MCP("mcp", "MCP");

  private final String value;
  private final String desc;

  NodeType(String value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  // 根据value值获取type
  public static NodeType getByValue(String value) {
    for (NodeType type : NodeType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    return null;
  }

}

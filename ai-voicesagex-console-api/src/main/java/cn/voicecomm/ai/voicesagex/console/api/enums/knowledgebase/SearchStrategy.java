package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase;

public enum SearchStrategy {
  /**
   * 混合检索
   */
  HYBRID,

  /**
   * 全文检索
   */
  FULL_TEXT,

  /**
   * 向量检索
   */
  VECTOR,

  /**
   *
   * 知识图谱检索
   */
  GRAPH,
}

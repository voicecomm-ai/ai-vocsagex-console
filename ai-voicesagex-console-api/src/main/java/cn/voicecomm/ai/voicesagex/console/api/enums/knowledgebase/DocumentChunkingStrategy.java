package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase;

/**
 * 文档分段
 */
public enum DocumentChunkingStrategy {
  /**
   * 普通分段
   */
  NORMAL,
  /**
   * 普通分段，Q&A
   */
  NORMAL_QA,
  /**
   * 高级分段，父段全文
   */
  ADVANCED_FULL_DOC,
  /**
   * 高级分段，父段段落
   */
  ADVANCED_PARAGRAPH
}

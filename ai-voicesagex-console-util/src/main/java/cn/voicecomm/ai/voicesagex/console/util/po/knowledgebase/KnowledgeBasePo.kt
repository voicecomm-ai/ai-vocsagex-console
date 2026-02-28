package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo
import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.FieldStrategy
import com.baomidou.mybatisplus.annotation.IEnum
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@TableName("knowledge_base", autoResultMap = true)
class KnowledgeBasePo : BaseAuditPo() {
    @TableId(type = IdType.AUTO)
    var id: Int? = null // 主键

    @TableField(value = "\"name\"")
    var name: String? = null // 知识库名称

    @TableField(value = "\"type\"", typeHandler = JacksonTypeHandler::class)
    var type: KnowledgeBaseType? = null // 类型（TRAD-传统，GRAPH-图谱）

    @TableField(value = "\"description\"")
    var description: String? = null // 知识库描述

    /**
     * 创建人id
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    var createBy: Int? = null

    /**
     * 更新人id
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    var updateBy: Int? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: LocalDateTime? = null

    @TableField(value = "\"embedding_model_id\"")
    var embeddingModelId: Int? = null // Embedding模型

    @TableField(value = "\"enable_multimodal\"")
    var enableMultimodal: Boolean? = null // 多模态

    @TableField(value = "enable_vector_search_rerank")
    var enableVectorSearchRerank: Boolean? = null // 向量检索是否启用Rerank模型

    @TableField(value = "vector_search_rerank_model_id")
    var vectorSearchRerankModelId: Int? = null; // 向量检索Rerank模型ID

    @TableField(value = "vector_search_top_k")
    var vectorSearchTopK: Int? = null // 向量检索TOP K

    @TableField(value = "vector_search_score")
    var vectorSearchScore: Float? = null // 向量检索Score阈值

    @TableField(value = "enable_vector_search_score")
    var enableVectorSearchScore: Boolean? = null // 向量检索是否启用Score阈值

    @TableField(value = "enable_full_text_search_rerank")
    var enableFullTextSearchRerank: Boolean? = null // 全文检索是否启用Rerank模型

    @TableField(value = "full_text_search_rerank_model_id")
    var fullTextSearchRerankModelId: Int? = null // 全文检索Rerank模型ID

    @TableField(value = "full_text_search_top_k")
    var fullTextSearchTopK: Int? = null // 全文检索TOP K

    @TableField(value = "full_text_search_score")
    var fullTextSearchScore: Float? = null // 全文检索Score阈值

    @TableField(value = "enable_full_text_search_score")
    var enableFullTextSearchScore: Boolean? = null // 全文检索是否启用Score阈值

    @TableField(value = "hybrid_search_top_k")
    var hybridSearchTopK: Int? = null // 混合检索TOP K

    @TableField(value = "enable_hybrid_search_score")
    var enableHybridSearchScore: Boolean? = null // 混合检索是否启用Score阈值

    @TableField(value = "hybrid_search_score")
    var hybridSearchScore: Float? = null // 混合检索Score阈值

    @TableField(value = "hybrid_search_rerank_model_id", updateStrategy = FieldStrategy.ALWAYS)
    var hybridSearchRerankModelId: Int? = null // 混合检索Rerank模型ID

    @TableField(value = "enable_hybrid_search_rerank")
    var enableHybridSearchRerank: Boolean? = null // 混合检索是否启用Rerank模型

    @TableField(value = "hybrid_search_semantic_matching_weight")
    var hybridSearchSemanticMatchingWeight: Float? = null // 混合检索语义匹配权重

    @TableField(value = "hybrid_search_keyword_matching_weight")
    var hybridSearchKeywordMatchingWeight: Float? = null // 混合检索关键词匹配权重

    @TableField(value = "enable_graph_search_rerank")
    var enableGraphSearchRerank: Boolean? = null // 知识图谱检索是否启用Rerank模型

    @TableField(value = "graph_search_rerank_model_id")
    var graphSearchRerankModelId: Int? = null // 知识图谱检索Rerank模型ID

    @TableField(value = "graph_search_top_k")
    var graphSearchTopK: Int? = null // 知识图谱检索TOP K

    @TableField(value = "search_strategy", typeHandler = JacksonTypeHandler::class)
    var searchStrategy: KnowledgeBaseSearchStrategy? =
        null; // 检索策略（HYBRID-混合，FULL_TEXT-全文，VECTOR-向量）

    @TableField(value = "chunking_strategy", typeHandler = JacksonTypeHandler::class)
    var chunkingStrategy: KnowledgeBaseChunkingStrategy? = null // 分段策略（COMMON-通用，PARENT_CHILD-父子）

    @TableField(value = "enable_qa_chunk")
    var enableQaChunk: Boolean? = null // 是否启用QA分段

    @TableField(value = "qa_model_id")
    var qaModelId: Int? = null

    @TableField(value = "chunk_identifier")
    var chunkIdentifier: String? = null

    @TableField(value = "chunk_max_length")
    var chunkMaxLength: Int? = null

    @TableField(value = "chunk_overlap_length")
    var chunkOverlapLength: Int? = null

    @TableField(value = "parent_chunk_context")
    var parentChunkContext: String? = null // PARAGRAPH;FULLTEXT

    @TableField(value = "paragraph_chunk_identifier")
    var paragraphChunkIdentifier: String? = null

    @TableField(value = "paragraph_chunk_max_length")
    var paragraphChunkMaxLength: Int? = null

    @TableField(value = "enable_text_process_first_rule")
    var enableTextProcessFirstRule: Boolean? = null

    @TableField(value = "enable_text_process_second_rule")
    var enableTextProcessSecondRule: Boolean? = null

    enum class KnowledgeBaseType : IEnum<String> {
        /**
         * 传统RAG
         */
        TRAD,

        /**
         * 图RAG
         */
        GRAPH,

        ;

        @JsonValue
        override fun getValue(): String = name
    }

    enum class KnowledgeBaseSearchStrategy : IEnum<String> {
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
        ;

        @JsonValue
        override fun getValue(): String = name
    }

    enum class KnowledgeBaseChunkingStrategy : IEnum<String> {
        COMMON,
        PARENT_CHILD,
        ;

        @JsonValue
        override fun getValue(): String = name
    }
}

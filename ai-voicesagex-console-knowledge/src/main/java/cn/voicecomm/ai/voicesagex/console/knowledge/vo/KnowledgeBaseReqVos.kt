package cn.voicecomm.ai.voicesagex.console.knowledge.vo

import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.PreviewChunksDto
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.ChunkingStrategy
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.KnowledgeBaseType
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBasePo.KnowledgeBaseSearchStrategy
import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AddApplicationKnowledgeBaseReqVo(
    val knowledgeBaseIds: List<Int>?,
    @field:NotNull(message = "智能体应用ID不能为空")
    val applicationId: Int,
)

data class CreateEmptyKnowledgeBaseVo(
    @field:NotBlank(message = "知识库名称不能为空")
    @field:Size(max = 50, message = "知识库名称不能超过50个字符")
    val name: String,
    @field:Size(max = 400, message = "知识库描述不能超过400个字符")
    @field:Nullable
    val description: String?,
    /**
     * 知识库类型（TRAD-传统，GRAPH-图谱），默认为TRAD
     */
    @field:Nullable
    val knowledgeBaseType: KnowledgeBaseType?,
)

data class EditKnowledgeBaseNameAndDescVo(
    @field:NotNull(message = "知识库ID不能为空")
    val id: Int,
    @field:NotBlank(message = "知识库名称不能为空")
    @field:Size(max = 50, message = "知识库名称不能超过50个字符")
    val name: String,
    @field:Size(max = 400, message = "知识库描述不能超过400个字符")
    val description: String?,
)

data class ListKnowledgeBaseVo(
    val tagIds: List<Int>?,
    val name: String?,
    val type: String?,
)

data class RemoveApplicationKnowledgeBaseReqVo(
    @field:NotNull(message = "知识库ID不能为空")
    val knowledgeBaseId: Int,
    @field:NotNull(message = "智能体应用ID不能为空")
    val applicationId: Int,
)

data class SaveAndProcessBaseVo(
    /**
     * 知识库ID
     */
    val knowledgeBaseId: Int?,
    /**
     * 分段策略
     */
    val chunkingStrategy: ChunkingStrategy,
    /**
     * 是否启用多模态
     */
    val enableMultimodal: Boolean,
    /**
     * 多模态模型ID
     */
    val embeddingModelId: Int?,
    /**
     * 检索策略
     */
    val searchStrategy: KnowledgeBaseSearchStrategy,
    /**
     * 是否启用Rerank模型
     */
    val enableRerank: Boolean,
    /**
     * Rerank模型ID
     */
    val rerankModelId: Int?,
    /**
     * Top K
     */
    val topK: Int,
    /**
     * 是否启用Score
     */
    val enableScore: Boolean,
    /**
     * Score阈值
     */
    val score: Float?,
    /**
     * 混合检索权重设置——语义
     */
    val hybridSearchSemanticMatchingWeight: Float?,
    /**
     * 混合检索权重设置——关键词
     */
    val hybridSearchKeywordMatchingWeight: Float?,
    /**
     * 知识库名称
     */
    @field:NotBlank(message = "知识库名称不能为空")
    val name: String,
    /**
     * 知识库类型
     */
    @field:NotNull(message = "知识库类型不能为空")
    val type: KnowledgeBaseType,
    /**
     * 文档ID列表
     */
    @field:NotEmpty(message = "文档不能为空")
    val documentIds: List<Int>,
    /**
     * 文档预览参数
     */
    val previewParams: PreviewChunksDto,
)

data class SaveAndProcessExistBaseVo(
    /**
     * 知识库ID
     */
    val knowledgeBaseId: Int,
    /**
     * 分段策略
     */
    val chunkingStrategy: ChunkingStrategy,
    /**
     * 文档ID列表
     */
    @field:NotEmpty(message = "文档不能为空")
    val documentIds: List<Int>,
    /**
     * 文档预览参数
     */
    val previewParams: PreviewChunksDto,
)

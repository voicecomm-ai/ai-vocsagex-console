package cn.voicecomm.ai.voicesagex.console.knowledge.vo

import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.ChildChunkSetting
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.ChunkSetting
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.CleanerSetting
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.ParentChunkSetting
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.PreviewChunksDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.QaSetting
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.DocumentStatus
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class DeleteDocumentsReqVo(
    val ids: List<Int>?,
)

data class GetBaseDocumentsReqVo(
    /**
     * 文档名称
     */
    val name: String?,
    /**
     * 文档状态：ENABLE;DISABLE;ARCHIVE
     */
    val status: String?,
)

data class UpdateDocumentStatusVo(
    /**
     * 文档ID列表
     */
    @field:NotEmpty(message = "文档ID列表不能为空")
    val documentIds: List<Int>,
    /**
     * 目标状态（为 null 不更新）
     */
    val status: DocumentStatus?,
    /**
     * 是否归档（为 null 则不更新）
     */
    val isArchived: Boolean?,
)

data class UploadDocsReqVo(
    val files: Array<MultipartFile>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadDocsReqVo

        return files.contentEquals(other.files)
    }

    override fun hashCode(): Int = files.contentHashCode()
}

data class PreviewCommonChunksReqVo(
    /**
     * 文档ID
     */
    @field:NotNull(message = "文档ID为空")
    val documentId: Int,
    /**
     * 分段设置
     */
    val chunkSetting: ChunkSetting,
    /**
     * 文本预处理规则
     */
    @field:NotNull(message = "文本预处理规则为空")
    val cleanerSetting: CleanerSetting,
    /**
     * Q&A分段设置
     */
    val qaSetting: QaSetting?,
) {
    fun toDto(): PreviewChunksDto =
        PreviewChunksDto().apply {
            documentId = this@PreviewCommonChunksReqVo.documentId
            chunkSetting = this@PreviewCommonChunksReqVo.chunkSetting
            cleanerSetting = this@PreviewCommonChunksReqVo.cleanerSetting
            qaSetting = this@PreviewCommonChunksReqVo.qaSetting
        }
}

data class PreviewParentChildChunksReqVo(
    /**
     * 文档ID
     */
    @field:NotNull(message = "文档ID为空")
    val documentId: Int,
    /**
     * 文本预处理规则
     */
    @field:NotNull(message = "文本预处理规则为空")
    val cleanerSetting: CleanerSetting,
    /**
     * 父子分段父段设置
     */
    val parentChunkSetting: ParentChunkSetting?,
    /**
     * 父子分段子段设置
     */
    val childChunkSetting: ChildChunkSetting?,
) {
    fun toDto(): PreviewChunksDto =
        PreviewChunksDto().apply {
            documentId = this@PreviewParentChildChunksReqVo.documentId
            cleanerSetting = this@PreviewParentChildChunksReqVo.cleanerSetting
            parentChunkSetting = this@PreviewParentChildChunksReqVo.parentChunkSetting
            childChunkSetting = this@PreviewParentChildChunksReqVo.childChunkSetting
        }
}

data class UploadDocumentsRespVo(
    val name: String?,
    val id: Int,
)

data class RetrievalTestReqVo(
    /**
     * 知识库ID
     */
    @field:NotNull(message = "知识库ID不能为空")
    val knowledgeBaseId: Int,
    /**
     * 问题
     */
    @field:NotBlank(message = "查询不能为空")
    val query: String,
    /**
     * 检索策略
     */
    @field:NotNull(message = "检索策略不能为空")
    val searchStrategy: SearchStrategy,
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
)

data class AddNormalChunkReqVo(
    val documentId: Int,
    val chunkContent: String,
)

data class AddNormalQaChunkReqVo(
    val documentId: Int,
    val chunkQuestion: String,
    val chunkAnswer: String,
)

data class AddAdvancedParentChunkReqVo(
    val documentId: Int,
    val chunkContent: String,
)

data class AddAdvancedChildChunkReqVo(
    val documentId: Int,
    val parentIdx: Int,
    val chunkContent: String,
)

data class DeleteNormalChunkReqVo(
    val documentId: Int,
    val chunkId: Int,
)

data class DeleteAdvancedParentChunkReqVo(
    val documentId: Int,
    val parentIdx: Int,
)

data class DeleteAdvancedChildChunkReqVo(
    val documentId: Int,
    val parentIdx: Int,
    val childChunkIdx: Int,
)

data class EditNormalChunkReqVo(
    val documentId: Int,
    val chunkId: Int,
    val chunkContent: String,
)

data class EditNormalQaChunkReqVo(
    val documentId: Int,
    val chunkId: Int,
    val chunkQuestion: String,
    val chunkAnswer: String,
)

data class EditAdvancedParentChunkReqVo(
    val documentId: Int,
    val parentIdx: Int,
    val chunkContent: String,
)

data class EditAdvancedChildChunkReqVo(
    val documentId: Int,
    val parentIdx: Int,
    val childChunkIdx: Int,
    val chunkContent: String,
)

package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseDocumentService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseMetadataService
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.*
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.DocumentChunkingStrategy
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.*
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.*
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.io.FileUtils
import org.apache.dubbo.config.annotation.DubboReference
import org.apache.dubbo.config.annotation.DubboService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.concurrent.Executors

@Service
@DubboService
class KnowledgeBaseDocumentServiceImpl(
    private val knowledgeBaseDocumentMapper: KnowledgeBaseDocumentMapper,
    private val knowledgeBaseMapper: KnowledgeBaseMapper,
    private val knowledgeBaseDocVectorMapper: KnowledgeBaseDocVectorMapper,
    private val knowledgeBaseApplicationRelationMapper: KnowledgeBaseApplicationRelationMapper,
    private val knowledgeBaseDocumentMetadataMapper: KnowledgeBaseDocumentMetadataMapper,
    private val knowledgeBaseMetadataService: KnowledgeBaseMetadataService,
    @Value("\${file.upload}") val uploadDir: String,
    @Value("\${pyServer}") val pyServer: String,
) : KnowledgeBaseDocumentService {
    private val log: Logger = LoggerFactory.getLogger(KnowledgeBaseDocumentServiceImpl::class.java)
    private val executor = Executors.newVirtualThreadPerTaskExecutor()
    private val objectMapper = jacksonObjectMapper()

    @DubboReference
    lateinit var modelService: ModelService

    /**
     * 更新文档的preview_chunks字段中指定chunk的状态
     */
    private fun updatePreviewChunksStatus(
        document: KnowledgeBaseDocumentPo,
        chunkIds: List<Int>?,
        newStatus: String,
    ): String? {
        try {
            val previewChunksJson = document.previewChunks ?: return null
            when (val chunkingStrategy = document.chunkingStrategy) {
                DocumentChunkingStrategy.ADVANCED_FULL_DOC.name,
                DocumentChunkingStrategy.ADVANCED_PARAGRAPH.name,
                -> {
                    // 处理父子分段结构
                    val previewData: MutableList<ParentChildChunkDto> = objectMapper.readValue(previewChunksJson)
                    previewData.forEach { parentChunk ->
                        parentChunk.content?.forEach { childChunk ->
                            if (chunkIds == null) {
                                childChunk.status = newStatus
                            } else {
                                if (chunkIds.contains(childChunk.primaryKey)) {
                                    childChunk.status = newStatus
                                }
                            }
                        }
                    }
                    return objectMapper.writeValueAsString(previewData)
                }
                DocumentChunkingStrategy.NORMAL.name -> {
                    // 处理普通分段结构
                    val previewData: MutableList<CommonChunkDto> = objectMapper.readValue(previewChunksJson)
                    previewData.forEach { chunk ->
                        if (chunkIds == null) {
                            chunk.status = newStatus
                        } else {
                            if (chunkIds.contains(chunk.primaryKey)) {
                                chunk.status = newStatus
                            }
                        }
                    }
                    return objectMapper.writeValueAsString(previewData)
                }
                DocumentChunkingStrategy.NORMAL_QA.name -> {
                    // 处理普通QA分段结构
                    val previewData: MutableList<CommonQaChunkDto> = objectMapper.readValue(previewChunksJson)
                    previewData.forEach { qaChunk ->
                        if (chunkIds == null) {
                            qaChunk.status = newStatus
                        } else {
                            if (chunkIds.contains(qaChunk.primaryKey)) {
                                qaChunk.status = newStatus
                            }
                        }
                    }
                    return objectMapper.writeValueAsString(previewData)
                }
                else -> {
                    log.warn("未知的分段策略: $chunkingStrategy")
                    return null
                }
            }
        } catch (e: Exception) {
            log.error("更新preview_chunks状态失败", e)
            return null
        }
    }

    /**
     * 从文档的preview_chunks字段中删除指定的chunk
     */
    private fun removeChunksFromPreviewChunks(
        document: KnowledgeBaseDocumentPo,
        chunkIds: List<Int>,
    ): String? {
        try {
            val previewChunksJson = document.previewChunks ?: return null
            when (val chunkingStrategy = document.chunkingStrategy) {
                DocumentChunkingStrategy.ADVANCED_FULL_DOC.name,
                DocumentChunkingStrategy.ADVANCED_PARAGRAPH.name,
                -> {
                    // 处理父子分段结构
                    val previewData: MutableList<ParentChildChunkDto> = objectMapper.readValue(previewChunksJson)
                    previewData.forEach { parentChunk ->
                        parentChunk.content?.removeAll { childChunk ->
                            chunkIds.contains(childChunk.primaryKey)
                        }
                    }
                    // 移除空的父段
                    previewData.removeAll { parentChunk ->
                        parentChunk.content.isNullOrEmpty()
                    }
                    return objectMapper.writeValueAsString(previewData)
                }
                DocumentChunkingStrategy.NORMAL.name -> {
                    // 处理普通分段结构
                    val previewData: MutableList<CommonChunkDto> = objectMapper.readValue(previewChunksJson)
                    previewData.removeAll { chunk ->
                        chunkIds.contains(chunk.primaryKey)
                    }
                    return objectMapper.writeValueAsString(previewData)
                }
                DocumentChunkingStrategy.NORMAL_QA.name -> {
                    // 处理普通QA分段结构
                    val previewData: MutableList<CommonQaChunkDto> = objectMapper.readValue(previewChunksJson)
                    previewData.removeAll { qaChunk ->
                        chunkIds.contains(qaChunk.primaryKey)
                    }
                    return objectMapper.writeValueAsString(previewData)
                }
                else -> {
                    log.warn("未知的分段策略: $chunkingStrategy")
                    return null
                }
            }
        } catch (e: Exception) {
            log.error("从preview_chunks删除chunk失败", e)
            return null
        }
    }

    override fun uploadDoc(dto: UploadDocDto): Int? =
        KnowledgeBaseDocumentPo()
            .apply {
                name = dto.name
                uniqueName = dto.uniqueName
                createTime = LocalDateTime.now()
                updateTime = LocalDateTime.now()
                createBy = UserAuthUtil.getUserId()
                updateBy = UserAuthUtil.getUserId()
            }.also { po ->
                knowledgeBaseDocumentMapper.insert(po)
            }.id

    override fun getKnowledgeBaseDocuments(
        knowledgeBaseId: Int?,
        name: String?,
        status: String?,
    ): CommonRespDto<List<KnowledgeBaseDocumentDto>> {
        // 参数验证
        if (knowledgeBaseId == null) {
            return CommonRespDto.error("知识库ID不能为空")
        }

        try {
            // 检查知识库是否存在
            val knowledgeBase =
                knowledgeBaseMapper.selectById(knowledgeBaseId)
                    ?: return CommonRespDto.error("知识库不存在")

            // 查询知识库下的所有文档，省略chunks和previewChunks字段
            val documents =
                knowledgeBaseDocumentMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocumentPo()).apply {
                        select(
                            KnowledgeBaseDocumentPo::id,
                            KnowledgeBaseDocumentPo::name,
                            KnowledgeBaseDocumentPo::knowledgeBaseId,
                            KnowledgeBaseDocumentPo::chunkingStrategy,
                            KnowledgeBaseDocumentPo::uniqueName,
                            KnowledgeBaseDocumentPo::processStatus,
                            KnowledgeBaseDocumentPo::wordCount,
                            KnowledgeBaseDocumentPo::status,
                            KnowledgeBaseDocumentPo::createTime,
                            KnowledgeBaseDocumentPo::updateTime,
                            KnowledgeBaseDocumentPo::processFailedReason,
                            KnowledgeBaseDocumentPo::isArchived,
                        )

                        name
                            ?.takeIf { n ->
                                n.isNotBlank()
                            }?.let { n ->
                                apply("name ILIKE {0}", "%${SpecialCharUtil.replaceSpecialWord(n)}%")
                            }
                        status?.takeIf { s -> s.isNotBlank() }?.let { s ->
                            if (s == "ARCHIVE") {
                                eq(KnowledgeBaseDocumentPo::isArchived, true)
                            } else {
                                eq(KnowledgeBaseDocumentPo::status, s)
                                eq(KnowledgeBaseDocumentPo::isArchived, false)
                            }
                        }

                        eq(KnowledgeBaseDocumentPo::knowledgeBaseId, knowledgeBaseId)
                        orderByDesc(KnowledgeBaseDocumentPo::createTime)
                    },
                )

            // 转换为DTO（省略chunks和previewChunks字段）
            val documentDtos =
                documents.map { document ->
                    KnowledgeBaseDocumentDto
                        .builder()
                        .id(document.id)
                        .name(document.name)
                        .knowledgeBaseId(document.knowledgeBaseId)
                        .chunkingStrategy(document.chunkingStrategy)
                        .uniqueName(document.uniqueName)
                        .processStatus(document.processStatus)
                        .wordCount(document.wordCount)
                        .status(document.status)
                        .processFailedReason(document.processFailedReason)
                        .isArchived(document.isArchived)
                        .createTime(document.createTime)
                        .updateTime(document.updateTime)
                        .build()
                }

            log.info("成功获取知识库 $knowledgeBaseId 的文档列表，共 ${documentDtos.size} 个文档")
            return CommonRespDto.success(documentDtos)
        } catch (e: Exception) {
            log.error("获取知识库文档列表失败", e)
            return CommonRespDto.error("获取知识库文档列表失败：${e.message}")
        }
    }

    override fun getKnowledgeBaseDocumentIds(knowledgeBaseId: Int?): CommonRespDto<List<Int>> {
        // 参数验证
        if (knowledgeBaseId == null) {
            return CommonRespDto.error("知识库ID不能为空")
        }

        try {
            // 检查知识库是否存在
            val knowledgeBase =
                knowledgeBaseMapper.selectById(knowledgeBaseId)
                    ?: return CommonRespDto.error("知识库不存在")

            // 查询知识库下的所有文档ID
            val documentIds =
                knowledgeBaseDocumentMapper
                    .selectList(
                        KtQueryWrapper(KnowledgeBaseDocumentPo())
                            .select(KnowledgeBaseDocumentPo::id)
                            .eq(KnowledgeBaseDocumentPo::knowledgeBaseId, knowledgeBaseId),
                    ).mapNotNull { it.id }

            log.info("成功获取知识库 $knowledgeBaseId 的文档ID列表，共 ${documentIds.size} 个文档")
            return CommonRespDto.success(documentIds)
        } catch (e: Exception) {
            log.error("获取知识库文档ID列表失败", e)
            return CommonRespDto.error("获取知识库文档ID列表失败：${e.message}")
        }
    }

    override fun updateDocumentStatus(dto: UpdateDocumentStatusDto): CommonRespDto<Void> {
        log.info("更新文档状态：$dto")

        // 参数验证
        if (dto.documentIds.isNullOrEmpty()) {
            return CommonRespDto.error("文档ID列表不能为空")
        }

        try {
            // 检查文档是否存在
            val existingDocuments =
                knowledgeBaseDocumentMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocumentPo())
                        .select(
                            KnowledgeBaseDocumentPo::id,
                            KnowledgeBaseDocumentPo::previewChunks,
                            KnowledgeBaseDocumentPo::chunkingStrategy,
                            KnowledgeBaseDocumentPo::knowledgeBaseId,
                        ).`in`(KnowledgeBaseDocumentPo::id, dto.documentIds),
                )

            if (existingDocuments.size != dto.documentIds.size) {
                return CommonRespDto.error("部分文档不存在")
            }

            // 批量更新文档状态
            dto.status?.also {
                log.info("更新文档${dto.documentIds}状态为${it.name}")
                knowledgeBaseDocumentMapper.update(
                    KtUpdateWrapper(KnowledgeBaseDocumentPo())
                        .`in`(KnowledgeBaseDocumentPo::id, dto.documentIds)
                        .set(KnowledgeBaseDocumentPo::status, it.name),
                )

                try {
                    // 批量更新向量状态为启用
                    knowledgeBaseDocVectorMapper.update(
                        KtUpdateWrapper(KnowledgeBaseDocVector())
                            .`in`(KnowledgeBaseDocVector::documentId, dto.documentIds)
                            .set(KnowledgeBaseDocVector::status, it.name),
                    )

                    // 批量更新 preview_chunks 状态
                    existingDocuments.forEach { document ->
                        updatePreviewChunksStatus(document, null, it.name)?.also { updatedPreviewChunks ->
                            knowledgeBaseDocumentMapper.updateById(
                                KnowledgeBaseDocumentPo().apply {
                                    id = document.id
                                    previewChunks = updatedPreviewChunks
                                    updateTime = LocalDateTime.now()
                                    updateBy = UserAuthUtil.getUserId()
                                },
                            )
                            knowledgeBaseMetadataService.updateLastUpdateDate(document.knowledgeBaseId, document.id)
                            log.info("成功更新文档${document.id}中所有preview_chunks字段")
                        }
                    }
                } catch (e: Exception) {
                    log.error("更新文档所有分段状态失败", e)
                }
            }

            // 更新是否归档
            dto.isArchived?.also {
                log.info("更新文档${dto.documentIds}是否归档: $it")
                knowledgeBaseDocumentMapper.update(
                    KtUpdateWrapper(KnowledgeBaseDocumentPo())
                        .`in`(KnowledgeBaseDocumentPo::id, dto.documentIds)
                        .set(KnowledgeBaseDocumentPo::isArchived, it)
                        .set(KnowledgeBaseDocumentPo::updateTime, LocalDateTime.now())
                        .set(KnowledgeBaseDocumentPo::updateBy, UserAuthUtil.getUserId()),
                )
                existingDocuments.forEach { document ->
                    knowledgeBaseMetadataService.updateLastUpdateDate(document.knowledgeBaseId, document.id)
                }
            }

            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("更新文档状态失败", e)
            return CommonRespDto.error("更新文档状态失败：${e.message}")
        }
    }

    override fun getDocumentById(documentId: Int?): CommonRespDto<KnowledgeBaseDocumentDto> {
        // 参数验证
        if (documentId == null) {
            return CommonRespDto.error("文档ID不能为空")
        }

        try {
            // 查询文档，包含所有字段
            val document =
                knowledgeBaseDocumentMapper.selectOne(
                    KtQueryWrapper(KnowledgeBaseDocumentPo())
                        .eq(KnowledgeBaseDocumentPo::id, documentId),
                )

            if (document == null) {
                return CommonRespDto.error("文档不存在")
            }

            val disabledPrimaryKeys =
                knowledgeBaseDocVectorMapper.selectObjs<Int>(
                    KtQueryWrapper(
                        KnowledgeBaseDocVector(),
                    ).select(
                        KnowledgeBaseDocVector::id,
                    ).eq(KnowledgeBaseDocVector::documentId, documentId)
                        .ne(KnowledgeBaseDocVector::status, "ENABLE"),
                )

            val vectors =
                knowledgeBaseDocVectorMapper.selectList(
                    KtQueryWrapper(
                        KnowledgeBaseDocVector(),
                    ).select(
                        KnowledgeBaseDocVector::id,
                        KnowledgeBaseDocVector::createTime,
                        KnowledgeBaseDocVector::updateTime,
                    ).eq(KnowledgeBaseDocVector::documentId, documentId),
                )
            val editedPrimaryKeys =
                vectors
                    .filter { it.createTime != null && it.updateTime != null }
                    .filter {
                        it.createTime?.isEqual(
                            it.updateTime,
                        ) == false
                    }.map { it.id }

            val documentDto =
                KnowledgeBaseDocumentDto
                    .builder()
                    .id(document.id)
                    .name(document.name)
                    .knowledgeBaseId(document.knowledgeBaseId)
                    .chunkingStrategy(document.chunkingStrategy)
                    .uniqueName(document.uniqueName)
                    .processStatus(document.processStatus)
                    .wordCount(document.wordCount)
                    .status(document.status)
                    .chunks(document.chunks)
                    .previewChunks(document.previewChunks)
                    .createTime(document.createTime)
                    .updateTime(document.updateTime)
                    .processFailedReason(document.processFailedReason)
                    .isArchived(document.isArchived)
                    .disabledPrimaryKeys(disabledPrimaryKeys)
                    .editedPrimaryKeys(editedPrimaryKeys)
                    .build()

            log.info("成功获取文档详情，文档ID: $documentId")
            return CommonRespDto.success(documentDto)
        } catch (e: Exception) {
            log.error("获取文档详情失败", e)
            return CommonRespDto.error("获取文档详情失败：${e.message}")
        }
    }

    override fun getDocumentProcessStatus(documentId: Int?): String {
        if (documentId == null) {
            return "ERROR: 文档ID不能为空"
        }

        try {
            val document =
                knowledgeBaseDocumentMapper.selectOne(
                    KtQueryWrapper(KnowledgeBaseDocumentPo())
                        .select(KnowledgeBaseDocumentPo::processStatus)
                        .eq(KnowledgeBaseDocumentPo::id, documentId),
                )

            return document?.processStatus ?: "NOT_FOUND"
        } catch (e: Exception) {
            log.error("获取文档处理状态失败，文档ID: $documentId", e)
            return "ERROR: ${e.message}"
        }
    }

    override fun getKnowledgeBaseDocumentsStatus(knowledgeBaseId: Int?): Map<Int, DocumentProcessDto> {
        if (knowledgeBaseId == null) {
            return emptyMap()
        }

        try {
            val documents =
                knowledgeBaseDocumentMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocumentPo())
                        .select(KnowledgeBaseDocumentPo::id, KnowledgeBaseDocumentPo::processStatus, KnowledgeBaseDocumentPo::wordCount)
                        .eq(KnowledgeBaseDocumentPo::knowledgeBaseId, knowledgeBaseId),
                )

            return documents.associate { document ->
                document.id!! to DocumentProcessDto(document.processStatus ?: "UNKNOWN", document.wordCount)
            }
        } catch (e: Exception) {
            log.error("获取知识库文档状态失败，知识库ID: $knowledgeBaseId", e)
            return emptyMap()
        }
    }

    override fun deleteDocuments(ids: MutableList<Int>?): CommonRespDto<Void> =
        ids?.let {
            val client =
                OkHttpClient
                    .Builder()
                    .build()
            val documents = knowledgeBaseDocumentMapper.selectBatchIds(ids)
            val objectMapper = jacksonObjectMapper()
            documents.forEach {
                // 删除元数据绑定关系
                try {
                    knowledgeBaseDocumentMetadataMapper.delete(
                        KtQueryWrapper(KnowledgeBaseDocumentMetadataPo())
                            .eq(KnowledgeBaseDocumentMetadataPo::documentId, it.id),
                    )
                    log.info("删除文档${it.id}的元数据绑定关系成功")
                } catch (e: Exception) {
                    log.error("删除文档${it.id}的元数据绑定关系失败", e)
                }

                // 删除表记录
                knowledgeBaseDocumentMapper.deleteById(it)

                // 删除文件
                val path = Paths.get(uploadDir, "knowledge-base", "documents", it.uniqueName)
                try {
                    FileUtils.delete(path.toFile())
                    log.info("删除文件${path.toAbsolutePath()}成功")
                } catch (e: Exception) {
                    log.error("删除文件${path.toAbsolutePath()}失败")
                }

                // 停止向量解析
                try {
                    val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ParseFileStop".toHttpUrl()
                    val body = mapOf("key_id" to it.id)
                    val json = objectMapper.writeValueAsString(body)
                    client
                        .newCall(
                            Request
                                .Builder()
                                .url(url)
                                .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
                                .build(),
                        ).execute()
                } catch (e: Exception) {
                    log.error("停止文档${it.id}向量处理失败")
                }

                // 删除向量表记录
                try {
                    knowledgeBaseDocVectorMapper.delete(
                        KtQueryWrapper(KnowledgeBaseDocVector()).eq(KnowledgeBaseDocVector::documentId, it.id),
                    )
                    log.info("删除文档${it.id}向量成功")
                } catch (e: Exception) {
                    log.error("删除文档${it.id}向量失败", e)
                }
            }

            // 智能体解绑空知识库
            executor.execute {
                try {
                    val knowledgeBaseIds = documents.map { doc -> doc.knowledgeBaseId }.toSet()
                    knowledgeBaseIds.forEach { kbId ->
                        knowledgeBaseDocumentMapper
                            .exists(
                                KtQueryWrapper(KnowledgeBaseDocumentPo()).eq(KnowledgeBaseDocumentPo::knowledgeBaseId, kbId),
                            ).takeIf { exists -> !exists }
                            ?.also {
                                log.info("知识库${kbId}为空，解绑智能体")
                                knowledgeBaseApplicationRelationMapper.delete(
                                    KtQueryWrapper(
                                        KnowledgeBaseApplicationRelationPo(),
                                    ).eq(KnowledgeBaseApplicationRelationPo::knowledgeBaseId, kbId),
                                )
                            }
                    }
                } catch (e: Exception) {
                    log.error("智能体解绑知识库失败", e)
                }
            }

            CommonRespDto.success()
        } ?: CommonRespDto.success()

    override fun enableChunks(
        documentId: Int,
        chunkIds: List<Int>,
    ): CommonRespDto<Void> {
        log.info("批量启用文档分段：文档ID=$documentId，分段ID列表=$chunkIds")

        // 参数验证
        if (chunkIds.isEmpty()) {
            return CommonRespDto.error("分段ID列表不能为空")
        }

        try {
            // 检查文档是否存在
            val document =
                knowledgeBaseDocumentMapper.selectById(documentId)
                    ?: return CommonRespDto.error("文档不存在")

            // 归档的文档不能操作
            if (document.isArchived == true) {
                return CommonRespDto.error("文档已归档，无法操作")
            }

            // 检查分段是否存在且属于该文档
            val existingChunks =
                knowledgeBaseDocVectorMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocVector())
                        .select(KnowledgeBaseDocVector::id)
                        .eq(KnowledgeBaseDocVector::documentId, documentId)
                        .`in`(KnowledgeBaseDocVector::id, chunkIds),
                )

            if (existingChunks.size != chunkIds.size) {
                return CommonRespDto.error("部分分段不存在或不属于该文档")
            }

            // 批量更新分段状态为启用
            knowledgeBaseDocVectorMapper.update(
                KtUpdateWrapper(KnowledgeBaseDocVector())
                    .eq(KnowledgeBaseDocVector::documentId, documentId)
                    .`in`(KnowledgeBaseDocVector::id, chunkIds)
                    .set(KnowledgeBaseDocVector::status, "ENABLE"),
            )

            // 更新preview_chunks字段
            updatePreviewChunksStatus(document, chunkIds, "ENABLE")?.also { updatedPreviewChunks ->
                knowledgeBaseDocumentMapper.updateById(
                    KnowledgeBaseDocumentPo().apply {
                        id = documentId
                        previewChunks = updatedPreviewChunks
                        updateTime = LocalDateTime.now()
                        updateBy = UserAuthUtil.getUserId()
                    },
                )
                log.info("成功更新文档${documentId}的preview_chunks字段")

                knowledgeBaseMetadataService.updateLastUpdateDate(document.knowledgeBaseId, document.id)
            }

            log.info("成功启用文档${documentId}的${chunkIds.size}个分段")
            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("批量启用文档分段失败", e)
            return CommonRespDto.error("批量启用文档分段失败：${e.message}")
        }
    }

    override fun disableChunks(
        documentId: Int,
        chunkIds: List<Int>,
    ): CommonRespDto<Void> {
        log.info("批量禁用文档分段：文档ID=$documentId，分段ID列表=$chunkIds")

        // 参数验证
        if (chunkIds.isEmpty()) {
            return CommonRespDto.error("分段ID列表不能为空")
        }

        try {
            // 检查文档是否存在
            val document =
                knowledgeBaseDocumentMapper.selectById(documentId)
                    ?: return CommonRespDto.error("文档不存在")

            if (document.isArchived == true) {
                return CommonRespDto.error("文档已归档，无法操作")
            }

            // 检查分段是否存在且属于该文档
            val existingChunks =
                knowledgeBaseDocVectorMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocVector())
                        .select(KnowledgeBaseDocVector::id)
                        .eq(KnowledgeBaseDocVector::documentId, documentId)
                        .`in`(KnowledgeBaseDocVector::id, chunkIds),
                )

            if (existingChunks.size != chunkIds.size) {
                return CommonRespDto.error("部分分段不存在或不属于该文档")
            }

            // 批量更新分段状态为禁用
            knowledgeBaseDocVectorMapper.update(
                KtUpdateWrapper(KnowledgeBaseDocVector())
                    .eq(KnowledgeBaseDocVector::documentId, documentId)
                    .`in`(KnowledgeBaseDocVector::id, chunkIds)
                    .set(KnowledgeBaseDocVector::status, "DISABLE"),
            )

            // 更新preview_chunks字段
            updatePreviewChunksStatus(document, chunkIds, "DISABLE")?.also { updatedPreviewChunks ->
                knowledgeBaseDocumentMapper.updateById(
                    KnowledgeBaseDocumentPo().apply {
                        id = documentId
                        previewChunks = updatedPreviewChunks
                        updateTime = LocalDateTime.now()
                        updateBy = UserAuthUtil.getUserId()
                    },
                )
                log.info("成功更新文档${documentId}的preview_chunks字段")
                knowledgeBaseMetadataService.updateLastUpdateDate(document.knowledgeBaseId, document.id)
            }

            log.info("成功禁用文档${documentId}的${chunkIds.size}个分段")
            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("批量禁用文档分段失败", e)
            return CommonRespDto.error("批量禁用文档分段失败：${e.message}")
        }
    }

    override fun deleteChunks(
        documentId: Int,
        chunkIds: List<Int>,
    ): CommonRespDto<Void> {
        log.info("批量删除文档分段：文档ID=$documentId，分段ID列表=$chunkIds")

        // 参数验证
        if (chunkIds.isEmpty()) {
            return CommonRespDto.error("分段ID列表不能为空")
        }

        try {
            // 检查文档是否存在
            val document =
                knowledgeBaseDocumentMapper.selectById(documentId)
                    ?: return CommonRespDto.error("文档不存在")

            // 检查分段是否存在且属于该文档
            val existingChunks =
                knowledgeBaseDocVectorMapper.selectList(
                    KtQueryWrapper(KnowledgeBaseDocVector())
                        .select(KnowledgeBaseDocVector::id)
                        .eq(KnowledgeBaseDocVector::documentId, documentId)
                        .`in`(KnowledgeBaseDocVector::id, chunkIds),
                )

            if (existingChunks.size != chunkIds.size) {
                return CommonRespDto.error("部分分段不存在或不属于该文档")
            }

            // 批量删除分段
            knowledgeBaseDocVectorMapper.delete(
                KtQueryWrapper(KnowledgeBaseDocVector())
                    .eq(KnowledgeBaseDocVector::documentId, documentId)
                    .`in`(KnowledgeBaseDocVector::id, chunkIds),
            )

            // 更新preview_chunks字段
            removeChunksFromPreviewChunks(document, chunkIds)?.also { updatedPreviewChunks ->
                knowledgeBaseDocumentMapper.updateById(
                    KnowledgeBaseDocumentPo().apply {
                        id = documentId
                        previewChunks = updatedPreviewChunks
                        updateTime = LocalDateTime.now()
                        updateBy = UserAuthUtil.getUserId()
                    },
                )
                log.info("成功更新文档${documentId}的preview_chunks字段")
                knowledgeBaseMetadataService.updateLastUpdateDate(document.knowledgeBaseId, document.id)
            }

            log.info("成功删除文档${documentId}的${chunkIds.size}个分段")
            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("批量删除文档分段失败", e)
            return CommonRespDto.error("批量删除文档分段失败：${e.message}")
        }
    }

    override fun saveAndProcess(dto: PreviewChunksDto): CommonRespDto<Void> {
        val document = knowledgeBaseDocumentMapper.selectById(dto.documentId)
        val knowledgeBase = knowledgeBaseMapper.selectById(document.knowledgeBaseId)

        knowledgeBase.chunkIdentifier = dto.chunkSetting.chunkIdentifier
        knowledgeBase.chunkMaxLength = dto.chunkSetting.chunkSize
        knowledgeBase.chunkOverlapLength = dto.chunkSetting.chunkOverlap
        knowledgeBase.enableTextProcessFirstRule = dto.cleanerSetting.filterBlank
        knowledgeBase.enableTextProcessSecondRule = dto.cleanerSetting.removeUrl
        knowledgeBase.updateTime = LocalDateTime.now()

        when (knowledgeBase.chunkingStrategy) {
            KnowledgeBasePo.KnowledgeBaseChunkingStrategy.COMMON -> {
                if (knowledgeBase.enableQaChunk == true) {
                    val qaModelId =
                        dto.qaSetting.modelId
                            ?: return CommonRespDto.error("QA模型ID为空")
                    val qaModel = modelService.getInfo(qaModelId).data
                    if (qaModel == null || qaModel.isShelf == false) {
                        return CommonRespDto.error("Q&A分段模型已下架")
                    }

                    knowledgeBase.qaModelId = dto.qaSetting.modelId
                }
            }

            KnowledgeBasePo.KnowledgeBaseChunkingStrategy.PARENT_CHILD -> {
                knowledgeBase.parentChunkContext = if (dto.parentChunkSetting.fulltext == true) "FULLTEXT" else "PARAGRAPH"
                knowledgeBase.paragraphChunkIdentifier = dto.parentChunkSetting.chunkIdentifier
                knowledgeBase.paragraphChunkMaxLength = dto.parentChunkSetting.chunkSize
                knowledgeBase.chunkIdentifier = dto.childChunkSetting.chunkIdentifier
                knowledgeBase.chunkMaxLength = dto.childChunkSetting.chunkSize
            }
            else -> {}
        }

        knowledgeBaseMapper.updateById(knowledgeBase)

        knowledgeBaseDocumentMapper.update(
            KtUpdateWrapper(
                KnowledgeBaseDocumentPo(),
            ).eq(
                KnowledgeBaseDocumentPo::id,
                document.id,
            ).set(
                KnowledgeBaseDocumentPo::updateTime,
                LocalDateTime.now(),
            ).set(KnowledgeBaseDocumentPo::updateBy, UserAuthUtil.getUserId()),
        )

        knowledgeBaseMetadataService.updateLastUpdateDate(document.knowledgeBaseId, document.id)

        // 根据分段策略，异步调用文档解析api
        knowledgeBase.chunkingStrategy?.let { strategy ->
            when (strategy) {
                KnowledgeBasePo.KnowledgeBaseChunkingStrategy.COMMON -> {
                    knowledgeBase.embeddingModelId?.also {
                        parseCommonChunkAsync(dto, it, dto.documentId)
                    }
                }

                KnowledgeBasePo.KnowledgeBaseChunkingStrategy.PARENT_CHILD -> {
                    knowledgeBase.embeddingModelId?.also {
                        parseParentChildChunkAsync(dto, it, dto.documentId)
                    }
                }
            }
        }

        return CommonRespDto.success()
    }

    @Async
    fun parseCommonChunkAsync(
        params: PreviewChunksDto,
        embeddingModelId: Int,
        documentId: Int,
    ) {
        val document = knowledgeBaseDocumentMapper.selectById(documentId)
        val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ParseFileNormal"
        val embeddingModel = modelService.getInfo(embeddingModelId).data
        val requestBodyMap =
            mutableMapOf(
                "key_id" to documentId,
                "knowledge_base_id" to document.knowledgeBaseId,
                "file_url" to "knowledge-base/documents/${document.uniqueName}",
                "chunk_setting" to
                    mapOf(
                        "chunk_identifier" to params.chunkSetting.chunkIdentifier,
                        "chunk_size" to params.chunkSetting.chunkSize,
                        "chunk_overlap" to params.chunkSetting.chunkOverlap,
                    ),
                "cleaner_setting" to
                    mapOf(
                        "filter_blank" to params.cleanerSetting.filterBlank,
                        "remove_url" to params.cleanerSetting.removeUrl,
                    ),
                "model_instance_provider" to embeddingModel.loadingMode,
                "model_instance_config" to
                    mapOf(
                        "model_name" to embeddingModel.internalName,
                        "base_url" to embeddingModel.url,
                        "context_length" to embeddingModel.contextLength,
                        "max_token_length" to embeddingModel.tokenMax,
                        "is_support_vision" to embeddingModel.isSupportVisual,
                        "is_support_function" to embeddingModel.isSupportFunction,
                    ),
            )
        if (params.qaSetting?.enable == true) {
            val qaModel = modelService.getInfo(params.qaSetting.modelId).data
            requestBodyMap["qa_setting"] =
                mapOf(
                    "enable" to true,
                    "language" to
                        if (params
                                .qaSetting
                                ?.language
                                .isNullOrBlank()
                        ) {
                            "Chinese Simplified"
                        } else {
                            params.qaSetting?.language
                        },
                    "model_instance_provider" to qaModel?.loadingMode,
                    "model_instance_config" to
                        mapOf(
                            "model_name" to qaModel?.internalName,
                            "base_url" to qaModel?.url,
                            "context_length" to qaModel?.contextLength,
                            "max_token_length" to qaModel?.tokenMax,
                            "is_support_vision" to qaModel?.isSupportVisual,
                            "is_support_function" to qaModel?.isSupportFunction,
                        ),
                )
        } else {
            requestBodyMap["qa_setting"] =
                mapOf(
                    "enable" to false,
                )
        }
        val objectMapper = jacksonObjectMapper()
        val json = objectMapper.writeValueAsString(requestBodyMap)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request =
            Request
                .Builder()
                .url(url)
                .post(body)
                .build()
        val client =
            OkHttpClient
                .Builder()
                .build()
        try {
            client.newCall(request).execute().use { response ->
                log.info("parseCommonChunkAsync 请求: $json")
                log.info("parseCommonChunkAsync 响应: ${response.code} ${response.body?.string()}")
            }
        } catch (e: Exception) {
            log.error("parseCommonChunkAsync 异常", e)
        }
    }

    @Async
    fun parseParentChildChunkAsync(
        params: PreviewChunksDto,
        embeddingModelId: Int,
        documentId: Int,
    ) {
        val document = knowledgeBaseDocumentMapper.selectById(documentId)
        val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ParseFileAdvanced"
        val embeddingModel = modelService.getInfo(embeddingModelId).data
        val requestBodyMap =
            mapOf(
                "key_id" to documentId,
                "knowledge_base_id" to document.knowledgeBaseId,
                "file_url" to "knowledge-base/documents/${document.uniqueName}",
                "cleaner_setting" to
                    mapOf(
                        "filter_blank" to params.cleanerSetting.filterBlank,
                        "remove_url" to params.cleanerSetting.removeUrl,
                    ),
                "fatherchunk_setting" to
                    mapOf(
                        "fulltext" to params.parentChunkSetting.fulltext,
                        "chunk_identifier" to params.parentChunkSetting.chunkIdentifier,
                        "chunk_size" to params.parentChunkSetting.chunkSize,
                    ),
                "sonchunk_setting" to
                    mapOf(
                        "chunk_identifier" to params.childChunkSetting.chunkIdentifier,
                        "chunk_size" to params.childChunkSetting.chunkSize,
                    ),
                "model_instance_provider" to embeddingModel.loadingMode,
                "model_instance_config" to
                    mapOf(
                        "model_name" to embeddingModel.internalName,
                        "base_url" to embeddingModel.url,
                        "context_length" to embeddingModel.contextLength,
                        "max_token_length" to embeddingModel.tokenMax,
                        "is_support_vision" to embeddingModel.isSupportVisual,
                        "is_support_function" to embeddingModel.isSupportFunction,
                    ),
            )
        val objectMapper = jacksonObjectMapper()
        val json = objectMapper.writeValueAsString(requestBodyMap)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request =
            Request
                .Builder()
                .url(url)
                .post(body)
                .build()
        val client =
            OkHttpClient
                .Builder()
                .build()
        try {
            client.newCall(request).execute().use { response ->
                log.info("parseParentChildChunkAsync 请求: $json")
                log.info("parseParentChildChunkAsync 响应: ${response.code} ${response.body?.string()}")
            }
        } catch (e: Exception) {
            log.error("parseParentChildChunkAsync 异常", e)
        }
    }
}

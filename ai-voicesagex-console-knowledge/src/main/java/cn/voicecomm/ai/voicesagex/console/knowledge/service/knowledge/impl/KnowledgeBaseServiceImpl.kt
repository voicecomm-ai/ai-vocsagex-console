package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl

import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseMetadataService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeExtractionManageService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphTagEdgeService
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.*
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.*
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.BuiltInMetadata
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.*
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.KnowledgeBaseHandler
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphSpaceManageService
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.*
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lombok.extern.slf4j.Slf4j
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.dubbo.config.annotation.DubboReference
import org.apache.dubbo.config.annotation.DubboService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
@DubboService
@Slf4j
class KnowledgeBaseServiceImpl(
    private val knowledgeBaseMapper: KnowledgeBaseMapper,
    private val knowledgeBaseTagRelationMapper: KnowledgeBaseTagRelationMapper,
    private val knowledgeBaseDocumentMapper: KnowledgeBaseDocumentMapper,
    private val knowledgeBaseTagMapper: KnowledgeBaseTagMapper,
    private val knowledgeBaseApplicationRelationMapper: KnowledgeBaseApplicationRelationMapper,
    private val knowledgeBaseDocVectorMapper: KnowledgeBaseDocVectorMapper,
    private val knowledgeBaseDocumentMetadataMapper: KnowledgeBaseDocumentMetadataMapper,
    private val knowledgeBaseMetadataService: KnowledgeBaseMetadataService,
    private val retrievalTestRecordMapper: RetrievalTestRecordMapper,
    private val graphSpaceManageService: GraphSpaceManageService,
    private val knowledgeBaseHandler: KnowledgeBaseHandler,
    private val knowledgeExtractionManageService: KnowledgeExtractionManageService,
    @Value("\${pyServer}") val pyServer: String,
    @Value("\${file.upload}") val uploadDir: String,
) : KnowledgeBaseService {
    private val log: Logger = LoggerFactory.getLogger(KnowledgeBaseServiceImpl::class.java)
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    @DubboReference
    lateinit var backendUserService: BackendUserService

    @DubboReference
    lateinit var applicationService: ApplicationService

    @DubboReference
    lateinit var modelService: ModelService

    @DubboReference
    lateinit var knowledgeGraphTagEdgeService: KnowledgeGraphTagEdgeService

    @Transactional(rollbackFor = [Exception::class])
    override fun createEmptyKnowledgeBase(
        name: String,
        description: String?,
        knowledgeBaseType: KnowledgeBaseType,
    ): CommonRespDto<Void> {
        val exists = knowledgeBaseMapper.exists(
            KtQueryWrapper(KnowledgeBasePo()).eq(
                KnowledgeBasePo::name,
                name,
            ),
        )
        return if (!exists) {
            val po = KnowledgeBasePo().apply {
                createBy = UserAuthUtil.getUserId()
                updateBy = UserAuthUtil.getUserId()
                createTime = LocalDateTime.now()
                updateTime = LocalDateTime.now()
                this.name = name
                this.description = description
                type = KnowledgeBasePo.KnowledgeBaseType.valueOf(knowledgeBaseType.name)
            }
            knowledgeBaseMapper.insert(po)
            // 存储可视化初始状态数据
            graphSpaceManageService.createGraphPattern(po.id);
            if (knowledgeBaseType.name == KnowledgeBasePo.KnowledgeBaseType.GRAPH.name) {
                try {
                    knowledgeBaseHandler.createNebulaSpace(SpaceConstant.SPACE_NAME_FIX + po.id)
                    log.info("创建图知识库成功")
                } catch (e: Exception) {
                    log.error("创建图知识库失败", e)
                    return CommonRespDto.error(e.message)
                }
            }
            CommonRespDto.success()
        } else {
            CommonRespDto.error("知识库名称重复")
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun deleteKnowledgeBase(id: Int): CommonRespDto<String> {

        //校验是否包含抽取任务
        var knowledgeExtractionList =
            knowledgeExtractionManageService.getKnowledgeExtractionBySpaceId(id);

        if (knowledgeExtractionList.isNotEmpty()) {
            return CommonRespDto.success(knowledgeExtractionList.map { it.jobName }
                .joinToString(", "));
        }


        val knowledgeBase = knowledgeBaseMapper.selectById(id)
        knowledgeBaseMapper.deleteById(id)

        // 删除图知识库
        // TODO: 完善后续逻辑
        if (knowledgeBase.type == KnowledgeBasePo.KnowledgeBaseType.GRAPH) {
            try {
                knowledgeGraphTagEdgeService.deleteTagEdgeByKnowledgeId(knowledgeBase.id)
                // 删除图数据库操作
                graphSpaceManageService.dropSpace(SpaceConstant.SPACE_FIX_NAME + "_" + id)
            } catch (e: Exception) {
                log.error("删除图知识库 ${knowledgeBase.id} 失败", e)
            }
        }

        knowledgeBaseApplicationRelationMapper.delete(
            KtQueryWrapper(
                KnowledgeBaseApplicationRelationPo(),
            ).eq(KnowledgeBaseApplicationRelationPo::knowledgeBaseId, id),
        )
        knowledgeBaseTagRelationMapper.delete(
            KtQueryWrapper(KnowledgeBaseTagRelationPo()).eq(
                KnowledgeBaseTagRelationPo::knowledgeBaseId,
                id,
            ),
        )
        val documents = knowledgeBaseDocumentMapper.selectList(
            KtQueryWrapper(KnowledgeBaseDocumentPo()).eq(
                KnowledgeBaseDocumentPo::knowledgeBaseId,
                id,
            ),
        )

        // 删除向量表记录
        try {
            knowledgeBaseDocVectorMapper.delete(
                KtQueryWrapper(KnowledgeBaseDocVector()).eq(
                    KnowledgeBaseDocVector::knowledgeBaseId,
                    id
                ),
            )
            log.info("删除知识库${id}向量成功")
        } catch (e: Exception) {
            log.error("删除知识库${id}向量失败", e)
        }

        val client = OkHttpClient.Builder().build()
        val objectMapper = jacksonObjectMapper()
        documents?.forEach {
            knowledgeBaseDocumentMapper.deleteById(it)
            val path = Paths.get(uploadDir, "knowledge-base", "documents", it.uniqueName)
            try {
                FileUtils.delete(path.toFile())
                log.info("删除知识库${id}中文件${path.toAbsolutePath()}成功")
            } catch (e: Exception) {
                log.error("删除知识库${id}中文件${path.toAbsolutePath()}失败")
            }

            // 停止向量处理
            try {
                val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ParseFileStop".toHttpUrl()
                val body = mapOf("key_id" to it.id)
                val json = objectMapper.writeValueAsString(body)
                client.newCall(
                    Request.Builder().url(url)
                        .post(json.toRequestBody("application/json".toMediaTypeOrNull())).build(),
                ).execute()
            } catch (e: Exception) {
                log.error("停止文档${it.id}向量处理失败")
            }
        }
        return CommonRespDto.success()
    }

    override fun editKnowledgeBaseNameAndDesc(
        id: Int,
        name: String,
        description: String?,
    ): CommonRespDto<Void> {
        val knowledgeBase = knowledgeBaseMapper.selectById(id)
            ?: return CommonRespDto.error("知识库不存在")

        knowledgeBase.apply {
            name.takeIf { StringUtils.isNotBlank(it) }?.let { this.name = it }
            this.description = description
            updateTime = LocalDateTime.now()
        }.also { po ->
            knowledgeBaseMapper.updateById(po)
        }
        return CommonRespDto.success()
    }

    override fun updateBaseSetting(dto: UpdateKnowledgeBaseSettingDto?): CommonRespDto<Void> {
        if (dto == null) {
            return CommonRespDto.error("参数不能为空")
        }

        // 检查知识库是否存在
        val knowledgeBase = knowledgeBaseMapper.selectById(dto.id)
            ?: return CommonRespDto.error("知识库不存在")

        val userId = UserAuthUtil.getUserId()

        val embeddingModelResp = modelService.getInfo(dto.embeddingModelId)
        val embeddingModel = embeddingModelResp.data

        if (!embeddingModelResp.isOk || embeddingModel == null || !embeddingModel.isShelf) {
            return CommonRespDto.error("Embedding模型已下架")
        }

        val rerankModelResp = modelService.getInfo(dto.rerankModelId)
        val rerankModel = rerankModelResp.data

        if (!rerankModelResp.isOk || rerankModel == null || !rerankModel.isShelf) {
            return CommonRespDto.error("Rerank模型已下架")
        }

        // embedding模型改动，重新构建索引
        if (Objects.nonNull(dto.embeddingModelId) && !dto.embeddingModelId.equals(knowledgeBase.embeddingModelId)) {
            // 如果当前有文档正在被解析，则无法更新知识库
            val processingCount = knowledgeBaseDocumentMapper.selectCount(
                KtQueryWrapper(KnowledgeBaseDocumentPo()).eq(
                    KnowledgeBaseDocumentPo::knowledgeBaseId,
                    knowledgeBase.id
                ).eq(
                    KnowledgeBaseDocumentPo::processStatus,
                    DocumentProcessStatus.IN_PROGRESS.name,
                ),
            )
            if (processingCount > 0) {
                return CommonRespDto.error("知识库嵌入处理中，向量模型不可修改")
            }

            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/RebuildEmbedding"
            executor.execute {
                val requestBodyMap = mapOf(
                    "knowledge_base_id" to dto.id,
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to mapOf(
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

                val request = Request.Builder().url(url).post(body).build()
                val client = OkHttpClient.Builder().build()
                try {
                    client.newCall(request).execute()
                } catch (e: Exception) {
                    log.error("重新构建索引异常", e)
                }
            }
        }

        // 更新知识库设置
        knowledgeBase.apply {
            name = dto.name
            description = dto.description
            embeddingModelId = dto.embeddingModelId
            searchStrategy =
                KnowledgeBasePo.KnowledgeBaseSearchStrategy.valueOf(dto.searchStrategy.name)
            updateTime = LocalDateTime.now()
            updateBy = userId
            enableMultimodal = dto.enableMultimodal

            when (searchStrategy) {
                KnowledgeBasePo.KnowledgeBaseSearchStrategy.VECTOR -> {
                    enableVectorSearchRerank = dto.enableRerank
                    vectorSearchRerankModelId = dto.rerankModelId
                    vectorSearchTopK = dto.topK
                    enableVectorSearchScore = dto.enableScore
                    vectorSearchScore = dto.score
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.HYBRID -> {
                    enableHybridSearchRerank = dto.enableRerank
                    hybridSearchRerankModelId = dto.rerankModelId
                    hybridSearchTopK = dto.topK
                    enableHybridSearchScore = dto.enableScore
                    hybridSearchScore = dto.score
                    hybridSearchSemanticMatchingWeight = dto.hybridSearchSemanticMatchingWeight
                    hybridSearchKeywordMatchingWeight = dto.hybridSearchKeywordMatchingWeight
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.FULL_TEXT -> {
                    enableFullTextSearchRerank = dto.enableRerank
                    fullTextSearchRerankModelId = dto.rerankModelId
                    fullTextSearchTopK = dto.topK
                    enableFullTextSearchScore = dto.enableScore
                    fullTextSearchScore = dto.score
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.GRAPH -> {
                    enableGraphSearchRerank = dto.enableRerank
                    graphSearchRerankModelId = dto.rerankModelId
                    graphSearchTopK = dto.topK
                }

                else -> {}
            }
        }.also { po ->
            knowledgeBaseMapper.updateById(po)
        }

        return CommonRespDto.success()
    }

    override fun saveAndProcess(dto: SaveAndProcessBaseDto): CommonRespDto<Int> =
        knowledgeBaseMapper.exists(
            KtQueryWrapper(KnowledgeBasePo()).eq(
                KnowledgeBasePo::name,
                dto.name,
            ),
        ).let {
            // 校验模型是否下架
            // Embedding模型
            val isEmbeddingModelAvailable = modelService.isAvailable(dto.embeddingModelId)
            if (!isEmbeddingModelAvailable.data) {
                return CommonRespDto.error("Embedding模型已下架")
            }

            // Rerank模型
            if (dto.enableRerank == true) {
                val isAvailableResp = modelService.isAvailable(dto.rerankModelId)
                if (!isAvailableResp.data) {
                    return CommonRespDto.error("Rerank模型已下架")
                }
            }

            if (dto.chunkingStrategy == ChunkingStrategy.COMMON && dto.previewParams?.qaSetting?.enable == true) {
                val qaModelId = dto.previewParams.qaSetting.modelId
                    ?: return CommonRespDto.error("QA模型ID为空")
                val qaModel = modelService.getInfo(qaModelId).data

                if (qaModel == null || qaModel.isShelf == false) {
                    return CommonRespDto.error("Q&A分段模型已下架")
                }
            }

            // 文档分段策略
            var documentChunkingStrategy = DocumentChunkingStrategy.NORMAL
            when (dto.chunkingStrategy) {
                ChunkingStrategy.COMMON -> {
                    if (dto.previewParams?.qaSetting?.enable == true) {
                        documentChunkingStrategy = DocumentChunkingStrategy.NORMAL_QA
                    }
                }

                ChunkingStrategy.PARENT_CHILD -> {
                    documentChunkingStrategy =
                        if (dto.previewParams?.parentChunkSetting?.fulltext == true) {
                            DocumentChunkingStrategy.ADVANCED_FULL_DOC
                        } else {
                            DocumentChunkingStrategy.ADVANCED_PARAGRAPH
                        }
                }

                else -> {}
            }

            // TODO 重名检查
            // if (it) {
            if (true) {
                KnowledgeBasePo().apply {
                    id = dto.knowledgeBaseId
                    name = dto.name
                    type = KnowledgeBasePo.KnowledgeBaseType.valueOf(dto.type)
                    chunkingStrategy =
                        KnowledgeBasePo.KnowledgeBaseChunkingStrategy.valueOf(dto.chunkingStrategy.name)
                    enableQaChunk = dto.previewParams?.qaSetting?.enable
                    qaModelId = dto.previewParams?.qaSetting?.modelId
                    enableTextProcessFirstRule = dto.previewParams?.cleanerSetting?.filterBlank
                    enableTextProcessSecondRule = dto.previewParams?.cleanerSetting?.removeUrl
                    enableMultimodal = dto.enableMultimodal
                    embeddingModelId = dto.embeddingModelId
                    searchStrategy =
                        KnowledgeBasePo.KnowledgeBaseSearchStrategy.valueOf(dto.searchStrategy)
                    createBy = UserAuthUtil.getUserId()
                    updateBy = UserAuthUtil.getUserId()
                    createTime = LocalDateTime.now()
                    updateTime = LocalDateTime.now()
                    description = dto.name

                    when (chunkingStrategy) {
                        KnowledgeBasePo.KnowledgeBaseChunkingStrategy.COMMON -> {
                            chunkIdentifier = dto.previewParams?.chunkSetting?.chunkIdentifier
                            chunkMaxLength = dto.previewParams?.chunkSetting?.chunkSize
                            chunkOverlapLength = dto.previewParams?.chunkSetting?.chunkOverlap
                        }

                        KnowledgeBasePo.KnowledgeBaseChunkingStrategy.PARENT_CHILD -> {
                            parentChunkContext =
                                if (dto.previewParams?.parentChunkSetting?.fulltext == true) "FULLTEXT" else "PARAGRAPH"
                            paragraphChunkIdentifier =
                                dto.previewParams?.parentChunkSetting?.chunkIdentifier
                            paragraphChunkMaxLength =
                                dto.previewParams?.parentChunkSetting?.chunkSize
                            chunkIdentifier = dto.previewParams?.childChunkSetting?.chunkIdentifier
                            chunkMaxLength = dto.previewParams?.childChunkSetting?.chunkSize
                        }

                        else -> {}
                    }

                    when (searchStrategy) {
                        KnowledgeBasePo.KnowledgeBaseSearchStrategy.VECTOR -> {
                            enableVectorSearchRerank = dto.enableRerank
                            vectorSearchRerankModelId = dto.rerankModelId
                            vectorSearchTopK = dto.topK
                            enableVectorSearchScore = dto.enableScore
                            vectorSearchScore = dto.score
                        }

                        KnowledgeBasePo.KnowledgeBaseSearchStrategy.HYBRID -> {
                            enableHybridSearchRerank = dto.enableRerank
                            hybridSearchRerankModelId = dto.rerankModelId
                            hybridSearchTopK = dto.topK
                            enableHybridSearchScore = dto.enableScore
                            hybridSearchScore = dto.score
                            hybridSearchSemanticMatchingWeight =
                                dto.hybridSearchSemanticMatchingWeight
                            hybridSearchKeywordMatchingWeight =
                                dto.hybridSearchKeywordMatchingWeight
                        }

                        KnowledgeBasePo.KnowledgeBaseSearchStrategy.FULL_TEXT -> {
                            enableFullTextSearchRerank = dto.enableRerank
                            fullTextSearchRerankModelId = dto.rerankModelId
                            fullTextSearchTopK = dto.topK
                            enableFullTextSearchScore = dto.enableScore
                            fullTextSearchScore = dto.score
                        }

                        else -> {}
                    }
                }.let { po ->
                    if (po.id == null) {
                        knowledgeBaseMapper.insert(
                            po,
                        )
                    } else {
                        knowledgeBaseMapper.updateById(
                            po,
                        )
                    }

                    // 将文档绑定到知识库
                    dto.documentIds.takeIf { ids -> !ids.isNullOrEmpty() }?.filterNotNull()
                        ?.let { ids ->
                            knowledgeBaseDocumentMapper.update(
                                KtUpdateWrapper(KnowledgeBaseDocumentPo()).`in`(
                                    KnowledgeBaseDocumentPo::id,
                                    ids
                                ).set(
                                    KnowledgeBaseDocumentPo::chunkingStrategy,
                                    documentChunkingStrategy.name
                                ).set(KnowledgeBaseDocumentPo::knowledgeBaseId, po.id),
                            )

                            // 为新绑定的文档绑定元数据
                            po.id?.also {
                                bindBuiltInMetadataToDocuments(it, ids)
                            }
                        }

                    // 根据分段策略，异步调用文档解析api
                    dto.chunkingStrategy?.let { strategy ->
                        val documentIds = dto.documentIds?.filterNotNull() ?: emptyList()
                        documentIds.forEach { docId ->
                            when (strategy) {
                                ChunkingStrategy.COMMON -> {
                                    parseCommonChunkAsync(
                                        dto.previewParams,
                                        dto.embeddingModelId,
                                        docId,
                                    )
                                }

                                ChunkingStrategy.PARENT_CHILD -> {
                                    parseParentChildChunkAsync(
                                        dto.previewParams,
                                        dto.embeddingModelId,
                                        docId,
                                    )
                                }
                            }
                        }
                    }
                    return CommonRespDto.success(po.id)
                }
            } else {
                CommonRespDto.error("知识库名称重复")
            }
        }

    override fun saveAndProcessExistKnowledgeBase(dto: SaveAndProcessExistBaseDto): CommonRespDto<Void> {
        val knowledgeBase = knowledgeBaseMapper.selectById(dto.knowledgeBaseId)

        // 检测已有文档数量是否超过50
        val count = getKnowledgeBaseDocumentCount(dto.knowledgeBaseId).data
        if (count >= 50) {
            return CommonRespDto.error("知识库文档数量超过50个")
        }

        // 校验模型是否下架
        // Embedding模型
        val isEmbeddingModelAvailable = modelService.isAvailable(knowledgeBase.embeddingModelId)
        if (!isEmbeddingModelAvailable.data) {
            return CommonRespDto.error("Embedding模型已下架")
        }

        knowledgeBase.searchStrategy?.also {
            var isAvailable = true
            when (it) {
                KnowledgeBasePo.KnowledgeBaseSearchStrategy.VECTOR -> {
                    // Rerank模型
                    if (knowledgeBase.enableVectorSearchRerank == true) {
                        val isAvailableResp =
                            modelService.isAvailable(knowledgeBase.vectorSearchRerankModelId)
                        isAvailable = isAvailableResp.data
                    }
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.HYBRID -> {
                    // Rerank模型
                    if (knowledgeBase.enableHybridSearchRerank == true) {
                        val isAvailableResp =
                            modelService.isAvailable(knowledgeBase.hybridSearchRerankModelId)
                        isAvailable = isAvailableResp.data
                    }
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.FULL_TEXT -> {
                    // Rerank模型
                    if (knowledgeBase.enableFullTextSearchRerank == true) {
                        val isAvailableResp =
                            modelService.isAvailable(knowledgeBase.fullTextSearchRerankModelId)
                        isAvailable = isAvailableResp.data
                    }
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.GRAPH -> {
                    // do nothing
                }
            }

            if (!isAvailable) {
                return CommonRespDto.error("Rerank模型已下架")
            }
        }

        if (dto.chunkingStrategy == ChunkingStrategy.COMMON && dto.previewParams?.qaSetting?.enable == true) {
            val qaModelId = dto.previewParams.qaSetting.modelId
                ?: return CommonRespDto.error("QA模型ID为空")
            val qaModel = modelService.getInfo(qaModelId).data
            if (qaModel == null || qaModel.isShelf == false) {
                return CommonRespDto.error("Q&A分段模型已下架")
            }
        }

        knowledgeBase.updateTime = LocalDateTime.now()

        // 更新知识库分段配置
        when (knowledgeBase.chunkingStrategy) {
            KnowledgeBasePo.KnowledgeBaseChunkingStrategy.COMMON -> {
                knowledgeBase.chunkIdentifier = dto.previewParams?.chunkSetting?.chunkIdentifier
                knowledgeBase.chunkMaxLength = dto.previewParams?.chunkSetting?.chunkSize
                knowledgeBase.chunkOverlapLength = dto.previewParams?.chunkSetting?.chunkOverlap
            }

            KnowledgeBasePo.KnowledgeBaseChunkingStrategy.PARENT_CHILD -> {
                knowledgeBase.parentChunkContext =
                    if (dto.previewParams?.parentChunkSetting?.fulltext == true) "FULLTEXT" else "PARAGRAPH"
                knowledgeBase.paragraphChunkIdentifier =
                    dto.previewParams?.parentChunkSetting?.chunkIdentifier
                knowledgeBase.paragraphChunkMaxLength =
                    dto.previewParams?.parentChunkSetting?.chunkSize
                knowledgeBase.chunkIdentifier =
                    dto.previewParams?.childChunkSetting?.chunkIdentifier
                knowledgeBase.chunkMaxLength = dto.previewParams?.childChunkSetting?.chunkSize
            }

            else -> {}
        }

        knowledgeBaseMapper.updateById(knowledgeBase)

        // 文档分段策略
        var documentChunkingStrategy = DocumentChunkingStrategy.NORMAL
        when (dto.chunkingStrategy) {
            ChunkingStrategy.COMMON -> {
                if (dto.previewParams?.qaSetting?.enable == true) {
                    documentChunkingStrategy = DocumentChunkingStrategy.NORMAL_QA
                }
            }

            ChunkingStrategy.PARENT_CHILD -> {
                documentChunkingStrategy =
                    if (dto.previewParams?.parentChunkSetting?.fulltext == true) {
                        DocumentChunkingStrategy.ADVANCED_FULL_DOC
                    } else {
                        DocumentChunkingStrategy.ADVANCED_PARAGRAPH
                    }
            }

            else -> {}
        }

        // 将文档绑定到知识库
        dto.documentIds.takeIf { ids -> !ids.isNullOrEmpty() }?.filterNotNull()?.forEach {
            val doc = knowledgeBaseDocumentMapper.selectById(it)
            // 覆盖同名文档
            val deletedDocIds = knowledgeBaseDocumentMapper.selectObjs<Int>(
                KtQueryWrapper(
                    KnowledgeBaseDocumentPo(),
                ).select(
                    KnowledgeBaseDocumentPo::id,
                ).eq(KnowledgeBaseDocumentPo::name, doc.name)
                    .eq(KnowledgeBaseDocumentPo::knowledgeBaseId, dto.knowledgeBaseId),
            )
            deletedDocIds.takeIf { ids -> ids.isNotEmpty() }?.also { ids ->
                knowledgeBaseDocumentMapper.deleteBatchIds(ids)
                log.info("覆盖${ids.size}条同名文档，文档名：${doc.name}，知识库ID：${dto.knowledgeBaseId}，文档ID：$deletedDocIds")

                // 删除向量库
                val deletedVectorCount = knowledgeBaseDocVectorMapper.delete(
                    KtQueryWrapper(
                        KnowledgeBaseDocVector(),
                    ).`in`(KnowledgeBaseDocVector::documentId, ids),
                )
                log.info("删除${deletedVectorCount}条向量表记录")

                // 删除元数据绑定关系
                val deletedMetadataCount = knowledgeBaseDocumentMetadataMapper.delete(
                    KtQueryWrapper(KnowledgeBaseDocumentMetadataPo()).`in`(
                        KnowledgeBaseDocumentMetadataPo::documentId,
                        ids
                    ),
                )
                log.info("删除${deletedMetadataCount}条元数据绑定关系")
            }

            // 绑定新文档
            doc.knowledgeBaseId = dto.knowledgeBaseId
            doc.chunkingStrategy = documentChunkingStrategy.name
            knowledgeBaseDocumentMapper.updateById(doc)
        }

        // 为新绑定的文档绑定元数据
        dto.documentIds?.filterNotNull()?.let { documentIds ->
            bindBuiltInMetadataToDocuments(dto.knowledgeBaseId, documentIds)
        }

        // 根据分段策略，异步调用文档解析api
        knowledgeBase.chunkingStrategy?.let { strategy ->
            val documentIds = dto.documentIds?.filterNotNull() ?: emptyList()
            documentIds.forEach { docId ->
                when (strategy) {
                    KnowledgeBasePo.KnowledgeBaseChunkingStrategy.COMMON -> {
                        knowledgeBase.embeddingModelId?.also {
                            parseCommonChunkAsync(dto.previewParams, it, docId)
                        }
                    }

                    KnowledgeBasePo.KnowledgeBaseChunkingStrategy.PARENT_CHILD -> {
                        knowledgeBase.embeddingModelId?.also {
                            parseParentChildChunkAsync(dto.previewParams, it, docId)
                        }
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
        val requestBodyMap = mutableMapOf(
            "key_id" to documentId,
            "knowledge_base_id" to document.knowledgeBaseId,
            "file_url" to "knowledge-base/documents/${document.uniqueName}",
            "chunk_setting" to mapOf(
                "chunk_identifier" to params.chunkSetting.chunkIdentifier,
                "chunk_size" to params.chunkSetting.chunkSize,
                "chunk_overlap" to params.chunkSetting.chunkOverlap,
            ),
            "cleaner_setting" to mapOf(
                "filter_blank" to params.cleanerSetting.filterBlank,
                "remove_url" to params.cleanerSetting.removeUrl,
            ),
            "model_instance_provider" to embeddingModel.loadingMode,
            "model_instance_config" to mapOf(
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
            requestBodyMap["qa_setting"] = mapOf(
                "enable" to true,
                "language" to if (params.qaSetting?.language.isNullOrBlank()) {
                    "Chinese Simplified"
                } else {
                    params.qaSetting?.language
                },
                "model_instance_provider" to qaModel?.loadingMode,
                "model_instance_config" to mapOf(
                    "model_name" to qaModel?.internalName,
                    "base_url" to qaModel?.url,
                    "context_length" to qaModel?.contextLength,
                    "max_token_length" to qaModel?.tokenMax,
                    "is_support_vision" to qaModel?.isSupportVisual,
                    "is_support_function" to qaModel?.isSupportFunction,
                ),
            )
        } else {
            requestBodyMap["qa_setting"] = mapOf(
                "enable" to false,
            )
        }
        val objectMapper = jacksonObjectMapper()
        val json = objectMapper.writeValueAsString(requestBodyMap)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder().url(url).post(body).build()
        val client = OkHttpClient.Builder().build()
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
        val requestBodyMap = mapOf(
            "key_id" to documentId,
            "knowledge_base_id" to document.knowledgeBaseId,
            "file_url" to "knowledge-base/documents/${document.uniqueName}",
            "cleaner_setting" to mapOf(
                "filter_blank" to params.cleanerSetting.filterBlank,
                "remove_url" to params.cleanerSetting.removeUrl,
            ),
            "fatherchunk_setting" to mapOf(
                "fulltext" to params.parentChunkSetting.fulltext,
                "chunk_identifier" to params.parentChunkSetting.chunkIdentifier,
                "chunk_size" to params.parentChunkSetting.chunkSize,
            ),
            "sonchunk_setting" to mapOf(
                "chunk_identifier" to params.childChunkSetting.chunkIdentifier,
                "chunk_size" to params.childChunkSetting.chunkSize,
            ),
            "model_instance_provider" to embeddingModel.loadingMode,
            "model_instance_config" to mapOf(
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

        val request = Request.Builder().url(url).post(body).build()
        val client = OkHttpClient.Builder().build()
        try {
            client.newCall(request).execute().use { response ->
                log.info("parseParentChildChunkAsync 请求: $json")
                log.info("parseParentChildChunkAsync 响应: ${response.code} ${response.body?.string()}")
            }
        } catch (e: Exception) {
            log.error("parseParentChildChunkAsync 异常", e)
        }
    }

    override fun list(
        tagIds: List<Int>?,
        name: String?,
        type: String?,
    ): CommonRespDto<List<KnowledgeBaseDto>> {
        val userId = UserAuthUtil.getUserId()

        // 数据权限
        val userIdList = backendUserService.getUserIdsByUserId(userId).data

        // 构建基础查询条件
        val qw = KtQueryWrapper(KnowledgeBasePo()).apply {
            name?.let { apply("name ILIKE {0}", "%${SpecialCharUtil.replaceSpecialWord(it)}%") }
            `in`(KnowledgeBasePo::createBy, userIdList)
            orderByDesc(KnowledgeBasePo::updateTime)
        }

        // 处理标签过滤条件
        tagIds?.takeIf { it.isNotEmpty() }?.let { ids ->
            val relations = knowledgeBaseTagRelationMapper.selectList(
                KtQueryWrapper(KnowledgeBaseTagRelationPo()).`in`(
                    KnowledgeBaseTagRelationPo::tagId,
                    ids
                ),
            )

            if (relations.isNullOrEmpty()) {
                return CommonRespDto.success(emptyList())
            }

            log.info("查找知识库列表，标签过滤条件：$relations")
            qw.`in`(KnowledgeBasePo::id, relations.map { it.knowledgeBaseId })
        }

        type?.let {
            qw.eq(KnowledgeBasePo::type, it)
        }

        // 查询知识库列表
        val bases = knowledgeBaseMapper.selectList(qw)
        if (bases.isEmpty()) {
            return CommonRespDto.success(emptyList())
        }

        // 获取知识库ID列表
        val baseIds = bases.map { it.id }

        // 获取知识库所有绑定的标签关系
        val relations = knowledgeBaseTagRelationMapper.selectList(
            KtQueryWrapper(KnowledgeBaseTagRelationPo()).`in`(
                KnowledgeBaseTagRelationPo::knowledgeBaseId,
                baseIds
            ),
        ).groupBy { it.knowledgeBaseId }

        val currentBaseTagIds = relations.values.flatten().map { it.tagId }

        // 获取标签信息
        val tags = currentBaseTagIds.takeIf { it.isNotEmpty() }?.let { ids ->
            knowledgeBaseTagMapper.selectList(
                KtQueryWrapper(KnowledgeBaseTagPo()).`in`(KnowledgeBaseTagPo::id, ids),
            ).associateBy { it.id }
        }

        // 批量查询知识库是否有文档
        val documentsByKnowledgeBase = knowledgeBaseDocumentMapper.selectList(
            KtQueryWrapper(KnowledgeBaseDocumentPo()).`in`(
                KnowledgeBaseDocumentPo::knowledgeBaseId,
                baseIds
            ),
        ).groupBy { it.knowledgeBaseId }

        val applicationRelations = knowledgeBaseApplicationRelationMapper.selectList(
            KtQueryWrapper(
                KnowledgeBaseApplicationRelationPo(),
            ).`in`(KnowledgeBaseApplicationRelationPo::knowledgeBaseId, baseIds),
        ).groupBy { it.knowledgeBaseId }

        // 构建返回结果
        return bases.map { base ->
            // 检查知识库是否有文档
            val documents = documentsByKnowledgeBase.getOrDefault(base.id, emptyList())
            val searchStrategy = base.searchStrategy?.let {
                SearchStrategy.valueOf(
                    it.name,
                )
            }

            KnowledgeBaseDto.builder().id(base.id).name(base.name).description(base.description)
                .isEmpty(documents.isEmpty()).searchStrategy(searchStrategy)
                .type(base.type?.let { KnowledgeBaseType.valueOf(it.name) }).tags(
                    relations[base.id]?.mapNotNull { relation ->
                        tags?.get(relation.tagId)?.let { tag ->
                            KnowledgeBaseTagDto.builder().name(tag.name).id(tag.id).build()
                        }
                    },
                ).documentCount(documentsByKnowledgeBase.getOrDefault(base.id, emptyList()).size)
                .applicationCount(applicationRelations.getOrDefault(base.id, emptyList()).size)
                .worldCount(
                    documents.sumOf { it.wordCount ?: 0 },
                ).build()
        }.let { CommonRespDto.success(it) }
    }

    override fun previewCommonChunks(dto: PreviewChunksDto): CommonRespDto<CommonChunkPreviewRespDataDto> =
        previewCommonChunks(
            documentId = dto.documentId,
            chunkSetting = dto.chunkSetting,
            cleanerSetting = dto.cleanerSetting,
            qaSetting = dto.qaSetting,
        )

    override fun previewParentChildChunks(dto: PreviewChunksDto): CommonRespDto<ParentChildChunkPreviewRespDataDto> =
        previewParentChildChunk(
            documentId = dto.documentId,
            parentChunkSetting = dto.parentChunkSetting,
            childChunkSetting = dto.childChunkSetting,
            cleanerSetting = dto.cleanerSetting,
        )

    private fun previewCommonChunks(
        documentId: Int,
        chunkSetting: ChunkSetting,
        cleanerSetting: CleanerSetting,
        qaSetting: QaSetting?,
    ): CommonRespDto<CommonChunkPreviewRespDataDto> {
        val url = "$pyServer/Voicecomm/VoiceSageX/Rag/PreviewChunkNormal"

        log.info("preview common chunks: {}", documentId)
        val document = knowledgeBaseDocumentMapper.selectById(documentId)
            ?: return CommonRespDto.error("文档不存在")

        var qaModel: ModelDto? = null
        qaSetting?.enable?.let {
            if (it) {
                val qaModelId = qaSetting.modelId
                qaModel = modelService.getInfo(qaModelId).data

                if (qaModel == null || qaModel?.isShelf == false) {
                    return CommonRespDto.error("Q&A分段模型已下架")
                }
            }
        }

        // 构建基础请求参数
        val baseParams = mutableMapOf(
            "key_id" to documentId,
            "file_url" to "knowledge-base/documents/${document.uniqueName}",
            "chunk_setting" to mapOf(
                "chunk_identifier" to chunkSetting.chunkIdentifier,
                "chunk_size" to chunkSetting.chunkSize,
                "chunk_overlap" to chunkSetting.chunkOverlap,
            ),
            "cleaner_setting" to mapOf(
                "filter_blank" to cleanerSetting.filterBlank,
                "remove_url" to cleanerSetting.removeUrl,
            ),
        )

        if (qaModel != null) {
            baseParams["qa_setting"] = mapOf(
                "enable" to qaSetting?.enable,
                "language" to if (qaSetting?.language.isNullOrBlank()) "Chinese Simplified" else qaSetting?.language,
                "model_instance_provider" to qaModel?.loadingMode,
                "model_instance_config" to mapOf(
                    "model_name" to qaModel?.internalName,
                    "base_url" to qaModel?.url,
                    "context_length" to qaModel?.contextLength,
                    "max_token_length" to qaModel?.tokenMax,
                    "is_support_vision" to qaModel?.isSupportVisual,
                    "is_support_function" to qaModel?.isSupportFunction,
                ),
            )
        } else {
            baseParams["qa_setting"] = mapOf(
                "enable" to false,
            )
        }

        val bodyJson = jacksonObjectMapper().writeValueAsString(baseParams)
        val body = bodyJson.toRequestBody("application/json".toMediaTypeOrNull())
        log.info(
            "doc chunk request params: {}, body: {}",
            baseParams,
            bodyJson,
        )
        val client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.MINUTES) // 连接超时
            .readTimeout(10, TimeUnit.MINUTES) // 读取超时
            .writeTimeout(10, TimeUnit.MINUTES) // 写入超时
            .build()
        return client.newCall(
            Request.Builder().url(url).post(body).build(),
        ).execute().use { response ->
            log.info("doc chunk resp: {}", response)
            if (!response.isSuccessful) {
                return CommonRespDto.error("文档分块失败")
            }
            response.body?.string()?.let { body ->
                try {
                    val mapper = jacksonObjectMapper()
                    val jsonNode = mapper.readTree(body)
                    val resp = mapper.treeToValue(
                        jsonNode,
                        CommonChunkPreviewRespDto::class.java,
                    )
                    log.info("普通文档预览: $resp")
                    log.info(body)
                    if (resp != null) {
                        if (resp.code == 1000) {
                            CommonRespDto.success("预览成功", resp.data)
                        } else {
                            CommonRespDto.error(resp.msg)
                        }
                    } else {
                        CommonRespDto.error("预览服务无响应")
                    }
                } catch (e: Exception) {
                    CommonRespDto.error("解析响应数据失败: ${e.message}")
                }
            } ?: CommonRespDto.error("响应数据为空")
        }
    }

    private fun previewParentChildChunk(
        documentId: Int,
        parentChunkSetting: ParentChunkSetting,
        childChunkSetting: ChildChunkSetting,
        cleanerSetting: CleanerSetting,
    ): CommonRespDto<ParentChildChunkPreviewRespDataDto> {
        val url = "$pyServer/Voicecomm/VoiceSageX/Rag/PreviewChunkAdvanced"

        val document = knowledgeBaseDocumentMapper.selectById(documentId)
            ?: return CommonRespDto.error("文档不存在")

        return mapOf(
            "key_id" to documentId,
            "file_url" to "knowledge-base/documents/${document.uniqueName}",
            "cleaner_setting" to mapOf(
                "filter_blank" to cleanerSetting.filterBlank,
                "remove_url" to cleanerSetting.removeUrl,
            ),
            "fatherchunk_setting" to mapOf(
                "fulltext" to parentChunkSetting.fulltext,
                "chunk_identifier" to parentChunkSetting.chunkIdentifier,
                "chunk_size" to parentChunkSetting.chunkSize,
            ),
            "sonchunk_setting" to mapOf(
                "chunk_identifier" to childChunkSetting.chunkIdentifier,
                "chunk_size" to childChunkSetting.chunkSize,
            ),
        ).let { requestBody ->
            val client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                .readTimeout(3, TimeUnit.MINUTES) // 读取超时
                .writeTimeout(30, TimeUnit.SECONDS) // 写入超时
                .build()
            client.newCall(
                Request.Builder().url(url).post(
                    jacksonObjectMapper().writeValueAsString(requestBody)
                        .toRequestBody("application/json".toMediaTypeOrNull()),
                ).build(),
            ).execute().use { response ->
                if (!response.isSuccessful) {
                    return CommonRespDto.error("文档分块失败")
                }
                response.body?.string()?.let { body ->
                    try {
                        val mapper = jacksonObjectMapper()
                        val jsonNode = mapper.readTree(body)
                        val resp = mapper.treeToValue(
                            jsonNode,
                            ParentChildChunkPreviewRespDto::class.java,
                        )
                        if (resp != null) {
                            if (resp.code == 1000) {
                                log.info(body)
                                CommonRespDto.success("预览成功", resp.data)
                            } else {
                                CommonRespDto.error(resp.msg)
                            }
                        } else {
                            CommonRespDto.error("预览服务无响应")
                        }
                    } catch (e: Exception) {
                        CommonRespDto.error("解析响应数据失败: ${e.message}")
                    }
                } ?: CommonRespDto.error("响应数据为空")
            }
        }
    }

    override fun removeKnowledgeBaseFromApplication(
        knowledgeBaseId: Int?,
        applicationId: Int?,
    ): CommonRespDto<Void> {
        // 参数验证
        if (knowledgeBaseId == null) {
            return CommonRespDto.error("知识库ID不能为空")
        }
        if (applicationId == null) {
            return CommonRespDto.error("应用ID不能为空")
        }

        // 检查知识库是否存在
        val knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId)
            ?: return CommonRespDto.error("知识库不存在")

        // 检查应用是否存在
        val applicationResp = applicationService.getById(applicationId)
        if (!applicationResp.isOk || applicationResp.data == null) {
            return CommonRespDto.error("应用不存在")
        }

        // 检查关联关系是否存在
        val existingRelation = knowledgeBaseApplicationRelationMapper.selectOne(
            KtQueryWrapper(KnowledgeBaseApplicationRelationPo()).eq(
                KnowledgeBaseApplicationRelationPo::knowledgeBaseId,
                knowledgeBaseId
            ).eq(KnowledgeBaseApplicationRelationPo::applicationId, applicationId),
        )

        if (existingRelation == null) {
            return CommonRespDto.error("该知识库未关联到此应用")
        }

        // 删除关联关系
        try {
            val deleteCount = knowledgeBaseApplicationRelationMapper.delete(
                KtQueryWrapper(KnowledgeBaseApplicationRelationPo()).eq(
                    KnowledgeBaseApplicationRelationPo::knowledgeBaseId,
                    knowledgeBaseId
                ).eq(KnowledgeBaseApplicationRelationPo::applicationId, applicationId),
            )

            if (deleteCount > 0) {
                log.info("成功解除知识库 $knowledgeBaseId 与应用 $applicationId 的关联关系")
                return CommonRespDto.success()
            } else {
                return CommonRespDto.error("解除关联失败")
            }
        } catch (e: Exception) {
            log.error("解除知识库与应用关联失败", e)
            return CommonRespDto.error("解除关联失败：${e.message}")
        }
    }

    override fun getKnowledgeBaseDetail(id: Int?): CommonRespDto<KnowledgeBaseDetailDto> {
        // 参数验证
        if (id == null) {
            return CommonRespDto.error("知识库ID不能为空")
        }

        // 检查知识库是否存在
        val knowledgeBase = knowledgeBaseMapper.selectById(id)
            ?: return CommonRespDto.error("知识库不存在")

        // 根据检索策略获取对应的配置
        val searchStrategy = knowledgeBase.searchStrategy?.let {
            cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy.valueOf(it.name)
        }

        // 根据检索策略设置通用字段
        val searchConfig = when (searchStrategy) {
            SearchStrategy.VECTOR -> {
                SearchConfig(
                    enableRerank = knowledgeBase.enableVectorSearchRerank,
                    rerankModelId = knowledgeBase.vectorSearchRerankModelId,
                    topK = knowledgeBase.vectorSearchTopK,
                    score = knowledgeBase.vectorSearchScore,
                    enableScore = knowledgeBase.enableVectorSearchScore,
                    semanticMatchingWeight = null,
                    keywordMatchingWeight = null,
                )
            }

            SearchStrategy.FULL_TEXT -> {
                SearchConfig(
                    enableRerank = knowledgeBase.enableFullTextSearchRerank,
                    rerankModelId = knowledgeBase.fullTextSearchRerankModelId,
                    topK = knowledgeBase.fullTextSearchTopK,
                    score = knowledgeBase.fullTextSearchScore,
                    enableScore = knowledgeBase.enableFullTextSearchScore,
                    semanticMatchingWeight = null,
                    keywordMatchingWeight = null,
                )
            }

            SearchStrategy.HYBRID -> {
                SearchConfig(
                    enableRerank = knowledgeBase.enableHybridSearchRerank,
                    rerankModelId = knowledgeBase.hybridSearchRerankModelId,
                    topK = knowledgeBase.hybridSearchTopK,
                    score = knowledgeBase.hybridSearchScore,
                    enableScore = knowledgeBase.enableHybridSearchScore,
                    semanticMatchingWeight = knowledgeBase.hybridSearchSemanticMatchingWeight,
                    keywordMatchingWeight = knowledgeBase.hybridSearchKeywordMatchingWeight,
                )
            }

            else -> {
                SearchConfig(
                    enableRerank = null,
                    rerankModelId = null,
                    topK = null,
                    score = null,
                    enableScore = null,
                    semanticMatchingWeight = null,
                    keywordMatchingWeight = null,
                )
            }
        }

        // 构建详情DTO
        val detailDto =
            KnowledgeBaseDetailDto.builder().id(knowledgeBase.id).name(knowledgeBase.name)
                .description(knowledgeBase.description).type(knowledgeBase.type?.name)
                .chunkingStrategy(
                    knowledgeBase.chunkingStrategy?.let {
                        cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.ChunkingStrategy.valueOf(
                            it.name,
                        )
                    },
                ).enableMultimodal(knowledgeBase.enableMultimodal)
                .chunkIdentifier(knowledgeBase.chunkIdentifier)
                .chunkMaxLength(knowledgeBase.chunkMaxLength)
                .chunkOverlapLength(knowledgeBase.chunkOverlapLength)
                .enableTextProcessFirstRule(knowledgeBase.enableTextProcessFirstRule)
                .enableTextProcessSecondRule(knowledgeBase.enableTextProcessSecondRule)
                .parentChunkContext(knowledgeBase.parentChunkContext)
                .paragraphChunkIdentifier(knowledgeBase.paragraphChunkIdentifier)
                .paragraphChunkMaxLength(knowledgeBase.paragraphChunkMaxLength)
                .enableQaChunk(knowledgeBase.enableQaChunk).qaModelId(knowledgeBase.qaModelId)
                .embeddingModelId(knowledgeBase.embeddingModelId).searchStrategy(searchStrategy)
                // 根据检索策略合并的通用字段
                .enableRerank(searchConfig.enableRerank).rerankModelId(searchConfig.rerankModelId)
                .topK(searchConfig.topK).score(searchConfig.score)
                .enableScore(searchConfig.enableScore)
                .semanticMatchingWeight(searchConfig.semanticMatchingWeight)
                .keywordMatchingWeight(searchConfig.keywordMatchingWeight)
                .createTime(knowledgeBase.createTime).updateTime(knowledgeBase.updateTime).build()

        return CommonRespDto.success(detailDto)
    }

    // 内部数据类用于存储检索配置
    private data class SearchConfig(
        val enableRerank: Boolean?,
        val rerankModelId: Int?,
        val topK: Int?,
        val score: Float?,
        val enableScore: Boolean?,
        val semanticMatchingWeight: Float?,
        val keywordMatchingWeight: Float?,
    )

    override fun addKnowledgeBasesToApplication(
        knowledgeBaseIds: List<Int>?,
        applicationId: Int?,
    ): CommonRespDto<Void> {
        // 参数验证
        if (applicationId == null) {
            return CommonRespDto.error("应用ID不能为空")
        }

        // 检查应用是否存在
        val applicationResp = applicationService.getById(applicationId)
        if (!applicationResp.isOk || applicationResp.data == null) {
            return CommonRespDto.error("应用不存在")
        }

        // 获取当前应用绑定的所有知识库关联关系
        val currentRelations = knowledgeBaseApplicationRelationMapper.selectList(
            KtQueryWrapper(KnowledgeBaseApplicationRelationPo()).eq(
                KnowledgeBaseApplicationRelationPo::applicationId,
                applicationId
            ),
        )

        val currentKnowledgeBaseIds = currentRelations.map { it.knowledgeBaseId }.toSet()
        val targetKnowledgeBaseIds = knowledgeBaseIds?.toSet() ?: emptySet()

        // 需要新增的关联关系（在目标列表中但不在当前列表中）
        val toAddIds = targetKnowledgeBaseIds - currentKnowledgeBaseIds

        // 需要删除的关联关系（在当前列表中但不在目标列表中）
        val toRemoveIds = currentKnowledgeBaseIds - targetKnowledgeBaseIds

        try {
            // 删除需要移除的关联关系
            if (toRemoveIds.isNotEmpty()) {
                val deleteCount = knowledgeBaseApplicationRelationMapper.delete(
                    KtQueryWrapper(KnowledgeBaseApplicationRelationPo()).`in`(
                        KnowledgeBaseApplicationRelationPo::knowledgeBaseId,
                        toRemoveIds
                    ).eq(KnowledgeBaseApplicationRelationPo::applicationId, applicationId),
                )
                log.info("成功移除应用 $applicationId 与知识库 ${toRemoveIds.joinToString(", ")} 的关联关系，删除数量：$deleteCount")
            }

            // 添加需要新增的关联关系
            if (toAddIds.isNotEmpty()) {
                // 检查知识库是否存在
                val knowledgeBases = knowledgeBaseMapper.selectList(
                    KtQueryWrapper(KnowledgeBasePo()).`in`(KnowledgeBasePo::id, toAddIds),
                )

                if (knowledgeBases.size != toAddIds.size) {
                    return CommonRespDto.error("部分知识库不存在")
                }

                // 批量创建关联关系
                val relations = toAddIds.map { knowledgeBaseId ->
                    KnowledgeBaseApplicationRelationPo().apply {
                        this.knowledgeBaseId = knowledgeBaseId
                        this.applicationId = applicationId
                    }
                }

                relations.forEach { relation ->
                    knowledgeBaseApplicationRelationMapper.insert(relation)
                }
                log.info("成功将知识库 ${toAddIds.joinToString(", ")} 关联到应用 $applicationId")
            }

            val operationSummary = buildString {
                if (toRemoveIds.isNotEmpty()) {
                    append("移除关联：${toRemoveIds.joinToString(", ")}")
                }
                if (toAddIds.isNotEmpty()) {
                    if (toRemoveIds.isNotEmpty()) append("；")
                    append("新增关联：${toAddIds.joinToString(", ")}")
                }
                if (toRemoveIds.isEmpty() && toAddIds.isEmpty()) {
                    append("无需操作")
                }
            }

            log.info("应用 $applicationId 知识库关联同步完成：$operationSummary")
            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("同步应用知识库关联失败", e)
            return CommonRespDto.error("同步失败：${e.message}")
        }
    }

    override fun getApplicationKnowledgeBases(applicationId: Int?): CommonRespDto<List<KnowledgeBaseDto>> {
        // 参数验证
        if (applicationId == null) {
            return CommonRespDto.error("应用ID不能为空")
        }

        // 检查应用是否存在
        val applicationResp = applicationService.getById(applicationId)
        if (!applicationResp.isOk || applicationResp.data == null) {
            return CommonRespDto.error("应用不存在")
        }

        // 获取应用绑定的知识库关联关系
        val relations = knowledgeBaseApplicationRelationMapper.selectList(
            KtQueryWrapper(KnowledgeBaseApplicationRelationPo()).eq(
                KnowledgeBaseApplicationRelationPo::applicationId,
                applicationId
            ),
        )

        if (relations.isEmpty()) {
            return CommonRespDto.success(emptyList())
        }

        // 获取知识库ID列表
        val knowledgeBaseIds = relations.map { it.knowledgeBaseId }

        // 查询知识库详细信息
        val knowledgeBases = knowledgeBaseMapper.selectList(
            KtQueryWrapper(KnowledgeBasePo()).apply {
                `in`(KnowledgeBasePo::id, knowledgeBaseIds)
            },
        )

        if (knowledgeBases.isEmpty()) {
            return CommonRespDto.success(emptyList())
        }

        // 构建返回结果
        val result = knowledgeBases.map { knowledgeBase ->
            var enableScore: Boolean? = false
            var score: Float? = null
            var topK: Int? = null
            var enableRerankModel: Boolean? = null
            var rerankModelId: Int? = null
            var hybridSearchSemanticMatchingWeight: Float? = null
            var hybridSearchKeywordMatchingWeight: Float? = null
            when (knowledgeBase.searchStrategy) {
                KnowledgeBasePo.KnowledgeBaseSearchStrategy.VECTOR -> {
                    enableRerankModel = knowledgeBase.enableVectorSearchRerank
                    rerankModelId = knowledgeBase.vectorSearchRerankModelId
                    topK = knowledgeBase.vectorSearchTopK
                    enableScore = knowledgeBase.enableVectorSearchScore
                    score = knowledgeBase.vectorSearchScore
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.HYBRID -> {
                    enableRerankModel = knowledgeBase.enableHybridSearchRerank
                    rerankModelId = knowledgeBase.hybridSearchRerankModelId
                    topK = knowledgeBase.hybridSearchTopK
                    enableScore = knowledgeBase.enableHybridSearchScore
                    score = knowledgeBase.hybridSearchScore
                    hybridSearchSemanticMatchingWeight =
                        knowledgeBase.hybridSearchSemanticMatchingWeight
                    hybridSearchKeywordMatchingWeight =
                        knowledgeBase.hybridSearchKeywordMatchingWeight
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.FULL_TEXT -> {
                    enableRerankModel = knowledgeBase.enableFullTextSearchRerank
                    rerankModelId = knowledgeBase.fullTextSearchRerankModelId
                    topK = knowledgeBase.fullTextSearchTopK
                    enableScore = knowledgeBase.enableFullTextSearchScore
                    score = knowledgeBase.fullTextSearchScore
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.GRAPH -> {
                    enableRerankModel = knowledgeBase.enableGraphSearchRerank
                    rerankModelId = knowledgeBase.graphSearchRerankModelId
                    topK = knowledgeBase.graphSearchTopK
                }

                else -> {}
            }

            KnowledgeBaseDto.builder().id(knowledgeBase.id).name(knowledgeBase.name)
                .description(knowledgeBase.description)
                .searchStrategy(knowledgeBase.searchStrategy?.let { SearchStrategy.valueOf(it.name) })
                .enableRerankModel(enableRerankModel).rerankModelId(rerankModelId).topK(topK)
                .enableScore(enableScore).score(score)
                .hybridSearchKeywordMatchingWeight(hybridSearchKeywordMatchingWeight)
                .hybridSearchSemanticMatchingWeight(hybridSearchSemanticMatchingWeight)
                .embeddingModelId(knowledgeBase.embeddingModelId)
                .createTime(relations.find { r -> r.knowledgeBaseId == knowledgeBase.id }?.createTime)
                .type(knowledgeBase.type?.let { KnowledgeBaseType.valueOf(it.name) }).build()
        }

        val sorted = result.sortedByDescending { it.createTime }

        return CommonRespDto.success(sorted)
    }

    override fun getKnowledgeBasesByIds(ids: MutableList<Int>): CommonRespDto<List<KnowledgeBaseDto>> {
        // 查询知识库详细信息
        val knowledgeBases = knowledgeBaseMapper.selectList(
            KtQueryWrapper(KnowledgeBasePo()).apply {
                `in`(KnowledgeBasePo::id, ids)
            },
        )

        if (knowledgeBases.isEmpty()) {
            return CommonRespDto.success(emptyList())
        }

        // 构建返回结果
        val result = knowledgeBases.map { knowledgeBase ->
            var enableScore: Boolean? = false
            var score: Float? = null
            var topK: Int? = null
            var enableRerankModel: Boolean? = null
            var rerankModelId: Int? = null
            var hybridSearchSemanticMatchingWeight: Float? = null
            var hybridSearchKeywordMatchingWeight: Float? = null
            when (knowledgeBase.searchStrategy) {
                KnowledgeBasePo.KnowledgeBaseSearchStrategy.VECTOR -> {
                    enableRerankModel = knowledgeBase.enableVectorSearchRerank
                    rerankModelId = knowledgeBase.vectorSearchRerankModelId
                    topK = knowledgeBase.vectorSearchTopK
                    enableScore = knowledgeBase.enableVectorSearchScore
                    score = knowledgeBase.vectorSearchScore
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.HYBRID -> {
                    enableRerankModel = knowledgeBase.enableHybridSearchRerank
                    rerankModelId = knowledgeBase.hybridSearchRerankModelId
                    topK = knowledgeBase.hybridSearchTopK
                    enableScore = knowledgeBase.enableHybridSearchScore
                    score = knowledgeBase.hybridSearchScore
                    hybridSearchSemanticMatchingWeight =
                        knowledgeBase.hybridSearchSemanticMatchingWeight
                    hybridSearchKeywordMatchingWeight =
                        knowledgeBase.hybridSearchKeywordMatchingWeight
                }

                KnowledgeBasePo.KnowledgeBaseSearchStrategy.FULL_TEXT -> {
                    enableRerankModel = knowledgeBase.enableFullTextSearchRerank
                    rerankModelId = knowledgeBase.fullTextSearchRerankModelId
                    topK = knowledgeBase.fullTextSearchTopK
                    enableScore = knowledgeBase.enableFullTextSearchScore
                    score = knowledgeBase.fullTextSearchScore
                }

                else -> {}
            }

            KnowledgeBaseDto.builder().id(knowledgeBase.id).name(knowledgeBase.name)
                .description(knowledgeBase.description)
                .searchStrategy(knowledgeBase.searchStrategy?.let { SearchStrategy.valueOf(it.name) })
                .enableRerankModel(enableRerankModel).rerankModelId(rerankModelId).topK(topK)
                .enableScore(enableScore).score(score)
                .hybridSearchKeywordMatchingWeight(hybridSearchKeywordMatchingWeight)
                .hybridSearchSemanticMatchingWeight(hybridSearchSemanticMatchingWeight)
                .embeddingModelId(knowledgeBase.embeddingModelId).build()
        }

        return CommonRespDto.success(result)
    }

    override fun getKnowledgeBaseDocumentCount(knowledgeBaseId: Int?): CommonRespDto<Long> =
        knowledgeBaseId?.let {
            val count = knowledgeBaseDocumentMapper.selectCount(
                KtQueryWrapper(KnowledgeBaseDocumentPo()).eq(
                    KnowledgeBaseDocumentPo::knowledgeBaseId,
                    knowledgeBaseId,
                ),
            )
            CommonRespDto.success(count)
        } ?: CommonRespDto.error("知识库ID为空")

    private fun bindBuiltInMetadataToDocuments(
        knowledgeBaseId: Int,
        documentIds: List<Int>,
    ) {
        if (documentIds.isEmpty()) {
            return
        }

        try {
            // 获取知识库下的所有元数据
            val metadatas =
                knowledgeBaseMetadataService.getKnowledgeBaseMetadataList(knowledgeBaseId)
            if (!metadatas.isOk || metadatas.data.isNullOrEmpty()) {
                log.info("知识库 $knowledgeBaseId 没有元数据，跳过绑定")
                return
            }

            // 获取知识库下的所有文档ID
            val documents = knowledgeBaseDocumentMapper.selectList(
                KtQueryWrapper(KnowledgeBaseDocumentPo()).select(
                    KnowledgeBaseDocumentPo::id,
                    KnowledgeBaseDocumentPo::updateTime,
                    KnowledgeBaseDocumentPo::name,
                    KnowledgeBaseDocumentPo::createTime,
                    KnowledgeBaseDocumentPo::createBy,
                ).`in`(KnowledgeBaseDocumentPo::id, documentIds),
            )

            // 为每个文档绑定所有内置元数据
            documents.forEach { document ->
                metadatas.data.filter { metadata -> metadata.isBuiltIn }.forEach { metadata ->
                    // 检查是否已经绑定
                    val existingRelation = knowledgeBaseDocumentMetadataMapper.selectOne(
                        KtQueryWrapper(KnowledgeBaseDocumentMetadataPo()).eq(
                            KnowledgeBaseDocumentMetadataPo::metadataId,
                            metadata.id
                        ).eq(KnowledgeBaseDocumentMetadataPo::documentId, document.id),
                    )

                    if (existingRelation == null) {
                        // 创建绑定关系
                        val uploader = backendUserService.getUserInfo(document.createBy).data

                        val relation = KnowledgeBaseDocumentMetadataPo().apply {
                            this.metadataId = metadata.id
                            this.documentId = document.id
                            createTime = LocalDateTime.now()
                            updateTime = LocalDateTime.now()
                            this.name = metadata.name
                            value = when (metadata.name) {
                                BuiltInMetadata.document_name.name -> document.name
                                BuiltInMetadata.last_update_date.name -> document.updateTime?.format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                )

                                BuiltInMetadata.upload_date.name -> document.createTime?.format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                                )

                                BuiltInMetadata.source.name -> "file_upload"
                                BuiltInMetadata.uploader.name -> uploader?.account ?: ""

                                else -> ""
                            }
                        }
                        knowledgeBaseDocumentMetadataMapper.insert(relation)
                        log.debug("绑定元数据 ${metadata.name} 到文档 ${document.id}")
                    }
                }
            }

            log.info("成功绑定元数据到文档，知识库ID：$knowledgeBaseId，文档数量：${documentIds.size}，元数据数量：${metadatas.data.size}")
        } catch (e: Exception) {
            log.error("绑定元数据到文档失败，知识库ID：$knowledgeBaseId", e)
        }
    }

    override fun getRetrievalTestRecordsByKnowledgeBaseId(knowledgeBaseId: Int?): CommonRespDto<List<RetrievalTestRecordDto>> {
        // 参数验证
        if (knowledgeBaseId == null) {
            return CommonRespDto.error("知识库ID不能为空")
        }

        try {
            // 检查文档是否存在
            val knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId)
                ?: return CommonRespDto.error("文档不存在")

            // 查询检索测试记录，按时间倒序排列
            val records = retrievalTestRecordMapper.selectList(
                KtQueryWrapper(RetrievalTestRecordPo()).eq(
                    RetrievalTestRecordPo::knowledgeBaseId,
                    knowledgeBaseId
                ).orderByDesc(RetrievalTestRecordPo::createTime),
            )

            // 转换为DTO
            val recordDtos = records.map { record ->
                RetrievalTestRecordDto.builder().id(record.id)
                    .knowledgeBaseId(record.knowledgeBaseId).query(record.query)
                    .createTime(record.createTime).build()
            }

            log.info("成功获取知识库 $knowledgeBaseId 的检索测试记录，共 ${recordDtos.size} 条记录")
            return CommonRespDto.success(recordDtos)
        } catch (e: Exception) {
            log.error("获取检索测试记录失败，知识库ID: $knowledgeBaseId", e)
            return CommonRespDto.error("获取检索测试记录失败：${e.message}")
        }
    }

    override fun getKnowledgeBaseNo(userId: Int?): Long {
        // 数据权限
        val userIdList = backendUserService.getUserIdsByUserId(userId).data
        // 构建基础查询条件
        val qw = KtQueryWrapper(KnowledgeBasePo()).apply {
            `in`(KnowledgeBasePo::createBy, userIdList)
        }
        val count = knowledgeBaseMapper.selectCount(qw);
        return count;
    }

    override fun saveRetrievalTestRecord(
        knowledgeBaseId: Int,
        query: String,
    ): CommonRespDto<Void> {
        try {
            val record = RetrievalTestRecordPo().apply {
                this.knowledgeBaseId = knowledgeBaseId
                this.query = query
                this.createTime = LocalDateTime.now()
            }
            retrievalTestRecordMapper.insert(record)
            return CommonRespDto.success()
        } catch (e: Exception) {
            log.error("保存检索测试记录失败", e)
            return CommonRespDto.error("保存检索测试记录失败: ${e.message}")
        }
    }
}

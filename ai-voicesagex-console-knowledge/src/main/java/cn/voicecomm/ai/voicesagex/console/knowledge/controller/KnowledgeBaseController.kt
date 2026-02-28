package cn.voicecomm.ai.voicesagex.console.knowledge.controller

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseDocumentService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.*
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.KnowledgeBaseType
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.*
import cn.voicecomm.ai.voicesagex.console.util.vo.Result
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.dubbo.config.annotation.DubboReference
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 知识库
 */
@RestController
@RequestMapping("/knowledge-base")
@Validated
class KnowledgeBaseController(
        private val knowledgeBaseService: KnowledgeBaseService,
        private val knowledgeBaseDocumentService: KnowledgeBaseDocumentService,
        @Value("\${file.upload}") val uploadDir: String,
        @Value("\${pyServer}") val pyServer: String,
) {
    val log: Logger = LoggerFactory.getLogger(KnowledgeBaseController::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val client =
            OkHttpClient
                    .Builder()
                    .build()
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    companion object {
        private val SUPPORTED_FILE_TYPES =
                setOf(
                        "txt",
                        "markdown",
                        "mdx",
                        "pdf",
                        "html",
                        "xlsx",
                        "xls",
                        "docx",
                        "csv",
                        "md",
                        "htm",
                )
        private const val MAX_FILE_SIZE = 15 * 1024 * 1024 // 15MB
    }

    @DubboReference
    lateinit var modelService: ModelService

    /**
     * 创建空知识库（包含传统知识库和图知识库）
     */
    @PostMapping("/create-empty-base")
    fun createEmptyKnowledgeBase(
            @RequestBody @Validated body: CreateEmptyKnowledgeBaseVo,
    ): Result<Void> {
        val knowledgeBaseType = body.knowledgeBaseType ?: KnowledgeBaseType.TRAD
        knowledgeBaseService.createEmptyKnowledgeBase(
                body.name,
                body.description,
                knowledgeBaseType
        ).let { resp ->
            return when {
                resp.isOk -> Result.success()
                else -> Result.error(resp.msg)
            }
        }
    }

    /**
     * 删除知识库
     * @param id 知识库id
     */
    @DeleteMapping("/delete/{id}")
    fun deleteKnowledgeBase(
            @PathVariable(name = "id") id: Int,
    ): Result<String> {
        val resp = knowledgeBaseService.deleteKnowledgeBase(id)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 编辑知识库名称和描述
     */
    @PostMapping("/edit-name-and-desc")
    fun editKnowledgeBaseNameAndDesc(
            @RequestBody @Validated body: EditKnowledgeBaseNameAndDescVo,
    ): Result<Void> {
        val resp =
                knowledgeBaseService.editKnowledgeBaseNameAndDesc(body.id, body.name, body.description)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 更新知识库设置
     */
    @PostMapping("/update-setting")
    fun updateSetting(
            @RequestBody body: UpdateKnowledgeBaseSettingDto,
    ): Result<Void> {
        val resp =
                knowledgeBaseService.updateBaseSetting(body)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 上传文档
     * @return 上传后的文件路径列表
     */
    @PostMapping("/upload-documents")
    fun uploadDocuments(body: UploadDocsReqVo): Result<List<UploadDocumentsRespVo>> {
        val files = body.files
        // 验证文件数量
        if (files.isEmpty() || files.size > 5) {
            return Result.error("文件数量必须在1-5个之间")
        }

        val duplicateFileNameCount = mutableMapOf<String, Int>()
        // 验证文件类型和大小
        files.forEach { file ->
            val fileType =
                    file.originalFilename?.substringAfterLast('.')?.lowercase()
                            ?: return Result.error("文件名不能为空")

            if (!SUPPORTED_FILE_TYPES.contains(fileType)) {
                return Result.error("不支持的文件类型: $fileType")
            }

            if (file.size > MAX_FILE_SIZE) {
                return Result.error("文件 ${file.originalFilename} 超过15MB限制")
            }

            file.originalFilename?.also {
                duplicateFileNameCount[it] = (duplicateFileNameCount[it] ?: 0) + 1
            }
        }

        // 校验重名
        val isDuplicate = duplicateFileNameCount.values.any { count -> count > 1 }
        if (isDuplicate) {
            return Result.error("文件重名")
        }

        // 创建上传目录
        val uploadPath = Paths.get(uploadDir, "knowledge-base", "documents").toFile()
        if (!uploadPath.exists()) {
            uploadPath.mkdirs()
        }

        // 保存文件并返回文件路径
        val results =
                files.map { file ->
                    val fileType = file.originalFilename?.substringAfterLast('.') ?: ""
                    val pureName = file.originalFilename?.substringBeforeLast('.') ?: ""
                    val filename = if (pureName.length > 100) pureName.substring(0, 100) else pureName
                    val uniqueName = "${UUID.randomUUID()}.$fileType"
                    val destFile = File(uploadPath, uniqueName)
                    file.transferTo(destFile)

                    val id =
                            knowledgeBaseDocumentService.uploadDoc(
                                    UploadDocDto().apply {
                                        this.name = "$filename.$fileType"
                                        this.uniqueName = uniqueName
                                    },
                            )

                    UploadDocumentsRespVo(file.originalFilename, id)
                }

        return Result.success(results)
    }

    /**
     * 保存并处理知识库
     */
    @PostMapping("/save-and-process")
    fun saveAndProcess(
            @RequestBody body: SaveAndProcessBaseVo,
    ): Result<Int> {
        val dto =
                SaveAndProcessBaseDto
                        .builder()
                        .knowledgeBaseId(body.knowledgeBaseId)
                        .chunkingStrategy(body.chunkingStrategy)
                        .enableMultimodal(body.enableMultimodal)
                        .embeddingModelId(body.embeddingModelId)
                        .topK(body.topK)
                        .hybridSearchKeywordMatchingWeight(body.hybridSearchKeywordMatchingWeight)
                        .hybridSearchSemanticMatchingWeight(body.hybridSearchSemanticMatchingWeight)
                        .name(body.name)
                        .searchStrategy(body.searchStrategy.name)
                        .rerankModelId(body.rerankModelId)
                        .enableRerank(body.enableRerank)
                        .enableScore(body.enableScore)
                        .score(body.score)
                        .type(body.type.name)
                        .documentIds(body.documentIds)
                        .previewParams(body.previewParams)
                        .build()
        val resp = knowledgeBaseService.saveAndProcess(dto)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 保存并处理已存在知识库
     */
    @PostMapping("/save-and-process-exist")
    fun saveAndProcessExistBase(
            @RequestBody body: SaveAndProcessExistBaseVo,
    ): Result<Void> {
        val dto =
                SaveAndProcessExistBaseDto
                        .builder()
                        .chunkingStrategy(body.chunkingStrategy)
                        .knowledgeBaseId(body.knowledgeBaseId)
                        .documentIds(body.documentIds)
                        .previewParams(body.previewParams)
                        .build()
        val resp = knowledgeBaseService.saveAndProcessExistKnowledgeBase(dto)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 获取知识库列表
     */
    @PostMapping("/list")
    fun list(
            @RequestBody body: ListKnowledgeBaseVo,
    ): Result<List<KnowledgeBaseDto>> {
        val resp = knowledgeBaseService.list(body.tagIds, body.name, body.type)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 预览文档普通分块
     */
    @PostMapping("/preview-common-chunks")
    fun previewCommonChunks(
            @RequestBody @Validated body: PreviewCommonChunksReqVo,
    ): Result<CommonChunkPreviewRespDataDto> {
        val dto = body.toDto()
        val resp = knowledgeBaseService.previewCommonChunks(dto)
        return when {
            resp.isOk -> Result.success(resp.data, resp.msg)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 预览文档父子分块
     */
    @PostMapping("/preview-parent-child-chunks")
    fun previewParentChildChunks(
            @RequestBody @Validated body: PreviewParentChildChunksReqVo,
    ): Result<ParentChildChunkPreviewRespDataDto> {
        val dto = body.toDto()
        val resp = knowledgeBaseService.previewParentChildChunks(dto)
        return when {
            resp.isOk -> Result.success(resp.data, resp.msg)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 同步智能体应用知识库关联关系
     * 将应用的知识库关联关系同步为目标状态：
     * - 添加knowledgeBaseIds中未绑定的知识库
     * - 移除当前绑定但不在knowledgeBaseIds中的知识库
     * - 如果knowledgeBaseIds为空或null，则移除所有关联关系
     */
    @PostMapping("/add-knowledge-base-to-application")
    fun addKnowledgeBaseToApplication(
            @RequestBody body: AddApplicationKnowledgeBaseReqVo,
    ): Result<Void> {
        val resp =
                knowledgeBaseService.addKnowledgeBasesToApplication(
                        body.knowledgeBaseIds,
                        body.applicationId,
                )
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 移除智能体应用知识库
     */
    @PostMapping("/remove-knowledge-base-from-application")
    fun removeKnowledgeBaseFromApplication(
            @RequestBody body: RemoveApplicationKnowledgeBaseReqVo,
    ): Result<Void> {
        val resp =
                knowledgeBaseService.removeKnowledgeBaseFromApplication(
                        body.knowledgeBaseId,
                        body.applicationId,
                )
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 获取知识库详情
     */
    @PostMapping("/detail/{id}")
    fun getKnowledgeBaseDetail(
            @PathVariable(name = "id") id: Int,
    ): Result<KnowledgeBaseDetailDto> {
        val resp = knowledgeBaseService.getKnowledgeBaseDetail(id)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 获取应用已绑定的知识库列表
     */
    @PostMapping("/application/{applicationId}/knowledge-bases")
    fun getApplicationKnowledgeBases(
            @PathVariable(name = "applicationId") applicationId: Int,
    ): Result<List<KnowledgeBaseDto>> {
        val resp = knowledgeBaseService.getApplicationKnowledgeBases(applicationId)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 批量更新文档状态
     * 支持将文档状态切换为：ENABLE（启用）、DISABLE（禁用）、ARCHIVE（归档）
     * 文档状态的初始值为null
     */
    @PostMapping("/update-document-status")
    fun updateDocumentStatus(
            @RequestBody @Validated body: UpdateDocumentStatusVo,
    ): Result<Void> {
        val dto =
                UpdateDocumentStatusDto().apply {
                    documentIds = body.documentIds
                    status = body.status
                    isArchived = body.isArchived
                }
        val resp = knowledgeBaseDocumentService.updateDocumentStatus(dto)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 根据知识库ID获取文档列表
     * @param knowledgeBaseId 知识库ID
     * @return 文档列表（省略chunks和previewChunks字段）
     */
    @PostMapping("/{knowledgeBaseId}/documents")
    fun getKnowledgeBaseDocuments(
            @PathVariable(name = "knowledgeBaseId") knowledgeBaseId: Int,
            @RequestBody body: GetBaseDocumentsReqVo,
    ): Result<List<KnowledgeBaseDocumentDto>> {
        val resp =
                knowledgeBaseDocumentService.getKnowledgeBaseDocuments(
                        knowledgeBaseId,
                        body.name,
                        body.status,
                )
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 根据文档ID获取文档详情
     * @param documentId 文档ID
     * @return 文档详情（包含所有字段）
     */
    @PostMapping("/document/{documentId}")
    fun getDocumentById(
            @PathVariable(name = "documentId") documentId: Int,
    ): Result<KnowledgeBaseDocumentDto> {
        val resp = knowledgeBaseDocumentService.getDocumentById(documentId)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 使用SSE实时推送知识库下所有文档的处理状态
     * @param knowledgeBaseId 知识库ID
     * @return SSE流
     */
    @PostMapping("/{knowledgeBaseId}/documents/status/stream")
    fun streamKnowledgeBaseDocumentsStatus(
            @PathVariable(name = "knowledgeBaseId") knowledgeBaseId: Int,
    ): SseEmitter {
        val emitter = SseEmitter(0L) // 无超时时间
        val isComplete = AtomicBoolean(false)

        val startTime = System.currentTimeMillis()
        val maxDuration = 20 * 60 * 1000L // 20分钟

        executor.execute {
            try {
                while (!isComplete.get()) {

                    // 时间兜底
                    if (System.currentTimeMillis() - startTime > maxDuration) {
                        log.warn("SSE达到最大时长，知识库ID: {}", knowledgeBaseId)
                        isComplete.set(true)
                        emitter.complete()
                        break
                    }

                    val currentDocumentsStatus =
                            knowledgeBaseDocumentService.getKnowledgeBaseDocumentsStatus(
                                    knowledgeBaseId,
                            )

//                        log.info("知识库${knowledgeBaseId}下文档处理状态$currentDocumentsStatus")

                    emitter.send(
                            SseEmitter
                                    .event()
                                    .name("process_status")
                                    .data(
                                            mapOf(
                                                    "documentStatus" to currentDocumentsStatus,
                                            ),
                                    ),
                    )

                    if (currentDocumentsStatus.isNotEmpty() &&
                            currentDocumentsStatus.values.all { "SUCCESS" == it.status }
                    ) {
                        log.info("知识库{} 下所有文档处理完成，关闭 SSE", knowledgeBaseId)
                        isComplete.set(true)
                        emitter.complete()
                        break
                    }

                    // 等待1秒后再次查询
                    TimeUnit.SECONDS.sleep(1)
                }
            } catch (e: Exception) {
                if (!isComplete.get()) {
                    log.error("SSE处理异常，知识库ID: {}", knowledgeBaseId, e)
                    isComplete.set(true)
                    emitter.completeWithError(e)
                }
            }
        }

        // 设置SSE连接关闭时的回调
        emitter.onCompletion {
            log.info("SSE连接完成，知识库ID: $knowledgeBaseId")
            isComplete.set(true)
        }

        emitter.onTimeout {
            log.warn("SSE连接超时，知识库ID: $knowledgeBaseId")
            isComplete.set(true)
            emitter.complete()
        }

        emitter.onError { throwable ->
            log.error("SSE连接错误，知识库ID: $knowledgeBaseId", throwable)
            isComplete.set(true)
            emitter.complete()
        }

        return emitter
    }

    /**
     * 获取知识库下文档数量
     */
    @GetMapping("/{knowledgeBaseId}/documents/count")
    fun getDocumentCount(
            @PathVariable knowledgeBaseId: Int,
    ): Result<Long> {
        val resp = knowledgeBaseService.getKnowledgeBaseDocumentCount(knowledgeBaseId)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 批量删除文档
     */
    @PostMapping("/delete/documents")
    fun deleteDocuments(
            @RequestBody body: DeleteDocumentsReqVo,
    ): Result<Void> {
        val resp = knowledgeBaseDocumentService.deleteDocuments(body.ids)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 检索测试
     */
    @PostMapping("/retrieval-test")
    fun retrievalTest(
            @RequestBody @Validated req: RetrievalTestReqVo,
    ): Result<RetrievalTestRespDataVo> {
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(req.knowledgeBaseId).data
        val embeddingModelResp = modelService.getInfo(knowledgeBase.embeddingModelId)
        val embeddingModel = embeddingModelResp.data

        if (!embeddingModelResp.isOk || Objects.isNull(embeddingModel) || false == embeddingModel.isShelf) {
            return Result.error("Embedding模型已经下架")
        }

        val embeddingModelInstanceConfig =
                mapOf(
                        "model_name" to embeddingModel.internalName,
                        "base_url" to embeddingModel.url,
                        "is_support_vision" to embeddingModel.isSupportVisual,
                        "context_length" to embeddingModel.contextLength,
                        "max_token_length" to embeddingModel.tokenMax,
                        "is_support_function" to embeddingModel.isSupportFunction,
                )

        var rerankModelInstanceConfig: Map<String, Any>? = null
        var rerankModelInstanceProvider: String? = null

        if (req.enableRerank) {
            val rerankModelResp = modelService.getInfo(req.rerankModelId)
            val rerankModel = rerankModelResp.data

            if (!rerankModelResp.isOk || Objects.isNull(rerankModel) || false == rerankModel.isShelf) {
                return Result.error("Rerank模型已经下架")
            }

            rerankModelInstanceProvider = rerankModel.loadingMode
            rerankModelInstanceConfig =
                    mapOf(
                            "model_name" to rerankModel.internalName,
                            "base_url" to rerankModel.url,
                            "is_support_vision" to rerankModel.isSupportVisual,
                            "context_length" to rerankModel.contextLength,
                            "max_token_length" to rerankModel.tokenMax,
                            "is_support_function" to rerankModel.isSupportFunction,
                    )
        }

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/Retrieve".toHttpUrl()
            val body =
                    mutableMapOf(
                            "query" to req.query,
                            "knowledge_base_config" to
                                    mapOf(
                                            "knowledge_base_id" to knowledgeBase.id,
                                            "knowledge_base_description" to knowledgeBase.description,
                                            "knowledge_base_retrieve_type" to req.searchStrategy.name,
                                            "knowledge_base_retrieve_config" to
                                                    mapOf(
                                                            "embedding_model_instance_provider" to embeddingModel.loadingMode,
                                                            "embedding_model_instance_config" to embeddingModelInstanceConfig,
                                                            "top_k" to req.topK,
                                                            "score_threshold" to if (req.enableScore) req.score else null,
                                                            "is_rerank" to req.enableRerank,
                                                            "rerank_model_instance_provider" to rerankModelInstanceProvider,
                                                            "rerank_model_instance_config" to rerankModelInstanceConfig,
                                                            "hybrid_rerank_type" to if (req.enableRerank) "MODEL" else "WEIGHT",
                                                            "hybrid_semantic_weight" to req.hybridSearchSemanticMatchingWeight,
                                                            "hybrid_keyword_weight" to req.hybridSearchKeywordMatchingWeight,
                                                    ),
                                    ),
                    )
            val json = objectMapper.writeValueAsString(body)
            client
                    .newCall(
                            Request
                                    .Builder()
                                    .url(url)
                                    .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
                                    .build(),
                    ).execute()
                    .use { response ->
                        if (!response.isSuccessful) {
                            log.error("检索测试失败：${response.message}")
                            return Result.error("检索测试失败")
                        }

                        return response.body?.string()?.let { body ->
                            try {
                                objectMapper.configure(
                                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                        false
                                )
                                val jsonNode = objectMapper.readTree(body)
                                log.info("检索响应body：$body")
                                val resp =
                                        objectMapper.treeToValue(jsonNode, RetrievalTestRespVo::class.java)
                                if (resp != null) {
                                    if (resp.code == 1000) {
                                        // 保存检索测试记录
                                        try {
                                            knowledgeBaseService.saveRetrievalTestRecord(
                                                    req.knowledgeBaseId,
                                                    req.query,
                                            )
                                        } catch (e: Exception) {
                                            log.warn("保存检索测试记录失败，但不影响检索测试结果", e)
                                        }
                                        Result.success(resp.data, "检索测试成功")
                                    } else {
                                        Result.error(resp.msg)
                                    }
                                } else {
                                    Result.error("检索测试无响应")
                                }
                            } catch (e: Exception) {
                                log.error("检索测试结果解析失败", e)
                                Result.error("检索测试失败")
                            }
                        } ?: Result.error("检索测试失败，无检索结果")
                    }
        } catch (e: Exception) {
            log.error("检索请求失败，知识库ID：${req.knowledgeBaseId}")
            return Result.error("检索请求失败")
        }
    }

    /**
     * 根据知识库ID获取所有检索测试记录，按时间倒序排列
     */
    @GetMapping("/{knowledge-base-id}/retrieval-test-records")
    fun getRetrievalTestRecords(
            @PathVariable(name = "knowledge-base-id") knowledgeBaseId: Int,
    ): Result<List<RetrievalTestRecordDto>> {
        val resp = knowledgeBaseService.getRetrievalTestRecordsByKnowledgeBaseId(knowledgeBaseId)
        return when {
            resp.isOk -> {
                Result.success(resp.data, "获取检索测试记录成功")
            }

            else -> Result.error(resp.msg)
        }
    }
}

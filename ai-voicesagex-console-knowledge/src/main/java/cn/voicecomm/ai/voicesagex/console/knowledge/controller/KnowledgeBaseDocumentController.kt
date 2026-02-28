package cn.voicecomm.ai.voicesagex.console.knowledge.controller

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseDocumentService
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.PreviewChunksDto
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.*
import cn.voicecomm.ai.voicesagex.console.util.vo.Result
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
import java.util.*

/**
 * 文档
 */
@RestController
@RequestMapping("/knowledge-base-document")
@Validated
class KnowledgeBaseDocumentController(
    private val knowledgeBaseDocumentService: KnowledgeBaseDocumentService,
    private val knowledgeBaseService: KnowledgeBaseService,
    @Value("\${pyServer}") val pyServer: String,
) {
    private val log: Logger = LoggerFactory.getLogger(KnowledgeBaseDocumentController::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val client =
        OkHttpClient
            .Builder()
            .build()

    @DubboReference
    lateinit var modelService: ModelService

    /**
     * 批量启用文档分段
     */
    @PutMapping("/{document-id}/chunks/enable")
    fun enableChunks(
        @PathVariable(name = "document-id") documentId: Int,
        @RequestBody chunkIds: List<Int>,
    ): Result<Void> {
        val resp =
            knowledgeBaseDocumentService.enableChunks(documentId, chunkIds)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 批量禁用文档分段
     */
    @PutMapping("/{document-id}/chunks/disable")
    fun disableChunks(
        @PathVariable(name = "document-id") documentId: Int,
        @RequestBody chunkIds: List<Int>,
    ): Result<Void> {
        val resp =
            knowledgeBaseDocumentService.disableChunks(documentId, chunkIds)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 批量删除文档分段
     */
    @PutMapping("/{document-id}/chunks/delete")
    fun deleteChunks(
        @PathVariable(name = "document-id") documentId: Int,
        @RequestBody chunkIds: List<Int>,
    ): Result<Void> {
        val resp =
            knowledgeBaseDocumentService.deleteChunks(documentId, chunkIds)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 保存并处理
     */
    @PostMapping("/save-and-process")
    fun saveAndProcess(
        @RequestBody body: PreviewChunksDto,
    ): Result<Void> {
        val resp =
            knowledgeBaseDocumentService.saveAndProcess(body)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 添加普通分段
     */
    @PostMapping("/normal/chunk")
    fun addNormalChunk(
        @RequestBody req: AddNormalChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/AddChunkNormal".toHttpUrl()
            val body =
                mutableMapOf(
                    "knowledge_base_id" to knowledgeBase.id,
                    "document_id" to req.documentId,
                    "chunk_content" to req.chunkContent,
                    "chunk_status" to document.status,
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("添加普通分段失败：${response.message}")
                        return Result.error("添加普通分段失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("添加普通分段成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("添加普通分段失败", e)
                            Result.error("添加普通分段失败")
                        }
                    } ?: Result.error("添加普通分段失败")
                }
        } catch (e: Exception) {
            log.error("添加普通分段失败，文档ID：${req.documentId}")
            return Result.error("添加普通分段失败")
        }
    }

    /**
     * 添加普通QA分段
     */
    @PostMapping("/normal/qa-chunk")
    fun addNormalQaChunk(
        @RequestBody req: AddNormalQaChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/AddChunkNormalQA".toHttpUrl()
            val body =
                mutableMapOf(
                    "knowledge_base_id" to knowledgeBase.id,
                    "document_id" to req.documentId,
                    "chunk_question" to req.chunkQuestion,
                    "chunk_answer" to req.chunkAnswer,
                    "chunk_status" to document.status,
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("添加普通QA分段失败：${response.message}")
                        return Result.error("添加普通QA分段失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("添加成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("添加普通QA分段失败", e)
                            Result.error("添加普通QA分段失败")
                        }
                    } ?: Result.error("添加普通QA分段失败")
                }
        } catch (e: Exception) {
            log.error("添加普通QA分段失败，文档ID：${req.documentId}")
            return Result.error("添加普通QA分段失败")
        }
    }

    /**
     * 添加高级分段父分段
     */
    @PostMapping("/advanced/parent-chunk")
    fun addAdvancedParentChunk(
        @RequestBody req: AddAdvancedParentChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/AddChunkAdvancedParent".toHttpUrl()
            val body =
                mutableMapOf(
                    "knowledge_base_id" to knowledgeBase.id,
                    "document_id" to req.documentId,
                    "chunk_content" to req.chunkContent,
                    "chunk_status" to document.status,
                    "sonchunk_setting" to
                        mapOf("chunk_identifier" to knowledgeBase.chunkIdentifier, "chunk_size" to knowledgeBase.chunkMaxLength),
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("添加高级分段父分段失败：${response.message}")
                        return Result.error("添加失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("添加成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("添加高级分段父分段失败", e)
                            Result.error("添加失败")
                        }
                    } ?: Result.error("添加失败")
                }
        } catch (e: Exception) {
            log.error("添加高级分段父分段失败，文档ID：${req.documentId}")
            return Result.error("添加失败")
        }
    }

    /**
     * 添加高级分段子分段
     */
    @PostMapping("/advanced/child-chunk")
    fun addAdvancedChildChunk(
        @RequestBody req: AddAdvancedChildChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/AddChunkAdvancedChild".toHttpUrl()
            val body =
                mutableMapOf(
                    "knowledge_base_id" to knowledgeBase.id,
                    "document_id" to req.documentId,
                    "parent_idx" to req.parentIdx,
                    "chunk_content" to req.chunkContent,
                    "chunk_status" to document.status,
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("添加高级分段子分段失败：${response.message}")
                        return Result.error("添加失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("添加成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("添加高级分段子分段失败", e)
                            Result.error("添加失败")
                        }
                    } ?: Result.error("添加失败")
                }
        } catch (e: Exception) {
            log.error("添加高级分段子分段失败，文档ID：${req.documentId}")
            return Result.error("添加失败")
        }
    }

    /**
     * 删除普通分段
     */
    @DeleteMapping("/normal/chunk")
    fun deleteNormalChunk(
        @RequestBody req: DeleteNormalChunkReqVo,
    ): Result<Void> {
        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/DelChunkNormal".toHttpUrl()
            val body =
                mutableMapOf(
                    "document_id" to req.documentId,
                    "chunk_id" to req.chunkId,
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
                        log.error("删除普通分段失败：${response.message}")
                        return Result.error("删除失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("删除成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("删除普通分段失败", e)
                            Result.error("删除失败")
                        }
                    } ?: Result.error("删除失败")
                }
        } catch (e: Exception) {
            log.error("删除普通分段失败，文档ID：${req.documentId}")
            return Result.error("删除失败")
        }
    }

    /**
     * 删除高级分段父分段
     */
    @DeleteMapping("/advanced/parent-chunk")
    fun deleteAdvancedParentChunk(
        @RequestBody req: DeleteAdvancedParentChunkReqVo,
    ): Result<Void> {
        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/DelChunkAdvancedParent".toHttpUrl()
            val body =
                mutableMapOf(
                    "document_id" to req.documentId,
                    "parent_idx" to req.parentIdx,
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
                        log.error("删除高级分段父分段失败：${response.message}")
                        return Result.error("删除失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("删除成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("删除高级分段父分段失败", e)
                            Result.error("删除失败")
                        }
                    } ?: Result.error("删除失败")
                }
        } catch (e: Exception) {
            log.error("删除高级分段父分段失败，文档ID：${req.documentId}")
            return Result.error("删除失败")
        }
    }

    /**
     * 删除高级分段子分段
     */
    @DeleteMapping("/advanced/child-chunk")
    fun deleteAdvancedChildChunk(
        @RequestBody req: DeleteAdvancedChildChunkReqVo,
    ): Result<Void> {
        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/DelChunkAdvancedChild".toHttpUrl()
            val body =
                mutableMapOf(
                    "document_id" to req.documentId,
                    "parent_idx" to req.parentIdx,
                    "child_chunk_id" to req.childChunkIdx,
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
                        log.error("删除高级分段子分段失败：${response.message}")
                        return Result.error("删除失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("删除成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("删除高级分段子分段失败", e)
                            Result.error("删除失败")
                        }
                    } ?: Result.error("删除失败")
                }
        } catch (e: Exception) {
            log.error("删除高级分段子分段失败，文档ID：${req.documentId}")
            return Result.error("删除失败")
        }
    }

    /**
     * 修改普通分段
     */
    @PutMapping("/normal/chunk")
    fun editNormalChunk(
        @RequestBody req: EditNormalChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ModifyChunkNormal".toHttpUrl()
            val body =
                mutableMapOf(
                    "chunk_id" to req.chunkId,
                    "document_id" to req.documentId,
                    "chunk_content" to req.chunkContent,
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("修改普通分段失败：${response.message}")
                        return Result.error("修改普通分段失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("修改成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("修改普通分段失败", e)
                            Result.error("修改普通分段失败")
                        }
                    } ?: Result.error("修改普通分段失败")
                }
        } catch (e: Exception) {
            log.error("修改普通分段失败，文档ID：${req.documentId}")
            return Result.error("修改普通分段失败")
        }
    }

    /**
     * 修改普通QA分段
     */
    @PutMapping("/normal/qa-chunk")
    fun editNormalQaChunk(
        @RequestBody req: EditNormalQaChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ModifyChunkNormalQA".toHttpUrl()
            val body =
                mutableMapOf(
                    "chunk_id" to req.chunkId,
                    "document_id" to req.documentId,
                    "chunk_answer" to req.chunkAnswer,
                    "chunk_question" to req.chunkQuestion,
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("修改普通QA分段失败：${response.message}")
                        return Result.error("修改普通QA分段失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("修改成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("修改普通QA分段失败", e)
                            Result.error("修改普通QA分段失败")
                        }
                    } ?: Result.error("修改普通QA分段失败")
                }
        } catch (e: Exception) {
            log.error("修改普通QA分段失败，文档ID：${req.documentId}")
            return Result.error("修改普通QA分段失败")
        }
    }

    /**
     * 修改高级分段父分段
     */
    @PutMapping("/advanced/parent-chunk")
    fun editAdvancedParentChunk(
        @RequestBody req: EditAdvancedParentChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ModifyChunkAdvancedParent".toHttpUrl()
            val body =
                mutableMapOf(
                    "knowledge_base_id" to knowledgeBase.id,
                    "parent_idx" to req.parentIdx,
                    "document_id" to req.documentId,
                    "chunk_content" to req.chunkContent,
                    "sonchunk_setting" to
                        mapOf("chunk_identifier" to knowledgeBase.chunkIdentifier, "chunk_size" to knowledgeBase.chunkMaxLength),
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("修改高级分段父分段失败：${response.message}")
                        return Result.error("修改高级分段父分段失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("修改成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("修改高级分段父分段失败", e)
                            Result.error("修改高级分段父分段失败")
                        }
                    } ?: Result.error("修改高级分段父分段失败")
                }
        } catch (e: Exception) {
            log.error("修改高级分段父分段失败，文档ID：${req.documentId}")
            return Result.error("修改高级分段父分段失败")
        }
    }

    /**
     * 修改高级分段子分段
     */
    @PutMapping("/advanced/child-chunk")
    fun editAdvancedChildChunk(
        @RequestBody req: EditAdvancedChildChunkReqVo,
    ): Result<Void> {
        val document = knowledgeBaseDocumentService.getDocumentById(req.documentId).data
        val knowledgeBase = knowledgeBaseService.getKnowledgeBaseDetail(document.knowledgeBaseId).data
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

        try {
            val url = "$pyServer/Voicecomm/VoiceSageX/Rag/ModifyChunkAdvancedChild".toHttpUrl()
            val body =
                mutableMapOf(
                    "parent_idx" to req.parentIdx,
                    "document_id" to req.documentId,
                    "chunk_content" to req.chunkContent,
                    "child_chunk_id" to req.childChunkIdx,
                    "sonchunk_setting" to
                        mapOf("chunk_identifier" to knowledgeBase.chunkIdentifier, "chunk_size" to knowledgeBase.chunkMaxLength),
                    "model_instance_provider" to embeddingModel.loadingMode,
                    "model_instance_config" to embeddingModelInstanceConfig,
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
                        log.error("修改高级分段子分段失败：${response.message}")
                        return Result.error("修改高级分段子分段失败")
                    }

                    return response.body?.string()?.let { body ->
                        try {
                            val jsonNode = objectMapper.readTree(body)
                            val code = jsonNode.get("code").asInt()
                            if (code == 1000) {
                                Result.successMsg("修改成功")
                            } else {
                                Result.error(jsonNode.get("msg").toString())
                            }
                        } catch (e: Exception) {
                            log.error("修改高级分段子分段失败", e)
                            Result.error("修改高级分段子分段失败")
                        }
                    } ?: Result.error("修改高级分段子分段失败")
                }
        } catch (e: Exception) {
            log.error("修改高级分段子分段失败，文档ID：${req.documentId}")
            return Result.error("修改高级分段子分段失败")
        }
    }
}

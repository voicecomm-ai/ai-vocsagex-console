package cn.voicecomm.ai.voicesagex.console.knowledge.controller

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseMetadataService
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataBaseDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataDto
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataWithValueDto
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.AddMetadataReqVo
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.DeleteMetadataReqVo
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.EditMetadataReqVo
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.EditMetadataValueReqVo
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.GetMetadataByDocumentIdsReqVo
import cn.voicecomm.ai.voicesagex.console.util.vo.Result
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 元数据
 */
@RestController
@RequestMapping("/knowledge-base-metadata")
@Validated
class KnowledgeBaseMetadataController(
    private val knowledgeBaseMetadataService: KnowledgeBaseMetadataService,
) {
    /**
     * 启用内置元数据
     */
    @PutMapping("/{knowledgebase-id}/built-in/enable")
    fun enableBuiltInMetadata(
        @PathVariable(name = "knowledgebase-id") knowledgeBaseId: Int,
    ): Result<Void> {
        val resp = knowledgeBaseMetadataService.enableBuiltInMetadata(knowledgeBaseId)

        return when {
            resp.isOk ->
                Result.success()
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 禁用内置元数据
     */
    @PutMapping("/{knowledgebase-id}/built-in/disable")
    fun disableBuiltInMetadata(
        @PathVariable(name = "knowledgebase-id") knowledgeBaseId: Int,
    ): Result<Void> {
        val resp = knowledgeBaseMetadataService.disableBuiltInMetadata(knowledgeBaseId)

        return when {
            resp.isOk ->
                Result.success()
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 新增元数据
     */
    @PostMapping
    fun addMetadata(
        @RequestBody body: AddMetadataReqVo,
    ): Result<Int> {
        val resp = knowledgeBaseMetadataService.addMetadata(body.knowledgeBaseId, body.name, body.type)

        return when {
            resp.isOk ->
                Result.success(resp.data)
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 编辑元数据
     */
    @PostMapping("/edit")
    fun editMetadata(
        @RequestBody body: EditMetadataReqVo,
    ): Result<Void> {
        val resp = knowledgeBaseMetadataService.editMetadata(body.id, body.name)

        return when {
            resp.isOk ->
                Result.success()
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 删除元数据
     */
    @PostMapping("/delete")
    fun deleteMetadata(
        @RequestBody body: DeleteMetadataReqVo,
    ): Result<Void> {
        val resp = knowledgeBaseMetadataService.deleteMetadata(body.id)

        return when {
            resp.isOk ->
                Result.success()
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 获取知识库元数据列表
     */
    @GetMapping("/{knowledgebase-id}")
    fun getKnowledgeBaseMetadataList(
        @PathVariable(name = "knowledgebase-id") knowledgeBaseId: Int,
    ): Result<List<KnowledgeBaseMetadataDto>> {
        val resp = knowledgeBaseMetadataService.getKnowledgeBaseMetadataList(knowledgeBaseId)

        return when {
            resp.isOk ->
                Result.success(resp.data)
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 根据文档ID列表获取元数据列表
     */
    @PostMapping("/by-document-ids")
    fun getMetadataListByDocumentIds(
        @RequestBody body: GetMetadataByDocumentIdsReqVo,
    ): Result<List<KnowledgeBaseMetadataWithValueDto>> {
        val resp = knowledgeBaseMetadataService.getMetadataListByDocumentIds(body.documentIds)

        return when {
            resp.isOk ->
                Result.success(resp.data)
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 编辑元数据值
     */
    @PostMapping("/edit-value")
    fun editMetadataValue(
        @RequestBody body: EditMetadataValueReqVo,
    ): Result<Void> {
        val resp =
            knowledgeBaseMetadataService.editMetadataValueBatch(
                body.metadataAddRequests.map {
                    KnowledgeBaseMetadataService.MetadataAddRequest(it.metadataId, it.newValue)
                },
                body.metadataEditRequests.map {
                    KnowledgeBaseMetadataService.MetadataEditRequest(it.metadataId, it.newValue)
                },
                body.documentIds,
                body.applyToAllDocuments,
                body.deletedMetadataIds,
            )

        return when {
            resp.isOk ->
                Result.success()
            else ->
                Result.error(resp.msg)
        }
    }

    /**
     * 根据知识库ID列表获取相同元数据
     */
    @PostMapping("/same")
    fun getSameMetadataByKnowledgeBaseIds(
        @RequestBody knowledgeBaseIds: List<Int>,
    ): Result<List<KnowledgeBaseMetadataBaseDto>> {
        val resp =
            knowledgeBaseMetadataService.getSameMetadataByKnowledgeBaseIds(
                knowledgeBaseIds,
            )

        return when {
            resp.isOk ->
                Result.success(resp.data)
            else ->
                Result.error(resp.msg)
        }
    }
}

package cn.voicecomm.ai.voicesagex.console.knowledge.controller

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseTagService
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseTagDto
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.BindTagVo
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.EditTagVo
import cn.voicecomm.ai.voicesagex.console.knowledge.vo.GetKnowledgeBaseTagListVo
import cn.voicecomm.ai.voicesagex.console.util.vo.Result
import lombok.extern.slf4j.Slf4j
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 知识库标签
 */
@RestController
@RequestMapping("/knowledge-base-tag")
@Validated
@Slf4j
open class KnowledgeBaseTagController(
    private val knowledgeBaseTagService: KnowledgeBaseTagService,
) {
    /**
     * 创建标签
     */
    @PostMapping("/create/{name}")
    fun createTag(
        @PathVariable(name = "name") name: String,
    ): Result<Int> {
        val resp = knowledgeBaseTagService.createTag(name)

        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 检查标签是否可删除
     */
    @GetMapping("/deletable/{id}")
    fun isDeletable(
        @PathVariable(name = "id") id: Int,
    ): Result<Boolean> = knowledgeBaseTagService.isDeletable(id).let { Result.success(it) }

    /**
     * 删除标签
     * @param id 标签ID
     */
    @DeleteMapping("/delete/{id}")
    fun deleteTag(
        @PathVariable("id") id: Int,
    ): Result<Void> {
        knowledgeBaseTagService.deleteTag(id)
        return Result.success()
    }

    /**
     * 绑定标签到知识库
     */
    @PostMapping("/bind")
    fun bindTag(
        @RequestBody @Validated body: BindTagVo,
    ): Result<Void> {
        val resp = knowledgeBaseTagService.bindTag(body.tagIds, body.knowledgeBaseId)

        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 编辑知识库标签
     */
    @PostMapping("/edit")
    fun editTag(
        @RequestBody @Validated body: EditTagVo,
    ): Result<Void> {
        val resp = knowledgeBaseTagService.editTag(body.id, body.name)
        return when {
            resp.isOk -> Result.success()
            else -> Result.error(resp.msg)
        }
    }

    /**
     * 获取标签列表
     */
    @PostMapping("/list")
    fun list(
        @RequestBody body: GetKnowledgeBaseTagListVo,
    ): Result<List<KnowledgeBaseTagDto>> {
        val resp = knowledgeBaseTagService.list(body.name)
        return when {
            resp.isOk -> Result.success(resp.data)
            else -> Result.error(resp.msg)
        }
    }
}

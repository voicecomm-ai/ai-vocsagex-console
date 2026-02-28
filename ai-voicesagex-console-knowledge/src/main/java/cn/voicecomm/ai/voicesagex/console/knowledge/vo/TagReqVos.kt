package cn.voicecomm.ai.voicesagex.console.knowledge.vo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class BindTagVo(
    val tagIds: List<Int>,
    @field:NotNull(message = "知识库ID不能为空")
    val knowledgeBaseId: Int,
)

data class EditTagVo(
    @field:NotNull(message = "标签ID不为空")
    val id: Int,
    @field:NotBlank(message = "标签名称不能为空")
    @field:Size(max = 50, message = "标签名称不能超过50个字符")
    val name: String,
)

data class GetKnowledgeBaseTagListVo(
    val name: String?,
)

package cn.voicecomm.ai.voicesagex.console.knowledge.vo

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.MetadataType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AddMetadataReqVo(
    @field:NotNull(message = "知识库ID不能为空")
    val knowledgeBaseId: Int,
    @field:NotBlank(message = "元数据名称不能为空")
    @field:Pattern(
        regexp = "^[a-z][a-z0-9_]*$",
        message = "文档属性名称仅支持小写字母、数字和下划线字符，且必须以字母开头",
    )
    @field:Size(max = 50, message = "元数据名称不能超过50个字符")
    val name: String,
    @field:NotNull(message = "元数据类型不能为空")
    val type: MetadataType,
)

data class EditMetadataReqVo(
    @field:NotNull(message = "元数据ID不能为空")
    val id: Int,
    @field:NotBlank(message = "元数据名称不能为空")
    @field:Pattern(
        regexp = "^[a-z][a-z0-9_]*$",
        message = "文档属性名称仅支持小写字母、数字和下划线字符，且必须以字母开头",
    )
    @field:Size(max = 50, message = "元数据名称不能超过50个字符")
    val name: String,
)

data class EditMetadataValueReqVo(
    @field:NotNull(message = "元数据编辑请求列表不存在")
    val metadataEditRequests: List<MetadataEditRequest>,
    @field:NotNull(message = "元数据增加请求列表不存在")
    val metadataAddRequests: List<MetadataAddRequest>,
    @field:NotEmpty(message = "文档ID列表不能为空")
    val documentIds: List<Int>,
    @field:NotNull(message = "是否应用于所有选定文档不能为空")
    val applyToAllDocuments: Boolean,
    val deletedMetadataIds: List<Int>?,
)

data class MetadataEditRequest(
    @field:NotNull(message = "元数据ID不能为空")
    val metadataId: Int,
    val newValue: String?,
)

data class MetadataAddRequest(
    @field:NotNull(message = "元数据ID不能为空")
    val metadataId: Int,
    val newValue: String?,
)

data class DeleteMetadataReqVo(
    @field:NotNull(message = "元数据ID不能为空")
    val id: Int,
)

data class GetMetadataByDocumentIdsReqVo(
    @field:NotEmpty(message = "文档ID列表不能为空")
    val documentIds: List<Int>,
)

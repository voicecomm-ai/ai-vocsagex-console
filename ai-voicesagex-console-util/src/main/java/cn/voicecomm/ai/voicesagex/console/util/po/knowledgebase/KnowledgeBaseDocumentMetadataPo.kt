package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase
import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@TableName("knowledge_base_document_metadata")
class KnowledgeBaseDocumentMetadataPo {
    @TableId(type = IdType.AUTO)
    var id: Int? = null // 主键

    /**
     * 文档ID
     */
    @TableField(value = "document_id")
    var documentId: Int? = null

    /**
     * 元数据ID
     */
    @TableField(value = "metadata_id")
    var metadataId: Int? = null

    /**
     * 元数据名称
     */
    @TableField(value = "\"name\"")
    var name: String? = null

    /**
     * 元数据值
     */
    @TableField(value = "\"value\"")
    var value: String? = null

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: LocalDateTime? = null

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: LocalDateTime? = null
}

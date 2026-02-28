package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import cn.voicecomm.ai.voicesagex.console.util.handler.JsonStringHandler
import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@TableName("knowledge_base_document")
class KnowledgeBaseDocumentPo {
    @TableId(type = IdType.AUTO)
    var id: Int? = null // 主键

    @TableField(value = "\"name\"")
    var name: String? = null

    @TableField(value = "\"chunks\"")
    var chunks: String? = null

    @TableField(value = "\"knowledge_base_id\"")
    var knowledgeBaseId: Int? = null

    @TableField(value = "\"chunking_strategy\"")
    var chunkingStrategy: String? = null

    @TableField(value = "\"unique_name\"")
    var uniqueName: String? = null

    @TableField(value = "\"preview_chunks\"", typeHandler = JsonStringHandler::class)
    var previewChunks: String? = null

    /**
     * 文档解析状态：WAIT IN_PROGRESS SUCCESS FAILED
     */
    @TableField(value = "\"process_status\"")
    var processStatus: String? = null

    @TableField(value = "\"word_count\"")
    var wordCount: Long? = null

    /**
     * 文档状态：ENABLE;DISABLE;
     */
    @TableField(value = "\"status\"")
    var status: String? = null

    @TableField(value = "\"process_failed_reason\"")
    var processFailedReason: String? = null

    @TableField(value = "\"is_archived\"")
    var isArchived: Boolean? = null

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

    /**
     * 创建人id
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    var createBy: Int? = null

    /**
     * 更新人id
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    var updateBy: Int? = null
}

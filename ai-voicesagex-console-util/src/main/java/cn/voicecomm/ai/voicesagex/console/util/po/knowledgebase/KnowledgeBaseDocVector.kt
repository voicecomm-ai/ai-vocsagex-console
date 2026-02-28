package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@TableName("knowledge_base_doc_vector")
class KnowledgeBaseDocVector {
    @TableId(type = IdType.AUTO)
    val id: Int? = null

    @TableField(value = "content_id")
    val contentId: String? = null

    @TableField(value = "knowledge_base_id")
    val knowledgeBaseId: Int? = null

    @TableField(value = "document_id")
    val documentId: Int? = null

    @TableField(value = "retrieve_content")
    val retrieveContent: String? = null

    @TableField(value = "context_content")
    val contextContent: String? = null

    @TableField(value = "metadata")
    val metadata: String? = null

    @TableField(value = "usage")
    val usage: String? = null

    @TableField(value = "process_status")
    val processStatus: String? = null

    @TableField(value = "vector")
    val vector: List<Float>? = null

    @TableField(value = "content_hash")
    val contentHash: String? = null

    @TableField(value = "chunking_strategy")
    val chunkingStrategy: String? = null

    @TableField(value = "\"status\"")
    val status: String? = null

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

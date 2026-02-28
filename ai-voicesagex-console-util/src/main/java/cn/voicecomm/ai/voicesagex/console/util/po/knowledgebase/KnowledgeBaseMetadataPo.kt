package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@TableName("knowledge_base_metadata")
class KnowledgeBaseMetadataPo {
    @TableId(type = IdType.AUTO)
    var id: Int? = null // 主键

    /**
     * 元数据名称
     */
    @TableField(value = "\"name\"")
    var name: String? = null

    /**
     * 数据类型：String;Number;Time
     */
    @TableField(value = "\"type\"")
    var type: String? = null

    /**
     * 是否内置
     */
    @TableField(value = "is_built_in")
    var isBuiltIn: Boolean? = null

    /**
     * 知识库ID
     */
    @TableField(value = "knowledge_base_id")
    var knowledgeBaseId: Int? = null

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

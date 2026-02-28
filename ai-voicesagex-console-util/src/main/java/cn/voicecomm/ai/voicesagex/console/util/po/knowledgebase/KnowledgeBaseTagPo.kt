package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@TableName("knowledge_base_tag")
class KnowledgeBaseTagPo {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    var id: Int? = null

    /**
     * 标签名称
     */
    @TableField(value = "\"name\"")
    var name: String? = null

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

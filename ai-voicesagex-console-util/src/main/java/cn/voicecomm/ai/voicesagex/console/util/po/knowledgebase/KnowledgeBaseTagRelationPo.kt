package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("knowledge_base_tag_relation")
class KnowledgeBaseTagRelationPo {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    var id: Int? = null

    /**
     * 知识库ID
     */
    @TableField(value = "\"knowledge_base_id\"")
    var knowledgeBaseId: Int? = null

    /**
     * 标签ID
     */
    @TableField(value = "\"tag_id\"")
    var tagId: Int? = null

    override fun toString(): String = "KnowledgeBaseTagRelationPo(id=$id, knowledgeBaseId=$knowledgeBaseId, tagId=$tagId)"
}

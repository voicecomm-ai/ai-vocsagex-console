package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 检索测试记录PO
 */
@TableName("retrieval_test_record")
class RetrievalTestRecordPo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    var id: Int? = null

    /**
     * 知识库ID
     */
    @TableField("knowledge_base_id")
    var knowledgeBaseId: Int? = null

    /**
     * 检索查询内容
     */
    @TableField("query")
    var query: String? = null

    /**
     * 创建时间
     */
    @TableField("create_time")
    var createTime: LocalDateTime? = null
}

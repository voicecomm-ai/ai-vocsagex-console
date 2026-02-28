package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @ClassName KnowledgeExtractionPo
 * @Author wangyang
 * @Date 2025/9/15 14:38
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_extraction")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeExtractionPo extends BaseAuditPo {

    /**
     * 知识抽取ID
     */
    @TableId(value = "\"extraction_id\"", type = IdType.AUTO)
    private Integer extractionId;


    /**
     * '任务名称'
     */
    @TableField(value = "\"job_name\"")
    private String jobName;

    /**
     * 图空间名称
     */
    @TableField(value = "\"space_name\"")
    private String spaceName;

    /**
     * 图空间id
     */
    @TableField(value = "\"space_id\"")
    private Integer spaceId;

    /**
     * tags
     *
     */
    @TableField(value = "\"tag_names\"")
    private String tagNames;

    /**
     * edges
     */
    @TableField(value = "\"edge_names\"")
    private String edgeNames;


    /**
     * 是否删除
     */
    @TableField(value = "\"deleted\"")
    private Boolean deleted;


    @TableField(value = "\"type\"")
    private Integer type;


}

package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @ClassName KnowledgeExtractionDto
 * @Description TODO
 * @Author wangyang
 * @Date 2025/9/16 15:31
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeExtractionDto extends BaseAuditDto {

    /**
     * 知识抽取ID
     */
    private Integer extractionId;


    /**
     * '任务名称'
     */
    private String jobName;

    /**
     * 图空间名称
     */
    private String spaceName;

    /**
     * 图空间id
     */
    private Integer spaceId;

    /**
     * tags
     *
     */
    private String tagNames;

    /**
     * edges
     */
    private String edgeNames;


    /**
     * 是否删除
     */
    private Boolean deleted;


    private Integer type;

}

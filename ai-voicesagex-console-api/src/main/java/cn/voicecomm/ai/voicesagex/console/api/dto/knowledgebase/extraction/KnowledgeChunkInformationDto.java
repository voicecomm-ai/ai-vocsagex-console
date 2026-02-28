package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @ClassName KnowledgeChunkInformationDto
 * @Author wangyang
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeChunkInformationDto implements Serializable {

    /**
     * ''chunk id''
     */

    private Integer chunkId;

    /**
     * 所属文档ID
     */

    private Long documentId;

    /**
     * '文本内容'
     */
    private String chunkContent;

    /**
     * '任务id  用于删除后取消任务'
     */

    private String  jobId;

    /**
     * 'chunk 状态  0 未抽取 1 抽取完成'
     */
    private  Integer chunkStatus;
    /**
     * 序号
     */
    private Integer chunkIndex;

    /**
     * '在原文中的页码'
     */
    private  Integer pageNumber;
    /**
     * 是否删除
     */
    private Boolean deleted;

    private String sheetName;

}

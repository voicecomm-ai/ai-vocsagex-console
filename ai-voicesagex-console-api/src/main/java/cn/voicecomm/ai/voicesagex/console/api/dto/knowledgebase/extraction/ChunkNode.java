package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * chunk 节点信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkNode {

    /**
     * 文本内容
     */
    private String page_content;


    /**
     * 页码
     */
    private Integer page;

    /**
     * 序号
     */
    private Integer chunk_index;

    /**
     * 文档来源
     */
    private DocumentNode source_document ;

    private String sheet_name;

}

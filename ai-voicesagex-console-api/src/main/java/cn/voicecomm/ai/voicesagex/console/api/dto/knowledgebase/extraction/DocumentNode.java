package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档节点信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentNode {

    /**
     * 文件名
     */
    @Schema(description = "文件名",example = "xxxx" )
    private String name;


    /**
     * 总页数
     */
    @Schema(description = "总页数",example = "10" )
    private Integer total_pages;


    /**
     * 文件路径
     */
    @Schema(description = "文件路径",example = "xxxx" )
    private String file_path;


    /**
     * 文件格式
     */
    @Schema(description = "文件路径",example = "xxxx" )
    private String format;


    /**
     * chunk 总数量
     */
    @Schema(description = "总数量",example = "10" )
    private Integer chunk_size;
}

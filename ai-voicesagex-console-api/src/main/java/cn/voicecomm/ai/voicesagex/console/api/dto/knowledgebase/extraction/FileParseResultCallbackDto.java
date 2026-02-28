package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件解析后返回chunk信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileParseResultCallbackDto {

    /**
     * mataData
     */
    @Schema(description = "metadata", example = "xxxx")
    @JsonProperty("metadata")
    private JSONObject metadata = new JSONObject();

    /**
     * 文档节点
     */
    @Schema(description = "文档节点", example = "xxxx")
    private DocumentNode document_node;


    /**
     * 文本块节点
     */
    @Schema(description = "文本块节点", example = "xxxx")
    private List<ChunkNode> chunk_nodes;



}

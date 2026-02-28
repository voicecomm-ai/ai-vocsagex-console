package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonChunkPreviewRespDataDto implements Serializable {

  @JsonProperty("chunk_type")
  private String chunkType;
  private List<CommonChunkDto> chunk;
  @JsonProperty("qa_chunk")
  private List<CommonQaChunkDto> qaChunk;
}

package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonChunkPreviewRespDto implements Serializable {

  private Integer code;
  private String msg;
  private CommonChunkPreviewRespDataDto data;
}

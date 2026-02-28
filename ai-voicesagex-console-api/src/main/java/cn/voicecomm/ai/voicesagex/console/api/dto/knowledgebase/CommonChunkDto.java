package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonChunkDto implements Serializable {

  private Integer id;

  private String content;

  private Integer character;

  /**
   * 失败原因
   */
  @JsonProperty("failed_reason")
  private String failedReason;

  /**
   * chunk ID
   */
  @JsonProperty("primary_key")
  private Integer primaryKey;

  /**
   * chunk状态：ENABLE DISABLE
   */
  private String status;

  /**
   * 是否编辑
   */
  private Boolean isEdited;
}

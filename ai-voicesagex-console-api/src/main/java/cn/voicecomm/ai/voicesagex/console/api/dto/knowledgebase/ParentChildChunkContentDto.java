package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentChildChunkContentDto implements Serializable {

  /**
   * 子段序号
   */
  private Integer id;

  /**
   * 子段内容
   */
  private String content;

  /**
   * 子段字节数
   */
  private Integer character;

  /**
   * 子段失败原因
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

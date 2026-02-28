package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentChildChunkDto implements Serializable {

  /**
   * 父段序号
   */
  private Integer id;

  /**
   * 子段数组
   */
  private List<ParentChildChunkContentDto> content;

  /**
   * 父段总长度
   */
  private Integer character;
}

package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图知识库本体关系Dto
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DataDto implements Serializable {


  @Serial
  private static final long serialVersionUID = -3536846521745679808L;

  private String id;

}

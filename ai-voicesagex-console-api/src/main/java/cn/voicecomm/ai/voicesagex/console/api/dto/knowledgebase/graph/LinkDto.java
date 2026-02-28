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
public class LinkDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 3501065033537002415L;

  private String source;

  private String target;

  private String value;

}

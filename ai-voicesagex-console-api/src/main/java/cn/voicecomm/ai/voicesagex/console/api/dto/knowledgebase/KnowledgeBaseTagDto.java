package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseTagDto implements Serializable {

  private Integer id;

  private String name;

  private Integer knowledgeBaseNum;
}

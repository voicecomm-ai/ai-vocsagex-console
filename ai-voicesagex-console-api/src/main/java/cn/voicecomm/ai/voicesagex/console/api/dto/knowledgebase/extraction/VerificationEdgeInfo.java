package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationEdgeInfo {

  private String subjectName;
  private String subjectId;

  private String objectName;
  private String objectId;
}

package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProcessDto implements Serializable {

  private String status;

  private Long wordCount;

}

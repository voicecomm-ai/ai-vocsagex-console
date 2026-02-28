package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractPreviewDTO {

  private String[] relations;

  private String[] tags;


  private String chunk;

  private String re_prompt;

  private String ner_prompt;


  private String ner_model;

  private String re_model;

}

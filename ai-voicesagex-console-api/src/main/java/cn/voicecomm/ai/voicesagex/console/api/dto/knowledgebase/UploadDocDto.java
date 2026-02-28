package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UploadDocDto implements Serializable {

  private String name;

  private String uniqueName;
}

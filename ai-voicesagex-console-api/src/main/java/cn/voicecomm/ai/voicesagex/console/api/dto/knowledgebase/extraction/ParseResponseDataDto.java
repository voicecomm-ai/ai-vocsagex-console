package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseResponseDataDto {

  private Map<String, Integer> docid_num;
}

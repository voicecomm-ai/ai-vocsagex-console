package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CodeNodeResponse extends BasePromptResponse implements Serializable {


  /**
   * code节点执行结果
   */
  private Map<String,Object> data;


}

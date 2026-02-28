package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import java.io.Serializable;
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
public class CodeResponse extends BasePromptResponse implements Serializable {


  /**
   * code结果
   */
  private CodeData data;


  /**
   * code结果
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class CodeData implements Serializable {

    /**
     * code结果
     */
    private String code;
  }

}

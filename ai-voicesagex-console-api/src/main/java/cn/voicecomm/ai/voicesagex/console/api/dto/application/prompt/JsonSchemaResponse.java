package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import cn.hutool.json.JSONObject;
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
public class JsonSchemaResponse extends BasePromptResponse implements Serializable {


  /**
   * json schema结果
   */
  private JsonSchemaData data;


  /**
   * json schema结果
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class JsonSchemaData implements Serializable {

    /**
     * json schema结果
     */
    private JSONObject json_schema;
  }

}

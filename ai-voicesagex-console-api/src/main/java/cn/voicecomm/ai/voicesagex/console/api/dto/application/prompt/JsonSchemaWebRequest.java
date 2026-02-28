package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class JsonSchemaWebRequest implements Serializable {

  /**
   * JSON Schema的自然语言描述
   */
  @NotBlank(message = "描述不能为空")
  private String description;


  /**
   * 连接id （前端uuid生成）
   */
  private String sseConnectId;


  /**
   * 模型id
   */
  @NotNull(message = "模型id不能为空")
  private Integer modelId;


}

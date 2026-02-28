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
public class CodeWebRequest implements Serializable {

  /**
   * 用户指令
   */
  @NotBlank(message = "用户指令不能为空")
  private String instruction;


  /**
   * 代码语言，python3，javascript
   */
  @NotBlank(message = "代码语言不能为空")
  private String language;

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

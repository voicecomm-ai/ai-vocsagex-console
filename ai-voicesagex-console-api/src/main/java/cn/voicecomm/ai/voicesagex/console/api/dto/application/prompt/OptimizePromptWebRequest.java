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
public class OptimizePromptWebRequest implements Serializable {

  /**
   * 提示词
   */
  @NotBlank(message = "提示词不能为空")
  private String prompt;

  /**
   * 用户指令
   */
  private String instruction;


  /**
   * 连接id （前端uuid生成）
   */
  private String sseConnectId;


  /**
   * 是否流式传输(默认为true)
   */
  private Boolean stream = true;


  /**
   * 模型id
   */
  @NotNull(message = "模型id不能为空")
  private Integer modelId;


}

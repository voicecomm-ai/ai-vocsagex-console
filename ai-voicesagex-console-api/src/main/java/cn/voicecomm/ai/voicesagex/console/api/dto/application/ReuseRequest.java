package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 应用复用请求参数
 *
 * @author wangf
 * @date 2025/10/27 上午 10:28
 */
@Data
public class ReuseRequest implements Serializable {

  /**
   * 应用id
   */
  @NotNull(message = "应用id不能为空")
  private Integer appId;

  /**
   * 应用类型---智能体agent,工作流应用workflow,智能体编排应用agent_arrangement
   */
  @NotBlank(message = "应用类型不能为空")
  private String type;


  /**
   * 应用名称
   */
  @NotBlank(message = "应用名称不能为空")
  @Size(message = "应用名称不能超过20个字", max = 50)
  private String name;

  /**
   * 描述
   */
  @Size(message = "应用描述不能超过100个字", max = 100)
  private String description;
}

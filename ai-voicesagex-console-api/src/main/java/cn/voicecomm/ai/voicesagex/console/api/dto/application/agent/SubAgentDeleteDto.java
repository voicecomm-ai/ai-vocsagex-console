package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 子智能体删除传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubAgentDeleteDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;


  /**
   * 当前多智能体的appId
   */
  @NotNull(message = "当前多智能体的appId不能为空")
  private Integer currentAppId;

  /**
   * 删除的子智能体的appId
   */
  @NotNull(message = "删除的子智能体的appId不能为空")
  private Integer deletedAppId;



}
package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 子智能体列表查询参数
 *
 * @author wangf
 * @date 2025/6/3 上午 10:47
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubAgentListReq implements Serializable {


  /**
   * 应用名称
   */
  private String applicationName;


}
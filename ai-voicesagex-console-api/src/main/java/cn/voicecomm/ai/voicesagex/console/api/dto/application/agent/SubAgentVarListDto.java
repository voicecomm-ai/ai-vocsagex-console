package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;


import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubAgentVarListDto implements Serializable {


  /**
   * 子智能体appId
   */
  private Integer subAgentAppId;


  /**
   * 子智能体Id
   */
  private Integer subAgentId;


  /**
   * 应用名称
   */
  private String applicationName;


  /**
   * 子智能体变量列表
   */
  private List<AgentVariableDto> variableList;


}

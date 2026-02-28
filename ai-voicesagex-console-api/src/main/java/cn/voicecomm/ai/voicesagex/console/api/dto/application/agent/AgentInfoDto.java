package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 智能体信息
 *
 * @author wangf
 * @date 2025/6/3 上午 10:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AgentInfoDto extends BaseDto implements Serializable {

  /**
   * id
   */
  private Integer id;

  /**
   * 应用id
   */
  private Integer applicationId;

  /**
   * 提示词
   */
  private String promptWords;

  /**
   * 模型id
   */
  private Integer modelId;

  /**
   * 模型名称
   */
  private String modelName;


  /**
   * mcp List
   */
  private List<McpDto> mcpList;


  /**
   * 短期记忆对话轮数
   */
  private Integer shortTermMemoryRounds;

  /**
   * 长期记忆是否开启
   */
  private Boolean longTermMemoryEnabled;


  /**
   * 长期记忆类型  always永久有效，custom自定义
   */
  private String longTermMemoryType;


  /**
   * 长期记忆有效期（xx天过期）
   */
  private Integer longTermMemoryExpired;


  /**
   * agent推理模式，默认为function_call
   * <p>
   * 枚举值: function_call react
   */
  private String agentMode;

  /**
   * 创建人
   */
  private Integer createBy;


  /**
   * 子智能体的app id列表
   */
  @Size(max = 10, message = "子智能体不能超过10个", groups = {AddGroup.class, UpdateGroup.class})
  private int[] subAgentAppIds;


  /**
   * 合作模式   主管Manager，协作Collaboration
   */
  private String cooperateMode;

}
package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
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
public class AgentInfoResponseDto extends BaseDto implements Serializable {

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
   * 应用名称
   */
  private String applicationName;


  /**
   * 应用icon url
   */
  private String applicationIconUrl;


  /**
   * agent类型  single单个，multiple多个
   */
  private String agentType;


  /**
   * 状态  -1删除，0草稿，1已发布
   */
  private Integer status;


  /**
   * mcp List
   */
  private List<McpDto> mcpList;


  /**
   * 变量 List
   */
  private List<AgentVariableDto> variableList;



  /**
   * 标签 List
   */
  private List<ApplicationExperienceTagDto> tagList;


  /**
   * 知识库 List
   */
  private List<KnowledgeBaseDto> knowledgeBaseDtoList;


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
   * 创建人用户名
   */
  private String createUsername;


  /**
   * 应用描述
   */
  private String applicationDescription;


  /**
   * 是否内置
   */
  private Boolean isIntegrated;


  /**
   * 子智能体的app id列表
   */
  private int[] subAgentAppIds;

  /**
   * 子智能体列表
   */
  private List<SubAgentInfoDto> subAgentAppList;


  /**
   * 智能体参数（发布后存储）
   */
  private String agentSchema;


  /**
   * 合作模式   主管Manager，协作Collaboration
   */
  private String cooperateMode;


}
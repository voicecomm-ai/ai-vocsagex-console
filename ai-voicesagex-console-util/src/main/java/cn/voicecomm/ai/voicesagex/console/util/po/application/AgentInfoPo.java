package cn.voicecomm.ai.voicesagex.console.util.po.application;

import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 智能体信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "agent_info")
public class AgentInfoPo extends BasePo implements Serializable {

  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 应用id
   */
  @TableField(value = "application_id")
  private Integer applicationId;

  /**
   * 提示词
   */
  @TableField(value = "prompt_words")
  private String promptWords;

  /**
   * 模型id
   */
  @TableField(value = "model_id")
  private Integer modelId;

  /**
   * 模型名称
   */
  @TableField(value = "model_name")
  private String modelName;


  /**
   * 短期记忆对话轮数
   */
  @TableField(value = "short_term_memory_rounds")
  private Integer shortTermMemoryRounds;

  /**
   * 长期记忆是否开启
   */
  @TableField(value = "long_term_memory_enabled")
  private Boolean longTermMemoryEnabled;


  /**
   * 长期记忆类型  always永久有效，custom自定义
   */
  @TableField(value = "long_term_memory_type")
  private String longTermMemoryType;


  /**
   * 长期记忆有效期（xx天过期）
   */
  @TableField(value = "long_term_memory_expired")
  private Integer longTermMemoryExpired;

  /**
   * agent推理模式，默认为function_call
   * <p>
   * 枚举值: function_call react
   */
  @TableField(value = "agent_mode")
  private String agentMode;

  /**
   * 创建人
   */
  @TableField(value = "create_by", fill = FieldFill.INSERT)
  private Integer createBy;


  /**
   * 子智能体的app id列表
   */
  @TableField(value = "sub_agent_app_ids")
  private int[] subAgentAppIds;

  /**
   * 合作模式   主管Manager，协作Collaboration
   */
  @TableField(value = "cooperate_mode")
  private String cooperateMode;

}
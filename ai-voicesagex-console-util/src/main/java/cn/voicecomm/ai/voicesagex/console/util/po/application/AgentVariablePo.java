package cn.voicecomm.ai.voicesagex.console.util.po.application;

import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
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
 * 智能体变量
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "agent_variable")
public class AgentVariablePo extends BasePo implements Serializable {

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
   * 字段类型--text文本,paragraph段落,select下拉选择，number数字
   */
  @TableField(value = "field_type")
  private String fieldType;

  /**
   * 变量名称
   */
  @TableField(value = "\"name\"")
  private String name;

  /**
   * 显示名称
   */
  @TableField(value = "display_name")
  private String displayName;

  /**
   * 下拉选项（逗号拼接）
   */
  @TableField(value = "select_options")
  private String selectOptions;

  /**
   * 最大长度
   */
  @TableField(value = "max_length")
  private Integer maxLength;

  /**
   * 是否必填
   */
  @TableField(value = "required")
  private Boolean required;
}
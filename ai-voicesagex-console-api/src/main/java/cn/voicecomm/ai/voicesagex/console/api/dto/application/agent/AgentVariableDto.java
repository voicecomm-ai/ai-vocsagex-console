package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 智能体-变量
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
public class AgentVariableDto extends BaseDto implements Serializable {

  /**
   * id
   */
  private Integer id;


  /**
   * 应用id
   */
  private Integer applicationId;

  /**
   * 字段类型--text文本,paragraph段落,select下拉选择，number数字
   */
  private String fieldType;

  /**
   * 变量名称
   */
  @NotBlank(message = "变量名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(max = 50, message = "变量名称不能超过50个字", groups = {AddGroup.class, UpdateGroup.class})
  private String name;

  /**
   * 显示名称
   */
  @Size(max = 50, message = "变量名称不能超过50个字", groups = {AddGroup.class, UpdateGroup.class})
  private String displayName;

  /**
   * 下拉选项（逗号拼接）
   */
  private String selectOptions;

  /**
   * 最大长度
   */
  private Integer maxLength;

  /**
   * 是否必填
   */
  private Boolean required;
}
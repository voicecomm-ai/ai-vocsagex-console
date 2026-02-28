package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDefaultValue {

  /**
   * 键
   */
  private String key;
  /**
   * 类型
   */
  private String type;
  /**
   * 值
   */
  private String value;
}
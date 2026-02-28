package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Variable implements Serializable {

  // --- 基础属性 ---

  /**
   * 变量名，用于在流程中引用此配置项。
   */
  private String variable;

  /**
   * 显示标签，用于在用户界面中展示此配置项的名称。
   */
  private String label;

  /**
   * 输入控件类型 (如 select, text-input, file-list)。
   *
   * @see cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.VariableEntityType
   */
  private String type;

  /**
   * 最大长度限制 (对于文本输入或列表项数)。 注意：JSON中的数字在Java中通常用Integer或int表示。
   */
  private Integer max_length;

  /**
   * 是否为必填项。
   */
  private Boolean required;

  // --- 特定于类型的属性 ---

  /**
   * 选择框 (select) 的可选项列表。 对于非选择框类型，此列表可能为空。
   */
  private List<String> options;

  /**
   * 文件上传配置：允许的上传方式 (如 local_file, remote_url)。 仅当 type 为 "file-list" 时有效。
   */
  private List<String> allowed_file_upload_methods;

  /**
   * 文件上传配置：允许的文件类型 (如 image, document)。 仅当 type 为 "file-list" 时有效。
   */
  private List<String> allowed_file_types;

  /**
   * 文件上传配置：允许的文件扩展名。 仅当 type 为 "file-list" 时有效。 （当前配置中为空数组）
   */
  private List<String> allowed_file_extensions;
}

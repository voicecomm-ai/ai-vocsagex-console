package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 变量dto
 *
 * @author wangf
 * @date 2025/5/21 下午 4:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VariableDto extends BaseAuditDto implements Serializable {

  /**
   * id
   */
  private Integer id;

  /**
   * 应用id
   */
  private Integer applicationId;

  /**
   * 节点id
   */
  private Integer nodeId;

  /**
   * 节点名称
   */
  private String nodeName;

  /**
   * 节点类型
   */
  private String nodeType;

  /**
   * 字段类型--text文本,select下拉选择，number数字，single_file单文件，file_list文件列表
   */
  private String fieldType;

  /**
   * 变量名称
   */
  private String name;

  /**
   * 显示名称
   */
  private String displayName;

  /**
   * 最大长度
   */
  private Integer maxLength;

  /**
   * 是否必填  0否，1是
   */
  private Integer required;

  /**
   * 支持的文件类型   文档、图片、音频、视频、其他文件类型（包含多个用逗号拼接）
   */
  private String supportFileType;

  /**
   * 其他文件类型扩展名称（多个逗号拼接）
   */
  private String otherFileTypeExtension;

  /**
   * 上传文件类型  local本地上传，url，both两者
   */
  private String uploadFileType;

  /**
   * 变量值类型 String,Number,File,Object,Array[string],Array[number],Array[file],Array[object]
   */
  private String valueType;

  /**
   * 变量值是否引用其他变量 0否，1是
   */
  private Integer existReference;

  /**
   * 变量引用的其他变量id
   */
  private Integer referenceVariableId;

  /**
   * 输入模式为Constant时的值
   */
  private String constantModeValue;
}
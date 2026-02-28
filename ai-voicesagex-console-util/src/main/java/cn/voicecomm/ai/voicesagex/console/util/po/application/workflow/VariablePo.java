package cn.voicecomm.ai.voicesagex.console.util.po.application.workflow;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
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
 * 变量
 * @author wangf
 * @date 2025/5/21 下午 5:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "\"variable\"")
public class VariablePo extends BaseAuditPo implements Serializable {

  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 应用id
   */
  @TableField(value = "application_id")
  private Integer application_id;

  /**
   * 节点id
   */
  @TableField(value = "node_id")
  private Integer node_id;

  /**
   * 节点类型
   */
  @TableField(value = "node_type")
  private String node_type;

  /**
   * 字段类型--text文本,select下拉选择，number数字，single_file单文件，file_list文件列表
   */
  @TableField(value = "field_type")
  private String field_type;

  /**
   * 变量名称
   */
  @TableField(value = "\"name\"")
  private String name;

  /**
   * 显示名称
   */
  @TableField(value = "display_name")
  private String display_name;

  /**
   * 最大长度
   */
  @TableField(value = "max_length")
  private Integer max_length;

  /**
   * 是否必填  0否，1是
   */
  @TableField(value = "required")
  private Integer required;

  /**
   * 支持的文件类型   文档、图片、音频、视频、其他文件类型（包含多个用逗号拼接）
   */
  @TableField(value = "support_file_type")
  private String support_file_type;

  /**
   * 其他文件类型扩展名称（多个逗号拼接）
   */
  @TableField(value = "other_file_type_extension")
  private String other_file_type_extension;

  /**
   * 上传文件类型  local本地上传，url，both两者
   */
  @TableField(value = "upload_file_type")
  private String upload_file_type;

  /**
   * 变量值类型 String,Number,File,Object,Array[string],Array[number],Array[file],Array[object]
   */
  @TableField(value = "value_type")
  private String value_type;

  /**
   * 输入模式 Variable，Constant
   */
  @TableField(value = "input_mode")
  private String input_mode;

  /**
   * 变量值是否引用其他变量 0否，1是
   */
  @TableField(value = "exist_reference")
  private Integer exist_reference;

  /**
   * 变量引用的其他变量id
   */
  @TableField(value = "reference_variable_id")
  private Integer reference_variable_id;

  /**
   * 输入模式为Constant时的值
   */
  @TableField(value = "constant_mode_value")
  private String constant_mode_value;
}
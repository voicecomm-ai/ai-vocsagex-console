package cn.voicecomm.ai.voicesagex.console.util.po.model;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据集文件
 *
 * @author ryc
 * @date 2025-08-06 13:17:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_dataset_file")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelDatasetFilePo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -1277018794696484878L;

  /**
   * 主键id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  /**
   * 数据集id
   */
  @TableField(value = "\"dataset_id\"")
  private Integer datasetId;
  /**
   * 文件名称
   */
  @TableField(value = "\"name\"")
  private String name;
  /**
   * 文件大小
   */
  @TableField(value = "\"size\"")
  private String size;
  /**
   * 文件路径
   */
  @TableField(value = "\"path\"")
  private String path;

}

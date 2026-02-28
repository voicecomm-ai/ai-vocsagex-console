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
 * 数据集
 *
 * @author ryc
 * @date 2025-07-29 10:12:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_dataset")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelDatasetPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = 8726501325822326506L;

  /**
   * 主键id
   */
  @TableId(value = "\"id\"", type = IdType.AUTO)
  private Integer id;
  /**
   * 数据集名称
   */
  @TableField(value = "\"name\"")
  private String name;
  /**
   * 数据集类型 0：训练数据；1：微调数据；2：评测数据
   */
  @TableField(value = "\"type\"")
  private Integer type;
  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  @TableField(value = "\"classification\"")
  private Integer classification;
  /**
   * 文件数量
   */
  @TableField(value = "\"file_num\"")
  private Integer fileNum;
  /**
   * 文件路径
   */
  @TableField(value = "\"path\"")
  private String path;
  /**
   * 是否解析 0：否；1：是
   */
  @TableField(value = "\"is_analysis\"")
  private Integer isAnalysis;

}

package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelAlgorithmTrainDto extends BaseAuditDto {


  @Serial
  private static final long serialVersionUID = 8821512490943056813L;

  /**
   * 选择训练的数据来源：0：选择；1：上传
   */
  private Integer selectDataSource;

  /**
   * 数据集id
   */
  private Integer datasetId;

  /**
   * 需要新增的数据集
   */
  private ModelDatasetDto modelDataset;

  /**
   * 模型id
   */
  private Integer modelId;

  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  private Integer classification;

  /**
   * 训练后的模型名称
   */
  private String modelName;

  /**
   * 是否支持微调
   */
  private Boolean isSupportAdjust;

  /**
   * 配置文件脚本
   */
  private String configText;

  /**
   * 模型内部名称
   */
  private String internalName;

  /**
   * CPU核数
   */
  private Integer cpuCoresNum;

  /**
   * 内存（MB）
   */
  private Integer memorySize;

  /**
   * 是否选择gpu
   */
  private Boolean isSelectedGpu;

  /**
   * gpu块数
   */
  private Integer gpuNum;

}

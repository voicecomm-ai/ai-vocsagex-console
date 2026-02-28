package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据集Dto
 *
 * @author ryc
 * @date 2025-07-29 10:12:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelDatasetDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = -8361562375104686585L;

  /**
   * 主键id
   */
  @NotNull(message = "主键id不能为空", groups = {UpdateGroup.class})
  private Integer id;
  /**
   * 数据集名称
   */
  @NotBlank(message = "数据集名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(max = 50, message = "数据集名称不能超过50个字", groups = {AddGroup.class,
      UpdateGroup.class})
  private String name;
  /**
   * 数据集类型 0：训练数据；1：微调数据；2：评测数据
   */
  @NotNull(message = "数据集类型不能为空", groups = {AddGroup.class})
  private Integer type;
  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  private Integer classification;
  /**
   * 文件数量
   */
  private Integer fileNum;
  /**
   * 文件路径
   */
  @NotBlank(message = "文件路径不能为空", groups = {AddGroup.class})
  private String path;
  /**
   * 是否解析 0：否；1：是
   */
  private Integer isAnalysis;
  /**
   * 解析描述
   */
  private String analysisDesc;
}

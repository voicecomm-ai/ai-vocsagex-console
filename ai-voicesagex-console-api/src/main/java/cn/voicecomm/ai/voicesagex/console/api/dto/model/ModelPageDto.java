package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
import java.util.List;
import java.util.Map;
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
public class ModelPageDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 20693690119271714L;

  /**
   * 主键id
   */
  private Integer id;

  /**
   * 模型名称
   */
  private String name;

  /**
   * 模型类型 0：算法模型；1：预训练模型
   */
  private Integer type;
  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  private Integer classification;
  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  private String classificationName;
  /**
   * 图标地址
   */
  private String iconUrl;
  /**
   * 模型类型名称
   */
  private String typeName;

  /**
   * 标签id集合
   */
  private List<ModelTagDto> tagList;
  /**
   * 模型简介
   */
  private String introduction;

  /**
   * 是否支持视觉
   */
  private Boolean isSupportVisual;

  /**
   * 是否上架 0：否；1：是
   */
  private Boolean isShelf;

  /**
   * 生成状态 0：生成中；1：生成成功；2：生成失败
   */
  private Integer generateStatus;

  /**
   * 是否支持函数
   */
  private Boolean isSupportFunction;

  /**
   * 是否特殊
   */
  private Boolean isSpecial;

  /**
   * 推理模式
   */
  private Map<String, String> reasoningMode;

}

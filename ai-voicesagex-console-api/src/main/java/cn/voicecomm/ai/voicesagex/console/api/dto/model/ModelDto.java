package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.List;
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
public class ModelDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 20693690119271714L;

  /**
   * 主键id
   */
  @NotNull(message = "主键id不能为空", groups = {UpdateGroup.class})
  private Integer id;
  /**
   * 模型名称
   */
  @NotBlank(message = "模型名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(message = "不能超过50个字", max = 50, groups = {AddGroup.class, UpdateGroup.class})
  private String name;
  /**
   * 模型类型 0：算法模型；1：预训练模型
   */
  @NotNull(message = "模型类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private Integer type;
  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  @NotNull(message = "模型分类不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private Integer classification;
  /**
   * 非内置标签id集合
   */
  private List<Integer> tagIdList;
  /**
   * 模型调用地址
   */
  private String url;
  /**
   * 图标地址
   */
  private String iconUrl;
  /**
   * 模型上传地址
   */
  private String path;
  /**
   * 模型配置
   */
  private String options;
  /**
   * 模型简介
   */
  @Size(message = "模型简介最大输入100个字", max = 100, groups = {AddGroup.class,
      UpdateGroup.class})
  private String introduction;
  /**
   * 模型唯一信息
   */
  @Size(message = "模型唯一信息最大输入100个字", max = 100, groups = {AddGroup.class,
      UpdateGroup.class})
  private String uniqueInfo;
  /**
   * 加载方式
   */
  private String loadingMode;
  /**
   * 音色名称
   */
  private String timbreName;
  /**
   * 音色默认文本
   */
  @Size(message = "音色默认文本最大输入100个字", max = 100, groups = {AddGroup.class,
      UpdateGroup.class})
  private String timbreText;
  /**
   * 模型上下文长度
   */
  @Min(value = 0, message = "模型上下文长度最小为0", groups = {AddGroup.class, UpdateGroup.class})
  @Max(value = 128000, message = "模型上下文长度不能超过128000", groups = {AddGroup.class,
      UpdateGroup.class})
  private Integer contextLength;
  /**
   * 最大tokens上限
   */
  @Min(value = 0, message = "最大tokens上限最小为0", groups = {AddGroup.class, UpdateGroup.class})
  @Max(value = 128000, message = "最大tokens上限不能超过128000", groups = {AddGroup.class,
      UpdateGroup.class})
  private Integer tokenMax;
  /**
   * 模型概述
   */
  @NotBlank(message = "请填写模型概述", groups = {AddGroup.class})
  @Size(message = "模型概述最大输入200个字", max = 200, groups = {AddGroup.class,
      UpdateGroup.class})
  private String overview;
  /**
   * 应用场景
   */
  @NotBlank(message = "请填写应用场景", groups = {AddGroup.class})
  @Size(message = "应用场景最大输入200个字", max = 200, groups = {AddGroup.class,
      UpdateGroup.class})
  private String usageScene;
  /**
   * 本地部署
   */
  @Size(message = "本地部署最大输入1000个字", max = 1000, groups = {AddGroup.class,
      UpdateGroup.class})
  private String localDeploy;
  /**
   * API调用文档
   */
  @Size(message = "API调用文档最大输入1000个字", max = 1000, groups = {AddGroup.class,
      UpdateGroup.class})
  private String apiDocument;
  /**
   * 是否支持视觉
   */
  private Boolean isSupportVisual;
  /**
   * 是否支持文档
   */
  private Boolean isSupportDocument;
  /**
   * 是否支持函数
   */
  private Boolean isSupportFunction;
  /**
   * 是否支持微调
   */
  private Boolean isSupportAdjust;
  /**
   * 是否上架
   */
  private Boolean isShelf;
  /**
   * 模型内部名称
   */
  @NotBlank(message = "模型内部名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(message = "不能超过50个字", max = 50, groups = {AddGroup.class, UpdateGroup.class})
  private String internalName;
  /**
   * 是否支持分布式训练
   */
  private Boolean isSupportDistributedTrain;
  /**
   * 分布式训练框架
   */
  private String trainFrame;
  /**
   * 量化模型存储方式 0：输入路径；1：上传文件
   */
  private Integer quantifiedStorageType;
  /**
   * 量化模型地址
   */
  private String quantifiedStorageUrl;
  /**
   * 权重文件存储方式 0：输入路径；1：上传文件
   */
  private Integer weightStorageType;
  /**
   * 权重文件地址
   */
  private String weightStorageUrl;
  /**
   * 模型代码文件
   */
  private String codeUrl;
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
  /**
   * 生成状态 0：生成中；1：生成成功；2：生成失败
   */
  private Integer generateStatus;
  /**
   * 任务id
   */
  private String taskId;
  /**
   * 任务信息
   */
  private String taskInfo;
  /**
   * 是否特殊
   */
  private Boolean isSpecial;
  /**
   * 模型apiKey
   */
  private String apiKey;
  /**
   * 模型调用地址
   */
  private String invokeUrl = "/voicesagex-console/application-web/model/pre-trained/v1/invoke";

}

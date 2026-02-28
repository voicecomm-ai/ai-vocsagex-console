package cn.voicecomm.ai.voicesagex.console.util.po.model;

import cn.voicecomm.ai.voicesagex.console.util.handler.JsonStringHandler;
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
 * 模型
 *
 * @author ryc
 * @date 2025-06-03 17:22:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -4429035274753921466L;

  /**
   * 主键id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  /**
   * 模型名称
   */
  @TableField(value = "\"name\"")
  private String name;
  /**
   * 模型类型 0：算法模型；1：预训练模型
   */
  @TableField(value = "\"type\"")
  private Integer type;
  /**
   * 模型分类 1：文本生成；2：多模态；4：视频生成；5：图片生成；6：向量模型；7：语音合成；8：语音识别；9：排序模型；
   */
  @TableField(value = "\"classification\"")
  private Integer classification;
  /**
   * 模型调用地址
   */
  @TableField(value = "\"url\"")
  private String url;
  /**
   * 图标地址
   */
  @TableField(value = "\"icon_url\"")
  private String iconUrl;
  /**
   * 模型上传地址
   */
  @TableField(value = "\"path\"")
  private String path;
  /**
   * 模型配置
   */
  @TableField(value = "\"options\"", typeHandler = JsonStringHandler.class)
  private String options;
  /**
   * 模型简介
   */
  @TableField(value = "\"introduction\"")
  private String introduction;
  /**
   * 模型唯一信息
   */
  @TableField(value = "\"unique_info\"")
  private String uniqueInfo;
  /**
   * 加载方式
   */
  @TableField(value = "\"loading_mode\"")
  private String loadingMode;
  /**
   * 音色名称
   */
  @TableField(value = "\"timbre_name\"")
  private String timbreName;
  /**
   * 音色默认文本
   */
  @TableField(value = "\"timbre_text\"")
  private String timbreText;
  /**
   * 模型上下文长度
   */
  @TableField(value = "\"context_length\"")
  private Integer contextLength;
  /**
   * 最大tokens上限
   */
  @TableField(value = "\"token_max\"")
  private Integer tokenMax;
  /**
   * 模型概述
   */
  @TableField(value = "\"overview\"")
  private String overview;
  /**
   * 应用场景
   */
  @TableField(value = "\"usage_scene\"")
  private String usageScene;
  /**
   * 本地部署
   */
  @TableField(value = "\"local_deploy\"")
  private String localDeploy;
  /**
   * API调用文档
   */
  @TableField(value = "\"api_document\"")
  private String apiDocument;
  /**
   * 是否支持视觉
   */
  @TableField(value = "\"is_support_visual\"")
  private Boolean isSupportVisual;
  /**
   * 是否支持文档
   */
  @TableField(value = "\"is_support_document\"")
  private Boolean isSupportDocument;
  /**
   * 是否支持函数
   */
  @TableField(value = "\"is_support_function\"")
  private Boolean isSupportFunction;
  /**
   * 是否支持微调
   */
  @TableField(value = "\"is_support_adjust\"")
  private Boolean isSupportAdjust;
  /**
   * 是否上架
   */
  @TableField(value = "\"is_shelf\"")
  private Boolean isShelf;
  /**
   * 模型内部名称
   */
  @TableField(value = "\"internal_name\"")
  private String internalName;
  /**
   * 是否支持分布式训练
   */
  @TableField(value = "\"is_support_distributed_train\"")
  private Boolean isSupportDistributedTrain;
  /**
   * 分布式训练框架
   */
  @TableField(value = "\"train_frame\"")
  private String trainFrame;
  /**
   * 量化模型存储方式 0：输入路径；1：上传文件
   */
  @TableField(value = "\"quantified_storage_type\"")
  private Integer quantifiedStorageType;
  /**
   * 量化模型地址
   */
  @TableField(value = "\"quantified_storage_url\"")
  private String quantifiedStorageUrl;
  /**
   * 解压后量化模型地址
   */
  @TableField(value = "\"quantified_storage_dir\"")
  private String quantifiedStorageDir;
  /**
   * 权重文件存储方式 0：输入路径；1：上传文件
   */
  @TableField(value = "\"weight_storage_type\"")
  private Integer weightStorageType;
  /**
   * 权重文件地址
   */
  @TableField(value = "\"weight_storage_url\"")
  private String weightStorageUrl;
  /**
   * 解压后权重文件地址
   */
  @TableField(value = "\"weight_storage_dir\"")
  private String weightStorageDir;
  /**
   * 模型代码文件
   */
  @TableField(value = "\"code_url\"")
  private String codeUrl;
  /**
   * 解压后模型代码文件
   */
  @TableField(value = "\"code_dir\"")
  private String codeDir;
  /**
   * CPU核数
   */
  @TableField(value = "\"cpu_cores_num\"")
  private Integer cpuCoresNum;
  /**
   * 内存（MB）
   */
  @TableField(value = "\"memory_size\"")
  private Integer memorySize;
  /**
   * 是否选择gpu
   */
  @TableField(value = "\"is_selected_gpu\"")
  private Boolean isSelectedGpu;
  /**
   * gpu块数
   */
  @TableField(value = "\"gpu_num\"")
  private Integer gpuNum;
  /**
   * 生成状态 0：生成中；1：生成成功；2：生成失败
   */
  @TableField(value = "\"generate_status\"")
  private Integer generateStatus;
  /**
   * 任务id
   */
  @TableField(value = "\"task_id\"")
  private String taskId;
  /**
   * 任务信息
   */
  @TableField(value = "\"task_info\"", typeHandler = JsonStringHandler.class)
  private String taskInfo;
  /**
   * 是否特殊
   */
  @TableField(value = "\"is_special\"")
  private Boolean isSpecial;
  /**
   * 模型apiKey
   */
  @TableField(value = "\"api_key\"")
  private String apiKey;
}

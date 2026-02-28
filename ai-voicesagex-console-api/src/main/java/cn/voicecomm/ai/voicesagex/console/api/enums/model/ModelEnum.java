package cn.voicecomm.ai.voicesagex.console.api.enums.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型枚举
 *
 * @author ryc
 * @date 2025/6/4
 */
@Getter
@AllArgsConstructor
public enum ModelEnum {
  ;

  @Getter
  @AllArgsConstructor
  public enum TypeEnum {

    /**
     * 算法模型
     */
    ALGORITHM(0, "算法模型"),

    PRE_TRAINING(1, "预训练模型"),
    ;

    private final Integer key;

    private final String desc;

    public static String getDescByKey(Integer key) {
      for (TypeEnum enumItem : TypeEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem.getDesc();
        }
      }
      return "";
    }

    public static TypeEnum getEnumByKey(Integer key) {
      for (TypeEnum enumItem : TypeEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem;
        }
      }
      return null;
    }
  }

  @Getter
  @AllArgsConstructor
  public enum ClassificationEnum {

    /**
     * 文本生成
     */
    TEXTGENERATION(1, "TextGeneration", "文本生成"),

    MULTIMODAL(2, "Multimodal", "多模态"),

    VIDEOGENERATION(4, "VideoGeneration", "视频生成"),

    IMAGEGENERATION(5, "ImageGeneration", "图片生成"),

    EMBEDDING(6, "Embedding", "向量模型"),

    TTS(7, "TTS", "语音合成"),

    ASR(8, "ASR", "语音识别"),

    RERANK(9, "Rerank", "排序模型"),

    ;

    private final Integer key;

    private final String value;

    private final String desc;

    public static String getValueByKey(Integer key) {
      for (ClassificationEnum enumItem : ClassificationEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem.getValue();
        }
      }
      return "";
    }

    public static String getDescByKey(Integer key) {
      for (ClassificationEnum enumItem : ClassificationEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem.getDesc();
        }
      }
      return "";
    }

    public static ClassificationEnum getEnumByKey(Integer key) {
      for (ClassificationEnum enumItem : ClassificationEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem;
        }
      }
      return null;
    }
  }

  @Getter
  @AllArgsConstructor
  public enum GenerateStatusEnum {

    /**
     * 生成状态
     */
    GENERATING(0, "生成中"),

    SUCCESS(1, "生成成功"),

    FAILURE(2, "生成失败"),
    ;

    private final Integer key;

    private final String desc;

    public static String getDescByKey(Integer key) {
      for (TypeEnum enumItem : TypeEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem.getDesc();
        }
      }
      return "";
    }

    public static TypeEnum getEnumByKey(Integer key) {
      for (TypeEnum enumItem : TypeEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem;
        }
      }
      return null;
    }
  }

  @Getter
  @AllArgsConstructor
  public enum LoadModeEnum {

    /**
     * 加载方式
     */
    OLLAMA("ollama", "ollama方式"),

    OTHER("other", "其他方式"),
    ;

    private final String key;

    private final String desc;

  }

  @Getter
  @AllArgsConstructor
  public enum SourceEnum {

    /**
     * 模型来源
     */
    FINETUNE(0, "微调模型"),

    PRE_TRAIN(1, "预训练模型"),
    ;

    private final Integer key;

    private final String desc;

  }

  @Getter
  @AllArgsConstructor
  public enum ReasoningModeEnum {

    /**
     * 推理模式
     */
    REACT("ReAct", "react"),

    FUNCTION_CALL("Function Calling", "function_call"),
    ;

    private final String key;

    private final String desc;

  }

}

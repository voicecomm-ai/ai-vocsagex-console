package cn.voicecomm.ai.voicesagex.console.api.enums.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置文件类型
 *
 * @author ryc
 * @date 2025/6/4
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnum {
  /**
   * 训练状态
   */
  ALGORITHM_TRAIN(0, "train.yaml", "算法模型训练配置文件"),

  TRAIN_DEPLOY(1, "server.yaml", "训练模型部署配置文件"),

  PRE_TRAIN_FINETUNE(2, "fine-tuning.yaml", "预训练模型微调配置文件(其他类型)"),

  FINETUNE_DEPLOY(3, "server.yaml", "微调模型部署配置文件"),

  ALGORITHM_EVAL(4, "eval.yaml", "算法模型评测配置文件"),

  PRE_TRAIN_EVAL(5, "eval.yaml", "预训练模型评测配置文件")
  ;
  private final Integer key;

  private final String value;

  private final String desc;

  public static String getValueByKey(Integer key) {
    for (ConfigTypeEnum enumItem : ConfigTypeEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getValue();
      }
    }
    return "";
  }

}

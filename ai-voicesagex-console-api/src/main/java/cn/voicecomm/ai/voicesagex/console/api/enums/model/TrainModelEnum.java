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
public enum TrainModelEnum {
  ;

  @Getter
  @AllArgsConstructor
  public enum TrainStatusEnum {
    /**
     * 训练状态
     */
    TRAINING(0, "训练中"),

    QUEUE(1, "排队中"),

    TRAIN_SUCCESS(2, "训练成功"),

    TRAIN_FAILURE(3, "训练失败"),

    IN_DEPLOY(4, "部署中"),

    DEPLOY_SUCCESS(5, "部署成功"),

    DEPLOY_FAILURE(6, "部署失败"),
    ;

    private final Integer key;

    private final String desc;

  }

  @Getter
  @AllArgsConstructor
  public enum TaskTypeEnum {
    /**
     * 训练状态
     */
    TRAIN("train"),

    FINE_TUNING("fine_tuning"),

    EVAL("eval"),
    ;

    private final String key;

  }

}

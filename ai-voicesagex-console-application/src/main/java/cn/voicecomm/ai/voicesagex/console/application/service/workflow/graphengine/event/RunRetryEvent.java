package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 运行重试事件
 */
@Data
@SuperBuilder
public class RunRetryEvent {

  /**
   * 错误
   */
  private String error;

  /**
   * 重试索引
   */
  private int retryIndex;

  /**
   * 开始时间
   */
  private LocalDateTime startAt;
}
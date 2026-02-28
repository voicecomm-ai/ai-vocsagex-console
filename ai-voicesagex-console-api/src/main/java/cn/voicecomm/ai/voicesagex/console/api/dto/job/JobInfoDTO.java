package cn.voicecomm.ai.voicesagex.console.api.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobInfoDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 113429442132966237L;

  /**
   * 主键ID
   */
  private int id;

  /**
   * 执行器主键ID
   */
  private int jobGroup;

  /**
   * 任务执行CRON表达式
   */
  private String jobCron;

  /**
   * 任务描述
   */
  private String jobDesc;

  /**
   * 报警邮件
   */
  private String alarmEmail;

  /**
   * 执行器，任务Handler名称
   */
  private String executorHandler;

  /**
   * 执行器，任务参数
   */
  private String executorParam;


  /**
   * 失败重试次数
   */
  private Integer executorFailRetryCount = 0;


  /**
   * 任务id
   */
  private Integer jobId;
}

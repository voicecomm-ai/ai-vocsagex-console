package cn.voicecomm.ai.voicesagex.console.api.dto.job;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * xxl-job info
 */
@Data
@Accessors(chain = true)
public class XxlJobInfo implements Serializable {

  private int id;        // 主键ID

  private int jobGroup = 1001;    // 执行器主键ID
  private String jobCron;    // 任务执行CRON表达式
  private String jobDesc;

  private Date addTime;
  private Date updateTime;

  private String author = "outbound";    // 负责人
  private String alarmEmail;  // 报警邮件

  private String executorRouteStrategy = "FIRST";  // 执行器路由策略
  private String executorHandler;        // 执行器，任务Handler名称
  private String executorParam;        // 执行器，任务参数
  private String executorBlockStrategy = "SERIAL_EXECUTION";  // 阻塞处理策略
  private int executorTimeout = 0;        // 任务执行超时时间，单位秒
  private int executorFailRetryCount = 0;    // 失败重试次数

  private String glueType = "BEAN";    // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
  private String glueSource;    // GLUE源代码
  private String glueRemark = "GLUE代码初始化";    // GLUE备注
  private Date glueUpdatetime;  // GLUE更新时间

  private String childJobId;    // 子任务ID，多个逗号分隔

  private int triggerStatus;    // 调度状态：0-停止，1-运行
  private long triggerLastTime;  // 上次调度时间
  private long triggerNextTime;  // 下次调度时间

}

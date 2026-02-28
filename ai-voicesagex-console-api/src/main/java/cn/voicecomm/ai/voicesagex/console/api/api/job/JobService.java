package cn.voicecomm.ai.voicesagex.console.api.api.job;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.job.XxlJobInfo;

/**
 * 定时任务服务接口，提供定时任务的增删改操作<br> 支持与XXL-JOB等调度框架集成，实现任务的创建、更新与删除功能
 *
 * @author wangfan
 * @date 2025/4/2 上午 9:48
 */
public interface JobService {

  /**
   * 创建定时任务（创建和启动同步进行）
   *
   * @param jobInfoDTO 任务配置信息（包含任务名称、Cron表达式、执行器等）
   * @return CommonRespDto<Integer> 操作结果封装，包含：
   * <ul>
   *   <li>code: 200表示成功，其他为错误码</li>
   *   <li>message: 操作结果描述</li>
   *   <li>data: 成功时返回任务ID，失败时返回null</li>
   * </ul>
   */
  CommonRespDto<Integer> add(XxlJobInfo jobInfoDTO);


  /**
   * 根据ID获取信息
   *
   * @param jobId ID
   * @return 包含信息的响应对象
   */
  CommonRespDto<XxlJobInfo> getInfo(Integer jobId);

  /**
   * 根据执行器处理器获取信息
   *
   * @param executorHandler 执行器处理器名称
   * @return 包含信息的响应对象
   */
  CommonRespDto<XxlJobInfo> getInfoByExecutorHandler(String executorHandler);


  CommonRespDto<XxlJobInfo> getInfoByExecutorHandlerAndParam(String executorHandler,String param);


  /**
   * 更新定时任务配置
   *
   * @param jobInfoDTO 更新后的任务信息（需包含任务ID作为更新标识）
   * @return CommonRespDto<Integer> 操作结果封装（同{@link #add(XxlJobInfo)}）
   */
  CommonRespDto<Integer> update(XxlJobInfo jobInfoDTO);

  /**
   * 删除指定ID的定时任务
   *
   * @param jobId 需要删除的任务ID
   * @return CommonRespDto<Integer> 操作结果封装（同{@link #add(XxlJobInfo)}）
   */
  CommonRespDto<Integer> delete(Integer jobId);


  /**
   * 停止指定ID的定时任务
   *
   * @param jobId 需要停止的任务ID
   * @return CommonRespDto<Integer> 操作结果封装（同{@link #add(XxlJobInfo)}）
   */
  CommonRespDto<Integer> stop(Integer jobId);


  /**
   * 启动指定ID的定时任务
   *
   * @param jobId 需要启动的任务ID
   * @return CommonRespDto<Integer> 操作结果封装（同{@link #add(XxlJobInfo)}）
   */
  CommonRespDto<Integer> start(Integer jobId);
}

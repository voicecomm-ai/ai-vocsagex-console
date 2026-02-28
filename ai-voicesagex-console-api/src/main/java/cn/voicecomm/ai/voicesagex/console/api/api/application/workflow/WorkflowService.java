package cn.voicecomm.ai.voicesagex.console.api.api.application.workflow;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ReuseRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ShelfRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.Variable;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowRunsDto;
import java.util.List;
import java.util.Map;

/**
 * 工作流服务接口，提供工作流的详情、创建、更新、删除操作
 */
public interface WorkflowService {

  /**
   * 根据ID获取工作流详情
   *
   * @param id 工作流ID
   * @return 工作流详情
   */
  CommonRespDto<WorkflowInfoResponseDto> getById(Integer id);

  /**
   * 根据ID获取发现页工作流详情
   *
   * @param id 工作流ID
   * @return 工作流详情
   */
  CommonRespDto<WorkflowInfoResponseDto> getExperienceById(Integer id);


  /**
   * 更新工作流
   *
   * @param dto 工作流信息
   * @return 是否成功
   */
  CommonRespDto<Void> update(WorkflowDto dto);


  /**
   * 获取系统变量
   *
   * @param workflowId 工作流ID
   * @return 系统变量列表
   */
  CommonRespDto<List<Map<String, String>>> getSystemVariables(Integer workflowId);


  /**
   * 获取最近一次运行结果
   *
   * @param appId  应用ID
   * @param nodeId 节点ID
   * @return 最近一次运行结果
   */
  CommonRespDto<WorkflowNodeExecutionsDto> lastRun(Integer appId, String nodeId);

  /**
   * 单节点运行详情
   *
   * @param workflowRunId 单节点运行id
   * @return 单节点运行结果
   */
  CommonRespDto<WorkflowRunsDto> workflowRunDetail(String workflowRunId);

  /**
   * 根据工作流运行ID获取节点执行记录列表
   *
   * @param workflowRunId 单节点运行id
   * @return 单节点运行结果
   */
  CommonRespDto<List<WorkflowNodeExecutionsDto>> getNodeExecutionsByWorkflowRunId(
      String workflowRunId);

  /**
   * 发布工作流
   *
   * @param appId the app id
   * @return the common resp dto
   */
  CommonRespDto<String> publish(Integer appId);

  /**
   * 获取发布的工作流参数
   *
   * @param appId 应用ID
   * @return 工作流运行列表
   */
  CommonRespDto<List<Variable>> getPublishWorkflowParams(Integer appId);

  /**
   * 获取上架的工作流参数
   *
   * @param appId 应用ID
   * @return 工作流运行列表
   */
  CommonRespDto<List<Variable>> getExperienceWorkflowParams(Integer appId);

  /**
   * 工作流上架.
   *
   * @param shelfRequest the app id
   * @return the common resp dto
   */
  CommonRespDto<Void> onShelf(ShelfRequest shelfRequest);

  /**
   * 工作流下架.
   *
   * @param appId the app id
   * @return the common resp dto
   */
  CommonRespDto<Void> offShelf(Integer appId);

  /**
   * 获取体验标签列表.
   *
   * @return the common resp dto
   */
  CommonRespDto<List<ApplicationExperienceTagDto>> listExperienceTags(Boolean all);

  /**
   * 添加体验标签.
   *
   * @param tagDto the tag dto
   * @return the common resp dto
   */
  CommonRespDto<Void> addExperienceTag(ApplicationExperienceTagDto tagDto);

  /**
   * 更新体验标签.
   *
   * @param tagDto the tag dto
   * @return the common resp dto
   */
  CommonRespDto<Void> updateExperienceTag(ApplicationExperienceTagDto tagDto);

  /**
   * 删除体验标签.
   *
   * @param tagId the tag id
   * @return the common resp dto
   */
  CommonRespDto<Void> deleteExperienceTag(Integer tagId);

  /**
   * 工作流发现页数据复用
   *
   * @param request 请求参数
   * @return 智能体信息
   */
  CommonRespDto<Integer> reuse(ReuseRequest request);


  /**
   * 智能体api访问信息
   *
   * @return 智能体api访问信息
   */
  CommonRespDto<ApiAccessResponse> apiAccess();
}
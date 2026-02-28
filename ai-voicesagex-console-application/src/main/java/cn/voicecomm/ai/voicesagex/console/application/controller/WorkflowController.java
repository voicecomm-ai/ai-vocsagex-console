package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.workflow.WorkflowService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.SingleNodeRunReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowRunReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowRunsDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationKeyMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.WorkflowExecuteService;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.SseEmitterManager;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 工作流
 */
@RestController
@RequestMapping("/workflow")
@Validated
@Slf4j
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowService workflowService;

  private final WorkflowExecuteService workflowExecuteService;

  private final SseEmitterManager sseEmitterManager;
  private final ApplicationKeyMapper applicationKeyMapper;
  private final ApplicationService applicationService;

  /**
   * 获取工作流详情
   *
   * @param appId 应用ID
   * @return 工作流详情
   */
  @GetMapping("/getById")
  public Result<WorkflowInfoResponseDto> getById(
      @NotNull(message = "appId不能为空") Integer appId) {
    CommonRespDto<WorkflowInfoResponseDto> respDto = workflowService.getById(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 更新工作流
   *
   * @param dto 工作流信息
   * @return 是否成功
   */
  @PostMapping("/update")
  public Result<Void> update(@RequestBody @Valid WorkflowDto dto) {
    CommonRespDto<Void> respDto = workflowService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success();
  }

  /**
   * 获取系统变量
   *
   * @param workflowId 工作流ID
   * @return 系统变量
   */
  @GetMapping("/getSystemVariables")
  public Result<List<Map<String, String>>> getSystemVariables(Integer workflowId) {
    CommonRespDto<List<Map<String, String>>> respDto = workflowService.getSystemVariables(
        workflowId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 单节点运行
   *
   * @param request 单节点运行请求
   * @return 单节点运行结果
   */
  @PostMapping("/singleNodeRun")
  public Result<WorkflowNodeExecutionsDto> singleNodeRun(
      @RequestBody @Validated SingleNodeRunReq request) {
    log.info("单节点运行, request={}", JSONUtil.toJsonStr(request));
    CommonRespDto<WorkflowNodeExecutionsDto> respDto = workflowExecuteService.singleNodeRun(
        request.getApp_id(), request.getNode_id(), request.getUser_inputs());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 工作流运行
   *
   * @param request 工作流运行
   * @return 工作流运行结果
   */
  @PostMapping(value = "/draftWorkflowRun")
  public SseEmitter draftWorkflowRun(@RequestBody @Validated WorkflowRunReq request) {
    log.info("工作流运行web, workflow_run_id：{}, request={}", request.getWorkflow_run_id(),
        JSONUtil.toJsonStr(request));
    SseEmitter sseEmitter = sseEmitterManager.register(request.getWorkflow_run_id());
    Integer userId = UserAuthUtil.getUserId();
    Thread.startVirtualThread(() -> {
      UserAuthUtil.setAttachmentUserId(userId);
      workflowExecuteService.draftWorkflowRun(request.getWorkflow_run_id(),
          request.getApp_id(), request.getUser_inputs(), ApplicationStatusEnum.DRAFT);
    });
    return sseEmitter;
  }

  /**
   * 发布工作流运行
   *
   * @param request 工作流运行
   * @return 工作流运行结果
   */
  @PostMapping(value = "/publishRun")
  public Object publishRun(@RequestBody @Validated WorkflowRunReq request,
      @RequestHeader("api-key") String apiKey) {
    log.info("发布工作流运行web, workflow_run_id：{}, request={}", request.getWorkflow_run_id(),
        JSONUtil.toJsonStr(request));
    Result<ApplicationKeyPo> validResult = ApplicationApiController.validApiKey(
        applicationKeyMapper, applicationService, apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    CommonRespDto<ApplicationDto> resp = applicationService.getById(applicationKeyPo.getAppId());
    if (!resp.isOk()) {
      return Result.error(resp.getMsg());
    }
    SseEmitter sseEmitter = sseEmitterManager.register(request.getWorkflow_run_id());
    Thread.startVirtualThread(() -> {
      UserAuthUtil.setAttachmentUserId(applicationKeyPo.getUserId());
      Integer appId = applicationKeyPo.getAppId();
      workflowExecuteService.draftWorkflowRun(request.getWorkflow_run_id(),
          appId, request.getUser_inputs(), ApplicationStatusEnum.PUBLISHED);
    });
    return sseEmitter;
  }

  /**
   * 根据appId发布工作流运行
   *
   * @param request 工作流运行
   * @return 工作流运行结果
   */
  @PostMapping(value = "/publishRunUrl")
  public Object publishRunUrl(@RequestBody @Validated WorkflowRunReq request) {
    log.info("发布工作流URL运行, workflow_run_id：{}, request={}", request.getWorkflow_run_id(),
        JSONUtil.toJsonStr(request));
    CommonRespDto<ApplicationDto> respDto = applicationService.getById(request.getApp_id());
    ApplicationDto data = respDto.getData();
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), data);
    }
    if (!data.getUrlAccessable()) {
      return Result.error("此应用暂停使用");
    }
    SseEmitter sseEmitter = sseEmitterManager.register(request.getWorkflow_run_id());
    Integer userId = data.getCreateBy();
    Thread.startVirtualThread(() -> {
      UserAuthUtil.setAttachmentUserId(userId);
      workflowExecuteService.draftWorkflowRun(request.getWorkflow_run_id(),
          request.getApp_id(), request.getUser_inputs(), ApplicationStatusEnum.PUBLISHED);
    });
    return sseEmitter;
  }

  /**
   * api访问
   *
   * @return 访问结果
   */
  @GetMapping("/apiAccess")
  public Result<ApiAccessResponse> apiAccess() {
    CommonRespDto<ApiAccessResponse> respDto = workflowService.apiAccess();
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 发现页工作流运行
   *
   * @param request 工作流运行
   * @return 工作流运行结果
   */
  @PostMapping(value = "/experienceRun")
  public SseEmitter experienceRun(@RequestBody @Validated WorkflowRunReq request) {
    log.info("发现页工作流运行web, workflow_run_id：{}, request={}", request.getWorkflow_run_id(),
        JSONUtil.toJsonStr(request));
    SseEmitter sseEmitter = sseEmitterManager.register(request.getWorkflow_run_id());
    Integer userId = UserAuthUtil.getUserId();
    Thread.startVirtualThread(() -> {
      UserAuthUtil.setAttachmentUserId(userId);
      workflowExecuteService.draftWorkflowRun(request.getWorkflow_run_id(),
          request.getApp_id(), request.getUser_inputs(), ApplicationStatusEnum.EXPERIENCE);
    });
    return sseEmitter;
  }


  /**
   * 获取最后运行结果
   *
   * @param request 单节点运行请求
   * @return 最后运行结果
   */
  @PostMapping("/lastRun")
  public Result<WorkflowNodeExecutionsDto> lastRun(
      @RequestBody @Validated SingleNodeRunReq request) {
    CommonRespDto<WorkflowNodeExecutionsDto> respDto = workflowService.lastRun(request.getApp_id(),
        request.getNode_id());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据运行ID获取工作流运行详情
   *
   * @param workflowRunId 工作流运行ID
   * @return 工作流运行详情
   */
  @GetMapping("/workflowRunDetail")
  public Result<WorkflowRunsDto> workflowRunDetail(@RequestParam String workflowRunId) {
    CommonRespDto<WorkflowRunsDto> respDto = workflowService.workflowRunDetail(workflowRunId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据工作流运行ID获取节点执行记录列表
   *
   * @param workflowRunId 工作流运行ID
   * @return 节点执行记录列表
   */
  @GetMapping("/nodeExecutions")
  public Result<List<WorkflowNodeExecutionsDto>> getNodeExecutionsByWorkflowRunId(
      @RequestParam String workflowRunId) {
    CommonRespDto<List<WorkflowNodeExecutionsDto>> respDto = workflowService.getNodeExecutionsByWorkflowRunId(
        workflowRunId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}
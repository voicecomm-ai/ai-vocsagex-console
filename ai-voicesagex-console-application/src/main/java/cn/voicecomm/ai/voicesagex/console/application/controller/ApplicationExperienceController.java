package cn.voicecomm.ai.voicesagex.console.application.controller;

import static cn.voicecomm.ai.voicesagex.console.application.controller.ApplicationApiController.validApiKey;

import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationExperienceService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.workflow.WorkflowService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceListReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ReuseRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ShelfRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.Variable;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationTypeEnum;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationKeyMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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

/**
 * 应用发现页
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@RestController
@RequestMapping("/applicationExperience")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ApplicationExperienceController {

  private final ApplicationExperienceService applicationExperienceService;

  private final AgentInfoService agentInfoService;

  private final WorkflowService workflowService;

  private final ApplicationKeyMapper applicationKeyMapper;

  private final ApplicationService applicationService;

  /**
   * 获取应用发现页列表
   *
   * @param dto 请求参数
   * @return 列表
   */
  @PostMapping("/list")
  public Result<List<ApplicationExperienceDto>> list(
      @Validated @RequestBody ApplicationExperienceListReqDto dto) {
    CommonRespDto<List<ApplicationExperienceDto>> respDto = applicationExperienceService.applicationExperienceList(
        dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 智能体上架
   *
   * @param dto 智能体上架请求参数
   * @return 上架结果
   */
  @PostMapping("/agent/onShelf")
  public Result<Void> agentOnShelf(@Validated @RequestBody ShelfRequest dto) {
    CommonRespDto<Void> respDto = agentInfoService.onShelf(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success();
  }

  /**
   * 智能体下架
   *
   * @param appId 应用ID
   * @return 下架结果
   */
  @GetMapping("/agent/offShelf")
  public Result<Void> agentOffShelf(@NotNull(message = "appId不能为空") Integer appId) {
    CommonRespDto<Void> respDto = agentInfoService.offShelf(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success();
  }

  /**
   * 智能体发布
   *
   * @param appId 应用ID
   * @return 发布结果
   */
  @PostMapping("/agent/publish")
  public Result<String> agentPublish(Integer appId) {
    CommonRespDto<String> respDto = agentInfoService.publish(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 工作流发布
   *
   * @param appId 应用ID
   * @return 发布结果
   */
  @PostMapping("/workflow/publish")
  public Result<String> workflowPublish(Integer appId) {
    CommonRespDto<String> respDto = workflowService.publish(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取工作流参数
   *
   * @return 发布结果
   */
  @GetMapping("/workflow/getWorkflowParams")
  public Result<List<Variable>> getWorkflowParams(@RequestHeader("api-key") String apiKey) {

    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    Integer userId = applicationKeyPo.getUserId();
    UserAuthUtil.setAttachmentUserId(userId);
    CommonRespDto<List<Variable>> respDto = workflowService.getPublishWorkflowParams(
        applicationKeyPo.getAppId());
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据appId获取发布工作流参数
   *
   * @return 发布结果
   */
  @GetMapping("/workflow/getWorkflowParamsByAppId")
  public Result<List<Variable>> getWorkflowParamsByAppId(@RequestParam("appId") Integer appId) {
    CommonRespDto<List<Variable>> respDto = workflowService.getPublishWorkflowParams(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据appId获取上架工作流参数
   *
   * @return 发布结果
   */
  @GetMapping("/workflow/getExperienceWorkflowParamsByAppId")
  public Result<List<Variable>> getExperienceWorkflowParamsByAppId(
      @RequestParam("appId") Integer appId) {
    CommonRespDto<List<Variable>> respDto = workflowService.getExperienceWorkflowParams(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
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
      @RequestHeader("api-key") String apiKey,
      @RequestParam String workflowRunId) {
    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    Integer userId = applicationKeyPo.getUserId();
    UserAuthUtil.setAttachmentUserId(userId);
    CommonRespDto<ApplicationDto> resp = applicationService.getById(applicationKeyPo.getAppId());
    if (!resp.isOk()) {
      return Result.error(resp.getMsg());
    }
    CommonRespDto<List<WorkflowNodeExecutionsDto>> respDto = workflowService.getNodeExecutionsByWorkflowRunId(
        workflowRunId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取发现页智能体信息
   *
   * @param appId 应用ID
   * @return 智能体信息
   */
  @GetMapping("/agent/getExperienceInfo")
  public Result<AgentInfoResponseDto> getExperienceInfo(Integer appId) {
    CommonRespDto<AgentInfoResponseDto> respDto = agentInfoService.getExperienceInfo(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取工作流详情
   *
   * @param appId 应用ID
   * @return 工作流详情
   */
  @GetMapping("/workflow/getExperienceById")
  public Result<WorkflowInfoResponseDto> workflowGetExperienceById(
      @NotNull(message = "appId不能为空") Integer appId) {
    CommonRespDto<WorkflowInfoResponseDto> respDto = workflowService.getExperienceById(appId);
    return Result.success(respDto.getData());
  }

  /**
   * 体验应用复用
   *
   * @param request 请求参数
   */
  @PostMapping("/reuse")
  public Result<Integer> reuse(@RequestBody @Validated ReuseRequest request) {
    CommonRespDto<Integer> respDto = null;
    if (ApplicationTypeEnum.AGENT.getKey().equals(request.getType())) {
      respDto = agentInfoService.reuse(request);
    } else if (ApplicationTypeEnum.WORKFLOW.getKey().equals(request.getType())) {
      respDto = workflowService.reuse(request);
    }
    if (respDto == null) {
      return Result.error();
    }
    if (!respDto.isOk()) {
      return Result.error(respDto.getMsg());
    }
    return Result.success(respDto.getData());

  }


  /**
   * 添加体验标签
   */
  @PostMapping("/addExperienceTag")
  public Result<Void> addExperienceTag(@RequestBody ApplicationExperienceTagDto tagDto) {
    CommonRespDto<Void> respDto = workflowService.addExperienceTag(tagDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新体验标签
   */
  @PostMapping("/updateExperienceTag")
  public Result<Void> updateExperienceTag(@RequestBody ApplicationExperienceTagDto tagDto) {
    CommonRespDto<Void> respDto = workflowService.updateExperienceTag(tagDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除体验标签
   */
  @GetMapping("/deleteExperienceTag")
  public Result<Void> deleteExperienceTag(@RequestParam Integer tagId) {
    CommonRespDto<Void> respDto = workflowService.deleteExperienceTag(tagId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 上架
   */
  @PostMapping("/workflow/onShelf")
  public Result<Void> workflowOnShelf(@RequestBody ShelfRequest shelfRequest) {
    CommonRespDto<Void> respDto = workflowService.onShelf(shelfRequest);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 下架
   */
  @GetMapping("/workflow/offShelf")
  public Result<Void> workflowOffShelf(@RequestParam Integer appId) {
    CommonRespDto<Void> respDto = workflowService.offShelf(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取体验标签列表
   */
  @GetMapping("/listExperienceTags")
  public Result<List<ApplicationExperienceTagDto>> listExperienceTags(@RequestParam Boolean all) {
    CommonRespDto<List<ApplicationExperienceTagDto>> respDto = workflowService.listExperienceTags(
        all);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}

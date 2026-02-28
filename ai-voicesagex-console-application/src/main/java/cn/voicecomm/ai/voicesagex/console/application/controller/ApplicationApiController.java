package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentChatHistoryService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentVariableService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.UploadFilesService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationKeyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.UploadFilesResp;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.UrlAccessReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentChatHistoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentUrlChatListRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentVariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.ChatTitleUpdateDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentVarListDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.AgentInfoConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentChatTokenMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationKeyMapper;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatApiReqVo;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatReqVo;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.UrlChatApiReqVo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentChatTokenPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 应用api调用
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@RestController
@RequestMapping("/api")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ApplicationApiController {

  /**
   * 应用服务接口，用于处理应用相关的业务逻辑
   */
  private final ApplicationService applicationService;
  private final ApplicationKeyMapper applicationKeyMapper;
  private final AgentInfoConverter agentInfoConverter;

  @Autowired
  private ChatController chatController;

  private final UploadFilesService uploadFilesService;
  @Autowired
  private AgentVariableService agentVariableService;
  private final AgentChatTokenMapper agentChatTokenMapper;
  @Autowired
  private AgentChatHistoryService agentChatHistoryService;


  /**
   * 智能体url测试对话
   *
   * @param body 测试对话参数
   * @return sse
   */
  @PostMapping("/agentChatUrl")
  public SseEmitter agentChatUrl(@RequestBody @Validated UrlChatApiReqVo body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    if (!validResult.isOk()) {
      try {
        emitter.send(SseEmitter.event().name("error").id(body.getConversationId())
            .data(validResult.getMsg()));
      } catch (Exception e) {
        log.error("发送{}异常出错", validResult.getMsg(), e);
      } finally {
        emitter.complete();
      }
      return emitter;
    }
    AgentChatTokenPo po = validResult.getData();
    ApplicationDto data = applicationService.getById(po.getAppId()).getData();
    Integer userId = data.getCreateBy();
    UserAuthUtil.setAttachmentUserId(userId);
    TestChatReqVo testChatReqVo = agentInfoConverter.chatApiVoToChatVo(body);
    testChatReqVo.setRunType(ApplicationStatusEnum.PUBLISHED.getKey());
    testChatReqVo.setApplicationId(po.getAppId());
    chatController.executeSingleAgentChat(testChatReqVo, emitter, userId);
    return emitter;
  }


  /**
   * 智能体url测试对话--多智能体
   *
   * @param body 测试对话参数
   * @return sse
   */
  @PostMapping("/multipleAgentChatUrl")
  public SseEmitter multipleAgentChatUrl(@RequestBody @Validated UrlChatApiReqVo body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    if (!validResult.isOk()) {
      try {
        emitter.send(SseEmitter.event().name("error").id(body.getConversationId())
            .data(validResult.getMsg()));
      } catch (Exception e) {
        log.error("发送{}异常出错", validResult.getMsg(), e);
      } finally {
        emitter.complete();
      }
      return emitter;
    }
    AgentChatTokenPo po = validResult.getData();
    ApplicationDto data = applicationService.getById(po.getAppId()).getData();
    Integer userId = data.getCreateBy();
    UserAuthUtil.setAttachmentUserId(userId);
    TestChatReqVo testChatReqVo = agentInfoConverter.chatApiVoToChatVo(body);
    testChatReqVo.setRunType(ApplicationStatusEnum.PUBLISHED.getKey());
    testChatReqVo.setApplicationId(po.getAppId());
    chatController.executeMultipleAgentChat(testChatReqVo, emitter, userId);
    return emitter;
  }


  /**
   * 获取智能体url应用参数
   *
   * @return 智能体应用参数
   */
  @PostMapping("/getAgentUrlParameters")
  public Result<List<AgentVariableDto>> getAgentUrlParameters(
      @RequestBody @Validated UrlAccessReq body) {

    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    AgentChatTokenPo data = validResult.getData();
    CommonRespDto<List<AgentVariableDto>> listCommonRespDto = agentVariableService.publishedVariableListByAppId(
        data.getAppId());
    if (!listCommonRespDto.isOk()) {
      return Result.error(listCommonRespDto.getCode(), listCommonRespDto.getMsg(),
          listCommonRespDto.getData());
    }
    return Result.success(listCommonRespDto.getData());
  }


  /**
   * 获取智能体url应用参数--多智能体
   *
   * @return 多智能体应用参数
   */
  @PostMapping("/getMultipleAgentUrlParameters")
  public Result<List<SubAgentVarListDto>> getMultipleAgentUrlParameters(
      @RequestBody @Validated UrlAccessReq body) {

    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    AgentChatTokenPo data = validResult.getData();
    CommonRespDto<List<SubAgentVarListDto>> listCommonRespDto = agentVariableService.getPublishedSubAgentVariableListByAppId(
        data.getAppId());
    if (!listCommonRespDto.isOk()) {
      return Result.error(listCommonRespDto.getCode(), listCommonRespDto.getMsg(),
          listCommonRespDto.getData());
    }
    return Result.success(listCommonRespDto.getData());
  }

  /**
   * 智能体url对话列表
   *
   * @param body body
   */
  @PostMapping("/urlChatList")
  public Result<List<AgentUrlChatListRespDto>> urlChatList(
      @RequestBody @Validated UrlAccessReq body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    CommonRespDto<List<AgentUrlChatListRespDto>> respDto = agentChatHistoryService.chatList(
        body.getToken());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 智能体url对话详情
   *
   * @param body body
   */
  @PostMapping("/urlChatInfo")
  public Result<AgentChatHistoryDto> urlChatInfo(@RequestBody @Validated ChatTitleUpdateDto body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    CommonRespDto<AgentChatHistoryDto> respDto = agentChatHistoryService.chatInfo(body.getId());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 智能体url对话删除
   *
   * @param body body
   */
  @DeleteMapping("/deleteChat")
  public Result<Void> deleteChat(@RequestBody @Validated ChatTitleUpdateDto body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    CommonRespDto<Void> respDto = agentChatHistoryService.deleteChat(body.getId());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }


  /**
   * 智能体url对话标题修改
   *
   * @param body 标题修改请求
   */
  @PostMapping("/agentUrlChatTitleUpdate")
  public Result<Void> agentUrlChatTitleUpdate(@RequestBody @Validated ChatTitleUpdateDto body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    CommonRespDto<Void> respDto = agentChatHistoryService.updateTitle(body.getId(),
        body.getConversationTitle());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 智能体url对话token生成
   *
   * @param urlKey urlkey
   * @return token
   */
  @GetMapping("/agentUrlChatTokenGenerate")
  public Result<String> agentUrlChatTokenGenerate(String urlKey) {
    CommonRespDto<String> respDto = applicationService.agentUrlChatTokenGenerate(urlKey);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 根据urlKey获取应用信息
   *
   * @param urlKey urlKey
   * @return sse
   */
  @GetMapping("/getByUrlKey")
  public Result<ApplicationDto> getByUrlKey(@NotBlank(message = "urlKey不能为空") String urlKey) {
    CommonRespDto<ApplicationDto> respDto = applicationService.getByUrlKey(urlKey);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 获取应用apiKey列表
   *
   * @param appId appId
   * @return apiKey列表
   */
  @GetMapping("/getApiKeyList")
  public Result<List<ApplicationKeyDto>> getApiKeyList(
      @NotNull(message = "appId不能为空") Integer appId) {
    CommonRespDto<List<ApplicationKeyDto>> respDto = applicationService.getApiKeyList(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 智能体测试对话api
   *
   * @param body 测试对话参数
   * @return sse
   */
  @PostMapping("/agentChatTest")
  public Object chatTestApi(@RequestHeader("api-key") String apiKey,
      @RequestBody @Validated TestChatApiReqVo body) {
    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();

    Integer userId = applicationKeyPo.getUserId();
    UserAuthUtil.setAttachmentUserId(userId);
    TestChatReqVo testChatReqVo = agentInfoConverter.chatApiVoToChatVo(body);
    testChatReqVo.setRunType(ApplicationStatusEnum.PUBLISHED.getKey());
    testChatReqVo.setApplicationId(applicationKeyPo.getAppId());
    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    chatController.executeSingleAgentChat(testChatReqVo, emitter, userId);
    return emitter;
  }


  /**
   * 智能体测试对话api---多智能体
   *
   * @param body 测试对话参数
   * @return sse
   */
  @PostMapping("/multipleAgentChatTest")
  public Object multipleAgentChatTest(@RequestHeader("api-key") String apiKey,
      @RequestBody @Validated TestChatApiReqVo body) {
    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();

    Integer userId = applicationKeyPo.getUserId();
    UserAuthUtil.setAttachmentUserId(userId);
    TestChatReqVo testChatReqVo = agentInfoConverter.chatApiVoToChatVo(body);
    testChatReqVo.setRunType(ApplicationStatusEnum.PUBLISHED.getKey());
    testChatReqVo.setApplicationId(applicationKeyPo.getAppId());
    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    chatController.executeMultipleAgentChat(testChatReqVo, emitter, userId);
    return emitter;
  }


  /**
   * 文件上传
   *
   * @param file 文件
   * @return 文件信息
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<UploadFilesResp> upload(@RequestHeader("api-key") String apiKey,
      @Valid @NotNull(message = "文件不能为空") MultipartFile file) {
    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    Integer userId = applicationKeyPo.getUserId();
    UserAuthUtil.setAttachmentUserId(userId);
    log.info("文件上传, file={}", file.getOriginalFilename());
    CommonRespDto<UploadFilesResp> respDto = uploadFilesService.upload(file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 获取应用基本信息
   *
   * @return 包含应用信息的响应结果
   */
  @GetMapping("/getAppInfo")
  public Result<ApplicationDto> getById(@RequestHeader("api-key") String apiKey) {

    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    Integer userId = applicationKeyPo.getUserId();
    UserAuthUtil.setAttachmentUserId(userId);
    CommonRespDto<ApplicationDto> respDto = applicationService.getById(applicationKeyPo.getAppId());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 获取智能体应用参数
   *
   * @return 智能体应用参数
   */
  @GetMapping("/getAgentParameters")
  public Result<List<AgentVariableDto>> getAgentParameters(
      @RequestHeader("api-key") String apiKey) {

    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    CommonRespDto<List<AgentVariableDto>> listCommonRespDto = agentVariableService.publishedVariableListByAppId(
        applicationKeyPo.getAppId());
    if (!listCommonRespDto.isOk()) {
      return Result.error(listCommonRespDto.getCode(), listCommonRespDto.getMsg(),
          listCommonRespDto.getData());
    }
    return Result.success(listCommonRespDto.getData());
  }


  /**
   * 获取智能体应用参数--多智能体
   *
   * @return 多智能体应用参数
   */
  @GetMapping("/getMultipleAgentParameters")
  public Result<List<SubAgentVarListDto>> getMultipleAgentParameters(
      @RequestHeader("api-key") String apiKey) {

    Result<ApplicationKeyPo> validResult = validApiKey(applicationKeyMapper, applicationService,
        apiKey);
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    ApplicationKeyPo applicationKeyPo = validResult.getData();
    CommonRespDto<List<SubAgentVarListDto>> listCommonRespDto = agentVariableService.getPublishedSubAgentVariableListByAppId(
        applicationKeyPo.getAppId());
    if (!listCommonRespDto.isOk()) {
      return Result.error(listCommonRespDto.getCode(), listCommonRespDto.getMsg(),
          listCommonRespDto.getData());
    }
    return Result.success(listCommonRespDto.getData());
  }


  public static Result<ApplicationKeyPo> validApiKey(ApplicationKeyMapper applicationKeyMapper,
      ApplicationService applicationService, String apiKey) {
    if (StrUtil.isBlank(apiKey)) {
      return Result.error("apiKey不能为空");
    }
    ApplicationKeyPo applicationKeyPo = applicationKeyMapper.selectOne(
        Wrappers.<ApplicationKeyPo>lambdaQuery().eq(ApplicationKeyPo::getKeyValue, apiKey));
    if (applicationKeyPo == null) {
      return Result.error("apiKey不合法");
    }
    if (applicationKeyPo.getExpireTime().isBefore(LocalDateTime.now())) {
      return Result.error("apiKey已过期");
    }
    //
    ApplicationDto data = applicationService.getById(applicationKeyPo.getAppId()).getData();
    if (!data.getApiAccessable()) {
      return Result.error("应用未开启api访问");
    }
    // 更新最后使用时间
    applicationKeyMapper.update(ApplicationKeyPo.builder().lastUseTime(LocalDateTime.now()).build(),
        Wrappers.<ApplicationKeyPo>lambdaUpdate()
            .eq(ApplicationKeyPo::getId, applicationKeyPo.getId()));
    return Result.success(applicationKeyPo);
  }


  public static Result<AgentChatTokenPo> validToken(AgentChatTokenMapper agentChatTokenMapper,
      ApplicationService applicationService, String token, String urlKey) {
    AgentChatTokenPo po = agentChatTokenMapper.selectOne(
        Wrappers.<AgentChatTokenPo>lambdaQuery().eq(AgentChatTokenPo::getToken, token)
            .eq(AgentChatTokenPo::getUrlKey, urlKey));
    if (po == null) {
      return Result.error("url无效");
    }
    CommonRespDto<ApplicationDto> applicationDtoCommonRespDto = applicationService.getById(
        po.getAppId());
    if (!applicationDtoCommonRespDto.isOk()) {
      return Result.error(applicationDtoCommonRespDto.getMsg());
    }
    ApplicationDto data = applicationDtoCommonRespDto.getData();
    if (!data.getUrlAccessable()) {
      return Result.error("应用未开启url访问");
    }
    return Result.success(po);
  }
}

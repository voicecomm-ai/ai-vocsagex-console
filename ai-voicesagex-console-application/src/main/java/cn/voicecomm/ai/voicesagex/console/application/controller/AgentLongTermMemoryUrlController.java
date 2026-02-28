package cn.voicecomm.ai.voicesagex.console.application.controller;

import static cn.voicecomm.ai.voicesagex.console.application.controller.ApplicationApiController.validToken;

import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentLongTermMemoryService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryListRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryUrlDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryUrlReq;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentChatTokenMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentChatTokenPo;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能体记忆Url访问
 *
 * @author wangf
 * @date 2025/9/9 上午 11:05
 */
@RestController
@RequestMapping("/agentLongTermMemoryUrl")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AgentLongTermMemoryUrlController {

  private final AgentLongTermMemoryService agentLongTermMemoryService;


  private final AgentChatTokenMapper agentChatTokenMapper;

  private final ApplicationService applicationService;


  private static final String PUBLISH_TYPE = "published";

  /**
   * 更新智能体长期记忆信息
   *
   * @param dto 智能体长期记忆信息
   * @return 更新结果
   */
  @PostMapping("/update")
  public Result<Void> update(@RequestBody AgentLongTermMemoryDto dto) {
    if (StrUtil.hasBlank(dto.getToken())) {
      return Result.error("token不能为空");
    }
    if (StrUtil.hasBlank(dto.getUrlKey())) {
      return Result.error("urlKey不能为空");
    }

    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        dto.getToken(), dto.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }

    CommonRespDto<Void> respDto = agentLongTermMemoryService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除智能体长期记忆
   *
   * @param body body
   * @return 删除结果
   */
  @DeleteMapping("/delete")
  public Result<Void> delete(@RequestBody AgentLongTermMemoryUrlDeleteReq body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    CommonRespDto<Void> respDto = agentLongTermMemoryService.delete(body.getId());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 清空智能体长期记忆
   *
   * @param body body
   * @return 清空结果
   */
  @DeleteMapping("/clear")
  public Result<Void> clear(@RequestBody AgentLongTermMemoryUrlReq body) {
    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }
    CommonRespDto<Void> respDto = agentLongTermMemoryService.clear(validResult.getData().getAppId(),
        null, PUBLISH_TYPE);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取智能体长期记忆详情
   *
   * @param body body
   * @return 记忆详情
   */
  @PostMapping("/getInfo")
  public Result<AgentLongTermMemoryDto> getInfo(@RequestBody AgentLongTermMemoryUrlDeleteReq body) {

    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }

    CommonRespDto<AgentLongTermMemoryDto> respDto = agentLongTermMemoryService.getInfo(
        body.getId());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取智能体长期记忆列表
   *
   * @param body body
   * @return 记忆列表
   */
  @PostMapping("/getList")
  public Result<List<AgentLongTermMemoryListRespDto>> getList(
      @RequestBody AgentLongTermMemoryUrlReq body) {

    Result<AgentChatTokenPo> validResult = validToken(agentChatTokenMapper, applicationService,
        body.getToken(), body.getUrlKey());
    if (!validResult.isOk()) {
      return Result.error(validResult.getMsg());
    }

    CommonRespDto<List<AgentLongTermMemoryListRespDto>> respDto = agentLongTermMemoryService.getList(
        validResult.getData().getAppId(), null, PUBLISH_TYPE);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
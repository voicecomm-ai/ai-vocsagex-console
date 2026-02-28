package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentLongTermMemoryService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryListRespDto;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能体记忆
 *
 * @author wangf
 * @date 2025/9/9 上午 11:05
 */
@RestController
@RequestMapping("/agentLongTermMemory")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AgentLongTermMemoryController {

  private final AgentLongTermMemoryService agentLongTermMemoryService;

  /**
   * 更新智能体长期记忆信息
   *
   * @param dto 智能体长期记忆信息
   * @return 更新结果
   */
  @PostMapping("/update")
  public Result<Void> update(@RequestBody AgentLongTermMemoryDto dto) {
    CommonRespDto<Void> respDto = agentLongTermMemoryService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除智能体长期记忆
   *
   * @param id 记忆ID
   * @return 删除结果
   */
  @DeleteMapping("/delete/{id}")
  public Result<Void> delete(@PathVariable @NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Void> respDto = agentLongTermMemoryService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 清空智能体长期记忆
   *
   * @param applicationId 应用ID
   * @param type          数据类型  草稿draft，已发布published，试用experience
   * @return 清空结果
   */
  @DeleteMapping("/clear")
  public Result<Void> clear(@NotNull(message = "applicationId不能为空") Integer applicationId,
      @NotBlank(message = "type不能为空") String type) {
    log.info("清空智能体长期记忆，应用ID: {}，用户ID: {}", applicationId, UserAuthUtil.getUserId());
    CommonRespDto<Void> respDto = agentLongTermMemoryService.clear(applicationId,
        UserAuthUtil.getUserId(), type);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取智能体长期记忆详情
   *
   * @param id 记忆ID
   * @return 记忆详情
   */
  @GetMapping("/getInfo/{id}")
  public Result<AgentLongTermMemoryDto> getInfo(
      @PathVariable @NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<AgentLongTermMemoryDto> respDto = agentLongTermMemoryService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取智能体长期记忆列表
   *
   * @param applicationId 应用ID
   * @param type          数据类型  草稿draft，已发布published，试用experience
   * @return 记忆列表
   */
  @GetMapping("/getList")
  public Result<List<AgentLongTermMemoryListRespDto>> getList(
      @NotNull(message = "applicationId不能为空") Integer applicationId,
      @NotBlank(message = "type不能为空") String type) {
    CommonRespDto<List<AgentLongTermMemoryListRespDto>> respDto = agentLongTermMemoryService.getList(
        applicationId, UserAuthUtil.getUserId(), type);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
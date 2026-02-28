package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentDeleteDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentSelectUpdateDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能体信息
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@RestController
@RequestMapping("/agentInfo")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AgentInfoController {

  private final AgentInfoService agentInfoService;


  /**
   * 添加智能体信息
   *
   * @param dto dto
   * @return 返回包含操作结果的封装对象
   */
  @PostMapping("/add")
  public Result<Integer> add(@RequestBody @Validated(value = AddGroup.class) AgentInfoDto dto) {
    CommonRespDto<Integer> respDto = agentInfoService.add(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());

  }

  /**
   * 更新智能体信息
   *
   * @param dto dto
   * @return 返回包含操作结果的封装对象
   */
  @PostMapping("/update")
  public Result<Void> update(@RequestBody @Validated(value = UpdateGroup.class) AgentInfoDto dto) {
    CommonRespDto<Void> respDto = agentInfoService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success();
  }

  /**
   * 获取智能体信息详情
   *
   * @param applicationId 应用id
   * @return 返回包含操作结果的封装对象
   */
  @GetMapping("/getInfo")
  public Result<AgentInfoResponseDto> getInfo(
      @NotNull(message = "应用id不能为空") Integer applicationId) {
    CommonRespDto<AgentInfoResponseDto> respDto = agentInfoService.getInfo(applicationId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * api访问
   *
   * @return 访问结果
   */
  @GetMapping("/apiAccess")
  public Result<ApiAccessResponse> apiAccess(Integer applicationId) {
    CommonRespDto<ApiAccessResponse> respDto = agentInfoService.apiAccess(applicationId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除子智能体
   *
   * @param deleteDto 删除子智能体参数
   * @return 访问结果
   */
  @DeleteMapping("/deleteSubAgent")
  public Result<Void> deleteSubAgent(@RequestBody @Validated SubAgentDeleteDto deleteDto) {
    CommonRespDto<Void> respDto = agentInfoService.deleteSubAgent(deleteDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 子智能体选择更新
   *
   * @param dto 子智能体参数
   * @return 访问结果
   */
  @PatchMapping("/subAgentSelectedUpdate")
  public Result<Void> subAgentSelectedUpdate(@RequestBody @Validated SubAgentSelectUpdateDto dto) {
    CommonRespDto<Void> respDto = agentInfoService.subAgentSelectedUpdate(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}

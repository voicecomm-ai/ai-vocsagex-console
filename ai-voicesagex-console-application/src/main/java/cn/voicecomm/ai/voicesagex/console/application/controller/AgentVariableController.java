package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentVariableService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentVariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentVarListDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能体变量
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@RestController
@RequestMapping("/agentVariable")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AgentVariableController {

  private final AgentVariableService agentVariableService;

  /**
   * 创建智能体变量
   *
   * @param dto dto
   * @return 返回包含操作结果的封装对象
   */
  @PostMapping("/add")
  public Result<Integer> add(@RequestBody @Validated(value = AddGroup.class) AgentVariableDto dto) {
    CommonRespDto<Integer> respDto = agentVariableService.add(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());

  }

  /**
   * 批量创建智能体变量
   *
   * @param list list
   * @return 返回包含操作结果的封装对象
   */
  @PostMapping("/batchAdd")
  public Result<Void> batchAdd(@RequestBody @Validated List<AgentVariableDto> list) {
    CommonRespDto<Void> respDto = agentVariableService.batchAdd(list);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success();

  }

  /**
   * 更新智能体变量
   *
   * @param dto dto
   * @return 返回包含操作结果的封装对象
   */
  @PostMapping("/update")
  public Result<Void> update(
      @RequestBody @Validated(value = UpdateGroup.class) AgentVariableDto dto) {
    CommonRespDto<Void> respDto = agentVariableService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success();
  }

  /**
   * 删除智能体变量
   *
   * @param id id
   * @return 返回包含操作结果的封装对象
   */
  @DeleteMapping("/delete")
  public Result<Void> delete(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Void> respDto = agentVariableService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success();
  }

  /**
   * 获取智能体变量详情
   *
   * @param id id
   * @return 返回包含操作结果的封装对象
   */
  @GetMapping("/getInfo")
  public Result<AgentVariableDto> getInfo(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<AgentVariableDto> respDto = agentVariableService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据应用id获取变量列表
   *
   * @param applicationId 应用id
   * @return 返回包含操作结果的封装对象
   */
  @GetMapping("/getVariableListByAppId")
  public Result<List<AgentVariableDto>> getVariableListByAppId(
      @NotNull(message = "应用id不能为空") Integer applicationId) {
    CommonRespDto<List<AgentVariableDto>> respDto = agentVariableService.variableListByAppId(
        applicationId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 获取 复合 智能体->子智能体的变量选项列表
   *
   * @param applicationId 应用id
   * @return 返回包含操作结果的封装对象
   */
  @GetMapping("/getSubAgentVariableListByAppId")
  public Result<List<SubAgentVarListDto>> getSubAgentVariableListByAppId(
      @NotNull(message = "应用id不能为空") Integer applicationId) {
    CommonRespDto<List<SubAgentVarListDto>> respDto = agentVariableService.getSubAgentVariableListByAppId(
        applicationId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取 发现页 复合智能体->子智能体的变量列表
   *
   * @param applicationId 应用id
   * @return 返回包含操作结果的封装对象
   */
  @GetMapping("/getDisccoverSubAgentVariableListByAppId")
  public Result<List<SubAgentVarListDto>> getDisccoverSubAgentVariableListByAppId(
      @NotNull(message = "应用id不能为空") Integer applicationId) {
    CommonRespDto<List<SubAgentVarListDto>> respDto = agentVariableService.getDisccoverSubAgentVariableListByAppId(
        applicationId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}

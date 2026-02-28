package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentVariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentVarListDto;
import java.util.List;


/**
 * 智能体变量服务接口 提供智能体变量的增删改查及选项列表获取能力
 *
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface AgentVariableService {

  /**
   * 创建智能体变量
   *
   * @param dto 智能体变量传输对象
   * @return 包含新创建智能体变量信息的响应对象id
   */
  CommonRespDto<Integer> add(AgentVariableDto dto);


  /**
   * 批量创建智能体变量
   *
   * @param dto dto
   * @return CommonRespDto
   */
  CommonRespDto<Void> batchAdd(List<AgentVariableDto> dto);

  /**
   * 更新智能体变量信息
   *
   * @param dto 包含更新数据的智能体变量传输对象
   * @return 空数据响应对象（包含操作状态）
   */
  CommonRespDto<Void> update(AgentVariableDto dto);

  /**
   * 获取智能体变量详细信息
   *
   * @param id 智能体变量ID
   * @return 包含智能体变量详细信息的响应对象
   */
  CommonRespDto<AgentVariableDto> getInfo(Integer id);

  /**
   * 删除智能体变量
   *
   * @param id 要删除的智能体变量ID
   * @return 空数据响应对象（包含操作状态）
   */
  CommonRespDto<Void> delete(Integer id);

  /**
   * 根据应用ID获取智能体变量选项列表
   *
   * @param applicationId 应用ID
   * @return 包含智能体变量选项列表的响应对象
   */
  CommonRespDto<List<AgentVariableDto>> variableListByAppId(Integer applicationId);


  /**
   * 根据应用ID获取已发布智能体变量选项列表
   *
   * @param applicationId 应用ID
   * @return 获取已发布智能体变量选项列表的响应对象
   */
  CommonRespDto<List<AgentVariableDto>> publishedVariableListByAppId(Integer applicationId);


  /**
   * 根据应用ID获取各个子智能体的变量选项列表
   *
   * @param applicationId 应用ID
   * @return 获取已发布智能体变量选项列表的响应对象
   */
  CommonRespDto<List<SubAgentVarListDto>> getSubAgentVariableListByAppId(Integer applicationId);


  /**
   * 根据应用ID获取发现页多智能体的各个子智能体的变量选项列表
   *
   * @param applicationId 应用ID
   * @return 获取发现页智能体变量选项列表的响应对象
   */
  CommonRespDto<List<SubAgentVarListDto>> getDisccoverSubAgentVariableListByAppId(Integer applicationId);


  /**
   * 根据应用ID获取已发布多智能体的各个子智能体的变量选项列表
   *
   * @param applicationId 应用ID
   * @return 获取已发布智能体变量选项列表的响应对象
   */
  CommonRespDto<List<SubAgentVarListDto>> getPublishedSubAgentVariableListByAppId(
      Integer applicationId);
}

package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ReuseRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ShelfRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentPublishHistoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentDeleteDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentSelectUpdateDto;
import java.util.List;


/**
 * 智能体信息服务接口
 *
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface AgentInfoService {

  /**
   * 创建智能体信息
   *
   * @param dto 智能体信息传输对象
   * @return 包含新创建智能体信息的响应对象Id
   */
  CommonRespDto<Integer> add(AgentInfoDto dto);

  /**
   * 更新智能体信息
   *
   * @param dto 包含更新数据的智能体信息传输对象
   * @return 空数据响应对象（包含操作状态）
   */
  CommonRespDto<Void> update(AgentInfoDto dto);

  /**
   * 获取智能体信息详细信息
   *
   * @param applicationId 应用id
   * @return 包含智能体信息详细信息的响应对象
   */
  CommonRespDto<AgentInfoResponseDto> getInfo(Integer applicationId);


  /**
   * 智能体上架
   *
   * @param dto 智能体上架传输对象
   * @return 空数据响应对象（包含操作状态）
   */
  CommonRespDto<Void> onShelf(ShelfRequest dto);


  /**
   * 智能体下架
   *
   * @param appId 应用id
   * @return 空数据响应对象（包含操作状态）
   */
  CommonRespDto<Void> offShelf(Integer appId);


  /**
   * 智能体发布
   *
   * @param appId 应用id
   * @return 空数据响应对象（包含操作状态）
   */
  CommonRespDto<String> publish(Integer appId);


  /**
   * 获取智能体发布历史列表
   *
   * @param appId 应用id
   * @return 智能体发布历史列表
   */
  CommonRespDto<List<AgentPublishHistoryDto>> publishHistoryList(Integer appId);


  /**
   * 获取体验智能体信息
   *
   * @param appId 应用id
   * @return 智能体信息
   */
  CommonRespDto<AgentInfoResponseDto> getExperienceInfo(Integer appId);


  /**
   * 获取已发布的智能体信息
   *
   * @param appId 应用id
   * @return 智能体信息
   */
  CommonRespDto<AgentInfoResponseDto> getPublishedInfo(Integer appId);


  /**
   * 智能体发现页数据复用
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
  CommonRespDto<ApiAccessResponse> apiAccess(Integer applicationId);


  /**
   * 删除子智能体
   *
   * @param deleteDto 删除子智能体参数
   * @return CommonRespDto
   */
  CommonRespDto<Void> deleteSubAgent(SubAgentDeleteDto deleteDto);


  /**
   * 子智能体选择更新
   *
   * @param dto dto
   * @return CommonRespDto
   */
  CommonRespDto<Void> subAgentSelectedUpdate(SubAgentSelectUpdateDto dto);

}

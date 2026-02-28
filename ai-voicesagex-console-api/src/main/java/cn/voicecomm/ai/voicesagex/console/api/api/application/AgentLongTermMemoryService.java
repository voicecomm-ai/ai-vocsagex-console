package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryListRespDto;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 长期记忆服务
 *
 * @author wangf
 * @date 2025/9/9 上午 11:00
 */
public interface AgentLongTermMemoryService {

  CommonRespDto<Integer> add(AgentLongTermMemoryDto dto);

  // 批量添加
  CommonRespDto<Void> addBatch(List<AgentLongTermMemoryDto> dtoList);

  CommonRespDto<Void> update(AgentLongTermMemoryDto dto);

  CommonRespDto<Void> delete(Integer id);

  CommonRespDto<Void> clear(Integer applicationId, Integer userId, String type);

  CommonRespDto<AgentLongTermMemoryDto> getInfo(Integer id);

  CommonRespDto<List<AgentLongTermMemoryListRespDto>> getList(Integer applicationId,
      Integer userId, String type);


  void clearExpiredData(Integer applicationId, Integer userId, LocalDateTime expiredTime,
      String type);


}

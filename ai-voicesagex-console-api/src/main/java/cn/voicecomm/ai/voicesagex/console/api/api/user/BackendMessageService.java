package cn.voicecomm.ai.voicesagex.console.api.api.user;


import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UnreadCountDto;

import java.util.List;

public interface BackendMessageService {

  CommonRespDto<BackendMessageDto> send(BackendMessageDto message, Boolean isPersist);

  /**
   * 发送消息通知 websocket
   */
  void send(BackendMessageDto message);

  /**
   * 发送消息通知 websocket
   */
  CommonRespDto<BackendMessageDto> sendAndGet(BackendMessageDto message);

  CommonRespDto<BackendMessageDto> save(BackendMessageDto message);

  /**
   * 批量发送websocket
   */
  void sendBatchMessage(List<BackendMessageDto> backendMessageDTOList);

  CommonRespDto<UnreadCountDto> getUnreadCount();

  void clearUnread();

  void clearSingleUnread(Integer messageId);

  void deleteAll();

  CommonRespDto<PagingRespDto<BackendMessageDto>> getAll(PagingReqDto dto);

  CommonRespDto<PagingRespDto<BackendMessageDto>> getAllUnread(PagingReqDto dto);

}

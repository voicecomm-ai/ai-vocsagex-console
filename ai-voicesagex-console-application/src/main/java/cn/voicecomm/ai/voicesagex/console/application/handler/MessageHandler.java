package cn.voicecomm.ai.voicesagex.console.application.handler;

import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMessageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author: ryc
 * @date: 2023/1/11
 * @desc
 */
@Slf4j
@Component
public class MessageHandler {

  @DubboReference
  private BackendMessageService messageService;

  /**
   * 发送消息给前端
   *
   * @param messageTypeEnum
   * @param messageText
   * @param userId
   * @param type
   */
  public void sendMessage(MessageTypeEnum messageTypeEnum, String messageText, Integer userId,
      BackendMessageDto.Type type) {
    BackendMessageDto messageDTO = BackendMessageDto.builder().userId(userId)
        .msgType(messageTypeEnum.getCode()).isRead(false).msg(messageText)
        .createTime(LocalDateTime.now()).type(type.getValue()).build();
    messageService.send(messageDTO);
  }

  /**
   * 发送消息给前端
   *
   * @param messageTypeEnum
   * @param messageText
   * @param userId
   * @param type
   * @param attachment
   * @param isPersist
   */
  public void sendMessage(MessageTypeEnum messageTypeEnum, String messageText, Integer userId,
      BackendMessageDto.Type type, String attachment, String downloadPath, Boolean isPersist) {
    BackendMessageDto message = BackendMessageDto.builder().userId(userId)
        .msgType(messageTypeEnum.getCode()).isRead(false).msg(messageText)
        .downloadPath(downloadPath).createTime(LocalDateTime.now()).type(type.getValue())
        .attachment(attachment).build();
    // 发送消息
    messageService.send(message, isPersist);
  }
}

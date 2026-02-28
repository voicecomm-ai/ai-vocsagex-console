package cn.voicecomm.ai.voicesagex.console.user.consumer;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.MessageConverter;
import cn.voicecomm.ai.voicesagex.console.util.constant.MessageTopicConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "ai-voicesagex-console-websocket-consumer-group", topic = MessageTopicConstant.MESSAGE_CENTER_TOPIC)
@RequiredArgsConstructor
public class WebSocketConsumer implements RocketMQListener<BackendMessageDto> {

  private final SimpMessagingTemplate simpMessagingTemplate;

  private final MessageConverter messageConverter;

  @Override
  public void onMessage(BackendMessageDto message) {

    if (ObjUtil.isNotNull(message.getMsgType()) && message.getMsgType() > 400
        && message.getMsgType() < 500) {
      JSONObject entries = JSONUtil.parseObj(message);
      log.info("工作流消息主题：[{}] 消息内容：[{}] 目标工作流：[{}]",
          MessageTopicConstant.MESSAGE_CENTER_TOPIC, message, entries.getStr("workflowId"));
      simpMessagingTemplate.convertAndSend(
          "/workflow/" + entries.getStr("workflowId") + "/updateNotice",
          messageConverter.dtoToVo(message));
      return;
    }
    log.info("消息主题：[{}] 消息内容：[{}] 目标用户：[{}]", MessageTopicConstant.MESSAGE_CENTER_TOPIC,
        message, message.getUserId());
    simpMessagingTemplate.convertAndSendToUser(message.getUserId().toString(), "/message",
        messageConverter.dtoToVo(message));
  }
}

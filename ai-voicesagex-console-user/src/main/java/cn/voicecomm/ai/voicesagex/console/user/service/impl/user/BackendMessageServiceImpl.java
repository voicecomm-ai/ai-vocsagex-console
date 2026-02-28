package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMessageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UnreadCountDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendMessageConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendMessageMapper;
import cn.voicecomm.ai.voicesagex.console.user.util.PageUtil;
import cn.voicecomm.ai.voicesagex.console.util.constant.MessageTopicConstant;
import cn.voicecomm.ai.voicesagex.console.util.po.user.MessagePo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class BackendMessageServiceImpl extends
  ServiceImpl<BackendMessageMapper, MessagePo> implements
  BackendMessageService {

  private final RocketMQTemplate rocketMQTemplate;

  private final BackendMessageConverter backendMessageConverter;

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void send(BackendMessageDto message) {
    sendAndGet(message);
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public CommonRespDto<BackendMessageDto> sendAndGet(BackendMessageDto message) {
    if (Objects.isNull(message)) {
      log.error("消息为空，不执行后续操作");
      return CommonRespDto.success();
    }

    // 持久化消息
    save(message);

    // 发送消息到指定主题
    String topic = MessageTopicConstant.MESSAGE_CENTER_TOPIC;
    if (StrUtil.isNotBlank(message.getMsgTag())) {
      topic = topic + ":" + message.getMsgTag();
    }
    sendMessage(topic, message);

    return CommonRespDto.success(message);
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public CommonRespDto<BackendMessageDto> send(BackendMessageDto message, Boolean isPersist) {
    if (Objects.isNull(message)) {
      log.error("消息为空，不执行后续操作");
      return CommonRespDto.success();
    }

    // 持久化消息
    if (BooleanUtils.isTrue(isPersist)) {
      save(message);
    }

    // 发送消息到指定主题
    String topic = MessageTopicConstant.MESSAGE_CENTER_TOPIC;
    if (StrUtil.isNotBlank(message.getMsgTag())) {
      topic = topic + ":" + message.getMsgTag();
    }
    sendMessage(topic, message);

    return CommonRespDto.success(message);
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public CommonRespDto<BackendMessageDto> save(BackendMessageDto message) {
    // 持久化消息
    MessagePo MessagePo = backendMessageConverter.dtoToPo(message);
    saveOrUpdate(MessagePo);
    MessagePo savedMessage = getById(MessagePo.getId());

    message.setId(savedMessage.getId());
    message.setCreateTime(savedMessage.getCreateTime());
    return CommonRespDto.success(message);
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void sendBatchMessage(List<BackendMessageDto> BackendMessageDtoList) {
    if (CollUtil.isEmpty(BackendMessageDtoList)) {
      log.error("消息为空，不执行后续操作");
      return;
    }
    List<MessagePo> messagePoList = backendMessageConverter.dtoListToPoList(
      BackendMessageDtoList);
    /// 批量入库
    saveBatch(messagePoList);
    for (MessagePo MessagePo : messagePoList) {
      MessagePo.setCreateTime(LocalDateTime.now());
    }
    // 持久化消息
    String topic = MessageTopicConstant.MESSAGE_CENTER_TOPIC;
    log.info("开始批量发送websocket");
    messagePoList.forEach(MessagePo -> sendMessage(topic, MessagePo));
  }

  @Override
  public CommonRespDto<UnreadCountDto> getUnreadCount() {
    Integer userId = UserAuthUtil.getUserId();
    log.info("用户id[{}]", userId);
    UnreadCountDto unreadCountDto = new UnreadCountDto();
    long unread = count(
      Wrappers.<MessagePo>lambdaQuery().eq(MessagePo::getUserId, userId)
        .eq(MessagePo::getDeleted, Boolean.FALSE)
        .eq(MessagePo::getIsRead, Boolean.FALSE)
        .ne(MessagePo::getMsg, StringUtils.EMPTY));
    unreadCountDto.setTotal(unread);
    return CommonRespDto.success(unreadCountDto);
  }

  @Override
  public void clearUnread() {
    Integer userId = UserAuthUtil.getUserId();

    LambdaUpdateWrapper<MessagePo> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(MessagePo::getUserId, userId)
      .eq(MessagePo::getDeleted, Boolean.FALSE)
      .eq(MessagePo::getIsRead, Boolean.FALSE)
      .set(MessagePo::getIsRead, Boolean.TRUE);

    update(updateWrapper);
  }

  @Override
  public void clearSingleUnread(Integer messageId) {
    LambdaUpdateWrapper<MessagePo> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(MessagePo::getId, messageId)
      .eq(MessagePo::getDeleted, Boolean.FALSE)
      .eq(MessagePo::getIsRead, Boolean.FALSE)
      .set(MessagePo::getIsRead, Boolean.TRUE);

    update(updateWrapper);
  }

  @Override
  public void deleteAll() {
    Integer userId = UserAuthUtil.getUserId();

    LambdaUpdateWrapper<MessagePo> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(MessagePo::getUserId, userId)
      .eq(MessagePo::getDeleted, Boolean.FALSE)
      .set(MessagePo::getDeleted, Boolean.TRUE);

    update(updateWrapper);
  }

  @Override
  public CommonRespDto<PagingRespDto<BackendMessageDto>> getAll(PagingReqDto dto) {
    LambdaQueryWrapper<MessagePo> query = new LambdaQueryWrapper<>();
    query.eq(MessagePo::getUserId, UserAuthUtil.getUserId())
      .eq(MessagePo::getDeleted, Boolean.FALSE)
      .ne(MessagePo::getMsg, StringUtils.EMPTY)
      .orderByDesc(MessagePo::getCreateTime)
      .orderByDesc(MessagePo::getId);
    Page<MessagePo> page = PageUtil.of(dto);
    getBaseMapper().selectPage(page, query);
    PagingRespDto<BackendMessageDto> messageDtoPage = PageUtil.of(page,
      backendMessageConverter::poListToDtoList);
    return CommonRespDto.success(messageDtoPage);
  }

  @Override
  public CommonRespDto<PagingRespDto<BackendMessageDto>> getAllUnread(PagingReqDto dto) {
    LambdaQueryWrapper<MessagePo> query = new LambdaQueryWrapper<>();
    query.eq(MessagePo::getUserId, UserAuthUtil.getUserId())
      .eq(MessagePo::getIsRead, Boolean.FALSE)
      .eq(MessagePo::getDeleted, Boolean.FALSE)
      .ne(MessagePo::getMsg, StringUtils.EMPTY)
      .orderByDesc(MessagePo::getCreateTime).orderByDesc(MessagePo::getId);
    Page<MessagePo> page = PageUtil.of(dto);
    getBaseMapper().selectPage(page, query);
    PagingRespDto<BackendMessageDto> messageDtoPage = PageUtil.of(page,
      backendMessageConverter::poListToDtoList);
    return CommonRespDto.success(messageDtoPage);
  }

  /**
   * 发送消息公共方法
   */
  private void sendMessage(String topicConstant, Object object) {

    try {
      SendResult sendResult = rocketMQTemplate.syncSend(topicConstant, object);
      if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
        log.info("发送持久化结果:[{}]给主题：[{}]，msg id：[{}]", JSONUtil.toJsonStr(object),
          topicConstant,
          sendResult.getMsgId());
      } else {
        log.error("发送持久化结果:[{}]给主题：[{}]失败，msg id：[{}]", JSONUtil.toJsonStr(object),
          topicConstant,
          sendResult.getMsgId());
      }
    } catch (Exception e) {
      log.error("RocketMQ发送消息出错", e);
    }
  }
}

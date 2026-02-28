package cn.voicecomm.ai.voicesagex.console.user.controller;


import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMessageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UnreadCountDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendMessageConverter;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendMessageVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.UnreadCountVo;
import cn.voicecomm.ai.voicesagex.console.util.vo.PagingReqVo;
import cn.voicecomm.ai.voicesagex.console.util.vo.PagingRespVo;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 信息管理
 */
@RestController
@RequestMapping("/message")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

  private final BackendMessageConverter backendMessageConverter;

  @DubboReference
  private BackendMessageService backendMessageService;

  /**
   * 获取未读消息数量
   *
   * @return 未读消息数量
   */
  @GetMapping("unread/count")
  public Result<UnreadCountVo> getUnreadCount() {
    CommonRespDto<UnreadCountDto> resp = backendMessageService.getUnreadCount();
    if (Boolean.FALSE.equals(resp.isOk())) {
      return Result.error("获取未读消息失败");
    }
    return Result.success(backendMessageConverter.dtoToVo(resp.getData()), "获取未读消息成功");
  }

  /**
   * 清楚消息
   *
   * @return void
   */
  @PatchMapping("unread/clear")
  public Result<Void> clearUnread() {
    backendMessageService.clearUnread();
    return Result.successMsg("清除未读消息成功");
  }

  /**
   * 清除具体某条消息
   *
   * @param messageId 消息id
   * @return void
   */
  @PatchMapping("unread/{messageId}/clear")
  public Result<Void> clearSingleUnread(
    @PathVariable(name = "messageId") Integer messageId) {
    backendMessageService.clearSingleUnread(messageId);
    return Result.successMsg("清除单条未读消息成功");
  }

  /**
   * 删除所有消息
   *
   * @return void
   */
  @DeleteMapping("all")
  public Result<Void> deleteAll() {
    backendMessageService.deleteAll();
    return Result.successMsg("删除所有消息成功");
  }

  /**
   * 获取所有消息分页列表
   *
   * @param vo 请求参数
   * @return 消息分页列表
   */
  @PostMapping("all")
  public Result<PagingRespVo<BackendMessageVo>> getAll(@RequestBody PagingReqVo vo) {
    CommonRespDto<PagingRespDto<BackendMessageDto>> resp =
      backendMessageService.getAll(backendMessageConverter.pageVoToDto(vo));
    if (!resp.isOk()) {
      return Result.error("获取消息列表失败");
    }
    return Result.success(backendMessageConverter.pageDtoToPageVo(resp.getData()));
  }

  /**
   * 获取所有未读消息分页列表
   *
   * @param vo 请求参数
   * @return 未读消息分页列表
   */
  @PostMapping("unread/all")
  public Result<PagingRespVo<BackendMessageVo>> getAllUnread(@RequestBody PagingReqVo vo) {
    CommonRespDto<PagingRespDto<BackendMessageDto>> resp =
      backendMessageService.getAllUnread(backendMessageConverter.pageVoToDto(vo));
    if (!resp.isOk()) {
      return Result.error("获取消息列表失败");
    }
    return Result.success(backendMessageConverter.pageDtoToPageVo(resp.getData()));
  }
}

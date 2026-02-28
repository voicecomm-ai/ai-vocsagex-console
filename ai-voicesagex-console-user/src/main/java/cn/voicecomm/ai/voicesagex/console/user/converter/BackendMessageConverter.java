package cn.voicecomm.ai.voicesagex.console.user.converter;


import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UnreadCountDto;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendMessageVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.UnreadCountVo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.MessagePo;
import cn.voicecomm.ai.voicesagex.console.util.vo.PagingReqVo;
import cn.voicecomm.ai.voicesagex.console.util.vo.PagingRespVo;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Mapper(componentModel = "spring")
public interface BackendMessageConverter {

  MessagePo dtoToPo(BackendMessageDto message);


  List<MessagePo> dtoListToPoList(List<BackendMessageDto> backendMessageDtoList);

  List<BackendMessageDto> poListToDtoList(List<MessagePo> messagePos);

  BackendMessageDto poToDto(MessagePo po);

  UnreadCountVo dtoToVo(UnreadCountDto dto);

  PagingRespVo<BackendMessageVo> pageDtoToPageVo(PagingRespDto<BackendMessageDto> pageDto);

  PagingReqDto pageVoToDto(PagingReqVo vo);
}

package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;

import java.util.List;
import java.util.Map;

public interface BackendUserInfoService {

  CommonRespDto<Map<Integer, String>> getUserNameMapByUserIds(List<Integer> userIds);

  CommonRespDto<Map<Integer, String>> getAccountMapByUserIds(List<Integer> userIds);

  CommonRespDto<Map<Integer, String>> getNameMapByUserIds(List<Integer> userIds);

  CommonRespDto<Map<Integer, String>> getUserNameAndAccountMapByUserIds(List<Integer> userIds);
}

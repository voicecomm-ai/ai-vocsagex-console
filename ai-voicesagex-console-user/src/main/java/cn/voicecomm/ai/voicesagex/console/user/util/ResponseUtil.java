package cn.voicecomm.ai.voicesagex.console.user.util;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;

public abstract class ResponseUtil {

  public static <T> Result<T> respToResult(CommonRespDto<T> resp) {
    if (resp.isOk()) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }
}

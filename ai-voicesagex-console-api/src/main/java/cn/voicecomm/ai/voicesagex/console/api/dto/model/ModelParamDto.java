package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型下载Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelParamDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 4877207365195249910L;

  /**
   * 用户id
   */
  private Integer userId;
  /**
   * 模型
   */
  private ModelDto model;
  /**
   * 回调地址
   */
  private String callbackUrl;

  /**
   * 返回的结果
   */
  private ModelBaseResponse response;

}

package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackendMessageVo {

  /**
   * id
   */
  private Integer id;
  /**
   * 用户id
   */
  private Integer userId;
  /**
   * 是否已读
   */
  private Boolean isRead;
  /**
   * 消息类型
   */
  private Integer type;
  /**
   * 消息文本
   */
  private String msg;

  /**
   * 消息文本附加信息
   */
  private String attachment;

  /**
   * 消息附加信息json
   */
  private JSONObject attachmentJson;

  /**
   * 资源路径
   */
  private String downloadPath;

  /**
   * 消息创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;

  /**
   * 消息文本类型
   */
  private Integer msgType;
}

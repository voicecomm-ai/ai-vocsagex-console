package cn.voicecomm.ai.voicesagex.console.api.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackendMessageDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1165845451960199311L;


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
  @Builder.Default
  private Boolean isRead = Boolean.FALSE;

  /**
   * 消息类型
   */
  @Builder.Default
  private Integer type = Type.NOTICE.getValue();

  /**
   * 消息文本
   */
  private String msg;

  /**
   * 消息tag (MQ分类)
   */
  private String msgTag;

  private Integer msgType;

  /**
   * 消息文本附加信息
   */
  private String attachment;

  /**
   * 资源路径
   */
  private String downloadPath;

  /**
   * 消息创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;


  @Getter
  public enum Type {
    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 失败
     */
    FAILURE(1),
    /**
     * 提醒
     */
    WARN(2),
    /**
     * 通知
     */
    NOTICE(3);

    private final int value;

    Type(int value) {
      this.value = value;
    }
  }
}

package cn.voicecomm.ai.voicesagex.console.util.po.user;

import cn.voicecomm.ai.voicesagex.console.util.handler.JsonStringHandler;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@TableName("message")
@NoArgsConstructor
@AllArgsConstructor
public class MessagePo extends BasePo {

  @Serial
  private static final long serialVersionUID = 1080237339510179380L;

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 用户id
   */
  @TableField("user_id")
  private Integer userId;
  /**
   * 是否已读
   */
  @TableField("is_read")
  private Boolean isRead;
  /**
   * 消息类型 0：成功 1：失败 2：提醒 3：通知
   */
  @TableField("type")
  private Integer type;
  /**
   * 消息文本
   */
  @TableField("msg")
  private String msg;

  /**
   * 消息附加信息
   */
  @TableField(value = "\"attachment\"", typeHandler = JsonStringHandler.class)
  private String attachment;

  /**
   * 资源路径
   */
  @TableField("download_path")
  private String downloadPath;

  @TableField("deleted")
  private Boolean deleted;

  @TableField("msg_type")
  private Integer msgType;
}

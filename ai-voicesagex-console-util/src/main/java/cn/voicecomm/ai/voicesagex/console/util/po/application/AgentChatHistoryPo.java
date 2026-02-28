package cn.voicecomm.ai.voicesagex.console.util.po.application;

import cn.voicecomm.ai.voicesagex.console.util.handler.JsonStringHandler;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能体对话历史
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "agent_chat_history")
@Builder
public class AgentChatHistoryPo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;


  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 应用id
   */
  @TableField(value = "app_id")
  private Integer appId;


  /**
   * url key
   */
  @TableField(value = "url_key")
  private String urlKey;

  /**
   * 对话标题
   */
  @TableField(value = "conversation_title")
  private String conversationTitle;

  /**
   * 对话token
   */
  @TableField(value = "conversation_token")
  private String conversationToken;

  /**
   * 对话历史
   */
  @TableField(value = "chat_history", typeHandler = JsonStringHandler.class)
  private String chatHistory;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /**
   * 智能体id
   */
  @TableField(value = "agent_id")
  private Integer agentId;


  /**
   * 上一次聊天时间
   */
  @TableField(value = "last_chat_time")
  private LocalDateTime lastChatTime;


}
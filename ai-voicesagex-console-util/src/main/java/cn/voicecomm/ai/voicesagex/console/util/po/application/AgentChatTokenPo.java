package cn.voicecomm.ai.voicesagex.console.util.po.application;

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
 * 智能体url对话token
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "agent_chat_token")
@Builder
public class AgentChatTokenPo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * token
   */
  @TableField(value = "token")
  private String token;

  /**
   * 应用id
   */
  @TableField(value = "app_id")
  private Integer appId;

  /**
   * 智能体id
   */
  @TableField(value = "agent_id")
  private Integer agentId;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * url key
   */
  @TableField(value = "url_key")
  private String urlKey;
}
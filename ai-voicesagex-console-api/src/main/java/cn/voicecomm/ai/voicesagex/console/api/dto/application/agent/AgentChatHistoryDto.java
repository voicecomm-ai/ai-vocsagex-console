package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 智能体对话历史
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentChatHistoryDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;


  /**
   * id
   */
  private Integer id;

  /**
   * 应用id
   */
  private Integer appId;


  /**
   * url key
   */
  private String urlKey;

  /**
   * 对话标题
   */
  private String conversationTitle;

  /**
   * 对话token
   */
  private String conversationToken;

  /**
   * 对话历史
   */
  private List<ObjectNode> chatHistory;

  /**
   * 创建时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updateTime;

  /**
   * 智能体id
   */
  private Integer agentId;


  /**
   * 上一次聊天时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime lastChatTime;


}
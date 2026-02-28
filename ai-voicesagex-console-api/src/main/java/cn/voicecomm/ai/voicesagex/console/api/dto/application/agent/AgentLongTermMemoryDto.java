package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 智能体长期记忆
 */
@Data
@Accessors(chain = true)
public class AgentLongTermMemoryDto implements Serializable {


  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键id
   */
  private Integer id;

  /**
   * 应用id
   */
  private Integer applicationId;

  /**
   * 使用者id
   */
  private Integer userId;

  /**
   * 创建时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createTime;


  /**
   * 记忆描述
   */
  private String content;

  /**
   * 智能体id
   */
  private Integer agentId;


  /**
   * 智能体id
   */
  private float[] vector;


  /**
   * 数据类型  草稿draft，已发布published，试用experience
   */
  private String dataType;


  /**
   * urlKey
   */
  private String urlKey;

  /**
   * 对话token
   */
  private String token;
}
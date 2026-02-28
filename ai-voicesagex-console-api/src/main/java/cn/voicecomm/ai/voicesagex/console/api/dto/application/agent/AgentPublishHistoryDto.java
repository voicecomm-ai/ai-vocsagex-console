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
 * 智能体-发布历史dto
 */
@Data
@Accessors(chain = true)
public class AgentPublishHistoryDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键
   */
  private Integer id;

  /**
   * 智能体id
   */
  private Integer agentId;

  /**
   * 应用id
   */
  private Integer applicationId;

  /**
   * 发布时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 版本号
   */
  private Integer version;

  /**
   * 发布的数据
   */
  private String configData;

  /**
   * 智能体信息
   */
  private AgentInfoResponseDto agentInfoResponseDto;


}
package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 子智能体信息
 *
 * @author wangf
 * @date 2025/6/3 上午 10:47
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubAgentInfoDto implements Serializable {

  /**
   * id
   */
  private Integer id;

  /**
   * 应用id
   */
  private Integer applicationId;


  /**
   * 应用名称
   */
  private String applicationName;


  /**
   * 应用icon url
   */
  private String applicationIconUrl;


  /**
   * 是否内置
   */
  private Boolean isIntegrated;


  /**
   * 应用描述
   */
  private String applicationDescription;


  /**
   * 发布时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime publishTime;


}
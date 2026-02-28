package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 应用-体验
 */
@Data
@Accessors(chain = true)
public class ApplicationExperienceDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  private Integer id;

  /**
   * 应用类型---智能体agent,工作流应用workflow,智能体编排应用agent_arrangement
   */
  private String type;

  /**
   * 应用名称
   */
  private String name;

  /**
   * 描述
   */
  private String description;

  /**
   * 图标地址
   */
  private String iconUrl;

  /**
   * 创建人
   */
  private Integer createBy;


  /**
   * 创建人用户名
   */
  private String createUsername;

  /**
   * 创建时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 开启工作流追踪
   */
  private Boolean enableWorkflowTrace;

  /**
   * 应用id
   */
  private Integer appId;


  /**
   * 标签List
   */
  private List<ApplicationExperienceTagDto> tagList;


  /**
   * agent类型  single单个，multiple多个
   */
  private String agentType;
}
package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 智能体长期记忆
 */
@Data
@Accessors(chain = true)
public class AgentLongTermMemoryUrlDeleteReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;



  /**
   * urlKey
   */
  @NotEmpty(message = "urlKey不能为空")
  private String urlKey;

  /**
   * 对话token
   */
  @NotEmpty(message = "对话token不能为空")
  private String token;


  /**
   * 记忆ID
   */
  private Integer id;





}
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
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 智能体-体验
 */
@Data
@Accessors(chain = true)
@TableName(value = "agent_experience")
public class AgentExperiencePo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 智能体id
   */
  @TableField(value = "agent_id")
  private Integer agentId;

  /**
   * 应用id
   */
  @TableField(value = "application_id")
  private Integer applicationId;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 配置数据
   */
  @TableField(value = "config_data", typeHandler = JsonStringHandler.class)
  private String configData;
}
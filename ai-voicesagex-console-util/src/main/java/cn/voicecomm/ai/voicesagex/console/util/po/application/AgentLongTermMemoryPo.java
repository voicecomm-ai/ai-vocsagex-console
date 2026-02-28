package cn.voicecomm.ai.voicesagex.console.util.po.application;

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
 * 智能体长期记忆
 */
@Data
@Accessors(chain = true)
@TableName(value = "agent_long_term_memory")
public class AgentLongTermMemoryPo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 应用id
   */
  @TableField(value = "application_id")
  private Integer applicationId;

  /**
   * 使用者id
   */
  @TableField(value = "user_id")
  private Integer userId;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 记忆描述
   */
  @TableField(value = "content")
  private String content;

  /**
   * 智能体id
   */
  @TableField(value = "agent_id")
  private Integer agentId;


  /**
   * 向量
   */
  @TableField(value = "\"vector\"")
  private float[] vector;

  /**
   * 数据类型  草稿draft，已发布published，试用experience
   */
  @TableField(value = "data_type")
  private String dataType;

}
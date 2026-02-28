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
 * 应用api_key
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "application_key")
public class ApplicationKeyPo implements Serializable {

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
   * key创建人
   */
  @TableField(value = "user_id")
  private Integer userId;

  /**
   * 密钥
   */
  @TableField(value = "key_value")
  private String keyValue;



  /**
   * 最后使用时间
   */
  @TableField(value = "last_use_time")
  private LocalDateTime lastUseTime;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 过期时间
   */
  @TableField(value = "expire_time")
  private LocalDateTime expireTime;
}
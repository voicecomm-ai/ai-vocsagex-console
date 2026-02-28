package cn.voicecomm.ai.voicesagex.console.util.po.model;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型密钥
 *
 * @author ryc
 * @date 2025-07-09 09:57:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_api_key")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelApiKeyPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -1395266408120291503L;

  /**
   * 主键id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  /**
   * 模型id
   */
  @TableField(value = "\"model_id\"")
  private Integer modelId;
  /**
   * 密钥
   */
  @TableField(value = "\"secret\"")
  private String secret;
  /**
   * 最后使用时间
   */
  @TableField(value = "\"last_used_time\"")
  private LocalDateTime lastUsedTime;

}

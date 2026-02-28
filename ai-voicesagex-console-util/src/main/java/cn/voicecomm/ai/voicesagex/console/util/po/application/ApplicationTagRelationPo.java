package cn.voicecomm.ai.voicesagex.console.util.po.application;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 应用与标签关联
 *
 * @author wangf
 * @date 2025/5/19 下午 1:42
 */

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "application_tag_relation")
public class ApplicationTagRelationPo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 标签id
   */
  @TableField(value = "tag_id")
  private Integer tagId;

  /**
   * 应用id
   */
  @TableField(value = "application_id")
  private Integer applicationId;
}
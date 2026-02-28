package cn.voicecomm.ai.voicesagex.console.util.po.mcp;

import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * mcp和应用关联
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "mcp_application_relation")
public class McpApplicationRelationPo extends BasePo implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * mcp id
     */
    @TableField(value = "mcp_id")
    private Integer mcpId;

    /**
     * 应用id
     */
    @TableField(value = "application_id")
    private Integer applicationId;
}
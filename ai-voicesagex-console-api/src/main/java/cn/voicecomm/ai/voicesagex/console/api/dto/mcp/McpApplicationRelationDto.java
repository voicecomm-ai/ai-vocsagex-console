package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * mcp和应用关联dto
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class McpApplicationRelationDto extends BaseDto implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * mcp id
     */
    private Integer mcpId;

    /**
     * 应用id
     */
    private Integer applicationId;
}
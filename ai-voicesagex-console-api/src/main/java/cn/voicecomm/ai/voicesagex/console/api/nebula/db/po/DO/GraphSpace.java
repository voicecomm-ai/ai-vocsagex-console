package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;


import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author adminst
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphSpace extends PageBeanDto {

    @Serial
    private static final long serialVersionUID = 2028436106563702736L;

    private String space;
}

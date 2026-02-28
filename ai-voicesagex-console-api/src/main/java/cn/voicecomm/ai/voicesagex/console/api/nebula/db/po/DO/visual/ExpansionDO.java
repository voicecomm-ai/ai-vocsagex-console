package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExpansionDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1926206796504401432L;
    private String spaceId;


    private String vertexInfoVOList;

    private String edgeNameList;

    private String   direction;


    private Integer stepNumber;

}

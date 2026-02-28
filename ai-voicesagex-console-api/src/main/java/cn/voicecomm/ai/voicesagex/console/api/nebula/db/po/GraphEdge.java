package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

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
public class GraphEdge implements Serializable {

    @Serial
    private static final long serialVersionUID = 276563102755955070L;

    private String spaceId;

    private String  edgeName;

    private String properties;

    private String rank;
}

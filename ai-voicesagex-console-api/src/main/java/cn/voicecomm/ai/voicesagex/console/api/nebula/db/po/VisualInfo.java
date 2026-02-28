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
public class VisualInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2590931462550309198L;
    private String subjectId;
    private String objectId;
    private String edgeName;
    private Long rank;


}

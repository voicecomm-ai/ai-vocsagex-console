package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

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
public class GraphEntityRelationDO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1383093891592815264L;
    private String spaceId;
    private String subjectTagName;
    private String subjectName;
    private String objectName;
    private String objectTagName;


}

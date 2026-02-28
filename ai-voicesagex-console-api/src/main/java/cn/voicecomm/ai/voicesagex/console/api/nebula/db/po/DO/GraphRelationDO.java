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
public class GraphRelationDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4464457009400369437L;
    private String spaceId;

    private String entityProperties;

    private String entityValue;

    private  String entityPropertiesBath;


    private String edgeName;


    private String  subjectId;


    private String  objectId;
    private String  rank;

    private String attributeMatch;






    public GraphRelationDO(String spaceId, String subjectId, String objectId, String edgeName) {
        this.spaceId = spaceId;
        this.subjectId = subjectId;
        this.objectId = objectId;
        this.edgeName = edgeName;
    }
}

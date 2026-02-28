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
public class GraphRelationEntityDO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7642936795161561635L;
    private  String  vid ;

    private String spaceId;


    private String tagName;

    private String name;


}

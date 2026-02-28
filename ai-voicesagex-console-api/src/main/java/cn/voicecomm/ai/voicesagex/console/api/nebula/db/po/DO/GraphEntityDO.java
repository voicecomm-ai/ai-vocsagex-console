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
public class GraphEntityDO implements Serializable {


    @Serial
    private static final long serialVersionUID = -3097022707780459556L;
    private String vid ;

    private String spaceId;

    private String entityName;

    private String entityProperties;
    private String entityPropertiesBath;

    private String entityValue;

    private String tagName;

    private String propertyType;

    private  int pageSize;

    private  int current;




}

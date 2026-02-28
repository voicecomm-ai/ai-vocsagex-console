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
public class EntityPropertiesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5201683203511391432L;
    private String propertyName;


    private String propertyValue;



    private String propertyType ;


    private Integer tagRequired;

    private String  defaultValueAsString;

    private String extra ;

}

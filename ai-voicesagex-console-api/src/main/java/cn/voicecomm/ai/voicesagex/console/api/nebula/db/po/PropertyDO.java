package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDO implements Serializable {


    @Serial
    private static final long serialVersionUID = 766792592165865692L;
    private  String propertyType;

    private Integer required;

    private String extra;


    public PropertyDO(String propertyType, Integer required) {
        this.propertyType = propertyType;
        this.required = required;
    }


    public PropertyDO(String propertyType) {
        this.propertyType = propertyType;
    }
}

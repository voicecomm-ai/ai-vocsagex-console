package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphDelAttribute implements Serializable {


    @Serial
    private static final long serialVersionUID = -7935782995802691489L;
    private String space;

    private String attribute;

    private String attributeName;

    private List<String> propertyNameList;
}

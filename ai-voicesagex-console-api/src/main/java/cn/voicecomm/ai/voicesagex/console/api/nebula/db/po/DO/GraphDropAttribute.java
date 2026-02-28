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
public class GraphDropAttribute implements Serializable {


    @Serial
    private static final long serialVersionUID = -1118056214696821771L;
    private String space;

    private String attributeName;

    private String attribute;
}

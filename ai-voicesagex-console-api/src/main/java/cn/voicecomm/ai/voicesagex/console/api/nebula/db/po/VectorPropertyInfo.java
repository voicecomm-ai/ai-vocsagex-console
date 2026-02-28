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
public class VectorPropertyInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1863155119148009166L;
    private String propertyName;

    private String propertyValue;
}

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
public class GraphShowAttribute implements Serializable {

    @Serial
    private static final long serialVersionUID = 5148519519344719969L;
    /**
     * 空间名称
     **/
    private String space;
    /**
     * attributes:  spaces/tags/edges
     **/
    private String attribute;

}

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
public class GraphDeleteVertex implements Serializable {

    @Serial
    private static final long serialVersionUID = -1134883127820913367L;
    /**
     * 空间名称
     **/
    private String space;

    private Object vid;

}

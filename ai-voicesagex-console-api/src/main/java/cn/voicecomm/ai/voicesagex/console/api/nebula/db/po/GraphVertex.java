package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 实体节点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphVertex implements Serializable {

    @Serial
    private static final long serialVersionUID = 2688251189855385595L;
    private  String  vid;


    private String values;


    private String vertexName;
}

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
public class GraphDeleteEdge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1591101211859302488L;
    /**
     * 空间名称
     **/
    private String space;

    private String edgeName;

    private Object srcVid;

    private Object dstVid;

}

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
public class GraphUpdateEdge implements Serializable {

    @Serial
    private static final long serialVersionUID = 9094562028656647108L;
    /**
     * 空间名称
     **/
    private String space;

    private String edgeName;

    private List<String> edgeList;

    private Object srcVid;

    private Object dstVid;

    private List<Object> edgeValueList;
}










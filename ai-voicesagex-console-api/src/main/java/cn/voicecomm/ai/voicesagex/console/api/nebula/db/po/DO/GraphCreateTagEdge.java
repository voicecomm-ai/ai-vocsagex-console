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
public class GraphCreateTagEdge implements Serializable {

    @Serial
    private static final long serialVersionUID = -5492631296633310755L;
    /**
     * 空间名称
     **/
    private String space;

    private String type;

    private String tagEdgeName;

    private String tagEdgeComment;

    private List<PropertyBean> propertyList;

    private String color;
}

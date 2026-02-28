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
public class GraphCreateIndex implements Serializable {

    @Serial
    private static final long serialVersionUID = -452903767068986232L;
    /**
     * 空间名称
     **/
    private String space;

    private String type;

    private String indexName;

    private String tagEdgeName;

    private String comment;

    private List<AttributeBean> attributeBeanList;
}

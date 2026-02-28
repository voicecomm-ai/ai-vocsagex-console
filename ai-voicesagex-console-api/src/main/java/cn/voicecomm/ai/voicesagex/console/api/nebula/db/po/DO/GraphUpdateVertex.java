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
public class GraphUpdateVertex implements Serializable {

    @Serial
    private static final long serialVersionUID = 235702569565761978L;
    /**
     * 空间名称
     **/
    private String space;

    private String tagName;

    private List<String> tagList;
    /**
     * point的key
     **/
    private Object pointKey;

    private List<Object> tagValueList;
}

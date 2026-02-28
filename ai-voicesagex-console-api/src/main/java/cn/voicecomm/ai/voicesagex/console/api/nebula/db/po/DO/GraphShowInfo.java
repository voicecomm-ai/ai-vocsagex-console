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
public class GraphShowInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2548857401524550850L;
    /**
     * 空间名称
     **/
    private String space;

    /**
     * attribute:  tag /edge
     **/
    private String attribute;

    /**
     * attributeName:  tag 名称/edge 名称
     **/
    private String attributeName;

}

package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author adminst
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphTagEdgePropertyDO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -4099738815458223216L;
    /**
     * 空间名称
     **/
    private String space;

    private Long spaceId;

    /**
     * tag /edge name
     */
    private String tagName;

    /**
     * 属性 集合
     */
    private String  properties;

    /**
     * 过期时间
     */
    private String  ttlCol;

    private int ttlDuration;

    /**
     *  0 tag  1 edge
     */
    private  int type ;

    private String propertyName;
    private String propertyType;
    // 是否必填
    private Integer tagRequired;
    private T  defaultValue;
    private String extra;


}

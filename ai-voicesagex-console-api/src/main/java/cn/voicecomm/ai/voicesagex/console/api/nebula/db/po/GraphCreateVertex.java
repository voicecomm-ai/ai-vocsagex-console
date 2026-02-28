package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

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
public class GraphCreateVertex implements Serializable {

    @Serial
    private static final long serialVersionUID = -9203085945410990025L;
    /**
     * 空间名称
     **/
    private String space;

    /**
     * 标签tag名称
     **/
    private String tagName;


    /**
     * 属性 集合
     */
    private String  properties;


    /**
     * 属性值 集合
     */
    private String  values;


    /**
     * vid
     */
    private String  VID;

    private List<Object> tagValueList;

    private List<String> tagList;

    private Object pointKey;





}

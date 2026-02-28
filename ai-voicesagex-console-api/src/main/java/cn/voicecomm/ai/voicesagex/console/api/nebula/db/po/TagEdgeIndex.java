package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

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
/**
 * 创建默认 index
 */
public class TagEdgeIndex implements Serializable {

    @Serial
    private static final long serialVersionUID = -1820628635145946195L;
    /**
     * 空间名称
     **/
    private String space;

    /**
     *  索引名称
     */

    private  String index ;

    /**
     * tag /edge name
     */
    private String tagEdgeName;
}

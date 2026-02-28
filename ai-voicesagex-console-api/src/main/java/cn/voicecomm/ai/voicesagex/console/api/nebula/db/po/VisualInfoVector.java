package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VisualInfoVector implements Serializable {

    @Serial
    private static final long serialVersionUID = -3995652668865014514L;
    private String subjectId;
    private String objectId;
    private String edgeName;
    private Map<String, ValueWrapper> propertyInfos;

}

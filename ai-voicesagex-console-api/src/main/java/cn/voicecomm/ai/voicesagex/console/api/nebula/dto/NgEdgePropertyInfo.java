package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NgEdgePropertyInfo {



    private String srcId;

    private String dstId;

    private String edgeName;


    private Map<String, ValueWrapper> properties;

    // 0 作为src  1 作为 dst
    private  int status;


    private Long rank;

}

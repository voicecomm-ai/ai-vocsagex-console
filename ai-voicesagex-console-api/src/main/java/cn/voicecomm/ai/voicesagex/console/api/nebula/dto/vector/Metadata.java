package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {

    private String source_vid ;

    private List<String> tag_name;


    private String edge_name;

    private  String source_node_name;

    private  String target_node_name;

    private String vector_type;

    private  String target_vid;

    private  String edge_rank;


}

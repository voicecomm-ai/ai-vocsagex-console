package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual;

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
public class VertexInfoDO implements Serializable {


    @Serial
    private static final long serialVersionUID = 3802864379657275198L;
    private String vertexName;


    private String vertexId;

    private List<String> vertexTagName;





}

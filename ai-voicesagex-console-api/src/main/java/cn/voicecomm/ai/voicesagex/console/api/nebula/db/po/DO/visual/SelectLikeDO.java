package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual;

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
public class SelectLikeDO implements Serializable {


    @Serial
    private static final long serialVersionUID = 7326153796834647305L;
    private String spaceId;


    private String vertexName;
}

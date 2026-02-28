package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveRelationDTO {

    private String  spaceId;


    private String subjectId;

    private String subjectName;

    private String objectName;

    private String objectId;

    private String edgeName;

    private String rank ;


    private List<EntityPropertiesVO> entityProperties;

}

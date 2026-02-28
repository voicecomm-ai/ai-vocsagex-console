package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationVectorDTO {

    private  String  subjectId;

    private  String  objectId;

    private String subjectName;

    private String objectName;

    private String rank ;

    private List<EntityPropertiesVO> propertiesVOList;
}

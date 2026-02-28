package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveTagInfoDTO {

    private String tagName;

    private List<EntityPropertiesVO> entityProperties;
}

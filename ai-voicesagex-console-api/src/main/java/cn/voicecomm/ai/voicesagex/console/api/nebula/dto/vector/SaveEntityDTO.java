package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveEntityDTO {

    private String  spaceId;

    private String entityId;

    private String entityName;

    private List<SaveTagInfoDTO> saveTagInfoDTO;



}

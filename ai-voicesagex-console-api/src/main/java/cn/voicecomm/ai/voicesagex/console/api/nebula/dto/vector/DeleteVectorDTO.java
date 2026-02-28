package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteVectorDTO {


    private String  spaceId;


    private List<String> vid ;
}

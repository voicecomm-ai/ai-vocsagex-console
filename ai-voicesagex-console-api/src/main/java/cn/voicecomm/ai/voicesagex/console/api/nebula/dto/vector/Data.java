package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {

    private String id ;

    private Metadata metadata;

    private  String document ;


}

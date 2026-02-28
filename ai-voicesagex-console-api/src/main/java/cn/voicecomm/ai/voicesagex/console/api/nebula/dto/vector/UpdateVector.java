package cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateVector {

  private String collection;

  private List<cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.Data> data;

  private String callbackUrl;
}

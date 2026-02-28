package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

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
public class NgTagEdge implements Serializable {

   @Serial
   private static final long serialVersionUID = -7139804840192497683L;
   private String field;
   private String type;
   private String nullable;


}

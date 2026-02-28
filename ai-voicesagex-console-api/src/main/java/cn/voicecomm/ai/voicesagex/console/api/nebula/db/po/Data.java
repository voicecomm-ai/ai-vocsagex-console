package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Data implements Serializable  {

    @Serial
    private static final long serialVersionUID = -1333384198012725339L;

    String id;

}

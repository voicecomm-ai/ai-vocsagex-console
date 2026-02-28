package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification;

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
public class VerificationInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3871917984581749912L;
    private String  spaceId;

    private String tag;

    private String name;

    private String vid;

    private String entityValue;

}

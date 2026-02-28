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
public class BatchVerificationInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7387792979774811904L;
    private String  spaceId;

    private String tag;

    private String value;

}

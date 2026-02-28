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
public class BatchVerificationRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = -7544124788546684836L;
    private String  spaceId;

    private String edgeName;

    private  String value;

}

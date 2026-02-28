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
public class BatchDocumentInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 25502609629858138L;
    private String  spaceId;

    private String tag;

    private  String name ;

    private String value;

}

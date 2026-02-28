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
public class Link implements Serializable {

    @Serial
    private static final long serialVersionUID = 4004906822031842641L;
    String  source ;

    String target;

    String  value ;

    // 静态变量来跟踪下一个 index


    public void reset() {
        this.source = null;
        this.target = null;
        // 重置其他字段...
    }



}

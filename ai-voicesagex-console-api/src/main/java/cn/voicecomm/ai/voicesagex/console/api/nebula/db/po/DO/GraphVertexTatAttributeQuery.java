package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.hutool.core.util.StrUtil;
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
public class GraphVertexTatAttributeQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = -4603410768864946749L;
    private String space;

    private String tag;

    private String condition;

    private Integer resultSize = Integer.MAX_VALUE;


    public String getCondition() {
        if (StrUtil.isNotBlank(condition)) {
            return " where " + condition;
        }
        return "";
    }
}

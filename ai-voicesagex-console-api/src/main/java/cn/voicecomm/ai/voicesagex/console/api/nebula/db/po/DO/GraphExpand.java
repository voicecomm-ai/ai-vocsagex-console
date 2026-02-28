package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.hutool.core.util.StrUtil;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphExpand implements Serializable {

    @Serial
    private static final long serialVersionUID = -4096962578294447549L;
    private String space;

    private List<String> edgeList;

    private String direction;

    private Integer stepStart;

    private Integer stepEnd;

    private Integer resultSize = Integer.MAX_VALUE;

    private List<Object> vidList;


    private String condition;

    public String getStepEndResult() {
        if (stepEnd != null) {
            return ".." + stepEnd;
        }
        return "";
    }

    public String getVidList(String vidType) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < vidList.size(); i++) {
            Object vid = vidList.get(i);

            if (vidType.contains("STRING")) {
                stringBuffer.append("\"").append(vid).append("\"");
            } else {
                stringBuffer.append(vid);
            }
            if (vidList.size() > 1 && (i + 1) != vidList.size()) {
                stringBuffer.append(",");
            }
        }
        return stringBuffer.toString();
    }

    //  l.degree CONTAINS 1 AND l.min_level == 2
    public String getCondition() {
        if (StrUtil.isNotBlank(condition)) {
            return " AND ALL(l IN e WHERE " + condition + ")";
        }
        return "";
    }
}

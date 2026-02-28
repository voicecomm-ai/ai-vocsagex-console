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
public class GraphVertexPathQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = -2886817864948443476L;
    private String space;

    private String pathType;

    private List<String> edgeList;

    private List<Object> srcVid;

    private List<Object> dstVid;

    private String direct;

    private Integer step;

    private Integer resultSize;

    private String condition;

    public String getEdgeList() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < edgeList.size(); i++) {
            String edge = edgeList.get(i);
            stringBuffer.append("`").append(edge).append("`");
            if (edgeList.size() > 1 && (i + 1) != edgeList.size()) {
                stringBuffer.append(",");
            }
        }
        return stringBuffer.toString();
    }

    public String getCondition() {
        if (StrUtil.isNotBlank(condition)) {
            return "WHERE " + condition;
        }
        return "";
    }
}

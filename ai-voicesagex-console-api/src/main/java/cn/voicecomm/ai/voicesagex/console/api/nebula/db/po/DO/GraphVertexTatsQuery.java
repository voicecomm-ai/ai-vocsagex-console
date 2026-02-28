package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;


import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphVertexTatsQuery extends PageBeanDto {

    @Serial
    private static final long serialVersionUID = -2329303095323746184L;
    private String space;

    //@ApiModelProperty(value = "标签集合", required = false)
    //private List<String> tagList;
    private String tag;

    private Object pointKey;
}

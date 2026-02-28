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
public class GraphPageEdge extends PageBeanDto {

    @Serial
    private static final long serialVersionUID = -2473697974731942320L;
    /**
     * 空间名称
     **/
    private String space;

    private String edge;


    private String vid;
}










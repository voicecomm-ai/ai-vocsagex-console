package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;


import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @Descriptin: 分页参数入参
 * @ClassName: PageBeanDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageBeanDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -7847877741355530332L;
    @Builder.Default
    private Integer pageNum = 1;
    @Builder.Default
    private Integer pageSize = 10;
}

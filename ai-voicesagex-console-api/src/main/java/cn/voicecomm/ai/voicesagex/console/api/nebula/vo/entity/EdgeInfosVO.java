package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoyan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "本体列表",description = "本体列表")
public class EdgeInfosVO {
    @Schema(description = "本体名称",example = "demo")
    private String edgeName;

    /**
     * 空间名称
     **/
    @Schema(description = "图空间id",example = "23434232323" )
    private Long spaceId;


    /**
     * 空间名称
     **/
    @Schema(description = "本体ID",example = "4323232323" )
    private Long edgeId;


    @Schema(description = "包含实体数量",example = "20" )
    private int tagNumber;

    /**
     * 更新时间
     */
    @Schema(description = "操作时间" )
    private String createTime;


}

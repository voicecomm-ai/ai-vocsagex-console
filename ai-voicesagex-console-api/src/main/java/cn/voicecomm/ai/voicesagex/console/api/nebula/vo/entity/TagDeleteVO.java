package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "删除实体记录",description = "删除实体记录")
public class TagDeleteVO {

    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;

    @Schema(description = "实体id集合",example = "")
    private List<String> entityIds;

}

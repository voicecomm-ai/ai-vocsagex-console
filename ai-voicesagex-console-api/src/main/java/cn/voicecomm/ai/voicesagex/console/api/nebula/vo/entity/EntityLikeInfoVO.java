package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "实体列表",description = "实体")
public class EntityLikeInfoVO {


    @Schema(description = "实体名称",example = "test")
    private String entityName;


    @Schema(description = "实体Id",example = "test")
    private String entityId;
}

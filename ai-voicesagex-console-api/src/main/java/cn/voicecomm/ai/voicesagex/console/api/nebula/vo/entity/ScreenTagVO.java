package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "关系筛选tag类型",description = "关系筛选tag类型")
public class ScreenTagVO {

    @Schema(description = "图空间id",example = "34323423")
    @NotNull(message = "图空间不能为空")
    private String  spaceId;

    @Schema(description = "关系名称",example = "test")
    private String edgeName;


    @Schema(description = "是否为主体",example = "true")
    private Boolean isSubject;
}

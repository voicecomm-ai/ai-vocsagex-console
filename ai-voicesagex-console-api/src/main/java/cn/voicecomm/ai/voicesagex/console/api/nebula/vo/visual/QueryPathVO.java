package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "路径查询信息",description = "路径查询信息")
public class QueryPathVO {


    @Schema(description = "图空间id",example = "23434232323" )
    @NotNull(message = "spaceId不能为空")
    private Long spaceId;


    @Schema(description = "起点id",example = "4323232323" )
    @NotNull(message = "startId不能为空")
    private String startId;





    @Schema(description = "终点id",example = "4323232323" )
    @NotNull(message = "endId不能为空")
    private String endId;



    @Schema(description = "选择边")
    private List<String> edgeNameList;


    @Schema(description = "扩展方向" ,example = "0 流入  1 流出  2 双向")
    private Integer  direction;


    @Schema(description = "步数" ,example = "2")
    private Integer  StepInterval = 1;

    @Schema(description = "步数类型" ,example = "2")
    private  Integer queryType;





}

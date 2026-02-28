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
@Schema(name = "获取实体列表详细信息",description = "获取实体列表详细信息")
public class TagPropertyVO {



    @Schema(description = "实体名称",example = "demo")
    private String entityName;


    @Schema(description = "实体名称",example = "demo")
    private String tagName;


    /**
     * 空间名称
     **/
    @Schema(description = "图空间id",example = "23434232323" )
    private Long spaceId;


    /**
     * 空间名称
     **/
    @Schema(description = "本体ID",example = "4323232323" )
    private Long tagId;


    @Schema(description = "pageSize",example = "10")
    private  int pageSize;

    @Schema(description = "current",example = "1")
    private  int current;



}

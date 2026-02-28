package cn.voicecomm.ai.voicesagex.console.api.nebula.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportSuccessVO {

    @Schema(description = "成功条数")
    private int success;
    @Schema(description = "失败条数")
    private int fail;
    @Schema(description = "失败文件下载路径")
    private String url;
    @Schema(description = "总条数")
    private int total;

}

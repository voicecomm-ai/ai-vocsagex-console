package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName KnowledgeExtractionReq
 * @Author wangyang
 * @Date 2025/9/16 15:34
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeExtractionReq extends PagingReqDto {

    @Schema(description = "模糊搜索任务名称",example = "demo" )
    private String jobName;

}

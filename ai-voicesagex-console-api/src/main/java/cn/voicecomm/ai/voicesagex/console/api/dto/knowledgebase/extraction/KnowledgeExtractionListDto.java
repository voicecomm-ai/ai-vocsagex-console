package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName KnowledgeExtractionListDto
 * @Author wangyang
 * @Date 2025/9/16 17:12
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeExtractionListDto {

  @Schema(description = "图空间id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  private Integer spaceId;


  @Schema(description = "入图空间", example = "demo")
  private String spaceName;


  @Schema(description = "任务id", example = "4545343434")
  private Integer jobId;


  @Schema(description = "任务名称", example = "demo")
  private String jobName;

  @Schema(description = "本体名称", example = "tag")
  private List<String> tags;


  @Schema(description = "关系名称", example = "edge")
  private List<String> edges;


  @Schema(description = "包含文档", example = "0")
  private Integer includeDocument;


  @Schema(description = "图空间类型", example = "0")
  private Integer type;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;


  private Integer createBy;

  /**
   * 创建人
   */
  private String createUser;


}

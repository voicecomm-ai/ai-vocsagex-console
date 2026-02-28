package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图知识库本体关系Dto
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TagPatternDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 3719441848803983364L;

  @Schema(description = "tagEdge名称", example = "demo", requiredMode = Schema.RequiredMode.REQUIRED)
  private String tagName;

  @Schema(description = "存活时间", example = "create_time", requiredMode = Schema.RequiredMode.REQUIRED)
  private String ttlCol;

  @Schema(description = "具体存活时间", example = "1 单位/小时", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer ttlDuration;


  private List<KnowledgeGraphTagEdgePropertyDto> patternProperties;


}

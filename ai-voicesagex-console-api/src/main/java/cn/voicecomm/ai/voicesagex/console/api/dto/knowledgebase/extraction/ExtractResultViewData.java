package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解析后的三元组信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractResultViewData {


  /**
   * 实体信息
   */
  @Schema(description = "实体信息", example = "xxxx")
  private List<EntityNode> entities;


  /**
   * 三元组信息
   */
  @Schema(description = "三元组信息", example = "xxxx")
  private List<Triple> triples;


}

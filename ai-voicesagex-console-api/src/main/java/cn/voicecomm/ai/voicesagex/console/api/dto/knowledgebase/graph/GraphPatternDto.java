package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
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
public class GraphPatternDto implements Serializable {


  @Serial
  private static final long serialVersionUID = 5890026017905502038L;

  @Schema(description = "本体数据")
  private Set<cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Data> nodes;

  @Schema(description = "边数据")
  private Set<Link> edges;


  @Schema(description = "上次更新时间")
  private Date updatedTime;

}

package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagType;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型分页列表请求
 *
 * @author ryc
 * @date 2025/6/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgePropertySaveReq extends PagingReqDto {

  @Serial
  private static final long serialVersionUID = -6221335191716038894L;

  /**
   * Tag/Edge id
   */
  private Integer tagEdgeId;
  /**
   * 知识库id
   */
  private Integer spaceId;
  /**
   * 类型  0：Tag；1：Edge
   */
  private Integer type;
  /**
   * Tag/Edge名称
   */
  private String tagName;
  /**
   * 关系名称
   */
  private TagType tagTypes;


}

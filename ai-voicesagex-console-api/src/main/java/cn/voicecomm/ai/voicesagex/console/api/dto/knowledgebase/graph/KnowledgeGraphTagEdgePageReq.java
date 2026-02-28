package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
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
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgePageReq extends PagingReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -5843174501103609963L;
  /**
   * 知识库id
   */
  @NotNull(message = "知识库id不能为空")
  private Integer spaceId;
  /**
   * 类型  0：Tag；1：Edge
   */
  private Integer type;
  /**
   * 名称
   */
  private String tagName;
}

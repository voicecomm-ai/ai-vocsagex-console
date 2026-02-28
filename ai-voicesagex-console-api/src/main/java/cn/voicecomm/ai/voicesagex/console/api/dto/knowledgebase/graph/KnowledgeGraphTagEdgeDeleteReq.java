package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图知识库本体关系删除Dto
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgeDeleteReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 3640921730006139728L;

  /**
   * 主键id
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
   * 名称
   */
  private String tagName;
  /**
   * 描述
   */
  private String description;
  /**
   * ttlCol 字段
   */
  private String ttlCol;
  /**
   * 过期时间（小时为单位）
   */
  private Integer ttlDuration;
  /**
   * 是否删除 false 否 true 删除
   */
  private Boolean deleted;
  /**
   * 属性名称
   */
  private String propertyName;

}

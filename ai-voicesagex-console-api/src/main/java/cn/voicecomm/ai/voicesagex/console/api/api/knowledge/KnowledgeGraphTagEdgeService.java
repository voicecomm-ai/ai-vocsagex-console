package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.EdgePatternDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.GraphPatternDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertySaveReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.TagPatternDto;
import java.util.List;

/**
 * 图知识库本体关系Service
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
public interface KnowledgeGraphTagEdgeService {

  CommonRespDto<List<KnowledgeGraphTagEdgeDto>> getList(KnowledgeGraphTagEdgePageReq pageReq);

  CommonRespDto<PagingRespDto<KnowledgeGraphTagEdgePropertyDto>> getTagEdgeInfo(
      KnowledgeGraphTagEdgePropertyReq tagEdgePropertyReq);

  CommonRespDto<Integer> save(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto);

  CommonRespDto<Boolean> delete(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto);

  CommonRespDto<KnowledgeGraphTagEdgeDto> getTtlCol(KnowledgeGraphTagEdgeReq tagEdgeReq);

  CommonRespDto<Boolean> createTtlCol(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto);

  CommonRespDto<Boolean> dropTtlCol(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto);

  CommonRespDto<List<String>> getAllTtlField(KnowledgeGraphTagEdgeReq tagEdgeReq);

  CommonRespDto<Integer> createTagEdgeProperties(
      KnowledgeGraphTagEdgePropertySaveReq propertySaveReq);

  CommonRespDto<Void> updateProperty(
      KnowledgeGraphTagEdgePropertyDto knowledgeGraphTagEdgePropertyDto);

  CommonRespDto<Boolean> dropTagEdgeProperty(
      KnowledgeGraphTagEdgePropertyDeleteReq propertyDeleteReq);

  CommonRespDto<Boolean> dropAllTagEdgeProperty(
      KnowledgeGraphTagEdgeDeleteReq graphTagEdgeDeleteReq);

  CommonRespDto<Boolean> deleteTagEdgeByKnowledgeId(Integer spaceId);

  CommonRespDto<Boolean> updateGraphPattern(Integer spaceId);

  CommonRespDto<TagPatternDto> getGraphPatternTag(Integer spaceId, String tagName);

  CommonRespDto<EdgePatternDto> getGraphPatternEdge(Integer spaceId, String edgeName);

  CommonRespDto<GraphPatternDto> getGraphPattern(Integer spaceId);

  KnowledgeGraphTagEdgeDto getTagInfo(Long spaceId, String tagName);

  KnowledgeGraphTagEdgeDto getEdgeInfo(Long spaceId, String tagName);

  List<KnowledgeGraphTagEdgeDto> getTagInfos(Long spaceId);

  List<KnowledgeGraphTagEdgeDto> getEdgeInfos(Long spaceId);

  KnowledgeGraphTagEdgePropertyDto getTagEdgeProperty(Long tagEdgeId, int type, String properyName);

  List<KnowledgeGraphTagEdgePropertyDto> getEdgeProperty(Long tagId, int type);
}


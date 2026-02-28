package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;

import com.vesoft.nebula.client.graph.data.ResultSet;

public interface GraphKnowledgeFusionService {

  ResultSet getAllNgEdge(String spaceId, String entityId);


}

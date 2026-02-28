package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import com.vesoft.nebula.client.graph.data.ResultSet;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

public interface GraphKnowledgeFusionManageMapper extends NebulaDaoBasic<Object, String> {


  ResultSet getAllNgEdge(@Param("spaceId") String spaceId, @Param("entityId") String entityId);

}

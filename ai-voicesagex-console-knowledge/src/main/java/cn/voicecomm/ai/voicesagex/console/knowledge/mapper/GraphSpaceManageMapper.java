package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateSpace;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.Map;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

public interface GraphSpaceManageMapper extends NebulaDaoBasic<GraphCreateSpace, String> {

  void createSpace(@Param("space") GraphCreateSpace space);

  void createSpaceDefault(@Param("space") GraphCreateSpace space);


  ResultSet showSpaces();


  void dropSpace(@Param("spaceName") String spaceName);


  Map<String, Object> descSpace(@Param("spaceName") String spaceName);


  void cloneSpace(@Param("space") GraphCreateSpace space);

  void startInit(@Param("spaceId") String spaceId);

}

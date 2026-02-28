package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphKnowledgeFusionService;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphKnowledgeFusionManageMapper;
import com.vesoft.nebula.client.graph.data.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GraphKnowledgeFusionImpl implements GraphKnowledgeFusionService {


  @Autowired
  private GraphKnowledgeFusionManageMapper graphKnowledgeFusionManageMapper;


  @Override
  public ResultSet getAllNgEdge(String spaceId, String entityId) {
    log.info("【Kg-webserver-db  get All ngEdge info and   properties : {}】", spaceId);
    try {
      // 创建 Tag/Edge
      return graphKnowledgeFusionManageMapper.getAllNgEdge(spaceId, entityId);
    } catch (Exception e) {
      log.error("【Error get All ngEdge info and   properties with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ALL_NGEDGE, e);
    }
  }
}

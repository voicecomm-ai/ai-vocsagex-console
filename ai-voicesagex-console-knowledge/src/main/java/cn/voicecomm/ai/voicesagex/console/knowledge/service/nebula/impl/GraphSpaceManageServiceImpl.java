package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Attribute;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateSpace;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgePatterManagerMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphSpaceManageMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.AsyncDocumentSpaceService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphSpaceManageService;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.PatterManagerPo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class GraphSpaceManageServiceImpl implements GraphSpaceManageService {


  @Autowired
  private GraphSpaceManageMapper graphCreateSpaceMapper;


  @Autowired
  private AsyncDocumentSpaceService asyncDocumentSpaceService;

  @Autowired
  private KnowledgePatterManagerMapper patterManagerMapper;


  @Override
  public void createSpace(GraphCreateSpace graphCreateSpace) {
    log.info("【Kg-webServer-db creating graph space info is ：{}】", graphCreateSpace.getSpace());
    // 检验图空间是否存在
    List<String> detailSpaceList = validateSpaceExistsAndGetDetail(graphCreateSpace);
    if (!StringUtil.isBlank(graphCreateSpace.getModel())) {
      graphCreateSpace.setModel(SpaceConstant.SPACE_NAME_FIX + graphCreateSpace.getModel());
      // 是否走图空间clone 逻辑
      cloneOrValidateSpace(graphCreateSpace, detailSpaceList);
    } else {
      try {
        log.info("【Start Creating graph space：  {}】", graphCreateSpace.getSpace());
        createDefaultSpaceOrValidateParams(graphCreateSpace);
        // 文档同空间设置本体客体
        if (SpaceConstant.REPLICA_FACTOR.equals(graphCreateSpace.getType())) {
          asyncDocumentSpaceService.makeAsyncRequest(graphCreateSpace);
        }
      } catch (Exception e) {
        log.error("【Error creating space  Message: {}】", e.getMessage());
        throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_CREATE);
      }
    }

  }


  private List<String> validateSpaceExistsAndGetDetail(GraphCreateSpace graphCreateSpace) {
    log.info("【Check whether the graph space ： {} exist】s", graphCreateSpace.getSpace());
    List<String> detailSpaceList = new ArrayList<>();
    // 校验图空间是否存在
    validateSpaceExists(graphCreateSpace, detailSpaceList);
    return detailSpaceList;
  }

  private void cloneOrValidateSpace(GraphCreateSpace graphCreateSpace,
      List<String> detailSpaceList) {
    log.info("【Check whether the graph space ：  {}  is copied】", graphCreateSpace.getSpace());
    if (!CollectionUtils.isEmpty(detailSpaceList) && detailSpaceList.contains(
        graphCreateSpace.getModel())) {
      graphCreateSpaceMapper.cloneSpace(graphCreateSpace);
    } else {
      log.error("【The target graph space ：  {} already exists】", graphCreateSpace.getModel());
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_TARGET);
    }
  }

  private void createDefaultSpaceOrValidateParams(GraphCreateSpace graphCreateSpace) {
    if (isDefaultSpaceCreationNeeded(graphCreateSpace)) {
      graphCreateSpaceMapper.createSpaceDefault(graphCreateSpace);
    } else {
      validateParams(graphCreateSpace);
    }
  }

  //    集群模式允许设置分片和副本
  private boolean isDefaultSpaceCreationNeeded(GraphCreateSpace graphCreateSpace) {
    return graphCreateSpace.getReplicaFactor().equals(SpaceConstant.INDEX)
        && graphCreateSpace.getPartitionNum().equals(SpaceConstant.INDEX);
  }


  //    集群模式允许设置分片和副本
  private void validateParams(GraphCreateSpace graphCreateSpace) {
    // 后期集群模式允许设置分片和副本
    graphCreateSpaceMapper.createSpace(graphCreateSpace);

  }

  // 校验图空间是否存在
  private void validateSpaceExists(GraphCreateSpace graphCreateSpace,
      List<String> detailSpaceList) {
    // 执行获取所有图空间操作
    processAllSpaces(detailSpaceList);

  }

  // 执行获取所有图空间操作
  public void processAllSpaces(List<String> detailSpaceList) {
    try {
      ResultSet resultSet = graphCreateSpaceMapper.showSpaces();
      if (resultSet.rowsSize() != 0) {
        List<String> columnNames = resultSet.getColumnNames();
        String firstCol = columnNames.get(SpaceConstant.INDEX);
        List<ValueWrapper> valueWrappers = resultSet.colValues(firstCol);
        valueWrappers.forEach((i) -> {
          detailSpaceList.add(ResultSetUtil.getValue(i));
        });
      }
    } catch (Exception e) {
      log.error("【Error geting spaceInfo  Message: {}】", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_CHECK);
    }
  }

  /**
   * 解析所有图空间
   *
   * @param data
   * @param graphCreateSpace
   */
  private void analysisSpaces(String data, GraphCreateSpace graphCreateSpace,
      List<String> detailSpaceList) {
    //解析JSON
    processJSONSpace(data, detailSpaceList);
    if (!detailSpaceList.isEmpty() && detailSpaceList.contains(graphCreateSpace.getSpace())) {
      log.error("The graph space = {} already exists ", graphCreateSpace.getSpace());
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_DUPLICATE);
    }
  }

  //解析JSON
  private void processJSONSpace(String data, List<String> detailSpaceList) {
    log.info("【Kg-webserver-db  process all graph space info  details】");
    JSONObject jsonObject = JSON.parseObject(data);
    final JSONObject error0 = jsonObject.getJSONArray("errors").getJSONObject(SpaceConstant.INDEX);
    final Integer code = error0.getInteger("code");
    if (code != SpaceConstant.INDEX) {
      log.error("【Error graph space details】");
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_ALL);
    }
    JSONArray results = JSONUtil.parseArray(jsonObject.get("results"));
    List<Attribute> spacesList = JSONUtil.toList(results, Attribute.class);
    Attribute attributeVo1 = spacesList.get(SpaceConstant.INDEX);

    for (Attribute.DataBean datum : attributeVo1.getData()) {
      // 查询tgas/edges
      String spaceName = datum.getRow().get(SpaceConstant.INDEX);
      detailSpaceList.add(spaceName);
    }
  }

  @Override
  public List<String> detailSpace() {
    log.info("【Kg-webserver-db  get all graph space details】");
    List<String> detailSpaceList = new ArrayList<>();
    // 执行获取所有图空间操作
    processAllSpaces(detailSpaceList);
    log.info("【Kg-webserver-db  get all space info : {} details】", detailSpaceList);
    return detailSpaceList;
  }


  @Override
  public void dropSpace(String spaceName) {
    log.info("【Kg-webServer-db Trop space ： {} and all data】", spaceName);
    try {
      graphCreateSpaceMapper.dropSpace(spaceName);
    } catch (Exception e) {
      log.error("【Description Failed to switch graph space ： {} error : {}】", spaceName,
          e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_DROP);
    }
  }

  @Override
  public Map<String, Object> descSpace(String spaceName) {
    log.info("【View space: {} details】", spaceName);
    try {
      return graphCreateSpaceMapper.descSpace(spaceName);
    } catch (Exception e) {
      log.error("【The graph space info   = {} error : {}】", spaceName, e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_NAME_CHECK);
    }

  }

  @Override
  public void createGraphPattern(Integer id) {
    log.info("【Mysql save create space pattern  info spaceId : {}】", id);
    PatterManagerPo patterManager = PatterManagerPo.builder()
        .spaceId(id)
        .isFlush(SpaceConstant.INDEX)
        .build();
    patterManagerMapper.insert(patterManager);
    log.info("【insert create space info : {} and pattern  info  】", id);


  }


}

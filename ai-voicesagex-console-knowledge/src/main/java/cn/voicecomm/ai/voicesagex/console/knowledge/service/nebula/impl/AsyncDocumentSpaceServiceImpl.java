package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.AsyncDocumentSpaceService;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeManageMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphTagManageMapper;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.DocumentSpaceTagDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateSpace;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.PropertyDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagEdgeIndex;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author adminst
 */
@Service
@Slf4j
public class AsyncDocumentSpaceServiceImpl implements AsyncDocumentSpaceService {


  // 定义一个静态的 Map
  private static final Map<String, PropertyDO> pageMap = new HashMap<>();
  private static final Map<String, PropertyDO> documentMap = new HashMap<>();

  static {
    // 初始化 Map 数据
    pageMap.put(SpaceConstant.PAGE_CONTENT,
        new PropertyDO("STRING", SpaceConstant.INDEX, SpaceConstant.TXT));
    pageMap.put(SpaceConstant.PAGE_NUMBER, new PropertyDO("INT16", SpaceConstant.INDEX));
    pageMap.put(SpaceConstant.SEGMENT_NUMBERING, new PropertyDO("INT16", SpaceConstant.INDEX));
  }

  static {
    // 初始化 Map 数据
    documentMap.put(SpaceConstant.TOTAL_PAGE, new PropertyDO("INT16", SpaceConstant.INDEX));
    documentMap.put(SpaceConstant.FILE_PATH,
        new PropertyDO("STRING", SpaceConstant.INDEX, SpaceConstant.TXT));
    documentMap.put(SpaceConstant.FORMAT,
        new PropertyDO("STRING", SpaceConstant.INDEX, SpaceConstant.TXT));
    documentMap.put(SpaceConstant.NUMBER_OF_FRAGMENTS,
        new PropertyDO("INT16", SpaceConstant.REPLICA_FACTOR));
  }


  @Autowired
  private GraphTagManageMapper graphTagManageMapper;

  @Autowired
  private GraphEdgeManageMapper graphEdgeManageMapper;


  @Override
  @Async
  public CompletableFuture<Void> makeAsyncRequest(GraphCreateSpace graphCreateSpace) {
    return CompletableFuture.runAsync(() -> {
      try {
        // 延迟两秒后执行
        Thread.sleep(SpaceConstant.WAIT);

        log.info("【开始异步创建文档类型图空间tag和edge：{}】", graphCreateSpace.getSpace());
        // 创建本体
        createDocumentTag(graphCreateSpace, documentMap, SpaceConstant.DOCUMENT);
        createDocumentTag(graphCreateSpace, pageMap, SpaceConstant.FRAGMENT);
        // 创建关系
        createDocumentEdge(graphCreateSpace, SpaceConstant.OWNING_DOCUMENT);
        createDocumentEdge(graphCreateSpace, SpaceConstant.FIRST_STAGE);
        createDocumentEdge(graphCreateSpace, SpaceConstant.NEXT_PARAGRAPH);
      } catch (Exception e) {
        log.error("【Description Failed to create an ontology or relationship asynchronously】", e);
        throw ServiceExceptionUtil.exception(ErrorConstants.CREATE_TAG_EDGE);
      }
    });
  }


  private void createDocumentEdge(GraphCreateSpace graphCreateSpace, String owningDocument) {
    graphEdgeManageMapper.createEdgeDocument(owningDocument, graphCreateSpace.getSpace());

    // 创建索引
    graphEdgeManageMapper.createIndexDefault(
        new TagEdgeIndex(graphCreateSpace.getSpace(), owningDocument + SpaceConstant.FIX_INDEX_NAME,
            owningDocument));

  }

  private void createDocumentTag(GraphCreateSpace graphCreateSpace,
      Map<String, PropertyDO> documentMap, String name) throws InterruptedException {
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    DocumentSpaceTagDO documentSpaceTagDO = new DocumentSpaceTagDO();
    documentSpaceTagDO.setSpaceId(graphCreateSpace.getSpace());
    documentSpaceTagDO.setTagName(name);
    value.add(SpaceConstant.NAME + SpaceConstant.TAG_SPACE + SpaceConstant.STRING);
    for (Map.Entry<String, PropertyDO> entry : documentMap.entrySet()) {
      value.add(SpaceConstant.QUOTATIONMARK + entry.getKey() + SpaceConstant.QUOTATIONMARK
          + SpaceConstant.TAG_SPACE + entry.getValue().getPropertyType());
    }
    documentSpaceTagDO.setPropertyName(value.toString());
    graphTagManageMapper.createDocumentTag(documentSpaceTagDO);

    // 延迟两秒后执行
//        Thread.sleep(SpaceConstant.WAIT);
    // 创建索引 默认以实体名称作为index
    graphTagManageMapper.createIndexDefault(
        new TagEdgeIndex(graphCreateSpace.getSpace(), name + SpaceConstant.FIX_INDEX_NAME, name));
  }

}

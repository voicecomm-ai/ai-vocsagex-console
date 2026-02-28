//package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;
//
//import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.ApiVectorQuantityService;
//import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphVectorInformationService;
//import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
//import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.Data;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.DeleteVectorDTO;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.DropVector;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.Metadata;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveEntityDTO;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveRelationDTO;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveTagInfoDTO;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.UpdateVector;
//import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
//import cn.voicecomm.ai.voicesagex.console.knowledge.util.MD5Util;
//import com.alibaba.fastjson.JSONObject;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import java.util.StringJoiner;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//@Slf4j
//public class ApiVectorQuantityServiceImpl implements ApiVectorQuantityService {
//
//  @Value("${vector.address}")
//  private String address;
//
//
//  @Value("${vector.collection.create}")
//  private String create;
//
//
//  @Value("${vector.collection.upsert_vector}")
//  private String upsertVector;
//
//  @Autowired
//  private RestTemplate restTemplate;
//
//
//  @Value("${vector.collection.drop}")
//  private String drop;
//
//
//  @Value("${vector.collection.callbackUrl}")
//  private String callbackUrl;
//
//  @Value("${vector.collection.deleteVector}")
//  private String deleteVectorUrl;
//
//
//  @Value("${vector.zhipu.api}")
//  private String api;
//
//
//  @Value("${vector.zhipu.vectorApi}")
//  private String vectorApi;
//
//
//  @Value("${vector.collection.deleteEntityWithEdge}")
//  private String deleteEntityWithEdge;
//
//  @Autowired
//  private KnowledgeGraphVectorInformationService vectorInformationService;
//
//
//  @Autowired
//  private ThreadPoolTaskExecutor vectorExecutor;
//
//  @Async
//  @Override
//  public CompletableFuture<Void> makeAsyncCreateOrDropCollection(String collection, Integer type) {
//    return CompletableFuture.runAsync(() -> {
//      try {
//        log.info("【开始调用远程创建/删除图空间存储向量数据库接口：{}】", address + create + drop);
//        JSONObject requestBody = new JSONObject();
//        requestBody.put(SpaceConstant.COLLECTION, collection);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<JSONObject> entity = new HttpEntity<>(requestBody, headers);
//        restTemplate.exchange(address + (type == SpaceConstant.INDEX ? create : drop),
//            HttpMethod.POST, entity, Object.class);
//        log.info("【调用远程创建/删除图空间存储向量数据库接口成功：{}】", address + create + drop);
//      } catch (Exception e) {
//        log.error("【Description Failed to invoke the create collection interface】", e);
//        throw ServiceExceptionUtil.exception(ErrorConstants.CREATE_COLLECTION_INFO);
//      }
//    });
//  }
//
//  @Async
//  @Override
//  public CompletableFuture<Void> makeAsyncCreateEntity(SaveEntityDTO saveEntityDTO) {
//    return CompletableFuture.runAsync(() -> {
//      try {
//        log.info("【开始调用远程创建实体更新向量接口：{}】", address + upsertVector);
//        UpdateVector updateVector = setReqParams(saveEntityDTO);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        String json = objectMapper.writeValueAsString(updateVector);
//        HttpEntity<String> entity = new HttpEntity<>(json, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(address + upsertVector,
//            HttpMethod.POST, entity, String.class);
//
//        log.info("【创建实体调用远程创建更新向量接口成功：{}】", address + upsertVector);
//        String result = (String) JSONObject.parseObject(response.getBody()).get(SpaceConstant.DATA);
//        log.info("【创建实体更新向量回调信息为：{}】", result);
//        // 将任务id存储到mysql中
//        vectorInformationService.saveVectorJobInfo(result,
//            Integer.valueOf(saveEntityDTO.getSpaceId().split("_")[SpaceConstant.REPLICA_FACTOR]));
//      } catch (Exception e) {
//        log.error("【Failed to remotely invoke the update vector interface】", e);
//        throw ServiceExceptionUtil.exception(ErrorConstants.VECTOR_UPDATE_ERROR_INFO);
//      }
//    }, vectorExecutor);
//  }
//
//
//  @Async
//  @Override
//  public CompletableFuture<Void> makeAsyncCreateRelation(SaveRelationDTO saveRelationDTO) {
//    return CompletableFuture.runAsync(() -> {
//      try {
//        log.info("【开始调用远程创建关系更新向量接口：{}】", address + upsertVector);
//        UpdateVector updateVector = setReqParamRelation(saveRelationDTO);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        String json = objectMapper.writeValueAsString(updateVector);
//        // 创建 HttpEntity 对象
//        HttpEntity<String> entity = new HttpEntity<>(json, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(address + upsertVector,
//            HttpMethod.POST, entity, String.class);
//
//        log.info("【创建实体调用远程创建关系更新向量接口成功：{}】", address + upsertVector);
//        String result = (String) JSONObject.parseObject(response.getBody()).get(SpaceConstant.DATA);
//        log.info("【创建关系更新向量回调信息为：{}】", result);
//        // 将任务id存储到mysql中
//        vectorInformationService.saveVectorJobInfo(result,
//            Integer.valueOf(saveRelationDTO.getSpaceId().split("_")[SpaceConstant.REPLICA_FACTOR]));
//      } catch (Exception e) {
//        log.error("【Failed to remotely invoke the update vector interface】", e);
//        throw ServiceExceptionUtil.exception(ErrorConstants.VECTOR_UPDATE_ERROR_INFO);
//      }
//    }, vectorExecutor);
//  }
//
//  @Async
//  @Override
//  public CompletableFuture<Void> makeAsyncDeleteRelationEntity(DeleteVectorDTO deleteVector) {
//    return CompletableFuture.runAsync(() -> {
//      try {
//        if (!CollectionUtils.isEmpty(deleteVector.getVid())) {
//
//          log.info("【开始调用删除向量接口：{}】", address + deleteVectorUrl);
//          HttpHeaders headers = new HttpHeaders();
//          DropVector dropVector = new DropVector();
//          dropVector.setCollection(deleteVector.getSpaceId());
//          dropVector.setIds(deleteVector.getVid());
//          dropVector.setCallbackUrl(callbackUrl);
//          headers.setContentType(MediaType.APPLICATION_JSON);
//          // 创建 HttpEntity 对象
//          ObjectMapper objectMapper = new ObjectMapper();
//          objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//          String json = objectMapper.writeValueAsString(dropVector);
//          HttpEntity<String> entity = new HttpEntity<>(json, headers);
//
//          ResponseEntity<String> response = restTemplate.exchange(address + deleteVectorUrl,
//              HttpMethod.POST, entity, String.class);
//
//          log.info("【调用删除向量接口向量接口成功：{}】", address + deleteVectorUrl);
//          String result = (String) JSONObject.parseObject(response.getBody())
//              .get(SpaceConstant.DATA);
//          log.info("【调用删除向量接口回调信息为：{}】", result);
//          // 将任务id存储到mysql中
//          vectorInformationService.saveVectorJobInfo(result,
//              Integer.valueOf(deleteVector.getSpaceId().split("_")[SpaceConstant.REPLICA_FACTOR]));
//        }
//
//      } catch (Exception e) {
//        log.error("【Remote call to delete vector failed】", e);
//        throw ServiceExceptionUtil.exception(ErrorConstants.VECTOR_DELETE_ERROR_INFO);
//      }
//    }, vectorExecutor);
//  }
//
//  @Async
//  @Override
//  public CompletableFuture<Void> makeAsyncDelete(DeleteVectorDTO deleteVector) {
//    return CompletableFuture.runAsync(() -> {
//      try {
//        if (!CollectionUtils.isEmpty(deleteVector.getVid())) {
//
//          log.info("【开始调用删除实体以及边向量接口：{}】", address + deleteEntityWithEdge);
//          HttpHeaders headers = new HttpHeaders();
//          DropVector dropVector = new DropVector();
//          dropVector.setCollection(deleteVector.getSpaceId());
//          dropVector.setIds(deleteVector.getVid());
//          dropVector.setCallbackUrl(callbackUrl);
//          headers.setContentType(MediaType.APPLICATION_JSON);
//          // 创建 HttpEntity 对象
//          ObjectMapper objectMapper = new ObjectMapper();
//          objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//          String json = objectMapper.writeValueAsString(dropVector);
//          HttpEntity<String> entity = new HttpEntity<>(json, headers);
//          ResponseEntity<String> response = restTemplate.exchange(address + deleteEntityWithEdge,
//              HttpMethod.POST, entity, String.class);
//
//          log.info("【调用删除实体以及边向量接口成功：{}】", address + deleteEntityWithEdge);
//          String result = (String) JSONObject.parseObject(response.getBody())
//              .get(SpaceConstant.DATA);
//          log.info("【调用删除实体以及边回调信息为：{}】", result);
//          // 将任务id存储到mysql中
//          vectorInformationService.saveVectorJobInfo(result,
//              Integer.valueOf(deleteVector.getSpaceId().split("_")[SpaceConstant.REPLICA_FACTOR]));
//        }
//      } catch (Exception e) {
//        log.error("【Remote call to delete vector failed】", e);
//        throw ServiceExceptionUtil.exception(ErrorConstants.VECTOR_DELETE_ERROR);
//      }
//    }, vectorExecutor);
//  }
//
//
//  /**
//   * 设置关系参数
//   *
//   * @param saveRelationDTO
//   * @return
//   */
//  private UpdateVector setReqParamRelation(SaveRelationDTO saveRelationDTO) {
//    log.info("【设置远程调用请求创建关系参数{}】", saveRelationDTO);
//    UpdateVector updateVector = new UpdateVector();
//    updateVector.setCollection(saveRelationDTO.getSpaceId());
//    updateVector.setCallbackUrl(callbackUrl);
//    Data data = new Data();
//    data.setId(MD5Util.generateMD5(saveRelationDTO.getSubjectId() + saveRelationDTO.getEdgeName()
//        + saveRelationDTO.getObjectId()));
//    Metadata metadata = new Metadata();
//    metadata.setVector_type(SpaceConstant.VECTOR_RELATION);
//    metadata.setSource_vid(saveRelationDTO.getSubjectId());
//    metadata.setSource_node_name(saveRelationDTO.getSubjectName());
//    metadata.setTarget_vid(saveRelationDTO.getObjectId());
//    metadata.setTarget_node_name(saveRelationDTO.getObjectName());
//    metadata.setEdge_rank(saveRelationDTO.getRank());
//    metadata.setEdge_name(saveRelationDTO.getEdgeName());
//    data.setMetadata(metadata);
//    if (CollectionUtils.isEmpty(saveRelationDTO.getEntityProperties())) {
//      data.setDocument(
//          saveRelationDTO.getSubjectName() + SpaceConstant.TAG_SPACE + saveRelationDTO.getEdgeName()
//              + SpaceConstant.TAG_SPACE + saveRelationDTO.getObjectName());
//    } else {
//      StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT_CHINESE);
//      for (EntityPropertiesVO property : saveRelationDTO.getEntityProperties()) {
//        joiner.add(
//            property.getPropertyName() + SpaceConstant.COLON_CHINESE + property.getPropertyValue());
//      }
//
//      data.setDocument(
//          saveRelationDTO.getSubjectName() + SpaceConstant.TAG_SPACE + saveRelationDTO.getEdgeName()
//              + SpaceConstant.FIX_TAG_NAME_START_CHINESE + joiner.toString()
//              + SpaceConstant.FIX_TAG_NAME_SUX_FUSION_CHINESE + SpaceConstant.TAG_SPACE
//              + saveRelationDTO.getObjectName());
//    }
//    updateVector.setData(Lists.newArrayList(data));
//    return updateVector;
//  }
//
//  /**
//   * 设置实体参数
//   *
//   * @param saveEntityDTO
//   * @return
//   */
//  private UpdateVector setReqParams(SaveEntityDTO saveEntityDTO) {
//    log.info("【设置远程调用请求创建实体参数{}】", saveEntityDTO);
//    UpdateVector updateVector = new UpdateVector();
//    updateVector.setCollection(saveEntityDTO.getSpaceId());
//    updateVector.setCallbackUrl(callbackUrl);
//    Data data = new Data();
//    data.setId(MD5Util.generateMD5(saveEntityDTO.getEntityId()));
//    Metadata metadata = new Metadata();
//    metadata.setVector_type(SpaceConstant.VECTOR_TAG);
//    metadata.setSource_vid(saveEntityDTO.getEntityId());
//    metadata.setSource_node_name(saveEntityDTO.getEntityName());
//    data.setMetadata(metadata);
//    // 设置document
//    if (!CollectionUtils.isEmpty(saveEntityDTO.getSaveTagInfoDTO())) {
//      metadata.setTag_name(
//          saveEntityDTO.getSaveTagInfoDTO().stream().map(SaveTagInfoDTO::getTagName)
//              .collect(Collectors.toList()));
//      StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT);
//      saveEntityDTO.getSaveTagInfoDTO().stream().forEach(saveTagInfoDTO -> {
//        StringJoiner property = new StringJoiner(SpaceConstant.TAG_SPLIT);
//
//        if (!CollectionUtils.isEmpty(saveTagInfoDTO.getEntityProperties())) {
//          saveTagInfoDTO.getEntityProperties().stream().forEach(entityPropertiesVO -> {
//            property.add(
//                entityPropertiesVO.getPropertyName() + ":" + entityPropertiesVO.getPropertyValue());
//          });
//        }
//        joiner.add(saveTagInfoDTO.getTagName() + ":" + saveEntityDTO.getEntityName()
//            + SpaceConstant.TAG_SPLIT + property.toString());
//      });
//      data.setDocument(joiner.toString());
//    }
//    updateVector.setData(Lists.newArrayList(data));
//
//    return updateVector;
//  }
//
//
//}

package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import static cn.voicecomm.ai.voicesagex.console.application.controller.PromptGenerateController.buildPromptRequest;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentLongTermMemoryService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryListRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.MemoryUpdateRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.MemoryUpdateResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.AgentLongTermMemoryConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentInfoMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentLongTermMemoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentInfoPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentLongTermMemoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 智能体长期记忆服务实现类
 *
 * @author wangf
 */
@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class AgentLongTermMemoryServiceImpl extends
    ServiceImpl<AgentLongTermMemoryMapper, AgentLongTermMemoryPo> implements
    AgentLongTermMemoryService {

  private final AgentLongTermMemoryMapper agentLongTermMemoryMapper;
  private final AgentLongTermMemoryConverter agentLongTermMemoryConverter;
  private final AgentInfoMapper agentInfoMapper;
  private final AgentExperienceMapper agentExperienceMapper;
  private final AgentPublishHistoryMapper agentPublishHistoryMapper;
  private final AgentInfoService agentInfoService;


  @DubboReference
  public ModelService modelService;

  /**
   * 记忆更新向量生成接口
   */
  @Value("${algoUrlPrefix}${chat.memoryEmbedding}")
  private String memoryEmbeddingUrl;

  /**
   * 添加智能体长期记忆
   *
   * @param dto 智能体长期记忆信息
   * @return 添加结果
   */
  @Override
  public CommonRespDto<Integer> add(AgentLongTermMemoryDto dto) {
    log.info("添加智能体长期记忆，智能体ID:{}", dto.getAgentId());
    AgentLongTermMemoryPo po = agentLongTermMemoryConverter.dtoToPo(dto);
    this.save(po);
    return CommonRespDto.success(po.getId());
  }

  /**
   * 批量添加智能体长期记忆
   *
   * @param dtoList 智能体长期记忆信息列表
   * @return 添加结果
   */
  @Override
  @Transactional
  public CommonRespDto<Void> addBatch(List<AgentLongTermMemoryDto> dtoList) {
    log.info("批量添加智能体长期记忆，数量:{}", dtoList.size());
    List<AgentLongTermMemoryPo> poList = agentLongTermMemoryConverter.dtoToPoList(dtoList);
    this.saveBatch(poList);
    return CommonRespDto.success();
  }

  /**
   * 更新智能体长期记忆
   *
   * @param dto 智能体信息
   * @return 更新结果
   */
  @Override
  public CommonRespDto<Void> update(AgentLongTermMemoryDto dto) {
    log.info("更新智能体长期记忆，ID:{}", dto.getId());
    AgentLongTermMemoryPo po = agentLongTermMemoryMapper.selectById(dto.getId());
    if (po == null) {
      return CommonRespDto.error("该记忆不存在");
    }

    AgentLongTermMemoryPo termMemoryPo = agentLongTermMemoryConverter.dtoToPo(dto);

    // 更新向量
    if (StrUtil.isNotBlank(dto.getContent()) && !po.getContent().equals(dto.getContent())) {
      Integer modelId;
      if (ApplicationStatusEnum.PUBLISHED.getKey().equals(dto.getDataType())) {
        CommonRespDto<AgentInfoResponseDto> commonRespDto = agentInfoService.getPublishedInfo(
            po.getApplicationId());
        if (!commonRespDto.isOk() || commonRespDto.getData() == null) {
          return CommonRespDto.error("请先发布智能体");
        }
        modelId = commonRespDto.getData().getModelId();
      } else if (ApplicationStatusEnum.DRAFT.getKey().equals(dto.getDataType())) {
        AgentInfoPo agentInfoPo = agentInfoMapper.selectOne(Wrappers.<AgentInfoPo>lambdaQuery()
            .eq(AgentInfoPo::getApplicationId, po.getApplicationId()));
        modelId = agentInfoPo.getModelId();
      } else {
        CommonRespDto<AgentInfoResponseDto> commonRespDto = agentInfoService.getExperienceInfo(
            po.getApplicationId());
        if (!commonRespDto.isOk() || commonRespDto.getData() == null) {
          return CommonRespDto.error("请先上架智能体");
        }
        modelId = commonRespDto.getData().getModelId();
      }
      // 获取模型信息
      CommonRespDto<ModelDto> modelDtoComm = modelService.getInfo(modelId);
      if (modelDtoComm.getData() != null && BooleanUtil.isTrue(
          modelDtoComm.getData().getIsShelf())) {
        MemoryUpdateRequest request = new MemoryUpdateRequest().setModel_instance_provider("ollama")
            .setContent(dto.getContent());
        JSONObject promptRequestJson = buildPromptRequest(modelDtoComm.getData());
        request.setModel_instance_config(promptRequestJson);
        // http请求
        log.info("记忆更新请求 url：{}，参数：{}", memoryEmbeddingUrl, JSONUtil.toJsonStr(request));
        String post = HttpUtil.post(memoryEmbeddingUrl, JSONUtil.toJsonStr(request));
        MemoryUpdateResponse response = JSONUtil.toBean(post, MemoryUpdateResponse.class);
        log.info("记忆更新请求结果：code: {}, msg: {}, done :{}, usage: {},", response.getCode(),
            response.getMsg(), response.getDone(), response.getUsage());
        if (response.getCode() == 1000) {
          float[] vector = response.getData().getVector();
          termMemoryPo.setVector(vector);
        }
      }
    }
    agentLongTermMemoryMapper.updateById(termMemoryPo);
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> delete(Integer id) {
    agentLongTermMemoryMapper.deleteById(id);
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> clear(Integer applicationId, Integer userId, String type) {
    agentLongTermMemoryMapper.delete(
        new LambdaQueryWrapper<AgentLongTermMemoryPo>().eq(AgentLongTermMemoryPo::getApplicationId,
                applicationId).eq(ObjUtil.isNotNull(userId), AgentLongTermMemoryPo::getUserId, userId)
            .eq(AgentLongTermMemoryPo::getDataType, type));
    return CommonRespDto.success();
  }

  /**
   * 获取智能体长期记忆信息
   *
   * @param id 智能体长期记忆ID
   * @return 智能体长期记忆信息
   */
  @Override
  public CommonRespDto<AgentLongTermMemoryDto> getInfo(Integer id) {
    log.info("获取智能体长期记忆信息，ID:{}", id);
    AgentLongTermMemoryPo po = agentLongTermMemoryMapper.selectById(id);
    if (po == null) {
      return CommonRespDto.error("该记忆不存在", null);
    }
    AgentLongTermMemoryDto dto = agentLongTermMemoryConverter.poToDto(po);
    return CommonRespDto.success(dto);
  }

  @Override
  public CommonRespDto<List<AgentLongTermMemoryListRespDto>> getList(Integer applicationId,
      Integer userId, String type) {
    log.info("获取智能体长期记忆列表，appId:{},userId:{}", applicationId, userId);
    Boolean longTermMemoryEnabled = false;
    String longTermMemoryType;
    Integer longTermMemoryExpired;
    if (ApplicationStatusEnum.EXPERIENCE.getKey().equals(type)) {
      AgentExperiencePo agentExperiencePo = agentExperienceMapper.selectOne(
          Wrappers.<AgentExperiencePo>lambdaQuery()
              .eq(AgentExperiencePo::getApplicationId, applicationId));
      if (ObjUtil.isNull(agentExperiencePo)) {
        log.warn("未找到对应体验应用信息，appId: {}", applicationId);
        return CommonRespDto.error("未找到体验智能体");
      }
      AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
          agentExperiencePo.getConfigData(), AgentInfoResponseDto.class);
      longTermMemoryEnabled = agentInfoResponseDto.getLongTermMemoryEnabled();
      longTermMemoryType = agentInfoResponseDto.getLongTermMemoryType();
      longTermMemoryExpired = agentInfoResponseDto.getLongTermMemoryExpired();

    } else if (ApplicationStatusEnum.DRAFT.getKey().equals(type)) {
      AgentInfoPo agentInfoPo = agentInfoMapper.selectOne(
          Wrappers.<AgentInfoPo>lambdaQuery().eq(AgentInfoPo::getApplicationId, applicationId));
      if (ObjUtil.isNull(agentInfoPo)) {
        log.warn("未找到对应应用信息，appId: {}", applicationId);
        return CommonRespDto.error("未找到智能体");
      }
      longTermMemoryEnabled = agentInfoPo.getLongTermMemoryEnabled();
      longTermMemoryType = agentInfoPo.getLongTermMemoryType();
      longTermMemoryExpired = agentInfoPo.getLongTermMemoryExpired();

    } else {
      AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
          Wrappers.<AgentPublishHistoryPo>lambdaQuery()
              .eq(AgentPublishHistoryPo::getApplicationId, applicationId)
              .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
      if (agentPublishHistoryPo == null) {
        log.warn("未找到对应已发布应用信息，appId: {}", applicationId);
        return CommonRespDto.error("未找到已发布智能体");
      }
      // 转化为DTO对象
      AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
          agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class);
      longTermMemoryEnabled = agentInfoResponseDto.getLongTermMemoryEnabled();
      longTermMemoryType = agentInfoResponseDto.getLongTermMemoryType();
      longTermMemoryExpired = agentInfoResponseDto.getLongTermMemoryExpired();
    }

    if (Boolean.FALSE.equals(longTermMemoryEnabled)) {
      log.warn("当前应用未启用长期记忆功能，appId: {}", applicationId);
      return CommonRespDto.error("当前应用未启用长期记忆功能");
    }

    LambdaQueryWrapper<AgentLongTermMemoryPo> wrapper = new LambdaQueryWrapper<AgentLongTermMemoryPo>().select(
            AgentLongTermMemoryPo::getId, AgentLongTermMemoryPo::getApplicationId,
            AgentLongTermMemoryPo::getUserId, AgentLongTermMemoryPo::getAgentId,
            AgentLongTermMemoryPo::getCreateTime, AgentLongTermMemoryPo::getContent)
        .eq(AgentLongTermMemoryPo::getApplicationId, applicationId)
        .eq(AgentLongTermMemoryPo::getDataType, type)
        .eq(ObjUtil.isNotNull(userId), AgentLongTermMemoryPo::getUserId, userId)
        .orderByDesc(AgentLongTermMemoryPo::getCreateTime);
    List<AgentLongTermMemoryPo> poList;
    if ("custom".equals(longTermMemoryType)) {
      LocalDateTime newExpireTime = LocalDateTime.now().minusDays(longTermMemoryExpired);
      log.info("前置操作--删除过期记忆  时间点:{}", LocalDateTimeUtil.formatNormal(newExpireTime));
      // 删除过期数据
      agentLongTermMemoryMapper.delete(Wrappers.<AgentLongTermMemoryPo>lambdaQuery()
          .eq(AgentLongTermMemoryPo::getApplicationId, applicationId)
          .eq(AgentLongTermMemoryPo::getUserId, userId).eq(AgentLongTermMemoryPo::getDataType, type)
          .lt(AgentLongTermMemoryPo::getCreateTime, newExpireTime));
      poList = agentLongTermMemoryMapper.selectList(wrapper.gt(AgentLongTermMemoryPo::getCreateTime,
          LocalDateTime.now().minusDays(longTermMemoryExpired)));
    } else {
      poList = agentLongTermMemoryMapper.selectList(wrapper);
    }
    List<AgentLongTermMemoryDto> dtoList = agentLongTermMemoryConverter.poToDtoList(poList);
    // 按日期分组并构建结果
    Map<String, List<AgentLongTermMemoryDto>> groupedByDate = new LinkedHashMap<>();
    for (AgentLongTermMemoryDto agentLongTermMemoryDto : dtoList) {
      String dayStr = LocalDateTimeUtil.dayOfWeek(
          agentLongTermMemoryDto.getCreateTime().toLocalDate()).toChinese("周");
      String dateStr = LocalDateTimeUtil.format(agentLongTermMemoryDto.getCreateTime(),
          "yyyy-MM-dd");
      groupedByDate.computeIfAbsent(dateStr + " " + dayStr, k -> new ArrayList<>())
          .add(agentLongTermMemoryDto);
    }

    // 构建最终结果列表
    List<AgentLongTermMemoryListRespDto> result = new ArrayList<>();
    groupedByDate.forEach((date, dataList) -> {
      AgentLongTermMemoryListRespDto respDto = new AgentLongTermMemoryListRespDto();
      respDto.setDate(date);
      respDto.setMemoryList(dataList);
      result.add(respDto);
    });

    return CommonRespDto.success(result);
  }


  @Override
  public void clearExpiredData(Integer applicationId, Integer userId, LocalDateTime expiredTime,
      String type) {
    agentLongTermMemoryMapper.delete(Wrappers.<AgentLongTermMemoryPo>lambdaQuery()
        .eq(AgentLongTermMemoryPo::getApplicationId, applicationId)
        .eq(AgentLongTermMemoryPo::getUserId, userId).eq(AgentLongTermMemoryPo::getDataType, type)
        .lt(AgentLongTermMemoryPo::getCreateTime, expiredTime));
  }
}
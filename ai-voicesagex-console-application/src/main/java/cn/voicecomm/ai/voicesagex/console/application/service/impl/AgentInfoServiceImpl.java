package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import static cn.voicecomm.ai.voicesagex.console.application.controller.PromptGenerateController.buildPromptRequest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse.ApiInterfaceInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ReuseRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ShelfRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentPublishHistoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentDeleteDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentSelectUpdateDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.AgentTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationTypeEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.AgentInfoConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.AgentVariableConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.ApplicationConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.McpConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentInfoMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentLongTermMemoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentVariableMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.KnowledgeBaseApplicationRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpApplicationRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.AgentConfigurationService;
import cn.voicecomm.ai.voicesagex.console.application.service.AgentConfigurationService.AgentValidationResult;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentInfoPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentLongTermMemoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentVariablePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseApplicationRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpApplicationRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author wangf
 * @date 2025/5/19 下午 1:51
 */
@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class AgentInfoServiceImpl extends ServiceImpl<AgentInfoMapper, AgentInfoPo> implements
    AgentInfoService {

  private final AgentInfoConverter agentInfoConverter;
  private final AgentVariableConverter agentVariableConverter;
  private final ApplicationConverter applicationConverter;
  private final McpConverter mcpConverter;


  private final ApplicationMapper applicationMapper;
  private final McpApplicationRelationMapper mcpApplicationRelationMapper;
  private final McpMapper mcpMapper;
  private final McpTagRelationMapper mcpTagRelationMapper;
  private final McpServiceImpl mcpServiceImpl;
  private final AgentLongTermMemoryMapper agentLongTermMemoryMapper;
  private final AgentVariableMapper agentVariableMapper;
  private final AgentExperienceMapper agentExperienceMapper;
  private final AgentPublishHistoryMapper agentPublishHistoryMapper;
  private final ApplicationExperienceMapper applicationExperienceMapper;
  private final ApplicationExperienceTagRelationMapper applicationExperienceTagRelationMapper;
  private final ApplicationExperienceTagMapper applicationExperienceTagMapper;
  private final KnowledgeBaseApplicationRelationMapper knowledgeBaseApplicationRelationMapper;
  private final ApplicationService applicationService;
  private final AgentConfigurationService agentConfigurationService;


  @DubboReference
  public BackendUserService backendUserService;

  @DubboReference
  public KnowledgeBaseService knowledgeBaseService;

  @DubboReference
  public ModelService modelService;
  /**
   * 模型回调地址
   */
  @Value("${invoke.base-url}")
  private String invokeBaseUrl;


  @Value("${algoUrlPrefix}${chat.agentSchema}")
  private String agentSchemaUrl;

  /**
   * mcp检测接口
   */
  @Value("${algoUrlPrefix}${chat.mcpCheck}")
  private String mcpCheckUrl;

  @Override
  public CommonRespDto<Integer> add(AgentInfoDto dto) {
    AgentInfoPo po = agentInfoConverter.dtoToPo(dto);

    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(
        dto.getApplicationId());
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();
    if (AgentTypeEnum.MULTIPLE.getKey().equals(applicationDto.getAgentType())) {
      // 添加内置的应用
      List<Integer> objects = applicationMapper.selectObjs(
          Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
              .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
              .eq(ApplicationPo::getAgentType, AgentTypeEnum.SINGLE.getKey())
              .eq(ApplicationPo::getIsIntegrated, true).ne(ApplicationPo::getStatus, -1));
      po.setSubAgentAppIds(ArrayUtils.toPrimitive(objects.toArray(new Integer[0])));
    }
    baseMapper.insert(po);
    return CommonRespDto.success(po.getId());
  }

  @Override
  public CommonRespDto<Void> update(AgentInfoDto dto) {
    AgentInfoPo oldAgentInfo = getById(dto.getId());
    AgentInfoPo po = agentInfoConverter.dtoToPo(dto);
    int i = baseMapper.updateById(po);
    if ("custom".equals(dto.getLongTermMemoryType()) && dto.getLongTermMemoryExpired() != null && (
        dto.getLongTermMemoryExpired() > oldAgentInfo.getLongTermMemoryExpired())) {
      // 新的过期时间减少时，立即删除过期的数据
      LocalDateTime newExpireTime = LocalDateTime.now().minusDays(dto.getLongTermMemoryExpired());
      log.info("最新过期时间点:{}", LocalDateTimeUtil.formatNormal(newExpireTime));
      agentLongTermMemoryMapper.delete(Wrappers.<AgentLongTermMemoryPo>lambdaQuery()
          .eq(AgentLongTermMemoryPo::getApplicationId, dto.getApplicationId())
          .lt(AgentLongTermMemoryPo::getCreateTime, newExpireTime));
    }
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<AgentInfoResponseDto> getInfo(Integer applicationId) {
    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(applicationId);
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    AgentInfoPo po = baseMapper.selectOne(
        Wrappers.<AgentInfoPo>lambdaQuery().eq(AgentInfoPo::getApplicationId, applicationId));
    if (ObjUtil.isNull(po)) {
      return CommonRespDto.error("数据不存在");
    }
    AgentInfoResponseDto agentInfoResponseDto = agentInfoConverter.poToInfoDto(po);
    // 模型设置
    CommonRespDto<Boolean> info = modelService.isAvailable(po.getModelId());
    if (!info.isOk()) {
      agentInfoResponseDto.setAgentMode("");
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();
    agentInfoResponseDto.setApplicationName(applicationDto.getName())
        .setStatus(applicationDto.getStatus()).setApplicationIconUrl(applicationDto.getIconUrl())
        .setAgentType(applicationDto.getAgentType())
        .setIsIntegrated(applicationDto.getIsIntegrated())
        .setApplicationDescription(applicationDto.getDescription());
    // 查出应用id对应所有mcp应用关系
    List<McpApplicationRelationPo> mcpAppRelationList = mcpApplicationRelationMapper.selectList(
        Wrappers.<McpApplicationRelationPo>lambdaQuery()
            .eq(McpApplicationRelationPo::getApplicationId, applicationId)
            .orderByDesc(BasePo::getCreateTime));
    if (CollUtil.isNotEmpty(mcpAppRelationList)) {
      List<Integer> mcpIds = mcpAppRelationList.stream().map(McpApplicationRelationPo::getMcpId)
          .toList();
      List<McpPo> mcpPoList = mcpMapper.selectList(
          Wrappers.<McpPo>lambdaQuery().in(McpPo::getId, mcpIds).eq(McpPo::getIsShelf, true)
              .last("ORDER BY ARRAY_POSITION(ARRAY[" + StrUtil.join(",", mcpIds) + "], id)"));
      mcpIds = mcpPoList.stream().map(McpPo::getId).toList();
      if (CollUtil.isNotEmpty(mcpIds)) {
        List<McpDto> mcpDtoList = mcpConverter.poListToDtoList(mcpPoList);
        List<McpTagRelationPo> mcpTagRelationPoList = mcpTagRelationMapper.selectList(
            Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getMcpId, mcpIds));
        mcpServiceImpl.mcpTagGroupAndSet(mcpTagRelationPoList, mcpDtoList);
        agentInfoResponseDto.setMcpList(mcpDtoList);
      }
    }
    List<SubAgentInfoDto> subAgentAppList = new ArrayList<>();
    if (ArrayUtils.isNotEmpty(agentInfoResponseDto.getSubAgentAppIds())) {
      List<ApplicationPo> applicationPos = applicationMapper.selectList(
          Wrappers.<ApplicationPo>lambdaQuery().in(ApplicationPo::getId,
              Arrays.stream(ArrayUtils.toObject(agentInfoResponseDto.getSubAgentAppIds()))
                  .toList()));
      // 转为map
      Map<Integer, ApplicationPo> applicationPoMap = applicationPos.stream()
          .collect(Collectors.toMap(ApplicationPo::getId, v -> v));
      for (int subAgentAppId : agentInfoResponseDto.getSubAgentAppIds()) {
        CommonRespDto<AgentInfoResponseDto> agentInfoResp = this.getPublishedInfo(subAgentAppId);
        if (!agentInfoResp.isOk() || Objects.isNull(agentInfoResp.getData())) {
          continue;
        }
        AgentInfoResponseDto subAgentData = agentInfoResp.getData();
        subAgentAppList.add(SubAgentInfoDto.builder().id(subAgentData.getId())
            .applicationId(subAgentData.getApplicationId())
            .applicationName(subAgentData.getApplicationName())
            .applicationDescription(subAgentData.getApplicationDescription())
            .applicationIconUrl(subAgentData.getApplicationIconUrl())
            .isIntegrated(applicationPoMap.get(subAgentAppId).getIsIntegrated())
            .publishTime(subAgentData.getCreateTime()).build());
      }
      agentInfoResponseDto.setSubAgentAppList(subAgentAppList);
    }
    return CommonRespDto.success(agentInfoResponseDto);

  }


  @Override
  @Transactional
  public CommonRespDto<Void> onShelf(ShelfRequest dto) {
    log.info("上架智能体:{}", dto);
    CommonRespDto<AgentInfoResponseDto> commonRespDto = getInfo(dto.getAppId());
    if (!commonRespDto.isOk()) {
      return CommonRespDto.error(commonRespDto.getMsg());
    }
    AgentInfoResponseDto agentInfoResponseDto = commonRespDto.getData();
    if (agentInfoResponseDto.getModelId() == null || agentInfoResponseDto.getModelId() == 0) {
      return CommonRespDto.error("请选择模型");
    }
    CommonRespDto<ModelDto> modelDtoComm = modelService.getInfo(agentInfoResponseDto.getModelId());
    if (modelDtoComm.getData() != null && BooleanUtil.isFalse(
        modelDtoComm.getData().getIsShelf())) {
      log.warn("模型不可用，modelId: {}", agentInfoResponseDto.getModelId());
      return CommonRespDto.error("请选择模型");
    }

    // 查询变量列表
    List<AgentVariablePo> list = agentVariableMapper.selectList(
        Wrappers.<AgentVariablePo>lambdaQuery()
            .eq(AgentVariablePo::getApplicationId, dto.getAppId())
            .orderByAsc(BasePo::getCreateTime));
    agentInfoResponseDto.setVariableList(agentVariableConverter.poToDtoList(list));

    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(dto.getAppId());
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();

    // 知识库id list
    List<KnowledgeBaseDto> knowledgeBaseDtoList = knowledgeBaseService.getApplicationKnowledgeBases(
        dto.getAppId()).getData();
    if (CollUtil.isNotEmpty(knowledgeBaseDtoList)) {
      agentInfoResponseDto.setKnowledgeBaseDtoList(knowledgeBaseDtoList);
    }

    if (AgentTypeEnum.SINGLE.getKey().equals(applicationDto.getAgentType())) {
      // 验证知识库 复用AgentConfigurationService的核心验证逻辑
      AgentValidationResult validationResult =
          agentConfigurationService.validateKnowledgeBaseConfigs(knowledgeBaseDtoList,
              agentInfoResponseDto.getApplicationName());

      // 如果有验证错误，直接返回
      if (validationResult.hasErrors()) {
        // 手动回滚事务·
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return CommonRespDto.error(validationResult.getErrors().getFirst());
      }
      //执行MCP服务可用性检查
      AgentValidationResult agentValidationResult = null;
      try {
        agentValidationResult = agentConfigurationService.checkMcpServicesAvailability(
            agentInfoResponseDto.getMcpList());
      } catch (IOException | InterruptedException e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return CommonRespDto.error("MCP检查异常");
      }

      // 处理检查结果
      if (agentValidationResult.hasErrors()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return CommonRespDto.error(
            StrUtil.join("、", agentValidationResult.getErrors()) + "MCP不可用");
      }
    }

    // 转化为json存储
    String jsonStr = JacksonUtil.toJsonStr(agentInfoResponseDto);

    // 旧的记忆删除
    agentLongTermMemoryMapper.delete(Wrappers.<AgentLongTermMemoryPo>lambdaQuery()
        .eq(AgentLongTermMemoryPo::getDataType, ApplicationStatusEnum.EXPERIENCE.getKey())
        .eq(AgentLongTermMemoryPo::getApplicationId, dto.getAppId()));
    // 旧的experience查询
    ApplicationExperiencePo oldExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, dto.getAppId()));
    if (ObjUtil.isNotNull(oldExperiencePo)) {
      // 旧的分类关联删除
      applicationExperienceTagRelationMapper.delete(
          Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
              .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                  oldExperiencePo.getId()));
    }

    // 旧的applicationExperience删除
    applicationExperienceMapper.delete(Wrappers.<ApplicationExperiencePo>lambdaQuery()
        .eq(ApplicationExperiencePo::getAppId, dto.getAppId()));
    // 旧的agentExperience删除
    agentExperienceMapper.delete(Wrappers.<AgentExperiencePo>lambdaQuery()
        .eq(AgentExperiencePo::getApplicationId, dto.getAppId()));

    // applicationExperienceMapper插入新的
    ApplicationExperiencePo newAppExperiencePo = applicationConverter.appDtoToExePo(applicationDto)
        .setAppId(dto.getAppId());
    newAppExperiencePo.setId(null);
    newAppExperiencePo.setCreateTime(LocalDateTime.now());
    newAppExperiencePo.setCreateBy(UserAuthUtil.getUserId());
    applicationExperienceMapper.insert(newAppExperiencePo);
    agentExperienceMapper.insert(new AgentExperiencePo().setAgentId(agentInfoResponseDto.getId())
        .setApplicationId(dto.getAppId()).setConfigData(jsonStr));
    // 插入新选中的分类
    if (CollUtil.isNotEmpty(dto.getTagIdList())) {
      List<ApplicationExperienceTagRelationPo> relationPoList = dto.getTagIdList().stream().map(
          tagId -> new ApplicationExperienceTagRelationPo().setTagId(tagId)
              .setExperienceApplicationId(newAppExperiencePo.getId())).toList();
      MybatisPlusUtil<ApplicationExperienceTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          applicationExperienceTagRelationMapper, ApplicationExperienceTagRelationPo.class);
      mybatisPlusUtil.saveBatch(relationPoList, relationPoList.size());
    }
    return CommonRespDto.success();
  }

  @Override
  @Transactional
  public CommonRespDto<Void> offShelf(Integer appId) {
    log.info("下架智能体:{}", appId);

    // 旧的experience查询
    ApplicationExperiencePo oldExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    if (ObjUtil.isNotNull(oldExperiencePo)) {
      // 旧的分类关联删除
      applicationExperienceTagRelationMapper.delete(
          Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
              .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                  oldExperiencePo.getId()));
    }

    // 旧的记忆删除
    agentLongTermMemoryMapper.delete(Wrappers.<AgentLongTermMemoryPo>lambdaQuery()
        .eq(AgentLongTermMemoryPo::getDataType, ApplicationStatusEnum.EXPERIENCE.getKey())
        .eq(AgentLongTermMemoryPo::getApplicationId, appId));

    // agentExperience删除
    agentExperienceMapper.delete(
        Wrappers.<AgentExperiencePo>lambdaQuery().eq(AgentExperiencePo::getApplicationId, appId));

    // applicationExperienceMapper删除
    applicationExperienceMapper.delete(Wrappers.<ApplicationExperiencePo>lambdaQuery()
        .eq(ApplicationExperiencePo::getAppId, appId));
    return CommonRespDto.success();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<String> publish(Integer appId) {
    log.info("发布智能体:{}", appId);
    CommonRespDto<AgentInfoResponseDto> commonRespDto = getInfo(appId);
    if (!commonRespDto.isOk()) {
      return CommonRespDto.error(commonRespDto.getMsg());
    }
    AgentInfoResponseDto agentInfoResponseDto = commonRespDto.getData();
    if (agentInfoResponseDto.getModelId() == null || agentInfoResponseDto.getModelId() == 0) {
      return CommonRespDto.error("请选择模型");
    }
    CommonRespDto<ModelDto> modelDtoComm = modelService.getInfo(agentInfoResponseDto.getModelId());
    if (modelDtoComm.getData() != null && BooleanUtil.isFalse(
        modelDtoComm.getData().getIsShelf())) {
      log.warn("模型不可用，modelId: {}", agentInfoResponseDto.getModelId());
      return CommonRespDto.error("请选择模型");
    }
    // 查询变量列表
    List<AgentVariablePo> list = agentVariableMapper.selectList(
        Wrappers.<AgentVariablePo>lambdaQuery().eq(AgentVariablePo::getApplicationId, appId)
            .orderByAsc(BasePo::getCreateTime));
    agentInfoResponseDto.setVariableList(agentVariableConverter.poToDtoList(list));
    ApplicationDto applicationDto = applicationService.getById(appId).getData();
    // 知识库id list
    List<KnowledgeBaseDto> knowledgeBaseDtoList = knowledgeBaseService.getApplicationKnowledgeBases(
        appId).getData();
    if (CollUtil.isNotEmpty(knowledgeBaseDtoList)) {
      agentInfoResponseDto.setKnowledgeBaseDtoList(knowledgeBaseDtoList);
    }

    // 单智能体发布时，请求schema
    if (AgentTypeEnum.SINGLE.getKey().equals(applicationDto.getAgentType())) {
      // 发布请求
      CommonRespDto<String> agentPublishRequest = agentPublishRequest(agentInfoResponseDto);
      if (!agentPublishRequest.isOk()) {
        // 手动回滚事务
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return CommonRespDto.error(agentPublishRequest.getMsg());
      }
      agentInfoResponseDto.setAgentSchema(agentPublishRequest.getData());
    }

    // 转化为json存储
    String jsonStr = JacksonUtil.toJsonStr(agentInfoResponseDto);
    // 当前最大版本号
    AgentPublishHistoryPo maxVersion = agentPublishHistoryMapper.selectOne(
        Wrappers.<AgentPublishHistoryPo>lambdaQuery()
            .eq(AgentPublishHistoryPo::getApplicationId, appId)
            .orderByDesc(AgentPublishHistoryPo::getVersion).last("LIMIT 1"));
    Integer nextVersion = maxVersion == null ? 1 : maxVersion.getVersion() + 1;
    agentPublishHistoryMapper.insert(
        new AgentPublishHistoryPo().setAgentId(agentInfoResponseDto.getId()).setApplicationId(appId)
            .setConfigData(jsonStr).setVersion(nextVersion));

    applicationMapper.updateById(
        ApplicationPo.builder().apiAccessable(Boolean.TRUE).urlAccessable(Boolean.TRUE).id(appId)
            .status(1).build());
    if (StrUtil.isBlank(applicationDto.getUrlKey())) {
      CommonRespDto<String> respDto = applicationService.regenerateUrl(appId);
      if (!respDto.isOk()) {
        // 手动回滚事务
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return CommonRespDto.error(respDto.getMsg());
      } else {
        return CommonRespDto.success(respDto.getData());
      }
    }
    return CommonRespDto.success(applicationDto.getUrlKey());
  }


  /**
   * 智能体发布请求
   *
   * @return 响应结果
   */
  private CommonRespDto<String> agentPublishRequest(AgentInfoResponseDto agentInfo) {
    if (StrUtil.isBlank(agentInfo.getApplicationName())) {
      return CommonRespDto.error("请填写智能体名称");
    }
    if (StrUtil.isBlank(agentInfo.getApplicationDescription())) {
      return CommonRespDto.error("请填写智能体描述");
    }
    JSONObject reqJson = JSONUtil.createObj();

    // 模型配置
    if (!checkAndSetModelConfig(reqJson, agentInfo)) {
      return CommonRespDto.error("模型获取错误");
    }
    // 用户输入配置
    setUserInputConfig(reqJson, agentInfo);

    // 长期记忆配置
    setLongtermConfig(reqJson, agentInfo);

    // mcp 配置
    String checkAndConfigureMcpServices = agentConfigurationService.publishCheckAndConfigureMcpServices(
        reqJson, agentInfo);
    if (StrUtil.isNotBlank(checkAndConfigureMcpServices)) {
      return CommonRespDto.error(checkAndConfigureMcpServices);
    }

    // 知识库配置
    String checkAndCOnfigureKnowledgeBase = agentConfigurationService.publishCheckAndConfigureKnowledgeBase(
        reqJson, agentInfo);
    if (StrUtil.isNotBlank(checkAndCOnfigureKnowledgeBase)) {
      return CommonRespDto.error(checkAndCOnfigureKnowledgeBase);
    }

    log.info("智能体发布请求 url：{}，参数：{}", agentSchemaUrl, JSONUtil.toJsonStr(reqJson));
    String post = HttpUtil.post(agentSchemaUrl, JSONUtil.toJsonStr(reqJson));
    log.info("智能体发布请求执行结果：{}", post);
    JSONObject jsonObject = JSONUtil.parseObj(post);
    if (jsonObject.getInt("code") != 1000) {
      return CommonRespDto.error(jsonObject.getStr("msg"));
    }
    return CommonRespDto.success(JSONUtil.getByPath(jsonObject, "data.schema_str", ""));
  }


  @Override
  public CommonRespDto<List<AgentPublishHistoryDto>> publishHistoryList(Integer appId) {
    List<AgentPublishHistoryPo> list = agentPublishHistoryMapper.selectList(
        Wrappers.<AgentPublishHistoryPo>lambdaQuery()
            .select(AgentPublishHistoryPo::getId, AgentPublishHistoryPo::getVersion,
                AgentPublishHistoryPo::getCreateTime, AgentPublishHistoryPo::getAgentId,
                AgentPublishHistoryPo::getApplicationId)
            .eq(AgentPublishHistoryPo::getApplicationId, appId)
            .orderByDesc(AgentPublishHistoryPo::getVersion));
    List<AgentPublishHistoryDto> historyDtoList = agentInfoConverter.publishDataPoToDtoList(list);
    return CommonRespDto.success(historyDtoList);
  }

  @Override
  public CommonRespDto<AgentInfoResponseDto> getExperienceInfo(Integer appId) {
    log.info("获取体验智能体:{}", appId);
    AgentExperiencePo agentExperiencePo = agentExperienceMapper.selectOne(
        Wrappers.<AgentExperiencePo>lambdaQuery().eq(AgentExperiencePo::getApplicationId, appId));
    if (agentExperiencePo == null) {
      return CommonRespDto.success();
    }
    // 转化为DTO对象
    AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
        agentExperiencePo.getConfigData(), AgentInfoResponseDto.class);
    // 查询应用信息
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    // 设置应用信息
    agentInfoResponseDto.setApplicationName(applicationExperiencePo.getName())
        .setApplicationIconUrl(applicationExperiencePo.getIconUrl());
    // 设置分类tagList
    List<Integer> tagIdList = applicationExperienceTagRelationMapper.selectObjs(
        Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
            .select(ApplicationExperienceTagRelationPo::getTagId)
            .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                applicationExperiencePo.getId()));
    if (CollUtil.isNotEmpty(tagIdList)) {
      List<ApplicationExperienceTagPo> tagPoList = applicationExperienceTagMapper.selectList(
          Wrappers.<ApplicationExperienceTagPo>lambdaQuery()
              .in(ApplicationExperienceTagPo::getId, tagIdList));
      agentInfoResponseDto.setTagList(applicationConverter.tagPoToDtoList(tagPoList));
    }
    // 设置用户名
    BackendUserDto backendUserDto = backendUserService.getUserInfo(
        applicationExperiencePo.getCreateBy()).getData();
    agentInfoResponseDto.setCreateUsername(backendUserDto.getUsername());
    agentInfoResponseDto.setApplicationDescription(applicationExperiencePo.getDescription());
    return CommonRespDto.success(agentInfoResponseDto);
  }

  @Override
  public CommonRespDto<AgentInfoResponseDto> getPublishedInfo(Integer appId) {
    AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
        Wrappers.<AgentPublishHistoryPo>lambdaQuery()
            .eq(AgentPublishHistoryPo::getApplicationId, appId)
            .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
    if (agentPublishHistoryPo == null) {
      return CommonRespDto.error("未找到已发布智能体");
    }
    // 转化为DTO对象
    AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
        agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class);
    return CommonRespDto.success(agentInfoResponseDto);

  }


  @Override
  @Transactional
  public CommonRespDto<Integer> reuse(ReuseRequest request) {
    Integer appId = request.getAppId();
    log.info("复用体验智能体:{}", appId);
    // 查询体验应用信息
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    if (applicationExperiencePo == null) {
      return CommonRespDto.error("未找到体验智能体");
    }
    // 查询体验智能体
    AgentExperiencePo agentExperiencePo = agentExperienceMapper.selectOne(
        Wrappers.<AgentExperiencePo>lambdaQuery().eq(AgentExperiencePo::getApplicationId, appId));
    if (agentExperiencePo == null) {
      return CommonRespDto.error("未找到体验智能体");
    }
    Integer userId = UserAuthUtil.getUserId();
    // 插入应用
    ApplicationPo newApplicationPo = ApplicationPo.builder().name(request.getName())
        .description(request.getDescription()).iconUrl(applicationExperiencePo.getIconUrl())
        .type(applicationExperiencePo.getType()).agentType(applicationExperiencePo.getAgentType())
        .createBy(userId).updateBy(userId).build();
    applicationMapper.insert(newApplicationPo);
    // 转化为DTO对象
    AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
        agentExperiencePo.getConfigData(), AgentInfoResponseDto.class);
    // 转化智能体po
    AgentInfoPo newAgentInfoPo = agentInfoConverter.dtoToInfoPo(agentInfoResponseDto);
    newAgentInfoPo.setCreateBy(userId);
    newAgentInfoPo.setCreateTime(LocalDateTime.now());
    newAgentInfoPo.setUpdateTime(LocalDateTime.now());
    newAgentInfoPo.setApplicationId(newApplicationPo.getId());
    newAgentInfoPo.setId(null);
    baseMapper.insert(newAgentInfoPo);
    // 处理变量
    if (CollUtil.isNotEmpty(agentInfoResponseDto.getVariableList())) {
      agentInfoResponseDto.getVariableList().stream().map(variable -> {
        AgentVariablePo agentVariablePo = agentVariableConverter.dtoToPo(variable);
        agentVariablePo.setApplicationId(newApplicationPo.getId());
        agentVariablePo.setCreateTime(LocalDateTime.now());
        agentVariablePo.setUpdateTime(LocalDateTime.now());
        agentVariablePo.setId(null);
        return agentVariablePo;
      }).forEach(agentVariableMapper::insert);
    }
    // 处理mcp列表
    if (CollUtil.isNotEmpty(agentInfoResponseDto.getMcpList())) {
      agentInfoResponseDto.getMcpList().stream().map(
              mcp -> McpApplicationRelationPo.builder().mcpId(mcp.getId())
                  .applicationId(newApplicationPo.getId()).createTime(LocalDateTime.now())
                  .updateTime(LocalDateTime.now()).build())
          .forEach(mcpApplicationRelationMapper::insert);
    }
    // 处理知识
    if (CollUtil.isNotEmpty(agentInfoResponseDto.getKnowledgeBaseDtoList())) {
      List<KnowledgeBaseApplicationRelationPo> list = agentInfoResponseDto.getKnowledgeBaseDtoList()
          .stream().map(e -> {
            KnowledgeBaseApplicationRelationPo knowledgeBaseApplicationRelationPo = new KnowledgeBaseApplicationRelationPo();
            knowledgeBaseApplicationRelationPo.setKnowledgeBaseId(e.getId());
            knowledgeBaseApplicationRelationPo.setApplicationId(newApplicationPo.getId());
            return knowledgeBaseApplicationRelationPo;
          }).toList();
      MybatisPlusUtil<KnowledgeBaseApplicationRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          knowledgeBaseApplicationRelationMapper, KnowledgeBaseApplicationRelationPo.class);
      mybatisPlusUtil.saveBatch(list, list.size());
    }
    return CommonRespDto.success(newApplicationPo.getId());
  }


  @Override
  public CommonRespDto<ApiAccessResponse> apiAccess(Integer applicationId) {

    ApiAccessResponse apiAccessResponse = ApiAccessResponse.builder().build();
    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(applicationId);
    if (!dtoCommonRespDto.isOk()) {
      apiAccessResponse.setApiInterfaceInfoList(new ArrayList<>());
      return CommonRespDto.success(apiAccessResponse);
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();

    List<ApiInterfaceInfo> interfaceInfoList;
    if (AgentTypeEnum.SINGLE.getKey().equals(applicationDto.getAgentType())) {
      interfaceInfoList = List.of(ApiInterfaceInfo.builder().apiInterfaceUrl(
              invokeBaseUrl + "voicesagex-console/application-web/api/getAgentParameters")
          .apiInterfaceName("获取应用参数").build(), ApiInterfaceInfo.builder()
          .apiInterfaceUrl(invokeBaseUrl + "voicesagex-console/application-web/api/agentChatTest")
          .apiInterfaceName("发送对话消息").build());
    } else {
      interfaceInfoList = List.of(ApiInterfaceInfo.builder().apiInterfaceUrl(
              invokeBaseUrl + "voicesagex-console/application-web/api/getMultipleAgentParameters")
          .apiInterfaceName("获取应用参数").build(), ApiInterfaceInfo.builder().apiInterfaceUrl(
              invokeBaseUrl + "voicesagex-console/application-web/api/multipleAgentChatTest")
          .apiInterfaceName("发送对话消息").build());
    }
    apiAccessResponse.setApiInterfaceInfoList(interfaceInfoList);
    return CommonRespDto.success(apiAccessResponse);
  }


  @Override
  public CommonRespDto<Void> deleteSubAgent(SubAgentDeleteDto deleteDto) {
    AgentInfoPo agentInfoPo = baseMapper.selectOne(Wrappers.<AgentInfoPo>lambdaQuery()
        .eq(AgentInfoPo::getApplicationId, deleteDto.getCurrentAppId()));
    if (agentInfoPo == null) {
      return CommonRespDto.error("未找到智能体");
    }
    // 查询内置的应用信息
    List<Integer> integratedAppIds = applicationMapper.selectObjs(
        Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
            .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
            .eq(ApplicationPo::getAgentType, AgentTypeEnum.SINGLE.getKey())
            .eq(ApplicationPo::getIsIntegrated, true).ne(ApplicationPo::getStatus, -1)
            .orderByAsc(ApplicationPo::getCreateTime));
    int[] subAgentAppIds = agentInfoPo.getSubAgentAppIds();
    // 未绑定到当前多智能体的内置appId
    List<Integer> needToAddAppIds = integratedAppIds.stream()
        .filter(id -> !ArrayUtils.contains(subAgentAppIds, id)).toList();
    // 删除当前要删除的
    int[] ints = ArrayUtils.removeAllOccurrences(subAgentAppIds, deleteDto.getDeletedAppId());
    if (CollUtil.isNotEmpty(needToAddAppIds)) {
      for (Integer needToAddAppId : needToAddAppIds) {
        // 最多10个
        if (ints.length >= 10) {
          break;
        }
        ints = ArrayUtils.add(ints, needToAddAppId);
      }
    }
    int update = baseMapper.update(
        AgentInfoPo.builder().subAgentAppIds(reorderArray(ints, integratedAppIds)).build(),
        Wrappers.<AgentInfoPo>lambdaQuery()
            .eq(AgentInfoPo::getApplicationId, deleteDto.getCurrentAppId()));
    return CommonRespDto.of(update > 0);
  }


  @Override
  public CommonRespDto<Void> subAgentSelectedUpdate(SubAgentSelectUpdateDto dto) {
    AgentInfoPo agentInfoPo = baseMapper.selectOne(Wrappers.<AgentInfoPo>lambdaQuery()
        .eq(AgentInfoPo::getApplicationId, dto.getCurrentAppId()));
    if (agentInfoPo == null) {
      return CommonRespDto.error("未找到智能体");
    }
    // 查询内置的应用信息
    List<Integer> integratedAppIds = applicationMapper.selectObjs(
        Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
            .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
            .eq(ApplicationPo::getAgentType, AgentTypeEnum.SINGLE.getKey())
            .eq(ApplicationPo::getIsIntegrated, true).ne(ApplicationPo::getStatus, -1)
            .orderByAsc(ApplicationPo::getCreateTime));
    // 当前选中的子智能体id集合
    int[] subAgentAppIds = dto.getSelectedAppIds();
    int[] finalSubAgentAppIds = subAgentAppIds;
    // 未绑定到当前多智能体的内置appId
    List<Integer> needToAddMultipleAppIds = integratedAppIds.stream()
        .filter(id -> !ArrayUtils.contains(finalSubAgentAppIds, id)).toList();
    // 添加到原先的内置的索引之后
    if (CollUtil.isNotEmpty(needToAddMultipleAppIds)) {
      for (Integer needToAddMultipleAppId : needToAddMultipleAppIds) {
        // 最多10个
        if (subAgentAppIds.length >= 10) {
          break;
        }
        subAgentAppIds = ArrayUtils.add(subAgentAppIds, needToAddMultipleAppId);
      }
    }
    int update = baseMapper.update(
        AgentInfoPo.builder().subAgentAppIds(reorderArray(subAgentAppIds, integratedAppIds))
            .build(), Wrappers.<AgentInfoPo>lambdaQuery()
            .eq(AgentInfoPo::getApplicationId, dto.getCurrentAppId()));
    return CommonRespDto.of(update > 0);
  }


  /**
   * 模型配置
   *
   * @param requestBody 请求体
   */
  private boolean checkAndSetModelConfig(JSONObject requestBody, AgentInfoResponseDto agentInfo) {

    CommonRespDto<ModelDto> chatModelResp = modelService.getInfo(agentInfo.getModelId());
    ModelDto chatModel;
    if (!chatModelResp.isOk() || Objects.isNull(chatModelResp.getData()) || Boolean.FALSE.equals(
        chatModelResp.getData().getIsShelf())) {
      log.error("对话模型获取异常：{}", chatModelResp.getData());
      return false;
    }
    chatModel = chatModelResp.getData();
    requestBody.putOnce("chat_model_instance_provider", chatModel.getLoadingMode());

    JSONObject chatModelConfig = JSONUtil.createObj();
    chatModelConfig.putOnce("llm_type", "chat");
    chatModelConfig.putOnce("model_name", chatModel.getInternalName());
    chatModelConfig.putOnce("base_url", chatModel.getUrl());
    chatModelConfig.putOnce("apikey", chatModel.getApiKey());
    chatModelConfig.putOnce("is_support_vision", chatModel.getIsSupportFunction());
    chatModelConfig.putOnce("context_length", chatModel.getContextLength());
    chatModelConfig.putOnce("max_token_length", chatModel.getTokenMax());
    chatModelConfig.putOnce("is_support_function", chatModel.getIsSupportVisual());

    requestBody.putOnce("chat_model_instance_config", chatModelConfig);
    return true;
  }

  /**
   * 用户输入参数配置
   *
   * @param chatRequestBody 请求参数
   * @param agentInfo       应用信息
   */
  private static void setUserInputConfig(JSONObject chatRequestBody,
      AgentInfoResponseDto agentInfo) {
    chatRequestBody.putOnce("chat_model_parameters", null); // todo
    chatRequestBody.putOnce("system_prompt", agentInfo.getPromptWords());
//    chatRequestBody.putOnce("user_query", chatReqVo.getQuery());
//    chatRequestBody.putOnce("chat_history", chatReqVo.getChatHistory());
    chatRequestBody.putOnce("chat_history_depth", agentInfo.getShortTermMemoryRounds());
//    chatRequestBody.putOnce("inputs", chatReqVo.getInputs());
    // 推理模式
    chatRequestBody.putOnce("agent_mode", agentInfo.getAgentMode());

    chatRequestBody.putOnce("agent_name",
        agentInfo.getApplicationId() + "_" + agentInfo.getApplicationName());
    chatRequestBody.putOnce("agent_description", agentInfo.getApplicationDescription());
  }


  /**
   * 长期记忆配置
   *
   * @param chatRequestBody 请求体
   * @param agentInfo       智能体信息
   */
  private void setLongtermConfig(JSONObject chatRequestBody, AgentInfoResponseDto agentInfo) {
    chatRequestBody.putOnce("is_memory", agentInfo.getLongTermMemoryEnabled());
    if (agentInfo.getLongTermMemoryEnabled()) {
      JSONObject memoryInfoJson = JSONUtil.createObj();
      memoryInfoJson.putOnce("application_id", agentInfo.getApplicationId());
//      memoryInfoJson.putOnce("user_id", userId);
      memoryInfoJson.putOnce("agent_id", agentInfo.getId());
//      memoryInfoJson.putOnce("data_type", chatReqVo.getRunType());
      // 长期记忆类型  always永久有效，custom自定义
      if ("custom".equals(agentInfo.getLongTermMemoryType())) {

        LocalDateTime localDateTime = LocalDateTime.now()
            .minusDays(agentInfo.getLongTermMemoryExpired());
        memoryInfoJson.putOnce("expired_time", LocalDateTimeUtil.formatNormal(localDateTime));
      }
      chatRequestBody.putOnce("memory_info", memoryInfoJson);
      CommonRespDto<ModelDto> commonRespDto = modelService.getMemoryModel();
      ModelDto modelDto = commonRespDto.getData();
      JSONObject promptRequestJson = buildPromptRequest(modelDto);
      chatRequestBody.putOnce("memory_model",
          JSONUtil.createObj().putOnce("model_instance_provider", "ollama")
              .putOnce("model_instance_config", promptRequestJson));
    }
  }


  /**
   * 重新排序数组，使存在于 multipleAppIds 中的元素排在前面， 但它们的顺序与在 subAgentAppIds 原数组中的顺序一致。 不在 integratedAppIds
   * 中的元素排在后面，同样保持原顺序。
   *
   * @param subAgentAppIds   需要排序的源数组
   * @param integratedAppIds 作为筛选条件的列表
   * @return 排序后的新数组
   */
  public static int[] reorderArray(int[] subAgentAppIds, List<Integer> integratedAppIds) {
    // 将 List 转换为 Set 以提高查找效率 (O(1) 平均时间复杂度)
    Set<Integer> targetSet = new HashSet<>(integratedAppIds);

    List<Integer> priorityElements = new ArrayList<>(); // 存储在 integratedAppIds 中的元素
    List<Integer> remainingElements = new ArrayList<>(); // 存储不在 integratedAppIds 中的元素

    // 遍历原数组 subAgentAppIds，根据元素是否在 targetSet 中进行分类
    for (int id : subAgentAppIds) {
      if (targetSet.contains(id)) {
        // 如果元素在 integratedAppIds 中，则加入优先级列表
        priorityElements.add(id);
      } else {
        // 如果元素不在 integratedAppIds 中，则加入剩余列表
        remainingElements.add(id);
      }
    }
    // 合并两个列表：优先级元素 + 剩余元素
    List<Integer> result = new ArrayList<>();
    result.addAll(priorityElements); // 这里的顺序就是 subAgentAppIds 中的顺序
    result.addAll(remainingElements); // 这里的顺序也是 subAgentAppIds 中的顺序

    // 将合并后的 List 转换为 int[] 数组
    return result.stream().mapToInt(Integer::intValue).toArray();
  }

}
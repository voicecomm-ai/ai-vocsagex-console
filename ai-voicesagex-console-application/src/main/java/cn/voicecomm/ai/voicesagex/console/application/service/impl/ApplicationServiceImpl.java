package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import static cn.voicecomm.ai.voicesagex.console.application.service.impl.AgentInfoServiceImpl.reorderArray;

import cn.hutool.core.codec.Base62;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiKeyCreate;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.AppPublishAndOnShelfTimeResp;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationKeyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationListReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagUpdateDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.TemplateDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.TemplateDto.ApplicationTagGroupDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentListReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.AgentTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.converter.ApplicationConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.ApplicationTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentChatTokenMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentInfoMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationKeyMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpApplicationRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowsPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.NodeCanvas;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.NodeCanvas.Position;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNode;
import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentChatTokenPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentInfoPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpApplicationRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wangf
 * @date 2025/5/19 下午 1:51
 */
@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, ApplicationPo> implements
    ApplicationService {

  private final ApplicationConverter applicationConverter;

  private final ApplicationTagConverter applicationTagConverter;

  private final ApplicationTagMapper applicationTagMapper;


  private final ApplicationTagRelationMapper applicationTagRelationMapper;

  private final McpApplicationRelationMapper mcpApplicationRelationMapper;

  private final McpMapper mcpMapper;

  private final WorkflowMapper workflowMapper;
  private final ApplicationExperienceMapper applicationExperienceMapper;
  private final AgentPublishHistoryMapper agentPublishHistoryMapper;
  private final WorkflowsPublishHistoryMapper workflowsPublishHistoryMapper;
  private final ApplicationExperienceTagMapper applicationExperienceTagMapper;
  private final ApplicationExperienceTagRelationMapper applicationExperienceTagRelationMapper;
  private final ApplicationKeyMapper applicationKeyMapper;
  private final AgentChatTokenMapper agentChatTokenMapper;
  private final AgentInfoMapper agentInfoMapper;

  @Autowired
  private AgentInfoService agentInfoService;

  @DubboReference
  public BackendUserService backendUserService;

  /**
   * 文件上传路径前缀
   */
  @Value("${file.upload}")
  private String uploadDir;

  @Override
  public CommonRespDto<PagingRespDto<ApplicationDto>> getPageList(ApplicationListReq req) {

    String name = req.getName();
    if (StrUtil.isNotBlank(name)) {
      name = SpecialCharUtil.replaceSpecialWord(name);
    }
    Integer userId = UserAuthUtil.getUserId();

    Page<ApplicationPo> page = Page.of(req.getCurrent(), req.getSize());
    // 数据权限
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(userId).getData();
    LambdaQueryWrapper<ApplicationPo> queryWrapper = Wrappers.<ApplicationPo>lambdaQuery()
        .in(CollUtil.isNotEmpty(req.getTypeList()), ApplicationPo::getType, req.getTypeList())
        .apply(StrUtil.isNotBlank(name), "LOWER(\"name\") LIKE LOWER({0})", "%" + name + "%")
        .in(BaseAuditPo::getCreateBy, userIdList).ne(ApplicationPo::getStatus, -1)
        .orderByDesc(BasePo::getUpdateTime);
    // 智能体发布列表
    List<AgentPublishHistoryPo> agentPublishHistoryPos = agentPublishHistoryMapper.selectList(
        Wrappers.emptyWrapper());
    // 工作流发布列表
    List<WorkflowsPublishHistoryPo> workflowsPublishHistoryPos = workflowsPublishHistoryMapper.selectList(
        Wrappers.emptyWrapper());
    // 应用体验列表
    List<ApplicationExperiencePo> applicationExperiencePos = applicationExperienceMapper.selectList(
        Wrappers.emptyWrapper());
    if (CollUtil.isNotEmpty(req.getTagIdList())) {
      // 添加标签查询条件
      List<Integer> appIds = applicationTagRelationMapper.selectObjs(
          Wrappers.<ApplicationTagRelationPo>lambdaQuery()
              .select(ApplicationTagRelationPo::getApplicationId)
              .in(ApplicationTagRelationPo::getTagId, req.getTagIdList()));
      if (CollUtil.isNotEmpty(appIds)) {
        queryWrapper.in(ApplicationPo::getId, appIds);
      } else {
        return CommonRespDto.success(applicationConverter.poToDtoPageList(page));
      }
    }

    Set<Integer> published = Stream.concat(
            agentPublishHistoryPos.stream().map(AgentPublishHistoryPo::getApplicationId),
            workflowsPublishHistoryPos.stream().map(WorkflowsPublishHistoryPo::getApp_id))
        .collect(Collectors.toSet());

    Set<Integer> experiencing = applicationExperiencePos.stream()
        .map(ApplicationExperiencePo::getAppId).collect(Collectors.toSet());

    if (ObjUtil.isNotNull(req.getHasPublish())) {
      if (req.getHasPublish()) {
        if (CollUtil.isEmpty(published)) {
          return CommonRespDto.success(applicationConverter.poToDtoPageList(page));
        }
        // 已发布
        queryWrapper.in(ApplicationPo::getId, published);
      } else {
        // 未发布
        queryWrapper.notIn(CollUtil.isNotEmpty(published), ApplicationPo::getId, published);
      }
    }
    if (ObjUtil.isNotNull(req.getHasExperience())) {
      if (req.getHasExperience()) {
        if (CollUtil.isEmpty(experiencing)) {
          return CommonRespDto.success(applicationConverter.poToDtoPageList(page));
        }
        // 已上架
        queryWrapper.in(ApplicationPo::getId, experiencing);
      } else {
        // 未上架
        queryWrapper.notIn(CollUtil.isNotEmpty(experiencing), ApplicationPo::getId, experiencing);
      }
    }
    // 是否内置
    queryWrapper.eq(ObjUtil.isNotNull(req.getIsIntegrated()), ApplicationPo::getIsIntegrated,
        req.getIsIntegrated());

    // agent类型筛选
    queryWrapper.eq(StrUtil.isNotBlank(req.getAgentType()), ApplicationPo::getAgentType,
        req.getAgentType());

    Page<ApplicationPo> poPage = baseMapper.selectPage(page, queryWrapper);
    PagingRespDto<ApplicationDto> dtoPageList = applicationConverter.poToDtoPageList(poPage);
    if (CollUtil.isNotEmpty(dtoPageList.getRecords())) {
      List<ApplicationDto> records = dtoPageList.getRecords();

      records.forEach(dto -> {
        Optional<ApplicationExperiencePo> any = applicationExperiencePos.stream()
            .filter(e -> e.getAppId().equals(dto.getId())).findAny();
        dto.setOnShelf(any.isPresent());
        if (ApplicationTypeEnum.AGENT.getKey().equals(dto.getType())) {
          Optional<AgentPublishHistoryPo> any1 = agentPublishHistoryPos.stream()
              .filter(e -> e.getApplicationId().equals(dto.getId())).findAny();
          if (any1.isPresent()) {
            dto.setStatus(1);
          }
        } else if (ApplicationTypeEnum.WORKFLOW.getKey().equals(dto.getType())) {
          Optional<WorkflowsPublishHistoryPo> any2 = workflowsPublishHistoryPos.stream()
              .filter(e -> e.getApp_id().equals(dto.getId())).findAny();
          if (any2.isPresent()) {
            dto.setStatus(1);
          }
        }
      });

      List<Integer> appIds = records.stream().map(ApplicationDto::getId).toList();
      // 查出应用id对应的所有标签,并根据ApplicationId分组
      List<ApplicationTagRelationPo> relationList = applicationTagRelationMapper.selectList(
          Wrappers.<ApplicationTagRelationPo>lambdaQuery()
              .in(ApplicationTagRelationPo::getApplicationId, appIds));
      if (CollUtil.isNotEmpty(relationList)) {
        List<Integer> tagIdList = relationList.stream().map(ApplicationTagRelationPo::getTagId)
            .toList();
        Map<Integer, ApplicationTagPo> tagMap = applicationTagMapper.selectList(
                Wrappers.<ApplicationTagPo>lambdaQuery().in(ApplicationTagPo::getId, tagIdList))
            .stream().collect(Collectors.toMap(ApplicationTagPo::getId, v -> v));
        // 根据ApplicationId分组成map，key为应用id，value为标签id集合
        Map<Integer, List<Integer>> appTagListMap = relationList.stream().collect(
            Collectors.groupingBy(ApplicationTagRelationPo::getApplicationId,
                Collectors.mapping(ApplicationTagRelationPo::getTagId, Collectors.toList())));
        records.forEach(dto -> {
          List<Integer> appTagList = appTagListMap.getOrDefault(dto.getId(), List.of());
          setAppTagList(dto, appTagList, tagMap);
        });
      }
      // 查出应用id对应所有mcp应用关系
      List<McpApplicationRelationPo> mcpAppRelationList = mcpApplicationRelationMapper.selectList(
          Wrappers.<McpApplicationRelationPo>lambdaQuery()
              .in(McpApplicationRelationPo::getApplicationId, appIds));

      if (CollUtil.isNotEmpty(mcpAppRelationList)) {
        List<Integer> mcpIds = mcpAppRelationList.stream().map(McpApplicationRelationPo::getMcpId)
            .toList();

        Map<Integer, McpPo> mcpMap = mcpMapper.selectList(
                Wrappers.<McpPo>lambdaQuery().in(McpPo::getId, mcpIds)).stream()
            .collect(Collectors.toMap(McpPo::getId, v -> v));

        // 根据ApplicationId分组成map，key为应用id，value为标签id集合
        Map<Integer, List<Integer>> appMcpListMap = mcpAppRelationList.stream().collect(
            Collectors.groupingBy(McpApplicationRelationPo::getApplicationId,
                Collectors.mapping(McpApplicationRelationPo::getMcpId, Collectors.toList())));

        records.forEach(dto -> {
          List<Integer> mcpIdList = appMcpListMap.getOrDefault(dto.getId(), List.of());
          setMcpList(dto, mcpIdList, mcpMap);
        });
      }
    }
    return CommonRespDto.success(dtoPageList);
  }


  @Override
  public CommonRespDto<List<SubAgentInfoDto>> getSubPublishedAgentList(SubAgentListReq reqDto) {

    Integer userId = UserAuthUtil.getUserId();
    // 数据权限
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(userId).getData();
    LambdaQueryWrapper<ApplicationPo> queryWrapper = Wrappers.<ApplicationPo>lambdaQuery()
        .in(BaseAuditPo::getCreateBy, userIdList)
        .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
        .eq(ApplicationPo::getAgentType, AgentTypeEnum.SINGLE.getKey())
        .eq(ApplicationPo::getStatus, 1)
        .orderByDesc(ApplicationPo::getIsIntegrated)
        .orderByDesc(BasePo::getUpdateTime);
    if (CollUtil.isEmpty(userIdList)) {
      return CommonRespDto.success(List.of());
    }
    List<ApplicationPo> applicationPoList = baseMapper.selectList(queryWrapper);
    // 转为map
    Map<Integer, ApplicationPo> applicationPoMap = applicationPoList.stream()
        .collect(Collectors.toMap(ApplicationPo::getId, v -> v));
    List<Integer> agentAppIds = applicationPoList.stream().map(ApplicationPo::getId).toList();
    List<AgentPublishHistoryPo> agentPublishHistoryPos = agentPublishHistoryMapper.selectList(
        Wrappers.<AgentPublishHistoryPo>lambdaQuery()
            .in(AgentPublishHistoryPo::getApplicationId, agentAppIds));
    // 根据applicationId分组，每个分组获取createTime最晚的组合为新的数组
    List<AgentPublishHistoryPo> poList = agentPublishHistoryPos.stream().collect(
        Collectors.groupingBy(AgentPublishHistoryPo::getApplicationId)
    ).values().stream().map(
        publishHistoryPos -> publishHistoryPos.stream()
            .max(Comparator.comparing(AgentPublishHistoryPo::getVersion))
            .get()).toList();
    List<SubAgentInfoDto> subAgentInfoDtos = new ArrayList<>();
    for (AgentPublishHistoryPo agentPublishHistoryPo : poList) {
      AgentInfoResponseDto subAgentData = JacksonUtil.toBean(agentPublishHistoryPo.getConfigData(),
          AgentInfoResponseDto.class);
      // 当请求的应用名称为空或目标应用名称包含请求名称时，添加到结果列表
      if (StrUtil.isBlank(reqDto.getApplicationName()) ||
          subAgentData.getApplicationName().toLowerCase()
              .contains(reqDto.getApplicationName().toLowerCase())) {
        subAgentInfoDtos.add(SubAgentInfoDto.builder()
            .id(subAgentData.getId())
            .applicationId(subAgentData.getApplicationId())
            .applicationName(subAgentData.getApplicationName())
            .applicationDescription(subAgentData.getApplicationDescription())
            .applicationIconUrl(subAgentData.getApplicationIconUrl())
            .isIntegrated(
                applicationPoMap.get(agentPublishHistoryPo.getApplicationId()).getIsIntegrated())
            .publishTime(agentPublishHistoryPo.getCreateTime())
            .build());
      }
      // 按照按照发布时间倒序
      subAgentInfoDtos = subAgentInfoDtos.stream()
          .sorted(Comparator.comparing(SubAgentInfoDto::getPublishTime).reversed())
          .collect(Collectors.toList());
    }
    return CommonRespDto.success(subAgentInfoDtos);
  }

  /**
   * 获取模板列表
   *
   * @param name  应用名称
   * @param appId 应用id
   * @return 模板列表
   */
  @Override
  public CommonRespDto<TemplateDto> templateList(String name, Integer appId) {
    String specialWord = SpecialCharUtil.replaceSpecialWord(name);
    TemplateDto templateDto = new TemplateDto();
    List<ApplicationExperiencePo> applicationExperiencePos = applicationExperienceMapper.selectList(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            // 排除当前工作流的id，避免形成死循环
            .ne(ApplicationExperiencePo::getAppId, appId)
            .apply(StrUtil.isNotBlank(specialWord), "LOWER(\"name\") LIKE LOWER({0})",
                "%" + specialWord + "%"));
    List<Integer> agentIds = applicationExperiencePos.stream()
        .filter(e -> e.getType().equals("agent")).map(ApplicationExperiencePo::getId).toList();
    List<Integer> workflowIds = applicationExperiencePos.stream()
        .filter(e -> e.getType().equals("workflow")).map(ApplicationExperiencePo::getId).toList();
    List<ApplicationExperienceTagPo> applicationExperienceTagPos = applicationExperienceTagMapper.selectList(
        Wrappers.emptyWrapper());
    applicationExperienceTagPos = applicationExperienceTagPos.stream().sorted(
            Comparator.comparing(ApplicationExperienceTagPo::getIsBuiltIn, Comparator.reverseOrder())
                .thenComparing(ApplicationExperienceTagPo::getCreateTime, Comparator.reverseOrder()))
        .toList();

    List<ApplicationTagGroupDto> agents = buildData(applicationExperiencePos, agentIds,
        applicationExperienceTagPos);
    templateDto.setAgentList(agents);

    List<ApplicationTagGroupDto> workflows = buildData(applicationExperiencePos, workflowIds,
        applicationExperienceTagPos);
    templateDto.setWorkflowList(workflows);

    return CommonRespDto.success(templateDto);
  }

  private List<ApplicationTagGroupDto> buildData(
      List<ApplicationExperiencePo> applicationExperiencePos, List<Integer> workflowIds,
      List<ApplicationExperienceTagPo> applicationExperienceTagPos) {
    List<ApplicationTagGroupDto> workflowTagGroupDtos = new ArrayList<>();
    List<ApplicationExperienceTagRelationPo> allRelationPos = applicationExperienceTagRelationMapper.selectList(
        Wrappers.emptyWrapper());
    if (CollUtil.isNotEmpty(workflowIds)) {
      Set<Integer> allFilterWorkflowIds = new HashSet<>();
      for (ApplicationExperienceTagPo tagPo : applicationExperienceTagPos) {
        List<ApplicationExperienceTagRelationPo> relationPoList = allRelationPos.stream().filter(
            e -> e.getTagId().equals(tagPo.getId()) && workflowIds.contains(
                e.getExperienceApplicationId())).toList();
        if (CollUtil.isNotEmpty(relationPoList)) {
          ApplicationTagGroupDto workflowTagGroupDto = new ApplicationTagGroupDto();
          workflowTagGroupDto.setTagId(tagPo.getId());
          workflowTagGroupDto.setTagName(tagPo.getName());
          List<Integer> filterWorkflowIds = relationPoList.stream()
              .map(ApplicationExperienceTagRelationPo::getExperienceApplicationId).toList();
          // 把有标签的id都存入一个set中，用于后续过滤
          allFilterWorkflowIds.addAll(filterWorkflowIds);
          List<ApplicationExperiencePo> workflowList = new ArrayList<>(
              applicationExperiencePos.stream().filter(e -> filterWorkflowIds.contains(e.getId()))
                  .toList());
          // 排序
          workflowList.sort(
              Comparator.comparing(ApplicationExperiencePo::getCreateTime).reversed());
          List<ApplicationExperienceDto> experienceDtos = applicationConverter.exePoToDtoList(
              workflowList);
          experienceDtos.forEach(dto -> {
            List<ApplicationExperienceTagRelationPo> relationPos = allRelationPos.stream()
                .filter(e -> e.getExperienceApplicationId().equals(dto.getId())).toList();
            List<Integer> tagIdList = relationPos.stream()
                .map(ApplicationExperienceTagRelationPo::getTagId).toList();
            List<ApplicationExperienceTagPo> tagList = applicationExperienceTagPos.stream()
                .filter(e -> tagIdList.contains(e.getId())).toList();

            dto.setTagList(applicationConverter.tagPoToDtoList(tagList));
          });
          workflowTagGroupDto.setApplicationDtos(experienceDtos);
          workflowTagGroupDtos.add(workflowTagGroupDto);
        }
      }
      // 过滤出没有标签的 应用
      List<Integer> otherIds = workflowIds.stream().filter(e -> !allFilterWorkflowIds.contains(e))
          .toList();
      if (CollUtil.isNotEmpty(otherIds)) {
        List<ApplicationExperiencePo> otherWorkflowList = applicationExperiencePos.stream()
            .filter(e -> otherIds.contains(e.getId())).toList();
        workflowTagGroupDtos.add(new ApplicationTagGroupDto(null, "其他",
            applicationConverter.exePoToDtoList(otherWorkflowList)));
      }
    }
    return workflowTagGroupDtos;
  }

  /**
   * 设置应用标签列表 此方法用于将应用DTO中的标签信息转换为应用标签列表，便于后续处理和使用
   *
   * @param dto        ApplicationDto对象，包含应用的基本信息和标签信息
   * @param appTagList 应用标签列表，用于存储转换后的标签信息
   */
  private void setAppTagList(ApplicationDto dto, List<Integer> appTagList,
      Map<Integer, ApplicationTagPo> tagMap) {
    if (CollUtil.isNotEmpty(appTagList)) {
      List<ApplicationTagDto> tagDtoList = new ArrayList<>();
      appTagList.forEach(tagId -> {
        ApplicationTagPo tagPo = tagMap.get(tagId);
        tagDtoList.add(applicationTagConverter.poToDto(tagPo));
      });
      dto.setTagList(tagDtoList);
    }
  }

  private void setMcpList(ApplicationDto dto, List<Integer> mcpList, Map<Integer, McpPo> mcpMap) {
    if (CollUtil.isNotEmpty(mcpList)) {
      List<McpDto> mcpDtoList = new ArrayList<>();
      mcpList.forEach(tagId -> {
        McpPo mcpPo = mcpMap.get(tagId);
        mcpDtoList.add(applicationTagConverter.poToMcpDto(mcpPo));
      });
      dto.setMcpList(mcpDtoList);
    }
  }

  @Override
  public CommonRespDto<ApplicationDto> getById(Integer id) {
    ApplicationPo po = baseMapper.selectById(id);
    if (po == null) {
      return CommonRespDto.error("应用不存在");
    }
    if (po.getStatus() == -1) {
      return CommonRespDto.error("该应用已被删除", null);
    }
    ApplicationDto dto = applicationConverter.poToDto(po);
    return CommonRespDto.success(dto);
  }


  @Override
  public CommonRespDto<Integer> add(ApplicationDto dto) {
    if (dto.getName().startsWith("_")) {
      return CommonRespDto.error("应用名称不能以_开头");
    }
    ApplicationPo po = applicationConverter.dtoToPo(dto);
    baseMapper.insert(po);
    if (ApplicationTypeEnum.WORKFLOW.getKey().equals(po.getType())) {
      // 添加工作流应用
      WorkflowPo workflowPo = WorkflowPo.builder().app_id(po.getId()).type("workflow")
          .version("draft").build();
      StartNode startNode = StartNode.builder().title("开始").desc("").selected(false)
          .type(NodeType.START.getValue()).build();
      Graph graph = Graph.builder().edges(new ArrayList<>()).nodes(List.of(
              NodeCanvas.builder().id(UUID.randomUUID().toString()).type(NodeType.START.getValue())
                  .position(new Position().setX(80).setY(282)).sourcePosition("right")
                  .targetPosition("left").positionAbsolute(new Position().setX(80).setY(282)).height(90)
                  .width(300).data(startNode).selected(false).build())).viewport(new ArrayList<>())
          .build();
      workflowPo.setGraph(JSONUtil.toJsonStr(graph));
      workflowMapper.insert(workflowPo);
    }
    if (CollUtil.isNotEmpty(dto.getTagList())) {
      List<ApplicationTagRelationPo> tagRelationList = dto.getTagList().stream().map(
          tagDto -> ApplicationTagRelationPo.builder().applicationId(po.getId())
              .tagId(tagDto.getId()).build()).toList();
      MybatisPlusUtil<ApplicationTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          applicationTagRelationMapper, ApplicationTagRelationPo.class);
      mybatisPlusUtil.saveBatch(tagRelationList, tagRelationList.size());
    }
    return CommonRespDto.success(po.getId());
  }

  @Override
  public CommonRespDto<Void> update(ApplicationDto dto) {
    ApplicationPo po = applicationConverter.dtoToPo(dto);
    int i = baseMapper.updateById(po);
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<Void> updateAppTag(ApplicationTagUpdateDto dto) {
    applicationTagRelationMapper.delete(Wrappers.<ApplicationTagRelationPo>lambdaQuery()
        .eq(ApplicationTagRelationPo::getApplicationId, dto.getId()));
    if (CollUtil.isNotEmpty(dto.getTagIdList())) {
      List<ApplicationTagPo> applicationTagPoList = applicationTagMapper.selectList(
          Wrappers.<ApplicationTagPo>lambdaQuery().in(ApplicationTagPo::getId, dto.getTagIdList()));
      if (CollUtil.isNotEmpty(applicationTagPoList)) {
        List<Integer> tagIdList = applicationTagPoList.stream().map(ApplicationTagPo::getId)
            .toList();
        List<ApplicationTagRelationPo> tagRelationList = tagIdList.stream().map(
            tagId -> ApplicationTagRelationPo.builder().applicationId(dto.getId()).tagId(tagId)
                .build()).toList();
        MybatisPlusUtil<ApplicationTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
            applicationTagRelationMapper, ApplicationTagRelationPo.class);
        mybatisPlusUtil.saveBatch(tagRelationList, tagRelationList.size());
      }
    }
    return CommonRespDto.of(true);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> delete(Integer id) {
    log.info("删除应用:{}", id);
    // 检验是否已经上架
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery().eq(ApplicationExperiencePo::getAppId, id));
    if (ObjUtil.isNotNull(applicationExperiencePo)) {
      return CommonRespDto.error("应用已上架到发现页，请先下架应用");
    }
    ApplicationPo applicationPo = baseMapper.selectById(id);
    if (ObjUtil.isNull(applicationPo)) {
      return CommonRespDto.error("数据不存在");
    }
    if (applicationPo.getType().equals(ApplicationTypeEnum.WORKFLOW.getKey())) {
      workflowsPublishHistoryMapper.delete(Wrappers.<WorkflowsPublishHistoryPo>lambdaQuery()
          .eq(WorkflowsPublishHistoryPo::getApp_id, id));
    } else if (applicationPo.getType().equals(ApplicationTypeEnum.AGENT.getKey())) {
      agentPublishHistoryMapper.delete(Wrappers.<AgentPublishHistoryPo>lambdaQuery()
          .eq(AgentPublishHistoryPo::getApplicationId, id));
    }
    // status 更新为-1
    int i = baseMapper.updateById(ApplicationPo.builder().id(id).status(-1).build());
    // 删除单智能体时，多智能体去除绑定
    if (ApplicationTypeEnum.AGENT.getKey().equals(applicationPo.getType())
        && AgentTypeEnum.SINGLE.getKey().equals(applicationPo.getAgentType())) {
      List<Integer> multipleAppIds = baseMapper.selectObjs(
          Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
              .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
              .eq(ApplicationPo::getAgentType, AgentTypeEnum.MULTIPLE.getKey())
              .ne(ApplicationPo::getStatus, -1));
      if (CollUtil.isEmpty(multipleAppIds)) {
        log.info("删除没有多智能体");
        return CommonRespDto.of(i > 0);
      }
      // 原先的多智能体绑定的子智能体去除
      List<AgentInfoPo> oldAgentInfoList = agentInfoMapper.selectList(
          Wrappers.<AgentInfoPo>lambdaQuery().in(AgentInfoPo::getApplicationId, multipleAppIds));
      List<AgentInfoPo> updateAgentInfoList = new ArrayList<>();
      for (AgentInfoPo agentInfoPo : oldAgentInfoList) {

        int[] subAgentAppIds = agentInfoPo.getSubAgentAppIds();
        if (!ArrayUtils.contains(subAgentAppIds, applicationPo.getId())) {
          continue;
        }
        // 移除应用ID
        int[] newSubAgentAppIds = ArrayUtils.removeAllOccurrences(subAgentAppIds,
            applicationPo.getId());
        updateAgentInfoList.add(
            AgentInfoPo.builder().id(agentInfoPo.getId()).subAgentAppIds(newSubAgentAppIds)
                .build());
      }
      if (CollUtil.isEmpty(updateAgentInfoList)) {
        return CommonRespDto.of(i > 0);
      }
      MybatisPlusUtil<AgentInfoPo> mybatisPlusUtil = new MybatisPlusUtil<>(agentInfoMapper,
          AgentInfoPo.class);
      mybatisPlusUtil.updateBatchById(updateAgentInfoList, updateAgentInfoList.size());
    }
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<String> uploadIcon(MultipartFile file) {
    // 校验文件类型和大小
    if (ObjUtil.isNull(file)) {
      return CommonRespDto.error("请选择图片");
    }
    log.info("开始上传图片文件，文件名: {}，文件大小: {}", file.getOriginalFilename(),
        file.getSize());
    if (file.isEmpty()) {
      log.warn("上传图片文件失败，文件为空");
      return CommonRespDto.error("请选择图片文件");
    }
    if (!isPictureFile(file)) {
      log.warn("上传图片文件失败，文件格式不支持，文件名: {}", file.getOriginalFilename());
      return CommonRespDto.error("仅支持图片格式文件");
    }

    try {
      // 保存到文件服务器
      String filePath = String.join("/", "icon",
          DatePattern.PURE_DATETIME_MS_FORMAT.format(new Date()) + RandomUtil.randomString(4)
              + file.getOriginalFilename());
      String path = uploadDir + filePath;
      FileUtil.touch(path);
      FileUtil.writeBytes(file.getBytes(), path);
      String realPath = StrUtil.replaceFirst(path, "data1", "file");
      return CommonRespDto.success(realPath);
    } catch (IOException e) {
      log.error("上传图片文件失败，文件名: {}", file.getOriginalFilename(), e);
      return CommonRespDto.error("上传失败");
    }
  }


  @Override
  public CommonRespDto<AppPublishAndOnShelfTimeResp> publishAndOnShelfTimeDescription(
      Integer appId) {
    AppPublishAndOnShelfTimeResp appPublishAndOnShelfTimeResp = new AppPublishAndOnShelfTimeResp();
    ApplicationPo applicationPo = baseMapper.selectById(appId);
    if (ApplicationTypeEnum.AGENT.getKey().equals(applicationPo.getType())) {
      AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
          Wrappers.<AgentPublishHistoryPo>lambdaQuery()
              .eq(AgentPublishHistoryPo::getApplicationId, appId)
              .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("limit 1"));
      if (ObjUtil.isNotNull(agentPublishHistoryPo)) {
        appPublishAndOnShelfTimeResp.setLastPublishedTimeDesc(
            "上次发布" + LocalDateTimeUtil.formatNormal(agentPublishHistoryPo.getCreateTime()));
      }

    } else if (ApplicationTypeEnum.WORKFLOW.getKey().equals(applicationPo.getType())) {
      // 获取应用发布时间描述
      WorkflowsPublishHistoryPo workflowsPublishHistoryPo = workflowsPublishHistoryMapper.selectOne(
          Wrappers.<WorkflowsPublishHistoryPo>lambdaQuery()
              .eq(WorkflowsPublishHistoryPo::getApp_id, appId)
              .orderByDesc(WorkflowsPublishHistoryPo::getCreate_time).last("limit 1"));
      if (ObjUtil.isNotNull(workflowsPublishHistoryPo)) {
        appPublishAndOnShelfTimeResp.setLastPublishedTimeDesc(
            "上次发布" + LocalDateTimeUtil.formatNormal(
                workflowsPublishHistoryPo.getCreate_time()));
      }
    }

    // 获取应用上架时间描述
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    if (ObjUtil.isNotNull(applicationExperiencePo)) {
      appPublishAndOnShelfTimeResp.setLastOnShelfTimeDesc(
          "上次上架" + LocalDateTimeUtil.formatNormal(applicationExperiencePo.getCreateTime()));
    }
    return CommonRespDto.success(appPublishAndOnShelfTimeResp);
  }


  @Override
  public CommonRespDto<String> regenerateUrl(Integer id) {

    ApplicationPo applicationPo = baseMapper.selectById(id);
    if (ObjUtil.isNull(applicationPo)) {
      return CommonRespDto.error("数据不存在");
    }
    String newUrlKey = Base62.encode(IdUtil.getSnowflakeNextIdStr());
    applicationPo.setUrlKey(newUrlKey);
    baseMapper.updateById(applicationPo);
    if (StrUtil.isBlank(newUrlKey)) {
      return CommonRespDto.error("生成URL失败");
    }
    return CommonRespDto.success(newUrlKey);
  }

  @Override
  public CommonRespDto<ApplicationKeyDto> createApiKey(ApiKeyCreate req) {
    ApplicationPo applicationPo = baseMapper.selectById(req.appId());
    if (ObjUtil.isNull(applicationPo)) {
      return CommonRespDto.error("数据不存在");
    }

    ApplicationKeyPo applicationKeyPo = ApplicationKeyPo.builder().userId(UserAuthUtil.getUserId())
        .appId(req.appId()).createTime(LocalDateTime.now()).lastUseTime(null)
        .expireTime(LocalDateTime.now().plusYears(10))
        .keyValue(applicationPo.getType() + "-" + Base62.encode(IdUtil.getSnowflakeNextIdStr()))
        .build();
    applicationKeyMapper.insert(applicationKeyPo);

    return CommonRespDto.success(applicationConverter.appKeyPoToDto(applicationKeyPo));
  }

  /**
   * 获取API密钥列表
   *
   * @param appId 应用ID
   * @return API密钥列表
   */
  @Override
  public CommonRespDto<List<ApplicationKeyDto>> getApiKeyList(Integer appId) {
    Integer userId = UserAuthUtil.getUserId();
    List<Integer> userIds = backendUserService.getUserIdsByUserId(userId).getData();
    List<ApplicationKeyPo> applicationKeyPos = applicationKeyMapper.selectList(
        Wrappers.<ApplicationKeyPo>lambdaQuery().eq(ApplicationKeyPo::getAppId, appId)
            .in(ApplicationKeyPo::getUserId, userIds).orderByDesc(ApplicationKeyPo::getCreateTime));
    List<ApplicationKeyDto> applicationKeyDtos = applicationConverter.appKeyPoToDtoList(
        applicationKeyPos);
    return CommonRespDto.success(applicationKeyDtos);
  }


  @Override
  public CommonRespDto<String> agentUrlChatTokenGenerate(String urlKey) {
    // 获取urlKey对应的应用
    ApplicationPo applicationPo = baseMapper.selectOne(
        Wrappers.<ApplicationPo>lambdaQuery().eq(ApplicationPo::getUrlKey, urlKey));
    if (ObjUtil.isNull(applicationPo)) {
      return CommonRespDto.error("数据不存在");
    }
    if (!applicationPo.getUrlAccessable()) {
      return CommonRespDto.error("应用未开放URL访问");
    }
    AgentInfoPo agentInfoPo = agentInfoMapper.selectOne(Wrappers.<AgentInfoPo>lambdaQuery()
        .eq(AgentInfoPo::getApplicationId, applicationPo.getId()));
    String token = UUID.randomUUID() + Base62.encode(IdUtil.getSnowflakeNextIdStr());
    agentChatTokenMapper.insert(
        AgentChatTokenPo.builder().appId(applicationPo.getId()).token(token).urlKey(urlKey)
            .agentId(agentInfoPo.getId()).build());
    return CommonRespDto.success(token);
  }

  @Override
  public CommonRespDto<ApplicationDto> getByUrlKey(String urlKey) {
    ApplicationPo applicationPo = baseMapper.selectOne(
        Wrappers.<ApplicationPo>lambdaQuery().eq(ApplicationPo::getUrlKey, urlKey));
    if (ObjUtil.isNull(applicationPo)) {
      return CommonRespDto.success("数据不存在", null);
    }
    if (!applicationPo.getUrlAccessable()) {
      return CommonRespDto.success("此应用暂停使用", null);
    }
    ApplicationDto applicationDto = applicationConverter.poToDto(applicationPo);
    if (ApplicationTypeEnum.AGENT.getKey().equals(applicationPo.getType())) {
      AgentInfoResponseDto data = agentInfoService.getPublishedInfo(applicationPo.getId())
          .getData();
      if (data == null) {
        return CommonRespDto.success("应用未发布", null);
      }
      applicationDto.setLongTermMemoryEnabled(data.getLongTermMemoryEnabled());
      applicationDto.setIconUrl(data.getApplicationIconUrl());
      applicationDto.setName(data.getApplicationName());
    } else {
      WorkflowsPublishHistoryPo workflowsPublishHistoryPo = workflowsPublishHistoryMapper.selectOne(
          Wrappers.<WorkflowsPublishHistoryPo>lambdaQuery()
              .eq(WorkflowsPublishHistoryPo::getApp_id, applicationPo.getId())
              .orderByDesc(WorkflowsPublishHistoryPo::getCreate_time).last("LIMIT 1"));
      if (workflowsPublishHistoryPo == null) {
        return CommonRespDto.success("应用未发布", null);
      }
      applicationDto.setIconUrl(workflowsPublishHistoryPo.getAppIconUrl());
      applicationDto.setName(workflowsPublishHistoryPo.getAppName());
    }
    return CommonRespDto.success(applicationDto);
  }


  @Override
  public CommonRespDto<Void> integrated(Integer appId) {
    CommonRespDto<ApplicationDto> dtoCommonRespDto = getById(appId);
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();
    if (applicationDto.getIsIntegrated()) {
      return CommonRespDto.error("应用已经是基础协作体", null);
    }
    // 内置数量最多为5个
    if (baseMapper.selectCount(
        Wrappers.<ApplicationPo>lambdaQuery().eq(ApplicationPo::getIsIntegrated, true)
            .eq(ApplicationPo::getStatus, 1)) >= 5) {
      return CommonRespDto.error("基础协作体超过限制数量", null);
    }
    baseMapper.update(ApplicationPo.builder().isIntegrated(true).build(),
        Wrappers.<ApplicationPo>lambdaQuery().eq(ApplicationPo::getId, appId));
    // 查询所有的多智能体
    List<Integer> multipleAppIds = baseMapper.selectObjs(
        Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
            .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
            .eq(ApplicationPo::getAgentType, AgentTypeEnum.MULTIPLE.getKey())
            .ne(ApplicationPo::getStatus, -1));
    if (CollUtil.isEmpty(multipleAppIds)) {
      log.info("没有多智能体");
      return CommonRespDto.success();
    }
    // 新的内置加上后，所有的多智能体绑定上去
    List<AgentInfoPo> oldAgentInfoList = agentInfoMapper.selectList(
        Wrappers.<AgentInfoPo>lambdaQuery().in(AgentInfoPo::getApplicationId, multipleAppIds));

    // 查询内置的应用信息
    List<Integer> integratedAppIds = baseMapper.selectObjs(
        Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
            .eq(ApplicationPo::getType, ApplicationTypeEnum.AGENT.getKey())
            .eq(ApplicationPo::getAgentType, AgentTypeEnum.SINGLE.getKey())
            .eq(ApplicationPo::getIsIntegrated, true).ne(ApplicationPo::getStatus, -1)
            .orderByAsc(ApplicationPo::getCreateTime));

    List<AgentInfoPo> updateAgentInfoList = new ArrayList<>();
    for (AgentInfoPo agentInfoPo : oldAgentInfoList) {
      int[] subAgentAppIds = agentInfoPo.getSubAgentAppIds();
      if (subAgentAppIds.length < 10 && !ArrayUtils.contains(subAgentAppIds, appId)) {
        // 添加内置应用ID
        int[] newSubAgentAppIds = ArrayUtils.add(subAgentAppIds, appId);
        updateAgentInfoList.add(
            AgentInfoPo.builder().id(agentInfoPo.getId())
                .subAgentAppIds(reorderArray(newSubAgentAppIds, integratedAppIds))
                .build());
      } else {
        updateAgentInfoList.add(
            AgentInfoPo.builder().id(agentInfoPo.getId())
                .subAgentAppIds(reorderArray(subAgentAppIds, integratedAppIds))
                .build());
      }
    }
    // 批量更新
    MybatisPlusUtil<AgentInfoPo> mybatisPlusUtil = new MybatisPlusUtil<>(agentInfoMapper,
        AgentInfoPo.class);
    mybatisPlusUtil.updateBatchById(updateAgentInfoList, 1000);

    return CommonRespDto.success();
  }

  /**
   * 检测应用是否上架
   *
   * @param appId 待检测的应用ID
   * @return 检测结果
   */
  @Override
  public CommonRespDto<Boolean> checkOnShelf(Integer appId) {
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    if (ObjUtil.isNull(applicationExperiencePo)) {
      return CommonRespDto.success("模板已下架", null);
    }
    CommonRespDto<ApplicationDto> dtoCommonRespDto = this.getById(appId);
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.success(dtoCommonRespDto.getMsg(), false);
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  /**
   * 检验文件是否为图片格式
   *
   * @param file file
   * @return boolean
   */
  private boolean isPictureFile(MultipartFile file) {
    String filename = file.getOriginalFilename();
    return filename != null && (filename.toLowerCase().endsWith(".png") || filename.toLowerCase()
        .endsWith(".jpeg") || filename.toLowerCase().endsWith(".jpg"));
  }

}

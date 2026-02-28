package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.workflow.WorkflowService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiAccessResponse.ApiInterfaceInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ReuseRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ShelfRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentVariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.Variable;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowRunsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto.Type;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.AgentTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.ApplicationTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.WorkflowConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.KnowledgeBaseApplicationRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowNodeExecutionsMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowRunsMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowsExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowsPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.handler.MessageHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNode.McpParam;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNode;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.WorkflowRunsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowsPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.KnowledgeBaseApplicationRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工作流服务实现
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class WorkflowServiceImpl extends ServiceImpl<WorkflowMapper, WorkflowPo> implements
    WorkflowService {

  private final WorkflowMapper workflowMapper;
  private final WorkflowConverter workflowConverter;
  private final ApplicationMapper applicationMapper;
  private final WorkflowNodeExecutionsMapper workflowNodeExecutionsMapper;
  private final WorkflowRunsMapper workflowRunsMapper;
  private final MessageHandler messageHandler;
  private final KnowledgeBaseApplicationRelationMapper knowledgeBaseApplicationRelationMapper;
  private final WorkflowsPublishHistoryMapper workflowsPublishHistoryMapper;
  private final ApplicationExperienceMapper applicationExperienceMapper;
  private final ApplicationExperienceTagRelationMapper applicationExperienceTagRelationMapper;
  private final WorkflowsExperienceMapper workflowsExperienceMapper;
  private final ApplicationExperienceTagMapper applicationExperienceTagMapper;
  private final ApplicationTagConverter applicationTagConverter;
  private final ApplicationService applicationService;
  private final AgentExperienceMapper agentExperienceMapper;
  private final AgentInfoService agentInfoService;
  private final McpMapper mcpMapper;
  private final AgentPublishHistoryMapper agentPublishHistoryMapper;

  @DubboReference
  public BackendUserInfoService backendUserInfoService;

  @DubboReference
  public BackendUserService backendUserService;

  /**
   * 模型回调地址
   */
  @Value("${invoke.base-url}")
  private String invokeBaseUrl;

  /**
   * 根据运行ID获取工作流运行详情
   *
   * @param workflowRunId 工作流运行ID
   * @return 工作流运行详情
   */
  @Override
  public CommonRespDto<WorkflowRunsDto> workflowRunDetail(String workflowRunId) {
    // 根据workflowRunId查询工作流运行详情
    WorkflowRunsPo workflowRunsPo = workflowRunsMapper.selectOne(
        new LambdaQueryWrapper<WorkflowRunsPo>().eq(WorkflowRunsPo::getWorkflow_run_id,
            workflowRunId));

    if (workflowRunsPo == null) {
      return CommonRespDto.error("未找到对应的工作流运行记录");
    }
    CommonRespDto<Map<Integer, String>> accountMapByUserIds = backendUserInfoService.getAccountMapByUserIds(
        CollUtil.newArrayList(workflowRunsPo.getCreated_by()));
    Map<Integer, String> accountMapByUserIdsData = accountMapByUserIds.getData();
    // 将PO对象转换为DTO对象
    WorkflowRunsDto workflowRunsDto = WorkflowRunsDto.builder().id(workflowRunsPo.getId())
        .workflow_run_id(workflowRunsPo.getWorkflow_run_id())
        .tenant_id(workflowRunsPo.getTenant_id()).app_id(workflowRunsPo.getApp_id())
        .workflow_id(workflowRunsPo.getWorkflow_id()).type(workflowRunsPo.getType())
        .triggered_from(workflowRunsPo.getTriggered_from()).version(workflowRunsPo.getVersion())
        .graph(JacksonUtil.readTree(workflowRunsPo.getGraph()))
        .inputs(JacksonUtil.readTree(workflowRunsPo.getInputs())).status(workflowRunsPo.getStatus())
        .outputs(JacksonUtil.readTree(workflowRunsPo.getOutputs())).error(workflowRunsPo.getError())
        .elapsed_time(workflowRunsPo.getElapsed_time())
        .total_tokens(workflowRunsPo.getTotal_tokens()).total_steps(workflowRunsPo.getTotal_steps())
        .exceptions_count(workflowRunsPo.getExceptions_count())
        .created_by(workflowRunsPo.getCreated_by()).created_at(workflowRunsPo.getCreated_at())
        .createdAccount(accountMapByUserIdsData.get(workflowRunsPo.getCreated_by()))
        .finished_at(workflowRunsPo.getFinished_at()).build();
    return CommonRespDto.success(workflowRunsDto);
  }

  /**
   * 根据工作流运行ID获取节点执行记录列表
   *
   * @param workflowRunId 工作流运行ID
   * @return 节点执行记录列表
   */
  @Override
  public CommonRespDto<List<WorkflowNodeExecutionsDto>> getNodeExecutionsByWorkflowRunId(
      String workflowRunId) {

    log.info("根据工作流运行ID获取节点执行记录列表, workflowRunId={}", workflowRunId);

    // 1. 查询数据
    List<WorkflowNodeExecutionsPo> poList = workflowNodeExecutionsMapper.selectList(
        new LambdaQueryWrapper<WorkflowNodeExecutionsPo>().eq(
                WorkflowNodeExecutionsPo::getWorkflow_run_id, workflowRunId)
            .orderByAsc(WorkflowNodeExecutionsPo::getCreateTime));

    if (CollUtil.isEmpty(poList)) {
      return CommonRespDto.success(new ArrayList<>());
    }

    // 2. 转 DTO 并填充执行人字段
    List<WorkflowNodeExecutionsDto> dtoList = workflowConverter.nodeExecutionPoToDto(poList);

    List<Integer> list = dtoList.stream().map(WorkflowNodeExecutionsDto::getToolAppId)
        .filter(x -> x != null && x != 0).toList();
    List<ApplicationPo> applicationPos;
    if (CollUtil.isEmpty(list)) {
      applicationPos = new ArrayList<>();
    } else {
      applicationPos = applicationMapper.selectBatchIds(list);
    }
    Map<Integer, ApplicationPo> applicationPoMap;
    if (CollUtil.isNotEmpty(applicationPos)) {
      applicationPoMap = applicationPos.stream()
          .collect(Collectors.toMap(ApplicationPo::getId, x -> x));
    } else {
      applicationPoMap = new HashMap<>();
    }
    for (WorkflowNodeExecutionsDto dto : dtoList) {
      BackendUserDto userDto = backendUserService.getUserInfo(dto.getCreatedBy()).getData();
      if (userDto != null) {
        dto.setExecutor_name(userDto.getAccount());
      }
      // 设置图标
      if (dto.getToolAppId() != null && dto.getToolAppId() != 0) {
        if (dto.getNode_type().equals(NodeType.MCP.getValue())) {
          McpPo mcpPo = mcpMapper.selectById(dto.getToolAppId());
          dto.setIconUrl(mcpPo.getMcpIconUrl());
        } else {
          dto.setIconUrl(applicationPoMap.get(dto.getToolAppId()).getIconUrl());
        }
      }
    }

    // 3. 构建 idMap（注意：putIfAbsent 保留第一次出现的记录，防止被后面相同 node_id 的临时循环记录覆盖）
    Map<String, WorkflowNodeExecutionsDto> idMap = new LinkedHashMap<>();
    List<WorkflowNodeExecutionsDto> topLevel = new ArrayList<>();
    for (WorkflowNodeExecutionsDto dto : dtoList) {
      idMap.putIfAbsent(dto.getNode_id(), dto); // 关键改动：不覆盖已有 entry

      if (StrUtil.isBlank(dto.getPredecessor_node_id())) {
        topLevel.add(dto);
      }
    }

    // ========== 若没有循环节点，直接返回 ==========
    boolean hasLoop = dtoList.stream()
        .anyMatch(x -> StrUtil.isNotBlank(x.getPredecessor_node_id()));

    if (!hasLoop) {
      return CommonRespDto.success(topLevel);
    }

    for (WorkflowNodeExecutionsDto dto : dtoList) {

      String parentId = dto.getPredecessor_node_id();
      Integer loopIndex = dto.getLoop_index();

      // 非循环子节点跳过
      if (StrUtil.isBlank(parentId)) {
        continue;
      }

      WorkflowNodeExecutionsDto parent = idMap.get(parentId);
      if (parent == null) {
        // 找不到父节点，兜底放到顶层
        topLevel.add(dto);
        continue;
      }

      // 确保父节点 loopList 已初始化
      if (parent.getLoopList() == null) {
        parent.setLoopList(new ArrayList<>());
      }
      List<WorkflowNodeExecutionsDto> parentLoops = parent.getLoopList();

      // 确保 parentLoops 能容纳到 loopIndex（每轮初始放一个父节点的“壳”拷贝）
      while (parentLoops.size() <= loopIndex) {
        WorkflowNodeExecutionsDto emptyShell = new WorkflowNodeExecutionsDto();
        emptyShell.setLoopList(new ArrayList<>());
        emptyShell.setElapsed_time(0d);
        parentLoops.add(emptyShell);
      }

      // 如果当前记录本身是 loop 类型（即这条记录表示“某次循环开始”的临时记录），
      if (dto.getNode_type() != null && (dto.getNode_type().equals(NodeType.LOOP.getValue())
          || dto.getNode_type().equals(NodeType.ITERATION.getValue()))) {
        WorkflowNodeExecutionsDto shellFromDto = new WorkflowNodeExecutionsDto();
        BeanUtil.copyProperties(dto, shellFromDto);
        // 保证内部 list 初始化（子节点会追加到这个 list）
        if (shellFromDto.getLoopList() == null) {
          shellFromDto.setLoopList(new ArrayList<>());
        }
        // 若 dto 自带 elapsed_time，保留它（但后面我们会以子节点求和为主）
        if (shellFromDto.getElapsed_time() == null) {
          shellFromDto.setElapsed_time(0d);
        }
        parentLoops.set(loopIndex, shellFromDto);
        // 不把 loop-start 记录放进子节点列表（它本身就是一轮的壳），继续下一条记录
        continue;
      }

      // 正常子节点，加入对应轮次的子列表
      WorkflowNodeExecutionsDto loopRound = parentLoops.get(loopIndex);
      // 防御性：确保 loopRound.loopList 已初始化
      if (loopRound.getLoopList() == null) {
        loopRound.setLoopList(new ArrayList<>());
      }
      loopRound.getLoopList().add(dto);
    }
    // 4. 汇总 elapsed_time，并排序
    for (WorkflowNodeExecutionsDto parent : topLevel) {
      List<WorkflowNodeExecutionsDto> loops = parent.getLoopList();
      if (CollUtil.isEmpty(loops)) {
        continue;
      }
      parent.getLoopList().removeIf(round -> {
        List<WorkflowNodeExecutionsDto> sub = round.getLoopList();
        return CollUtil.isEmpty(sub);
      });
      for (WorkflowNodeExecutionsDto round : loops) {
        List<WorkflowNodeExecutionsDto> sub = round.getLoopList();
        // 每轮按创建时间排
        sub.sort(Comparator.comparing(WorkflowNodeExecutionsDto::getCreateTime));
      }
      // 防止展示迭代节点中占位但未执行的
      parent.getLoopList().removeIf(e -> e.getId() == null);
    }

    return CommonRespDto.success(topLevel);
  }

  /**
   * 发布工作流
   *
   * @param appId the app id
   * @return the common resp dto
   */
  @Override
  public CommonRespDto<String> publish(Integer appId) {
    log.info("发布工作流, appId={}", appId);
    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(appId);
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    WorkflowPo workflowPo = workflowMapper.selectOne(
        Wrappers.<WorkflowPo>lambdaQuery().eq(WorkflowPo::getApp_id, appId));
    if (workflowPo == null) {
      return CommonRespDto.error("未找到对应工作流");
    }
    Integer userId = UserAuthUtil.getUserId();
    ApplicationDto applicationDto = dtoCommonRespDto.getData();
    WorkflowsPublishHistoryPo historyPo = WorkflowsPublishHistoryPo.builder()
        .app_id(workflowPo.getApp_id()).type(workflowPo.getType())
        .version(UUID.randomUUID().toString()).graph(workflowPo.getGraph())
        .features(workflowPo.getFeatures())
        .environment_variables(workflowPo.getEnvironment_variables())
        .conversation_variables(workflowPo.getConversation_variables())
        .create_time(LocalDateTime.now()).create_by(userId)
        .appName(applicationDto.getName()).appIconUrl(applicationDto.getIconUrl())
        .build();
    workflowsPublishHistoryMapper.insert(historyPo);

    applicationMapper.updateById(
        ApplicationPo.builder().apiAccessable(Boolean.TRUE).urlAccessable(Boolean.TRUE).id(appId)
            .status(1).build());

    if (StrUtil.isBlank(applicationDto.getUrlKey())) {
      CommonRespDto<String> respDto = applicationService.regenerateUrl(appId);
      if (!respDto.isOk()) {
        return CommonRespDto.error(respDto.getMsg());
      } else {
        return CommonRespDto.success(respDto.getData());
      }
    }
    return CommonRespDto.success(applicationDto.getUrlKey());
  }

  /**
   * 获取工作流参数
   *
   * @param appId 应用ID
   * @return 工作流运行列表
   */
  @Override
  public CommonRespDto<List<Variable>> getPublishWorkflowParams(Integer appId) {
    log.info("获取发布工作流参数, appId={}", appId);
    WorkflowsPublishHistoryPo workflowPo = workflowsPublishHistoryMapper.selectOne(
        Wrappers.<WorkflowsPublishHistoryPo>lambdaQuery()
            .eq(WorkflowsPublishHistoryPo::getApp_id, appId)
            .orderByDesc(WorkflowsPublishHistoryPo::getCreate_time).last("LIMIT 1"));
    JSONObject graph = JSONUtil.parseObj(workflowPo.getGraph());
    // 根据节点ID中提取节点配置
    JSONArray nodes = JSONUtil.getByPath(graph, "nodes", JSONUtil.createArray());
    Optional<Object> startNode = nodes.stream().filter(nodeObj -> {
      JSONObject nodeJson = (JSONObject) nodeObj;
      return NodeType.START.getValue().equals(nodeJson.getStr("type"));
    }).findFirst();
    if (startNode.isEmpty()) {
      return CommonRespDto.error("未找到开始节点");
    }
    JSONObject node = (JSONObject) startNode.get();
    StartNode startNodeData = JSONUtil.toBean(JSONUtil.getByPath(node, "data", ""),
        StartNode.class);
    List<Variable> variables = startNodeData.getVariables();
    return CommonRespDto.success(variables);
  }

  /**
   * 获取上架的工作流参数
   *
   * @param appId 应用ID
   * @return 工作流运行列表
   */
  @Override
  public CommonRespDto<List<Variable>> getExperienceWorkflowParams(Integer appId) {
    log.info("获取上架工作流参数, appId={}", appId);
    WorkflowsExperiencePo workflowPo = workflowsExperienceMapper.selectOne(
        Wrappers.<WorkflowsExperiencePo>lambdaQuery().eq(WorkflowsExperiencePo::getApp_id, appId));
    JSONObject graph = JSONUtil.parseObj(workflowPo.getGraph());
    // 根据节点ID中提取节点配置
    JSONArray nodes = JSONUtil.getByPath(graph, "nodes", JSONUtil.createArray());
    Optional<Object> startNode = nodes.stream().filter(nodeObj -> {
      JSONObject nodeJson = (JSONObject) nodeObj;
      return NodeType.START.getValue().equals(nodeJson.getStr("type"));
    }).findFirst();
    if (startNode.isEmpty()) {
      return CommonRespDto.error("未找到开始节点");
    }
    JSONObject node = (JSONObject) startNode.get();
    StartNode startNodeData = JSONUtil.toBean(JSONUtil.getByPath(node, "data", ""),
        StartNode.class);
    List<Variable> variables = startNodeData.getVariables();
    return CommonRespDto.success(variables);
  }


  /**
   * 上架.
   *
   * @param shelfRequest the app id
   * @return the common resp dto
   */
  @Override
  public CommonRespDto<Void> onShelf(ShelfRequest shelfRequest) {
    log.info("上架, appId={}", shelfRequest.getAppId());

    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(
        shelfRequest.getAppId());
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, shelfRequest.getAppId()));
    // 如果存在，则更新
    Integer applicationExperienceId;
    Integer userId = UserAuthUtil.getUserId();
    if (applicationExperiencePo != null) {
      applicationExperiencePo.setAppId(shelfRequest.getAppId()).setType(applicationDto.getType())
          .setName(applicationDto.getName()).setDescription(applicationDto.getDescription())
          .setIconUrl(applicationDto.getIconUrl()).setCreateBy(userId)
          .setCreateTime(LocalDateTime.now())
          .setEnableWorkflowTrace(shelfRequest.getEnableWorkflowTrace());
      applicationExperienceMapper.updateById(applicationExperiencePo);
      applicationExperienceId = applicationExperiencePo.getId();
      WorkflowsExperiencePo workflowsExperiencePo = workflowsExperienceMapper.selectOne(
          Wrappers.<WorkflowsExperiencePo>lambdaQuery()
              .eq(WorkflowsExperiencePo::getApp_id, shelfRequest.getAppId()));
      WorkflowPo workflowPo = workflowMapper.selectOne(
          Wrappers.<WorkflowPo>lambdaQuery().eq(WorkflowPo::getApp_id, shelfRequest.getAppId()));
      workflowsExperiencePo.setApp_id(shelfRequest.getAppId()).setType(workflowPo.getType())
          .setGraph(workflowPo.getGraph()).setFeatures(workflowPo.getFeatures())
          .setEnvironment_variables(workflowPo.getEnvironment_variables())
          .setConversation_variables(workflowPo.getConversation_variables());
      workflowsExperienceMapper.updateById(workflowsExperiencePo);
    } else {
      ApplicationExperiencePo experiencePo = new ApplicationExperiencePo().setAppId(
              shelfRequest.getAppId()).setType(applicationDto.getType())
          .setName(applicationDto.getName())
          .setDescription(applicationDto.getDescription()).setIconUrl(applicationDto.getIconUrl())
          .setCreateTime(LocalDateTime.now()).setCreateBy(userId)
          .setEnableWorkflowTrace(shelfRequest.getEnableWorkflowTrace());
      applicationExperienceMapper.insert(experiencePo);
      applicationExperienceId = experiencePo.getId();
      WorkflowPo workflowPo = workflowMapper.selectOne(
          Wrappers.<WorkflowPo>lambdaQuery().eq(WorkflowPo::getApp_id, shelfRequest.getAppId()));
      WorkflowsExperiencePo workflowsExperiencePo = WorkflowsExperiencePo.builder()
          .app_id(shelfRequest.getAppId()).type(workflowPo.getType()).graph(workflowPo.getGraph())
          .features(workflowPo.getFeatures())
          .environment_variables(workflowPo.getEnvironment_variables())
          .conversation_variables(workflowPo.getConversation_variables())
          .create_time(LocalDateTime.now()).create_by(userId).build();
      workflowsExperienceMapper.insert(workflowsExperiencePo);
    }
    if (CollUtil.isNotEmpty(shelfRequest.getTagIdList())) {
      applicationExperienceTagRelationMapper.delete(
          Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
              .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                  applicationExperienceId));
      MybatisPlusUtil<ApplicationExperienceTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          applicationExperienceTagRelationMapper, ApplicationExperienceTagRelationPo.class);
      List<ApplicationExperienceTagRelationPo> collect = shelfRequest.getTagIdList().stream().map(
          categoryId -> ApplicationExperienceTagRelationPo.builder().tagId(categoryId)
              .experienceApplicationId(applicationExperienceId).build()).toList();
      mybatisPlusUtil.saveBatch(collect, collect.size());
    } else {
      applicationExperienceTagRelationMapper.delete(
          Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
              .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                  applicationExperienceId));
    }
    return CommonRespDto.success();
  }

  /**
   * 下架
   *
   * @param appId the app id
   * @return the common resp dto
   */
  @Override
  public CommonRespDto<Void> offShelf(Integer appId) {
    log.info("下架, appId={}", appId);
    ApplicationExperiencePo experiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    if (experiencePo == null) {
      return CommonRespDto.error("数据不存在");
    }
    applicationExperienceMapper.deleteById(experiencePo.getId());
    workflowsExperienceMapper.delete(Wrappers.<WorkflowsExperiencePo>lambdaQuery()
        .eq(WorkflowsExperiencePo::getApp_id, experiencePo.getAppId()));
    applicationExperienceTagRelationMapper.delete(
        Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
            .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                experiencePo.getId()));
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<List<ApplicationExperienceTagDto>> listExperienceTags(Boolean all) {
    List<ApplicationExperienceTagPo> applicationExperienceTagPos = applicationExperienceTagMapper.selectList(
        Wrappers.emptyWrapper());
    List<ApplicationExperienceTagDto> applicationExperienceTagDtos = applicationTagConverter.experiencePoToDtoList(
        applicationExperienceTagPos);
    List<ApplicationExperienceTagRelationPo> relationPos = applicationExperienceTagRelationMapper.selectList(
        Wrappers.emptyWrapper());
    applicationExperienceTagDtos.forEach(tagDto -> tagDto.setTagUsedNumber(
        relationPos.stream().filter(relationPo -> relationPo.getTagId().equals(tagDto.getId()))
            .count()));
    if (!all) {
      applicationExperienceTagDtos = applicationExperienceTagDtos.stream()
          .filter(tagDto -> tagDto.getTagUsedNumber() > 0).toList();
    }
    applicationExperienceTagDtos = applicationExperienceTagDtos.stream().sorted(
            Comparator.comparing(ApplicationExperienceTagDto::getIsBuiltIn, Comparator.reverseOrder())
                .thenComparing(ApplicationExperienceTagDto::getCreateTime, Comparator.reverseOrder()))
        .toList();

    return CommonRespDto.success(applicationExperienceTagDtos);
  }

  @Override
  public CommonRespDto<Void> addExperienceTag(ApplicationExperienceTagDto tagDto) {
    if (tagDto.getName().length() > 10) {
      return CommonRespDto.error("分类名称长度不能超过10");
    }
    // 检查标签数量是否已达上限30个
    if (applicationExperienceTagMapper.selectCount(Wrappers.emptyWrapper()) >= 30) {
      return CommonRespDto.error("最多添加30个分类");
    }
    if (applicationExperienceTagMapper.selectCount(
        Wrappers.<ApplicationExperienceTagPo>lambdaQuery()
            .eq(ApplicationExperienceTagPo::getName, tagDto.getName())) > 0) {
      return CommonRespDto.error("分类名称已存在");
    }
    applicationExperienceTagMapper.insert(applicationTagConverter.experienceDtoToPo(tagDto));
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> updateExperienceTag(ApplicationExperienceTagDto tagDto) {
    if (tagDto.getName().length() > 10) {
      return CommonRespDto.error("分类名称长度不能超过10");
    }
    if (applicationExperienceTagMapper.selectCount(
        Wrappers.<ApplicationExperienceTagPo>lambdaQuery()
            .eq(ApplicationExperienceTagPo::getName, tagDto.getName())
            .ne(ApplicationExperienceTagPo::getId, tagDto.getId())) > 0) {
      return CommonRespDto.error("分类名称已存在");
    }
    ApplicationExperienceTagPo tagPo = applicationTagConverter.experienceDtoToPo(tagDto);
    applicationExperienceTagMapper.updateById(tagPo);
    return CommonRespDto.success();
  }

  @Override
  @Transactional
  public CommonRespDto<Void> deleteExperienceTag(Integer tagId) {
    log.info("删除分类, tagId={}", tagId);
    ApplicationExperienceTagPo applicationExperienceTagPo = applicationExperienceTagMapper.selectById(
        tagId);
    if (applicationExperienceTagPo.getIsBuiltIn()) {
      return CommonRespDto.error("内置分类不能删除");
    }
    applicationExperienceTagMapper.deleteById(tagId);
    applicationExperienceTagRelationMapper.delete(
        Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
            .eq(ApplicationExperienceTagRelationPo::getTagId, tagId));
    return CommonRespDto.success();
  }

  /**
   * 获取工作流详情
   *
   * @param id 工作流ID
   * @return 工作流详情
   */
  @Override
  public CommonRespDto<WorkflowInfoResponseDto> getById(Integer id) {
    log.info("获取工作流详情, appid={}", id);
    WorkflowPo po = baseMapper.selectOne(
        Wrappers.<WorkflowPo>lambdaQuery().eq(WorkflowPo::getApp_id, id));
    if (ObjUtil.isNull(po)) {
      return CommonRespDto.error("数据不存在");
    }
    WorkflowInfoResponseDto workflowInfoResponseDto = workflowConverter.poToInfoDto(po);
    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(id);
    if (!dtoCommonRespDto.isOk()) {
      return CommonRespDto.error(dtoCommonRespDto.getMsg());
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();

    workflowInfoResponseDto.setApplication_name(applicationDto.getName())
        .setStatus(applicationDto.getStatus()).setApplication_icon_url(applicationDto.getIconUrl());
    ObjectNode graphNode = (ObjectNode) workflowInfoResponseDto.getGraph();
    JsonNode nodeConfigs = JacksonUtil.at(graphNode, "/nodes");
    List<JsonNode> nodeList = new ArrayList<>();
    for (JsonNode jsonNode : nodeConfigs) {
      nodeList.add(jsonNode); // 转字符串核心方法
    }
    List<ObjectNode> list = nodeList.stream().map(nodeConfig -> {
      ObjectNode objectNode = (ObjectNode) nodeConfig;
      String nodeType = objectNode.get("type").asText();
      if (!nodeType.equals(NodeType.WORKFLOW.getValue()) && !nodeType.equals(
          NodeType.AGENT.getValue())) {
        return objectNode;
      }
      JsonNode nodeData = JacksonUtil.at(objectNode, "/data");
      ObjectNode objNode = (ObjectNode) nodeData;
      Integer appId = objNode.at("/appId").asInt();
      CommonRespDto<Boolean> checkOnShelf = applicationService.checkOnShelf(appId);
      if (checkOnShelf.getData() == null) {
        return objectNode;
      }
      if (nodeType.equals(NodeType.WORKFLOW.getValue())) {
        CommonRespDto<WorkflowInfoResponseDto> experienceById = getExperienceById(appId);
        WorkflowInfoResponseDto data = experienceById.getData();
        objNode.put("iconUrl", data.getApplication_icon_url());
        objNode.putPOJO("tagList", data.getTags());
      } else {
        CommonRespDto<AgentInfoResponseDto> experienceInfo = agentInfoService.getExperienceInfo(
            appId);
        AgentInfoResponseDto data = experienceInfo.getData();
        objNode.put("iconUrl", data.getApplicationIconUrl());
        objNode.putPOJO("tagList", data.getTagList());
      }

      List<McpParam> oldParam = objNode.at("/param").isArray() ?
          JacksonUtil.MAPPER.convertValue(objNode.at("/param"),
              new TypeReference<>() {
              }) : new ArrayList<>();
      if (nodeType.equals(NodeType.WORKFLOW.getValue())) {
        List<Variable> variables = getExperienceWorkflowParams(appId).getData();
        if (CollUtil.isEmpty(variables)) {
          objNode.putNull("param");
          return objectNode;
        }
        Map<String, McpParam> oldParamMap = oldParam.stream()
            .collect(Collectors.toMap(McpParam::getName, v -> v));
        List<McpParam> newParams = new ArrayList<>();
        variables.forEach(variable -> {
          if (oldParamMap.containsKey(variable.getVariable())) {
            McpParam mcpParam = oldParamMap.get(variable.getVariable());

            boolean sameType = Objects.equals(getValueType(variable.getType()), mcpParam.getType());
            boolean sameRequired = Objects.equals(variable.getRequired(), mcpParam.getRequired());

            if (!sameType || !sameRequired) {
              // 有不同字段的，清空值
              mcpParam.setValue(NullNode.getInstance());
              if (!sameType) {
                mcpParam.setType(getValueType(variable.getType()));
              }
              if (!sameRequired) {
                // 有不同字段的，清空值
                mcpParam.setRequired(variable.getRequired());
              }
            }
            newParams.add(mcpParam);
          } else {
            newParams.add(McpParam.builder()
                .name(variable.getVariable())
                .type(getValueType(variable.getType()))
                .required(variable.getRequired())
                .value_type("Variable")
                .value(null)
                .build());
          }
        });
        objNode.putPOJO("param", newParams);
      } else {
        ApplicationDto agentNodeApp = applicationService.getById(appId).getData();
        if (AgentTypeEnum.SINGLE.getKey().equals(agentNodeApp.getAgentType())) {
          AgentExperiencePo agentExperiencePo = agentExperienceMapper.selectOne(
              Wrappers.<AgentExperiencePo>lambdaQuery()
                  .eq(AgentExperiencePo::getApplicationId, appId));
          if (agentExperiencePo == null) {
            return objectNode;
          }
          // 转化为DTO对象
          AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
              agentExperiencePo.getConfigData(), AgentInfoResponseDto.class);
          List<AgentVariableDto> variableList = agentInfoResponseDto.getVariableList();
          Map<String, McpParam> oldParamMap = oldParam.stream()
              .collect(Collectors.toMap(McpParam::getName, v -> v));
          List<McpParam> newParams = new ArrayList<>();
          variableList.forEach(variable -> {
            if (oldParamMap.containsKey(variable.getName())) {
              McpParam mcpParam = oldParamMap.get(variable.getName());

              boolean sameType = Objects.equals(getValueType(variable.getFieldType()),
                  mcpParam.getType());
              boolean sameRequired = Objects.equals(variable.getRequired(), mcpParam.getRequired());

              if (!sameType || !sameRequired) {
                // 有不同字段的，清空值
                mcpParam.setValue(NullNode.getInstance());
                if (!sameType) {
                  mcpParam.setType(getValueType(variable.getFieldType()));
                }
                if (!sameRequired) {
                  // 有不同字段的，清空值
                  mcpParam.setRequired(variable.getRequired());
                }
              }
              newParams.add(mcpParam);
            } else {
              newParams.add(McpParam.builder()
                  .name(variable.getName())
                  .type(getValueType(variable.getFieldType()))
                  .required(variable.getRequired())
                  .value_type("Variable")
                  .value(null)
                  .build());
            }
          });
          objNode.putPOJO("param", newParams);
        } else {
          AgentExperiencePo agentExperiencePo = agentExperienceMapper.selectOne(
              Wrappers.<AgentExperiencePo>lambdaQuery()
                  .eq(AgentExperiencePo::getApplicationId, appId));
          if (agentExperiencePo == null) {
            return objectNode;
          }
          // 转化为DTO对象
          AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
              agentExperiencePo.getConfigData(), AgentInfoResponseDto.class);

          // key为1&param1 1&param2  2&param1  2&param2
          Map<String, McpParam> oldParamMap = oldParam.stream()
              .collect(Collectors.toMap(McpParam::getName, v -> v));
          // 根据key的前缀进行分组
          Map<String, Map<String, McpParam>> groupedMap = oldParamMap.entrySet().stream()
              .collect(Collectors.groupingBy(
                  entry -> {
                    String key = entry.getKey();
                    int dotIndex = key.indexOf('&');
                    if (dotIndex > 0) {
                      return key.substring(0, dotIndex);
                    }

                    return key;
                  },
                  Collectors.toMap(
                      entry -> {
                        String key = entry.getKey();
                        int dotIndex = key.indexOf('&');
                        if (dotIndex > 0 && dotIndex < key.length() - 1) {
                          return key.substring(dotIndex + 1);
                        }
                        return key;
                      },
                      Map.Entry::getValue
                  )
              ));

          List<McpParam> newParams = new ArrayList<>();
          if (ArrayUtils.isNotEmpty(agentInfoResponseDto.getSubAgentAppIds())) {
            for (int subAgentAppId : agentInfoResponseDto.getSubAgentAppIds()) {
              CommonRespDto<ApplicationDto> dtoCommonRespDto1 = applicationService.getById(
                  subAgentAppId);
              if (!dtoCommonRespDto1.isOk()) {
                continue;
              }
              AgentPublishHistoryPo subAgentPublishPo = agentPublishHistoryMapper.selectOne(
                  Wrappers.<AgentPublishHistoryPo>lambdaQuery()
                      .eq(AgentPublishHistoryPo::getApplicationId, subAgentAppId)
                      .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
              AgentInfoResponseDto subAgentInfoResponseDto = JacksonUtil.toBean(
                  subAgentPublishPo.getConfigData(), AgentInfoResponseDto.class);
              List<AgentVariableDto> variableList = subAgentInfoResponseDto.getVariableList();
              Map<String, McpParam> oldSubAgentParamMap = groupedMap.get(
                  String.valueOf(subAgentAppId));
              variableList.forEach(variable -> {
                if (CollUtil.isNotEmpty(oldSubAgentParamMap) && oldSubAgentParamMap.containsKey(variable.getName())) {
                  McpParam mcpParam = oldSubAgentParamMap.get(variable.getName());

                  boolean sameType = Objects.equals(getValueType(variable.getFieldType()),
                      mcpParam.getType());
                  boolean sameRequired = Objects.equals(variable.getRequired(),
                      mcpParam.getRequired());

                  if (!sameType || !sameRequired) {
                    // 有不同字段的，清空值
                    mcpParam.setValue(NullNode.getInstance());
                    if (!sameType) {
                      mcpParam.setType(getValueType(variable.getFieldType()));
                    }
                    if (!sameRequired) {
                      // 有不同字段的，清空值
                      mcpParam.setRequired(variable.getRequired());
                    }
                  }
                  newParams.add(mcpParam);
                } else {
                  newParams.add(McpParam.builder()
                      .name(subAgentAppId + "." + variable.getName())
                      .type(getValueType(variable.getFieldType()))
                      .required(variable.getRequired())
                      .value_type("Variable")
                      .value(null)
                      .build());
                }
              });
            }
          }
          objNode.putPOJO("param", newParams);
        }
      }
      objectNode.putPOJO("data", objNode);
      return objectNode;
    }).toList();
    graphNode.set("nodes", JacksonUtil.MAPPER.valueToTree(list));
    return CommonRespDto.success(workflowInfoResponseDto);
  }

  private static String getValueType(String fieldType) {
    return switch (fieldType) {
      case "text", "paragraph", "select", "text-input" -> "string";
      case "number" -> "number";
      case "file" -> "file";
      case "file-list" -> "array[file]";
      default -> fieldType;
    };
  }

  /**
   * 根据ID获取发现页工作流详情
   *
   * @param id 工作流ID
   * @return 工作流详情
   */
  @Override
  public CommonRespDto<WorkflowInfoResponseDto> getExperienceById(Integer id) {
    log.info("获取发现页工作流详情, appid={}", id);
    WorkflowsExperiencePo workflowsExperiencePo = workflowsExperienceMapper.selectOne(
        Wrappers.<WorkflowsExperiencePo>lambdaQuery().eq(WorkflowsExperiencePo::getApp_id, id));
    if (ObjUtil.isNull(workflowsExperiencePo)) {
      return CommonRespDto.success();
    }
    WorkflowInfoResponseDto workflowInfoResponseDto = workflowConverter.poToInfoDto(
        workflowsExperiencePo);
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery().eq(ApplicationExperiencePo::getAppId, id));
    if (ObjUtil.isNull(applicationExperiencePo)) {
      return CommonRespDto.success();
    }
    workflowInfoResponseDto.setApplication_name(applicationExperiencePo.getName())
        .setApplication_icon_url(applicationExperiencePo.getIconUrl())
        .setEnableWorkflowTrace(applicationExperiencePo.getEnableWorkflowTrace());
    List<ApplicationExperienceTagRelationPo> relationPos = applicationExperienceTagRelationMapper.selectList(
        Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
            .eq(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                applicationExperiencePo.getId()));
    if (CollUtil.isNotEmpty(relationPos)) {
      Set<Integer> collect = relationPos.stream().map(ApplicationExperienceTagRelationPo::getTagId)
          .collect(Collectors.toSet());
      List<ApplicationExperienceTagPo> applicationExperienceTagPos = applicationExperienceTagMapper.selectList(
          Wrappers.<ApplicationExperienceTagPo>lambdaQuery()
              .in(ApplicationExperienceTagPo::getId, collect));
      workflowInfoResponseDto.setTags(
          applicationTagConverter.experiencePoToDtoList(applicationExperienceTagPos));
    }
    // 设置用户名
    BackendUserDto backendUserDto = backendUserService.getUserInfo(
        workflowInfoResponseDto.getCreateBy()).getData();
    workflowInfoResponseDto.setCreateUsername(backendUserDto.getUsername());
    workflowInfoResponseDto.setApplicationDescription(applicationExperiencePo.getDescription());

    return CommonRespDto.success(workflowInfoResponseDto);
  }


  /**
   * 更新工作流
   *
   * @param dto 工作流信息
   * @return 是否成功
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> update(WorkflowDto dto) {
    WorkflowPo po = workflowConverter.dtoToPo(dto);
    int update = workflowMapper.updateById(po);
    addKnowledgeRelations(dto);

    if (update > 0) {
      JSONObject jsonObject = JSONUtil.createObj().set("workflowId", po.getId())
          .set("update_request_uuid", dto.getUpdate_request_uuid());
      messageHandler.sendMessage(MessageTypeEnum.WORKFLOW_UPDATE_NOTICE,
          MessageTypeEnum.WORKFLOW_UPDATE_NOTICE.getMessage(), UserAuthUtil.getUserId(),
          Type.NOTICE, JSONUtil.toJsonStr(jsonObject), "", false);
      return CommonRespDto.success();
    }
    return CommonRespDto.error("更新失败");
  }

  private void addKnowledgeRelations(WorkflowDto dto) {
    JSONObject graph = JSONUtil.parseObj(dto.getGraph());
    JSONArray nodes = JSONUtil.getByPath(graph, "nodes", JSONUtil.createArray());
    Set<Object> knowledgeNodes = nodes.stream().filter(e -> {
      JSONObject nodeJson = (JSONObject) e;
      return NodeType.KNOWLEDGE_RETRIEVAL.getValue().equals(nodeJson.getStr("type"));
    }).collect(Collectors.toSet());
    if (!knowledgeNodes.isEmpty()) {
      knowledgeBaseApplicationRelationMapper.delete(
          new LambdaQueryWrapper<KnowledgeBaseApplicationRelationPo>().eq(
              KnowledgeBaseApplicationRelationPo::getApplicationId, dto.getApp_id()));
      Set<KnowledgeBaseApplicationRelationPo> relationPos = new HashSet<>();
      Map<String, KnowledgeBaseApplicationRelationPo> uniqueMap = new HashMap<>();
      for (Object object : knowledgeNodes) {
        JSONObject node = (JSONObject) object;
        JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
        KnowledgeRetrievalNode data = JSONUtil.toBean(jsonObject, KnowledgeRetrievalNode.class);
        if (data.getDataSet_list() == null) {
          continue;
        }
        data.getDataSet_list().forEach(e -> {
          Integer kbId = e.getId();
          Integer appId = dto.getApp_id();

          // 用 kbId + appId 作为唯一键避免插入重复数据
          String key = kbId + "@" + appId;

          if (!uniqueMap.containsKey(key)) {
            KnowledgeBaseApplicationRelationPo relationPo = new KnowledgeBaseApplicationRelationPo();
            relationPo.setKnowledgeBaseId(kbId);
            relationPo.setApplicationId(appId);
            uniqueMap.put(key, relationPo);
            relationPos.add(relationPo);
          }
        });
      }
      if (CollUtil.isNotEmpty(relationPos)) {
        MybatisPlusUtil<KnowledgeBaseApplicationRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
            knowledgeBaseApplicationRelationMapper, KnowledgeBaseApplicationRelationPo.class);
        mybatisPlusUtil.saveBatch(relationPos, relationPos.size());
      }
    }
  }

  @Override
  public CommonRespDto<List<Map<String, String>>> getSystemVariables(Integer workflowId) {
    log.info("获取系统变量, workflowId={}", workflowId);
    List<Map<String, String>> varList = new ArrayList<>();
    varList.add(Map.of("name", "sys.user_id", "type", "string"));
    varList.add(Map.of("name", "sys.app_id", "type", "string"));
    varList.add(Map.of("name", "sys.workflow_id", "type", "string"));
    varList.add(Map.of("name", "sys.workflow_run_id", "type", "string"));
    return CommonRespDto.success(varList);
  }


  @Override
  public CommonRespDto<WorkflowNodeExecutionsDto> lastRun(Integer appId, String nodeId) {
    log.info("获取工作流最后运行结果, appId={}, nodeId={}", appId, nodeId);
    WorkflowNodeExecutionsPo workflowNodeExecutionsPo = workflowNodeExecutionsMapper.selectOne(
        Wrappers.<WorkflowNodeExecutionsPo>lambdaQuery()
            .eq(WorkflowNodeExecutionsPo::getApp_id, appId)
            .eq(WorkflowNodeExecutionsPo::getNode_execution_id, nodeId)
            .orderByDesc(WorkflowNodeExecutionsPo::getCreateTime).last("limit 1"));

    WorkflowNodeExecutionsDto dto = workflowConverter.nodeExecutionPoToDto(
        workflowNodeExecutionsPo);
    if (dto != null && dto.getCreatedBy() != null) {
      BackendUserDto backendUserDto = backendUserService.getUserInfo(dto.getCreatedBy()).getData();
      dto.setExecutor_name(backendUserDto.getAccount());
      if (dto.getNode_type().equals(NodeType.LOOP.getValue()) || dto.getNode_type()
          .equals(NodeType.ITERATION.getValue())) {
        List<WorkflowNodeExecutionsPo> poList = workflowNodeExecutionsMapper.selectList(
            Wrappers.<WorkflowNodeExecutionsPo>lambdaQuery()
                .eq(WorkflowNodeExecutionsPo::getWorkflow_run_id, dto.getWorkflow_run_id())
                .eq(WorkflowNodeExecutionsPo::getPredecessor_node_id, nodeId));
        List<WorkflowNodeExecutionsDto> dtoList = workflowConverter.nodeExecutionPoToDto(poList);
        if (CollUtil.isEmpty(dtoList)) {
          return CommonRespDto.success(dto);
        }
        // 先按 loop_index 分组
        Map<Integer, List<WorkflowNodeExecutionsDto>> loopIndexMap = dtoList.stream()
            .collect(Collectors.groupingBy(WorkflowNodeExecutionsDto::getLoop_index));
        if (dto.getLoopList() == null) {
          dto.setLoopList(new ArrayList<>());
        }

        // 遍历每个 loop_index，按顺序组装
        loopIndexMap.keySet().stream().sorted().forEach(loopIndex -> {
          List<WorkflowNodeExecutionsDto> childList = loopIndexMap.get(loopIndex);
          // 按 createTime 排序
          childList.sort(Comparator.comparing(WorkflowNodeExecutionsDto::getCreateTime));

          // 创建当前轮壳（拷贝父节点，初始化 loopList）
          Optional<WorkflowNodeExecutionsDto> first = childList.stream()
              .filter(executionsDto -> executionsDto.getNode_type().equals(dto.getNode_type()))
              .findFirst();
          WorkflowNodeExecutionsDto loopShell = new WorkflowNodeExecutionsDto();
          if (first.isPresent()) {
            loopShell = first.get();
          }

          // 把本轮子节点放入壳的 loopList
          loopShell.getLoopList().addAll(
              childList.stream().filter(e -> !e.getNode_type().equals(dto.getNode_type()))
                  .toList());

          // 放入父节点 loopList
          dto.getLoopList().add(loopShell);
        });
      }
    }
    return CommonRespDto.success(dto);
  }

  @Override
  public CommonRespDto<Integer> reuse(ReuseRequest request) {

    Integer appId = request.getAppId();
    log.info("复用体验工作流:{}", appId);
    // 查询体验应用信息
    ApplicationExperiencePo applicationExperiencePo = applicationExperienceMapper.selectOne(
        Wrappers.<ApplicationExperiencePo>lambdaQuery()
            .eq(ApplicationExperiencePo::getAppId, appId));
    if (applicationExperiencePo == null) {
      return CommonRespDto.error("未找到体验工作流");
    }
    // 查询体验工作流
    WorkflowsExperiencePo workflowsExperiencePo = workflowsExperienceMapper.selectOne(
        Wrappers.<WorkflowsExperiencePo>lambdaQuery().eq(WorkflowsExperiencePo::getApp_id, appId));
    if (workflowsExperiencePo == null) {
      return CommonRespDto.error("未找到体验工作流");
    }
    Integer userId = UserAuthUtil.getUserId();
    // 插入应用
    ApplicationPo newApplicationPo = ApplicationPo.builder().name(request.getName())
        .description(request.getDescription()).iconUrl(applicationExperiencePo.getIconUrl())
        .type(applicationExperiencePo.getType()).createBy(userId).updateBy(userId).build();
    applicationMapper.insert(newApplicationPo);
    WorkflowPo workflowPo = WorkflowPo.builder().app_id(newApplicationPo.getId())
        .type(workflowsExperiencePo.getType()).version("draft")
        .graph(workflowsExperiencePo.getGraph()).features(workflowsExperiencePo.getFeatures())
        .environment_variables(workflowsExperiencePo.getEnvironment_variables())
        .conversation_variables(workflowsExperiencePo.getConversation_variables()).createBy(userId)
        .updateBy(userId).build();
    save(workflowPo);
    return CommonRespDto.success(newApplicationPo.getId());
  }

  @Override
  public CommonRespDto<ApiAccessResponse> apiAccess() {
    List<ApiInterfaceInfo> interfaceInfoList = List.of(ApiInterfaceInfo.builder().apiInterfaceUrl(
            invokeBaseUrl
                + "voicesagex-console/application-web/applicationExperience/workflow/getWorkflowParams")
        .apiInterfaceName("获取工作流参数").build(), ApiInterfaceInfo.builder()
        .apiInterfaceUrl(invokeBaseUrl + "voicesagex-console/application-web/api/upload")
        .apiInterfaceName("上传文件").build(), ApiInterfaceInfo.builder()
        .apiInterfaceUrl(invokeBaseUrl + "voicesagex-console/application-web/workflow/publishRun")
        .apiInterfaceName("工作流运行").build(), ApiInterfaceInfo.builder().apiInterfaceUrl(
            invokeBaseUrl + "voicesagex-console/application-web/applicationExperience/nodeExecutions")
        .apiInterfaceName("获取工作流日志").build());
    ApiAccessResponse apiAccessResponse = ApiAccessResponse.builder()
        .apiInterfaceInfoList(interfaceInfoList).build();
    return CommonRespDto.success(apiAccessResponse);
  }
}
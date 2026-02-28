package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentVariableService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentVariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentVarListDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.VariableTypeEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.AgentVariableConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentInfoMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentPublishHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentVariableMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentInfoPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentVariablePo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wangf
 * @date 2025/5/19 下午 1:51
 */
@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class AgentVariableServiceImpl extends
    ServiceImpl<AgentVariableMapper, AgentVariablePo> implements AgentVariableService {


  /**
   * 变量命名规则
   */
  public static final String VARIABLE_REGEX = "^[^0-9][A-Za-z0-9_]*$";
  private final AgentVariableConverter agentVariableConverter;

  private final AgentInfoMapper agentInfoMapper;
  private final AgentPublishHistoryMapper agentPublishHistoryMapper;
  private final AgentExperienceMapper agentExperienceMapper;

  @Override
  public CommonRespDto<Integer> add(AgentVariableDto dto) {
    log.info("开始新增智能体变量, 请求参数: {}", JSONUtil.toJsonStr(dto));
    // 变量名称不能以数字开头；只能包含英文字符、下划线和数字
    if (!dto.getName().matches(VARIABLE_REGEX)) {
      return CommonRespDto.error("变量名称不能以数字开头并且只能包含英文字符、下划线和数字");
    }
    if (ObjUtil.isNull(dto.getMaxLength())) {
      dto.setMaxLength(48);
    }
    AgentVariablePo po = agentVariableConverter.dtoToPo(dto);
    // 根据应用id和name查询
    Long count = baseMapper.selectCount(Wrappers.<AgentVariablePo>lambdaQuery()
        .eq(AgentVariablePo::getApplicationId, dto.getApplicationId())
        .eq(AgentVariablePo::getName, dto.getName()));
    if (count > 0) {
      return CommonRespDto.error("变量名称重复");
    }
    if (VariableTypeEnum.TEXT.getKey().equals(dto.getFieldType()) && (dto.getMaxLength() < 1
        || dto.getMaxLength() > 256)) {
      return CommonRespDto.error("文本类型变量范围为1-256");
    }
    if (VariableTypeEnum.PARAGRAPH.getKey().equals(dto.getFieldType()) && (dto.getMaxLength() < 1
        || dto.getMaxLength() > 9999)) {
      return CommonRespDto.error("段落类型变量范围为1-9999");
    }
    if (VariableTypeEnum.SELECT.getKey().equals(dto.getFieldType())) {
      if (StrUtil.isNotBlank(dto.getSelectOptions())) {
        List<String> list = StrUtil.split(dto.getSelectOptions(), ",");
        // 检测是否有重复
        HashSet<String> set = new HashSet<>(list);
        if (set.size() != list.size()) {
          return CommonRespDto.error("下拉选项重复");
        }
      }
    }

    int insertResult = baseMapper.insert(po);
    log.info("新增智能体变量成功, 生成ID: {}, 影响行数: {}", po.getId(), insertResult);
    return CommonRespDto.success(po.getId());
  }


  @Override
  public CommonRespDto<Void> batchAdd(List<AgentVariableDto> list) {
    log.info("开始批量新增智能体变量, 请求参数: {}", JSONUtil.toJsonStr(list));
    if (CollUtil.isEmpty(list)) {
      return CommonRespDto.error("参数不能为空");
    }
    List<String> distinctNameList = list.stream().map(AgentVariableDto::getName).distinct()
        .toList();
    if (distinctNameList.size() != list.size()) {
      return CommonRespDto.error("参数内部变量名称存在重复");
    }
    List<String> patternErrorList = new ArrayList<>();
    List<String> sameNameErrorList = new ArrayList<>();
    List<String> lengthErrorList = new ArrayList<>();

    list.forEach(dto -> {
      // 变量名称不能以数字开头；只能包含英文字符、下划线和数字
      if (!dto.getName().matches(VARIABLE_REGEX)) {
        patternErrorList.add(dto.getName());
      }
      // 根据应用id和name查询
      Long count = baseMapper.selectCount(Wrappers.<AgentVariablePo>lambdaQuery()
          .eq(AgentVariablePo::getApplicationId, dto.getApplicationId())
          .eq(AgentVariablePo::getName, dto.getName()));
      if (count > 0) {
        sameNameErrorList.add(dto.getName());
      }
      if (VariableTypeEnum.TEXT.getKey().equals(dto.getFieldType()) && (dto.getMaxLength() < 1
          || dto.getMaxLength() > 256)) {
        lengthErrorList.add(dto.getName());
      }
      if (VariableTypeEnum.PARAGRAPH.getKey().equals(dto.getFieldType()) && (dto.getMaxLength() < 1
          || dto.getMaxLength() > 9999)) {
        lengthErrorList.add(dto.getName());
      }
      AgentVariablePo po = agentVariableConverter.dtoToPo(dto);
      baseMapper.insert(po);
    });
    if (!patternErrorList.isEmpty()) {
      return CommonRespDto.error(
          "变量名称不能以数字开头并且只能包含英文字符、下划线和数字! " + StrUtil.join(",",
              patternErrorList));
    }
    if (!sameNameErrorList.isEmpty()) {
      return CommonRespDto.error("变量名称重复! " + StrUtil.join(",", sameNameErrorList));
    }
    if (!lengthErrorList.isEmpty()) {
      return CommonRespDto.error("变量最大长度范围错误! " + StrUtil.join(",", lengthErrorList));
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> update(AgentVariableDto dto) {
    log.info("开始更新智能体变量, ID: {}, 请求参数: {}", dto.getId(), dto);
    if (!dto.getName().matches(VARIABLE_REGEX)) {
      return CommonRespDto.error("变量名称不能以数字开头并且只能包含英文字符、下划线和数字");
    }
    if (ObjUtil.isNull(dto.getMaxLength())) {
      dto.setMaxLength(48);
    }

    // 根据应用id和name查询
    Long count = baseMapper.selectCount(Wrappers.<AgentVariablePo>lambdaQuery()
        .eq(AgentVariablePo::getApplicationId, dto.getApplicationId())
        .eq(AgentVariablePo::getName, dto.getName()).ne(AgentVariablePo::getId, dto.getId()));
    if (count > 0) {
      return CommonRespDto.error("变量名称重复");
    }
    if (StrUtil.isNotBlank(dto.getFieldType()) && ObjUtil.isNotNull(dto.getMaxLength())
        && VariableTypeEnum.TEXT.getKey().equals(dto.getFieldType()) && (dto.getMaxLength() < 1
        || dto.getMaxLength() > 256)) {
      return CommonRespDto.error("文本类型变量范围为1-256");
    }
    if (StrUtil.isNotBlank(dto.getFieldType()) && ObjUtil.isNotNull(dto.getMaxLength())
        && VariableTypeEnum.PARAGRAPH.getKey().equals(dto.getFieldType()) && (dto.getMaxLength() < 1
        || dto.getMaxLength() > 9999)) {
      return CommonRespDto.error("段落类型变量范围为1-9999");
    }
    if (VariableTypeEnum.SELECT.getKey().equals(dto.getFieldType())) {
      if (StrUtil.isNotBlank(dto.getSelectOptions())) {
        List<String> list = StrUtil.split(dto.getSelectOptions(), ",");
        // 检测是否有重复
        HashSet<String> set = new HashSet<>(list);
        if (set.size() != list.size()) {
          return CommonRespDto.error("下拉选项重复");
        }
      }
    }

    AgentVariablePo po = agentVariableConverter.dtoToPo(dto);
    int i = baseMapper.updateById(po);
    log.info("更新智能体变量完成, ID: {}, 影响行数: {}", dto.getId(), i);
    if (i <= 0) {
      log.warn("未找到需要更新的智能体变量记录，ID: {}", dto.getId());
    }
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<AgentVariableDto> getInfo(Integer id) {
    log.info("查询智能体变量详情开始, ID: {}", id);
    AgentVariablePo po = baseMapper.selectById(id);
    if (ObjUtil.isNull(po)) {
      log.warn("查询智能体变量不存在, ID: {}", id);
      return CommonRespDto.error("数据不存在");
    }
    log.info("查询智能体变量成功, ID: {}", id);
    return CommonRespDto.success(agentVariableConverter.poToDto(po));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> delete(Integer id) {
    AgentVariablePo po = baseMapper.selectById(id);
    if (ObjUtil.isNull(po)) {
      log.warn("删除智能体变量不存在, ID: {}", id);
      return CommonRespDto.error("数据不存在");
    }
    // 变量：点击删除，引用的地方连带删除
    AgentInfoPo agentInfoPo = agentInfoMapper.selectOne(Wrappers.<AgentInfoPo>lambdaQuery()
        .eq(AgentInfoPo::getApplicationId, po.getApplicationId()));
    String newPromptWords = agentInfoPo.getPromptWords().replace("{{" + po.getName() + "}}", "");
    agentInfoMapper.updateById(
        AgentInfoPo.builder().id(po.getApplicationId()).promptWords(newPromptWords).build());
    log.info("开始删除智能体变量, ID: {}", id);
    int i = baseMapper.deleteById(id);
    if (i > 0) {
      log.info("删除智能体变量成功, ID: {}", id);
      return CommonRespDto.success();
    }
    log.warn("删除智能体变量失败，未找到对应记录, ID: {}", id);
    return CommonRespDto.error("删除失败");
  }

  @Override
  public CommonRespDto<List<AgentVariableDto>> variableListByAppId(Integer applicationId) {
    List<AgentVariablePo> list = baseMapper.selectList(Wrappers.<AgentVariablePo>lambdaQuery()
        .eq(AgentVariablePo::getApplicationId, applicationId).orderByAsc(BasePo::getCreateTime));
    return CommonRespDto.success(agentVariableConverter.poToDtoList(list));
  }

  @Override
  public CommonRespDto<List<AgentVariableDto>> publishedVariableListByAppId(Integer applicationId) {
    AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
        Wrappers.<AgentPublishHistoryPo>lambdaQuery()
            .eq(AgentPublishHistoryPo::getApplicationId, applicationId)
            .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
    if (agentPublishHistoryPo == null) {
      log.warn("未找到已发布智能体变量, applicationId: {}", applicationId);
      return CommonRespDto.error("未找到已发布智能体变量");
    }
    // 转化为DTO对象
    AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
        agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class);
    return CommonRespDto.success(agentInfoResponseDto.getVariableList());
  }

  @Override
  public CommonRespDto<List<SubAgentVarListDto>> getSubAgentVariableListByAppId(
      Integer applicationId) {
    AgentInfoPo agentInfoPo = agentInfoMapper.selectOne(
        Wrappers.<AgentInfoPo>lambdaQuery().eq(AgentInfoPo::getApplicationId, applicationId));
    if (agentInfoPo == null) {
      log.warn("未找到对应应用信息，appId: {}", applicationId);
      return CommonRespDto.success(List.of());
    }
    int[] subAgentAppIds = agentInfoPo.getSubAgentAppIds();
    if (ArrayUtils.isEmpty(subAgentAppIds)) {
      return CommonRespDto.success(List.of());
    }
    List<SubAgentVarListDto> list = new ArrayList<>();
    for (int subAgentAppId : subAgentAppIds) {
      AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
          Wrappers.<AgentPublishHistoryPo>lambdaQuery()
              .eq(AgentPublishHistoryPo::getApplicationId, subAgentAppId)
              .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
      if (agentPublishHistoryPo == null) {
        log.warn("未找到已发布的子智能体变量, subAgentAppId: {}", subAgentAppId);
        continue;
      }
      // 转化为DTO对象
      AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
          agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class);
      list.add(SubAgentVarListDto.builder()
          .subAgentAppId(subAgentAppId)
          .subAgentId(agentInfoResponseDto.getId())
          .applicationName(agentInfoResponseDto.getApplicationName())
          .variableList(agentInfoResponseDto.getVariableList())
          .build());
    }
    return CommonRespDto.success(list);
  }

  @Override
  public CommonRespDto<List<SubAgentVarListDto>> getDisccoverSubAgentVariableListByAppId(Integer applicationId) {
    AgentExperiencePo multipleAgent = agentExperienceMapper.selectOne(
        Wrappers.<AgentExperiencePo>lambdaQuery()
            .eq(AgentExperiencePo::getApplicationId, applicationId)
            .orderByDesc(AgentExperiencePo::getCreateTime).last("LIMIT 1"));
    if (multipleAgent == null) {
      log.warn("未找到已上架的多智能体, applicationId: {}", applicationId);
      return CommonRespDto.success(List.of());
    }
    // 转化为DTO对象
    AgentInfoResponseDto multipleAgentInfo = JacksonUtil.toBean(
        multipleAgent.getConfigData(), AgentInfoResponseDto.class);
    int[] subAgentAppIds = multipleAgentInfo.getSubAgentAppIds();
    if (ArrayUtils.isEmpty(subAgentAppIds)) {
      log.warn("未找到对应子应用信息，appId: {}", applicationId);
      return CommonRespDto.success(List.of());
    }
    List<SubAgentVarListDto> list = new ArrayList<>();
    for (int subAgentAppId : subAgentAppIds) {
      AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
          Wrappers.<AgentPublishHistoryPo>lambdaQuery()
              .eq(AgentPublishHistoryPo::getApplicationId, subAgentAppId)
              .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
      if (agentPublishHistoryPo == null) {
        log.warn("未找到已上架的子智能体变量, subAgentAppId: {}", subAgentAppId);
        continue;
      }
      // 转化为DTO对象
      AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
          agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class);
      list.add(SubAgentVarListDto.builder()
          .subAgentAppId(subAgentAppId)
          .subAgentId(agentInfoResponseDto.getId())
          .applicationName(agentInfoResponseDto.getApplicationName())
          .variableList(agentInfoResponseDto.getVariableList())
          .build());
    }
    return CommonRespDto.success(list);
  }

  @Override
  public CommonRespDto<List<SubAgentVarListDto>> getPublishedSubAgentVariableListByAppId(
      Integer applicationId) {
    AgentPublishHistoryPo multipleAgent = agentPublishHistoryMapper.selectOne(
        Wrappers.<AgentPublishHistoryPo>lambdaQuery()
            .eq(AgentPublishHistoryPo::getApplicationId, applicationId)
            .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
    if (multipleAgent == null) {
      log.warn("未找到已发布的多智能体, applicationId: {}", applicationId);
      return CommonRespDto.success(List.of());
    }
    // 转化为DTO对象
    AgentInfoResponseDto multipleAgentInfo = JacksonUtil.toBean(
        multipleAgent.getConfigData(), AgentInfoResponseDto.class);
    int[] subAgentAppIds = multipleAgentInfo.getSubAgentAppIds();
    if (ArrayUtils.isEmpty(subAgentAppIds)) {
      log.warn("未找到对应子应用信息，appId: {}", applicationId);
      return CommonRespDto.success(List.of());
    }
    List<SubAgentVarListDto> list = new ArrayList<>();
    for (int subAgentAppId : subAgentAppIds) {
      AgentPublishHistoryPo agentPublishHistoryPo = agentPublishHistoryMapper.selectOne(
          Wrappers.<AgentPublishHistoryPo>lambdaQuery()
              .eq(AgentPublishHistoryPo::getApplicationId, subAgentAppId)
              .orderByDesc(AgentPublishHistoryPo::getCreateTime).last("LIMIT 1"));
      if (agentPublishHistoryPo == null) {
        log.warn("未找到已发布的子智能体变量, subAgentAppId: {}", subAgentAppId);
        continue;
      }
      // 转化为DTO对象
      AgentInfoResponseDto agentInfoResponseDto = JacksonUtil.toBean(
          agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class);
      list.add(SubAgentVarListDto.builder()
          .subAgentAppId(subAgentAppId)
          .subAgentId(agentInfoResponseDto.getId())
          .applicationName(agentInfoResponseDto.getApplicationName())
          .variableList(agentInfoResponseDto.getVariableList())
          .build());
    }
    return CommonRespDto.success(list);
  }
}

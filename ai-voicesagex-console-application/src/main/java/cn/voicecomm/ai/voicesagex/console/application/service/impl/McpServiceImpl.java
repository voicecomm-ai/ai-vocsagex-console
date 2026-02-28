package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.mcp.McpService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpAppRelationRemoveReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpApplicationAddReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpBatchReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpPageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagBatchReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagGroupDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.McpConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.McpTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpApplicationRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpApplicationRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
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
public class McpServiceImpl extends ServiceImpl<McpMapper, McpPo> implements McpService {

  private final McpTagRelationMapper mcpTagRelationMapper;

  private final McpTagMapper mcpTagMapper;

  private final McpApplicationRelationMapper mcpApplicationRelationMapper;

  private final McpConverter mcpConverter;

  private final McpTagConverter mcpTagConverter;

  private static final Pattern specialCharPattern = Pattern.compile("^[a-zA-Z0-9_\\-:./]+$");


  @Value("${algoUrlPrefix}${mcp.info}")
  private String mcpInfoUrl;

  @Override
  public CommonRespDto<PagingRespDto<McpDto>> getPageList(McpPageReq req) {

    Page<McpPo> page = Page.of(req.getCurrent(), req.getSize());
    LambdaQueryWrapper<McpPo> queryWrapper = Wrappers.<McpPo>lambdaQuery()
        .eq(ObjectUtil.isNotNull(req.getIsShelf()), McpPo::getIsShelf, req.getIsShelf())
        .orderByDesc(BasePo::getUpdateTime);

    // 忽略大小写的displayName搜索
    if (StrUtil.isNotBlank(req.getDisplayName())) {
      String displayName = SpecialCharUtil.replaceSpecialWord(req.getDisplayName());
      queryWrapper.apply("LOWER(display_name) LIKE LOWER({0})", "%" + displayName + "%");
    }
    if (CollUtil.isNotEmpty(req.getTagIdList())) {
      // 添加标签查询条件
      List<Integer> mcpIds = mcpTagRelationMapper.selectObjs(
          Wrappers.<McpTagRelationPo>lambdaQuery().select(McpTagRelationPo::getMcpId)
              .in(McpTagRelationPo::getTagId, req.getTagIdList()));
      if (CollUtil.isNotEmpty(mcpIds)) {
        queryWrapper.in(McpPo::getId, mcpIds);
      } else {
        return CommonRespDto.success(mcpConverter.pagePoToDto(page));
      }
    }
    Page<McpPo> poPage = baseMapper.selectPage(page, queryWrapper);
    PagingRespDto<McpDto> dtoPageList = mcpConverter.pagePoToDto(poPage);
    if (CollUtil.isNotEmpty(dtoPageList.getRecords())) {
      List<Integer> mcpIds = dtoPageList.getRecords().stream().map(McpDto::getId).toList();
      // 查出应用id对应的所有标签,并分组
      List<McpTagRelationPo> relationList = mcpTagRelationMapper.selectList(
          Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getMcpId, mcpIds));
      mcpTagGroupAndSet(relationList, dtoPageList.getRecords());
    }
    return CommonRespDto.success(dtoPageList);
  }

  @Override
  public Long getMcpNo() {
    return count();
  }

  /**
   * 设置标签
   *
   * @param dto    dto
   * @param list   tagId list
   * @param tagMap tagMap
   */
  public void setMcpTagList(McpDto dto, List<Integer> list, Map<Integer, McpTagPo> tagMap) {
    if (CollUtil.isNotEmpty(list)) {
      List<McpTagDto> tagDtoList = new ArrayList<>();
      list.forEach(tagId -> {
        McpTagPo tagPo = tagMap.get(tagId);
        tagDtoList.add(mcpTagConverter.poToDto(tagPo));
      });
      dto.setTagList(tagDtoList);
    }
  }

  @Override
  public CommonRespDto<List<McpDto>> getList(McpReq req) {
    LambdaQueryWrapper<McpPo> queryWrapper = Wrappers.<McpPo>lambdaQuery()
        .select(McpPo::getId, McpPo::getDisplayName, McpPo::getDescription, McpPo::getMcpIconUrl,
            McpPo::getUrl)
        .eq(ObjectUtil.isNotNull(req.getIsShelf()), McpPo::getIsShelf, req.getIsShelf())
        .orderByDesc(BasePo::getUpdateTime);

    // 忽略大小写的displayName搜索
    if (StrUtil.isNotBlank(req.getDisplayName())) {
      String displayName = SpecialCharUtil.replaceSpecialWord(req.getDisplayName());
      queryWrapper.apply("LOWER(display_name) LIKE LOWER({0})", "%" + displayName + "%");
    }

    List<McpPo> poList = baseMapper.selectList(queryWrapper);
    if (CollUtil.isEmpty(poList)) {
      return CommonRespDto.success(new ArrayList<>());
    }
    List<McpDto> dtoList = mcpConverter.poListToDtoList(poList);
    List<Integer> mcpIds = dtoList.stream().map(McpDto::getId).toList();
    // 查出应用id对应的所有标签,并分组
    List<McpTagRelationPo> relationList = mcpTagRelationMapper.selectList(
        Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getMcpId, mcpIds));
    mcpTagGroupAndSet(relationList, dtoList);
    return CommonRespDto.success(dtoList);
  }

  /**
   * 获取列表（用于工作流）
   *
   * @param name 名称
   * @return 数据
   */
  @Override
  public CommonRespDto<List<McpTagGroupDto>> listMcpGroupedByTags(String name) {
    LambdaQueryWrapper<McpPo> queryWrapper = new LambdaQueryWrapper<McpPo>()
        .eq(McpPo::getIsShelf, true)
        .orderByDesc(McpPo::getUpdateTime);
    if (StrUtil.isNotBlank(name)) {
      String displayName = SpecialCharUtil.replaceSpecialWord(name);
      queryWrapper.apply("LOWER(display_name) LIKE LOWER({0})", "%" + displayName + "%");
    }
    // 查询所有上架的 MCP
    List<McpPo> mcps = baseMapper.selectList(queryWrapper);
    if (CollUtil.isEmpty(mcps)) {
      return CommonRespDto.success(new ArrayList<>());
    }
    Map<Integer, McpPo> mcpMap = mcps.stream()
        .collect(Collectors.toMap(McpPo::getId, Function.identity()));

    // 查询所有关系
    List<McpTagRelationPo> relations = mcpTagRelationMapper.selectList(
        new LambdaQueryWrapper<McpTagRelationPo>().in(McpTagRelationPo::getMcpId, mcpMap.keySet())
    );
    Set<Integer> tagIds = relations.stream().map(McpTagRelationPo::getTagId)
        .collect(Collectors.toSet());
    if (CollUtil.isEmpty(tagIds)) {
      List<McpDto> mcpDtos = mcpConverter.poListToDtoList(mcps);
      McpTagGroupDto mcpTagGroupDto = new McpTagGroupDto(null, null, mcpDtos);
      return CommonRespDto.success(CollUtil.newArrayList(mcpTagGroupDto));
    }
    // 查询关联的标签，按创建时间倒序
    List<McpTagPo> tags = mcpTagMapper.selectList(
        new LambdaQueryWrapper<McpTagPo>().in(McpTagPo::getId, tagIds)
            .orderByDesc(McpTagPo::getCreateTime)
    );
    Map<Integer, McpTagPo> tagMap = tags.stream()
        .collect(Collectors.toMap(McpTagPo::getId, e -> e));
    // 构建 tagId -> MCP 列表映射
    Map<Integer, List<McpPo>> tagMcpMap = relations.stream()
        .collect(Collectors.groupingBy(
            McpTagRelationPo::getTagId,
            Collectors.mapping(rel -> mcpMap.get(rel.getMcpId()), Collectors.toList())
        ));

    // 组装结果
    List<McpTagGroupDto> result = new ArrayList<>();
    Set<Integer> taggedMcpIds = new HashSet<>();

    for (McpTagPo tag : tags) {
      List<McpPo> taggedMcps = tagMcpMap.getOrDefault(tag.getId(), Collections.emptyList());
      // 排序：MCP 创建时间倒序
      taggedMcps.sort(Comparator.comparing(McpPo::getUpdateTime).reversed());

      // 记录已分配标签的 MCP
      taggedMcps.forEach(m -> taggedMcpIds.add(m.getId()));
      List<McpDto> mcpDtos = mcpConverter.poListToDtoList(taggedMcps);
      mcpDtos.forEach(m -> {
        List<McpTagRelationPo> mcpTagRelationPos = relations.stream()
            .filter(e -> e.getMcpId().equals(m.getId()))
            .toList();
        if (CollUtil.isNotEmpty(mcpTagRelationPos)) {
          List<Integer> mcpTagIds = mcpTagRelationPos.stream().map(McpTagRelationPo::getTagId)
              .toList();
          setMcpTagList(m, mcpTagIds, tagMap);
        }
      });
      result.add(
          new McpTagGroupDto(tag.getId(), tag.getName(), mcpDtos));
    }

    // 找出没有标签的 MCP -> 放到 "其他"
    List<McpPo> untaggedMcps = mcps.stream()
        .filter(mcp -> !taggedMcpIds.contains(mcp.getId()))
        .sorted(Comparator.comparing(McpPo::getUpdateTime).reversed())
        .toList();

    if (!untaggedMcps.isEmpty()) {
      result.add(new McpTagGroupDto(null, "其他", mcpConverter.poListToDtoList(untaggedMcps)));
    }

    return CommonRespDto.success(result);
  }

  @Override
  public CommonRespDto<McpDataDto> getTools(Integer id) {
    McpPo mcpPo = baseMapper.selectById(id);
    if (mcpPo == null) {
      return CommonRespDto.error("Mcp不存在");
    }
    // 构建请求参数
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("mcp_name", mcpPo.getInternalName());

    // 构建 connection 子对象
    Map<String, Object> connection = new HashMap<>();
    connection.put("transport", mcpPo.getTransport());
    connection.put("url", mcpPo.getUrl());

    requestBody.put("connection", connection);

    // 转成 JSON 字符串
    String jsonStr = JSONUtil.toJsonStr(requestBody);
    log.info("开始请求mcp工具列表接口:{}", jsonStr);

    // 发送 POST 请求
    String response = HttpUtil.post(mcpInfoUrl, jsonStr);
    log.info("mcp工具列表接口返回结果：{}", response);
    JSONObject jsonObject = JSONUtil.parseObj(response);

    if (jsonObject.getInt("code") != 1000) {
      return CommonRespDto.error(StrUtil.subAfter(jsonObject.getStr("msg"), ": ", false));
    }
    McpDataDto mcpDataDto = JSONUtil.toBean(jsonObject.getJSONObject("data"), McpDataDto.class);
    return CommonRespDto.success(mcpDataDto);
  }

  /**
   * 标签分组并设置
   *
   * @param relationList 关联list
   * @param dtoList      dto list
   */
  public void mcpTagGroupAndSet(List<McpTagRelationPo> relationList, List<McpDto> dtoList) {
    if (CollUtil.isNotEmpty(relationList)) {
      List<Integer> tagIdList = relationList.stream().map(McpTagRelationPo::getTagId).toList();
      Map<Integer, McpTagPo> tagMap = mcpTagMapper.selectList(
              Wrappers.<McpTagPo>lambdaQuery().in(McpTagPo::getId, tagIdList)).stream()
          .collect(Collectors.toMap(McpTagPo::getId, v -> v));
      // 根据mcpId分组成map，key为应用id，value为标签id集合
      Map<Integer, List<Integer>> appTagListMap = relationList.stream().collect(
          Collectors.groupingBy(McpTagRelationPo::getMcpId,
              Collectors.mapping(McpTagRelationPo::getTagId, Collectors.toList())));
      dtoList.forEach(dto -> {
        List<Integer> appTagList = appTagListMap.getOrDefault(dto.getId(), List.of());
        setMcpTagList(dto, appTagList, tagMap);
      });
    }
  }

  @Override
  public CommonRespDto<McpDto> getInfo(Integer id) {
    McpPo mcpPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(mcpPo)) {
      return CommonRespDto.error("数据不存在");
    }
    McpDto mcpDto = mcpConverter.poToDto(mcpPo);
    List<McpTagRelationPo> modelTagRelationPoList = mcpTagRelationMapper.selectList(
        Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getMcpId, mcpDto.getId())
            .orderByDesc(McpTagRelationPo::getCreateTime));
    if (CollUtil.isNotEmpty(modelTagRelationPoList)) {
      mcpDto.setTagList(mcpTagConverter.poListToDtoList(mcpTagMapper.selectList(
          Wrappers.<McpTagPo>lambdaQuery().in(McpTagPo::getId,
              modelTagRelationPoList.stream().map(McpTagRelationPo::getTagId)
                  .collect(Collectors.toList())))));
    }
    return CommonRespDto.success(mcpDto);
  }

  @Override
  public CommonRespDto<Boolean> isAvailable(Integer id) {
    McpPo mcpPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(mcpPo)) {
      return CommonRespDto.success("mcp不存在", Boolean.FALSE);
    }
    if (BooleanUtil.isFalse(mcpPo.getIsShelf())) {
      return CommonRespDto.success("mcp已下架", Boolean.FALSE);
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Integer> save(McpDto dto) {
    McpPo po = mcpConverter.dtoToPo(dto);
    // 名称重名校验
    if (baseMapper.selectCount(
        Wrappers.<McpPo>lambdaQuery().eq(McpPo::getDisplayName, po.getDisplayName())) > 0) {
      return CommonRespDto.error("名称已存在");
    }
    // 内部名称重名校验
    if (baseMapper.selectCount(
        Wrappers.<McpPo>lambdaQuery().eq(McpPo::getInternalName, po.getInternalName())) > 0) {
      return CommonRespDto.error("内部名称已存在");
    }
    // url重名校验
    if (baseMapper.selectCount(Wrappers.<McpPo>lambdaQuery().eq(McpPo::getUrl, po.getUrl())) > 0) {
      return CommonRespDto.error("mcp url已存在");
    }
    // 只能包含英文、数字、下划线、-、：、.、/
    if (!ReUtil.isMatch(specialCharPattern, po.getInternalName())) {
      return CommonRespDto.error("名称只能包含英文、数字、下划线、-、:、.、/");
    }
    baseMapper.insert(po);
    addMcpTagList(dto, po);
    return CommonRespDto.success(po.getId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> update(McpDto dto) {
    McpPo po = mcpConverter.dtoToPo(dto);
    // 名称重名校验
    if (baseMapper.selectCount(
        Wrappers.<McpPo>lambdaQuery().eq(McpPo::getDisplayName, po.getDisplayName())
            .ne(McpPo::getId, po.getId())) > 0) {
      return CommonRespDto.error("名称已存在");
    }
    // 内部名称重名校验
    if (baseMapper.selectCount(
        Wrappers.<McpPo>lambdaQuery().eq(McpPo::getInternalName, po.getInternalName())
            .ne(McpPo::getId, po.getId())) > 0) {
      return CommonRespDto.error("内部名称已存在");
    }
    // url重名校验
    if (baseMapper.selectCount(
        Wrappers.<McpPo>lambdaQuery().eq(McpPo::getUrl, po.getUrl()).ne(McpPo::getId, po.getId()))
        > 0) {
      return CommonRespDto.error("mcp url已存在");
    }
    // 只能包含英文、数字、下划线、-、：、.、/
    if (!ReUtil.isMatch(specialCharPattern, po.getInternalName())) {
      return CommonRespDto.error("名称只能包含英文、数字、下划线、-、:、.、/");
    }
    mcpTagRelationMapper.delete(
        Wrappers.<McpTagRelationPo>lambdaQuery().eq(McpTagRelationPo::getMcpId, dto.getId()));
    addMcpTagList(dto, po);

    int i = baseMapper.updateById(po);
    return CommonRespDto.of(i > 0);
  }

  private void addMcpTagList(McpDto dto, McpPo po) {
    if (CollUtil.isNotEmpty(dto.getTagList())) {
      List<McpTagRelationPo> tagRelationList = dto.getTagList().stream()
          .map(tagDto -> McpTagRelationPo.builder().mcpId(po.getId()).tagId(tagDto.getId()).build())
          .collect(Collectors.toList());
      MybatisPlusUtil<McpTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          mcpTagRelationMapper, McpTagRelationPo.class);
      mybatisPlusUtil.saveBatch(tagRelationList, tagRelationList.size());
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> delete(Integer id) {
    baseMapper.deleteById(id);
    mcpTagRelationMapper.delete(
        Wrappers.<McpTagRelationPo>lambdaQuery().eq(McpTagRelationPo::getMcpId, id));
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> deleteBatch(List<Integer> ids) {
    baseMapper.deleteBatchIds(ids);
    mcpTagRelationMapper.delete(
        Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getMcpId, ids));
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> shelfBatch(McpBatchReq mcpBatchReq) {
    if (ObjectUtil.isNull(mcpBatchReq.getIsShelf())) {
      return CommonRespDto.error("是否上下架不能为空");
    }
    List<McpPo> mcpPoList = new ArrayList<>();
    mcpBatchReq.getIds().forEach(
        id -> mcpPoList.add(McpPo.builder().id(id).isShelf(mcpBatchReq.getIsShelf()).build()));
    return CommonRespDto.success(this.updateBatchById(mcpPoList));
  }

  @Override
  public CommonRespDto<Boolean> updateTagBatch(McpTagBatchReq req) {
    List<McpTagPo> modelTagPoList = mcpTagMapper.selectList(
        Wrappers.<McpTagPo>lambdaQuery().in(McpTagPo::getId, req.getTagIds()));
    if (CollUtil.isEmpty(modelTagPoList)) {
      return CommonRespDto.error("标签不存在");
    }
    List<Integer> mcpIds = req.getIds();
    // 先删除现有的标签
    mcpTagRelationMapper.delete(
        Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getMcpId, mcpIds));
    List<McpTagRelationPo> mcpTagRelationPoList = new ArrayList<>();
    for (Integer mcpId : mcpIds) {
      for (Integer tagId : req.getTagIds()) {
        McpTagRelationPo tagRelationPo = McpTagRelationPo.builder().mcpId(mcpId).tagId(tagId)
            .build();
        mcpTagRelationPoList.add(tagRelationPo);
      }
    }
    MybatisPlusUtil<McpTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(mcpTagRelationMapper,
        McpTagRelationPo.class);
    mybatisPlusUtil.saveBatch(mcpTagRelationPoList, mcpTagRelationPoList.size());
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> addMcpListToApplication(McpApplicationAddReq req) {

    List<McpApplicationRelationPo> mcpApplicationRelationPos = mcpApplicationRelationMapper.selectList(
        Wrappers.<McpApplicationRelationPo>lambdaQuery()
            .eq(McpApplicationRelationPo::getApplicationId, req.getApplicationId()));
    List<Integer> oldMcpIds = mcpApplicationRelationPos.stream()
        .map(McpApplicationRelationPo::getMcpId).toList();
    // 筛选出在req.getMcpIds中，不在oldMcpIds中的作为新增
    List<Integer> newMcpIds = req.getMcpIds().stream().filter(mcpId -> !oldMcpIds.contains(mcpId))
        .toList();
    // 筛选出不在req.getMcpIds中，在oldMcpIds中的作为删除
    List<Integer> deleteMcpIds = oldMcpIds.stream()
        .filter(mcpId -> !CollUtil.contains(req.getMcpIds(), mcpId)).toList();

    if (CollUtil.isNotEmpty(deleteMcpIds)) {
      // 删除旧的绑定关系
      mcpApplicationRelationMapper.delete(Wrappers.<McpApplicationRelationPo>lambdaQuery()
          .eq(McpApplicationRelationPo::getApplicationId, req.getApplicationId())
          .in(McpApplicationRelationPo::getMcpId, deleteMcpIds));
    }
    if (CollUtil.isEmpty(req.getMcpIds()) || CollUtil.isEmpty(newMcpIds)) {
      return CommonRespDto.success(Boolean.TRUE);
    }
    // 插入新的
    List<McpApplicationRelationPo> list = newMcpIds.stream().map(
        mcpId -> McpApplicationRelationPo.builder().applicationId(req.getApplicationId())
            .mcpId(mcpId).build()).collect(Collectors.toList());
    MybatisPlusUtil<McpApplicationRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
        mcpApplicationRelationMapper, McpApplicationRelationPo.class);
    mybatisPlusUtil.saveBatch(list, list.size());
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Void> removeMcpAppRelation(McpAppRelationRemoveReq req) {
    int delete = mcpApplicationRelationMapper.delete(
        Wrappers.<McpApplicationRelationPo>lambdaQuery()
            .eq(McpApplicationRelationPo::getApplicationId, req.getApplicationId())
            .eq(McpApplicationRelationPo::getMcpId, req.getMcpId()));
    return CommonRespDto.of(delete > 0);
  }
}

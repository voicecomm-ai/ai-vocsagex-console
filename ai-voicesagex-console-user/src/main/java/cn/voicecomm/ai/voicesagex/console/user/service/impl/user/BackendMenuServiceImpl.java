package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService;
import cn.voicecomm.ai.voicesagex.console.api.api.mcp.McpService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelDatasetService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMenuService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleMenuRelationService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserRoleRelationService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuListDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenusAndNumDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleMenuRelationDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendMenuConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendMenuMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.user.MenuPo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
public class BackendMenuServiceImpl extends ServiceImpl<BackendMenuMapper, MenuPo> implements
    BackendMenuService {

  private final BackendMenuConverter backendMenuConverter;

  private final BackendRoleMenuRelationService backendRoleMenuRelationService;

  private final BackendUserRoleRelationService backendUserRoleRelationService;

  @DubboReference
  public ModelDatasetService modelDatasetService;
  @DubboReference
  public KnowledgeBaseService knowledgeBaseService;

  @DubboReference
  public McpService mcpService;

  @Override
  public CommonRespDto<List<BackendMenuDto>> getAllMenuList() {
    List<MenuPo> menuPos = baseMapper.selectList(null);
    List<BackendMenuDto> backendMenuDtoList = backendMenuConverter.poListToDtoList(menuPos);
    // 获取根菜单
    List<BackendMenuDto> rootMenuList = getRootMenuList(backendMenuDtoList);
    return CommonRespDto.success(rootMenuList);
  }

  /**
   * 查询用户菜单（包含uri）
   *
   * @return
   */
  @Override
  public CommonRespDto<BackendMenuListDto> getMenuList() {
    BackendRoleDto roleDto = backendUserRoleRelationService.getRoleByUserId(
        UserAuthUtil.getUserId()).getData();
    List<Integer> menuIds = backendRoleMenuRelationService.getRelationsByRoleId(roleDto.getId())
        .getData().stream().map(BackendRoleMenuRelationDto::getMenuId).distinct().sorted().toList();
    List<MenuPo> allMenuList = list();

    List<MenuPo> respList = new ArrayList<>();
    addParentMenus(allMenuList, respList, menuIds);

    List<BackendMenuDto> backendMenuDtos = backendMenuConverter.poListToDtoList(respList);
    backendMenuDtos = backendMenuDtos.stream().distinct().filter(e -> e.getType() != 1)
        .sorted(Comparator.comparingInt(BackendMenuDto::getSort)).toList();
    // 获取根菜单
    List<BackendMenuDto> rootMenuList = getRootMenuList(backendMenuDtos);
    List<String> uriList = respList.stream().map(MenuPo::getUri).toList();
    BackendMenuListDto build = BackendMenuListDto.builder().menuList(rootMenuList).uriList(uriList)
        .build();
    return CommonRespDto.success(build);
  }

  @Override
  public CommonRespDto<List<BackendMenusAndNumDto>> getMenusAndNumByParentId(Integer parentId) {
    Integer userId = UserAuthUtil.getUserId();
    BackendRoleDto roleDto = backendUserRoleRelationService.getRoleByUserId(userId).getData();
    List<Integer> menuIds = backendRoleMenuRelationService.getRelationsByRoleId(roleDto.getId())
        .getData().stream().map(BackendRoleMenuRelationDto::getMenuId).distinct().sorted().toList();
    List<MenuPo> allMenuList = list(
        Wrappers.<MenuPo>lambdaQuery().eq(MenuPo::getSuperiorId, parentId)
            .orderByAsc(MenuPo::getSort));
    List<MenuPo> childMenuList = allMenuList.stream().filter(menu -> menuIds.contains(menu.getId()))
        .toList();
    List<BackendMenuDto> backendMenuDtos = backendMenuConverter.poListToDtoList(childMenuList);
    List<BackendMenusAndNumDto> resultList = new ArrayList<>();
    for (BackendMenuDto backendMenuDto : backendMenuDtos) {
      if ("modelDataManage".equals(backendMenuDto.getSign())) {
        // 查询数据集数量(根据当前用户权限)
        Long num = modelDatasetService.getModelDataSetNo(userId);
        resultList.add(BackendMenusAndNumDto.builder().menu(backendMenuDto).num(num).build());
      }
      if ("mcpManage".equals(backendMenuDto.getSign())) {
        // 获取MCP数量(根据当前用户权限)
        Long num = mcpService.getMcpNo();
        resultList.add(BackendMenusAndNumDto.builder().menu(backendMenuDto).num(num).build());
      }
      if ("knowledge".equals(backendMenuDto.getSign())) {
        Long num = knowledgeBaseService.getKnowledgeBaseNo(userId);
        resultList.add(BackendMenusAndNumDto.builder().menu(backendMenuDto).num(num).build());
      }
    }
    return CommonRespDto.success(resultList);
  }

  // 递归查找所有父级节点
  public void addParentMenus(List<MenuPo> allMenuList, List<MenuPo> respList,
      List<Integer> menuIds) {
    // 找到 menuIds 对应的菜单项并添加到结果列表
    List<MenuPo> menuPos = allMenuList.stream().filter(menu -> menuIds.contains(menu.getId()))
        .toList();
    respList.addAll(menuPos);

    // 获取上一级的父级 ID 列表
    List<Integer> parentIds = menuPos.stream().map(MenuPo::getSuperiorId).filter(id -> id != 0)
        .toList();

    // 如果还有父级菜单项，则递归处理
    if (CollUtil.isNotEmpty(parentIds)) {
      addParentMenus(allMenuList, respList, parentIds);
    }
  }


  @Override
  public CommonRespDto<List<BackendMenuDto>> getMenuListByUserId(Integer userId) {
    BackendRoleDto roleDto = backendUserRoleRelationService.getRoleByUserId(userId).getData();
    List<Integer> menuIds = backendRoleMenuRelationService.getRelationsByRoleId(roleDto.getId())
        .getData().stream().map(BackendRoleMenuRelationDto::getMenuId).distinct().sorted().toList();
    List<MenuPo> menuPos = baseMapper.selectBatchIds(menuIds);
    List<BackendMenuDto> backendMenuDtos = backendMenuConverter.poListToDtoList(menuPos);
    backendMenuDtos = backendMenuDtos.stream()
        .sorted(Comparator.comparingInt(BackendMenuDto::getSort)).toList();
    // 获取根菜单
    List<BackendMenuDto> rootMenuList = getRootMenuList(backendMenuDtos);
    return CommonRespDto.success(rootMenuList);
  }

  /**
   * 获取根菜单
   *
   * @param menuDTOS 菜单列表
   * @return
   */
  private List<BackendMenuDto> getRootMenuList(List<BackendMenuDto> menuDTOS) {
    List<BackendMenuDto> rootMenuList = menuDTOS.stream()
        .filter(menuDTO -> Objects.equals(menuDTO.getSuperiorId(), 0))
        .sorted(Comparator.comparing(BackendMenuDto::getSort)).collect(Collectors.toList());
    // 添加子菜单
    rootMenuList.forEach(root -> getMenuTree(menuDTOS, root));
    return rootMenuList;
  }

  /**
   * 递归获取菜单树
   *
   * @param menuDTOS   菜单列表
   * @param parentMenu 上级菜单
   * @return
   */
  private BackendMenuDto getMenuTree(List<BackendMenuDto> menuDTOS, BackendMenuDto parentMenu) {
    List<BackendMenuDto> children = new ArrayList<>();
    List<BackendMenuDto> sortedList = menuDTOS.stream()
        .filter(m -> Objects.equals(m.getSuperiorId(), parentMenu.getId()))
        .sorted(Comparator.comparing(BackendMenuDto::getSort)).toList();
    sortedList.forEach(menuRespDTO -> children.add(getMenuTree(menuDTOS, menuRespDTO)));
    parentMenu.setChildren(children);
    return parentMenu;
  }
}

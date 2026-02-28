package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuListDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenusAndNumDto;
import java.util.List;

public interface BackendMenuService {

  /**
   * 查询所有菜单
   */
  CommonRespDto<List<BackendMenuDto>> getAllMenuList();

  /**
   * 查询用户菜单（包含uri）
   */
  CommonRespDto<BackendMenuListDto> getMenuList();


  CommonRespDto<List<BackendMenusAndNumDto>> getMenusAndNumByParentId(Integer parentId);


  /**
   * 根据用户id查询菜单
   */
  CommonRespDto<List<BackendMenuDto>> getMenuListByUserId(Integer userId);
}

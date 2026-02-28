package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagDto;
import java.util.List;

/**
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface ApplicationTagService {

  /**
   * 获取应用标签列表
   *
   * @return 包含应用标签列表的CommonRespDto对象
   */
  CommonRespDto<List<ApplicationTagDto>> getList(String tagName);

  /**
   * 根据ID获取应用标签
   *
   * @param id 应用标签的唯一标识
   * @return 包含应用标签信息的CommonRespDto对象
   */
  CommonRespDto<ApplicationTagDto> getById(Integer id);

  /**
   * 添加新的应用标签
   *
   * @param dto 包含要添加的应用标签信息的ApplicationTagDto对象
   * @return 包含新添加标签ID的CommonRespDto对象
   */
  CommonRespDto<Integer> add(ApplicationTagDto dto);

  /**
   * 更新应用标签信息
   *
   * @param dto 包含要更新的应用标签信息的ApplicationTagDto对象
   * @return 表示更新操作是否成功的CommonRespDto对象
   */
  CommonRespDto<Void> update(ApplicationTagDto dto);

  /**
   * 删除指定ID的应用标签
   *
   * @param id 要删除的应用标签的唯一标识
   * @return 表示删除操作是否成功的CommonRespDto对象
   */
  CommonRespDto<Void> delete(Integer id);


  /**
   * 删除指定ID的应用标签前检测
   *
   * @param id 要删除的应用标签的唯一标识
   * @return 是否正在被使用
   */
  CommonRespDto<Boolean> deleteCheck(Integer id);

}


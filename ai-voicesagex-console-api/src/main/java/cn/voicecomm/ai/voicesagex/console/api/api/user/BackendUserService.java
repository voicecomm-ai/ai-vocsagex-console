package cn.voicecomm.ai.voicesagex.console.api.api.user;


import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UserPageReqDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;

import java.util.List;

public interface BackendUserService {

  /**
   * 新增用户
   */
  CommonRespDto<Void> add(BackendUserDto dto);

  /**
   * 根据用户id获取信息
   */
  CommonRespDto<BackendUserDto> getInfo(Integer userId);


  CommonRespDto<BackendUserDto> getUserInfo(Integer userId);


  /**
   * 用户分页查询
   */
  CommonRespDto<PagingRespDto<BackendUserDto>> getUserPage(UserPageReqDto dto);

  CommonRespDto<List<BackendUserDto>> getUserListByUserId(Integer type);


  List<BackendUserDto> getUserListByUserIdWithAdmin();

  CommonRespDto<List<Integer>> getUserIdsByUserId(Integer userId);

  CommonRespDto<List<Integer>> getUserIdsByCurrentUserAndDeviceType(Integer userId, Integer type);

  CommonRespDto<List<BackendUserDto>> getUserByUserId(Integer userId);


  CommonRespDto<List<BackendUserDto>> getUserByIds(List<Integer> userIds);

  /**
   * 根据账号获取信息
   */
  CommonRespDto<BackendUserDto> getInfoByAccount(String account);

  /**
   * 更新用户信息
   */
  CommonRespDto<Void> update(BackendUserDto dto);

  /**
   * 禁用用户
   */
  CommonRespDto<Void> disable(Integer userId);

  /**
   * 启用用户
   */
  CommonRespDto<Void> enable(Integer userId);


  /**
   * 删除用户
   */
  CommonRespDto<Void> delete(Integer userId);


  /**
   * 更新用户密码
   *
   * @param userId         用户id
   * @param oldPassword    旧密码
   * @param firstPassword  新密码
   * @param secondPassword 再次确认密码
   * @return resp
   */
  CommonRespDto<Void> updatePassword(Integer userId, String oldPassword, String firstPassword,
    String secondPassword);

  /**
   * 校验登录密码
   *
   * @param password 密码
   * @return ok
   */
  CommonRespDto<Void> checkPassword(String password);

  /**
   * @return 批量导入用户模板下载地址
   */
  CommonRespDto<String> downloadBatchImportUserTemplate();

  /**
   * @param roleId    角色ids
   * @param fileBytes 文件字节流
   * @return 是否成功
   */
  CommonRespDto<Void> batchImportUser(Integer roleId, byte[] fileBytes);

  CommonRespDto<Void> deleteLogin(Integer userId, MessageTypeEnum messageTypeEnum);
}

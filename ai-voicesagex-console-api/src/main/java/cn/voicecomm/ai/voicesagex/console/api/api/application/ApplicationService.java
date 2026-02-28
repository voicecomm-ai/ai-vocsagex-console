package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApiKeyCreate;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.AppPublishAndOnShelfTimeResp;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationKeyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationListReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagUpdateDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.TemplateDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.SubAgentListReq;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * ApplicationService
 *
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface ApplicationService {

  /**
   * 获取应用列表 根据请求参数获取经过筛选的应用列表
   *
   * @param req 包含筛选条件的请求对象
   * @return 包含应用列表的响应对象
   */
  CommonRespDto<PagingRespDto<ApplicationDto>> getPageList(ApplicationListReq req);


  /**
   * 获取已发布的子智能体列表
   */
  CommonRespDto<List<SubAgentInfoDto>> getSubPublishedAgentList(SubAgentListReq reqDto);


  /**
   * 获取模板列表
   *
   * @param name  应用名称
   * @param appId
   * @return 模板列表
   */
  CommonRespDto<TemplateDto> templateList(String name, Integer appId);

  /**
   * 通过ID获取应用信息
   *
   * @param id 应用的唯一标识符
   * @return 包含单个应用信息的响应对象
   */
  CommonRespDto<ApplicationDto> getById(Integer id);

  /**
   * 添加新应用
   *
   * @param dto 包含应用信息的数据传输对象
   * @return 包含新应用ID的响应对象
   */
  CommonRespDto<Integer> add(ApplicationDto dto);

  /**
   * 更新应用信息
   *
   * @param dto 包含更新后应用信息的数据传输对象
   * @return 表示更新操作是否成功的响应对象
   */
  CommonRespDto<Void> update(ApplicationDto dto);


  /**
   * 更新应用标签
   *
   * @param dto dto
   * @return 表示更新操作是否成功的响应对象
   */
  CommonRespDto<Void> updateAppTag(ApplicationTagUpdateDto dto);

  /**
   * 删除应用
   *
   * @param id 应用的唯一标识符
   * @return 表示删除操作是否成功的响应对象
   */
  CommonRespDto<Void> delete(Integer id);

  /**
   * 上传应用图标
   *
   * @param file 要上传的图标文件
   * @return 包含上传后文件路径的响应对象
   */
  CommonRespDto<String> uploadIcon(MultipartFile file);


  /**
   * 获取应用发布时间描述
   *
   * @param appId 查询参数
   * @return 应用发布时间描述
   */
  CommonRespDto<AppPublishAndOnShelfTimeResp> publishAndOnShelfTimeDescription(Integer appId);

  /**
   * 检测应用状态
   *
   * @param appId 待检测的应用ID
   * @return 检测结果
   */
  CommonRespDto<Boolean> checkOnShelf(Integer appId);


  /**
   * 刷新访问url
   *
   * @param id API密钥ID，不能为空
   * @return 删除结果
   */
  CommonRespDto<String> regenerateUrl(Integer id);


  /**
   * 创建API密钥
   *
   * @param req req
   * @return API密钥
   */
  CommonRespDto<ApplicationKeyDto> createApiKey(ApiKeyCreate req);


  /**
   * 获取API密钥列表
   *
   * @param appId 应用ID
   * @return API密钥列表
   */
  CommonRespDto<List<ApplicationKeyDto>> getApiKeyList(Integer appId);


  CommonRespDto<String> agentUrlChatTokenGenerate(String urlKey);


  CommonRespDto<ApplicationDto> getByUrlKey(String urlKey);


  CommonRespDto<Void> integrated(Integer appId);
}

package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
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
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationKeyMapper;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 应用
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@RestController
@RequestMapping("/application")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ApplicationController {

  /**
   * 应用服务接口，用于处理应用相关的业务逻辑
   */
  private final ApplicationService applicationService;

  private final ApplicationKeyMapper applicationKeyMapper;


  /**
   * 删除API密钥
   *
   * @param id API密钥ID，不能为空
   * @return 删除结果
   */
  @DeleteMapping("/deleteApiKey/{id}")
  public Result<Void> deleteApiKey(@PathVariable @NotNull(message = "id不能为空") Integer id) {
    int i = applicationKeyMapper.deleteById(id);
    if (i > 0) {
      return Result.success();
    }
    return Result.error("删除失败");
  }


  /**
   * 创建API密钥
   *
   * @param req 创建API密钥的请求参数
   * @return 新创建的API密钥
   */
  @PostMapping("/createApiKey")
  public Result<ApplicationKeyDto> createApiKey(@RequestBody ApiKeyCreate req) {
    CommonRespDto<ApplicationKeyDto> respDto = applicationService.createApiKey(req);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 重新生成访问url
   *
   * @param id 应用ID，不能为空
   * @return 新的url
   */
  @PatchMapping("/regenerateUrl/{id}")
  public Result<String> regenerateUrl(@PathVariable @NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<String> respDto = applicationService.regenerateUrl(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 获取应用分页列表
   *
   * @param reqDto 应用列表请求DTO，包含查询条件
   * @return 包含应用列表的响应结果
   */
  @PostMapping("/getPageList")
  public Result<PagingRespDto<ApplicationDto>> getPageList(
      @RequestBody @Validated ApplicationListReq reqDto) {
    CommonRespDto<PagingRespDto<ApplicationDto>> respDto = applicationService.getPageList(reqDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取已发布的子智能体列表
   *
   * @return 获取子应用列表的响应结果
   */
  @PostMapping("/getSubPublishedAgentList")
  public Result<List<SubAgentInfoDto>> getSubPublishedAgentList(
      @RequestBody SubAgentListReq reqDto) {
    CommonRespDto<List<SubAgentInfoDto>> respDto = applicationService.getSubPublishedAgentList(
        reqDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据ID获取应用信息
   *
   * @param id 应用ID，不能为空
   * @return 包含应用信息的响应结果
   */
  @GetMapping("/getById")
  public Result<ApplicationDto> getById(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<ApplicationDto> respDto = applicationService.getById(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 内置应用
   *
   * @param appId 应用id
   */
  @PatchMapping("/integrated/{appId}")
  public Result<Void> integrated(@NotNull(message = "appId不能为空") @PathVariable Integer appId) {
    CommonRespDto<Void> respDto = applicationService.integrated(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 获取模板列表
   *
   * @return 包含应用信息的响应结果
   */
  @GetMapping("/templateList")
  public Result<TemplateDto> templateList(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "appId") Integer appId) {
    CommonRespDto<TemplateDto> respDto = applicationService.templateList(name, appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 添加新应用
   *
   * @param dto 应用DTO，包含要添加的应用信息
   * @return 包含新应用ID的响应结果
   */
  @PostMapping("/add")
  public Result<Integer> add(@RequestBody @Validated(value = AddGroup.class) ApplicationDto dto) {
    CommonRespDto<Integer> respDto = applicationService.add(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新应用信息
   *
   * @param dto 应用DTO，包含要更新的应用信息
   * @return 表示更新操作是否成功的响应结果
   */
  @PostMapping("/update")
  public Result<Void> update(
      @RequestBody @Validated(value = UpdateGroup.class) ApplicationDto dto) {
    CommonRespDto<Void> respDto = applicationService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新应用标签
   *
   * @param dto 应用标签更新DTO，包含要更新的标签信息
   * @return 表示更新操作是否成功的响应结果
   */
  @PostMapping("/updateAppTag")
  public Result<Void> updateAppTag(@RequestBody @Validated ApplicationTagUpdateDto dto) {
    CommonRespDto<Void> respDto = applicationService.updateAppTag(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除应用
   *
   * @param id 应用ID，不能为空
   * @return 表示删除操作是否成功的响应结果
   */
  @DeleteMapping("/delete")
  public Result<Void> delete(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Void> respDto = applicationService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 上传应用图标
   *
   * @param file 要上传的图标文件
   * @return 包含上传图标URL的响应结果
   */
  @PostMapping("/uploadIcon")
  public Result<String> uploadIcon(MultipartFile file) {
    CommonRespDto<String> respDto = applicationService.uploadIcon(file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取应用发布时间描述
   *
   * @param appId 应用ID，不能为空
   * @return 应用发布时间描述的响应结果
   */
  @GetMapping("/publishAndOnShelfTimeDescription")
  public Result<AppPublishAndOnShelfTimeResp> publishAndOnShelfTimeDescription(
      @NotNull(message = "appId不能为空") Integer appId) {
    CommonRespDto<AppPublishAndOnShelfTimeResp> respDto = applicationService.publishAndOnShelfTimeDescription(
        appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 检测应用状态
   *
   * @param appId 应用ID，不能为空
   * @return 应用发布时间描述的响应结果
   */
  @GetMapping("/checkOnShelf")
  public Result<Boolean> checkOnShelf(@NotNull(message = "appId不能为空") Integer appId) {
    CommonRespDto<Boolean> respDto = applicationService.checkOnShelf(appId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

}

package cn.voicecomm.ai.voicesagex.console.application.controller;

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
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagGroupDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * mcp
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@RestController
@RequestMapping("/mcp")
@Validated
@Slf4j
@RequiredArgsConstructor
public class McpController {

  private final McpService mcpService;

  /**
   * 获取mcp分页列表
   *
   * @param reqDto mcp列表请求DTO，包含查询条件
   * @return 包含mcp列表的响应结果
   */
  @PostMapping("/getPageList")
  public Result<PagingRespDto<McpDto>> getPageList(
      @RequestBody @Validated McpPageReq reqDto) {
    CommonRespDto<PagingRespDto<McpDto>> respDto = mcpService.getPageList(reqDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取列表
   *
   * @param reqDto reqDto
   * @return List<McpDto>
   */
  @PostMapping("/getList")
  public Result<List<McpDto>> getList(
      @RequestBody @Validated McpReq reqDto) {
    CommonRespDto<List<McpDto>> respDto = mcpService.getList(reqDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取列表（用于工作流）
   *
   * @param name 名称
   * @return List<McpDto>
   */
  @GetMapping("/listMcpGroupedByTags")
  public Result<List<McpTagGroupDto>> listMcpGroupedByTags(
      @RequestParam(value = "name", required = false) String name) {
    CommonRespDto<List<McpTagGroupDto>> respDto = mcpService.listMcpGroupedByTags(name);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取mcp工具列表详情
   *
   * @param id mcpID，不能为空
   * @return 详情
   */
  @GetMapping("/getTools")
  public Result<McpDataDto> getTools(@RequestParam(value = "id") Integer id) {
    CommonRespDto<McpDataDto> respDto = mcpService.getTools(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据ID获取mcp信息
   *
   * @param id mcpID，不能为空
   * @return 包含mcp信息的响应结果
   */
  @GetMapping("/getInfo")
  public Result<McpDto> getInfo(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<McpDto> respDto = mcpService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 判断mcp是否可用
   *
   * @param id mcpID，不能为空
   * @return 是否可用
   */
  @GetMapping("/isAvailable")
  public Result<Boolean> isAvailable(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = mcpService.isAvailable(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 添加新mcp
   *
   * @param dto mcpDTO，包含要添加的mcp信息
   * @return 包含新mcpID的响应结果
   */
  @PostMapping("/save")
  public Result<Integer> save(
      @RequestBody @Validated(value = AddGroup.class) McpDto dto) {
    CommonRespDto<Integer> respDto = mcpService.save(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新mcp信息
   *
   * @param dto mcpDTO，包含要更新的mcp信息
   * @return 表示更新操作是否成功的响应结果
   */
  @PostMapping("/update")
  public Result<Boolean> update(
      @RequestBody @Validated(value = UpdateGroup.class) McpDto dto) {
    CommonRespDto<Boolean> respDto = mcpService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 删除mcp
   *
   * @param id mcpID，不能为空
   * @return 表示删除操作是否成功的响应结果
   */
  @DeleteMapping("/delete")
  public Result<Boolean> delete(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = mcpService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  /**
   * 批量删除mcp
   *
   * @param ids 主键id集合
   * @return 是否成功
   */
  @DeleteMapping("/delete-batch")
  public Result<Boolean> deleteBatch(
      @RequestBody @NotEmpty(message = "id不能为空") List<Integer> ids) {
    CommonRespDto<Boolean> respDto = mcpService.deleteBatch(ids);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 批量上下架mcp
   *
   * @param req 批量的对象
   * @return 是否成功
   */
  @PostMapping("/shelf-batch")
  public Result<Boolean> shelfBatch(@RequestBody @Validated McpBatchReq req) {
    CommonRespDto<Boolean> respDto = mcpService.shelfBatch(req);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 批量更新mcp标签
   *
   * @param req req
   * @return Result
   */
  @PostMapping("/updateTagBatch")
  public Result<Boolean> updateTagBatch(@RequestBody @Validated McpTagBatchReq req) {
    CommonRespDto<Boolean> respDto = mcpService.updateTagBatch(req);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 批量添加mcp到应用
   *
   * @param req req
   * @return Result
   */
  @PostMapping("/addMcpListToApplication")
  public Result<Boolean> addMcpListToApplication(@RequestBody @Validated McpApplicationAddReq req) {
    CommonRespDto<Boolean> respDto = mcpService.addMcpListToApplication(req);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除mcp应用关系
   *
   * @param req req
   * @return Result
   */
  @PostMapping("/removeMcpAppRelation")
  public Result<Void> removeMcpAppRelation(@RequestBody @Validated McpAppRelationRemoveReq req) {
    CommonRespDto<Void> respDto = mcpService.removeMcpAppRelation(req);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success();
  }


}

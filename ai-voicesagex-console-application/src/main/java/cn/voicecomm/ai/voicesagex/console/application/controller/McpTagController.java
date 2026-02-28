package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.mcp.McpTagService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * mcp标签
 *
 * @author wangf
 * @date 2025/5/19 下午 2:00
 */
@RestController
@RequestMapping("/mcpTag")
@Validated
@Slf4j
@RequiredArgsConstructor
public class McpTagController {

  private final McpTagService mcpTagService;

  /**
   * 获取mcp标签列表
   *
   * @return mcp标签列表
   */
  @PostMapping("/getList")
  public Result<List<McpTagDto>> getList(String tagName) {
    CommonRespDto<List<McpTagDto>> respDto = mcpTagService.getList(tagName);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据已上架的Mcp获取所有标签列表
   * 
   * @return Result
   */
  @GetMapping("/getListByShelfMcp")
  public Result<List<McpTagDto>> getListByShelfMcp() {
    CommonRespDto<List<McpTagDto>> respDto = mcpTagService.getListByShelfMcp();
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
  
  

  /**
   * 根据ID获取mcp标签信息
   *
   * @param id mcp标签ID
   * @return mcp标签信息
   */
  @GetMapping("/getById")
  public Result<McpTagDto> getById(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<McpTagDto> respDto = mcpTagService.getById(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 添加mcp标签
   *
   * @param dto mcp标签数据传输对象
   * @return 新增mcp标签的ID
   */
  @PostMapping("/add")
  public Result<Integer> add(@RequestBody @Validated McpTagDto dto) {
    CommonRespDto<Integer> respDto = mcpTagService.add(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新mcp标签信息
   *
   * @param dto mcp标签数据传输对象
   * @return 更新是否成功
   */
  @PostMapping("/update")
  public Result<Void> update(@RequestBody @Validated McpTagDto dto) {
    CommonRespDto<Void> respDto = mcpTagService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除mcp标签
   *
   * @param id mcp标签ID
   * @return 删除是否成功
   */
  @DeleteMapping("/delete")
  public Result<Void> delete(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Void> respDto = mcpTagService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}

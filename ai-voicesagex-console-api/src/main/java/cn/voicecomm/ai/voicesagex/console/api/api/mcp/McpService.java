package cn.voicecomm.ai.voicesagex.console.api.api.mcp;

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
import java.util.List;

public interface McpService {

  /**
   * 获取Mcp列表
   *
   * @return 数据
   */
  CommonRespDto<PagingRespDto<McpDto>> getPageList(McpPageReq pageReq);

  Long getMcpNo();

  /**
   * 获取列表
   *
   * @param McpReq McpReq
   * @return 数据
   */
  CommonRespDto<List<McpDto>> getList(McpReq McpReq);

  /**
   * 获取列表（用于工作流）
   *
   * @param name 名称
   * @return 数据
   */
  CommonRespDto<List<McpTagGroupDto>> listMcpGroupedByTags(String name);

  /**
   * 获取Mcp工具详情数据
   *
   * @param id Mcp id
   * @return 数据
   */
  CommonRespDto<McpDataDto> getTools(Integer id);
  /**
   * 获取Mcp详情数据
   *
   * @param id Mcp id
   * @return 数据
   */
  CommonRespDto<McpDto> getInfo(Integer id);

  /**
   * Mcp是否可用
   *
   * @param id Mcp id
   * @return 是否可用 true；可用；false：不可用
   */
  CommonRespDto<Boolean> isAvailable(Integer id);

  /**
   * 新增Mcp数据
   *
   * @param McpDto McpDto
   * @return 成功的id
   */
  CommonRespDto<Integer> save(McpDto McpDto);

  /**
   * 修改Mcp数据
   *
   * @param McpDto McpDto
   * @return 是否成功
   */
  CommonRespDto<Boolean> update(McpDto McpDto);

  /**
   * 删除Mcp数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

  /**
   * 批量删除Mcp
   *
   * @param ids 主键id集合
   * @return 是否成功
   */
  CommonRespDto<Boolean> deleteBatch(List<Integer> ids);

  /**
   * 批量上下架Mcp
   *
   * @param mcpBatchReq McpBatchReq
   * @return 是否成功
   */
  CommonRespDto<Boolean> shelfBatch(McpBatchReq mcpBatchReq);


  /**
   * 批量更新标签
   *
   * @param mcpTagBatchReq mcpTagBatchReq
   * @return 是否成功
   */
  CommonRespDto<Boolean> updateTagBatch(McpTagBatchReq mcpTagBatchReq);


  /**
   * 批量添加Mcp到应用
   *
   * @param req req
   * @return 是否成功
   */
  CommonRespDto<Boolean> addMcpListToApplication(McpApplicationAddReq req);


  /**
   * 删除单个mcp和app关联
   *
   * @param req req
   * @return CommonRespDto
   */
  CommonRespDto<Void> removeMcpAppRelation(McpAppRelationRemoveReq req);

}

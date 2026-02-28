package cn.voicecomm.ai.voicesagex.console.api.api.mcp;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagDto;
import java.util.List;

/**
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface McpTagService {

  /**
   * 获取Mcp标签列表
   *
   * @return 包含Mcp标签列表的CommonRespDto对象
   */
  CommonRespDto<List<McpTagDto>> getList(String tagName);


  /**
   * 根据已上架的Mcp获取所有标签列表
   *
   * @return CommonRespDto
   */
  CommonRespDto<List<McpTagDto>> getListByShelfMcp();

  /**
   * 根据ID获取Mcp标签
   *
   * @param id Mcp标签的唯一标识
   * @return 包含Mcp标签信息的CommonRespDto对象
   */
  CommonRespDto<McpTagDto> getById(Integer id);

  /**
   * 添加新的Mcp标签
   *
   * @param dto 包含要添加的Mcp标签信息的McpTagDto对象
   * @return 包含新添加标签ID的CommonRespDto对象
   */
  CommonRespDto<Integer> add(McpTagDto dto);

  /**
   * 更新Mcp标签信息
   *
   * @param dto 包含要更新的Mcp标签信息的McpTagDto对象
   * @return 表示更新操作是否成功的CommonRespDto对象
   */
  CommonRespDto<Void> update(McpTagDto dto);

  /**
   * 删除指定ID的Mcp标签
   *
   * @param id 要删除的Mcp标签的唯一标识
   * @return 表示删除操作是否成功的CommonRespDto对象
   */
  CommonRespDto<Void> delete(Integer id);


}


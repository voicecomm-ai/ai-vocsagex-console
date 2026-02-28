package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * MCP 数据结构
 */
@Data
public class McpDataDto {

  /**
   * MCP 名称
   */
  @JsonProperty("mcp_name")
  private String mcpName;

  /**
   * 工具列表
   */
  private List<Tool> tools;

  /**
   * 工具信息
   */
  @Data
  public static class Tool {

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 参数模式定义（schema）
     */
    @JsonProperty("args_schema")
    private ArgsSchema argsSchema;

    /**
     * 响应格式
     */
    @JsonProperty("response_format")
    private String responseFormat;
  }

  /**
   * 参数模式定义
   */
  @Data
  public static class ArgsSchema {

    /**
     * 参数属性
     */
    private String properties;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 必填参数
     */
    private List<String> required;
  }
}

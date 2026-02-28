package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: gaox
 * @date: 2025/10/22 14:15
 */

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "工作流发布历史 DTO")
public class WorkflowsPublishHistoryDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键ID，自增整数
   */
  @Schema(description = "主键ID，自增整数")
  private Integer id;

  /**
   * 应用ID
   */
  @Schema(description = "应用ID")
  private Integer app_id;

  /**
   * 工作流类型 (workflow/chat)
   */
  @Schema(description = "工作流类型 (workflow/chat)")
  private String type;

  /**
   * 版本 (draft/具体版本号)
   */
  @Schema(description = "版本 (draft/具体版本号)")
  private String version;

  /**
   * 工作流图配置 (JSON)
   */
  @Schema(description = "工作流图配置 (JSON)")
  private JSONObject graph;

  /**
   * 功能特性配置 (JSON)
   */
  @Schema(description = "功能特性配置 (JSON)")
  private JSONObject features;

  /**
   * 环境变量 (JSON)
   */
  @Schema(description = "环境变量 (JSON)")
  private JSONArray environment_variables;

  /**
   * 对话变量 (JSON)
   */
  @Schema(description = "对话变量 (JSON)")
  private JSONObject conversation_variables;

  /**
   * 创建者ID
   */
  @Schema(description = "创建者ID")
  private Integer create_by;

  /**
   * 创建时间
   */
  @Schema(description = "创建时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
  private Date create_time;


  @Schema(description = "app名称")
  private String appName;

  @Schema(description = "app图标")
  private String appIconUrl;
}
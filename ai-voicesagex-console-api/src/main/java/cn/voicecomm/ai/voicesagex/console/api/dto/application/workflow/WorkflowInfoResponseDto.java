package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serial;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工作流DTO
 *
 * @author wangf
 * @date 2025/1/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowInfoResponseDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键ID，自增整数
   */
  private Integer id;

  /**
   * 租户ID
   */
  private String tenant_id;

  /**
   * 应用ID
   */
  private Integer app_id;

  /**
   * 工作流类型 (workflow/chat)
   */
  private String type;

  /**
   * 版本 (draft/具体版本号)
   */
  private String version;

  /**
   * 标记名称
   */
  private String marked_name;

  /**
   * 标记注释
   */
  private String marked_comment;

  /**
   * 工作流图配置 (JSON)
   */
  private JsonNode graph;

  /**
   * 功能特性配置 (JSON)
   */
  private JSONObject features;

  /**
   * 环境变量 (JSON)
   */
  private JSONArray environment_variables;

  /**
   * 对话变量 (JSON)
   */
  private JSONObject conversation_variables;


  /**
   * 应用名称
   */
  private String application_name;


  /**
   * 状态  -1删除，0草稿，1已发布
   */
  private Integer status;


  /**
   * 应用icon url
   */
  private String application_icon_url;

  /**
   * 应用标签
   */
  private List<ApplicationExperienceTagDto> tags;

  /**
   * 开启工作流追踪
   */
  private Boolean enableWorkflowTrace;

  /**
   * 创建人用户名
   */
  private String createUsername;


  /**
   * 应用描述
   */
  private String applicationDescription;
} 
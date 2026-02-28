package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
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
public class WorkflowDto extends BaseAuditDto {

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
  private JSONObject graph;

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
   * 更新请求uuid（唯一标识，防止订阅后收到消息自我更新）
   */
  private String update_request_uuid;
} 
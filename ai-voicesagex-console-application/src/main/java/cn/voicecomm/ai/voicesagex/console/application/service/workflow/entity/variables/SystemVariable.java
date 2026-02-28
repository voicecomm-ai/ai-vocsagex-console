package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SystemVariable {

  /**
   * 用户ID
   */
  private Integer user_id;

  /**
   * 应用ID
   */
  private Integer app_id;

  /**
   * 工作流ID
   */
  private Integer workflow_id;

  /**
   * 文件列表
   */
  private List<File> files = List.of();

  /**
   * 工作流执行ID
   */
  private String workflow_execution_id;

  /**
   * 查询内容
   */
  private String query;

  /**
   * 对话ID
   */
  private String conversation_id;

  /**
   * 对话计数
   */
  private Integer dialogue_count;

  /**
   * 创建空的系统变量
   */
  public static SystemVariable empty() {
    return new SystemVariable();
  }
}

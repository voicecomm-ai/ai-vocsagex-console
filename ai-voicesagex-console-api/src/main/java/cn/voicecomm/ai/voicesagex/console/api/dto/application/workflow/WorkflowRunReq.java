package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import cn.hutool.json.JSONObject;
import java.io.Serializable;
import lombok.Data;

/**
 * 单个节点运行请求
 *
 * @author wangf
 * @date 2025/8/5 下午 2:33
 */
@Data
public class WorkflowRunReq implements Serializable {

  /**
   * 应用id (application id)
   */
  private Integer app_id;

  /**
   * 用户输入 (获取最后运行结果时，无需传递)
   */
  private JSONObject user_inputs;

  /**
   * 工作流运行id (获取最后运行结果时，无需传递)
   */
  private String workflow_run_id;

}

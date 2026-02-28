package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工作流执行状态枚举
 *
 * @author wangf
 * @date 2025/8/25
 */
@Getter
@AllArgsConstructor
public enum WorkflowExecutionStatus {
    /**
     * 运行中
     */
    RUNNING("running"),

    /**
     * 执行成功
     */
    SUCCEEDED("succeeded"),

    /**
     * 执行失败
     */
    FAILED("failed"),

    /**
     * 已停止
     */
    STOPPED("stopped"),

    /**
     * 部分成功
     */
    PARTIAL_SUCCEEDED("partial-succeeded");

    private final String value;
}

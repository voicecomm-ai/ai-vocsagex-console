package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 路由节点状态类
 * 表示工作流中单个节点的执行状态信息
 */
@Data
@EqualsAndHashCode
public class RouteNodeState {
    
    /**
     * 节点状态枚举
     */
    public enum Status {
        RUNNING("running"),
        SUCCESS("success"),
        FAILED("failed"),
        PAUSED("paused"),
        EXCEPTION("exception");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * 节点状态ID
     */
    private String id;
    
    /**
     * 节点ID
     */
    private String node_id;
    
    /**
     * 节点运行结果
     */
    private NodeRunResult node_run_result;
    
    /**
     * 节点状态
     */
    private Status status = Status.RUNNING;
    
    /**
     * 开始时间
     */
    private LocalDateTime start_at;
    
    /**
     * 暂停时间
     */
    private LocalDateTime paused_at;
    
    /**
     * 完成时间
     */
    private LocalDateTime finished_at;
    
    /**
     * 失败原因
     */
    private String failed_reason;
    
    /**
     * 暂停者
     */
    private String paused_by;
    
    /**
     * 索引
     */
    private int index = 1;
    
    /**
     * 设置节点完成状态
     * 
     * @param run_result 运行结果
     */
    public void setFinished(NodeRunResult run_result) {
        // 检查节点状态是否已经完成
        if (this.status == Status.SUCCESS || this.status == Status.FAILED || this.status == Status.EXCEPTION) {
            throw new RuntimeException("Route state " + this.id + " already finished");
        }
        
        // 根据运行结果设置状态
        if (run_result.getStatus() == WorkflowNodeExecutionStatus.SUCCEEDED) {
            this.status = Status.SUCCESS;
        } else if (run_result.getStatus() == WorkflowNodeExecutionStatus.FAILED) {
            this.status = Status.FAILED;
            this.failed_reason = run_result.getError();
        } else if (run_result.getStatus() == WorkflowNodeExecutionStatus.EXCEPTION) {
            this.status = Status.EXCEPTION;
            this.failed_reason = run_result.getError();
        } else {
            throw new RuntimeException("Invalid route status " + run_result.getStatus());
        }
        
        this.node_run_result = run_result;
        this.finished_at = LocalDateTime.now();
    }
}

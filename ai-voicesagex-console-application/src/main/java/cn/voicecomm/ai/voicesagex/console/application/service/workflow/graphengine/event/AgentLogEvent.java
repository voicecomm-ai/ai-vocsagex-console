package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 代理日志事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class AgentLogEvent extends BaseAgentEvent {

  private String id;
  private String label;
  private String nodeExecutionId;
  private String parentId;
  private String error;
  private String status;
  private Map<String, Object> data;
  private Map<String, Object> metadata;
  private String nodeId;

  // Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getNodeExecutionId() {
    return nodeExecutionId;
  }

  public void setNodeExecutionId(String nodeExecutionId) {
    this.nodeExecutionId = nodeExecutionId;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }
}
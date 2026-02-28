package cn.voicecomm.ai.voicesagex.console.application.service.workflow;


import lombok.Getter;

@Getter
public enum WorkflowEventEnum {
  PING("ping"),
  ERROR("error"),
  MESSAGE("message"),
  MESSAGE_END("message_end"),
  TTS_MESSAGE("tts_message"),
  TTS_MESSAGE_END("tts_message_end"),
  MESSAGE_FILE("message_file"),
  MESSAGE_REPLACE("message_replace"),
  AGENT_THOUGHT("agent_thought"),
  AGENT_MESSAGE("agent_message"),
  WORKFLOW_STARTED("workflow_started"),
  WORKFLOW_FINISHED("workflow_finished"),
  NODE_STARTED("node_started"),
  NODE_FINISHED("node_finished"),
  NODE_RETRY("node_retry"),
  PARALLEL_BRANCH_STARTED("parallel_branch_started"),
  PARALLEL_BRANCH_FINISHED("parallel_branch_finished"),
  ITERATION_STARTED("iteration_started"),
  ITERATION_NEXT("iteration_next"),
  ITERATION_COMPLETED("iteration_completed"),
  LOOP_STARTED("loop_started"),
  LOOP_NEXT("loop_next"),
  LOOP_COMPLETED("loop_completed"),
  TEXT_CHUNK("text_chunk"),
  TEXT_REPLACE("text_replace"),
  AGENT_LOG("agent_log");


  private final String value;

  WorkflowEventEnum(String value) {
    this.value = value;
  }

  public WorkflowEventEnum getByValue() {
    for (WorkflowEventEnum event : values()) {
      if (event.value.equals(value)) {
        return event;
      }
    }
    return null;
  }

}

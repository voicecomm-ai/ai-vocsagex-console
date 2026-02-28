package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import lombok.Getter;

/**
 * 图边状态
 */
@Getter
public enum GraphEdgeState {


  UNKNOWN("unknown"),
  TAKEN("taken"),
  SKIPPED("skipped");
  private final String state;

  GraphEdgeState(String state) {
    this.state = state;
  }

}

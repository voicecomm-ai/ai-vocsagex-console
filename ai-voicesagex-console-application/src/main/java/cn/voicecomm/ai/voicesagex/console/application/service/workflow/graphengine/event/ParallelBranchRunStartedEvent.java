package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 并行分支运行开始事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ParallelBranchRunStartedEvent extends BaseParallelBranchEvent {
  // 仅继承父类属性
}
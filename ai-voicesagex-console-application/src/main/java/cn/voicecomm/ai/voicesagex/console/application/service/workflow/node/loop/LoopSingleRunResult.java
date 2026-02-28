package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: gaox
 * @date: 2025/11/14 13:43
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LoopSingleRunResult {

  Map<String, Object> outputs;

  Long tokens;

  Boolean success;

  String error;

  String errorType;
}

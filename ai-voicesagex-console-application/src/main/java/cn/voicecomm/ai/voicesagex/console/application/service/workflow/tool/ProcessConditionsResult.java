package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import java.util.List;
import java.util.Map;

/**
 * 处理条件的结果封装类
 *
 * @param inputConditions 输入条件列表
 * @param groupResults    组结果列表
 * @param finalResult     最终结果
 */
public record ProcessConditionsResult(List<Map<String, Object>> inputConditions,
                               List<Boolean> groupResults, boolean finalResult) {

}

package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.answer;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GenerateRouteChunk;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphEdge;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * 答案流生成路由类 定义答案节点的流生成路由信息
 */
@Data
public class AnswerStreamGenerateRoute {

  /**
   * 答案依赖关系（答案节点ID -> 依赖的答案节点ID列表）
   */
  private Map<String, List<String>> answerDependencies;

  /**
   * 答案生成路由（答案节点ID -> 生成路由块列表）
   */
  private Map<String, List<GenerateRouteChunk>> answerGenerateRoute;


  public static AnswerStreamGenerateRoute init(Map<String, JSONObject> nodeIdConfigMapping,
      Map<String, List<GraphEdge>> leafNodeIds, Map<String, String> nodeParallelMapping) {

    return new AnswerStreamGenerateRoute();
  }
}

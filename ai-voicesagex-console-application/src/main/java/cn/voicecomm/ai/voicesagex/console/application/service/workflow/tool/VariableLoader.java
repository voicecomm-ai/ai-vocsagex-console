package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 变量加载器接口
 */
public interface VariableLoader {

  /**
   * 最小选择器长度
   */
  int MIN_SELECTORS_LENGTH = 2;

  /**
   * 加载变量
   *
   * @param variablesToLoad 需要加载的变量选择器列表
   * @return 加载的变量列表
   */
  List<Variable> loadVariables(List<List<String>> variablesToLoad);

  /**
   * 将变量加载到变量池中
   *
   * @param variableLoader  变量加载器
   * @param variablePool    变量池
   * @param variableMapping 变量映射
   * @param userInputs      用户输入
   */
  static void loadIntoVariablePool(
      VariableLoader variableLoader,
      VariablePool variablePool,
      Map<String, List<String>> variableMapping,
      Map<String, Object> userInputs) {

    // 需要加载的变量列表
    List<List<String>> variablesToLoad = new ArrayList<>();

    for (Map.Entry<String, List<String>> entry : variableMapping.entrySet()) {
      String key = entry.getKey();
      List<String> selector = entry.getValue();

      // 检查变量键格式
      List<String> nodeVariableList = Arrays.asList(key.split("\\."));
      if (nodeVariableList.isEmpty()) {
        throw new IllegalArgumentException(
            "Invalid variable key: " + key + ". It should have at least one element.");
      }

      // 如果键在用户输入中，跳过
      if (userInputs.containsKey(key)) {
        continue;
      }

      // 构建节点变量键
      String nodeVariableKey = String.join(".",
          nodeVariableList.subList(1, nodeVariableList.size()));

      // 如果节点变量键在用户输入中，跳过
      if (userInputs.containsKey(nodeVariableKey)) {
        continue;
      }

      // 如果变量池中没有该变量，添加到加载列表
      if (variablePool.get(selector) == null) {
        variablesToLoad.add(new ArrayList<>(selector));
      }
    }

    if (variableLoader == null) {
      return;
    }
    // 加载变量
    List<Variable> loaded = variableLoader.loadVariables(variablesToLoad);

    // 将加载的变量添加到变量池
    for (Variable var : loaded) {
      if (var.getSelector().size() < MIN_SELECTORS_LENGTH) {
        throw new IllegalArgumentException("Invalid variable: " + var);
      }
      variablePool.appendVariablesRecursively(
          var.getSelector().getFirst(),
          var.getSelector().subList(1, var.getSelector().size()),
          var
      );
    }
  }
}
package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变量模板解析器类
 */
@Data
public class VariableTemplateParser {

  private String template;
  private List<String> variableKeys;

  public static final Pattern REGEX = Pattern.compile(
      "\\{\\{#([a-zA-Z0-9_-]{1,50}(?:\\.[a-zA-Z_][a-zA-Z0-9_]{0,29}){1,10})#\\}\\}");
  private static final Pattern SELECTOR_PATTERN = Pattern.compile(
      "\\{\\{([#a-zA-Z0-9_]{1,50}(?:\\.[a-zA-Z_][a-zA-Z0-9_]{0,29}){1,10}#)\\}\\}");
  public VariableTemplateParser(String template) {
    this.template = template;
    this.variableKeys = extract();
  }

  /**
   * 提取模板中的所有变量键
   *
   * @return 变量键列表
   */
  public List<String> extract() {
    List<String> matches = new ArrayList<>();
    Matcher matcher = REGEX.matcher(template);

    while (matcher.find()) {
      matches.add(matcher.group(1));
    }
    // 去重
    return new ArrayList<>(new LinkedHashSet<>(matches));
  }

  /**
   * 提取变量选择器
   *
   * @return 变量选择器列表
   */
  public List<VariableSelector> extractVariableSelectors() {
    List<VariableSelector> variableSelectors = new ArrayList<>();

    for (String variableKey : variableKeys) {
      String removeHash = variableKey.replace("#", "");
      String[] splitResult = removeHash.split("\\.");

      if (splitResult.length < 2) {
        continue;
      }

      List<String> valueSelector = Arrays.asList(splitResult);
      variableSelectors.add(new VariableSelector(variableKey, valueSelector));
    }

    return variableSelectors;
  }

  /**
   * 格式化模板字符串
   *
   * @param inputs 输入变量映射
   * @return 格式化后的字符串
   */
  public String format(Map<String, Object> inputs) {
    String result = template;

    for (String variableKey : variableKeys) {
      Object value = inputs.get(variableKey);
      String replacement = value != null ? value.toString() : "";
      result = result.replace("{{" + variableKey + "}}", replacement);
    }

    // 移除模板变量标记
    result = result.replaceAll("<\\|.*?\\|>", "");

    return result;
  }

  /**
   * 从模板中提取选择器（静态方法）
   *
   * @param template 模板字符串
   * @return 变量选择器列表
   */
  public static List<VariableSelector> extractSelectorsFromTemplate(String template) {
    VariableTemplateParser parser = new VariableTemplateParser(template);
    return parser.extractVariableSelectors();
  }

  /**
   * 变量选择器类
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class VariableSelector {

    private String variable;
    private List<String> valueSelector;
  }
}



package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.FileAttribute;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.FileManager;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.SystemVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.VariableFactory;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.FileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.NoneSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ObjectSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentGroup;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * 变量池 - 用于管理工作流中的变量
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Slf4j
public class VariablePool {

  // 变量字典，用于通过选择器查找变量
  private Map<String, Map<String, Segment>> variable_dictionary = new HashMap<>();

  // 用户输入
  private Map<String, Object> user_inputs = new HashMap<>();

  // 系统变量
  private SystemVariable system_variables;

  // 环境变量
  private List<Variable> environment_variables = new ArrayList<>();

  // 对话变量
  private List<Variable> conversation_variables = new ArrayList<>();


  public void _setupVariablePool(String query, List<File> files, Integer userId,
      Map<String, Object> userInputs, WorkflowPo workflowPo, List<Variable> conversationVariables,
      String nodeType, String conversationId) {
    SystemVariable systemVariable;
    if (NodeType.START.getValue().equals(nodeType)) {
      systemVariable = new SystemVariable().setUser_id(userId).setApp_id(workflowPo.getApp_id())
          .setFiles(files).setWorkflow_id(workflowPo.getId())
          .setWorkflow_execution_id(UUID.randomUUID().toString());
      if (!Objects.equals(workflowPo.getType(), "workflow")) {
        systemVariable.setQuery(query);
        systemVariable.setConversation_id(conversationId);
        systemVariable.setDialogue_count(0);
      }
    } else {
      systemVariable = SystemVariable.empty();
    }
    this.initPool(systemVariable, userInputs,
        JSONUtil.toList(workflowPo.getEnvironment_variables(), Variable.class),
        conversationVariables);
  }

  public void initPool(SystemVariable systemVariables, Map<String, Object> userInputs,
      List<Variable> environmentVariables, List<Variable> conversationVariables) {
    this.system_variables = systemVariables;
    this.user_inputs = userInputs;
    this.environment_variables = environmentVariables;
    this.conversation_variables = conversationVariables;
  }


  /**
   * 添加变量到变量池
   *
   * @param selector 变量的选择器
   * @param value    变量的值
   * @throws IllegalArgumentException 如果选择器无效
   */
  public void add(List<String> selector, Object value) {
    if (selector.size() < 2) {
      throw new IllegalArgumentException("Invalid selector");
    }
    log.info("开始添加变量：{},value:{}", selector, value);
    Segment variable;
    if (value instanceof Variable) {
      variable = (Segment) value;
    } else if (value instanceof Segment) {
      variable = VariableFactory.segmentToVariable((Segment) value, selector);
    } else {
      Segment segment = VariableFactory.buildSegment(value);
      variable = VariableFactory.segmentToVariable(segment, selector);
    }

    String hashKey = StrUtil.join(".", selector.subList(1, selector.size()));
    variable_dictionary.computeIfAbsent(selector.getFirst(), k -> new HashMap<>())
        .put(hashKey, variable);
  }

  /**
   * 根据选择器从变量池中获取值
   *
   * @param selector 用于标识变量的选择器
   * @return 与给定选择器关联的值，如果未找到则返回null
   */
  public Segment get(List<String> selector) {
    // 检查选择器是否为空或长度不足
    if (CollUtil.isEmpty(selector) || selector.size() < 2) {
      log.warn("无效的选择器，长度小于2或为空: {}", selector);
      return null;
    }

    log.info("VariablePool获取变量....选择器：{}, 当前pool变量：{}", selector,
        JSONUtil.toJsonStr(variable_dictionary));

    String hKey = StrUtil.join(".", selector.subList(1, selector.size()));

    // 获取节点变量映射
    Map<String, Segment> nodeVariables = variable_dictionary.get(selector.getFirst());
    if (nodeVariables == null) {
      log.warn("未找到节点变量映射，节点ID: {}", selector.getFirst());
      return null;
    }

    // 尝试直接获取变量值
    Segment value = nodeVariables.get(hKey);
    if (value == null) {
      // 获取属性名和父选择器
      String attr = selector.getLast();
      // 检查是否为文件属性
      if (!FileAttribute.contains(attr)) {
        return null;
      }
      log.info("未直接找到变量值，获取文件属性，key: {}", hKey);
      List<String> parentSelector = selector.subList(0, selector.size() - 1);
      log.info("属性名: {}, 父选择器: {}", attr, parentSelector);

      // 获取父级变量
      value = get(parentSelector);
      if (!(value instanceof FileSegment) && !(value instanceof NoneSegment)) {
        log.info("父级变量不是文件类型，类型: {}",
            value != null ? value.getClass().getSimpleName() : "null");
        return null;
      }

      // 如果是文件片段，获取文件属性
      if (value instanceof FileSegment fileSegment) {
        FileAttribute fileAttr = FileAttribute.fromValue(attr);
        log.info("获取文件属性: {}", fileAttr);
        Object attrValue = FileManager.getAttr((File) fileSegment.getValue(), fileAttr);
        log.info("文件属性值: {}", attrValue);
        return VariableFactory.buildSegment(attrValue);
      }

      log.info("返回NoneSegment类型的父级变量");
      return value;
    }

    log.info("成功获取变量值，变量: {}, value: {}", selector, value.getValue());
    return value;
  }


  /**
   * 将模板字符串转换为 SegmentGroup
   *
   * @param template 模板字符串，如 "你好{{#user.name#}}，很高兴见到你"
   * @return SegmentGroup 结构化片段组 [你好,xxx,，很高兴见到你]
   */
  public SegmentGroup convertTemplate(String template) {
    // 过滤掉空的部分并处理
    List<Segment> segments = new ArrayList<>();

    if (!template.contains("{{#")) {
      // 没有变量，直接返回
      segments.add(VariableFactory.buildSegment(template));
      return new SegmentGroup(segments, SegmentType.GROUP);
    }
    // 使用Matcher获取所有匹配项（变量部分）
    List<String> allParts = parseTemplateParts(template);
    // 处理每个部分
    for (String part : allParts) {
      if (part != null && !part.isEmpty()) {
        Matcher matcher = VariableTemplateParser.REGEX.matcher(part);
        if (matcher.matches()) {
          String group = matcher.group(1);
          // 是变量选择器
          String[] selectorArray = group.split("\\.");
          List<String> selector = Arrays.asList(selectorArray);
          Segment variable = get(selector);
          segments.add(variable);
        } else {
          segments.add(VariableFactory.buildSegment(part));
        }
      }
    }

    return new SegmentGroup(segments, SegmentType.GROUP);
  }

  /**
   * 解析模板字符串，将其中的变量占位符提取出来，并与普通文本部分交替组成列表。
   * <ol>
   *   <li>使用正则表达式匹配模板中的变量占位符</li>
   *   <li>遍历所有匹配项，分别提取变量前的普通文本和变量名</li>
   *   <li>处理最后一段未被匹配到的普通文本</li>
   * </ol>
   *
   * @param template 输入的模板字符串，包含普通文本和变量占位符
   * @return 包含交替文本和变量名的字符串列表
   */
  @NotNull
  private static List<String> parseTemplateParts(String template) {
    Matcher matcher = VariableTemplateParser.REGEX.matcher(template);
    // 重新构建所有部分（文本和变量交替）
    List<String> allParts = new ArrayList<>();

    int lastEnd = 0;
    while (matcher.find()) {
      // 添加匹配前的文本
      if (matcher.start() > lastEnd) {
        allParts.add(template.substring(lastEnd, matcher.start()));
      }
      // 添加捕获组内容（变量名）
      allParts.add(matcher.group(0));  // group(0) 是整个匹配内容
      lastEnd = matcher.end();
    }

    // 添加最后一段
    if (lastEnd < template.length()) {
      allParts.add(template.substring(lastEnd));
    }
    return allParts;
  }


  /**
   * 提取模板中的变量名列表
   * <ol>
   *   <li>使用预定义的正则表达式匹配模板中的变量</li>
   *   <li>遍历所有匹配项，提取变量名</li>
   *   <li>将提取到的变量名添加到结果列表中</li>
   * </ol>
   *
   * @param template 输入的模板字符串
   * @return 包含所有提取到的变量名的列表
   */
  public static List<String> extractVariableNames(String template) {
    Matcher matcher = VariableTemplateParser.REGEX.matcher(template);
    // 重新构建所有部分（文本和变量交替）
    List<String> allParts = new ArrayList<>();

    while (matcher.find()) {
      // 添加捕获组内容（变量名）
      allParts.add(matcher.group(1));  // group(1) 是第一个捕获组
    }
    return allParts;
  }


  /**
   * 递归地将变量添加到变量池中，并根据变量值的类型进行递归处理。
   * <ol>
   *   <li>构造完整的选择器并添加到变量池：[node_id] + variable_key_list</li>
   *   <li>如果 variableValue 是 ObjectSegment 或 Map，则递归追加变量</li>
   * </ol>
   *
   * @param nodeId          节点ID，用于构造变量选择器的前缀
   * @param variableKeyList 变量键列表，用于构造变量选择器的后缀
   * @param variableValue   变量值，根据其类型决定是否递归处理
   */
  public void appendVariablesRecursively(
      String nodeId,
      List<String> variableKeyList,
      Object variableValue) {

    // 构造完整的选择器并添加到变量池
    ArrayList<String> varList = new ArrayList<>() {{
      add(nodeId);
      addAll(variableKeyList);
    }};

    // 将变量添加到池中
    this.add(varList, variableValue);

    // 如果 variableValue 是 ObjectSegment 或 Map，则递归追加变量
    Map<String, Object> variableDict = null;
    switch (variableValue) {
      case ObjectSegment objectSegment ->
          variableDict = (Map<String, Object>) objectSegment.getValue();
      case Map ignored -> variableDict = (Map<String, Object>) variableValue;
//      case File file -> variableDict = BeanUtil.beanToMap(file);
      case null, default -> {
        return;
      }
    }

    // 遍历字典中的每个键值对并递归处理
    for (Map.Entry<String, Object> entry : variableDict.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      // 构造新的键列表
      List<String> newKeyList = new java.util.ArrayList<String>() {{
        addAll(variableKeyList);
        add(key);
      }};

      // 递归调用
      appendVariablesRecursively(nodeId, newKeyList, value);
    }
  }


}


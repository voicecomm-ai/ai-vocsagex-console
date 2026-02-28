package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🚀 Jackson 工具类 - 生产级封装 功能：JSON 字符串 ↔ JavaBean / Map / List / JsonNode 互转 特性：线程安全、异常包装、默认忽略未知字段、支持
 * LocalDateTime、空安全
 *
 * @author YourName
 * @version 1.0
 */
public class JacksonUtil {

  // 🚀 静态全局 ObjectMapper（线程安全，可复用）
  public static final ObjectMapper MAPPER = new ObjectMapper();

  // 🎯 静态初始化配置
  static {

    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    //✅ 注册Hutool JSONNull序列化器，解决null被转为JSONNull的问题
    SimpleModule jsonNullModule = new SimpleModule();
    jsonNullModule.addSerializer(JSONNull.class, new JSONNullSerializer());
    MAPPER.registerModule(jsonNullModule);
//    // ✅ 忽略实体类中不存在的 JSON 字段（前端传多了也不报错）
//    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//    // ✅ 允许基本类型字段为 null（如 int age = null → 不报错，设为 0）
//    MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
//
//    // ✅ 序列化时忽略值为 null 的字段（紧凑输出）
//    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//    // ✅ 注册 Java 8 时间模块（支持 LocalDateTime、LocalDate 等）
//    JavaTimeModule javaTimeModule = new JavaTimeModule();
//    // 自定义 LocalDateTime 序列化/反序列化格式
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
//    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
//    MAPPER.registerModule(javaTimeModule);
  }

  // ███████████████████████████████████████████████████████████████████████
  // █                        JSON 字符串 → Java 对象                       █
  // ███████████████████████████████████████████████████████████████████████
  private static class JSONNullSerializer extends JsonSerializer<JSONNull> {

    @Override
    public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeNull(); // 将JSONNull → JSON标准null
    }
  }


  /**
   * JSON 字符串 → JsonNode
   */
  public static JsonNode readTree(String data) {
    if (data == null) {
      data = "{}";
    }
    try {
      return MAPPER.readTree(data);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }

  /**
   * 从 JsonNode 获取指定路径的值（支持多层嵌套，如 "user.address.city"）
   *
   * @param node JsonNode 对象
   * @param path 路径，用点分隔，例如 "user.address.city"
   * @return 路径对应的值，如果路径不存在则返回 null
   */
  public static JsonNode at(JsonNode node, String path) {
    return node.at(path);
  }

  /**
   * JSON 字符串 → JavaBean 例：json → User.class
   */
  public static <T> T toBean(String json, Class<T> clazz) {
    if (json == null) {
      return null;
    }
    try {
      return MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException("JSON反序列化失败: " + clazz.getSimpleName(), e);
    }
  }

  /**
   * JSON 字符串 → List<T> 例：json → List<User>
   */
  public static <T> List<T> toList(String json, Class<T> elementType) {
    if (json == null) {
      return null;
    }
    try {
      return MAPPER.readValue(json, MAPPER.getTypeFactory()
          .constructCollectionType(List.class, elementType));
    } catch (Exception e) {
      throw new RuntimeException("JSON转List失败: " + elementType.getSimpleName(), e);
    }
  }

  /**
   * JSON 字符串 → Map<String, Object>
   */
  public static Map<String, Object> toMap(String json) {
    if (json == null) {
      return null;
    }
    try {
      return MAPPER.readValue(json, new TypeReference<>() {
      });
    } catch (Exception e) {
      throw new RuntimeException("JSON转Map失败", e);
    }
  }

  // ███████████████████████████████████████████████████████████████████████
  // █                        Java 对象 → JSON 字符串                       █
  // ███████████████████████████████████████████████████████████████████████

  /**
   * Java对象 → JSON字符串（紧凑格式）
   */
  public static String toJsonStr(Object obj) {
    if (obj == null) {
      return "{}";
    }
    try {
      return MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException("对象序列化失败", e);
    }
  }

  /**
   * Java对象 → 格式化JSON字符串（带缩进，用于日志/调试）
   */
  public static String toJsonPrettyStr(Object obj) {
    if (obj == null) {
      return "{}";
    }
    try {
      return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException("对象格式化序列化失败", e);
    }
  }

  // ███████████████████████████████████████████████████████████████████████
  // █                        Java 对象 ↔ JsonNode                         █
  // ███████████████████████████████████████████████████████████████████████

  /**
   * Java对象 → JsonNode
   */
  public static JsonNode valueToTree(Object obj) {
    if (obj == null) {
      return NullNode.getInstance();
    }
    try {
      return MAPPER.valueToTree(obj);
    } catch (Exception e) {
      throw new RuntimeException("对象转JsonNode失败", e);
    }
  }

  /**
   * JsonNode → JSON字符串
   */
  public static String toJsonStr(JsonNode node) {
    if (node == null) {
      return "{}";
    }
    try {
      return MAPPER.writeValueAsString(node);
    } catch (Exception e) {
      throw new RuntimeException("JsonNode转JSON失败", e);
    }
  }

  /**
   * JsonNode → 格式化JSON字符串
   */
  public static String toJsonPrettyStr(JsonNode node) {
    if (node == null) {
      return "{}";
    }
    try {
      return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    } catch (Exception e) {
      throw new RuntimeException("JsonNode格式化失败", e);
    }
  }

  // ███████████████████████████████████████████████████████████████████████
  // █                        JsonNode 创建与操作辅助                       █
  // ███████████████████████████████████████████████████████████████████████

  /**
   * 创建空 JSON 对象 {}
   */
  public static ObjectNode createObjectNode() {
    return JsonNodeFactory.instance.objectNode();
  }

  /**
   * 创建空 JSON 数组 []
   */
  public static ArrayNode createArrayNode() {
    return JsonNodeFactory.instance.arrayNode();
  }

  /**
   * 安全转换：null 时返回空对象 {}
   */
  public static JsonNode toNodeOrEmptyObject(Object obj) {
    if (obj == null) {
      return createObjectNode();
    }
    try {
      JsonNode node = MAPPER.valueToTree(obj);
      return node.isNull() ? createObjectNode() : node;
    } catch (Exception e) {
      return createObjectNode();
    }
  }

  /**
   * 安全转换：null 时返回空数组 []
   */
  public static JsonNode toNodeOrEmptyArray(Object obj) {
    if (obj == null) {
      return createArrayNode();
    }
    try {
      JsonNode node = MAPPER.valueToTree(obj);
      return node.isNull() ? createArrayNode() : node;
    } catch (Exception e) {
      return createArrayNode();
    }
  }

  // ███████████████████████████████████████████████████████████████████████
  // █                         JsonNode 字段操作辅助                        █
  // ███████████████████████████████████████████████████████████████████████

  /**
   * 安全地向 JsonNode 添加字段（仅当是 ObjectNode 时）
   */
  public static void putField(JsonNode node, String fieldName, String value) {
    if (node instanceof ObjectNode) {
      ((ObjectNode) node).put(fieldName, value);
    } else {
      throw new IllegalArgumentException("目标节点不是 ObjectNode，无法添加字段: " + fieldName);
    }
  }

  /**
   * 安全地向 JsonNode 添加子节点
   */
  public static void setField(JsonNode node, String fieldName, JsonNode value) {
    if (node instanceof ObjectNode) {
      ((ObjectNode) node).set(fieldName, value);
    } else {
      throw new IllegalArgumentException("目标节点不是 ObjectNode，无法设置子节点: " + fieldName);
    }
  }

  /**
   * 安全地向 JsonNode 添加 POJO（自动转 JsonNode）
   */
  public static void setPOJOField(JsonNode node, String fieldName, Object pojo) {
    if (node instanceof ObjectNode) {
      JsonNode childNode = valueToTree(pojo);
      ((ObjectNode) node).set(fieldName, childNode);
    } else {
      throw new IllegalArgumentException(
          "目标节点不是 ObjectNode，无法设置 POJO 字段: " + fieldName);
    }
  }

  // 转换 Hutool 的 JSONObject 为 Map（处理 JSONNull）
  public Map<String, Object> convertToMap(JSONObject jsonObj) {
    Map<String, Object> map = new HashMap<>();
    jsonObj.forEach((key, value) -> {
      switch (value) {
        case JSONNull ignored -> map.put(key, null); // 转为 Java null
        case JSONObject jsonObject -> map.put(key, convertToMap(jsonObject)); // 递归处理嵌套对象
        case JSONArray jsonArray -> map.put(key, convertToList(jsonArray)); // 处理数组
        case null, default -> map.put(key, value);
      }
    });
    return map;
  }

  // 转换 Hutool 的 JSONArray 为 List
  public List<Object> convertToList(JSONArray jsonArray) {
    List<Object> list = new ArrayList<>();
    jsonArray.forEach(item -> {
      switch (item) {
        case JSONNull ignored -> list.add(null);
        case JSONObject jsonObject -> list.add(convertToMap(jsonObject));
        case JSONArray objects -> list.add(convertToList(objects));
        case null, default -> list.add(item);
      }
    });
    return list;
  }

}
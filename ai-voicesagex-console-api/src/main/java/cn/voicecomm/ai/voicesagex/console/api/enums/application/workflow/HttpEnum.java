package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

/**
 * @author: gaox
 * @date: 2025/9/8 15:17
 */
public enum HttpEnum {
  ;

  @Getter
  public enum HttpMethod {
    GET("get"),
    POST("post"),
    PUT("put"),
    PATCH("patch"),
    DELETE("delete"),
    HEAD("head"),
    OPTIONS("options"),
    GET_UPPER("GET"),
    POST_UPPER("POST"),
    PUT_UPPER("PUT"),
    PATCH_UPPER("PATCH"),
    DELETE_UPPER("DELETE"),
    HEAD_UPPER("HEAD"),
    OPTIONS_UPPER("OPTIONS");

    private final String value;

    HttpMethod(String value) {
      this.value = value;
    }
  }

  @Getter
  public enum AuthorizationType {
    NO_AUTH("no-auth"),

    API_KEY("api-key");

    private final String value;

    AuthorizationType(String value) {
      this.value = value;
    }

    // 根据value值获取type
    public static AuthorizationType getByValue(String value) {
      for (AuthorizationType type : AuthorizationType.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      return null;
    }
  }

  @Getter
  public enum AuthType {
    BASIC("basic"),

    BEARER("bearer"),

    CUSTOM("custom");

    private final String value;

    AuthType(String value) {
      this.value = value;
    }

    // 根据value值获取type
    public static AuthType getByValue(String value) {
      for (AuthType type : AuthType.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      return BASIC;
    }
  }

  @Getter
  public enum BodyType {
    NONE("none"),

    FORM_DATA("form-data"),

    X_WWW_FORM_URLENCODED("x-www-form-urlencoded"),

    RAW_TEXT("raw-text"),

    JSON("json"),

    BINARY("binary");

    private final String value;

    BodyType(String value) {
      this.value = value;
    }

    // 根据value值获取type
    public static BodyType getByValue(String value) {
      for (BodyType type : BodyType.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      return NONE;
    }
  }
}




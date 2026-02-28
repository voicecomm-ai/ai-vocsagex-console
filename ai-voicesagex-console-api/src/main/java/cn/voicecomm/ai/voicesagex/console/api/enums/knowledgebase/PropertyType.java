package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;

/**
 * 转换属性
 *
 * @author ryc
 * @date 2025/8/21
 */
public enum PropertyType {
  /**
   * 属性转换
   */
  INT8(8), INT16(16), INT32(32), INT64(64);

  private final int sizeInBytes;

  PropertyType(int sizeInBytes) {
    this.sizeInBytes = sizeInBytes;
  }

  public int getSizeInBytes() {
    return sizeInBytes;
  }

  public static int getStatus(String typeString) throws Exception {
    for (PropertyType type : PropertyType.values()) {
      if (type.name().equalsIgnoreCase(typeString)) {
        return type.sizeInBytes;
      }
    }
    throw new RuntimeException(ErrorConstants.UPDATE_TAG_EDGE_INT.getMessage());
  }
}


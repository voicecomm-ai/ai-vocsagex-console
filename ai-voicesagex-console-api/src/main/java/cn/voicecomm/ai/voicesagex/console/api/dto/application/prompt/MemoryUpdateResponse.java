package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 记忆更新响应
 *
 * @author wangf
 * @date 2025/9/8 下午 2:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class MemoryUpdateResponse extends BasePromptResponse implements Serializable {

  /**
   * 向量数组
   */
  private MemoryUpdateData data;

  /**
   * 内部类，用于存储具体的响应数据。
   */
  @Data
  public static class MemoryUpdateData implements Serializable {

    /**
     * 向量数组
     */
    private float[] vector;
  }
}




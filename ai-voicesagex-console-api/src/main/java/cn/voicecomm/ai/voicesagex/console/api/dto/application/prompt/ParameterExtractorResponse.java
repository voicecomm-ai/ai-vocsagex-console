package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 参数提取器响应
 *
 * @author wangf
 * @date 2025/9/8 下午 2:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ParameterExtractorResponse extends BasePromptResponse implements Serializable {

  /**
   * 包含实际数据的对象。
   */
  private ParameterExtractorData data;

  /**
   * 内部类，用于存储具体的响应数据。
   */
  @Data
  public static class ParameterExtractorData implements Serializable {
    /**
     * 提取的参数，kv取决于需要提取的参数格式
     */
    private Map<String, Object> result;
  }
}




package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OptimizePromptResponse extends BasePromptResponse implements Serializable {


  /**
   * 优化后的提示词数据（失败时为null）
   */
  private OptimizedPromptData data;


  /**
   * 优化后的提示词详情
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class OptimizedPromptData implements Serializable {

    /**
     * 优化后的完整提示词 例如："你是一名时尚穿搭顾问..."
     */
    private String prompt;
  }

}

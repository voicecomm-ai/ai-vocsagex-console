package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BasePromptResponse implements Serializable {

  /**
   * 状态码（1000=成功, 2000=失败）
   */
  private int code;

  /**
   * 状态信息（成功时为唯一标识符，失败时为错误描述）
   */
  private String msg;


  /**
   * 是否结束
   */
  private Boolean done;

  /**
   * Token用量统计（失败时为null）
   */
  private UsageInfo usage;


  /**
   * Token用量统计详情
   */
  @Data
  @Accessors(chain = true)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UsageInfo implements Serializable {

    /**
     * 输入文本消耗的token数量
     */
    private Integer prompt_tokens = 0;

    /**
     * 输出文本消耗的token数量
     */
    private Integer completion_tokens = 0;

    /**
     * 总消耗token数量（输入+输出）
     */
    private Integer total_tokens = 0;
  }

}

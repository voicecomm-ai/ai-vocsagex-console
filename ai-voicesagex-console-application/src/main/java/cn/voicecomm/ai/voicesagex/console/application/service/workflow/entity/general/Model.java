package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: gaox
 * @date: 2025/8/6 14:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Model {
  /**
   * 提供者
   */
  private String provider;

  /**
   * 名称
   */
  private String name;

  /**
   * 模式
   */
  private String mode;

  /**
   * 模型id
   */
  private Integer id;

  /**
   * 完成参数配置
   */
  private CompletionParams completion_params;


  /**
   * 完成参数配置类
   */
  @Data
  @Accessors(chain = true)
  public static class CompletionParams implements Serializable {

    /**
     * top_k 参数
     */
    private Integer topK;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * top_p 参数
     */
    private Double topP;

    /**
     * 最大令牌数
     */
    private Integer max_tokens;

    /**
     * 随机种子
     */
    private Integer seed;

    /**
     * 响应格式
     */
    private String responseFormat;

    /**
     * 重复惩罚参数
     */
    private Double repetitionPenalty;

    /**
     * 停止符列表
     */
    private List<String> stop;
  }
}

package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大语言模型（LLM）资源使用情况类
 * <p>
 * 用于记录一次 LLM 调用的详细用量信息，包括： - Token 消耗（输入/输出/总计） - 价格计算（单价、总价、货币） - 延迟（响应耗时）
 * <p>
 * 该类通常用于计费、成本分析、性能监控等场景。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LLMUsage implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 输入提示词（Prompt）的 token 数量 例如：用户输入 + 系统指令 + 历史上下文
   */
  private int prompt_tokens;

  /**
   * 输入提示词的单位价格（每 token 价格） 例如：0.0015 USD / 1K tokens → 0.0000015 USD per token
   */
  private BigDecimal prompt_unit_price = BigDecimal.ZERO;

  /**
   * 输入提示词的价格单位（通常为 1000 或 1） 表示 prompt_unit_price 是基于多少 token 计算的 例如：如果按 1K tokens 计费，则 priceUnit =
   * 1000
   */
  private BigDecimal prompt_price_unit = BigDecimal.ZERO;

  /**
   * 输入提示词的总费用 计算公式：(promptTokens / promptPriceUnit) * promptUnitPrice
   */
  private BigDecimal prompt_price = BigDecimal.ZERO;

  /**
   * 模型生成内容（Completion）的 token 数量 即 LLM 输出的 token 数
   */
  private int completion_tokens;

  /**
   * 生成内容的单位价格（每 token 价格） 通常高于 prompt_unit_price
   */
  private BigDecimal completion_unit_price = BigDecimal.ZERO;

  /**
   * 生成内容的价格单位（通常为 1000 或 1） 表示 completion_unit_price 是基于多少 token 计算的
   */
  private BigDecimal completion_price_unit = BigDecimal.ZERO;

  /**
   * 生成内容的总费用 计算公式：(completionTokens / completionPriceUnit) * completionUnitPrice
   */
  private BigDecimal completion_price = BigDecimal.ZERO;

  /**
   * 总共消耗的 token 数量 计算公式：promptTokens + completionTokens
   */
  private int total_tokens;

  /**
   * 总费用 计算公式：promptPrice + completionPrice
   */
  private BigDecimal total_price = BigDecimal.ZERO;

  /**
   * 费用货币类型 常见值：USD, CNY, JPY, EUR 等
   */
  private String currency;

  /**
   * 请求延迟（秒或毫秒） 表示从发送请求到收到完整响应的时间 单位建议为秒（float 类型可表示小数） 例如：1.234 表示 1.234 秒
   */
  private float latency;

  /**
   * 可选：模型名称（非原始字段，但常用于上下文） 例如：gpt-3.5-turbo, gpt-4, qwen-max 等
   */
  private String model_name;

  /**
   * 可选：供应商名称（如 openai, anthropic, alibaba）
   */
  private String provider;

  /**
   * 加法操作 - 将两个LLMUsage实例相加
   *
   * @param other 另一个LLMUsage实例
   * @return 新的LLMUsage实例，包含累加后的值
   */
  public LLMUsage plus(LLMUsage other) {
    if (other == null) {
      return this;
    }

    if (this.total_tokens == 0) {
      return other;
    } else {
      this.prompt_tokens += other.prompt_tokens;
      this.prompt_price = this.prompt_price.add(other.prompt_price);
      this.completion_tokens += other.completion_tokens;
      this.completion_price = this.completion_price.add(other.completion_price);
      this.total_tokens += other.total_tokens;
      this.total_price = this.total_price.add(other.total_price);
      this.latency += other.latency;

      this.prompt_unit_price = other.prompt_unit_price;
      this.prompt_price_unit = other.prompt_price_unit;
      this.completion_unit_price = other.completion_unit_price;
      this.currency = other.currency;
      return this;
    }
  }
}
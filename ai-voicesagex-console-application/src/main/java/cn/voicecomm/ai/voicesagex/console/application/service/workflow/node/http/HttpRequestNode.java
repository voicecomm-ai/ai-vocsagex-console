package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.HttpEnum;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author: gaox
 * @date: 2025/9/8 15:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequestNode extends BaseNode implements Serializable {

  /**
   * HTTP请求方法类型，如GET、POST等
   *
   * @see HttpEnum.HttpMethod
   */
  private String method;

  /**
   * 请求的URL地址
   */
  private String url;

  /**
   * 请求认证配置信息
   */
  private HttpRequestNodeAuthorization authorization;

  /**
   * HTTP请求头信息，通常为键值对格式的字符串
   */
  private String headers = "";

  /**
   * URL参数信息，通常为键值对格式的字符串
   */
  private String params = "";

  /**
   * HTTP请求体配置
   */
  private HttpRequestNodeBody body;

  @Data
  @Accessors(chain = true)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HttpRequestNodeAuthorizationConfig {

    /**
     * 认证类型，如basic、bearer、custom等
     *
     * @see HttpEnum.AuthorizationType
     */
    private String type;

    /**
     * API密钥值
     */
    private String apiKey;

    /**
     * 自定义认证头名称，默认为空字符串
     */
    private String header = "";
  }

  @Data
  public static class HttpRequestNodeAuthorization {

    /**
     * 授权类型，如no-auth、api-key等
     *
     * @see HttpEnum.AuthorizationType
     */
    private String type;

    /**
     * 授权配置详情
     */
    private HttpRequestNodeAuthorizationConfig config;

  }

  @Data
  @Accessors(chain = true)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HttpRequestNodeBody {

    /**
     * 请求体类型，如form-data、json、binary等
     *
     * @see HttpEnum.BodyType
     */
    private String type;

    /**
     * 请求体数据列表
     */
    private List<BodyData> data = new ArrayList<>();
  }

  @Data
  @Accessors(chain = true)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BodyData {

    /**
     * 数据项的键名，默认为空字符串
     */
    private String key = "";

    /**
     * 数据项的类型，如text、file等
     *
     * @see HttpEnum.BodyType
     */
    private String type;

    /**
     * 数据项的值，默认为空字符串
     */
    private String value = "";

    /**
     * 文件列表，用于文件类型的请求体数据
     */
    private List<String> file = new ArrayList<>();
  }
}

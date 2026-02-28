package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.HttpEnum.AuthType;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.HttpEnum.BodyType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayFileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.FileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http.HttpRequestNode.BodyData;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http.HttpRequestNode.HttpRequestNodeAuthorization;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http.HttpRequestNode.HttpRequestNodeBody;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Executor {

  /**
   * Maximum allowed size in bytes for binary data in HTTP requests. Default: 10 MB
   */
  public static final int HTTP_REQUEST_NODE_MAX_BINARY_SIZE = 10 * 1024 * 1024;

  /**
   * Maximum allowed size in bytes for text data in HTTP requests. Default: 1 MB
   */
  public static final int HTTP_REQUEST_NODE_MAX_TEXT_SIZE = 1024 * 1024;
  // 常量定义
  private static final Map<String, String> BODY_TYPE_TO_CONTENT_TYPE = new HashMap<>();
  private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom RANDOM = new SecureRandom();

  static {
    BODY_TYPE_TO_CONTENT_TYPE.put("json", "application/json");
    BODY_TYPE_TO_CONTENT_TYPE.put("x-www-form-urlencoded", "application/x-www-form-urlencoded");
    BODY_TYPE_TO_CONTENT_TYPE.put("form-data", "multipart/form-data");
    BODY_TYPE_TO_CONTENT_TYPE.put("raw-text", "text/plain");
  }

  // 字段定义
  private final String method;
  // Getter方法
  @Getter
  private String url;
  private List<Entry<String, String>> params;
  private Object content;
  private Map<String, String> data;
  private List<Entry<String, FileTuple>> files;
  private Object json;
  private Map<String, String> headers;
  private final HttpRequestNodeAuthorization auth;

  private final VariablePool variablePool;
  private final HttpRequestNode nodeData;

  private static final String CRLF = "\r\n"; // 默认 \r\n
  private static final String LF = "\n";   // Linux fallback

  // 切换使用 CRLF 或 LF
  private String newline = CRLF;

  // 构造函数
  public Executor(HttpRequestNode nodeData,
      VariablePool variablePool) {
    // 处理API密钥认证
    if ("api-key".equals(nodeData.getAuthorization().getType())) {
      if (nodeData.getAuthorization().getConfig() == null) {
        throw new RuntimeException("authorization config is required");
      }
      String apiKey = variablePool.convertTemplate(
          nodeData.getAuthorization().getConfig().getApiKey()).getText();
      nodeData.getAuthorization().getConfig().setApiKey(apiKey);
    }

    this.url = nodeData.getUrl();
    this.method = nodeData.getMethod();
    this.auth = nodeData.getAuthorization();
    this.params = new ArrayList<>();
    this.headers = new HashMap<>();
    this.content = null;
    this.files = null;
    this.data = null;
    this.json = null;

    this.variablePool = variablePool;
    this.nodeData = nodeData;

    // 初始化请求
    initialize();
  }

  // 初始化方法
  private void initialize() {
    initUrl();
    initParams();
    initHeaders();
    initBody();
  }

  // 初始化URL
  private void initUrl() throws RuntimeException {
    this.url = variablePool.convertTemplate(this.nodeData.getUrl()).getText();

    // 验证URL
    if (this.url == null || this.url.isEmpty()) {
      throw new RuntimeException("url is required");
    }
    if (!this.url.startsWith("http://") && !this.url.startsWith("https://")) {
      throw new RuntimeException("url should start with http:// or https://");
    }
  }

  // 初始化查询参数
  private void initParams() {
    List<Entry<String, String>> result = new ArrayList<>();
    String[] lines = this.nodeData.getParams().split("\n");

    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }

      String[] parts = line.split(":", 2);
      String key = parts[0].trim();
      if (key.isEmpty()) {
        continue;
      }

      String value = parts.length > 1 ? parts[1].trim() : "";
      String convertedKey = variablePool.convertTemplate(key).getText();
      String convertedValue = variablePool.convertTemplate(value).getText();
      result.add(new SimpleEntry<>(convertedKey, convertedValue));
    }

    this.params = result;
  }

  // 初始化请求头
  private void initHeaders() {
    String headersText = variablePool.convertTemplate(this.nodeData.getHeaders()).getText();
    Map<String, String> headersMap = new HashMap<>();

    String[] lines = headersText.split("\n");
    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }

      String[] parts = line.split(":", 2);
      String key = parts[0].trim();
      String value = parts.length > 1 ? parts[1].trim() : "";
      headersMap.put(key, value);
    }
    if (!headersMap.containsKey("User-Agent")) {
      headersMap.put("User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
    }

    this.headers = headersMap;
  }

  // 初始化请求体
  private void initBody() throws RuntimeException {
    HttpRequestNodeBody body = this.nodeData.getBody();
    if (body == null) {
      return;
    }

    List<BodyData> data = body.getData();
    switch (BodyType.getByValue(body.getType())) {
      case NONE:
        this.content = "";
        break;

      case RAW_TEXT:
        if (data.size() != 1) {
          throw new RuntimeException("raw-text body type should have exactly one item");
        }
        this.content = variablePool.convertTemplate(data.getFirst().getValue()).getText();
        break;

      case JSON:
        if (data.size() != 1) {
          throw new RuntimeException("json body type should have exactly one item");
        }
        String jsonString = variablePool.convertTemplate(data.getFirst().getValue()).getText();
        try {
          // 这里需要实现JSON修复逻辑，简化处理
          ObjectMapper mapper = new ObjectMapper();
          this.json = mapper.readTree(jsonString);
        } catch (Exception e) {
          throw new RuntimeException("Failed to parse JSON: " + jsonString, e);
        }
        break;

      case BINARY:
        if (data.size() != 1) {
          throw new RuntimeException("binary body type should have exactly one item");
        }
        List<String> fileSelector = data.getFirst().getFile();
        FileSegment fileVariable = (FileSegment) variablePool.get(fileSelector);
        if (fileVariable == null) {
          throw new RuntimeException("cannot fetch file with selector " + fileSelector);
        }
        File file = (File) fileVariable.getValue();
        this.content = FileUtil.readBytes(file.getStorage_key());
        break;

      case X_WWW_FORM_URLENCODED:
        Map<String, String> formData = new HashMap<>();
        for (BodyData item : data) {
          String key = variablePool.convertTemplate(item.getKey()).getText();
          String value = variablePool.convertTemplate(item.getValue()).getText();
          formData.put(key, value);
        }
        this.data = formData;
        break;

      case FORM_DATA:
        Map<String, String> textFormData = new HashMap<>();
        Map<String, List<String>> fileSelectors = new HashMap<>();

        // 处理文本字段
        for (BodyData item : data) {
          if ("text".equals(item.getType())) {
            String key = variablePool.convertTemplate(item.getKey()).getText();
            String value = variablePool.convertTemplate(item.getValue()).getText();
            textFormData.put(key, value);
          } else if ("file".equals(item.getType())) {
            String key = variablePool.convertTemplate(item.getKey()).getText();
            fileSelectors.put(key, item.getFile());
          }
        }

        // 处理文件字段
        Map<String, List<FileTuple>> filesMap = new HashMap<>();
        for (Entry<String, List<String>> entry : fileSelectors.entrySet()) {
          String key = entry.getKey();
          List<String> selector = entry.getValue();
          Segment segment = variablePool.get(selector);

          if (segment instanceof FileSegment fileSegment) {
            List<File> fileList = Collections.singletonList((File) fileSegment.getValue());
            processFiles(key, fileList, filesMap);
          } else if (segment instanceof ArrayFileSegment arrayFileSegment) {
            List<File> fileList = (List<File>) arrayFileSegment.getValue();
            processFiles(key, fileList, filesMap);
          }
        }

        // 转换文件为列表格式
        if (filesMap.isEmpty()) {
          this.files = new ArrayList<>();
          this.files.add(new SimpleEntry<>(
              "__multipart_placeholder__",
              new FileTuple("", new byte[0], "application/octet-stream")));
        } else {
          this.files = new ArrayList<>();
          for (Entry<String, List<FileTuple>> fileEntry : filesMap.entrySet()) {
            String key = fileEntry.getKey();
            for (FileTuple fileTuple : fileEntry.getValue()) {
              this.files.add(new SimpleEntry<>(key, fileTuple));
            }
          }
        }
        // 把 form-data 的文本字段转到 params 里
        if (this.params == null) {
          this.params = new ArrayList<>();
        }
        for (Entry<String, String> entry : textFormData.entrySet()) {
          this.params.add(new SimpleEntry<>(entry.getKey(), entry.getValue()));
        }
        break;
    }
  }

  // 处理文件
  private void processFiles(String key, List<File> fileList,
      Map<String, List<FileTuple>> filesMap) {
    for (File file : fileList) {
      if (file.getRelated_id() != null) {
        byte[] fileContent = FileUtil.readBytes(file.getStorage_key());
        FileTuple fileTuple = new FileTuple(
            file.getFilename(),
            fileContent,
            file.getMime_type() != null ? file.getMime_type() : "application/octet-stream"
        );

        filesMap.computeIfAbsent(key, k -> new ArrayList<>()).add(fileTuple);
      }
    }
  }

  // 组装请求头
  private Map<String, String> assemblingHeaders() throws RuntimeException {
    HttpRequestNodeAuthorization authorization = this.auth;
    Map<String, String> headersCopy = this.headers;

    if ("api-key".equals(this.auth.getType())) {
      if (this.auth.getConfig() == null) {
        throw new RuntimeException("authorization config is required");
      }

      if (this.auth.getConfig().getApiKey() == null) {
        throw new RuntimeException("api_key is required");
      }

      if (authorization.getConfig().getHeader() == null ||
          authorization.getConfig().getHeader().isEmpty()) {
        authorization.getConfig().setHeader("Authorization");
      }

      String headerName = authorization.getConfig().getHeader();
      switch (AuthType.getByValue(this.auth.getConfig().getType())) {
        case BEARER:
          headersCopy.put(headerName, "Bearer " + authorization.getConfig().getApiKey());
          break;
        case BASIC:
          String credentials = authorization.getConfig().getApiKey();
          String encodedCredentials;
          if (credentials.contains(":")) {
            encodedCredentials = Base64.getEncoder().encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8));
          } else {
            encodedCredentials = credentials;
          }
          headersCopy.put(headerName, "Basic " + encodedCredentials);
          break;
        case CUSTOM:
          headersCopy.put(headerName, authorization.getConfig().getApiKey() != null ?
              authorization.getConfig().getApiKey() : "");
          break;
      }
    }

    return headersCopy;
  }

  // 验证和解析响应
  private Response validateAndParseResponse(HttpResponse<byte[]> response) throws RuntimeException {
    Response executorResponse = new Response(response);

    long thresholdSize = executorResponse.isFile() ?
        HTTP_REQUEST_NODE_MAX_BINARY_SIZE :
        HTTP_REQUEST_NODE_MAX_TEXT_SIZE;

    if (executorResponse.getSize() > thresholdSize) {
      String type = executorResponse.isFile() ? "File" : "Text";
      double maxSizeMB = thresholdSize / (1024.0 * 1024.0);
      throw new RuntimeException(
          String.format("%s size is too large, max size is %.2f MB, but current size is %s.",
              type, maxSizeMB, executorResponse.getReadableSize()));
    }

    return executorResponse;
  }

  // 执行HTTP请求
  private HttpResponse<byte[]> doHttpRequest(Map<String, String> headers)
      throws RuntimeException, IOException, InterruptedException {
    Set<String> validMethods = new HashSet<>(Arrays.asList(
        "get", "head", "post", "put", "delete", "patch", "options",
        "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"
    ));

    if (!validMethods.contains(method)) {
      throw new RuntimeException("Invalid http method " + this.method);
    }

    // --- 构建 URL + params ---
    String fullUrl = url;
    if (params != null && !params.isEmpty()) {
      String queryString = params.stream()
          .map(p -> URLEncoder.encode(p.getKey(), StandardCharsets.UTF_8) + "=" +
              URLEncoder.encode(p.getValue(), StandardCharsets.UTF_8))
          .reduce((a, b) -> a + "&" + b)
          .orElse("");
      fullUrl += (url.contains("?") ? "&" : "?") + queryString;
    }
    // --- 3. 构建 HttpClient ---
    Builder clientBuilder = HttpClient.newBuilder();

    HttpClient client = clientBuilder.version(HttpClient.Version.HTTP_1_1).build();
    // --- 4. 构建 BodyPublisher ---
    BodyPublisher bodyPublisher;
    String contentType = null;
    if (files != null && !files.isEmpty()) {
      String boundary = "----Boundary" + UUID.randomUUID();
      bodyPublisher = buildMultipartBodyPublisher(boundary);
      contentType = "multipart/form-data; boundary=" + boundary;
    } else if (json != null) {
      bodyPublisher = BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8);
      contentType = "application/json";
    } else if (data != null && !data.isEmpty()) {
      String form = data.entrySet().stream()
          .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
              + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
          .reduce((a, b) -> a + "&" + b)
          .orElse("");
      bodyPublisher = BodyPublishers.ofString(form, StandardCharsets.UTF_8);
      contentType = "application/x-www-form-urlencoded";
    } else if (content != null) {
      if (content instanceof byte[] bytes) {
        bodyPublisher = BodyPublishers.ofByteArray(bytes);
        contentType = "application/octet-stream"; // 或接口要求的二进制类型
      } else {
        bodyPublisher = BodyPublishers.ofString(content.toString(), StandardCharsets.UTF_8);
        contentType = "text/plain";
      }
    } else {
      bodyPublisher = BodyPublishers.noBody();
    }
    // --- 5. 构建 HttpRequest ---
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(URI.create(fullUrl));

    // 设置 Headers
    if (MapUtil.isNotEmpty(headers)) {
      headers.forEach(requestBuilder::header);
    }
    if (StrUtil.isNotBlank(contentType)) {
      requestBuilder.setHeader("Content-Type", contentType);
    }
    // 设置 Method
    switch (method) {
      case "get" -> requestBuilder.GET();
      case "post" -> requestBuilder.POST(bodyPublisher);
      case "put" -> requestBuilder.PUT(bodyPublisher);
      case "delete" -> requestBuilder.DELETE();
      default -> requestBuilder.method(method.toUpperCase(), bodyPublisher);
    }

    HttpRequest request = requestBuilder.build();
    log.info("开始请求api：{}", url);
    // 打印 Headers
    request.headers().map().forEach((k, v) -> log.info("Header: {}, {}", k, v));

    // 如果是 form-data，自行打印 boundary 和文件内容大小
    if (files != null && !files.isEmpty()) {
      for (Entry<String, FileTuple> entry : files) {
        log.info("FormData File field: {}, filename: {}, size: {} bytes, mime: {}",
            entry.getKey(),
            entry.getValue().getFilename(),
            entry.getValue().getContent().length,
            entry.getValue().getMimeType());
      }
    }

    // 如果是 JSON
    if (json != null) {
      log.info("Request JSON Body: {}", json.toString());
    }

    // 如果是 x-www-form-urlencoded
    if (data != null && !data.isEmpty()) {
      log.info("Request Form Body: {}", data.toString());
    }

    // 如果是 text/plain
    if (content != null) {
      log.info("Request Raw Body: {}", content.toString());
    }
    HttpResponse<byte[]> send = client.send(request, BodyHandlers.ofByteArray());
    log.info("请求结果：{}", JSONUtil.toJsonStr(send));
    return send;
  }

  private BodyPublisher buildMultipartBodyPublisher(String boundary)
      throws IOException {
    var byteArrayOutputStream = new ByteArrayOutputStream();

    for (Entry<String, FileTuple> entry : files) {
      String name = entry.getKey();
      FileTuple file = entry.getValue();
      log.info("准备写入文件字段: {}, 文件名: {}, mimeType: {}, contentLength: {}",
          name, file.filename, file.getMimeType(),
          file.getContent() == null ? "null" : file.getContent().length);
      byteArrayOutputStream.write(("--" + boundary + newline).getBytes(StandardCharsets.UTF_8));
      byteArrayOutputStream.write(
          ("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getFilename()
              + "\"" + newline).getBytes(StandardCharsets.UTF_8));
      byteArrayOutputStream.write(
          ("Content-Type: " + file.getMimeType() + newline + newline).getBytes(
              StandardCharsets.UTF_8));
      byteArrayOutputStream.write(file.getContent());
      byteArrayOutputStream.write(newline.getBytes(StandardCharsets.UTF_8));
    }
    byteArrayOutputStream.write(
        ("--" + boundary + "--" + newline).getBytes(StandardCharsets.UTF_8));
    log.info("multipart 总长度: {}", byteArrayOutputStream.size());
    return BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray());
  }

  // 主执行方法
  public Response invoke() throws RuntimeException, IOException, InterruptedException {
    // 组装请求头
    Map<String, String> headers = assemblingHeaders();
    // 执行HTTP请求
    HttpResponse<byte[]> httpResponse = doHttpRequest(headers);
    // 验证和解析响应
    return validateAndParseResponse(httpResponse);
  }


  // 内部类：文件元组
  @Setter
  @Data
  public static class FileTuple {

    // Getter和Setter方法
    private String filename;
    private byte[] content;
    private String mimeType;

    public FileTuple(String filename, byte[] content, String mimeType) {
      this.filename = filename;
      this.content = content;
      this.mimeType = mimeType;
    }

  }

  public String toLog() {
    try {
      URI uri = URI.create(url);
      String path = uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "/";

      // 处理 query 参数
      if (params != null && !params.isEmpty()) {
        String queryString = params.stream()
            .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
        path += "?" + queryString;
      } else if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
        path += "?" + uri.getQuery();
      }

      StringBuilder raw = new StringBuilder();
      raw.append(method).append(" ").append(path).append(" HTTP/1.1").append(newline);
      raw.append("Host: ").append(uri.getHost()).append(newline);

      Map<String, String> finalHeaders = assemblingHeaders();
      String boundary = "----WebKitFormBoundary" + generateRandomString(16);

      // 判断是否需要设置 Content-Type
      if (nodeData != null && nodeData.getBody() != null) {
        if (!finalHeaders.keySet().stream().map(String::toLowerCase).collect(Collectors.toSet())
            .contains("content-type")
            && BODY_TYPE_TO_CONTENT_TYPE.containsKey(nodeData.getBody().getType())) {
          finalHeaders.put("Content-Type",
              BODY_TYPE_TO_CONTENT_TYPE.get(nodeData.getBody().getType()));
        }
        if ("form-data".equals(nodeData.getBody().getType())) {
          finalHeaders.put("Content-Type", "multipart/form-data; boundary=" + boundary);
        }
      }

      // 处理 Headers，敏感信息脱敏
      for (Entry<String, String> entry : finalHeaders.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        if ("api-key".equalsIgnoreCase(auth != null ? auth.getType() : "")
            && auth.getConfig() != null) {
          String authHeader =
              auth.getConfig().getHeader() != null ? auth.getConfig().getHeader() : "Authorization";
          if (key.equalsIgnoreCase(authHeader)) {
            value = "*".repeat(value.length());
          }
        }
        raw.append(key).append(": ").append(value).append(newline);
      }

      // --- 构建 body string ---
      StringBuilder bodyString = new StringBuilder();

      if (files != null && !files.isEmpty() && files.stream()
          .anyMatch(f -> !"__multipart_placeholder__".equals(f.getValue().getFilename()))) {
        for (Entry<String, FileTuple> entry : files) {
          String key = entry.getKey();
          FileTuple file = entry.getValue();

          bodyString.append("--").append(boundary).append(newline);
          bodyString.append("Content-Disposition: form-data; name=\"").append(key)
              .append("\"; filename=\"").append(file.getFilename()).append("\"").append(newline);
          bodyString.append("Content-Type: ").append(file.getMimeType()).append(newline)
              .append(newline);

          try {
            bodyString.append(new String(file.getContent(), StandardCharsets.UTF_8));
          } catch (Exception ignored) {
            continue;
          }
          bodyString.append(newline);
        }
        bodyString.append("--").append(boundary).append("--").append(newline);
      } else if (nodeData != null && nodeData.getBody() != null) {
        switch (BodyType.getByValue(nodeData.getBody().getType())) {
          case X_WWW_FORM_URLENCODED:
            if (data != null) {
              String form = data.entrySet().stream()
                  .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                      + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                  .collect(Collectors.joining("&"));
              bodyString.append(form);
            }
            break;
          case FORM_DATA:
            if (data != null) {
              for (Entry<String, String> entry : data.entrySet()) {
                bodyString.append("--").append(boundary).append(newline);
                bodyString.append("Content-Disposition: form-data; name=\"").append(entry.getKey())
                    .append("\"").append(newline).append(newline);
                bodyString.append(entry.getValue()).append(newline);
              }
              bodyString.append("--").append(boundary).append("--").append(newline);
            }
            break;
          case RAW_TEXT:
            if (nodeData.getBody().getData().size() != 1) {
              throw new RuntimeException("raw-text body type should have exactly one item");
            }
            bodyString.append(nodeData.getBody().getData().getFirst().getValue());
            break;
          default:
            if (json != null) {
              bodyString.append(json);
            } else if (content != null) {
              bodyString.append(content);
            }
            break;
        }
      }

      if (!bodyString.isEmpty()) {
        raw.append("Content-Length: ")
            .append(bodyString.toString().getBytes(StandardCharsets.UTF_8).length)
            .append(newline);
      }

      raw.append(newline);
      raw.append(bodyString);

      return raw.toString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to build log string", e);
    }
  }

  public static String generateRandomString(int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      int index = RANDOM.nextInt(LETTERS.length());
      sb.append(LETTERS.charAt(index));
    }
    return sb.toString();
  }

}

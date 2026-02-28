package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpApplicationAddReq;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 测试接口
 *
 * @author: gaox
 * @date: 2025/9/24 17:24
 */
@RestController
@RequestMapping("/test")
@Validated
@Slf4j
@RequiredArgsConstructor
public class HttpTestController {

  private final Random random = new Random();

  @PutMapping("/put")
  public Result<String> put(@RequestBody @Validated McpApplicationAddReq req) {
    return Result.success("请求成功：" + JSONUtil.toJsonStr(req));
  }

  /**
   * 1. raw (JSON)
   */
  @PostMapping(value = "/raw", consumes = "application/json")
  public Map<String, Object> testRaw(@RequestBody Map<String, Object> body) {
    Map<String, Object> result = new HashMap<>();
    result.put("input", body);
    result.put("random", random.nextInt(1000));
    return result;
  }

  /**
   * 2. binary
   */
  @PostMapping(value = "/binary", consumes = "application/octet-stream")
  public Map<String, Object> testBinary(@RequestBody byte[] fileBytes) {
    Map<String, Object> result = new HashMap<>();
    result.put("length", fileBytes.length);
    result.put("random", random.nextInt(1000));
    return result;
  }

  /**
   * 3. form-data
   */
  @PostMapping(value = "/formdata", consumes = "multipart/form-data")
  public Map<String, Object> testFormData(@RequestParam("username") String username,
      @RequestParam(value = "file", required = false) MultipartFile file) {
    Map<String, Object> result = new HashMap<>();
    result.put("username", username);
    if (file != null) {
      result.put("fileName", file.getOriginalFilename());
      result.put("fileSize", file.getSize());
    }
    result.put("random", random.nextInt(1000));
    return result;
  }

  /**
   * 4. x-www-form-urlencoded
   */
  @PostMapping(value = "/urlencoded", consumes = "application/x-www-form-urlencoded")
  public Map<String, Object> testUrlencoded(@RequestParam Map<String, String> params) {
    Map<String, Object> result = new HashMap<>();
    result.put("params", params);
    result.put("random", random.nextInt(1000));
    return result;
  }
}

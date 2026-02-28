package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.controller;


import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util.VerifyCodeUtil;
import cn.voicecomm.ai.voicesagex.console.util.enums.ResultCodeEnum;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;

/**
 * 认证控制层
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RefreshScope
public class AuthController {

  @Resource(name = "captchaProducer")
  private Producer captchaProducer;

  @Resource(name = "captchaProducerMath")
  private Producer captchaProducerMath;

  /**
   * 获取图形验证码
   */
  @GetMapping(value = "/getImageCaptcha")
  public Result<String> getImageCaptcha(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String type = request.getParameter("type");
    String capStr;
    String code;
    BufferedImage bi;
    if ("math".equals(type)) {
      String capText = captchaProducerMath.createText();
      capStr = capText.substring(0, capText.lastIndexOf("@"));
      code = capText.substring(capText.lastIndexOf("@") + 1);
      bi = captchaProducerMath.createImage(capStr);
    } else {
      capStr = code = captchaProducer.createText();
      bi = captchaProducer.createImage(capStr);
    }
    session.setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
    String imageBase64 = VerifyCodeUtil.getBase64(bi);
    if (imageBase64 == null || imageBase64.isEmpty()) {
      return Result.of(ResultCodeEnum.IMAGECHECKCODE_FAILURE, "获取验证码图片失败");
    }
    return Result.of(ResultCodeEnum.IMAGECHECKCODE_SUCCESS, null, imageBase64);
  }
}

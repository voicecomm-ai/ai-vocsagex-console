package cn.voicecomm.ai.voicesagex.console.util.config;

import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author jiwh
 * @date 2024/6/4 14:29
 */
@Slf4j
@RestControllerAdvice
@AutoConfiguration
public class ControllerAdviceAutoConfiguration {

  private static final String DEFAULT_ERROR_MSG_TEMPLATE = "数据校验出现异常：{}，异常类型：{}";

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public Result<String> handleValidException(MethodArgumentNotValidException e) {
    logError(e);
    BindingResult bindingResult = e.getBindingResult();
    StringBuilder errorMessage = new StringBuilder();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errorMessage.append(fieldError.getDefaultMessage());
      break;
    }
    return Result.error(errorMessage.toString());
  }

  @ExceptionHandler(value = ConstraintViolationException.class)
  public Result<String> handleValidationException(ConstraintViolationException e) {
    StringBuilder errorMessage = new StringBuilder();
    for (ConstraintViolation<?> s : e.getConstraintViolations()) {
      errorMessage.append(s.getMessage());
      break;
    }
    return Result.error(errorMessage.toString());
  }

  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  public Result<String> handleValidationException(HttpMessageNotReadableException e) {
    logError(e);
    return Result.error("请求错误");
  }

  @ExceptionHandler(value = TypeMismatchException.class)
  public Result<String> handleValidationException(TypeMismatchException e) {
    logError(e);
    return Result.error("请求参数类型错误：" + e.getPropertyName());
  }

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  public Result<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    logError(e);
    return Result.error("文件过大");
  }

  private void logError(Exception e) {
    log.error(DEFAULT_ERROR_MSG_TEMPLATE, e.getMessage(), e.getClass());
  }

}

package cn.voicecomm.ai.voicesagex.console.api.constant.graph;


import java.io.Serializable;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 错误码对象
 *
 * 全局错误码，占用 [0, 999],
 *
 * TODO 错误码设计成对象的原因，为未来的 i18 国际化做准备
 */
@Data
public class ErrorCode implements Serializable {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String messageKey; // 这里改成 messageKey，而不是直接的错误信息

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.messageKey = message;
    }


    public String getMessage() {
        MessageSource messageSource = SpringContUtil.getBean(MessageSource.class);
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }


    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", message='" + getMessage() + '\'' +
                '}';
    }

}

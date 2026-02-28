package cn.voicecomm.ai.voicesagex.console.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否存在特殊字符
 *
 * @author ryc
 * @date 2023/5/15
 */
@Documented
@Constraint(
    validatedBy = {ChineseOperatorCharConstraintValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChineseOperatorChar {

  String message() default "仅限汉字和英文 | ";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

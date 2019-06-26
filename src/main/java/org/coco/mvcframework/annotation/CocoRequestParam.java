package org.coco.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * Created by KEKE on 2019/6/25
 */
@Target(ElementType.PARAMETER) // 用在方法内部的参数上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CocoRequestParam {
    String value() default "";
    boolean required() default true;
}

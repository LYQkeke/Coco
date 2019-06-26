package org.coco.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * Created by KEKE on 2019/6/25
 */
@Target(ElementType.FIELD) // 用在成员变量上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CocoAutowired {
    // 可以跟参数 RequestMapping("/hello")
    String value() default "";
}

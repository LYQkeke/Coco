package org.coco.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * Created by KEKE on 2019/6/25
 */
@Target({ElementType.TYPE,ElementType.METHOD}) // 可以使用在类上面，也可以使用在方法上面
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CocoRequestMapping {
    String value() default "";
}

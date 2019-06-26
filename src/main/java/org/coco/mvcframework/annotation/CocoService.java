package org.coco.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * Created by KEKE on 2019/6/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CocoService {
    String value() default "";
}

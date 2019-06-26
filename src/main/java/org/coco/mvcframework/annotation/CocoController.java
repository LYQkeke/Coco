package org.coco.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * Created by KEKE on 2019/6/25
 */
@Target(ElementType.TYPE) // 用在控制类上面
@Retention(RetentionPolicy.RUNTIME) // 在运行时起作用
@Documented // 可以在java doc 中记录起来
public @interface CocoController {
    String value() default ""; // 可以跟参数
}

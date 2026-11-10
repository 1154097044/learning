package com.xy.learning.week1.miniioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模仿 Spring 的 @Autowired —— 标记"这个构造器参数需要容器注入"。
 * 为简化，这里标记在构造器上，容器会选择被标记的构造器做依赖注入。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface MiniInject {
}

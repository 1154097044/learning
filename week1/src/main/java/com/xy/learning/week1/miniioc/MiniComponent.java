package com.xy.learning.week1.miniioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手写迷你 IoC 容器用到的注解。
 * 模仿 Spring 的 @Component —— 标记"这个类交给容器管理"。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MiniComponent {
}

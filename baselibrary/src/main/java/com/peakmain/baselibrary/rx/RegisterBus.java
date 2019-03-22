package com.peakmain.baselibrary.rx;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/18
 * 描述：观察则注解类的作用域
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterBus {
}

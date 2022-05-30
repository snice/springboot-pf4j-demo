package com.itfenbao.main;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Test {

    /**
     * 切面
     */
    @Pointcut(value = "execution(public * com.itfenbao.main.web.*.*(..))")
    public void log() {

    }

    @Before("log()")
    public void b() {
        System.out.println("before=======");
    }
}

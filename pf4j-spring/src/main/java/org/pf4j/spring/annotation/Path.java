package org.pf4j.spring.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Path {
    String[] value() default {"/**"};

    String[] exclude() default {};
}

package com.example.homework_1.runner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    String description() default "";
    long timeout() default 0L;

    Class<? extends Throwable> expected() default Test.None.class;

    class None extends Throwable {
        private None() {}
    }

}
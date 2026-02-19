package inf222.aop.account.annotation;

import org.slf4j.event.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transfer {

    Level value() default Level.INFO;

    double LogTransferAbove() default Double.MAX_VALUE;

    boolean internationalTransfer() default false;

    boolean logErrors() default false;

}

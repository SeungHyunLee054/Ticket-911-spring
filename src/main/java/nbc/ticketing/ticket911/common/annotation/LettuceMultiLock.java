package nbc.ticketing.ticket911.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LettuceMultiLock {
	String key();

	String group() default "";

	long waitTime() default 10L;

	long leaseTime() default 10L;

	TimeUnit timeUnit() default TimeUnit.SECONDS;
}


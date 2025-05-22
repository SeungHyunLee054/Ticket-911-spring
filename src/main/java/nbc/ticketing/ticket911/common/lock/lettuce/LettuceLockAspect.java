package nbc.ticketing.ticket911.common.lock.lettuce;

import java.lang.reflect.Method;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.common.lock.RedissonLock;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;

@Aspect
@Component
@RequiredArgsConstructor
public class LettuceLockAspect {
	private final LettuceLockManager lockManager;

	@Around("@annotation(redissonLock)")
	public Object lock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
		String key = resolveKey(joinPoint, redissonLock);
		String lockKey = "lock:" + (redissonLock.group().isEmpty() ? "" : redissonLock.group() + ":") + key;
		String lockValue = UUID.randomUUID().toString();

		boolean locked = lockManager.tryLock(lockKey, lockValue, redissonLock.leaseTime() * 1000);
		if (!locked) {
			throw new BookingException(BookingExceptionCode.LOCK_ACQUIRE_FAIL);
		}

		try {
			return joinPoint.proceed();
		} finally {
			lockManager.unlock(lockKey, lockValue);
		}
	}

	private String resolveKey(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String[] parameterNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();

		StandardEvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		ExpressionParser parser = new SpelExpressionParser();
		return parser.parseExpression(redissonLock.key()).getValue(context, String.class);
	}
}

package nbc.ticketing.ticket911.common.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.common.annotation.RedissonMultiLock;
import nbc.ticketing.ticket911.domain.lock.LockRedisService;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.LockRedisException;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.code.LockRedisExceptionCode;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedissonMultiLockAspect {

	private final LockRedisService lockRedisService;
	private final ExpressionParser parser = new SpelExpressionParser();

	@Around("@annotation(nbc.ticketing.ticket911.common.annotation.RedissonMultiLock)")
	public Object lock(ProceedingJoinPoint joinPoint) {
		Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
		RedissonMultiLock annotation = method.getAnnotation(RedissonMultiLock.class);

		List<String> lockKeys;
		List<String> dynamicKeys = parseKeyList(annotation.key(), joinPoint);
		lockKeys = dynamicKeys.stream()
			.map(key -> buildLockKey(annotation.group(), key))
			.toList();
		return lockRedisService.executeWithMultiLock(
			lockKeys,
			annotation.waitTime(),
			annotation.leaseTime(),
			annotation.timeUnit(),
			joinPoint::proceed
		);
	}

	private List<String> parseKeyList(String keyExpression, ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		String[] paramNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();

		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < Objects.requireNonNull(paramNames).length; i++) {
			context.setVariable(paramNames[i], args[i]);
		}

		Object result = parser.parseExpression(keyExpression).getValue(context);
		if (result instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		throw new LockRedisException(LockRedisExceptionCode.LOCK_KEY_EVALUATION_FAIL);
	}

	private String buildLockKey(String group, String key) {
		return String.format("lock:%s:%s", group, key);
	}
}

package nbc.ticketing.ticket911.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomTransactionManager {

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object getProceedingJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		return joinPoint.proceed();
	}
}

package nbc.ticketing.ticket911.domain.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import nbc.ticketing.ticket911.common.funtional.ThrowingSupplier;

public interface LockRedisService {
	<T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, ThrowingSupplier<T> action);

	<T> T executeWithMultiLock(List<String> key, long waitTime, long leaseTime, TimeUnit timeUnit,
		ThrowingSupplier<T> action);
}

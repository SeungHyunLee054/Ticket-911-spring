package nbc.ticketing.ticket911.domain.lock;

import java.util.List;

import nbc.ticketing.ticket911.common.funtional.ThrowingSupplier;

public interface LockRedisService {
	<T> T executeWithLock(String key, long waitTime, long leaseTime, ThrowingSupplier<T> action);

	<T> T executeWithMultiLock(List<String> key, long waitTime, long leaseTime, ThrowingSupplier<T> action);
}

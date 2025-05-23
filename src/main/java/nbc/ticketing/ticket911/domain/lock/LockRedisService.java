package nbc.ticketing.ticket911.domain.lock;

import java.util.List;
import java.util.concurrent.Callable;

public interface LockRedisService {
	<T> T executeWithLock(String key, long waitTime, long leaseTime, Callable<T> action);

	<T> T executeWithMultiLock(List<String> key, long waitTime, long leaseTime, Callable<T> action);
}

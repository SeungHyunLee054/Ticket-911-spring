package nbc.ticketing.ticket911.domain.lock;

import java.util.List;
import java.util.function.Supplier;

public interface LockRedisService {
	<T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> action);
	void executeWithMultiLock(List<String> key, long waitTime, long leaseTime);
}

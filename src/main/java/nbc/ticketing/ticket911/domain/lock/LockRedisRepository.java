package nbc.ticketing.ticket911.domain.lock;

import java.util.concurrent.TimeUnit;

public interface LockRedisRepository {
	boolean lock(String key, long waitTime, long leaseTime, TimeUnit timeUnit);

	void unlock(String key);
}

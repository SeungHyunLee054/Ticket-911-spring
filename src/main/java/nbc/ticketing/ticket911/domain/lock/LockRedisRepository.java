package nbc.ticketing.ticket911.domain.lock;

public interface LockRedisRepository {
	boolean lock(String key, long waitTime, long leaseTime);
	void unlock(String key);
}

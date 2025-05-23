package nbc.ticketing.ticket911.infrastructure.redisson;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nbc.ticketing.ticket911.domain.lock.LockRedisRepository;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.LockRedisException;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.code.LockRedisExceptionCode;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedissonLockRedisRepository implements LockRedisRepository {

	private final RedissonClient redissonClient;

	@Override
	public boolean lock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
		RLock lock = redissonClient.getLock(key);
		try {
			return lock.tryLock(waitTime, leaseTime, timeUnit);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new LockRedisException(LockRedisExceptionCode.LOCK_INTERRUPTED);
		} catch (Exception e) {
			log.error("락 획득 중 시스템 예외 발생: {}", e.getMessage(), e);
			throw new LockRedisException(LockRedisExceptionCode.LOCK_ACQUIRE_FAIL);
		}
	}

	@Override
	public void unlock(String key) {
		RLock lock = redissonClient.getLock(key);
		if (lock.isHeldByCurrentThread()) {
			lock.unlock();
		} else {
			log.warn("락을 해제할 수 없습니다. 락이 예상보다 빨리 풀렸거나, 소유 상태가 손실됨: key = {}", key);
		}
	}
}

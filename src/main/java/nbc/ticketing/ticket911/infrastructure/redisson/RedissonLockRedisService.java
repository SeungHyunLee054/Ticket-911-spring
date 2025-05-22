package nbc.ticketing.ticket911.infrastructure.redisson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.lock.LockRedisRepository;
import nbc.ticketing.ticket911.domain.lock.LockRedisService;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.LockRedisException;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.code.LockRedisExceptionCode;

@Service
@RequiredArgsConstructor
public class RedissonLockRedisService implements LockRedisService {

	private final LockRedisRepository lockRedisRepository;

	@Override
	public <T> T executeWithLock(String key, long waitTime, long leaseTime, Callable<T> action) {
		boolean locked = lockRedisRepository.lock(key, waitTime, leaseTime);
		if (!locked) {
			throw new LockRedisException(LockRedisExceptionCode.LOCK_TIMEOUT);
		}
		try {
			return action.call();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new LockRedisException(LockRedisExceptionCode.LOCK_PROCEED_FAIL);
		} finally {
			lockRedisRepository.unlock(key);
		}
	}

	@Override
	public <T> T executeWithMultiLock(List<String> keys, long waitTime, long leaseTime, Callable<T> action) {
		List<String> lockedKeys = new ArrayList<>();

		try {
			for (String key : keys) {
				boolean locked = lockRedisRepository.lock(key, waitTime, leaseTime);
				if (!locked) {
					throw new LockRedisException(LockRedisExceptionCode.LOCK_TIMEOUT);
				}
				lockedKeys.add(key);
			}
			return action.call();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new LockRedisException(LockRedisExceptionCode.LOCK_PROCEED_FAIL);
		} finally {
			for (String key : lockedKeys) {
				lockRedisRepository.unlock(key);
			}
		}
	}
}

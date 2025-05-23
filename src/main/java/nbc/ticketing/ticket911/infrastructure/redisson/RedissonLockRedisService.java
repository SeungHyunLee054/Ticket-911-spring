package nbc.ticketing.ticket911.infrastructure.redisson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.common.funtional.ThrowingSupplier;
import nbc.ticketing.ticket911.domain.lock.LockRedisRepository;
import nbc.ticketing.ticket911.domain.lock.LockRedisService;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.LockRedisException;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.code.LockRedisExceptionCode;

@Service
@RequiredArgsConstructor
public class RedissonLockRedisService implements LockRedisService {

	private final LockRedisRepository lockRedisRepository;

	@Override
	public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit,
		ThrowingSupplier<T> action) {
		boolean locked = lockRedisRepository.lock(key, waitTime, leaseTime, timeUnit);
		if (!locked) {
			throw new LockRedisException(LockRedisExceptionCode.LOCK_TIMEOUT);
		}
		try {
			return action.get();
		} catch (LockRedisException lre) {
			throw lre;
		} catch (Throwable t) {
			if (t instanceof RuntimeException re) {
				throw re;
			}
			if (t instanceof Error err) {
				throw err;
			}
			throw new LockRedisException(LockRedisExceptionCode.LOCK_PROCEED_FAIL);
		} finally {
			lockRedisRepository.unlock(key);
		}
	}

	@Override
	public <T> T executeWithMultiLock(List<String> keys, long waitTime, long leaseTime, TimeUnit timeUnit,
		ThrowingSupplier<T> action)
		throws LockRedisException {
		List<String> lockedKeys = new ArrayList<>();

		try {
			for (String key : keys) {
				boolean locked = lockRedisRepository.lock(key, waitTime, leaseTime, timeUnit);
				if (!locked) {
					throw new LockRedisException(LockRedisExceptionCode.LOCK_TIMEOUT);
				}
				lockedKeys.add(key);
			}
			return action.get();
		} catch (LockRedisException lre) {
			throw lre;
		} catch (Throwable t) {
			if (t instanceof RuntimeException re) {
				throw re;
			}
			if (t instanceof Error err) {
				throw err;
			}
			throw new LockRedisException(LockRedisExceptionCode.LOCK_PROCEED_FAIL);
		} finally {
			for (String key : lockedKeys) {
				lockRedisRepository.unlock(key);
			}
		}
	}
}

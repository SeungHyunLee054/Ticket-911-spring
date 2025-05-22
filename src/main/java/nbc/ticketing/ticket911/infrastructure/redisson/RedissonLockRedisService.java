package nbc.ticketing.ticket911.infrastructure.redisson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

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
	public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> action) {
		boolean locked = lockRedisRepository.lock(key, waitTime, leaseTime);
		if (!locked) {
			throw new LockRedisException(LockRedisExceptionCode.LOCK_TIMEOUT);
		}

		try {
			return action.get();
		} finally {
			lockRedisRepository.unlock(key);
		}
	}

	@Override
	public void executeWithMultiLock(List<String> keys, long waitTime, long leaseTime) {
		List<String> sortedKeys = new ArrayList<>(keys);
		Collections.sort(keys);
		List<String> lockedKeys = new ArrayList<>();

		try {
			for (String key : sortedKeys) {
				boolean locked = lockRedisRepository.lock(key, waitTime, leaseTime);
				if (!locked) {
					throw new LockRedisException(LockRedisExceptionCode.LOCK_TIMEOUT);
				}
				lockedKeys.add(key);
			}
		} finally {
			for (String key : lockedKeys) {
				lockRedisRepository.unlock(key);
			}
		}
	}
}

package nbc.ticketing.ticket911.common.lock.lettuce;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class LettuceDistributedLockService implements DistributedLockService {

	private final LettuceLockManager lockManager;

	@Override
	public <T> T runWithLock(List<String> lockKeys, long expireMillis, Supplier<T> task) {
		LettuceMultiLock multiLock = new LettuceMultiLock(lockKeys, expireMillis, lockManager);

		if (!multiLock.tryLockAll()) {
			throw new BookingException(BookingExceptionCode.LOCK_ACQUIRE_FAIL);
		}

		try {
			return task.get();
		} finally {
			multiLock.unlockAll();
		}
	}
}

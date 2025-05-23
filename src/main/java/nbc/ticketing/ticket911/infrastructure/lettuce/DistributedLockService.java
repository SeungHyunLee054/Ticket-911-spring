package nbc.ticketing.ticket911.infrastructure.lettuce;

import java.util.List;
import java.util.function.Supplier;

public interface DistributedLockService {
	<T> T runWithLock(List<String> lockKeys, long expireMillis, Supplier<T> task);

}

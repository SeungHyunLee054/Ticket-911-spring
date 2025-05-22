package nbc.ticketing.ticket911.common;

@FunctionalInterface
public interface CheckedSupplier<T> {
	T get() throws Throwable;
}

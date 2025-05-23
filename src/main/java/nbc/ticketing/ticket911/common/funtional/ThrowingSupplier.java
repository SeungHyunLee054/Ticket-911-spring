package nbc.ticketing.ticket911.common.funtional;

@FunctionalInterface
public interface ThrowingSupplier<T> {
	T get() throws Throwable;
}

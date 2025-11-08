package xland.s7c7icu.client.internal;

public final class SneakyThrow {
    private SneakyThrow() {}

    public static Error sneakyThrow(Throwable t) {
        throw sneakyThrow0(t);
    }

    @SuppressWarnings("unchecked")
    private static <X extends Throwable> Error sneakyThrow0(Throwable t) throws X {
        throw (X) t;
    }
}

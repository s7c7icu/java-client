package xland.s7c7icu.client;

import java.util.function.Predicate;

public interface SinceSchema {
    int availableSinceSchema();

    static <T extends SinceSchema> Predicate<T> filtersSupported(final int schema) {
        return t -> schema >= t.availableSinceSchema();
    }
}

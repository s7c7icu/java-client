package xland.s7c7icu.client;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Provider<T extends Identifiable> {
    Collection<? extends T> values();

    static <T extends Identifiable> Map<String, ? extends T> getValues(Class<? extends Provider<T>> type) {
        Map<String, T> m = ServiceLoader.load(type).stream()
                .flatMap(p -> p.get().values().stream())
                .collect(Collectors.toMap(Identifiable::id, Function.identity()));

        for (T value : new ArrayList<>(m.values())) {   // make a copy
            if (value instanceof Aliased aliased) {
                for (String alias : aliased.aliases()) {
                    // Never conflict primary names
                    m.putIfAbsent(alias, value);
                }
            }
        }

        return m;
    }
}

package xland.s7c7icu.client.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface Salter {
    static Salter empty() {
        return xland.s7c7icu.client.impl.salter.EmptySalter.INSTANCE;
    }

    SalterType<?> type();

    // outside the Hashing wrapper
    @Deprecated
    default HasherInputStream wrap(HasherInputStream original) {
        throw new UnsupportedOperationException();
    }

    HasherOutputStream wrapOutput(HasherOutputStream original);

    // Guarantees `!hashes.isEmpty()`
    HasherInputStream calculateInput(InputStream input, Map<Hashing, String> hashes) throws IOException;
}

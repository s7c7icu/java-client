package xland.s7c7icu.client.impl.salter;

import xland.s7c7icu.client.api.*;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.api.json.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class EmptySalter implements Salter {
    public static final EmptySalter INSTANCE = new EmptySalter();

    private EmptySalter() {}

    @Override
    public SalterType<?> type() {
        return TYPE;
    }

    @Override
    public HasherOutputStream wrapOutput(HasherOutputStream original) {
        return original;
    }

    @Override
    public HasherInputStream calculateInput(InputStream originalInput, Map<Hashing, String> hashes) throws IOException {
        if (hashes.isEmpty()) throw new IllegalArgumentException("Empty hash table");
        InputStream res = originalInput;

        var entries = hashes.entrySet().iterator();
        do {
            final Map.Entry<Hashing, String> entry = entries.next();
            res = entry.getKey().wrapInput(res);
            // TODO: entry.getValue()??? How to VERIFY while WRAPPING streams together? Or can we simply give up flowing
        } while (entries.hasNext());
        return (HasherInputStream) res;
    }

    @Override
    public int hashCode() {
        return -1359954973;
    }

    @Override
    public String toString() {
        return "EmptySalter";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmptySalter;
    }

    private static final SalterType<EmptySalter> TYPE = new SalterType<>() {
        @Override
        public EmptySalter deserialize(JsonObject obj) {
            return EmptySalter.INSTANCE;
        }

        @Override
        public void serialize(EmptySalter salter, JsonWriter writer) {
        }

        @Override
        public String id() {
            return "none";
        }

        @Override
        public int availableSinceSchema() {
            return 0;   // fixme: 0 or 3 for default salter? see compareHashSalted() in JS version
        }
    };
}

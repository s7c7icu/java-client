package xland.s7c7icu.client.api;

import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.api.json.JsonWriter;

import java.io.FilterInputStream;

public final class EmptySalter implements Salter {
    public static final EmptySalter INSTANCE = new EmptySalter();

    private EmptySalter() {}

    @Override
    public SalterType<?> type() {
        return TYPE;
    }

    @Override
    public HasherInputStream wrap(HasherInputStream original) {
        return original;
    }

    @Override
    public HasherOutputStream wrap(HasherOutputStream original) {
        return original;
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
    };
}

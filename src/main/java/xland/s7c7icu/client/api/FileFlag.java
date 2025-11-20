package xland.s7c7icu.client.api;

import xland.s7c7icu.client.Identifiable;
import xland.s7c7icu.client.SinceSchema;

import java.util.Objects;

public abstract class FileFlag implements Identifiable, SinceSchema {
    protected FileFlag() {}

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof FileFlag otherFlag && Objects.equals(this.id(), otherFlag.id());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(this.id());
    }

    @Override
    public final String toString() {
        return String.valueOf(this.id());
    }
}

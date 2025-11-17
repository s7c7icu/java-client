package xland.s7c7icu.client.api.json;

import org.jetbrains.annotations.NotNull;
import xland.s7c7icu.client.impl.json.JsonWriterImpl;

import java.io.IOException;

public sealed interface JsonWriter extends java.io.Closeable permits JsonWriterImpl {
    void beginArray() throws IOException;
    void endArray() throws IOException;
    void beginObject() throws IOException;
    void endObject() throws IOException;
    void appendKey(@NotNull String key) throws IOException;
    void appendPrimitive(@NotNull JsonPrimitive primitive) throws IOException;
    void appendValue(@NotNull JsonElement element) throws IOException;

    static JsonWriter create(Appendable appendable) {
        return new JsonWriterImpl(appendable);
    }
}

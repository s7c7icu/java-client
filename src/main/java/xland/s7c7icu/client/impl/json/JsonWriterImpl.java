package xland.s7c7icu.client.impl.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONWriter;
import xland.s7c7icu.client.api.json.*;

import java.io.IOException;
import java.util.Objects;

public final class JsonWriterImpl implements JsonWriter {
    private final JSONWriter wrapped;
    private @Nullable java.io.Closeable closeable;

    public JsonWriterImpl(Appendable appendable) {
        Objects.requireNonNull(appendable);
        this.wrapped = new JSONWriter(appendable);
        if (appendable instanceof java.io.Closeable c) {
            this.closeable = c;
        }
    }

    @Override
    public void beginArray() throws IOException {
        try {
            wrapped.array();
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void endArray() throws IOException {
        try {
            wrapped.endArray();
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void beginObject() throws IOException {
        try {
            wrapped.object();
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void endObject() throws IOException {
        try {
            wrapped.endObject();
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void appendKey(@NotNull String key) throws IOException {
        Objects.requireNonNull(key, "key");
        try {
            wrapped.key(key);
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void appendPrimitive(@NotNull JsonPrimitive primitive) throws IOException {
        Objects.requireNonNull(primitive, "primitive");
        try {
            if (primitive instanceof JsonBoolean b) {
                wrapped.value(b.booleanValue());
            } else if (primitive instanceof JsonNull) {
                wrapped.value(null);    // impl allows null invocation
            } else {    // Number or String
                wrapped.value(JsonElementImpl.unwrap(primitive));
            }
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void appendValue(@NotNull JsonElement element) throws IOException {
        Objects.requireNonNull(element, "element");
        if (element instanceof JsonPrimitive) {
            appendPrimitive((JsonPrimitive) element);
            return;
        }
        try {
            wrapped.value(JsonElementImpl.unwrap(element));
        } catch (JSONException e) {
            throw wrapThrow(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    static Error wrapThrow(Throwable t) throws IOException {
        if (t.getCause() instanceof IOException ex) {
            throw ex;
        } else {
            throw new JsonException(t.getMessage());
        }
    }
}

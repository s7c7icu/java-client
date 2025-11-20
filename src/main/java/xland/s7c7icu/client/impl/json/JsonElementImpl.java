package xland.s7c7icu.client.impl.json;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.*;
import xland.s7c7icu.client.api.json.*;
import xland.s7c7icu.client.impl.DelegateIterator;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;

public final class JsonElementImpl {
    @Contract("null->null;!null->!null")
    public static JsonElement wrap(final Object obj) {
        if (obj == null) return null;
        if (obj == JSONObject.NULL) return JsonNull.INSTANCE;

        return switch (obj.getClass().getName()) {
            case "java.lang.String", "java.lang.Character" -> new JsonStringImpl(obj.toString());
            case "java.lang.Boolean" -> JsonBoolean.of((Boolean) obj);
            default -> {
                if (obj.getClass().isArray()) {
                    yield new JsonArrayImpl(new JSONArray(obj));
                } else if (obj instanceof Enum) {
                    yield new JsonStringImpl(((Enum<?>) obj).name());
                } else if (obj instanceof Number) {
                    yield new JsonNumberImpl((Number) obj);
                } else if (obj instanceof JSONObject) {
                    yield new JsonObjectImpl((JSONObject) obj);
                } else if (obj instanceof JSONArray) {
                    yield new JsonArrayImpl((JSONArray) obj);
                } else {
                    yield new JsonStringImpl(obj.toString());
                }
            }
        };
    }

    @Contract("null->null; !null->!null")
    public static Object unwrap(final JsonElement element) {
        if (element == null) return null;
        if (element instanceof JsonNull) return JSONObject.NULL;
        if (element instanceof JsonBoolean) return ((JsonBoolean) element).booleanValue();
        if (element instanceof JsonNumber) return ((JsonNumberImpl) element).numeralValue();
        if (element instanceof JsonString) return ((JsonString) element).stringValue();
        if (element instanceof JsonArray) return ((JsonArrayImpl) element).arr();
        if (element instanceof JsonObject) return ((JsonObjectImpl) element).obj();
        throw new IncompatibleClassChangeError();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class JsonReaderImpl implements JsonReader {
        private final JSONTokener jsonTokener;

        JsonReaderImpl(JSONTokener jsonTokener) {
            this.jsonTokener = jsonTokener;
        }

        private static final JSONParserConfiguration PARSER_CONF = new JSONParserConfiguration().withUseNativeNulls(true).withStrictMode();

        public JsonReaderImpl(Reader reader) {
            this(new JSONTokener(reader, PARSER_CONF));
        }

        @Override
        public JsonElement nextElement() throws IOException {
            Object obj;
            try {
                obj = jsonTokener.nextValue();
            } catch (JSONException e) {
                throw JsonWriterImpl.wrapThrow(e);
            }
            return wrap(obj);
        }

        @Override
        public JsonArray nextArray() throws IOException {
            JSONArray arr;
            try {
                arr = new JSONArray(jsonTokener);
            } catch (JSONException e) {
                throw JsonWriterImpl.wrapThrow(e);
            }
            return new JsonArrayImpl(arr);
        }

        @Override
        public JsonObject nextObject() throws IOException {
            JSONObject obj;
            try {
                obj = new JSONObject(jsonTokener);
            } catch (JSONException e) {
                throw JsonWriterImpl.wrapThrow(e);
            }
            return new JsonObjectImpl(obj);
        }

        @Override
        public void close() throws IOException {
            jsonTokener.close();
        }
    }

    public record JsonArrayImpl(JSONArray arr) implements JsonArray {
        public JsonArrayImpl() {
            this(new JSONArray());
        }

        @Override
        public void add(@NotNull JsonElement element) {
            Objects.requireNonNull(element, "element");
            arr.put(unwrap(element));
        }

        @Override
        public @NotNull Iterator<JsonElement> iterator() {
            return new DelegateIterator<>(arr.iterator(), JsonElementImpl::wrap);
        }

        @Override
        public JsonElement deepCopy() {
            JSONArray backingArray = new JSONArray(arr.toList());
            return new JsonArrayImpl(backingArray);
        }

        @Override
        public JsonElement get(int index) {
            return wrap(arr.get(index));
        }

        @Override
        public int size() {
            return arr.length();
        }

        @Override
        public boolean isEmpty() {
            return arr.isEmpty();
        }

        @Override
        public @Nullable JsonElement remove(int index) {
            return wrap(arr.remove(index));
        }

        @Override
        public void clear() {
            arr.clear();
        }

        @Override
        public @NotNull String toString() {
            return arr.toString();
        }
    }

    public record JsonObjectImpl(JSONObject obj) implements JsonObject {
        public JsonObjectImpl() {
            this(new JSONObject());
        }

        @Override
        public JsonElement deepCopy() {
            JSONObject backingObject = new JSONObject(obj.toMap());
            return new JsonObjectImpl(backingObject);
        }

        @Override
        public @Nullable JsonElement put(@NotNull String key, JsonElement value) {
            Objects.requireNonNull(key, "key");
            Object prevKey = obj.opt(key);
            obj.put(key, unwrap(value));
            return wrap(prevKey);
        }

        @Override
        public Optional<JsonElement> get(@NotNull String key) {
            Objects.requireNonNull(key, "key");
            Object value = obj.opt(key);
            if (value == null) return Optional.empty();
            return Optional.of(wrap(value));
        }

        @Override
        public @Nullable JsonElement remove(@NotNull String key) {
            Objects.requireNonNull(key, "key");
            return wrap(obj.remove(key));
        }

        @Override
        public boolean has(@NotNull String key) {
            Objects.requireNonNull(key, "key");
            return obj.has(key);
        }

        @Override
        public int size() {
            return obj.length();
        }

        @Override
        public boolean isEmpty() {
            return obj.isEmpty();
        }

        @Override
        public void clear() {
            obj.clear();
        }

        @Override
        public @NotNull Iterator<Map.Entry<String, JsonElement>> iterator() {
            final class Entry implements Map.Entry<String, JsonElement> {
                private final String key;

                Entry(String k) {
                    this.key = k;
                }

                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public JsonElement getValue() {
                    return JsonObjectImpl.this.get(key).orElseThrow();
                }

                @Override
                public JsonElement setValue(JsonElement value) {
                    return JsonObjectImpl.this.put(key, value);
                }
            }
            return new DelegateIterator<>(obj.keys(), Entry::new);
        }

        @Override
        public void forEach(BiConsumer<? super String, ? super JsonElement> c) {
            Objects.requireNonNull(c);
            for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
                String k = it.next();
                c.accept(k, this.get(k).orElseThrow());
            }
        }

        @Override
        public @NotNull String toString() {
            return obj.toString();
        }
    }

    public record JsonNumberImpl(@Override Number numeralValue) implements JsonNumber {
        public JsonNumberImpl {
            Objects.requireNonNull(numeralValue, "value");
        }

        @Override
        public int hashCode() {
            return numeralValue.hashCode();
        }

        @Override
        public @NotNull String toString() {
            return numeralValue.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof JsonNumber otherNumber && numeralValue.equals(otherNumber.numeralValue());
        }
    }

    public record JsonStringImpl(@Override String stringValue) implements JsonString {
        public JsonStringImpl {
            Objects.requireNonNull(stringValue, "value");
        }

        @Override
        public int hashCode() {
            return stringValue.hashCode();
        }

        @Override
        public @NotNull String toString() {
            return stringValue;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof JsonString otherStr && stringValue.equals(otherStr.stringValue());
        }
    }
}

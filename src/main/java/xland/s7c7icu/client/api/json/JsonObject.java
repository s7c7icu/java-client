package xland.s7c7icu.client.api.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xland.s7c7icu.client.impl.json.JsonElementImpl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public sealed interface JsonObject extends JsonElement, Iterable<Map.Entry<String, JsonElement>> permits JsonElementImpl.JsonObjectImpl {
    static JsonObject of() {
        return new JsonElementImpl.JsonObjectImpl();
    }

    static JsonObject from(java.io.Reader reader) throws java.io.IOException, JsonException {
        return JsonReader.create(reader).nextObject();
        // not closing the JsonReader is acceptable because it actually closes the Reader
    }

    @Nullable JsonElement put(@NotNull String key, JsonElement value);
    Optional<JsonElement> get(@NotNull String key);
    @Nullable JsonElement remove(@NotNull String key);

    default boolean has(@NotNull String key) {
        return get(key).isPresent();
    }

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    void clear();

    default void forEach(BiConsumer<? super String, ? super JsonElement> c) {
        Objects.requireNonNull(c);
        this.forEach(entry -> c.accept(entry.getKey(), entry.getValue()));
    }
}

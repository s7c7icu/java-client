package xland.s7c7icu.client.api.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xland.s7c7icu.client.impl.json.JsonElementImpl;

import java.util.NoSuchElementException;

public sealed interface JsonArray extends JsonElement, Iterable<JsonElement> permits JsonElementImpl.JsonArrayImpl {
    static JsonArray of() {
        return new JsonElementImpl.JsonArrayImpl();
    }

    static JsonArray of(@NotNull JsonElement @NotNull... elements) {
        JsonArray arr = of();
        for (JsonElement e : elements) {
            arr.add(e);
        }
        return arr;
    }

    static JsonArray read(java.io.Reader reader) throws java.io.IOException, JsonException {
        return JsonReader.create(reader).nextArray();
        // not closing the JsonReader is acceptable because it actually closes the Reader
    }

    void add(@NotNull JsonElement element);
    int size();
    JsonElement get(int index);

    @Nullable JsonElement remove(int index);

    void clear();

    default boolean isEmpty() {
        return size() == 0;
    }

    default JsonElement getFirst() {
        assumeNonEmpty();
        return get(0);
    }

    default JsonElement getLast() {
        assumeNonEmpty();
        return get(size() - 1);
    }

    private void assumeNonEmpty() {
        if (isEmpty()) throw new NoSuchElementException();
    }
}

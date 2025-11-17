package xland.s7c7icu.client.api.json;

import java.util.Optional;

public sealed interface JsonElement permits JsonArray, JsonObject, JsonPrimitive {
    JsonElement deepCopy();

    default Optional<String> asString() {
        if (this instanceof JsonString s) {
            return Optional.of(s.stringValue());
        } else {
            return Optional.empty();
        }
    }

    default Optional<Number> asNumber() {
        if (this instanceof JsonNumber n) {
            return Optional.of(n.numeralValue());
        } else {
            return Optional.empty();
        }
    }
}

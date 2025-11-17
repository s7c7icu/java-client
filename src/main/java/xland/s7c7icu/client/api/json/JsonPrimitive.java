package xland.s7c7icu.client.api.json;

public sealed interface JsonPrimitive extends JsonElement permits JsonBoolean, JsonNull, JsonNumber, JsonString {
    default JsonElement deepCopy() {
        return this;
    }
}

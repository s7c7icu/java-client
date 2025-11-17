package xland.s7c7icu.client.api.json;

import xland.s7c7icu.client.impl.json.JsonElementImpl;

import java.io.IOException;
import java.io.Reader;

public sealed interface JsonReader extends java.io.Closeable permits JsonElementImpl.JsonReaderImpl {
    JsonElement nextElement() throws IOException, JsonException;
    JsonArray nextArray() throws IOException, JsonException;
    JsonObject nextObject() throws IOException, JsonException;

    static JsonReader create(Reader reader) {
        return new JsonElementImpl.JsonReaderImpl(reader);
    }
}

package xland.s7c7icu.client.api;

import xland.s7c7icu.client.Identifiable;
import xland.s7c7icu.client.SinceSchema;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.api.json.JsonWriter;

import java.io.IOException;

public interface SalterType<S extends Salter> extends Identifiable, SinceSchema {
    S deserialize(JsonObject obj);

    void serialize(S salter, JsonWriter writer) throws IOException;
}

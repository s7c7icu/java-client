package xland.s7c7icu.client.api;

import xland.s7c7icu.client.Identifiable;
import xland.s7c7icu.client.api.json.JsonObject;

public interface SalterType<S extends Salter> extends Identifiable {
    S create(JsonObject obj);
}

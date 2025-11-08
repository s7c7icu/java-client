package xland.s7c7icu.client.api;

import xland.s7c7icu.client.Identifiable;

public interface SalterType<S extends Salter> extends Identifiable {
    S create(org.json.JSONObject obj);
}

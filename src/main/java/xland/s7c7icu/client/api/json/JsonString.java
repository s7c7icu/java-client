package xland.s7c7icu.client.api.json;

import xland.s7c7icu.client.impl.json.JsonElementImpl;

public sealed interface JsonString extends JsonPrimitive permits JsonElementImpl.JsonStringImpl {
    static JsonString of(CharSequence cs) {
        return new JsonElementImpl.JsonStringImpl(cs.toString());
    }

    String stringValue();
}

package xland.s7c7icu.client.api.json;

import xland.s7c7icu.client.impl.json.JsonElementImpl;

public sealed interface JsonNumber extends JsonPrimitive permits JsonElementImpl.JsonNumberImpl {
    static JsonNumber of(Number number) {
        return new JsonElementImpl.JsonNumberImpl(number);
    }

    static JsonNumber of(long x) {
        return of(Long.valueOf(x));
    }

    static JsonNumber of(double x) {
        return of(Double.valueOf(x));
    }

    Number numeralValue();
}

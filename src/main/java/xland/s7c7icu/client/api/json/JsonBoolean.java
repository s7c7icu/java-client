package xland.s7c7icu.client.api.json;

public enum JsonBoolean implements JsonPrimitive {
    FALSE(false), TRUE(true);
    private final boolean value;

    JsonBoolean(boolean value) {
        this.value = value;
    }

    public boolean booleanValue() {
        return this.value;
    }

    public static JsonBoolean of(boolean b) {
        return b ? TRUE : FALSE;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }
}

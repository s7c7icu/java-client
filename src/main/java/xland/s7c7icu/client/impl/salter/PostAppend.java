package xland.s7c7icu.client.impl.salter;

import xland.s7c7icu.client.api.HasherInputStream;
import xland.s7c7icu.client.api.HasherOutputStream;
import xland.s7c7icu.client.api.Salter;
import xland.s7c7icu.client.api.SalterType;
import xland.s7c7icu.client.api.json.JsonElement;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.api.json.JsonString;
import xland.s7c7icu.client.api.json.JsonWriter;

import java.io.IOException;
import java.util.Base64;
import java.util.NoSuchElementException;

@SuppressWarnings("ClassCanBeRecord")
final class PostAppend implements Salter {
    private final byte[] salt;

    PostAppend(String saltAsString) {
        this(Base64.getDecoder().decode(saltAsString));
    }

    PostAppend(byte[] trustedSalt) {
        this.salt = trustedSalt;
    }

    static PostAppend random(java.util.random.RandomGenerator random) {
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return new PostAppend(salt);
    }

    @Override
    public SalterType<?> type() {
        return TYPE;
    }

    @Override
    public HasherInputStream wrap(HasherInputStream original) {
        original.updateHash(this.salt());
        return original;
    }

    @Override
    public HasherOutputStream wrap(HasherOutputStream original) {
        original.updateHash(this.salt());
        return original;
    }

    static final SalterType<PostAppend> TYPE = new SalterType<>() {
        @Override
        public PostAppend deserialize(JsonObject obj) {
            String salt = obj.get("salt").flatMap(JsonElement::asString).orElseThrow(() -> new NoSuchElementException("salt"));
            return new PostAppend(salt);
        }

        @Override
        public void serialize(PostAppend salter, JsonWriter writer) throws IOException {
            String saltAsString = salter.saltAsString();
            writer.appendKey("salt");
            writer.appendPrimitive(JsonString.of(saltAsString));
        }

        @Override
        public String id() {
            return "s7c7icu:postappend-v0";
        }
    };

    public byte[] salt() {
        return salt.clone();
    }

    String saltAsString() {
        return Base64.getEncoder().encodeToString(salt);
    }
}

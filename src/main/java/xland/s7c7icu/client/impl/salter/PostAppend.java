package xland.s7c7icu.client.impl.salter;

import xland.s7c7icu.client.api.*;
import xland.s7c7icu.client.api.json.JsonElement;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.api.json.JsonString;
import xland.s7c7icu.client.api.json.JsonWriter;
import xland.s7c7icu.client.impl.json.JsonElementImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;
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
    // fixme: cannot reuse EmptySalter under this module
    public HasherInputStream calculateInput(InputStream originalInput, Map<Hashing, String> hashes) throws IOException {
        if (hashes.isEmpty()) throw new IllegalArgumentException("Empty hash table");
        InputStream res = originalInput;

        var entries = hashes.entrySet().iterator();
        do {
            final Map.Entry<Hashing, String> entry = entries.next();
            HasherInputStream hasherWrapped = entry.getKey().wrapInput(res);
            hasherWrapped.updateHash(this.salt());
            res = hasherWrapped;
            // TODO: entry.getValue()??? How to VERIFY while WRAPPING streams together? Or can we simply give up flowing
        } while (entries.hasNext());
        return (HasherInputStream) res;
    }

    @Override
    public HasherOutputStream wrapOutput(HasherOutputStream original) {
        original.updateHash(this.salt());
        return original;
    }

    static final SalterType<PostAppend> TYPE = new SalterType<>() {
        @Override
        public PostAppend deserialize(JsonObject obj) {
            String salt = obj.get("salt").flatMap(JsonElement::asString).orElseThrow(JsonElementImpl.keyAbsent("salt"));
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

        @Override
        public int availableSinceSchema() {
            return 3;
        }
    };

    public byte[] salt() {
        return salt.clone();
    }

    String saltAsString() {
        return Base64.getEncoder().encodeToString(salt);
    }
}

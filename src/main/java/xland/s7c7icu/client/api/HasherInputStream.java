package xland.s7c7icu.client.api;

import java.io.FilterInputStream;
import java.io.InputStream;

public abstract class HasherInputStream extends FilterInputStream {
    protected HasherInputStream(InputStream in) {
        super(in);
    }

    public abstract byte[] getHash();

    public final String getHashAsHex() {
        return Hashing.toHex(this.getHash());
    }

    public final boolean matchesHash(byte[] expected) {
        byte[] computedHash = getHash();
        return java.security.MessageDigest.isEqual(computedHash, expected);
    }

    public final boolean matchesHash(String expected) {
        return this.matchesHash(Hashing.fromHex(expected));
    }
}

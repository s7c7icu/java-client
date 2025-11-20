package xland.s7c7icu.client.api;

import java.io.FilterOutputStream;
import java.io.OutputStream;

public abstract class HasherOutputStream extends FilterOutputStream {
    public HasherOutputStream(OutputStream out) {
        super(out);
    }

    public abstract byte[] getHash();

    public void updateHash(byte[] hash) {
        this.updateHash(hash, 0, hash.length);
    }
    public abstract void updateHash(byte[] hash, int off, int len);

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

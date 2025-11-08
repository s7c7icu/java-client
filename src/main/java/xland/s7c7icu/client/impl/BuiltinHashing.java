package xland.s7c7icu.client.impl;

import xland.s7c7icu.client.api.HasherInputStream;
import xland.s7c7icu.client.api.Hashing;
import xland.s7c7icu.client.spi.HashingProvider;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public enum BuiltinHashing implements Hashing {
    SHA512("SHA-512", "sha512", "sha-512", "sha_512"),
    SHA256("SHA-256", "sha256", "sha-256", "sha_256"),
    SHA384("SHA-384", "sha384", "sha-384", "sha_384"),
    SHA3("SHA3-512", "sha3", "sha3-512", "sha3_512"),
    SHA3_256("SHA3-256", "sha3_256", "sha3-256"),
    SHA3_384("SHA3-384", "sha3_384", "sha3-384")
    ;

    private final Supplier<MessageDigest> mdSupplier;
    private final String id;
    private final List<String> aliases;

    BuiltinHashing(String mdIdentifier, String id, String... aliases) {
        this.mdSupplier = mdSupplier(mdIdentifier);
        this.id = id;
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public HasherInputStream wrapInput(InputStream inputStream) {
        return new DigestInputStreamWrapper(inputStream, mdSupplier.get());
    }

    private static final class DigestInputStreamWrapper extends HasherInputStream {
        private final MessageDigest messageDigest;

        DigestInputStreamWrapper(InputStream in, MessageDigest messageDigest) {
            super(new DigestInputStream(in, messageDigest));
            this.messageDigest = messageDigest;
        }

        @Override
        public byte[] getHash() {
            return messageDigest.digest();
        }
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public Collection<String> aliases() {
        return this.aliases;
    }

    private static Supplier<MessageDigest> mdSupplier(String identifier) {
        return () -> {
            try {
                return MessageDigest.getInstance(identifier);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("Illegal message digest algorithm: " + identifier, e);
            }
        };
    }

    public static final class Provider implements HashingProvider {
        @Override
        public Collection<? extends Hashing> values() {
            return Arrays.asList(BuiltinHashing.values());
        }
    }
}

package xland.s7c7icu.client.impl;

import org.jetbrains.annotations.NotNull;
import software.pando.crypto.nacl.SecretBox;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

record SodiumChloride(byte[] key, byte[] nonce) implements AutoCloseable {
    SodiumChloride(byte[] password) {
        this(
                Arrays.copyOfRange(password, 24, password.length),
                Arrays.copyOf(password, 24)
        );
    }

    static InputStream wrap(InputStream in, byte[] rawPassword) {
        return new PseudoFlowInputStream(in, new SodiumChloride(rawPassword));
    }

    static OutputStream wrap(OutputStream out, byte[] rawPassword) {
        return new PseudoFlowOutputStream(out, new SodiumChloride(rawPassword));
    }

    private final static class PseudoFlowInputStream extends InputStream {
        private final InputStream in;
        private final SodiumChloride pass;
        private ByteArrayInputStream decrypted;

        PseudoFlowInputStream(InputStream in, SodiumChloride pass) {
            Objects.requireNonNull(in, "in");
            Objects.requireNonNull(pass, "pass");
            this.in = in;
            this.pass = pass;
        }

        private void ensureDecrypted() throws IOException {
            if (this.decrypted == null) {
                byte[] raw = in.readAllBytes();
                try (SecretBox box = SecretBox.fromCombined(pass.nonce(), raw)) {
                    byte[] decryptedBytes = box.decrypt(SecretBox.key(pass.key()));
                    this.decrypted = new ByteArrayInputStream(decryptedBytes);
                }
            }
        }

        @Override
        public int read() throws IOException {
            ensureDecrypted();
            return decrypted.read();
        }

        @Override
        public int read(byte @NotNull [] b) throws IOException {
            ensureDecrypted();
            return decrypted.read(b);
        }

        @Override
        public int read(byte @NotNull [] b, int off, int len) throws IOException {
            ensureDecrypted();
            return decrypted.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            pass.close();
            in.close();
        }
    }

    private final static class PseudoFlowOutputStream extends FilterOutputStream {
        private final OutputStream realOut;
        private final SodiumChloride pass;

        PseudoFlowOutputStream(OutputStream out, SodiumChloride pass) {
            super(new ByteArrayOutputStream());
            Objects.requireNonNull(out, "out");
            Objects.requireNonNull(pass, "pass");
            this.realOut = out;
            this.pass = pass;
        }

        @Override
        public void close() throws IOException {
            final OutputStream realOut = this.realOut;
            byte[] raw = ((ByteArrayOutputStream) this.out).toByteArray();
            byte[] encrypted;
            try (realOut; SecretBox box = SecretBox.encrypt(SecretBox.key(pass.key()), pass.nonce(), raw)) {
                encrypted = box.getCiphertextWithTag();
                realOut.write(encrypted);
            } finally {
                this.pass.close();
            }
        }
    }

    @Override
    public byte[] key() {
        return key.clone();
    }

    @Override
    public byte[] nonce() {
        return nonce.clone();
    }

    private static final byte ONES = -1;

    @Override
    public void close() {
        Arrays.fill(key, ONES);
        Arrays.fill(nonce, ONES);
    }
}

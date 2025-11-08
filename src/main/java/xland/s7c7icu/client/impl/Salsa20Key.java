package xland.s7c7icu.client.impl;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.jetbrains.annotations.Contract;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

final class Salsa20Key {
    private final byte[] key;
    private final byte[] nonce;

    Salsa20Key(byte[] rawPassword) {
        nonce = Arrays.copyOfRange(rawPassword, 24, rawPassword.length - 24);
        key = Arrays.copyOfRange(rawPassword, 0, 24);
    }

    @Contract("->new")
    private CipherParameters parameters() {
        return new ParametersWithIV(new KeyParameter(key), nonce);
    }

    OutputStream wrapOutput(OutputStream wrapped) {
        StreamCipher engine = new Salsa20Engine();
        engine.init(/*forEncryption=*/true, this.parameters());
        return new CipherOutputStream(wrapped, engine);
    }

    InputStream wrapInput(InputStream wrapped) {
        StreamCipher engine = new Salsa20Engine();
        engine.init(/*forEncryption=*/false, this.parameters());
        return new CipherInputStream(wrapped, engine);
    }
}

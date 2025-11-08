package xland.s7c7icu.client.impl;

import xland.s7c7icu.client.api.Algorithm;
import xland.s7c7icu.client.spi.AlgorithmProvider;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public enum BuiltinAlgorithm implements Algorithm {
    BASE64("base64", Base64.getEncoder()::wrap, Base64.getDecoder()::wrap),
    DEFLATE("deflate", DeflaterOutputStream::new, InflaterInputStream::new),
    AES("aes", UnaryOperator.identity(), UnaryOperator.identity()) {
        @Override
        public OutputStream mapOutput(OutputStream wrapped, byte[] rawPassword) {
            return new Salsa20Key(rawPassword).wrapOutput(wrapped);
        }

        @Override
        public InputStream mapInput(InputStream wrapped, byte[] rawPassword) {
            return new Salsa20Key(rawPassword).wrapInput(wrapped);
        }
    };

    private final String id;
    private final UnaryOperator<OutputStream> outFilter;
    private final UnaryOperator<InputStream> inFilter;

    BuiltinAlgorithm(String id, UnaryOperator<OutputStream> outFilter, UnaryOperator<InputStream> inFilter) {
        this.id = id;
        this.outFilter = outFilter;
        this.inFilter = inFilter;
    }

    @Override
    public OutputStream mapOutput(OutputStream wrapped, byte[] rawPassword) {
        return outFilter.apply(wrapped);
    }

    @Override
    public InputStream mapInput(InputStream wrapped, byte[] rawPassword) {
        return inFilter.apply(wrapped);
    }

    @Override
    public String id() {
        return id;
    }

    public static final class Provider implements AlgorithmProvider {
        @Override
        public Collection<? extends Algorithm> values() {
            return Arrays.asList(BuiltinAlgorithm.values());
        }
    }
}

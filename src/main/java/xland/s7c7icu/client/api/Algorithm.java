package xland.s7c7icu.client.api;

import xland.s7c7icu.client.Identifiable;
import xland.s7c7icu.client.SinceSchema;

import java.io.InputStream;
import java.io.OutputStream;

public interface Algorithm extends Identifiable, SinceSchema {
    OutputStream mapOutput(OutputStream wrapped, byte[] rawPassword);

    InputStream mapInput(InputStream wrapped, byte[] rawPassword);
}

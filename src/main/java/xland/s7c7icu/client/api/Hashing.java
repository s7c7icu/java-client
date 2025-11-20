package xland.s7c7icu.client.api;

import xland.s7c7icu.client.Aliased;
import xland.s7c7icu.client.SinceSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Hashing extends Aliased, SinceSchema {
    HasherInputStream wrapInput(InputStream inputStream) throws IOException;
    HasherOutputStream wrapOutput(OutputStream outputStream) throws IOException;

    static String toHex(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            hexString.append(String.format("%02x", b)); // Converts byte to 2 hex digits
        }
        return hexString.toString();
    }

    static byte[] fromHex(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length >>> 1]; // each byte is represented by 2 hex digits
        for (int i = 0; i < length; i += 2) {
            byteArray[i >>> 1] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }
}

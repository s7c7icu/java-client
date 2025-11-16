package xland.s7c7icu.client.impl;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;

final class Salsa20Cipher {
    private static final int ROUNDS = 20;
    private final byte[] key;
    private final byte[] nonce;
    private long counter;
    private final byte[] keyStream = new byte[64];
    private int keyStreamPos = 64;

    private Salsa20Cipher(byte[] key, byte[] nonce) {
        this.key = key;
        this.nonce = nonce;
        this.counter = 0;

        if (nonce.length == 24) {
            setupXSalsa20();
        }
    }

    Salsa20Cipher(byte[] rawPassword) {
        this(
                /*key  =*/Arrays.copyOfRange(rawPassword, 24, rawPassword.length),
                /*nonce=*/Arrays.copyOf(rawPassword, 24)
        );
    }

    private static void qr(int[] arr, int a, int b, int c, int d) {
        arr[b] ^= Integer.rotateLeft(arr[a] + arr[d], 7);
        arr[c] ^= Integer.rotateLeft(arr[b] + arr[a], 9);
        arr[d] ^= Integer.rotateLeft(arr[c] + arr[b], 13);
        arr[a] ^= Integer.rotateLeft(arr[d] + arr[c], 18);
    }

    static void block(int[/*16*/] out, int[/*16*/] in) {
        int[] x = Arrays.copyOf(in, 16);
        // 10 loops * 2 rounds/loop = 20 rounds
        for (int i = 0; i < ROUNDS; i += 2) {
            // Odd round
            qr(x, 0, 4, 8, 12);
            qr(x, 5, 9, 13, 1);
            qr(x, 10, 14, 2, 6);
            qr(x, 15, 3, 7, 11);
            // Even round
            qr(x, 0, 1, 2, 3);
            qr(x, 5, 6, 7, 4);
            qr(x, 10, 11, 8, 9);
            qr(x, 15, 12, 13, 14);
        }
        for (int i = 0; i < 16; i++) {
            out[i] = x[i] + in[i];
        }
    }

    static InputStream wrapInput(InputStream input, byte[] rawPassword) {
        return new Salsa20InputStream(input, rawPassword);
    }

    static OutputStream wrapOutput(OutputStream output, byte[] rawPassword) {
        return new Salsa20OutputStream(output, rawPassword);
    }

    // Little Endian
    static int bytesToInt(byte[] b, int offset) {
        return (b[offset] & 0xFF) |
                ((b[offset + 1] & 0xFF) << 8) |
                ((b[offset + 2] & 0xFF) << 16) |
                ((b[offset + 3] & 0xFF) << 24);
    }

    // Little Endian
    static void intToBytes(int value, byte[] b, int offset) {
        b[offset] = (byte) value;
        b[offset + 1] = (byte) (value >>> 8);
        b[offset + 2] = (byte) (value >>> 16);
        b[offset + 3] = (byte) (value >>> 24);
    }

    private void setupXSalsa20() {
        int[] hsalsaInput = new int[16];
        int[] hsalsaOutput = new int[16];

        fillConstants(hsalsaInput);

        for (int i = 0; i < 8; i++) {
            hsalsaInput[1 + i] = bytesToInt(key, i * 4);
        }

        for (int i = 0; i < 4; i++) {
            hsalsaInput[6 + i] = bytesToInt(nonce, i * 4);
        }

        block(hsalsaOutput, hsalsaInput);

        int[] subkey = new int[8];
        subkey[0] = hsalsaOutput[0];
        subkey[1] = hsalsaOutput[5];
        subkey[2] = hsalsaOutput[10];
        subkey[3] = hsalsaOutput[15];
        subkey[4] = hsalsaOutput[6];
        subkey[5] = hsalsaOutput[7];
        subkey[6] = hsalsaOutput[8];
        subkey[7] = hsalsaOutput[9];

        for (int i = 0; i < 8; i++) {
            intToBytes(subkey[i], key, i * 4);
        }
        System.arraycopy(nonce, 16, nonce, 0, 8);
    }

    public void processBytes(byte[] data, int offset, int length) {
        for (int i = 0; i < length; i++) {
            if (keyStreamPos >= 64) {
                generateKeyStream();
                keyStreamPos = 0;
            }
            data[offset + i] ^= keyStream[keyStreamPos++];
        }
    }

    private void generateKeyStream() {
        int[] input = new int[16];
        int[] output = new int[16];

        // 构造Salsa20输入块
        // 常量
        fillConstants(input);

        // 密钥
        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                input[1 + i] = bytesToInt(key, i * 4);
            } else {
                input[11 + (i - 4)] = bytesToInt(key, i * 4);
            }
        }

        // Nonce和计数器（使用后8字节nonce）
        input[6] = bytesToInt(nonce, 0);
        input[7] = bytesToInt(nonce, 4);
        input[8] = (int) counter;
        input[9] = (int) (counter >>> 32);

        // 执行Salsa20块函数
        block(output, input);

        // 转换为字节流
        for (int i = 0; i < 16; i++) {
            intToBytes(output[i], keyStream, i * 4);
        }

        counter++;
    }

    private static void fillConstants(int[] arr) {
        // Little Endian
        arr[0] = 0x61707865;  // "expa"
        arr[5] = 0x3320646e;  // "nd 3"
        arr[10] = 0x79622d32; // "2-by"
        arr[15] = 0x6b206574; // "te k"
    }

    // Salsa20输入流实现
    private static class Salsa20InputStream extends FilterInputStream {
        private final Salsa20Cipher cipher;
        private final byte[] buffer = new byte[64];
        private int bufferPos = 0;
        private int bufferLen = 0;

        public Salsa20InputStream(InputStream input, byte[] rawPassword) {
            super(input);
            this.cipher = new Salsa20Cipher(rawPassword);
        }

        @Override
        public int read() throws IOException {
            if (bufferPos >= bufferLen) {
                bufferLen = super.read(buffer, 0, 64);
                if (bufferLen == -1) return -1;

                // 解密当前块
                cipher.processBytes(buffer, 0, bufferLen);
                bufferPos = 0;
            }

            return buffer[bufferPos++] & 0xFF;
        }

        @Override
        public int read(byte @NotNull [] b, int off, int len) throws IOException {
            int totalRead = 0;
            while (len > 0) {
                if (bufferPos >= bufferLen) {
                    bufferLen = super.read(buffer, 0, 64);
                    if (bufferLen == -1) break;

                    cipher.processBytes(buffer, 0, bufferLen);
                    bufferPos = 0;
                }

                int toCopy = Math.min(len, bufferLen - bufferPos);
                System.arraycopy(buffer, bufferPos, b, off, toCopy);

                bufferPos += toCopy;
                off += toCopy;
                len -= toCopy;
                totalRead += toCopy;
            }

            return totalRead == 0 ? -1 : totalRead;
        }
    }

    // Salsa20输出流实现
    private static class Salsa20OutputStream extends FilterOutputStream {
        private final Salsa20Cipher cipher;
        private final byte[] buffer = new byte[64];
        private int bufferPos = 0;

        public Salsa20OutputStream(OutputStream output, byte[] rawPassword) {
            super(output);
            this.cipher = new Salsa20Cipher(rawPassword);
        }

        @Override
        public void write(int b) throws IOException {
            if (bufferPos >= buffer.length) {
                flushBuffer();
            }
            buffer[bufferPos++] = (byte) b;
        }

        @Override
        public void write(byte @NotNull [] b, int off, int len) throws IOException {
            while (len > 0) {
                int toWrite = Math.min(len, buffer.length - bufferPos);
                System.arraycopy(b, off, buffer, bufferPos, toWrite);

                bufferPos += toWrite;
                off += toWrite;
                len -= toWrite;

                if (bufferPos >= buffer.length) {
                    flushBuffer();
                }
            }
        }

        @Override
        public void flush() throws IOException {
            flushBuffer();
            super.flush();
        }

        @Override
        public void close() throws IOException {
            flush();
            super.close();
        }

        private void flushBuffer() throws IOException {
            if (bufferPos > 0) {
                // 加密当前块
                byte[] toEncrypt = Arrays.copyOf(buffer, bufferPos);
                cipher.processBytes(toEncrypt, 0, bufferPos);
                super.write(toEncrypt, 0, bufferPos);
                bufferPos = 0;
            }
        }
    }
}

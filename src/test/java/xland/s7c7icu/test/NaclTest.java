package xland.s7c7icu.test;

import software.pando.crypto.nacl.SecretBox;

import java.security.MessageDigest;
import java.util.Arrays;

public record NaclTest(byte[] key, byte[] nonce) {
    public NaclTest(byte[] password) {
        this(
                Arrays.copyOfRange(password, 24, password.length),
                Arrays.copyOf(password, 24)
        );
    }

    public static void main(String[] args) {
        NaclTest naclTest = new NaclTest(Salsa20Test.PASSWORD);
        System.out.println("Content length: " + Salsa20Test.CONTENT.length);

        // encrypt
        try (SecretBox secretBox = SecretBox.encrypt(
                SecretBox.key(naclTest.key()),
                naclTest.nonce(),
                Salsa20Test.CONTENT.clone()
        )) {
            byte[] withoutTag = secretBox.getCiphertextWithoutTag();
            System.out.println("WithoutTag length: " + withoutTag.length);
            byte[] withTag = secretBox.getCiphertextWithTag();
            System.out.println("WithTag length: " + withTag.length);

            System.out.println("IsEqual: " + MessageDigest.isEqual(withTag, Salsa20Test.ENCRYPTED));
        }

        // decrypt
        try (SecretBox secretBox = SecretBox.fromCombined(
                naclTest.nonce(), Salsa20Test.ENCRYPTED.clone()
        )) {
            byte[] decrypted = secretBox.decrypt(SecretBox.key(naclTest.key()));

            System.out.println("IsEqual: " + MessageDigest.isEqual(decrypted, Salsa20Test.CONTENT));
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
}

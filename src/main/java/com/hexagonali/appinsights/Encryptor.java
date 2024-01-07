package com.hexagonali.appinsights;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.Encryptors;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

class Encryptor {
    private static final Base64.Encoder Encoder = Base64.getMimeEncoder();

    private static String encode(byte[] bytes) {
        return new String(Encoder.encode(bytes), StandardCharsets.ISO_8859_1);
    }

    private static byte[] getSalt() {
        byte[] salt = new byte[32];
        (new SecureRandom()).nextBytes(salt);
        return salt;
    }

    static String encryptText(String clearText, byte[] salt, String encryptionKey) {
        String hexEncodedSalt = String.valueOf(Hex.encode(salt));
        byte[] encryptedBytes = Encryptors.standard(encryptionKey, hexEncodedSalt).encrypt(clearText.getBytes(StandardCharsets.UTF_8));
        return encode(encryptedBytes);
    }

    static String createCipherText(String clearText, String encryptionKey) {
        byte[] saltBytes = getSalt();
        String salt = encode(saltBytes);
        String ecrypted = encryptText(clearText, saltBytes, encryptionKey);
        return "CIPHERTEXT{1;" + salt + ";" + ecrypted + "}";
    }
}

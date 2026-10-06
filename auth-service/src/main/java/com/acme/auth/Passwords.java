package com.acme.auth;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Passwords are never stored as plain text. Stored format:  iterations:salt:hash   (salt and hash are Base64)
public class Passwords {
    private static final int ITERATIONS = 120000;
    private static final int KEY_BITS = 256;

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);                    // a different random salt for every user
        byte[] h = pbkdf2(password, salt, ITERATIONS);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(h);
    }

    public static boolean verify(String password, String stored) {
        try {
            String[] p = stored.split(":");
            if (p.length != 3) return false;
            byte[] salt = Base64.getDecoder().decode(p[1]);
            byte[] expected = Base64.getDecoder().decode(p[2]);
            byte[] actual = pbkdf2(password, salt, Integer.parseInt(p[0]));
            return MessageDigest.isEqual(expected, actual);    // constant-time comparison
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

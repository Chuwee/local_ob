package es.onebox.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

public final class GeneratorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorUtils.class);

    private static final String BASE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int PASSWORD_DEFAULT_LENGTH = 6;
    private static final Random RANDOM = new Random();

    private GeneratorUtils() {
        throw new UnsupportedOperationException("Not allowed");
    }

    public static String getPassword() {
        return getPassword(BASE, PASSWORD_DEFAULT_LENGTH);
    }

    public static String getPassword(String key, int length) {
        StringBuilder pswd = new StringBuilder("");
        for (int i = 0; i < length; i++) {
            pswd.append(key.charAt((RANDOM.nextInt(key.length()))));
        }
        return pswd.toString();
    }

    public static String generateApiKey() {
        try {
            MessageDigest salt = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            salt.update(generateUUID().getBytes(StandardCharsets.UTF_8));
            return DigestUtils.md5Hex(salt.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Algorithm not alloweed");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getHashMD5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            return asHex(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Error generating hash MD5", e);
        }
    }

    public static String getHashSHA256(String text) {
        try {
            MessageDigest sha = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
            return asHex(sha.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Error generating hash SHA256", e);
        }
    }

    private static String asHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; ++i) {
            sb.append(HEXADECIMAL[(bytes[i] & 240) >> 4]);
            sb.append(HEXADECIMAL[bytes[i] & 15]);
        }

        return sb.toString();
    }
}

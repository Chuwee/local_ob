package es.onebox.mgmt.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    private static final char[] HEXADECIMAL
            = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private PasswordUtils() {
    }

    public static String getHashMD5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return asHex(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Error generating password", e);
        }
    }

    private static String asHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(2 * bytes.length);

        for (byte aByte : bytes) {
            sb.append(HEXADECIMAL[(aByte & 240) >> 4]);
            sb.append(HEXADECIMAL[aByte & 15]);
        }

        return sb.toString();
    }
}

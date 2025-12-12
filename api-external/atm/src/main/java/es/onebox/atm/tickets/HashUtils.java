package es.onebox.atm.tickets;

import org.apache.commons.codec.binary.Hex;
import org.hashids.Hashids;

import java.nio.charset.StandardCharsets;

public class HashUtils {

    private static final String SALT = "0ne3ox";
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int HASHID_LENGHT = 16;


    private HashUtils() {
    }

    public static String encodeHashIds(String value) {
        Hashids hashids = new Hashids(SALT, HASHID_LENGHT, ALPHABET);
        String hexValue = Hex.encodeHexString(value.getBytes(StandardCharsets.UTF_8));
        return hashids.encodeHex(hexValue);
    }

    public static String decodeHashIds(String value) {
        try {
            Hashids hashids = new Hashids(SALT, HASHID_LENGHT, ALPHABET);
            String hexValue = hashids.decodeHex(value);
            return new String(Hex.decodeHex(hexValue.toCharArray()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
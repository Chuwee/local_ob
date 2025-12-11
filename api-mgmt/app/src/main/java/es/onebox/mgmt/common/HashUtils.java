package es.onebox.mgmt.common;

import org.hashids.Hashids;

public class HashUtils {

    private static final String SALT = "0ne3ox";
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int HASHID_LENGHT = 16;


    private HashUtils() {}

    public static String encodeHashIds(long... codes) {
        Hashids hashids = new Hashids(SALT, HASHID_LENGHT, ALPHABET);
        return hashids.encode(codes);
    }

    public static long[] decodeHashIds(String code) {
        Hashids hashids = new Hashids(SALT, HASHID_LENGHT, ALPHABET);
        return hashids.decode(code);
    }
}

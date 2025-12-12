package es.onebox.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class HMACUtils {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String computeHmac(String secret, String requestBody) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] hmacBytes = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hmacBytes).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Error computing HMAC", e);
        }
    }

    public static boolean verifyHmac(String secret, String requestBody, String receivedSignature) {
        if (receivedSignature == null || receivedSignature.isEmpty()) {
            return false;
        }
        String computedHmac = HMACUtils.computeHmac(secret, requestBody);
        return MessageDigest.isEqual(computedHmac.getBytes(StandardCharsets.UTF_8),
                receivedSignature.getBytes(StandardCharsets.UTF_8));
    }
}

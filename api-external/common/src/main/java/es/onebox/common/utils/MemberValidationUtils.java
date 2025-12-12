package es.onebox.common.utils;

import org.apache.commons.lang3.StringUtils;

public class MemberValidationUtils {

    public static final int AVET_MEMBER_PARTNER_BALANCE_ID = 11;

    private static final String PARTNER_PREFIX = "partner:";
    private static final String PARTNER_COMPANION_PREFIX = "partnerCompanion:";

    private MemberValidationUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static String getPromotionPartnerId(String collectiveKey) {
        if (StringUtils.isNotBlank(collectiveKey)) {
            if(collectiveKey.startsWith(PARTNER_PREFIX)) {
                return collectiveKey.substring(collectiveKey.indexOf(PARTNER_PREFIX) + PARTNER_PREFIX.length());
            }
            if(collectiveKey.startsWith(PARTNER_COMPANION_PREFIX)) {
                return collectiveKey.substring(collectiveKey.indexOf(PARTNER_COMPANION_PREFIX) + PARTNER_COMPANION_PREFIX.length());
            }
        }
        return collectiveKey;
    }

}

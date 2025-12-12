package es.onebox.common.datasources.distribution.dto;

import java.util.HashMap;
import java.util.Map;

public class ClientDataParamMap {

    public static final String EMAIL = "email";
    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String EXTERNAL_CLIENT_ID = "external_client_id";
    public static final String FIRST_NAME = "name";
    public static final String LAST_NAME = "lastname";
    public static final String BIRTHDAY = "birthday";
    public static final String PHONE = "phone";
    public static final String PHONE_PREFIX = "phone_prefix";
    public static final String IDENTIFICATION = "identification";
    public static final String GENDER = "gender";
    public static final String COUNTRY = "country";
    public static final String COUNTRY_SUBDIVISION = "country_subdivision";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String POSTAL_CODE = "zip_code";
    public static final String ALLOW_COMMERCIAL_MAILING = "allow_commercial_mailing";

    private final Map<String, Object> paramMap = new HashMap<>();

    public void put(String key, Object value) {
        if (value != null) {
            paramMap.put(key, value);
        }
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}

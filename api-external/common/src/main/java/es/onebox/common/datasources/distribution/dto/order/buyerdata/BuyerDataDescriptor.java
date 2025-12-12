package es.onebox.common.datasources.distribution.dto.order.buyerdata;

import java.time.ZonedDateTime;
import java.util.Arrays;

public enum BuyerDataDescriptor {

    NAME("name","given-name",  false, String.class),
    LASTNAME("lastname","family-name",  false, String.class),
    EMAIL("email", null, true, String.class),
    EXTERNAL_CLIENT_ID("external_client_id", null, false, String.class),
    PHONE("phone",null,  false, String.class),
    ALLOW_COMMERCIAL_MAILING("allow_commercial_mailing", null, false, Boolean.class),
    GENDER("gender", null, false, GenderField.class),
    LANGUAGE("language",null,  false, String.class),
    BIRTHDAY("birthday",  null,false, ZonedDateTime.class),
    //location attrs
    CITY("city", null, false, String.class),
    COUNTRY("country" , null,false, String.class),
    COUNTRY_SUBDIVISION("country_subdivision",null,  false, String.class),
    ZIP_CODE("zip_code", null, false, String.class),
    ADDRESS("address", null, false, String.class);



    private final String apiAttribute;
    private final String autoCompleteAttribute;
    private final boolean mandatory;
    private final Class type;

    BuyerDataDescriptor(String apiAttribute, String autoCompleteAttribute, boolean mandatory, Class type) {
        this.apiAttribute = apiAttribute;
        this.autoCompleteAttribute = autoCompleteAttribute;
        this.mandatory = mandatory;
        this.type = type;
    }

    public String apiAttribute() {
        return apiAttribute;
    }

    public String getAutoCompleteAttribute() {
        return autoCompleteAttribute;
    }

    public boolean mandatory() {
        return mandatory;
    }

    public Class type() {
        return type;
    }

    public static BuyerDataDescriptor fromApiAttribute(String apiAttribute) {
        return Arrays.stream(BuyerDataDescriptor.values())
                .filter(attr -> attr.apiAttribute.equals(apiAttribute))
                .findFirst()
                .orElse(null);
    }
}

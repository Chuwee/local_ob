package es.onebox.eci.ticketsales.dto;

import java.util.Arrays;

public enum BrandType {
    WEB("eci-web",""),
    VIAJES("eci-viajes",""),
    CC("eci-cc",""),
    TPV("eci-tpv",""),
    MARCA_BLANCA("eci-mb","0001"),
    COLECTIVOS("eci-colectivos",""),
    CORPORATE("eci-corporate","");

    private final String value;
    private final String identifier;

    BrandType(String value, String identifier) {
        this.value = value;
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static BrandType findByValue(String name) {
        return Arrays.asList(BrandType.values())
                .stream().filter(b -> b.getValue().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static BrandType findByIdentifier(String id) {
        return Arrays.asList(BrandType.values())
                .stream().filter(b -> b.getIdentifier().equals(id))
                .findFirst()
                .orElse(null);
    }
}

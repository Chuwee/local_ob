package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum Provider {
    
    SEETICKETS("seetickets"),
    ITALIAN_COMPLIANCE("italian_compliance"),
    SGA("sga");

    private final String code;

    Provider(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Provider getCode(String name) {
        return Stream.of(values()).filter(v -> v.getCode().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}

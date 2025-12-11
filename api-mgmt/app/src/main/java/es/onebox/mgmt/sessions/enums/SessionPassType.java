package es.onebox.mgmt.sessions.enums;

import java.util.Arrays;

public enum SessionPassType {
    DAYS("días"),
    USES("usos"),
    ALL_FAIR("toda la feria"),
    PERIOD("periodo");

    private final String value;

    SessionPassType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SessionPassType findByValue(String value) {
        return Arrays.stream(values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .orElse(null);
    }


}

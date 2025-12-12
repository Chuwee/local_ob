package es.onebox.eci.organizations.dto;

import java.util.Arrays;

public enum OrganizationType {
    SPONSOR('S'), ORGANIZER('O'), PROVIDER('P');

    private final char initial;

    OrganizationType(char initial) {
        this.initial = initial;
    }

    public static OrganizationType findByInitial(char c) {
        return Arrays.stream(values()).filter(it -> it.initial == c).findFirst().orElse(null);
    }
}

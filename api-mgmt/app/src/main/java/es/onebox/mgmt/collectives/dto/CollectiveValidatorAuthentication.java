package es.onebox.mgmt.collectives.dto;

import java.util.Arrays;

public enum CollectiveValidatorAuthentication {
    NONE,
    USER_PASSWORD;

    public static CollectiveValidatorAuthentication getByName(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(CollectiveValidatorAuthentication.values()).filter(a -> a.name().equals(name)).findFirst().orElse(null);
    }
}


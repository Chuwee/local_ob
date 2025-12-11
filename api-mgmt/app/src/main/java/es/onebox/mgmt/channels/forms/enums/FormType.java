package es.onebox.mgmt.channels.forms.enums;

import java.util.stream.Stream;

public enum FormType {
    BUYER_DATA_FORMS("buyer-data-forms"),
    DATA_PROTECTION_FORMS("data-protection-forms");

    String name;

    FormType (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FormType fromName(String name) {
        return Stream.of(values()).filter(v -> v.name.equals(name)).findFirst().orElse(null);
    }
}

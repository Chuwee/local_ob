package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum PackSubtype {
    PROMOTER(0),
    CHANNEL(1);

    private final Integer id;

    PackSubtype(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static PackSubtype getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }
}

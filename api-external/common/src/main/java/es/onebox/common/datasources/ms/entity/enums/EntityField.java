package es.onebox.common.datasources.ms.entity.enums;

import es.onebox.common.datasources.common.dto.FiltrableField;

import java.util.stream.Stream;

public enum EntityField implements FiltrableField {

    ID("id", "id"),
    NAME("name", "name"),
    SHORTNAME("short_name", "shortName"),
    EMAIL("email", "email"),
    STATUS("status", "status"),
    ALLOW_AVET_INTEGRATION("settings.allow_avet_integration", "settings.allowAvetIntegration"),
    ALLOW_ACTIVITY_EVENTS("settings.allow_activity_events", "settings.allowActivityEvents"),
    OPERATORID("operator.id", "operator.id"),
    ALLOW_MEMBERS("settings.allow_members", "settings.allowMembers");

    String name;
    String dtoName;

    EntityField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static EntityField byName(String name) {
        return Stream.of(EntityField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}

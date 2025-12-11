package es.onebox.mgmt.users.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum UserField implements FiltrableField {

    USERNAME("username", "username"),
    EMAIL("email", "email"),
    NAME("name", "name"),
    LASTNAME("last_name", "lastName"),
    JOBTITLE("job_title", "jobTitle"),
    LASTVISIT("last_visit", "lastVisit"),
    STATUS("status", "status");

    String name;
    String dtoName;

    UserField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static UserField byName(String name) {
        return Stream.of(UserField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}

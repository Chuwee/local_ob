package es.onebox.event.catalog.elasticsearch.enums;

import java.util.Arrays;

public enum TemplateType {

    INTERN(1), AVET(2), ACTIVITY(3);

    private final Integer id;

    TemplateType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TemplateType fromId(Integer id) {
        return Arrays.stream(TemplateType.values()).filter(s ->
                s.getId().equals(id)).findFirst().orElse(null);
    }
}

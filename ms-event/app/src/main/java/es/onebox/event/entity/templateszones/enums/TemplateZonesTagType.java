package es.onebox.event.entity.templateszones.enums;

import java.util.stream.Stream;

public enum TemplateZonesTagType {
    NAME(1 ),
    DESCRIPTION(2);

    private final Integer id;

    private TemplateZonesTagType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public static TemplateZonesTagType getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(TemplateZonesTagType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

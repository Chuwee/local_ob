package es.onebox.event.entity.templateszones.enums;

import java.util.stream.Stream;

public enum TemplatesZonesStatus {
    DELETED(0),
    DISABLED(1),
    ENABLED(2);

    private final Integer id;

    TemplatesZonesStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TemplatesZonesStatus getStatusById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(TemplatesZonesStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

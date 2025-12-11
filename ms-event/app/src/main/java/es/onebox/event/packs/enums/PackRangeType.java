package es.onebox.event.packs.enums;

import java.util.stream.Stream;

public enum PackRangeType {
    AUTOMATIC(1),
    CUSTOM(2);

    private Integer id;

    PackRangeType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static PackRangeType getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }
}

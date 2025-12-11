package es.onebox.event.priceengine.packs;

import java.util.stream.Stream;

public enum PackItemType {
    SESSION(1),
    PRODUCT(2),
    EVENT(3);

    private final Integer id;

    PackItemType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static PackItemType getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }

}

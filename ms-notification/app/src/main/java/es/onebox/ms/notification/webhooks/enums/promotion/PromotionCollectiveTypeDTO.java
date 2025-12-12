package es.onebox.ms.notification.webhooks.enums.promotion;

import java.util.stream.Stream;

public enum PromotionCollectiveTypeDTO {
    NONE(0),
    RESTRICTED(1);

    private Integer id;

    PromotionCollectiveTypeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionCollectiveTypeDTO fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}

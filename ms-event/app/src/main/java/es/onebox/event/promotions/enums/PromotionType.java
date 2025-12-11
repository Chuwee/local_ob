package es.onebox.event.promotions.enums;

import java.util.Arrays;

/**
 * @author ignasi
 */
public enum PromotionType {

    AUTOMATIC(1),
    PROMOTION(2),
    DISCOUNT(3);

    private final Integer id;

    PromotionType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionType fromId(Integer id) {
        return Arrays.stream(PromotionType.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

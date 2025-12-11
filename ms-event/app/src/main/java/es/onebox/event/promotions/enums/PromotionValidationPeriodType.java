/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.promotions.enums;

import java.util.Arrays;

/**
 * @author ignasi
 */
public enum PromotionValidationPeriodType {

    ALL(0),
    PERIOD(1),
    DAYS_UNTIL_SESSION(2);

    private final Integer id;

    private PromotionValidationPeriodType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionValidationPeriodType fromId(Integer id) {
        return Arrays.stream(PromotionValidationPeriodType.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}

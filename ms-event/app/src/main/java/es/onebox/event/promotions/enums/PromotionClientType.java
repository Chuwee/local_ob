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
public enum PromotionClientType {

    ALL(0),
    COLLECTIVE(1);

    private final Integer id;

    private PromotionClientType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionClientType fromId(Integer id) {
        return Arrays.stream(PromotionClientType.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

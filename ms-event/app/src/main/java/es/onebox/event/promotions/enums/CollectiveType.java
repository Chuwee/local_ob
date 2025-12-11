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
public enum CollectiveType {

    VENUE(1),
    CHANNEL(2),
    EXTERNAL(3),
    INTERNAL(4);

    private final Integer id;

    private CollectiveType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static CollectiveType fromId(Integer id) {
        return Arrays.stream(CollectiveType.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

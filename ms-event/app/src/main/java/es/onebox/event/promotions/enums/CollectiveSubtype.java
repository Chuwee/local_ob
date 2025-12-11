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
public enum CollectiveSubtype {

    EXTERNAL_PROMOTIONAL_CODE(4, CollectiveType.EXTERNAL, CollectiveValidationType.PROMOTIONAL_CODE),
    EXTERNAL_USER(5, CollectiveType.EXTERNAL, CollectiveValidationType.USER),
    EXTERNAL_USER_PASSWORD(6, CollectiveType.EXTERNAL, CollectiveValidationType.USER_PASSWORD),
    INTERNAL_PROMOTIONAL_CODE(1, CollectiveType.INTERNAL, CollectiveValidationType.PROMOTIONAL_CODE),
    INTERNAL_USER(2, CollectiveType.INTERNAL, CollectiveValidationType.USER),
    INTERNAL_USER_PASSWORD(3, CollectiveType.INTERNAL, CollectiveValidationType.USER_PASSWORD),
    INTERNAL_GIFT_TICKET(11, CollectiveType.INTERNAL, CollectiveValidationType.GIFT_TICKET);

    private final Integer id;
    private final CollectiveType collectiveType;
    private final CollectiveValidationType validationType;

    private CollectiveSubtype(Integer id, CollectiveType collectiveType, CollectiveValidationType validationType) {
        this.id = id;
        this.collectiveType = collectiveType;
        this.validationType = validationType;
    }

    public Integer getId() {
        return id;
    }

    public CollectiveType getCollectiveType() {
        return collectiveType;
    }

    public CollectiveValidationType getValidationType() {
        return validationType;
    }

    public static CollectiveSubtype fromId(Integer id) {
        return Arrays.stream(CollectiveSubtype.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

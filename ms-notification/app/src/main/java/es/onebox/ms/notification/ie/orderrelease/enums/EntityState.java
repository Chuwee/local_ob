package es.onebox.ms.notification.ie.orderrelease.enums;


import es.onebox.ms.notification.ie.orderrelease.dto.EnumConvertible;

import java.io.Serializable;

public enum EntityState implements EnumConvertible, Serializable {

    DELETED(0),
    ACTIVE(1),
    PENDING(2),
    BLOCKED(3),
    BLOCKED_TEMPORARILY(4);

    private Integer id;

    EntityState(Integer id) {
        this.id = id;
    }

    public static EnumConvertible get(Integer id) {
        return values()[id];
    }

    @Override
    public EnumConvertible getEnum(int id) {
        return get(id);
    }

    @Override
    public int getId() {
        return id;
    }
}

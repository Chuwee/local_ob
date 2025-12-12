package es.onebox.common.datasources.avetconfig.dto;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum WSConnectionVersion {

    ONE_DOT_X(1),
    TWO_DOT_FOUR(2),
    TWO_DOT_FOUR_LEGACY(3);

    private int id;

    private static final Map<Integer, WSConnectionVersion> lookup = new HashMap<>();

    static {
        for (WSConnectionVersion connectionVersion : EnumSet.allOf(WSConnectionVersion.class)) {
            lookup.put(connectionVersion.getId(), connectionVersion);
        }
    }

    WSConnectionVersion(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static WSConnectionVersion get(int id) {
        return lookup.get(id);
    }
}

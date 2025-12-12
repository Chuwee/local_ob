package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChannelType implements Serializable {

    EXTERNAL(1),
    PORTAL(2),
    BOXOFFICE(3),
    MEMBERS(4);

    private int id;

    private static final Map<Integer, ChannelType> lookup = new HashMap<>();

    static {
        for (ChannelType channelType : EnumSet.allOf(ChannelType.class)) {
            lookup.put(channelType.getId(), channelType);
        }
    }

    ChannelType(int id) {
        this.id = id;
    }

    public static ChannelType get(Integer id) {
        return lookup.get(id);
    }

    public int getId() {
        return id;
    }

}

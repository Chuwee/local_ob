package es.onebox.mgmt.datasources.ms.channel.salerequests.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SessionType {
    SESSION(0),
    RESTRICTED_PACK(1),
    UNRESTRICTED_PACK(2);

    private int type;

    private static final Map<Integer, es.onebox.mgmt.sessions.enums.SessionType> lookup = new HashMap<>();

    static {
        for (es.onebox.mgmt.sessions.enums.SessionType sessionType : EnumSet.allOf(es.onebox.mgmt.sessions.enums.SessionType.class)) {
            lookup.put(sessionType.getType(), sessionType);
        }
    }

    public static es.onebox.mgmt.sessions.enums.SessionType getById(Integer id) {
        return lookup.get(id);
    }

    SessionType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

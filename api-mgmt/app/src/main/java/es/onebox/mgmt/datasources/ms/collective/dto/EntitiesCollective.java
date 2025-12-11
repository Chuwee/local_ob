package es.onebox.mgmt.datasources.ms.collective.dto;

import java.util.HashMap;
import java.util.Map;

public class EntitiesCollective extends HashMap<Long, EntityCollective> {

    private static final long serialVersionUID = -7340219256822056793L;

    public EntitiesCollective() {
    }

    public EntitiesCollective(Map<? extends Long, ? extends EntityCollective> m) {
        super(m);
    }
}

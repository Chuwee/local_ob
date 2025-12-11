package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class Entities extends BaseResponseCollection<Entity, Metadata> {

    private static final long serialVersionUID = 1L;

    public Entities() {
    }

    public Entities(List<Entity> response, Metadata metadata) {
        super(response, metadata);
    }

}

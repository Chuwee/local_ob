package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.io.Serial;
import java.util.List;

public class Producers extends BaseResponseCollection<Producer, Metadata> {

    @Serial
    private static final long serialVersionUID = 1L;

    public Producers() {
    }

    public Producers(List<Producer> response, Metadata metadata) {
        super(response, metadata);
    }

}

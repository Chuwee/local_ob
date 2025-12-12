package es.onebox.common.datasources.ms.client.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateExternalCustomersRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7518424567473880270L;

    private List<String> externalIds;
    private Long entityId;

    public List<String> getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(List<String> externalIds) {
        this.externalIds = externalIds;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}

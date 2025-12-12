package es.onebox.common.datasources.webhook.dto.fever;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class AllowedEntitiesFileData implements Serializable {

    @Serial
    private static final long serialVersionUID = -5752766005578918846L;
    @Id
    private Long entityId;
    private List<Long> allowedEntities;

    public AllowedEntitiesFileData() {
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<Long> getAllowedEntities() {
        return allowedEntities;
    }

    public void setAllowedEntities(List<Long> allowedEntities) {
        this.allowedEntities = allowedEntities;
    }
}

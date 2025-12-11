package es.onebox.event.datasources.ms.entity.dto;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cgalindo on 01/03/2019.
 */
@CouchDocument
public class EntityConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer entityId;
    private Map<Integer, List<Integer>> eventSaleRetrictions = new HashMap<>();
    private Integer passbookId;
    private AccommodationsEntityConfig accommodationsConfig;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Map<Integer, List<Integer>> getEventSaleRetrictions() {
        return eventSaleRetrictions;
    }

    public void setEventSaleRetrictions(Map<Integer, List<Integer>> eventSaleRetrictions) {
        this.eventSaleRetrictions = eventSaleRetrictions;
    }

    public Integer getPassbookId() {
        return passbookId;
    }

    public void setPassbookId(Integer passbookId) {
        this.passbookId = passbookId;
    }

    public AccommodationsEntityConfig getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(AccommodationsEntityConfig accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

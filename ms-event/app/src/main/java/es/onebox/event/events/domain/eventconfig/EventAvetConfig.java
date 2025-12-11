package es.onebox.event.events.domain.eventconfig;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.products.dao.couch.AvetSectorRestriction;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class EventAvetConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer eventId;
    private Boolean isSocket;
    private List<AvetSectorRestriction> restrictions;

    public List<AvetSectorRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<AvetSectorRestriction> restrictions) {
        this.restrictions = restrictions;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Boolean getIsSocket() {
        return this.isSocket;
    }

    public void setIsSocket(Boolean isSocket) {
        this.isSocket = isSocket;
    }
}

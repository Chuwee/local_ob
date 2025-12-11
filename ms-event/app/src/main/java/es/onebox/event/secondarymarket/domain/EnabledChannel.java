package es.onebox.event.secondarymarket.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@CouchDocument
public class EnabledChannel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public EnabledChannel() {}
    public EnabledChannel(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
}

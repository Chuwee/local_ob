package es.onebox.event.events.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.List;

@CouchDocument
public class ExternalRateType implements Serializable {

    @Id
    private Long sessionId;

    private List<ExternalRateTypeDetail> rateTypes;

    private List<String> seatSections;

    public Long getSessionId() { return sessionId; }

    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public List<ExternalRateTypeDetail> getRateTypes() { return rateTypes; }

    public void setRateTypes(List<ExternalRateTypeDetail> rateTypes) { this.rateTypes = rateTypes; }

    public List<String> getSeatSections() {
        return seatSections;
    }

    public void setSeatSections(List<String> seatSections) {
        this.seatSections = seatSections;
    }
}

package es.onebox.event.loyaltypoints.seasontickets.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;
import java.util.List;

@CouchDocument
public class SeasonTicketLoyaltyPointsConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SessionLoyaltyPoints> sessions;

    public List<SessionLoyaltyPoints> getSessions() { return sessions; }

    public void setSessions(List<SessionLoyaltyPoints> sessions) { this.sessions = sessions; }
}

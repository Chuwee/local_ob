package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@CouchDocument
public class SeasonTicketRenewalCouchDocument implements Serializable {
    private static final long serialVersionUID = 8233150484553027316L;

    private final String ns = "renewals";

    @Id
    private String userId;

    private Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<Long, List<SeasonTicketRenewalProduct>> getSeasonTicketProductMap() {
        return seasonTicketProductMap;
    }

    public void setSeasonTicketProductMap(Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap) {
        this.seasonTicketProductMap = seasonTicketProductMap;
    }

    public String getNs() {
        return ns;
    }
}

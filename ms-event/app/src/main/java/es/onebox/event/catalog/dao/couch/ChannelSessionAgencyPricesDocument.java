package es.onebox.event.catalog.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

@CouchDocument
public class ChannelSessionAgencyPricesDocument extends ChannelSessionPricesDocument {

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long sessionId;
    @Id(index = 3)
    private Long agencyId;

    @Override
    public Long getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public Long getChannelId() {
        return channelId;
    }

    @Override
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }
}

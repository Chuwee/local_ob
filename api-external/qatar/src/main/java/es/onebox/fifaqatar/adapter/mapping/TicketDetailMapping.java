package es.onebox.fifaqatar.adapter.mapping;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;

@CouchDocument
public class TicketDetailMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = -4455858864622113216L;

    @Id
    private String id; // ticket_fvid or ticket_orderCode_sessionId

    private String orderCode;
    private Long sessionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}

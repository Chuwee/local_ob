package es.onebox.event.loyaltypoints.sessions.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;

@CouchDocument
public class SessionLoyaltyPointsConfig implements Serializable {

    private static final long serialVersionUID = -7104279849222582191L;

    private PointGain pointGain;

    public PointGain getPointGain() { return pointGain; }

    public void setPointGain(PointGain pointGain) { this.pointGain = pointGain; }
}

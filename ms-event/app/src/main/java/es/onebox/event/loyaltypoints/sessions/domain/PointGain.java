package es.onebox.event.loyaltypoints.sessions.domain;

import es.onebox.event.loyaltypoints.sessions.domain.enums.SessionPointsType;

import java.io.Serializable;

public class PointGain implements Serializable {

    private static final long serialVersionUID = -7104279849222582191L;

    private Integer amount;
    private SessionPointsType type;

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public SessionPointsType getType() { return type; }

    public void setType(SessionPointsType type) { this.type = type; }
}

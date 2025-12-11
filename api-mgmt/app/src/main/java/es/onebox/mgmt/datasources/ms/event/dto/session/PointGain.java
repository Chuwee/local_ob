package es.onebox.mgmt.datasources.ms.event.dto.session;

import es.onebox.mgmt.sessions.enums.SessionPointsType;

import java.io.Serializable;

public class PointGain implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer amount;
    private SessionPointsType type;

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public SessionPointsType getType() { return type; }

    public void setType(SessionPointsType type) { this.type = type; }
}

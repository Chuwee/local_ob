package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class RedemptionExpiration implements Serializable {

    @Serial
    private static final long serialVersionUID = -1827036735386601645L;

    private ExpirationUnit unit;
    private Integer expires;

    public RedemptionExpiration() {
    }

    public ExpirationUnit getUnit() {
        return unit;
    }

    public void setUnit(ExpirationUnit unit) {
        this.unit = unit;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }
}

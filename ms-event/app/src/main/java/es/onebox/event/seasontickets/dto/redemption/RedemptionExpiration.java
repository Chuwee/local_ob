package es.onebox.event.seasontickets.dto.redemption;

import java.io.Serializable;

public class RedemptionExpiration implements Serializable {

    private ExpirationUnit unit;
    private Integer expires;

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

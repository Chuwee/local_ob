package es.onebox.mgmt.seasontickets.dto.redemption;

import java.io.Serial;
import java.io.Serializable;

public class RedemptionExpirationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6172530981835858327L;

    private ExpirationUnitDTO unit;
    private Integer expires;

    public RedemptionExpirationDTO() {
    }

    public ExpirationUnitDTO getUnit() {
        return unit;
    }

    public void setUnit(ExpirationUnitDTO unit) {
        this.unit = unit;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }
}

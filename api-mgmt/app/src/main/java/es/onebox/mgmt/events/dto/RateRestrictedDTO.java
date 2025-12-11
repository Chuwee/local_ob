package es.onebox.mgmt.events.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;

public class RateRestrictedDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private IdNameDTO rate;

    private RateRestrictionDTO restrictions;

    public IdNameDTO getRate() {
        return rate;
    }

    public void setRate(IdNameDTO rate) {
        this.rate = rate;
    }

    public RateRestrictionDTO getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(RateRestrictionDTO restrictions) {
        this.restrictions = restrictions;
    }
}

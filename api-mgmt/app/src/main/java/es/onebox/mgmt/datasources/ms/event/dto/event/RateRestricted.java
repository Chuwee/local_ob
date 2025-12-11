package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;

public class RateRestricted implements Serializable {

    private static final long serialVersionUID = 1L;

    private IdNameDTO rate;
    private RateRestrictions restrictions;

    public IdNameDTO getRate() {
        return rate;
    }

    public void setRate(IdNameDTO rate) {
        this.rate = rate;
    }

    public RateRestrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(RateRestrictions restrictions) {
        this.restrictions = restrictions;
    }
}

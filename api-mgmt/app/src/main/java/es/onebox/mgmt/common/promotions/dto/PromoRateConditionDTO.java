package es.onebox.mgmt.common.promotions.dto;

import java.io.Serial;
import java.io.Serializable;

public class PromoRateConditionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer limit;
    private Long rate;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Long getRate() {
        return rate;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }
}

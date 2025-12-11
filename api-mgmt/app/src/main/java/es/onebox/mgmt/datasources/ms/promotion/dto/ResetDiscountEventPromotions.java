package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serializable;

public class ResetDiscountEventPromotions implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long currencyId;

    public ResetDiscountEventPromotions(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }
}

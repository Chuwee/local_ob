package es.onebox.mgmt.datasources.ms.event.dto.secondarymarket;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean hideBasePrice;
    private Boolean payToBalance;

    public Boolean getHideBasePrice() {
        return hideBasePrice;
    }

    public void setHideBasePrice(Boolean hideBasePrice) {
        this.hideBasePrice = hideBasePrice;
    }

    public Boolean getPayToBalance() {
        return payToBalance;
    }

    public void setPayToBalance(Boolean payToBalance) {
        this.payToBalance = payToBalance;
    }
}

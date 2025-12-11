package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -9030820347845251464L;

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

package es.onebox.event.secondarymarket.dto;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3203204399606205216L;

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

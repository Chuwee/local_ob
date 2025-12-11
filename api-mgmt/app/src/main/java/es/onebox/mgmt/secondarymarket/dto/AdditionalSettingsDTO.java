package es.onebox.mgmt.secondarymarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class AdditionalSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3203204399606205216L;

    @JsonProperty("hide_base_price")
    private Boolean hideBasePrice;
    @JsonProperty("pay_to_balance")
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

package es.onebox.event.events.dto.conditions;

import java.io.Serial;
import java.io.Serializable;

public class ClientConditions implements Serializable {

    @Serial
    private static final long serialVersionUID = -5529909998319966816L;

    ClientCondition commission;
    ClientCondition discount;

    public ClientConditions() {
    }

    public ClientConditions(ClientCondition commission, ClientCondition discount) {
        this.commission = commission;
        this.discount = discount;
    }

    public ClientCondition getCommission() {
        return commission;
    }

    public void setCommission(ClientCondition commission) {
        this.commission = commission;
    }

    public ClientCondition getDiscount() {
        return discount;
    }

    public void setDiscount(ClientCondition discount) {
        this.discount = discount;
    }
}

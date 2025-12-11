package es.onebox.event.secondarymarket.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serial;
import java.io.Serializable;

@CouchDocument
public class SessionSecondaryMarketConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private ResalePrice price;
    private Commission commission;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ResalePrice getPrice() {
        return price;
    }

    public void setPrice(ResalePrice price) {
        this.price = price;
    }

    public Commission getCommission() {
        return commission;
    }

    public void setCommission(Commission commission) {
        this.commission = commission;
    }
}
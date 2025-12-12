package es.onebox.common.datasources.catalog.dto.session.prices;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionPrices implements Serializable {

    @Serial
    private static final long serialVersionUID = -2436667936912977463L;
    private List<CatalogRate> rates;

    public SessionPrices() {
    }

    public List<CatalogRate> getRates() {
        return rates;
    }

    public void setRates(List<CatalogRate> rates) {
        this.rates = rates;
    }

}

package es.onebox.event.catalog.dto.product;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class ProductStockSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3283278561567566637L;

    @NotNull
    private Map<Long, Long> productsVariant;
    @NotNull
    private Set<Long> sessionIds;

    public Map<Long, Long> getProductsVariant() {
        return productsVariant;
    }

    public void setProductsVariant(Map<Long, Long> productsVariant) {
        this.productsVariant = productsVariant;
    }

    public Set<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Set<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }
}

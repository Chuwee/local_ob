package es.onebox.event.catalog.dto.product;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductVariantStock implements Serializable {

    @Serial
    private static final long serialVersionUID = -7543684169532369514L;

    private Long decrementStock;
    private Long incrementStock;

    public Long getDecrementStock() {
        return decrementStock;
    }

    public void setDecrementStock(Long decrementStock) {
        this.decrementStock = decrementStock;
    }

    public Long getIncrementStock() {
        return incrementStock;
    }

    public void setIncrementStock(Long incrementStock) {
        this.incrementStock = incrementStock;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

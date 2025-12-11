package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateProductVariantPrices implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private Double price;
    private List<Long> variants;

    public UpdateProductVariantPrices() {
    }

    public UpdateProductVariantPrices(Double price, List<Long> variants) {
        this.price = price;
        this.variants = variants;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Long> getVariants() {
        return variants;
    }

    public void setVariants(List<Long> variants) {
        this.variants = variants;
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

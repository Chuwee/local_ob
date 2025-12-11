package es.onebox.mgmt.products.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateProductVariantPricesDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    @Min(value = 0, message = "price must be above 0")
    private Double price;
    @NotNull(message = "product variants ids can not be null")
    private List<Long> variants;

    public UpdateProductVariantPricesDTO() {
    }

    public UpdateProductVariantPricesDTO(Double price, List<Long> variants) {
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

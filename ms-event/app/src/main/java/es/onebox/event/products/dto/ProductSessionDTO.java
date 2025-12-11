package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSessionDTO extends ProductSessionBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7409361650401759459L;

    private List<ProductSessionVariantDTO> variants;

    //TODO remove after migration
    private Long stock;
    private Boolean useCustomStock;
    private Boolean isSmartBooking;

    public List<ProductSessionVariantDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductSessionVariantDTO> variants) {
        this.variants = variants;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Boolean getUseCustomStock() {
        return useCustomStock;
    }

    public void setUseCustomStock(Boolean useCustomStock) {
        this.useCustomStock = useCustomStock;
    }

    public Boolean getIsSmartBooking() {
        return isSmartBooking;
    }

    public void setIsSmartBooking(Boolean isSmartBooking) {
        this.isSmartBooking = isSmartBooking;
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

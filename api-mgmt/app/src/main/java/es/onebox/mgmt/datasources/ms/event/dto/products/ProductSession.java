package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSession extends ProductSessionBase implements Serializable {

    @Serial
    private static final long serialVersionUID = -8061105847538611953L;

    private List<ProductSessionVariant> variants;
    private Long stock;
    private Boolean useCustomStock;
    private Boolean isSmartBooking;

    public List<ProductSessionVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductSessionVariant> variants) {
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

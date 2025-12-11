package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSessionDTO extends ProductSessionBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8061105847538611953L;

    private List<ProductSessionVariantDTO> variants;

    //TODO remove after migration
    private Long stock;
    @JsonProperty("use_custom_stock")
    private Boolean useCustomStock;
    @JsonProperty("smart_booking")
    private ProductSessionSmartBookingDTO smartBooking;

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

    public ProductSessionSmartBookingDTO getSmartBooking() {
        return smartBooking;
    }

    public void setSmartBooking(ProductSessionSmartBookingDTO smartBooking) {
        this.smartBooking = smartBooking;
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

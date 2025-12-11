package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateProductSessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5188874430917316838L;

    private List<ProductSessionVariantDTO> variants;

    //TODO remove after migration
    private Long stock;
    private Boolean useCustomStock;

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
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

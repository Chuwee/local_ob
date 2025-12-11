package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateProductSessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 531888060704702795L;

    private List<ProductSessionVariantDTO> variants;

    private Long stock;
    @JsonProperty("use_custom_stock")
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

}

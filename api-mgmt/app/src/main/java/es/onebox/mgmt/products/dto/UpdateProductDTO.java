package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.products.enums.ProductState;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private String name;
    @JsonProperty("product_state")
    private ProductState productState;
    @JsonProperty("tax_id")
    private Long taxId;
    @JsonProperty("surcharge_tax_id")
    private Long surchargeTaxId;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("ui_settings")
    ProductUISettingsDTO productUiSettings;
    private ProductSettingDTO settings;

    public UpdateProductDTO() {
    }

    public UpdateProductDTO(String name, ProductState productState, Long taxId,
                            Long surchargeTaxId, String currencyCode, ProductUISettingsDTO productUiSettings) {
        this.name = name;
        this.productState = productState;
        this.taxId = taxId;
        this.surchargeTaxId = surchargeTaxId;
        this.currencyCode = currencyCode;
        this.productUiSettings = productUiSettings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductState getProductState() {
        return productState;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public Long getSurchargeTaxId() {
        return surchargeTaxId;
    }

    public void setSurchargeTaxId(Long surchargeTaxId) {
        this.surchargeTaxId = surchargeTaxId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public ProductUISettingsDTO getProductUiSettings() {
        return productUiSettings;
    }

    public void setProductUiSettings(ProductUISettingsDTO productUiSettings) {
        this.productUiSettings = productUiSettings;
    }

    public ProductSettingDTO getSettings() {
        return settings;
    }

    public void setSettings(ProductSettingDTO settings) {
        this.settings = settings;
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

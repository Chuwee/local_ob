package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.TaxModeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

public class UpdateProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    @Length(max = 50, message = "product name max size is 50")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the name")
    private String name;
    private ProductState productState;
    private Long currencyId;
    private Long ticketTemplateId;
    private Long taxId;
    private Long surchargeTaxId;
    private Boolean hideDeliveryPoint;
    private Boolean hideDeliveryDateTime;
    private IdDTO category;
    private IdDTO customCategory;
    private TaxModeDTO taxMode;

    public UpdateProductDTO() {
    }

    public UpdateProductDTO(String name, ProductState productState, Long taxId, Long surchargeTaxId, Long currencyId,
                            Boolean hideDeliveryPoint, Boolean hideDeliveryDateTime) {
        this.name = name;
        this.productState = productState;
        this.taxId = taxId;
        this.surchargeTaxId = surchargeTaxId;
        this.currencyId = currencyId;
        this.hideDeliveryPoint = hideDeliveryPoint;
        this.hideDeliveryDateTime = hideDeliveryDateTime;
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

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getTicketTemplateId() {
        return ticketTemplateId;
    }

    public void setTicketTemplateId(Long ticketTemplateId) {
        this.ticketTemplateId = ticketTemplateId;
    }

    public Boolean getHideDeliveryPoint() {
        return hideDeliveryPoint;
    }

    public void setHideDeliveryPoint(Boolean hideDeliveryPoint) {
        this.hideDeliveryPoint = hideDeliveryPoint;
    }

    public Boolean getHideDeliveryDateTime() {
        return hideDeliveryDateTime;
    }

    public void setHideDeliveryDateTime(Boolean hideDeliveryDateTime) {
        this.hideDeliveryDateTime = hideDeliveryDateTime;
    }

    public IdDTO getCategory() {
        return category;
    }

    public void setCategory(IdDTO category) {
        this.category = category;
    }

    public IdDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(IdDTO customCategory) {
        this.customCategory = customCategory;
    }

    public TaxModeDTO getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxModeDTO taxMode) {
        this.taxMode = taxMode;
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

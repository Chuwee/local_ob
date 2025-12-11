package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.TaxMode;
import es.onebox.mgmt.products.enums.ProductState;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private String name;
    private ProductState productState;
    private Long taxId;
    private Long surchargeTaxId;
    private Long currencyId;
    private Long ticketTemplateId;
    private Boolean hideDeliveryPoint;
    private Boolean hideDeliveryDateTime;
    private IdDTO category;
    private IdDTO customCategory;
    private TaxMode taxMode;

    public UpdateProduct() {
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

    public TaxMode getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxMode taxMode) {
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

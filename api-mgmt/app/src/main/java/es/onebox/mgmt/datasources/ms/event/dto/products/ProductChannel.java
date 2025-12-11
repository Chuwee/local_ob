package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductChannel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO product;
    private ProductChannelInfo channel;
    private ProductSaleRequestsStatus saleRequestsStatus;
    private Boolean checkoutSuggestionEnabled;
    private Boolean standaloneEnabled;

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
    }

    public ProductChannelInfo getChannel() {
        return channel;
    }

    public void setChannel(ProductChannelInfo channel) {
        this.channel = channel;
    }

    public Boolean getCheckoutSuggestionEnabled() {
        return checkoutSuggestionEnabled;
    }

    public void setCheckoutSuggestionEnabled(Boolean checkoutSuggestionEnabled) {
        this.checkoutSuggestionEnabled = checkoutSuggestionEnabled;
    }

    public Boolean getStandaloneEnabled() {
        return standaloneEnabled;
    }

    public void setStandaloneEnabled(Boolean standaloneEnabled) {
        this.standaloneEnabled = standaloneEnabled;
    }

    public ProductSaleRequestsStatus getSaleRequestsStatus() {
        return saleRequestsStatus;
    }

    public void setSaleRequestsStatus(ProductSaleRequestsStatus saleRequestsStatus) {
        this.saleRequestsStatus = saleRequestsStatus;
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

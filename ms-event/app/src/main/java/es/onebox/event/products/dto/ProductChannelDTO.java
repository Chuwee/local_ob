package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.products.enums.SaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductChannelDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO product;
    private ProductChannelInfoDTO channel;
    private SaleRequestsStatus saleRequestsStatus;
    private Boolean checkoutSuggestionEnabled;
    private Boolean standaloneEnabled;

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
    }

    public ProductChannelInfoDTO getChannel() {
        return channel;
    }

    public void setChannel(ProductChannelInfoDTO channel) {
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

    public SaleRequestsStatus getSaleRequestsStatus() {
        return saleRequestsStatus;
    }

    public void setSaleRequestsStatus(SaleRequestsStatus saleRequestsStatus) {
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

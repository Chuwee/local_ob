package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductChannelDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO product;
    private ProductChannelInfoDTO channel;
    private ChannelLanguagesDTO languages;

    @JsonProperty("checkout_suggestion_enabled")
    private Boolean checkoutSuggestionEnabled;

    @JsonProperty("standalone_enabled")
    private Boolean standaloneEnabled;

    @JsonProperty("sale_request_status")
    private ProductSaleRequestsStatus saleRequestsStatus;

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

    public ProductSaleRequestsStatus getSaleRequestsStatus() {
        return saleRequestsStatus;
    }

    public void setSaleRequestsStatus(ProductSaleRequestsStatus saleRequestsStatus) {
        this.saleRequestsStatus = saleRequestsStatus;
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
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

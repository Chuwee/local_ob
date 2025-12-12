package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.SaleRequestsStatus;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

public class ProductChannelDTO implements Serializable  {
    @Serial
    private static final long serialVersionUID = -1384084149115108268L;

    private IdNameDTO product;
    private ProductChannelInfoDTO channel;
    private SaleRequestsStatus saleRequestsStatus;
    private Boolean checkoutSuggestionEnabled;
    private Boolean standaloneEnabled;

    public IdNameDTO getProduct() { return product; }

    public void setProduct(IdNameDTO product) { this.product = product; }

    public ProductChannelInfoDTO getChannel() { return channel; }

    public void setChannel(ProductChannelInfoDTO channel) { this.channel = channel; }

    public SaleRequestsStatus getSaleRequestsStatus() { return saleRequestsStatus; }

    public void setSaleRequestsStatus(SaleRequestsStatus saleRequestsStatus) { this.saleRequestsStatus = saleRequestsStatus; }

    public Boolean getCheckoutSuggestionEnabled() {return checkoutSuggestionEnabled;}

    public void setCheckoutSuggestionEnabled(Boolean checkoutSuggestionEnabled) {this.checkoutSuggestionEnabled = checkoutSuggestionEnabled;}

    public Boolean getStandaloneEnabled() {return standaloneEnabled;}

    public void setStandaloneEnabled(Boolean standaloneEnabled) {this.standaloneEnabled = standaloneEnabled;}

}

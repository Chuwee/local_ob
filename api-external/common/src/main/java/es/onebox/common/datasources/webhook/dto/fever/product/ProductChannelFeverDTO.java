package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.SaleRequestsStatus;

import java.io.Serial;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductChannelFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5946377504282146980L;

    private Long productId;
    private Long channelId;
    private SaleRequestsStatus saleRequestsStatus;
    private Boolean checkoutSuggestionEnabled;
    private Boolean standaloneEnabled;


    public Long getProductId() { return productId; }

    public void setProductId(Long productId) { this.productId = productId; }

    public Long getChannelId() { return channelId; }

    public void setChannelId(Long channelId) { this.channelId = channelId; }

    public SaleRequestsStatus getSaleRequestsStatus() { return saleRequestsStatus; }

    public void setSaleRequestsStatus(SaleRequestsStatus saleRequestsStatus) { this.saleRequestsStatus = saleRequestsStatus; }

    public Boolean getCheckoutSuggestionEnabled() {return checkoutSuggestionEnabled;}

    public void setCheckoutSuggestionEnabled(Boolean checkoutSuggestionEnabled) {this.checkoutSuggestionEnabled = checkoutSuggestionEnabled;}

    public Boolean getStandaloneEnabled() {return standaloneEnabled;}

    public void setStandaloneEnabled(Boolean standaloneEnabled) {this.standaloneEnabled = standaloneEnabled;}
}


package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductSaleRequestDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = -614868422962483684L;

    private Long id;
    private ProductSaleRequestsStatus status;
    private ZonedDateTime date;
    private ChannelSaleRequestDetail channel;
    private ProductDetail product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductSaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(ProductSaleRequestsStatus status) {
        this.status = status;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ChannelSaleRequestDetail getChannel() {
        return channel;
    }

    public void setChannel(ChannelSaleRequestDetail channel) {
        this.channel = channel;
    }

    public ProductDetail getProduct() {
        return product;
    }

    public void setProduct(ProductDetail product) {
        this.product = product;
    }
}

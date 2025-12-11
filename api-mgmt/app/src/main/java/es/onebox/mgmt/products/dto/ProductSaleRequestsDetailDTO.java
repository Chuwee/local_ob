package es.onebox.mgmt.products.dto;

import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import es.onebox.mgmt.salerequests.dto.ChannelSaleRequestDetailDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductSaleRequestsDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8357590398439174058L;

    private Long id;
    private ProductSaleRequestsStatus status;
    private ZonedDateTime date;
    private ChannelLanguagesDTO languages;
    private ChannelSaleRequestDetailDTO channel;
    private ProductDetailDTO product;

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

    public ChannelSaleRequestDetailDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelSaleRequestDetailDTO channel) {
        this.channel = channel;
    }

    public ProductDetailDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDetailDTO product) {
        this.product = product;
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
    }
}


package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductSaleRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8357590398439174058L;

    private Long id;
    private ProductSaleRequestsStatus status;
    @JsonProperty("request_date")
    private ZonedDateTime requestDate;
    private IdNameDTO channel;
    private IdNameDTO product;
    private IdNameDTO producer;

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

    public ZonedDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(ZonedDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public IdNameDTO getChannel() {
        return channel;
    }

    public void setChannel(IdNameDTO channel) {
        this.channel = channel;
    }

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
    }

    public IdNameDTO getProducer() {
        return producer;
    }

    public void setProducer(IdNameDTO producer) {
        this.producer = producer;
    }
}

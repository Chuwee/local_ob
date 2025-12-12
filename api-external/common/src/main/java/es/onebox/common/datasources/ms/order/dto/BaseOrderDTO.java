package es.onebox.common.datasources.ms.order.dto;

import es.onebox.dal.dto.couch.order.ExternalOrderInvoiceDataDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseOrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -331284563175870764L;

    private OrderStatusDTO status;
    private OrderDateDTO date = new OrderDateDTO();
    private OrderDataDTO orderData = new OrderDataDTO();
    private OrderInvoiceDataDTO invoiceData  = new OrderInvoiceDataDTO();
    private List<ExternalOrderInvoiceDataDTO> externalInvoiceData;
    private List<OrderGroupDTO> groups = new ArrayList<>();
    private OrderPriceDTO price = new OrderPriceDTO();
    private List<OrderPaymentDTO> payments;
    private List<OrderCollectiveDTO> collectives = new ArrayList();
    private List<OrderProductDTO> products = new ArrayList<>();
    private OrderUserDTO customer;
    private String relatedOriginalCode;
    private String relatedModicationCode;
    private ClientDTO client;

    public OrderStatusDTO getStatus() {
        return status;
    }

    public void setStatus(OrderStatusDTO status) {
        this.status = status;
    }

    public OrderDataDTO getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderDataDTO orderData) {
        this.orderData = orderData;
    }

    public OrderInvoiceDataDTO getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(OrderInvoiceDataDTO invoiceData) {
        this.invoiceData = invoiceData;
    }

    public OrderDateDTO getDate() {
        return date;
    }

    public void setDate(OrderDateDTO date) {
        this.date = date;
    }

    public List<OrderGroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<OrderGroupDTO> groups) {
        this.groups = groups;
    }

    public OrderPriceDTO getPrice() {
        return price;
    }

    public void setPrice(OrderPriceDTO price) {
        this.price = price;
    }

    public List<OrderCollectiveDTO> getCollectives() {
        return collectives;
    }

    public void setCollectives(List<OrderCollectiveDTO> collectives) {
        this.collectives = collectives;
    }

    public List<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductDTO> products) {
        this.products = products;
    }

    public OrderUserDTO getCustomer() {
        return customer;
    }

    public void setCustomer(OrderUserDTO customer) {
        this.customer = customer;
    }

    public String getRelatedOriginalCode() {
        return relatedOriginalCode;
    }

    public void setRelatedOriginalCode(String relatedOriginalCode) {
        this.relatedOriginalCode = relatedOriginalCode;
    }

    public String getRelatedModicationCode() {
        return relatedModicationCode;
    }

    public void setRelatedModicationCode(String relatedModicationCode) {
        this.relatedModicationCode = relatedModicationCode;
    }

    public List<ExternalOrderInvoiceDataDTO> getExternalInvoiceData() {
        return externalInvoiceData;
    }

    public void setExternalInvoiceData(List<ExternalOrderInvoiceDataDTO> externalInvoiceData) {
        this.externalInvoiceData = externalInvoiceData;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public List<OrderPaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<OrderPaymentDTO> payments) {
        this.payments = payments;
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

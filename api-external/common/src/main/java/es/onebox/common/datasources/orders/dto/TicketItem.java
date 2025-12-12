package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.dto.Attribute;
import es.onebox.common.datasources.orders.enums.DeliveryMethod;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class TicketItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Product product;

    @JsonProperty("code")
    private String orderCode;
    @JsonProperty("datePurchased")
    private ZonedDateTime purchaseDate;
    private Integer channelId;
    private Long channelEntityId;
    private Integer productsSize;
    private Integer insuranceId;
    private DeliveryMethod deliveryMethod;
    private OrderStatus status;
    private String relatedOriginalCode;
    private List<Attribute> groupAttributes;

    private String groupName;
    private Integer numAssistants;
    private Integer numAccompanists;
    
    private transient Map<String, Object> customer;
    private Client client;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getProductsSize() {
        return productsSize;
    }

    public void setProductsSize(Integer productsSize) {
        this.productsSize = productsSize;
    }

    public Integer getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Integer insuranceId) {
        this.insuranceId = insuranceId;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getRelatedOriginalCode() {
        return relatedOriginalCode;
    }

    public void setRelatedOriginalCode(String relatedOriginalCode) {
        this.relatedOriginalCode = relatedOriginalCode;
    }

    public List<Attribute> getGroupAttributes() {
        return groupAttributes;
    }

    public void setGroupAttributes(List<Attribute> groupAttributes) {
        this.groupAttributes = groupAttributes;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getNumAssistants() {
        return numAssistants;
    }

    public void setNumAssistants(Integer numAssistants) {
        this.numAssistants = numAssistants;
    }

    public Integer getNumAccompanists() {
        return numAccompanists;
    }

    public void setNumAccompanists(Integer numAccompanists) {
        this.numAccompanists = numAccompanists;
    }

    public Map<String, Object> getCustomer() {
        return customer;
    }

    public void setCustomer(Map<String, Object> customer) {
        this.customer = customer;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(Long channelEntityId) {
        this.channelEntityId = channelEntityId;
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

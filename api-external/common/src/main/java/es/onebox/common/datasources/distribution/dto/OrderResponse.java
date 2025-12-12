package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.common.datasources.distribution.dto.order.OrderDeliveryMethod;
import es.onebox.common.datasources.distribution.dto.order.OrderPromotion;
import es.onebox.common.datasources.distribution.dto.order.OrderStatus;
import es.onebox.common.datasources.distribution.dto.order.OrderType;
import es.onebox.common.datasources.distribution.dto.order.buyerdata.BuyerDataDescriptor;
import es.onebox.common.datasources.distribution.dto.order.items.OrderItem;
import es.onebox.common.datasources.distribution.dto.order.items.promotion.CollectiveApplied;
import es.onebox.common.datasources.distribution.dto.order.price.Price;
import es.onebox.common.datasources.distribution.dto.order.voucher.Voucher;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class OrderResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 5491892066269602514L;
    @JsonProperty("id")
    private String id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("date")
    private ZonedDateTime date;
    @JsonProperty("external_code")
    private String externalCode;
    @JsonProperty("type")
    private OrderType type;
    @JsonProperty("status")
    private OrderStatus status;
    @JsonProperty("language_code")
    private String languageCode;
    @JsonProperty("items")
    private List<OrderItem> items;
    @JsonProperty("buyer_data")
    private Map<String, Object> buyerData;
    @JsonProperty("delivery_method")
    private OrderDeliveryMethod deliveryMethod;
    @JsonProperty("insurance")
    private IdNameDTO insurance;
    @JsonProperty("voucher")
    private Voucher voucher;
    @JsonProperty("promotions")
    private List<OrderPromotion> orderPromotions;
    @JsonProperty("presales")
    private List<Presale> presales;
    @JsonProperty("activators")
    private List<CollectiveApplied> activators;
    @JsonProperty("price")
    private Price price;
    @JsonProperty("consumer_metadata")
    private Map<String, String> consumerMetadata;
    private String traceId;

    public Object getBuyerDataItem(BuyerDataDescriptor buyerDataDescriptor) {
        if (this.buyerData.containsKey(buyerDataDescriptor.apiAttribute())) {
            return this.buyerData.get(buyerDataDescriptor.apiAttribute());
        }
        return null;
    }

    public OrderResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Map<String, Object> getBuyerData() {
        return buyerData;
    }

    public void setBuyerData(Map<String, Object> buyerData) {
        this.buyerData = buyerData;
    }

    public OrderDeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(OrderDeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public IdNameDTO getInsurance() {
        return insurance;
    }

    public void setInsurance(IdNameDTO insurance) {
        this.insurance = insurance;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public List<OrderPromotion> getOrderPromotions() {
        return orderPromotions;
    }

    public void setOrderPromotions(List<OrderPromotion> orderPromotions) {
        this.orderPromotions = orderPromotions;
    }

    public List<Presale> getPresales() {
        return presales;
    }

    public void setPresales(List<Presale> presales) {
        this.presales = presales;
    }

    public List<CollectiveApplied> getActivators() {
        return activators;
    }

    public void setActivators(List<CollectiveApplied> activators) {
        this.activators = activators;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Map<String, String> getConsumerMetadata() {
        return consumerMetadata;
    }

    public void setConsumerMetadata(Map<String, String> consumerMetadata) {
        this.consumerMetadata = consumerMetadata;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
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

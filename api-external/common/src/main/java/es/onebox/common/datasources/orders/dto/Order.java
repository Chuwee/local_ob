package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.dto.Channel;
import es.onebox.common.datasources.common.enums.OrderType;
import es.onebox.common.datasources.orders.enums.DeliveryType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class Order extends BaseOrder {
    @Serial
    private static final long serialVersionUID = 1L;

    private OrderType type;
    private DeliveryType delivery;
    private String language;
    private Channel channel;
    @JsonProperty("buyer_data")
    private Map<String, Object> buyerData = new HashMap<>();
    private OrderPrice price;

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public DeliveryType getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryType delivery) {
        this.delivery = delivery;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public OrderPrice getPrice() {
        return price;
    }

    public void setPrice(OrderPrice price) {
        this.price = price;
    }

    public Map<String, Object> getBuyerData() {
        return buyerData;
    }

    public void setBuyerData(Map<String, Object> buyerData) {
        this.buyerData = buyerData;
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

package es.onebox.common.datasources.orderitems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.dto.Channel;
import es.onebox.common.datasources.orderitems.dto.action.OrderItemAction;
import es.onebox.common.datasources.orderitems.dto.pack.OrderItemPack;
import es.onebox.common.datasources.orderitems.dto.transfer.OrderItemTransfer;
import es.onebox.common.datasources.orderitems.enums.OrderItemRelatedProductState;
import es.onebox.common.datasources.orderitems.enums.OrderItemState;
import es.onebox.common.datasources.orderitems.enums.OrderItemType;
import es.onebox.common.datasources.orders.dto.BaseOrder;
import es.onebox.common.datasources.orders.dto.ItemPrice;
import es.onebox.common.datasources.orders.dto.OrderRelated;
import es.onebox.common.datasources.orders.dto.VenueLocation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OrderItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private OrderItemType type;

    private OrderItemState state;
    @JsonProperty("related_product_state")
    private OrderItemRelatedProductState relatedProductState;

    @JsonProperty("order_related")
    private OrderRelated orderRelated;

    private BaseOrder order;

    @JsonProperty("buyer_data")
    private transient Map<String, Object> buyerData = new HashMap<>();

    private Channel channel;

    private ItemTicket ticket;

    private ItemPrice price;

    private transient Map<String, Object> attendant = new HashMap<>();

    private VenueLocation location;

    private OrderItemPack pack;
    private OrderItemTransfer transfer;
    private OrderItemAction action;
    @JsonProperty("user_id")
    private String userId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItemType getType() {
        return type;
    }

    public void setType(OrderItemType type) {
        this.type = type;
    }

    public BaseOrder getOrder() {
        return order;
    }

    public void setOrder(BaseOrder order) {
        this.order = order;
    }

    public Map<String, Object> getBuyerData() {
        return buyerData;
    }

    public void setBuyerData(Map<String, Object> buyerData) {
        this.buyerData = buyerData;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ItemTicket getTicket() {
        return ticket;
    }

    public void setTicket(ItemTicket ticket) {
        this.ticket = ticket;
    }

    public ItemPrice getPrice() {
        return price;
    }

    public void setPrice(ItemPrice price) {
        this.price = price;
    }

    public OrderItemState getState() {
        return state;
    }

    public void setState(OrderItemState state) {
        this.state = state;
    }

    public OrderItemRelatedProductState getRelatedProductState() {
        return relatedProductState;
    }

    public void setRelatedProductState(OrderItemRelatedProductState relatedProductState) {
        this.relatedProductState = relatedProductState;
    }

    public OrderRelated getOrderRelated() {
        return orderRelated;
    }

    public void setOrderRelated(OrderRelated orderRelated) {
        this.orderRelated = orderRelated;
    }

    public Map<String, Object> getAttendant() {
        return attendant;
    }

    public void setAttendant(Map<String, Object> attendant) {
        this.attendant = attendant;
    }

    public VenueLocation getLocation() {
        return location;
    }

    public void setLocation(VenueLocation location) {
        this.location = location;
    }

    public OrderItemPack getPack() {
        return pack;
    }

    public void setPack(OrderItemPack pack) {
        this.pack = pack;
    }

    public OrderItemTransfer getTransfer() {
        return transfer;
    }

    public void setTransfer(OrderItemTransfer transfer) {
        this.transfer = transfer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderItemAction getAction() {
        return action;
    }

    public void setAction(OrderItemAction action) {
        this.action = action;
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

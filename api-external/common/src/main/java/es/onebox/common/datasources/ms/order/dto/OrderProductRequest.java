package es.onebox.common.datasources.ms.order.dto;

import es.onebox.common.datasources.ms.order.enums.OrderActionTypeSupport;
import es.onebox.common.datasources.ms.order.enums.TicketFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OrderProductRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long userId;
    private Long channelId;
    private TicketFormat format;
    private OrderActionTypeSupport type;
    private transient Map<String, Object> customAttrs;
    @NotNull
    private List<Long> productsId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public TicketFormat getFormat() {
        return format;
    }

    public void setFormat(TicketFormat format) {
        this.format = format;
    }

    public OrderActionTypeSupport getType() {
        return type;
    }

    public void setType(OrderActionTypeSupport type) {
        this.type = type;
    }

    public Map<String, Object> getCustomAttrs() {
        return customAttrs;
    }

    public void setCustomAttrs(Map<String, Object> customAttrs) {
        this.customAttrs = customAttrs;
    }

    public List<Long> getProductsId() {
        return productsId;
    }

    public void setProductsId(List<Long> productsId) {
        this.productsId = productsId;
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

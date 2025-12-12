package es.onebox.common.datasources.ms.order.dto;

import es.onebox.common.datasources.ms.order.enums.OrderActionTypeSupport;
import es.onebox.common.datasources.ms.order.enums.TicketFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

public class OrderProductAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String txId;
    private Long userId;
    private Long channelId;
    private transient Map<String, Object> customAttrs;
    private ZonedDateTime date;
    private TicketFormat format;
    private OrderActionTypeSupport type;

    public String getTxId() {
        return this.txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Map<String, Object> getCustomAttrs() {
        return this.customAttrs;
    }

    public void setCustomAttrs(Map<String, Object> customAttrs) {
        this.customAttrs = customAttrs;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public TicketFormat getFormat() {
        return this.format;
    }

    public void setFormat(TicketFormat format) {
        this.format = format;
    }

    public OrderActionTypeSupport getType() {
        return this.type;
    }

    public void setType(OrderActionTypeSupport type) {
        this.type = type;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[0]);
    }
}

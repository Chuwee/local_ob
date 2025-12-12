package es.onebox.ms.notification.webhooks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.ms.notification.webhooks.enums.NotificationSubtype;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ItemPayloadDTO extends PayloadRequest{

    @JsonProperty("code")
    private String orderCode;
    @JsonProperty("item_id")
    private Long itemId;
    private String url;
    private NotificationSubtype notificationSubtype;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NotificationSubtype getNotificationSubtype() {
        return notificationSubtype;
    }

    public void setNotificationSubtype(NotificationSubtype notificationSubtype) {
        this.notificationSubtype = notificationSubtype;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}

package es.onebox.ms.notification.webhooks.dto;

import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.ms.notification.webhooks.enums.NotificationSortableField;
import es.onebox.ms.notification.webhooks.enums.NotificationVisible;
import es.onebox.ms.notification.webhooks.enums.NotificationsScope;
import es.onebox.ms.notification.webhooks.enums.NotificationsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class SearchNotificationConfigFilterDTO {

    private static final long serialVersionUID = 1L;

    private Long entityId;
    private String channelId;
    private Long operatorId;
    private NotificationsStatus status;
    private List<NotificationsScope> scope;
    private NotificationVisible visible;
    private Long offset;
    private Long limit;
    private SortOperator<NotificationSortableField> sort;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public NotificationsStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationsStatus status) {
        this.status = status;
    }

    public List<NotificationsScope> getScope() {return scope;}

    public void setScope(List<NotificationsScope> scope) {this.scope = scope;}

    public NotificationVisible getVisible() {
        return visible;
    }

    public void setVisible(NotificationVisible visible) {
        this.visible = visible;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public SortOperator<NotificationSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<NotificationSortableField> sort) {
        this.sort = sort;
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


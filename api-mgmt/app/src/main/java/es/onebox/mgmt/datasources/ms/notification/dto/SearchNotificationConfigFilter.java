package es.onebox.mgmt.datasources.ms.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;
import es.onebox.mgmt.notifications.enums.NotificationsVisible;

import java.io.Serializable;
import java.util.List;

public class SearchNotificationConfigFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long entityId;
    private Long channelId;
    private Long operatorId;
    private NotificationsStatus status;
    private List<NotificationsScope> scope;
    private NotificationsVisible visible;
    private SortOperator<String> sort;
    private Long offset;
    private Long limit;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
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

    public NotificationsVisible getVisible() {
        return visible;
    }

    public void setVisible(NotificationsVisible visible) {
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

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }
}

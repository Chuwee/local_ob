package es.onebox.mgmt.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.notifications.enums.NotificationsScope;
import es.onebox.mgmt.notifications.enums.NotificationsStatus;
import es.onebox.mgmt.notifications.enums.NotificationsVisible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(20)
@DefaultLimit(20)
public class SearchNotificationConfigFilterDTO extends BaseEntityRequestFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("operator_id")
    private Long operatorId;
    private NotificationsStatus status;
    private List<NotificationsScope> scope;
    private NotificationsVisible visible;
    private SortOperator<String> sort;

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}


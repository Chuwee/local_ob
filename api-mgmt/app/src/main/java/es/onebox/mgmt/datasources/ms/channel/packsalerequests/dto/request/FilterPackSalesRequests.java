package es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackChannelSaleRequestStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@MaxLimit(50)
@DefaultLimit(50)
public class FilterPackSalesRequests extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -2280063800391554419L;

    private Long operatorId;
    private List<Long> channelId;
    private List<Long> packId;
    private List<Long> channelEntityId;
    private List<Long> packEntityId;
    private Long entityAdminId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> date;
    private String q;
    private List<PackChannelSaleRequestStatus> status;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<Long> getChannelId() {
        return channelId;
    }

    public void setChannelId(List<Long> channelId) {
        this.channelId = channelId;
    }

    public List<Long> getPackId() {
        return packId;
    }

    public void setPackId(List<Long> packId) {
        this.packId = packId;
    }

    public List<Long> getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(List<Long> channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public List<Long> getPackEntityId() {
        return packEntityId;
    }

    public void setPackEntityId(List<Long> packEntityId) {
        this.packEntityId = packEntityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<FilterWithOperator<ZonedDateTime>> getDate() {
        return date;
    }

    public void setDate(List<FilterWithOperator<ZonedDateTime>> date) {
        this.date = date;
    }

    public List<PackChannelSaleRequestStatus> getStatus() {
        return status;
    }

    public void setStatus(List<PackChannelSaleRequestStatus> status) {
        this.status = status;
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

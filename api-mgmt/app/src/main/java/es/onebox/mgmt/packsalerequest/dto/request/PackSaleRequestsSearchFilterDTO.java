package es.onebox.mgmt.packsalerequest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;


@DefaultLimit(50)
@MaxLimit(50)
public class PackSaleRequestsSearchFilterDTO extends BaseEntityRequestFilter implements Serializable {

    private List<Long> channelEntityId;
    @JsonProperty("request_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> date;
    private String q;
    private List<PackSaleRequestStatus> status;

    public List<Long> getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(List<Long> channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public List<FilterWithOperator<ZonedDateTime>> getDate() {
        return date;
    }

    public void setDate(List<FilterWithOperator<ZonedDateTime>> date) {
        this.date = date;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<PackSaleRequestStatus> getStatus() {
        return status;
    }

    public void setStatus(List<PackSaleRequestStatus> status) {
        this.status = status;
    }

    @Serial
    private static final long serialVersionUID = 6912069845067165175L;


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

package es.onebox.event.catalog.elasticsearch.dto.channelpack;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.event.packs.enums.PackStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class ChannelPackFilter extends BaseRequestFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final long DEFAULT_MAX_LIMIT = 100L;

    private PackStatus status;
    private Boolean forSale;
    private Boolean onSale;
    private String customCategoryCode;
    private String q;
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    private List<Long> eventId;
    private List<Long> sessionId;
    private Boolean main;
    private Boolean suggested;

    public PackStatus getStatus() {
        return status;
    }

    public void setStatus(PackStatus status) {
        this.status = status;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public String getCustomCategoryCode() {
        return customCategoryCode;
    }

    public void setCustomCategoryCode(String customCategoryCode) {
        this.customCategoryCode = customCategoryCode;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public List<Long> getSessionId() {
        return sessionId;
    }

    public void setSessionId(List<Long> sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
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

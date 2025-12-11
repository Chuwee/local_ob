package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class SessionLinksFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 2129169186602354647L;
    private SortOperator<String> sort;

    @NotNull(message = "session_status can not be null")
    @JsonProperty("session_status")
    private List<SessionStatus> sessionStatus;

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<SessionStatus> getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(List<SessionStatus> sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    private SessionLinksFilter(Builder builder) {
        if (builder.offset != null) {
            super.setOffset(builder.offset);
        }
        if (builder.limit != null) {
            super.setLimit(builder.limit);
        }
        if (builder.sessionStatus != null) {
            this.setSessionStatus(builder.sessionStatus);
        }
        if (builder.sort != null) {
            this.setSort(builder.sort);
        }
    }

    public SessionLinksFilter() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(SessionLinksFilter eventChannelSearchFilter) {
        return new Builder(eventChannelSearchFilter);
    }

    @JsonIgnoreType
    public static final class Builder {
        private Long offset;
        private Long limit;
        private SortOperator<String> sort;
        private List<SessionStatus> sessionStatus;
        private Builder() {
        }
        private Builder(SessionLinksFilter filter) {
            this.limit = filter.getLimit();
            this.offset = filter.getOffset();
            this.sessionStatus = filter.getSessionStatus();
            this.sort = filter.getSort();
        }

        public Builder offset(Long offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Builder sessionStatus(List<SessionStatus> sessionStatus) {
            this.sessionStatus = sessionStatus;
            return this;
        }

        public Builder sort(SortOperator<String> sort) {
            this.sort = sort;
            return this;
        }

        public SessionLinksFilter build() {
            return new SessionLinksFilter(this);
        }
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

package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.channels.enums.ReleaseStatusType;
import es.onebox.mgmt.channels.enums.RequestStatusType;
import es.onebox.mgmt.channels.enums.SaleStatusType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.EnumSet;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class EventChannelSearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 7263497955501858551L;
    private SortOperator<String> sort;
    private List<String> fields;
    private EnumSet<ChannelSubtype> type;
    @JsonProperty("entity_id")
    private Long entityId;
    private String q;
    @JsonProperty("request_status")
    private EnumSet<RequestStatusType> requestStatusType;
    @JsonProperty("sale_status")
    private EnumSet<SaleStatusType> saleStatusType;
    @JsonProperty("release_status")
    private EnumSet<ReleaseStatusType> releaseStatusType;

    private EventChannelSearchFilter(Builder builder) {
        this.sort = builder.sort;
        this.fields = builder.fields;
        this.type = builder.type;
        this.entityId = builder.entityId;
        this.q = builder.name;
        this.requestStatusType = builder.requestStatusType;
        this.saleStatusType = builder.saleStatusType;
        this.releaseStatusType = builder.releaseStatusType;
        if (builder.offset != null) {
            super.setOffset(builder.offset);
        }
        if (builder.limit != null) {
            super.setLimit(builder.limit);
        }
    }

    public EventChannelSearchFilter() {
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public EnumSet<ChannelSubtype> getType() {
        return type;
    }

    public void setType(EnumSet<ChannelSubtype> type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public static Builder builder() {
        return new Builder();
    }

    public EnumSet<RequestStatusType> getRequestStatusType() {
        return requestStatusType;
    }

    public void setRequestStatusType(EnumSet<RequestStatusType> requestStatusType) {
        this.requestStatusType = requestStatusType;
    }

    public EnumSet<SaleStatusType> getSaleStatusType() {
        return saleStatusType;
    }

    public void setSaleStatusType(EnumSet<SaleStatusType> saleStatusType) {
        this.saleStatusType = saleStatusType;
    }

    public EnumSet<ReleaseStatusType> getReleaseStatusType() {
        return releaseStatusType;
    }

    public void setReleaseStatusType(EnumSet<ReleaseStatusType> releaseStatusType) {
        this.releaseStatusType = releaseStatusType;
    }

    public static Builder builderFrom(EventChannelSearchFilter eventChannelSearchFilter) {
        return new Builder(eventChannelSearchFilter);
    }

    @JsonIgnoreType
    public static final class Builder {
        private SortOperator<String> sort;
        private List<String> fields;
        private Long offset;
        private Long limit;
        private EnumSet<ChannelSubtype> type;
        private Long entityId;
        private String name;
        private EnumSet<RequestStatusType> requestStatusType;
        private EnumSet<SaleStatusType> saleStatusType;
        private EnumSet<ReleaseStatusType> releaseStatusType;

        private Builder() {
        }

        private Builder(EventChannelSearchFilter filter) {
            this.sort = filter.sort;
            this.fields = filter.fields;
            this.limit = filter.getLimit();
            this.offset = filter.getOffset();
            this.type = filter.type;
            this.entityId = filter.entityId;
            this.name = filter.q;
            this.requestStatusType = filter.requestStatusType;
            this.saleStatusType = filter.saleStatusType;
            this.releaseStatusType = filter.releaseStatusType;
        }

        public Builder type(EnumSet<ChannelSubtype> type) {
            this.type = type;
            return this;
        }

        public Builder sort(SortOperator<String> sort) {
            this.sort = sort;
            return this;
        }

        public Builder offset(Long offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Builder fields(List<String> fields) {
            this.fields = fields;
            return this;
        }

        public Builder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder requestStatusType(EnumSet<RequestStatusType> requestStatusType) {
            this.requestStatusType = requestStatusType;
            return this;
        }

        public Builder saleStatusType(EnumSet<SaleStatusType> saleStatusType) {
            this.saleStatusType = saleStatusType;
            return this;
        }

        public Builder releaseStatusType(EnumSet<ReleaseStatusType> releaseStatusType) {
            this.releaseStatusType = releaseStatusType;
            return this;
        }

        public EventChannelSearchFilter build() {
            return new EventChannelSearchFilter(this);
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

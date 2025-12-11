package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@MaxLimit(1000)
@DefaultLimit(50)
public class VenueSearchAggFilter extends BaseEntityRequestFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("include_third_party_venues")
    private Boolean includeThirdPartyVenues;

    @JsonProperty("include_own_template_venues")
    private Boolean includeOwnTemplateVenues;

    @JsonProperty("only_in_use_venues")
    private Boolean onlyInUseVenues;

    private SortOperator<String> sort;

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public Boolean getIncludeThirdPartyVenues() {
        return includeThirdPartyVenues;
    }

    public void setIncludeThirdPartyVenues(Boolean includeThirdPartyVenues) {
        this.includeThirdPartyVenues = includeThirdPartyVenues;
    }

    public Boolean getIncludeOwnTemplateVenues() {
        return includeOwnTemplateVenues;
    }

    public void setIncludeOwnTemplateVenues(Boolean includeOwnTemplateVenues) {
        this.includeOwnTemplateVenues = includeOwnTemplateVenues;
    }

    public Boolean getOnlyInUseVenues() {
        return onlyInUseVenues;
    }

    public void setOnlyInUseVenues(Boolean onlyInUseVenues) {
        this.onlyInUseVenues = onlyInUseVenues;
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

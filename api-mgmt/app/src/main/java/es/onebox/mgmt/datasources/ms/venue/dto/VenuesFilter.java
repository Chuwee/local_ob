package es.onebox.mgmt.datasources.ms.venue.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class VenuesFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private Long operatorId;
    private Long entityId;
    private String freeSearch;
    private Boolean grouped;
    private Boolean includeThirdPartyVenues;
    private Boolean includeOwnTemplateVenues;
    private Boolean onlyInUseVenues;
    private String countryCode;
    private String city;
    private List<Long> visibleEntities;
    private Long entityAdminId;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public Boolean getGrouped() {
        return grouped;
    }

    public void setGrouped(Boolean grouped) {
        this.grouped = grouped;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setVisibleEntities(List<Long> visibleEntities) {
        this.visibleEntities = visibleEntities;
    }

    public List<Long> getVisibleEntities() {
        return visibleEntities;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
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

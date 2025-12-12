package es.onebox.common.datasources.ms.venue.dto;

import es.onebox.common.datasources.ms.venue.enums.VenueTemplateScope;
import es.onebox.common.datasources.ms.venue.enums.VenueTemplateStatus;
import es.onebox.common.datasources.ms.venue.enums.VenueTemplateType;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class VenueTemplatesFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 2L;

    private Long entityId;
    private Long eventId;
    private Long venueId;
    private Long venueEntityId;
    private String venueCity;
    private String venueCountry;
    private Boolean isPublic;
    private Boolean graphic;
    private List<VenueTemplateType> templateType;
    private List<VenueTemplateScope> scope;
    private List<VenueTemplateStatus> status;
    private Boolean includeThirdParty;
    private String freeSearch;
    private List<Long> visibleEntities;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Long getVenueEntityId() {
        return venueEntityId;
    }

    public void setVenueEntityId(Long venueEntityId) {
        this.venueEntityId = venueEntityId;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public String getVenueCountry() {
        return venueCountry;
    }

    public void setVenueCountry(String venueCountry) {
        this.venueCountry = venueCountry;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getGraphic() {
        return graphic;
    }

    public void setGraphic(Boolean graphic) {
        this.graphic = graphic;
    }

    public List<VenueTemplateType> getTemplateType() {
        return templateType;
    }

    public void setTemplateType(List<VenueTemplateType> templateType) {
        this.templateType = templateType;
    }

    public List<VenueTemplateScope> getScope() {
        return scope;
    }

    public void setScope(List<VenueTemplateScope> scope) {
        this.scope = scope;
    }

    public List<VenueTemplateStatus> getStatus() {
        return status;
    }

    public void setStatus(List<VenueTemplateStatus> status) {
        this.status = status;
    }

    public Boolean getIncludeThirdParty() {
        return includeThirdParty;
    }

    public void setIncludeThirdParty(Boolean includeThirdParty) {
        this.includeThirdParty = includeThirdParty;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public List<Long> getVisibleEntities() {
        return visibleEntities;
    }

    public void setVisibleEntities(List<Long> visibleEntities) {
        this.visibleEntities = visibleEntities;
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

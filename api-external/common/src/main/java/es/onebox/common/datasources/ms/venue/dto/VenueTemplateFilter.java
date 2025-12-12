package es.onebox.common.datasources.ms.venue.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class VenueTemplateFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 7364548523270209040L;
    private Integer operatorId;
    private Integer entityId;
    private Long entityAdminId;
    private Integer eventId;
    private Long venueId;
    private Long venueEntityId;
    private String venueCity;
    private String venueCountry;
    private Boolean isPublic;
    private Boolean graphic;
    private Boolean hasAvetMapping;
    private Boolean includeThirdParty;
    private Boolean hasGenerationSequence;
    private String freeSearch;
    private List<Long> visibleEntities;
    private List<String> fields;
    private SortOperator<String> sort;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
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

    public Boolean getHasAvetMapping() {
        return hasAvetMapping;
    }

    public void setHasAvetMapping(Boolean hasAvetMapping) {
        this.hasAvetMapping = hasAvetMapping;
    }

    public Boolean getIncludeThirdParty() {
        return includeThirdParty;
    }

    public void setIncludeThirdParty(Boolean includeThirdParty) {
        this.includeThirdParty = includeThirdParty;
    }

    public Boolean getHasGenerationSequence() {
        return hasGenerationSequence;
    }

    public void setHasGenerationSequence(Boolean hasGenerationSequence) {
        this.hasGenerationSequence = hasGenerationSequence;
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

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
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

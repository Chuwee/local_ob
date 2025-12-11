package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateStatusDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class VenueTemplateFilterDTO extends BaseEntityRequestFilter implements VenueTemplateFilterScoped {

    private static final long serialVersionUID = 1L;

    @JsonProperty("venue_id")
    private Long venueId;

    @JsonProperty("venue_entity_id")
    private Long venueEntityId;

    @JsonProperty("venue_city")
    private String venueCity;

    @JsonProperty("venue_country")
    private String venueCountry;

    @JsonProperty("event_id")
    private Long eventId;

    private List<VenueTemplateScopeDTO> scope;

    private List<VenueTemplateTypeDTO> type;

    private List<VenueTemplateStatusDTO> status;

    @JsonProperty("public")
    private Boolean isPublic;

    @JsonProperty("graphic")
    private Boolean graphic;

    @JsonProperty("inventory_provider")
    private InventoryProviderEnum inventoryProvider;

    @JsonProperty("has_avet_mapping")
    private Boolean hasAvetMapping;

    @JsonProperty("include_third_party_templates")
    private Boolean includeThirdPartyTemplates;

    @JsonProperty("q")
    private String freeSearch;

    private SortOperator<String> sort;

    private List<String> fields;

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

    public String getVenueCountry() {
        return venueCountry;
    }

    public void setVenueCountry(String venueCountry) {
        this.venueCountry = venueCountry;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    @Override
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public List<VenueTemplateScopeDTO> getScope() {
        return scope;
    }

    @Override
    public void setScope(List<VenueTemplateScopeDTO> scope) {
        this.scope = scope;
    }

    public List<VenueTemplateTypeDTO> getType() {
        return type;
    }

    public void setType(List<VenueTemplateTypeDTO> type) {
        this.type = type;
    }

    public List<VenueTemplateStatusDTO> getStatus() {
        return status;
    }

    public void setStatus(List<VenueTemplateStatusDTO> status) {
        this.status = status;
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

    public InventoryProviderEnum getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(InventoryProviderEnum inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public Boolean getHasAvetMapping() {
        return hasAvetMapping;
    }

    public void setHasAvetMapping(Boolean hasAvetMapping) {
        this.hasAvetMapping = hasAvetMapping;
    }

    public Boolean getIncludeThirdPartyTemplates() {
        return includeThirdPartyTemplates;
    }

    public void setIncludeThirdPartyTemplates(Boolean includeThirdPartyTemplates) {
        this.includeThirdPartyTemplates = includeThirdPartyTemplates;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

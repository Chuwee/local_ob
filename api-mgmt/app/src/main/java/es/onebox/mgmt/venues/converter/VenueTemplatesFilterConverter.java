package es.onebox.mgmt.venues.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFiltersRequest;
import es.onebox.mgmt.venues.dto.VenueTemplateFilterDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatesFilterOptionsRequest;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VenueTemplatesFilterConverter {

    private VenueTemplatesFilterConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static VenueTemplatesFilter convert(VenueTemplateFilterDTO filter, List<Long> visibleEntities) {
        if (filter == null) {
            return null;
        }
        VenueTemplatesFilter venueTemplatesFilter = new VenueTemplatesFilter();
        venueTemplatesFilter.setEntityId(filter.getEntityId());
        venueTemplatesFilter.setVenueEntityId(filter.getVenueEntityId());
        venueTemplatesFilter.setEventId(filter.getEventId());
        venueTemplatesFilter.setVenueId(filter.getVenueId());
        venueTemplatesFilter.setVenueCity(filter.getVenueCity());
        venueTemplatesFilter.setVenueCountry(filter.getVenueCountry());
        venueTemplatesFilter.setGraphic(filter.getGraphic());
        venueTemplatesFilter.setInventoryProvider(filter.getInventoryProvider());
        venueTemplatesFilter.setHasAvetMapping(filter.getHasAvetMapping());
        venueTemplatesFilter.setPublic(filter.getPublic());
        if (!CommonUtils.isEmpty(filter.getScope())) {
            venueTemplatesFilter.setScope(filter.getScope().stream().
                    map(VenueTemplateScopeConverter::toVenueTemplateScope).collect(Collectors.toList()));
        }
        if (!CommonUtils.isEmpty(filter.getStatus())) {
            venueTemplatesFilter.setStatus(filter.getStatus().stream().
                    map(VenueTemplateStatusConverter::toVenueTemplateStatus).collect(Collectors.toList()));
        }
        if (!CommonUtils.isEmpty(filter.getType())) {
            venueTemplatesFilter.setTemplateType(filter.getType().stream().
                    map(VenueTemplateTypeConverter::toVenueTemplateType).collect(Collectors.toList()));
        }
        venueTemplatesFilter.setIncludeThirdParty(filter.getIncludeThirdPartyTemplates());
        venueTemplatesFilter.setVisibleEntities(visibleEntities);
        venueTemplatesFilter.setFreeSearch(filter.getFreeSearch());
        venueTemplatesFilter.setLimit(filter.getLimit());
        venueTemplatesFilter.setOffset(filter.getOffset());
        venueTemplatesFilter.setEntityAdminId(filter.getEntityAdminId());
        return venueTemplatesFilter;
    }

    public static VenueTemplatesFiltersRequest convert(VenueTemplatesFilterOptionsRequest request, Long operatorId, Long entityId, Long entityAdminId) {
        if (request == null) {
            return null;
        }
        VenueTemplatesFiltersRequest filtersRequest = new VenueTemplatesFiltersRequest();
        filtersRequest.setLimit(request.getLimit());
        filtersRequest.setOffset(request.getOffset());
        filtersRequest.setOperatorId(operatorId);
        filtersRequest.setEventId(request.getEventId());
        filtersRequest.setEntityId(entityId);
        filtersRequest.setEntityAdminId(entityAdminId);
        List<VenueTemplateScopeDTO> scopes = request.getScope();
        if (scopes != null) {
            filtersRequest.setScope(scopes.stream()
                    .map(VenueTemplateScopeConverter::toVenueTemplateScope)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        return filtersRequest;
    }

}

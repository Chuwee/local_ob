package es.onebox.mgmt.venues.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.common.groups.GroupAttendeeDTO;
import es.onebox.mgmt.common.groups.GroupCompanionDTO;
import es.onebox.mgmt.common.groups.GroupDTO;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueLocation;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;
import es.onebox.mgmt.venues.dto.BaseVenueDTO;
import es.onebox.mgmt.venues.dto.BaseVenueTemplateDTO;
import es.onebox.mgmt.venues.dto.CreateTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.UpdateTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.VenueDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateDetailsDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenueTemplateConverter {

    private static final String CONFIG_INVENTORY_PROVIDER = "inventoryProvider";
    private static final String CONFIG_INVENTORY_ID = "inventoryId";

    private VenueTemplateConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static VenueTemplateDTO convert(VenueTemplate venueTemplate) {
        if (venueTemplate == null) {
            return null;
        }
        VenueTemplateDTO dto = new VenueTemplateDTO();
        fill(dto, venueTemplate);
        dto.setVenue(convertVenue(venueTemplate.getVenue()));
        return dto;
    }

    public static VenueTemplateDetailsDTO convertToDetails(VenueTemplate venueTemplate) {
        if (venueTemplate == null) {
            return null;
        }
        VenueTemplateDetailsDTO dto = new VenueTemplateDetailsDTO();
        fill(dto, venueTemplate);
        dto.setVenue(convertVenueDetails(venueTemplate.getVenue()));
        dto.setSpace(venueTemplate.getSpace());
        dto.setAvailableCapacity(venueTemplate.getAvailableCapacity());
        dto.setExternalId(venueTemplate.getExternalId());

        if (venueTemplate.getTemplateType() != null && venueTemplate.getTemplateType().equals(VenueTemplateType.ACTIVITY)) {
            //TODO deprecated fields
            dto.setMaxGroups(venueTemplate.getMaxGroups());
            dto.setMinAttendees(venueTemplate.getMinAttendees());
            dto.setMaxAttendees(venueTemplate.getMaxAttendees());
            dto.setMinCompanions(venueTemplate.getMinCompanions());
            dto.setMaxCompanions(venueTemplate.getMaxCompanions());
            dto.setCompanionsOccupyCapacity(venueTemplate.getCompanionsOccupyCapacity());

            GroupDTO group = new GroupDTO();
            group.setLimit(new LimitlessValueDTO(venueTemplate.getMaxGroups()));
            group.setAttendees(new GroupAttendeeDTO());
            group.getAttendees().setMin(venueTemplate.getMinAttendees());
            group.getAttendees().setMax(new LimitlessValueDTO(venueTemplate.getMaxAttendees()));
            group.setCompanions(new GroupCompanionDTO());
            group.getCompanions().setMin(venueTemplate.getMinCompanions());
            group.getCompanions().setMax(new LimitlessValueDTO(venueTemplate.getMaxCompanions()));
            group.getCompanions().setOccupyCapacity(venueTemplate.getCompanionsOccupyCapacity());
            dto.setGroups(group);
        }
        if (VenueTemplateType.AVET.getId().equals(venueTemplate.getTemplateType().getId()) && MapUtils.isNotEmpty(venueTemplate.getExternalData())) {
            dto.setExternalData(venueTemplate.getExternalData());
        }
        if (venueTemplate.getInventoryProvider() != null && MapUtils.isNotEmpty(venueTemplate.getExternalData())) {
            dto.setExternalData(venueTemplate.getExternalData());
        }

        return dto;
    }

    private static void fill(BaseVenueTemplateDTO venueTemplateDTO, VenueTemplate venueTemplate) {
        venueTemplateDTO.setId(venueTemplate.getId());
        venueTemplateDTO.setName(venueTemplate.getName());
        venueTemplateDTO.setCapacity(venueTemplate.getCapacity());
        venueTemplateDTO.setGraphic(venueTemplate.getGraphic());
        venueTemplateDTO.setPublic(venueTemplate.getPublic());
        venueTemplateDTO.setScope(VenueTemplateScopeConverter.toVenueTemplateScopeDTO(venueTemplate.getScope()));
        venueTemplateDTO.setStatus(VenueTemplateStatusConverter.toVenueTemplateStatusDTO(venueTemplate.getStatus()));
        venueTemplateDTO.setType(VenueTemplateTypeConverter.toVenueTemplateTypeDTO(venueTemplate.getTemplateType()));
        venueTemplateDTO.setCreationDate(venueTemplate.getCreationDate());
        venueTemplateDTO.setEventId(venueTemplate.getEventId());
        venueTemplateDTO.setInventoryProvider(venueTemplate.getInventoryProvider());
        venueTemplateDTO.setExternalId(venueTemplate.getExternalId());

        if (venueTemplate.getEntityId() != null) {
            venueTemplateDTO.setEntity(new IdNameDTO(venueTemplate.getEntityId(), venueTemplate.getEntityName()));
        }

        venueTemplateDTO.setImageUrl(venueTemplate.getImageUrl());
    }

    private static BaseVenueDTO convertVenue(Venue venue) {
        if (venue == null || venue.getId() == null) {
            return null;
        }
        BaseVenueDTO venueDTO = new BaseVenueDTO();
        fill(venueDTO, venue);
        return venueDTO;
    }

    private static VenueDTO convertVenueDetails(Venue venue) {
        if (venue == null || venue.getId() == null) {
            return null;
        }
        VenueDTO venueDTO = new VenueDTO();
        fill(venueDTO, venue);
        venueDTO.setCapacity(venue.getMaxCapacity());
        return venueDTO;
    }

    private static void fill(BaseVenueDTO venueDTO, Venue venue) {
        venueDTO.setId(venue.getId());
        venueDTO.setName(venue.getName());
        venueDTO.setGooglePlaceId(venue.getGooglePlaceId());
        VenueLocation location = venue.getLocation();
        if (location != null) {
            venueDTO.setCity(location.getCity());
            venueDTO.setCountry(location.getCountry());
        }
        venueDTO.setTimezone(venue.getTimezone().getOlsonId());
        if (CollectionUtils.isNotEmpty(venue.getAccessControlSystems())){
            List<NameDTO> accessControlSystemNames = venue.getAccessControlSystems().stream()
                    .map(accessControlSystem -> new NameDTO(accessControlSystem.name())).toList();
            venueDTO.setAccessControlSystems(accessControlSystemNames);
        }
        venueDTO.setEntity(new IdNameDTO(venue.getEntity().getId(), venue.getEntity().getName()));
    }

    public static UpdateVenueTemplate toMsVenue(UpdateTemplateRequestDTO source) {
        if (source == null) {
            return null;
        }
        UpdateVenueTemplate updateVenueTemplate = new UpdateVenueTemplate();
        updateVenueTemplate.setName(source.getName());
        updateVenueTemplate.setStatus(null);
        updateVenueTemplate.setImage(source.getImage());
        updateVenueTemplate.setSpaceId(source.getSpaceId());
        updateVenueTemplate.setVenueId(source.getVenueId());

        GroupDTO sourceGroups = source.getGroups();
        if (sourceGroups != null) {
            updateVenueTemplate.setMaxGroups(ConverterUtils.getIntLimitlessValue(sourceGroups.getLimit()));
            if (sourceGroups.getAttendees() != null) {
                updateVenueTemplate.setMinAttendees(sourceGroups.getAttendees().getMin());
                updateVenueTemplate.setMaxAttendees(ConverterUtils.getIntLimitlessValue(sourceGroups.getAttendees().getMax()));
            }
            if (sourceGroups.getCompanions() != null) {
                updateVenueTemplate.setMinCompanions(sourceGroups.getCompanions().getMin());
                updateVenueTemplate.setMaxCompanions(ConverterUtils.getIntLimitlessValue(sourceGroups.getCompanions().getMax()));
                updateVenueTemplate.setCompanionsOccupyCapacity(sourceGroups.getCompanions().getOccupyCapacity());
            }
        }
        return updateVenueTemplate;
    }

    public static CreateVenueTemplateRequest convertToDatasource(CreateTemplateRequestDTO request) {
        if(request == null){
            return null;
        }

        CreateVenueTemplateRequest createVenueTemplateRequest = new CreateVenueTemplateRequest();
        createVenueTemplateRequest.setName(request.getName());
        createVenueTemplateRequest.setEventId(request.getEventId());
        createVenueTemplateRequest.setVenueId(request.getVenueId());
        createVenueTemplateRequest.setSpaceId(request.getSpaceId());
        createVenueTemplateRequest.setEntityId(request.getEntityId());
        createVenueTemplateRequest.setScope(VenueTemplateScopeConverter.toVenueTemplateScope(request.getScope()));
        createVenueTemplateRequest.setType(VenueTemplateTypeConverter.toVenueTemplateType(request.getType()));
        createVenueTemplateRequest.setGraphical(request.getGraphic());
        createVenueTemplateRequest.setFromTemplateId(request.getFromTemplateId());
        createVenueTemplateRequest.setImage(request.getImage());
        createVenueTemplateRequest.setExternalId(request.getExternalId());

        es.onebox.mgmt.venues.dto.AdditionalConfigDTO additionalConfigRq = request.getAdditionalConfig();
        if(additionalConfigRq != null){
            convertToDatasource(additionalConfigRq, createVenueTemplateRequest);
        }

        return createVenueTemplateRequest;
    }

    private static void convertToDatasource(es.onebox.mgmt.venues.dto.AdditionalConfigDTO source,
                                            CreateVenueTemplateRequest target) {

        Map<String, Object> additionalConfig = new HashMap<>();
        if( source.getInventoryProvider() != null){
            additionalConfig.put(CONFIG_INVENTORY_PROVIDER, source.getInventoryProvider().getCode());
        }
        if( StringUtils.isNotBlank(source.getInventoryId())) {
            additionalConfig.put(CONFIG_INVENTORY_ID, source.getInventoryId());
        }
        target.setAdditionalConfig(additionalConfig);
        target.setCapacityId(source.getCapacityId());
    }

}

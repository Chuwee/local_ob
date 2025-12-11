package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateNotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateScope;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.sessions.SessionUtils;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTagConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateNotNumberedZoneConverter;
import es.onebox.mgmt.venues.dto.BaseNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.CloneNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.CreateNotNumberedZoneBulkDTO;
import es.onebox.mgmt.venues.dto.CreateNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.NotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZoneBulkDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZonesBulkDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneRequestDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotNumberedZoneService {

    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    @Autowired
    public NotNumberedZoneService(VenuesRepository venuesRepository,
                                  final ValidationService validationService, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public void updateNotNumberedZoneTags(Long venueTemplateId, VenueTagNotNumberedZoneRequestDTO[] notNumberedZone) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        SessionUtils.validateVenueTags(notNumberedZone);
        venuesRepository.updateNotNumberedZoneTags(venueTemplateId, VenueTagConverter.fromVenueTagRequest(notNumberedZone));

        if (venueTemplate.getEventId() != null && venueTemplate.getVenue() != null
                && venueTemplate.getVenue().getId() != null && venueTemplate.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }
    }

    public List<BaseNotNumberedZoneDTO> getNotNumberedZones(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        List<NotNumberedZone> notNumberedZones = venuesRepository.getNotNumberedZones(venueTemplateId, null);
        return notNumberedZones.stream()
                .map(VenueTemplateNotNumberedZoneConverter::fromMsVenue)
                .collect(Collectors.toList());
    }

    public NotNumberedZoneDTO getNotNumberedZone(Long venueTemplateId, Long zoneId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        return VenueTemplateNotNumberedZoneConverter.fromMsVenue(venuesRepository.getNotNumberedZone(venueTemplateId, zoneId));

    }

    public List<NotNumberedZoneDTO> getNotNumberedZones(Long venueTemplateId, Long sectorId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        List<NotNumberedZoneCapacity> notNumberedZones = venuesRepository.getNotNumberedZonesBySectorId(venueTemplateId, sectorId);
        return notNumberedZones.stream()
                .map(VenueTemplateNotNumberedZoneConverter::fromMsVenue)
                .collect(Collectors.toList());
    }

    public IdDTO createNotNumberedZone(Long venueTemplateId, CreateNotNumberedZoneDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        NotNumberedZone nnZone = VenueTemplateNotNumberedZoneConverter.toMS(body);
        IdDTO nnzId = venuesRepository.createNotNumberedZone(venueTemplateId, nnZone);

        if (venueTemplate.getEventId() != null && venueTemplate.getVenue() != null
                && venueTemplate.getVenue().getId() != null && venueTemplate.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }

        return nnzId;
    }

    public List<IdDTO> createNotNumberedZones(Long venueTemplateId, CreateNotNumberedZoneBulkDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        Set<NotNumberedZone> nnZone = VenueTemplateNotNumberedZoneConverter.toMS(body);

        List<IdDTO> nnzIds = venuesRepository.createNotNumberedZones(venueTemplateId, nnZone);

        if (venueTemplate.getEventId() != null && venueTemplate.getVenue() != null
                && venueTemplate.getVenue().getId() != null && venueTemplate.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }
        return nnzIds;
    }

    public Long cloneNotNumberedZone(Long venueTemplateId, Long nnZoneId, CloneNotNumberedZoneDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        NotNumberedZone notNumberedZone = new NotNumberedZone();
        notNumberedZone.setName(body.getName());
        notNumberedZone.setSectorId(body.getSectorId());
        notNumberedZone.setViewId(body.getViewId());
        Long nnzId =  venuesRepository.cloneNotNumberedZone(venueTemplateId, nnZoneId, notNumberedZone);

        if (venueTemplate.getEventId() != null && venueTemplate.getVenue() != null
                && venueTemplate.getVenue().getId() != null && venueTemplate.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }

        return nnzId;
    }

    public void updateNotNumberedZone(Long venueTemplateId, Long notNumberedZoneId, UpdateNotNumberedZoneDTO body) {
        VenueTemplate template = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        Long eventId = template.getEventId();
        if (VenueTemplateScope.EVENT.equals(template.getScope()) && template.getEventId() != null) {
            validationService.getAndCheckEventExternal(eventId);
        }
        validateUpdateNotNumberedZone(body);
        UpdateNotNumberedZone out = VenueTemplateNotNumberedZoneConverter.toDTO(body);
        venuesRepository.updateNotNumberedZone(venueTemplateId, notNumberedZoneId, out);


        if (template.getEventId() != null && template.getVenue() != null
                && template.getVenue().getId() != null && template.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(template.getEntityId(), template.getVenue().getId(), venueTemplateId);
        }
    }

    private void validateUpdateNotNumberedZone(UpdateNotNumberedZoneDTO body) {
        if (body.getCapacity() != null && body.getCapacity() < 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid capacity", null);
        }
    }

    public void updateNotNumberedZones(Long venueTemplateId, UpdateNotNumberedZonesBulkDTO body) {
        VenueTemplate template = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        Long eventId = template.getEventId();
        Set<Long> nnZoneIds = body.stream().map(UpdateNotNumberedZoneBulkDTO::getId).collect(Collectors.toSet());
        List<NotNumberedZone> nnZones = this.venuesRepository.getNotNumberedZones(venueTemplateId, new NotNumberedZoneFilter(nnZoneIds));
        if (CollectionUtils.isEmpty(nnZones) || nnZones.size() != nnZoneIds.size()) {
            throw ExceptionBuilder.build(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INVALID_PARAM, "Invalid not_numbered_zone_id");
        }
        if (VenueTemplateScope.EVENT.equals(template.getScope()) && template.getEventId() != null) {
            validationService.getAndCheckEventExternal(eventId);
        }
        body.forEach(this::validateUpdateNotNumberedZone);
        Set<UpdateNotNumberedZone> out = VenueTemplateNotNumberedZoneConverter.toDTO(body);
        venuesRepository.updateNotNumberedZoneBulk(venueTemplateId, out);


        if (template.getEventId() != null && template.getVenue() != null
                && template.getVenue().getId() != null && template.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(template.getEntityId(), template.getVenue().getId(), venueTemplateId);
        }
    }

    public void deleteNotNumberedZone(Long venueTemplateId, Long notNumberedZoneId) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        venuesRepository.deleteNotNumberedZone(venueTemplateId, notNumberedZoneId);
    }

    private void checkAndProcessExternalVenueTemplates(Long entityId, Long venueId, Long venueTemplateId) {
        if (venueTemplateId == null) {
            return;
        }

        List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);

        if (CollectionUtils.isNotEmpty(venueAccessControlSystems)) {
            venueAccessControlSystems.stream().distinct().forEach(accessControlSystem -> {
                ExternalAccessControlHandler externalAccessControlHandler;
                externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

                if (externalAccessControlHandler == null) {
                    return;
                }

                externalAccessControlHandler.addOrUpdateVenueElements(entityId, venueTemplateId);
            });
        }
    }
}

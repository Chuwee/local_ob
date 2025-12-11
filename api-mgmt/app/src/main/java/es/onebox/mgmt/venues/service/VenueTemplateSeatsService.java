package es.onebox.mgmt.venues.service;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateBaseSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.sessions.SessionUtils;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateSeatConverter;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSeatsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateSeatDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateBaseSeatDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateSeatDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueTemplateSeatsService {

    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;
    private final AccessControlSystemsRepository accessControlSystemsRepository;

    public VenueTemplateSeatsService(VenuesRepository venuesRepository, ValidationService validationService, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider, AccessControlSystemsRepository accessControlSystemsRepository) {
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
    }

    public VenueTemplateSeatDTO getVenueTemplateSeat(Long venueTemplateId, Integer seatId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        VenueTemplateSeat seat = venuesRepository.getVenueTemplateSeat(venueTemplateId, seatId);

        return VenueTemplateSeatConverter.toDto(seat);
    }

    public List<VenueTemplateBaseSeatDTO> getVenueTemplateSeatsByRows(Long venueTemplateId, List<Integer> rowIds) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        List<VenueTemplateBaseSeat> seats = venuesRepository.getVenueTemplateSeatsByRows(venueTemplateId, rowIds);

        return VenueTemplateSeatConverter.toDto(seats);
    }

    public List<IdDTO> createVenueTemplateSeats(Long venueTemplateId, CreateVenueTemplateSeatsDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        List<IdDTO> seatIds = venuesRepository.createVenueTemplateSeats(venueTemplateId, VenueTemplateSeatConverter.toMs(requestDTO));

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null
                && venueTemplate.getVenue() != null && venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }

        return seatIds;
    }

    public void updateSeatTags(Long venueTemplateId, UpdateVenueTemplateSeatDTO[] seats) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        SessionUtils.validateVenueTags(seats);
        venuesRepository.updateAssignTags(venueTemplateId, VenueTemplateSeatConverter.toMs(seats));
    }

    public void deleteVenueTemplateSeats(Long venueTemplateId, List<Integer> seatIds) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        venuesRepository.deleteVenueTemplateSeats(venueTemplateId, seatIds);
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

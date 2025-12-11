package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplatePriceTypesConverter;
import es.onebox.mgmt.venues.dto.CreateVenueTemplatePriceTypeRestrictionDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatePriceTypeRestrictionDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateRestrictionsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueTemplateRestrictionsService {
    
    private final ValidationService validationService;
    private final EventsRepository eventsRepository;

    protected VenueTemplateRestrictionsService(ValidationService validationService, EventsRepository eventsRepository) {
        this.validationService = validationService;
        this.eventsRepository = eventsRepository;
    }

    public VenueTemplatePriceTypeRestrictionDTO getVenueTemplatePriceTypeRestrictions(Long venueTemplateId, Long priceTypeId) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        Long eventId = venueTemplate.getEventId();
        Long templateId = venueTemplate.getId();
        validationService.getAndCheckEvent(eventId);
        VenueTemplatePriceTypeRestriction restriction = eventsRepository.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        return VenueTemplatePriceTypesConverter.fromMsEvent(restriction);
    }

    public void upsertVenueTemplatePriceTypeRestrictions(Long venueTemplateId, Long priceTypeId, CreateVenueTemplatePriceTypeRestrictionDTO createVenueTemplatePriceTypeRestrictionDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        Long eventId = venueTemplate.getEventId();
        Long templateId = venueTemplate.getId();
        if (createVenueTemplatePriceTypeRestrictionDTO.getRequiredPriceTypeIds().stream()
                .anyMatch(priceTypeId::equals)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.CIRCULAR_PRICE_TYPE_RESTRICTION);
        }
        validationService.getAndCheckEvent(eventId);
        CreateVenueTemplatePriceTypeRestriction createPriceTypeRestriction = VenueTemplatePriceTypesConverter.toCreateMsEvent(createVenueTemplatePriceTypeRestrictionDTO);
        eventsRepository.upsertVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, createPriceTypeRestriction);
    }

    public void deleteVenueTemplatePriceTypeRestriction(Long venueTemplateId, Long priceTypeId) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        Long eventId = venueTemplate.getEventId();
        Long templateId = venueTemplate.getId();
        validationService.getAndCheckEvent(eventId);
        eventsRepository.deleteVenueTemplatePriceTypeRestriction(eventId, templateId, priceTypeId);
    }

    public VenueTemplateRestrictionsDTO getAllVenueTemplateRestrictions(final Long venueTemplateId) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        Long eventId = venueTemplate.getEventId();
        Long templateId = venueTemplate.getId();
        validationService.getAndCheckEvent(eventId);
        List<IdNameDTO> restriction = eventsRepository.getAllVenueTemplateRestrictions(eventId, templateId);
        return VenueTemplatePriceTypesConverter.fromMsEvent(restriction);
    }

}

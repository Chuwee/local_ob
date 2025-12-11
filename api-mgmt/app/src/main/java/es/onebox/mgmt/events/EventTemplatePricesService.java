package es.onebox.mgmt.events;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateStatus;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.events.converter.VenueTemplatePriceConverter;
import es.onebox.mgmt.events.dto.UpdateEventTemplatePriceRequestDTO;
import es.onebox.mgmt.events.dto.VenueTemplatePriceExtendedDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.SessionUtils;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_MATCH_EVENT;

@Service
public class EventTemplatePricesService {

    private final EventsRepository eventsRepository;
    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;

    @Autowired
    public EventTemplatePricesService(EventsRepository eventsRepository, VenuesRepository venuesRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
    }

    public List<VenueTemplatePriceExtendedDTO> getVenueTemplatePrices(Long eventId, Long templateId, List<Long> sessionIdList, List<Integer> rateGroupList, List<Integer> rateGroupProductList) {
        Event event = validationService.getAndCheckEvent(eventId);

        checkEventTemplate(eventId, templateId);
        if (CollectionUtils.isNotEmpty(rateGroupList) && !EventType.AVET.equals(event.getType()) && !SessionUtils.isSgaEvent(event.getInventoryProvider())) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_PRICE_TYPE_EVENT_NOT_AVET_FOR_RATE_GROUP_FILTER);
        }

        if (CollectionUtils.isNotEmpty(rateGroupProductList) && !SessionUtils.isSgaEvent(event.getInventoryProvider())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_IS_NOT_SGA);
        }

        List<VenueTemplatePrice> venueTemplatePrices = eventsRepository.getVenueTemplatePrices(eventId, templateId, sessionIdList, rateGroupList, rateGroupProductList);
        return VenueTemplatePriceConverter.fromMsVenueTemplatePricesToExtended(venueTemplatePrices);
    }

    public void updateVenueTemplatePrices(Long eventId, Long templateId, List<UpdateEventTemplatePriceRequestDTO> prices) {
        Event event = validationService.getAndCheckEventExternal(eventId);
        checkEventTemplate(eventId, templateId);
        if (EventType.AVET.equals(event.getType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "AVET templates prices cannot be modified ", null);
        }
        eventsRepository.updateVenueTemplatePrices(eventId, templateId, VenueTemplatePriceConverter.toMsVenue(prices));
    }

    private void checkEventTemplate(Long eventId, Long templateId) {
        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(templateId);
        validateTemplateBelongsToEvent(eventId, venueTemplate);
    }


    private static void validateTemplateBelongsToEvent(Long eventId, VenueTemplate venueTemplate) {
        if (venueTemplate == null || VenueTemplateStatus.DELETED.equals(venueTemplate.getStatus())) {
            throw new OneboxRestException(VENUE_TEMPLATE_NOT_FOUND);
        }
        if (venueTemplate.getEventId() == null || eventId.longValue() != venueTemplate.getEventId().longValue()) {
            throw new OneboxRestException(VENUE_TEMPLATE_NOT_MATCH_EVENT);
        }
    }

}

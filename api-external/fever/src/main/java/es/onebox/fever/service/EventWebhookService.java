package es.onebox.fever.service;

import es.onebox.common.datasources.ms.event.dto.EventChannelSurchargesDTO;
import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementDTO;
import es.onebox.common.datasources.ms.event.dto.EventRatesDTO;
import es.onebox.common.datasources.ms.event.dto.SurchargesDTO;
import es.onebox.common.datasources.ms.event.enums.SurchargeTypeDTO;
import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementFilter;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.EventTemplatePriceDTO;
import es.onebox.common.datasources.ms.event.enums.EventTagType;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeWebCommunicationElementDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.webhook.dto.fever.event.EventDetail;
import es.onebox.common.datasources.webhook.dto.fever.EventUpdate;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.PriceTypeDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.converter.CommonConverter;
import es.onebox.fever.converter.EventConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class EventWebhookService {

    private static final String IMAGE_TAGS = "EVENT_COMMUNICATION_IMAGES";
    
    private static final List<SurchargeTypeDTO> EXCLUDED_SURCHARGE_TYPES = Arrays.asList(
        SurchargeTypeDTO.SECONDARY_MARKET_CHANNEL,
        SurchargeTypeDTO.SECONDARY_MARKET_PROMOTER
    );

    private final MsEventRepository msEventRepository;
    private final VenueTemplateRepository venueTemplateRepository;
    private final EntityValidationService entityValidationService;


    @Autowired
    public EventWebhookService(MsEventRepository msEventRepository,
                               VenueTemplateRepository venueTemplateRepository,
                               EntityValidationService entityValidationService) {

        this.msEventRepository = msEventRepository;
        this.venueTemplateRepository = venueTemplateRepository;
        this.entityValidationService = entityValidationService;
    }

    public WebhookFeverDTO sendEventGeneralData(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        EventDTO updatedEvent = msEventRepository.getEventWithSeasonTickets(eventId).getData()
                .stream().findFirst().orElseThrow(() ->  new OneboxRestException(ApiExternalErrorCode.EVENT_NOT_FOUND));

        EventUpdate eventUpdate = new EventUpdate();
        EventDetail eventDetails = new EventDetail();
        eventDetails.setName(updatedEvent.getName());
        eventDetails.setStatus(updatedEvent.getStatus());
        eventDetails.setLanguages(EventConverter.toEventLanguageList(updatedEvent));
        eventDetails.setSupraEvent(updatedEvent.getSupraEvent());
        eventUpdate.setEventDetails(eventDetails);
        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    public WebhookFeverDTO sendEventCommunication(WebhookFeverDTO webhookFever, String obSubtype) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        msEventRepository.getEventWithSeasonTickets(eventId).getData()
                .stream().findFirst().orElseThrow(() ->  new OneboxRestException(ApiExternalErrorCode.EVENT_NOT_FOUND));

        EventCommunicationElementFilter filter = generateEventCommunicationFilter(obSubtype);
        List<EventCommunicationElementDTO> updatedEventCommunication = msEventRepository.getEventCommunicationElements(eventId, filter);

        Long channelId = webhookFever.getNotificationMessage().getChannelId();
        if (channelId != null) {
            entityValidationService.validateAllowedEntities(webhookFever);
            updatedEventCommunication.addAll(msEventRepository.getEventChannelCommunicationElements(eventId, channelId, filter));
        }

        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventCommunicationElements(EventConverter.toEventCommunicationElementDTO(
            updatedEventCommunication));

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    public WebhookFeverDTO sendEventSurcharges(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        getAndValidateEvent(eventId);

        List<SurchargesDTO> eventSurchargesList = Optional.ofNullable(
            msEventRepository.getEventSurcharges(eventId)
        ).orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.EVENT_CHANNEL_SURCHARGES_NOT_FOUND));

        eventSurchargesList = eventSurchargesList.stream().filter(
            surcharges -> !EXCLUDED_SURCHARGE_TYPES.contains(surcharges.getType())).toList();


        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventSurcharges(
            EventConverter.toEventSuchargesFeverDTOList(eventSurchargesList)
        );
        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }


    public WebhookFeverDTO sendEventChannelSurcharges(WebhookFeverDTO webhookFever) {

        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        Long channelId = webhookFever.getNotificationMessage().getChannelId();

        getAndValidateEvent(eventId);
        entityValidationService.validateAllowedEntities(webhookFever);

        List<EventChannelSurchargesDTO> eventChannelSurchargesList = Optional.ofNullable(
            msEventRepository.getEventChannelSurcharges(eventId, channelId)
        ).orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.EVENT_CHANNEL_SURCHARGES_NOT_FOUND));

        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventChannelSurcharges(
            EventConverter.toEventChannelSurchargesFeverDTOList(eventChannelSurchargesList)
        );

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    public WebhookFeverDTO sendEventVenueTemplatePrices(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        Long templateId = webhookFever.getNotificationMessage().getTemplateId();

        getAndValidateEvent(eventId);

        List<EventTemplatePriceDTO> eventTemplatePriceList = Optional.ofNullable(
            msEventRepository.getEventVenueTemplatePrices(eventId, templateId)
        ).orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.EVENT_CHANNEL_SURCHARGES_NOT_FOUND));

        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventTemplatePrices(EventConverter.toEventTemplatePriceFeverDTOList(eventTemplatePriceList));

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    public WebhookFeverDTO sendEventRateDetail(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());

        getAndValidateEvent(eventId);

        EventRatesDTO eventRatesDetail = Optional.ofNullable(
            msEventRepository.getEventRatesDetails(eventId)
        ).orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.EVENT_CHANNEL_SURCHARGES_NOT_FOUND));



        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventRateDetails(EventConverter.toEventRatesFeverDTO(eventRatesDetail));

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    private EventCommunicationElementFilter generateEventCommunicationFilter(String obSubtype) {
        EventCommunicationElementFilter filter = new EventCommunicationElementFilter();
        if (IMAGE_TAGS.equals(obSubtype)) {
            filter.setTags(new HashSet<>(Arrays.asList(EventTagType.LOGO_WEB, EventTagType.IMG_BODY_WEB,
                    EventTagType.IMG_BANNER_WEB,EventTagType.IMG_CARD_WEB,EventTagType.IMG_SQUARE_BANNER_WEB)));
        } else {
            filter.setTags(new HashSet<>(Arrays.asList(EventTagType.TEXT_TITLE_WEB, EventTagType.TEXT_SUBTITLE_WEB,
                    EventTagType.TEXT_LENGTH_WEB,EventTagType.TEXT_SUMMARY_WEB, EventTagType.TEXT_BODY_WEB, EventTagType.TEXT_LOCATION_WEB)));
        }
        return filter;
    }

    private EventDTO getAndValidateEvent(Long eventId) {
        return Optional.ofNullable(msEventRepository.getEvent(eventId))
            .orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.EVENT_NOT_FOUND));
    }

    public WebhookFeverDTO sendEventPriceTypeDetail(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        Long venueTemplateId = webhookFever.getNotificationMessage().getTemplateId();
        Long priceTypeId = webhookFever.getNotificationMessage().getPriceTypeId();

        getAndValidateEvent(eventId);
        MsPriceTypeDTO msPriceTypeDTO = venueTemplateRepository.getPriceType(venueTemplateId, priceTypeId);

        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventPriceTypeDetails(Arrays.asList(EventConverter.toPriceTypeDTO(msPriceTypeDTO)));

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    public WebhookFeverDTO sendEventPriceTypeDeleted(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        getAndValidateEvent(eventId);

        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(webhookFever.getNotificationMessage().getPriceTypeId());
        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventPriceTypeDetails(Arrays.asList(priceTypeDTO));

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

    public WebhookFeverDTO sendEventPriceTypeCommunication(WebhookFeverDTO webhookFever) {
        Long eventId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        Long venueTemplateId = webhookFever.getNotificationMessage().getTemplateId();
        Long priceTypeId = webhookFever.getNotificationMessage().getPriceTypeId();

        getAndValidateEvent(eventId);
        List<MsPriceTypeWebCommunicationElementDTO> priceTypeCommunicationElements =
                venueTemplateRepository.getPriceTypeWebCommunicationElements(venueTemplateId, priceTypeId, null);

        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setEventPriceTypeCommunicationElements(EventConverter.toEventPriceTypeWebCommunicationElements(priceTypeCommunicationElements));


        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));
        webhookFever.getFeverMessage().setEventUpdate(eventUpdate);

        return webhookFever;
    }

}

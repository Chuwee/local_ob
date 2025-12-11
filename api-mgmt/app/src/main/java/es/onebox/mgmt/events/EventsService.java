package es.onebox.mgmt.events;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.b2b.conditions.converter.ConditionsConverter;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.enums.GroupType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.Match;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.DeleteConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInoivcePrefixFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.Tiers;
import es.onebox.mgmt.datasources.ms.event.dto.event.Attribute;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.event.TaxMode;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.AttendantTicketsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.ClonePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateFilter;
import es.onebox.mgmt.datasources.ms.promotion.repository.EntityPromotionsRepository;
import es.onebox.mgmt.datasources.ms.promotion.repository.EventPromotionsRepository;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.converter.AttributeConverter;
import es.onebox.mgmt.entities.dto.AttributeDTO;
import es.onebox.mgmt.entities.dto.AttributeRequestValuesDTO;
import es.onebox.mgmt.entities.dto.AttributeSearchFilter;
import es.onebox.mgmt.entities.enums.AttributeScope;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.entities.factory.InventoryProviderServiceFactory;
import es.onebox.mgmt.events.converter.AttendantsConverter;
import es.onebox.mgmt.events.converter.EventConverter;
import es.onebox.mgmt.events.dto.AdditionalConfigMatchesDTO;
import es.onebox.mgmt.events.dto.BaseEventDTO;
import es.onebox.mgmt.events.dto.CreateEventRequestDTO;
import es.onebox.mgmt.events.dto.EventAttendantTicketsDTO;
import es.onebox.mgmt.events.dto.EventDTO;
import es.onebox.mgmt.events.dto.EventSearchFilterDTO;
import es.onebox.mgmt.events.dto.EventVenueTemplateDTO;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.events.dto.SearchEventsResponse;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.events.dto.UpdateEventRequestDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import es.onebox.mgmt.events.enums.AttendantTicketsChannelScopeTypeDTO;
import es.onebox.mgmt.events.enums.AttendantTicketsEventStatusDTO;
import es.onebox.mgmt.events.enums.EventAvetConfigType;
import es.onebox.mgmt.events.enums.EventType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.MatchConverter;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateConverter;
import es.onebox.mgmt.venues.dto.CreateTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateDetailsDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import es.onebox.mgmt.venues.service.VenueTemplatesService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;
    private final EntitiesRepository entitiesRepository;
    private final ValidationService validationService;
    private final VenueTemplatesService venueTemplatesService;
    private final VenuesRepository venuesRepository;
    private final AvetConfigRepository avetConfigRepository;
    private final AttendantTicketsRepository attendantTicketsRepository;
    private final ChannelsRepository channelsRepository;
    private final EntityPromotionsRepository entityPromotionsRepository;
    private final EventPromotionsRepository eventPromotionsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final OrdersRepository ordersRepository;
    private final InventoryProviderServiceFactory externalInventoryProviderServiceFactory;
    private final ClientsRepository clientsRepository;
    private final DispatcherRepository dispatcherRepository;

    @Autowired
    public EventsService(EventsRepository eventsRepository, UsersRepository usersRepository, MasterdataService masterdataService,
                         SecurityManager securityManager, EntitiesRepository entitiesRepository, ValidationService validationService,
                         VenueTemplatesService venueTemplatesService, VenuesRepository venuesRepository, AvetConfigRepository avetConfigRepository,
                         AttendantTicketsRepository attendantTicketsRepository, ChannelsRepository channelsRepository,
                         EntityPromotionsRepository entityPromotionsRepository, EventPromotionsRepository eventPromotionsRepository,
                         EventChannelsRepository eventChannelsRepository, OrdersRepository ordersRepository,
                         InventoryProviderServiceFactory externalInventoryProviderServiceFactory, ClientsRepository clientsRepository, DispatcherRepository dispatcherRepository) {
        this.eventsRepository = eventsRepository;
        this.usersRepository = usersRepository;
        this.masterdataService = masterdataService;
        this.securityManager = securityManager;
        this.entitiesRepository = entitiesRepository;
        this.validationService = validationService;
        this.venueTemplatesService = venueTemplatesService;
        this.venuesRepository = venuesRepository;
        this.avetConfigRepository = avetConfigRepository;
        this.attendantTicketsRepository = attendantTicketsRepository;
        this.channelsRepository = channelsRepository;
        this.entityPromotionsRepository = entityPromotionsRepository;
        this.eventPromotionsRepository = eventPromotionsRepository;
        this.eventChannelsRepository = eventChannelsRepository;
        this.ordersRepository = ordersRepository;
        this.externalInventoryProviderServiceFactory = externalInventoryProviderServiceFactory;
        this.clientsRepository = clientsRepository;
        this.dispatcherRepository = dispatcherRepository;
    }

    public EventDTO getEvent(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);
        Operator operator = entitiesRepository.getCachedOperator(event.getEntityId());

        EventDTO eventResponse = EventConverter.fromMsEvent(event, masterdataService.getCurrencies());

        if(event.getProducer() != null && event.getProducer().getId() != null && event.getInvoicePrefixId() != null) {
            InvoicePrefix invoicePrefix = entitiesRepository.getInvoicePrefix(event.getProducer().getId(), event.getInvoicePrefixId());
            if(invoicePrefix != null) {
                EventConverter.addInvoicePrefix(eventResponse, invoicePrefix);
            }
        }

        fillEventDTO(eventResponse, event);

        EventAttendantsConfigDTO attendantTickets = attendantTicketsRepository.getEventAttendantsConfig(eventId);
        EventChannels channels = eventChannelsRepository.getEventChannels(eventId,
                EventChannelSearchFilter.builder().limit(999L).offset(0L).build());
        eventResponse.getSettings().setAttendantTickets(AttendantsConverter.fromMsEvent(attendantTickets, channels));

        setCurrencyDefaultByOperatorMulticurrency(eventResponse,operator);
        eventResponse.setOperatorTZ(operator.getTimezone().getValue());
        eventResponse.setHasSales(ordersRepository.eventHasOrders(eventId));
        eventResponse.setHasSalesRequest(getEventHasSaleRequests(eventId));
        eventResponse.setPhoneVerificationRequired(event.getPhoneVerificationRequired());

        return eventResponse;
    }


    public SearchEventsResponse searchEvents(EventSearchFilterDTO filter) {
        securityManager.checkEntityAccessible(filter);

        Events events = eventsRepository.getEvents(EventConverter.toMsEvent(filter, masterdataService::getCountryIdByCode, masterdataService.getCurrencies()));
        SearchEventsResponse response = new SearchEventsResponse();
        response.setData(events.getData().stream().map(e -> {
            BaseEventDTO eventDTO = EventConverter.fromMsEvent(e, new BaseEventDTO(), masterdataService.getCurrencies());
            fillEventDTO(eventDTO, e);
            return eventDTO;
        }).collect(Collectors.toList()));

        response.setMetadata(events.getMetadata());

        return response;
    }

    public Long createEvent(CreateEventRequestDTO body) {
        validateAVETFields(body);

        securityManager.checkEntityAccessible(body.getEntityId());

        validationService.checkCategory(body.getCategoryId());

        Entity entity = entitiesRepository.getCachedEntity(body.getEntityId());
        Operator entityOperator = entitiesRepository.getCachedOperator(body.getEntityId());

        EventType type = body.getType();

        if (BooleanUtils.isNotTrue(entity.getUseExternalAvetIntegration()) && type.equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_CANNOT_CREATE_AVET_EVENTS);
        }
        if ((type.equals(EventType.ACTIVITY) || type.equals(EventType.THEME_PARK))
                && BooleanUtils.isNotTrue(entity.getUseActivityEvent())) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_CANNOT_CREATE_ACTIVITY_EVENTS);
        }

        String authUsername = SecurityUtils.getUsername();
        User authUser = usersRepository.getUser(authUsername, entity.getOperator().getId(),
                SecurityUtils.getApiKey());

        EventAvetConfigType avetConfigType;
        Integer competitionId;
        if (body.getAdditionalConfig() != null) {
            avetConfigType = body.getAdditionalConfig().getAvetConfig();
            competitionId = body.getAdditionalConfig().getAvetCompetitionId();
        } else {
            avetConfigType = null;
            competitionId = null;
        }

        VenueTemplateDetailsDTO venueTemplate = null;
        if (type.equals(EventType.AVET)) {
            venueTemplate = getVenueTemplate(body.getAdditionalConfig().getVenueTemplateId());
            validateCompetitionId(venueTemplate, competitionId);
        }

        List<IdNameDTO> favoriteEntityChannels = channelsRepository.getEntityFavoriteChannel(entity.getId());

        Long defaultInvoicePrefixId = getDefaultInvoicePrefixId(body.getProducerId());

        Long currencyId = entityOperator.getCurrency().getId().longValue();

        if (BooleanUtils.isTrue(entityOperator.getUseMultiCurrency())) {
            if (body.getCurrencyCode() != null) {
                currencyId = entityOperator.getCurrencies().getSelected().stream()
                        .filter(currency -> currency.getCode().equals(body.getCurrencyCode()))
                        .map(Currency::getId)
                        .findAny()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.EVENT_CURRENCY_NOT_MATCH_OPERATOR));
            } else {
                currencyId = entityOperator.getCurrencies().getSelected().stream()
                        .filter(currency -> currency.getCode().equals(entityOperator.getCurrencies().getDefaultCurrency()))
                        .map(Currency::getId)
                        .findAny()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.ERROR_OPERATOR_WITHOUT_MULTICURRENCY_DEFAULT));
            }
        }

        String providerId = body.getAdditionalConfig() == null || body.getAdditionalConfig().getInventoryProvider() == null ?
        null : body.getAdditionalConfig().getInventoryProvider().getCode();

        InventoryProviderService inventoryProviderService =
                externalInventoryProviderServiceFactory.getIntegrationService(entity.getId(), providerId);

        CreateEventData eventData = EventConverter.prepareCreateEventData(body.getName(), body.getReference(), es.onebox.mgmt.datasources.ms.event.dto.event.EventType.valueOf(type.name()),
                body.getEntityId(), body.getProducerId(), defaultInvoicePrefixId, body.getCategoryId(), entity.getLanguage().getId().intValue(),
                favoriteEntityChannels.isEmpty()? null: favoriteEntityChannels.stream().map(IdNameDTO::getId).collect(Collectors.toList()),
                competitionId, avetConfigType, currencyId, EventConverter.convertToDatasource(body.getAdditionalConfig()));

        if (body.getAdditionalConfig() != null && body.getAdditionalConfig().getInventoryProvider() != null && InventoryProviderEnum.ITALIAN_COMPLIANCE.equals(body.getAdditionalConfig().getInventoryProvider())) {
            eventData.setInventoryProvider(Provider.ITALIAN_COMPLIANCE);
        }

        EventConverter.addAuthContact(authUser, eventData);

        Long eventId = inventoryProviderService.createEvent(eventData);

        if (type.equals(EventType.AVET)) {
            createAvetVenueTemplate(body, eventId, venueTemplate);
        }

        createDefaultEventPromotions(eventId, entity.getId());

        return eventId;
    }

    private void validateCompetitionId(VenueTemplateDetailsDTO venueTemplate, Integer competitionId) {
        if (competitionId == null) {
            return;
        }

        EventSearchFilter filter = new EventSearchFilter();
        filter.setOperatorId(SecurityUtils.getUserOperatorId());
        filter.setStatus(es.onebox.mgmt.events.enums.EventStatus.actives().stream().map(Enum::name).toList());
        filter.setVenueId(venueTemplate.getVenue().getId());
        filter.setAvetCompetitions(Collections.singletonList(Long.valueOf(competitionId)));

        Events events = eventsRepository.getEvents(filter);
        boolean hasConflict = events.getData()
                .stream()
                .anyMatch(event -> Objects.equals(event.getExternalId(), Long.valueOf(competitionId)));
        if (hasConflict) {
            throw new OneboxRestException(ApiMgmtErrorCode.COMPETITION_ID_ALREADY_IN_USE);
        }
    }

    private void createAvetVenueTemplate(CreateEventRequestDTO createRequest, Long eventId, VenueTemplateDetailsDTO venueTemplate) {
        try {
            CreateTemplateRequestDTO request = buildCreateTemplateRequest(createRequest, eventId, venueTemplate);
            CreateVenueTemplateRequest venueTemplateRequest = VenueTemplateConverter.convertToDatasource(request);
            venuesRepository.createVenueTemplate(venueTemplateRequest);
        } catch (OneboxRestException e) {
            this.delete(eventId);
            throw new OneboxRestException(e);
        } catch (Exception e) {
            this.delete(eventId);
            throw new OneboxRestException(ApiMgmtErrorCode.GENERIC_ERROR, "Error creating Venue Template. Event not created.", null);
        }
    }

    private VenueTemplateDetailsDTO getVenueTemplate(Long venueTemplateId){
        try {
            return venueTemplatesService.getVenueTemplate(venueTemplateId);
        } catch (
                OneboxRestException e) { // no debería de hacer falta pero se pierde el error code de la exception original
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND, "venue_template_id not found. Event not created.", null);
        }
    }

    private static CreateTemplateRequestDTO buildCreateTemplateRequest(CreateEventRequestDTO createRequest, Long eventId, VenueTemplateDetailsDTO venueTemplate) {
        CreateTemplateRequestDTO request = new CreateTemplateRequestDTO();
        request.setName(venueTemplate.getName());
        request.setEventId(eventId);
        request.setEntityId(createRequest.getEntityId());
        request.setType(VenueTemplateTypeDTO.AVET);
        request.setFromTemplateId(venueTemplate.getId());
        request.setImage(Optional.empty());
        return request;
    }

    private void createDefaultEventPromotions(Long eventId, Long entityId) {
        PromotionTemplateFilter filter = new PromotionTemplateFilter();
        filter.setEntityId(entityId);
        filter.setFavorite(Boolean.TRUE);

        EventPromotionTemplates templates = entityPromotionsRepository.getEventPromotionTemplates(filter);
        while (templates.getMetadata().getTotal() > (filter.getOffset() + filter.getLimit())) {
            filter.setOffset(filter.getOffset() + filter.getLimit());
            templates.getData().addAll(entityPromotionsRepository.getEventPromotionTemplates(filter).getData());
        }

        List<ClonePromotion> clonePromotions = templates.getData()
                .stream()
                .map(template -> {
                    ClonePromotion clonePromotion = new ClonePromotion();
                    clonePromotion.setEntityPromotionTemplateId(template.getId());
                    clonePromotion.setName(template.getName());
                    return clonePromotion;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(clonePromotions)) {
            eventPromotionsRepository.cloneEventPromotionsFromEntityTemplates(eventId, clonePromotions);
        }
    }

    public void updateEvent(Long eventId, UpdateEventRequestDTO eventDTO) {
        Event eventToUpdate = validationService.getAndCheckEvent(eventId);

        validateUpdate(eventDTO, eventToUpdate);

        Event event = EventConverter.toMsEvent(eventDTO);
        event.setId(eventId);

        fillLanguages(eventDTO, event, eventToUpdate.getEntityId());
        fillCurrency(eventDTO, event, eventToUpdate.getEntityId());


        if (event.getCurrencyId() != null) {
            if (eventToUpdate.getSalesGoalRevenue() != null) {
                event.setSalesGoalRevenue((double) 0);
            }
            resetEventCurrencyValues(eventId, event.getCurrencyId());
            if(eventToUpdate.getUseTieredPricing()) {
                Tiers tiers= eventsRepository.getEventTiers(eventId, null, null, null, null);
                tiers.getData().forEach(tier -> {
                        tier.setPrice((double)0);
                        eventsRepository.updateEventTier(eventId, tier.getId(), tier);
                });
            }

        }
        EventAttendantsConfigDTO oldConfig = updateAttendantsTicketInfo(eventId, eventDTO);

        try {
            eventsRepository.updateEvent(event);
        } catch (OneboxRestException e) {
            updateAttendantsRollback(eventId, eventDTO, oldConfig);
            throw e;
        }
    }

    public void resetEventCurrencyValues(Long eventId, Long currencyId){
        eventPromotionsRepository.resetDiscountEventPromotions(eventId, currencyId);
        eventsRepository.resetEventVenueTemplatesPricesCurrency(eventId);
        eventsRepository.deleteSurcharge(eventId);

        ConditionsFilterDTO filter = new ConditionsFilterDTO();
        filter.setEventId(eventId);
        ConditionsData response;

        DeleteConditionsFilter conditionsFilter = new DeleteConditionsFilter();
        conditionsFilter.setEventId(eventId);
        conditionsFilter.setConditionGroupType(ConditionGroupType.CLIENT_B2B_EVENT);

        clientsRepository.deleteClientsConditions(conditionsFilter);

        try {
            response = clientsRepository.getConditions(ConditionsConverter.toMs(filter, GroupType.EVENT));

        } catch (OneboxRestException e) {
            response = null;
        }
        if (response != null){
            clientsRepository.deleteConditions(response.getConditionGroupId().longValue());
        }
    }

    private void validateUpdate(UpdateEventRequestDTO event, Event eventToUpdate) {

        final List<EventStatus> validToArchiveStates = Arrays.asList(EventStatus.CANCELLED, EventStatus.NOT_ACCOMPLISHED, EventStatus.FINISHED);
        final EventStatus newStatus = event.getStatus() != null ? EventStatus.valueOf(event.getStatus().name()) : null;

        Long currentInvoicePrefixId = eventToUpdate.getInvoicePrefixId();
        if(event.getSettings() != null && event.getSettings().getInvoicePrefixId() != null) {
            Long updateInvoicePrefixId = event.getSettings().getInvoicePrefixId();
            if((newStatus != null && !newStatus.equals(EventStatus.IN_PROGRAMMING)
            || (newStatus == null && !eventToUpdate.getStatus().equals(EventStatus.IN_PROGRAMMING)))
                    && (
                    (currentInvoicePrefixId != null
                            && !currentInvoicePrefixId.equals(updateInvoicePrefixId))
                            || (currentInvoicePrefixId == null && updateInvoicePrefixId != null))) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVOICE_PREFIX_CANNOT_BE_MODIFIED);
            }
        }

        if(event.getSettings() != null) {
            if(event.getSettings().getUseProducerFiscalData() == null && eventToUpdate.getUseProducerFiscalData() != null
            && !eventToUpdate.getUseProducerFiscalData()) {
                if(event.getSettings().getInvoicePrefixId() != null || (event.getSettings().getInvoicePrefixId() == null && eventToUpdate.getInvoicePrefixId() != null)) {
                    throw new OneboxRestException(ApiMgmtErrorCode.SIMPLIFIED_INVOICES_CANNOT_BE_USED);
                }
            }
        }

        if (BooleanUtils.isTrue(event.getArchived())
                && (!validToArchiveStates.contains(eventToUpdate.getStatus())
                || newStatus != null && !validToArchiveStates.contains(newStatus))) {
            throw new OneboxRestException(ApiMgmtErrorCode.ARCHIVE_EVENT_NOT_ALLOWED);
        }

        if (BooleanUtils.isTrue(eventToUpdate.getArchived()) && BooleanUtils.isNotFalse(event.getArchived())
                && newStatus != null && !eventToUpdate.getStatus().equals(newStatus)) {
            throw new OneboxRestException(ApiMgmtErrorCode.UPDATE_EVENT_STATE_NOT_ALLOWED);
        }

        boolean eventHasSales = ordersRepository.eventHasOrders(eventToUpdate.getId());

        TaxMode taxMode = event.getSettings() == null ? null : TaxMode.fromDTO(event.getSettings().getTaxMode());
        if (taxMode != null) {
            if (eventHasSales && !eventToUpdate.getTaxMode().equals(taxMode)) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_UPDATE_TAX_MODE_HAS_SALES);
            }
            if (!TaxMode.INCLUDED.equals(taxMode) && es.onebox.mgmt.datasources.ms.event.dto.event.EventType.AVET.equals(eventToUpdate.getType())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_TAX_MODE_NOT_ALLOWED);
            }
        }

        if (event.getCurrencyCode() != null) {
            //Validate SaleRequests and Orders
            if(eventHasSales || getEventHasSaleRequests(eventToUpdate.getId())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_UPDATE_EVENT_CURRENCY_HAS_SALES);
            }

            String eventStatus = null;

            if (event.getStatus() != null) {
                eventStatus = event.getStatus().name();

            } else if (eventToUpdate.getStatus() != null){
                eventStatus = eventToUpdate.getStatus().name();
            }

            if (eventStatus != null && (eventStatus.equals(EventStatus.READY.name()) || eventStatus.equals(EventStatus.FINISHED.name())  || eventStatus.equals(EventStatus.NOT_ACCOMPLISHED.name()))){
                    throw new OneboxRestException(ApiMgmtErrorCode.EVENT_UPDATE_EVENT_CURRENCY_IS_PUBLISHED);
            }
        }

        if (event.getSettings() != null) {
            if (event.getSettings().getCategories() != null) {
                if (event.getSettings().getCategories().getBase() != null && event.getSettings().getCategories().getBase().getId() != null) {
                    validationService.checkCategory(event.getSettings().getCategories().getBase().getId().intValue());
                }
                if (event.getSettings().getCategories().getCustom() != null && event.getSettings().getCategories().getCustom().getId() != null) {
                    validationService.checkCustomCategory(eventToUpdate.getEntityId(), event.getSettings().getCategories().getCustom().getId());
                }
            }
            if (event.getSettings().getInteractiveVenue() != null) {
                SettingsInteractiveVenueDTO settingsInteractiveVenueDTO = event.getSettings().getInteractiveVenue();

                if(BooleanUtils.isTrue(settingsInteractiveVenueDTO.getAllowInteractiveVenue()) && settingsInteractiveVenueDTO.getInteractiveVenueType() == null){
                    throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG);
                }

                Entity entity = entitiesRepository.getEntity(eventToUpdate.getEntityId());

                if (entity.getInteractiveVenue() == null || CommonUtils.isFalse(entity.getInteractiveVenue().getEnabled())) {
                    throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_EVENT_INTERACTIVE_VENUE_UPDATE);
                }
                InteractiveVenueType allowedVenue = settingsInteractiveVenueDTO.getInteractiveVenueType();
                if (allowedVenue != null && entity.getInteractiveVenue().getAllowedVenues().stream()
                        .noneMatch(vt -> vt.name().equals(allowedVenue.name()))) {
                    throw ExceptionBuilder.build(ApiMgmtErrorCode.INTERACTIVE_VENUE_TYPE_NOT_FROM_ENTITY, allowedVenue.name());
                }
            }
            if (event.getSettings().getAccommodationsConfig() != null) {
                if (CommonUtils.isTrue(event.getSettings().getAccommodationsConfig().getEnabled())) {
                    if (event.getSettings().getAccommodationsConfig().getVendor() == null
                        || StringUtils.isBlank(event.getSettings().getAccommodationsConfig().getValue())) {
                        throw new OneboxRestException(ApiMgmtErrorCode.EVENT_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED);
                    }
                }
            }
        }
    }

    private EventAttendantsConfigDTO updateAttendantsTicketInfo(Long eventId, UpdateEventRequestDTO event) {
        EventAttendantsConfigDTO oldConfig = null;
        if (event.getSettings() != null && event.getSettings().getAttendantTickets() != null) {
            oldConfig = attendantTicketsRepository.getEventAttendantsConfig(eventId);
            EventAttendantsConfigDTO newConfig = AttendantsConverter.toMsEvent(eventId, event.getSettings().getAttendantTickets());
            if (!Objects.equals(oldConfig, newConfig)) {
                validateAttendantsUpdate(event.getSettings().getAttendantTickets());
                attendantTicketsRepository.upsertEventAttendantsConfig(eventId, newConfig);
            } else {
                event.getSettings().setAttendantTickets(null);
            }
        }
        return oldConfig;
    }

    private void updateAttendantsRollback(Long eventId, UpdateEventRequestDTO event,
                                          EventAttendantsConfigDTO eventAttendantsConfig) {
        if (event.getSettings() != null && event.getSettings().getAttendantTickets() != null) {
            if (eventAttendantsConfig == null) {
                attendantTicketsRepository.deleteEventAttendantsConfig(eventId);
            } else {
                attendantTicketsRepository.upsertEventAttendantsConfig(eventId, eventAttendantsConfig);
            }
        }
    }

    private void validateAttendantsUpdate(EventAttendantTicketsDTO attendant) {
        if (AttendantTicketsEventStatusDTO.DISABLED.equals(attendant.getStatus()) && attendant.getChannelsScope() != null) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANCE_DATA_CONFLICT, "set to disabled but channel scope information is included");
        }

        if (attendant.getChannelsScope() != null && AttendantTicketsChannelScopeTypeDTO.ALL.equals(attendant.getChannelsScope().getType())) {
            if (CollectionUtils.isNotEmpty(attendant.getChannelsScope().getChannels())) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANCE_DATA_CONFLICT, "set to all channels but contains a channel list");
            }
            if (BooleanUtils.isTrue(attendant.getChannelsScope().getAddNewEventChannelRelationships())) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANCE_DATA_CONFLICT, "set to all channels auto-add flag included");
            }
        }
    }

    public void delete(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);
        Long entityId = event.getEntityId();

        Provider provider = event.getInventoryProvider();

        String providerId = provider == null ?
                null : provider.getCode();

        InventoryProviderService inventoryProviderService =
                externalInventoryProviderServiceFactory.getIntegrationService(entityId, providerId);

        Event updateStatus = new Event();
        updateStatus.setId(eventId);
        updateStatus.setStatus(EventStatus.DELETED);

        inventoryProviderService.deleteEvent(entityId, updateStatus);
    }

    public void updateActivityExternalInventory(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);

        if (EventStatus.DELETED.equals(event.getStatus())
            || EventStatus.CANCELLED.equals(event.getStatus())
            || EventStatus.FINISHED.equals(event.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_INVALID_STATUS);
        }

        if (!es.onebox.mgmt.datasources.ms.event.dto.event.EventType.ACTIVITY.equals(event.getType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_INVALID_TYPE);
        }
        if (!Provider.SGA.equals(event.getInventoryProvider())){
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_IS_NOT_SGA);
        }

        dispatcherRepository.updateActivityInventory(event.getEntityId(), eventId);
    }

    public List<AttributeDTO> getEventAttributes(Long eventId, boolean fullLoad) {
        Event event = validationService.getAndCheckEvent(eventId);

        List<Attribute> eventAttributes = eventsRepository.getEventAttributes(eventId);

        AttributeSearchFilter filter = new AttributeSearchFilter();
        filter.setScope(AttributeScope.EVENT);
        List<es.onebox.mgmt.datasources.ms.entity.dto.Attribute> attributes = entitiesRepository.getAttributes(event.getEntityId(), filter);

        Map<Long, String> attrAssignedValues = eventAttributes.stream()
                .filter(ea -> StringUtils.isNotEmpty(ea.getValue()))
                .collect(Collectors.toMap(Attribute::getId, Attribute::getValue));

        Map<Long, List<Long>> validAttrValueIds = eventAttributes.stream()
                .filter(eventAttribute -> eventAttribute.getSelected() != null)
                .collect(Collectors.toMap(Attribute::getId, Attribute::getSelected));

        Map<Long, String> languages = masterdataService.getLanguagesByIds();
        List<AttributeDTO> result = new ArrayList<>();

        for (es.onebox.mgmt.datasources.ms.entity.dto.Attribute attribute : attributes) {
            if (!fullLoad) {
                attribute.getTexts().getValues().removeIf(av -> validAttrValueIds.get(attribute.getId()) != null && !validAttrValueIds.get(attribute.getId()).contains(av.getId()));
            }
            AttributeDTO attributeDTO = AttributeConverter.fromMsEntity(attribute, languages, attrAssignedValues.get(attribute.getId()));
            if (CollectionUtils.isNotEmpty(validAttrValueIds.get(attribute.getId()))) {
                attributeDTO.setSelectedValuesIds(validAttrValueIds.get(attribute.getId()));
            }
            if (fullLoad || attrAssignedValues.get(attribute.getId()) != null || CollectionUtils.isNotEmpty(validAttrValueIds.get(attribute.getId()))) {
                result.add(attributeDTO);
            }
        }

        return result;
    }

    public void putEventAttributeValue(Long eventId, AttributeRequestValuesDTO attributeRequestValuesDTO) {
        Event event = validationService.getAndCheckEvent(eventId);
        eventsRepository.putEventAttributes(event.getId(), attributeRequestValuesDTO);
    }

    public AdditionalConfigMatchesDTO getEventAdditionalConfig(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);

        List<Match> matches = avetConfigRepository.getMatches(event.getExternalId());

        final EventSearchFilter eventSearchFilter = new EventSearchFilter();
        eventSearchFilter.setOperatorId(SecurityUtils.getUserOperatorId());
        eventSearchFilter.setAvetCompetitions(List.of(event.getExternalId()));

        Events events = eventsRepository.getEvents(eventSearchFilter);
        List<Long> eventIds = events.getData()
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        Sessions sessions = eventsRepository.getSessionsByEventIds(SecurityUtils.getUserOperatorId(), eventIds, sessionSearchFilter);

        List<Match> unusedMatches = matches.stream()
                .filter(match -> sessions.getData().stream().noneMatch(session -> session.getExternalId().equals(match.getMatchId())))
                .collect(Collectors.toList());

        return MatchConverter.listFromMs(unusedMatches);
    }

    private void fillLanguages(UpdateEventRequestDTO event, Event eventDTO, Long eventEntityId) {
        if (event.getSettings() != null && event.getSettings().getLanguages() != null) {
            Entity entity = entitiesRepository.getCachedEntity(eventEntityId);
            Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
            LanguagesDTO eventLanguages = event.getSettings().getLanguages();
            if (!CommonUtils.isEmpty(eventLanguages.getSelected())) {
                eventDTO.setLanguages(new ArrayList<>());
                for (String langCode : eventLanguages.getSelected()) {
                    boolean isDefault = langCode.equals(eventLanguages.getDefaultLanguage());
                    String locale = ConverterUtils.checkLanguage(langCode, languages);
                    Long languageId = languages.get(locale);
                    if (entity.getSelectedLanguages().stream().noneMatch(l -> l.getId().equals(languageId))) {
                        throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
                    }
                    eventDTO.getLanguages().add(new EventLanguage(languageId, isDefault));
                }
            }
        }
    }

    private void fillCurrency(UpdateEventRequestDTO event, Event eventDTO, Long eventEntityId) {
        Operator operator = entitiesRepository.getCachedOperator(eventEntityId);
        if (BooleanUtils.isTrue(operator.getUseMultiCurrency()) && event.getCurrencyCode() != null) {
            eventDTO.setCurrencyId(operator.getCurrencies()
                    .getSelected()
                    .stream()
                    .filter(currency -> currency.getCode().equals(event.getCurrencyCode()))
                    .findFirst()
                    .map(Currency::getId)
                    .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.EVENT_CURRENCY_NOT_MATCH_OPERATOR)));
        }
    }

    private void fillEventDTO(BaseEventDTO eventDTO, Event event) {
        if (!CommonUtils.isEmpty(eventDTO.getVenueTemplates())) {
            Map<Long, String> venueCountries = new HashMap<>();
            for (Venue venue : event.getVenues()) {
                String country = masterdataService.getCountry(venue.getCountryId().longValue()).getCode();
                venueCountries.put(venue.getId(), country);
            }
            for (EventVenueTemplateDTO venue : eventDTO.getVenueTemplates()) {
                venue.getVenue().setCountry(venueCountries.get(venue.getVenue().getId()));
            }
        }
    }

    private void setCurrencyDefaultByOperatorMulticurrency(EventDTO eventDTO, Operator operator) {
        if (BooleanUtils.isTrue(operator.getUseMultiCurrency()) && eventDTO.getSettings() != null &&
                eventDTO.getCurrencyCode() == null) {
            eventDTO.setCurrencyCode(
                    operator.getCurrencies() != null ?
                            operator.getCurrencies().getSelected()
                                    .stream()
                                    .filter(operatorCurrency -> operatorCurrency.getCode().equals(operator.getCurrencies().getDefaultCurrency()))
                                    .findFirst()
                                    .map(Currency::getCode)
                                    .orElse(null)
                            : null
            );
        }
    }

    private void validateAVETFields(CreateEventRequestDTO eventData) {
        if (EventType.AVET.equals(eventData.getType())) {
            if (eventData.getAdditionalConfig() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "additional_config is mandatory in an Avet Event",
                        null);
            }
            if (eventData.getAdditionalConfig().getAvetCompetitionId() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "avet_competition_id is mandatory in an Avet Event",
                        null);
            }
            if (eventData.getAdditionalConfig().getAvetConfig() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "avet_config is mandatory in an Avet Event", null);
            }
            if (eventData.getAdditionalConfig().getVenueTemplateId() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "venue_template_id is mandatory in an Avet Event",
                        null);
            }
        }
    }

    public Long getDefaultInvoicePrefixId(Long producerId) {
        Producer producer = entitiesRepository.getProducer(producerId);
        if (BooleanUtils.isTrue(producer.getUseSimplifiedInvoice())) {
            ProducerInoivcePrefixFilter filter = new ProducerInoivcePrefixFilter();
            filter.setDefult(Boolean.TRUE);
            return entitiesRepository.getProducerInvoicePrefixes(producerId, filter)
                    .getData()
                    .stream()
                    .filter(p -> BooleanUtils.isTrue(p.getDefaultPrefix()))
                    .map(InvoicePrefix::getId)
                    .findFirst().orElse(null);
        }
        return null;
    }

    private boolean getEventHasSaleRequests(Long eventId) {
        EventChannels eventChannels = eventChannelsRepository.getEventChannels(eventId, null);
        return (eventChannels != null && eventChannels.getData() != null && !eventChannels.getData().isEmpty());
    }
}

package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationsSearchRequest;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionOccupationRepository;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dto.EventChannelsDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.enums.EventChannelStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.service.EventChannelService;
import es.onebox.event.events.service.EventService;
import es.onebox.event.events.service.EventTemplateService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.sessions.converter.DynamicPriceConverter;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPrice;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPriceZone;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import es.onebox.event.sessions.dto.ConditionType;
import es.onebox.event.sessions.dto.DynamicPriceDTO;
import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import es.onebox.event.sessions.dto.DynamicPriceZoneDTO;
import es.onebox.event.sessions.dto.DynamicRatesPriceDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionDynamicPriceConfigDTO;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.utils.DynamicPriceValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionDynamicPriceService {
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final SessionService sessionService;
    private final VenuesRepository venuesRepository;
    private final EventService eventService;
    private final SessionOccupationRepository sessionOccupationRepository;
    private final RefreshDataService refreshDataService;
    private final CatalogEventCouchDao catalogEventCouchDao;
    private final EventChannelService eventChannelService;
    private final EventLanguageDao eventLanguageDao;
    private final EventTemplateService eventTemplateService;

    private static final int TICKETS_CACHE = 15;


    @Autowired
    public SessionDynamicPriceService(SessionConfigCouchDao sessionConfigCouchDao, SessionService sessionService,
                                      VenuesRepository venuesRepository, EventService eventService,
                                      SessionOccupationRepository sessionOccupationRepository, CatalogEventCouchDao catalogEventCouchDao,
                                      RefreshDataService refreshDataService, EventChannelService eventChannelService,
                                      EventLanguageDao eventLanguageDao, EventTemplateService eventTemplateService) {

        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.sessionService = sessionService;
        this.venuesRepository = venuesRepository;
        this.eventService = eventService;
        this.sessionOccupationRepository = sessionOccupationRepository;
        this.refreshDataService = refreshDataService;
        this.catalogEventCouchDao = catalogEventCouchDao;
        this.eventChannelService = eventChannelService;
        this.eventLanguageDao = eventLanguageDao;
        this.eventTemplateService = eventTemplateService;
    }

    public void updateActivationDynamicPrice(Long eventId, Long sessionId, Boolean status) {
        EventDTO eventDTO = eventService.getEvent(eventId);
        if(eventDTO.getUseTieredPricing()){
            throw new OneboxRestException(MsEventErrorCode.CANNOT_ACTIVATED_DYNAMIC_PRICE_IF_TIERED_PRICING_ACTIVE);
        }
        if(eventDTO.getType().equals(EventType.SEASON_TICKET) || eventDTO.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(MsEventErrorCode.CANNOT_ACTIVATED_DYNAMIC_PRICE_FOR_THIS_TYPE_EVENT);
        }
        EventChannelSearchFilter filter = new EventChannelSearchFilter();
        EventChannelsDTO channels = eventChannelService.getEventChannels(eventId, filter);
        boolean hasNonV4Channels = false;

        if (channels.getData() != null && !channels.getData().isEmpty()) {
            hasNonV4Channels = channels.getData().stream()
                    .anyMatch(channel -> {
                        boolean isNonV4Channel = channel.getChannel() == null ||
                                channel.getChannel().getV4Enabled() == null ||
                                !channel.getChannel().getV4Enabled();

                        if (isNonV4Channel) {
                            return channel.getStatus() != null &&
                                    channel.getStatus().getRequest() != null &&
                                    (channel.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) ||
                                            channel.getStatus().getRequest().equals(EventChannelStatus.PENDING));
                        }
                        return false;
                    });
        }

        if (hasNonV4Channels && BooleanUtils.isTrue(status)) {
            throw new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICES_REQUIRE_V4_CHANNEL);
        }
        SessionDTO sessionDTO = sessionService.getSessionWithoutEventId(sessionId);
        if(sessionDTO.getStatus().equals(SessionStatus.READY)){
            throw new OneboxRestException(MsEventErrorCode.CANNOT_UPDATE_DYNAMIC_PRICE_FOR_ACTIVE_SESSION);
        }
        if(BooleanUtils.isTrue(status) && eventDTO.getType().equals(EventType.ACTIVITY) && !sessionDTO.getSaleType().equals(SessionSalesType.INDIVIDUAL.getType())){
            throw new OneboxRestException(MsEventErrorCode.CANNOT_ACTIVATED_DYNAMIC_PRICE_FOR_ACTIVITY_WITH_GROUPS);
        }

        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        SessionDynamicPriceConfig config = sessionConfig.getSessionDynamicPriceConfig();
        if (config == null) {
            config = new SessionDynamicPriceConfig();
        }
        config.setActive(status);
        sessionConfig.setSessionDynamicPriceConfig(config);
        sessionConfigCouchDao.upsert(String.valueOf(sessionId), sessionConfig);
    }

    public SessionDynamicPriceConfigDTO getSessionDynamicPriceConfig(Long sessionId, Boolean initialize) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        SessionDynamicPriceConfig config = sessionConfig.getSessionDynamicPriceConfig();

        SessionDTO sessionDTO = sessionService.getSessionWithoutEventId(sessionId);
        List<IdNameCodeDTO> pricesTypes = venuesRepository.getPriceTypes(sessionDTO.getVenueConfigId());

        if (initialize) {
            config = DynamicPriceConverter.initDynamicPricesConfig(config, pricesTypes);
            sessionConfig.setSessionDynamicPriceConfig(config);
            sessionConfigCouchDao.upsert(String.valueOf(sessionId), sessionConfig);
        }

        return DynamicPriceConverter.toDTO(config, sessionDTO);
    }

    public List<DynamicRatesPriceDTO> getDynamicRatePrice(Long sessionId, Long idPriceZone) {
        SessionDynamicPriceConfig config = sessionConfigCouchDao.findDynamicPriceBySessionId(sessionId);
        SessionDTO sessionDTO = sessionService.getSessionWithoutEventId(sessionId);

        DynamicPriceZone dynamicPriceZone = config.getDynamicPriceZone().stream()
                .filter(zone -> zone.getIdPriceZone().equals(idPriceZone))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICE_ZONE_NOT_FOUND));

        Map<Long, String> rateMap = DynamicPriceConverter.createRateMap(sessionDTO.getRates());

        return dynamicPriceZone.getDynamicPrices().stream()
                .flatMap(price -> DynamicPriceConverter.toDynamicRatesPriceDTOList(price.getDynamicRatesPrice(), rateMap).stream())
                .toList();
    }

    public void createOrUpdateSessionDynamicPrices(Long sessionId, Long idPriceZone, List<DynamicPriceDTO> requests) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        SessionDTO sessionDTO = sessionService.getSessionWithoutEventId(sessionId);
        SessionDynamicPriceConfig sessionDynamicPriceConfig;
        if (sessionConfig.getSessionDynamicPriceConfig() == null) {
            sessionDynamicPriceConfig = new SessionDynamicPriceConfig();
            sessionConfig.setSessionDynamicPriceConfig(sessionDynamicPriceConfig);
            updateActivationDynamicPrice(sessionDTO.getEventId(), sessionId, true);
        } else {
            sessionDynamicPriceConfig = sessionConfig.getSessionDynamicPriceConfig();
            updateActivationDynamicPrice(sessionDTO.getEventId(), sessionId, true);
        }

        if (CollectionUtils.isEmpty(sessionDynamicPriceConfig.getDynamicPriceZone())) {
            sessionDynamicPriceConfig.setDynamicPriceZone(new ArrayList<>());
        }

        ZonedDateTime creationDate = ZonedDateTime.now();
        ZonedDateTimeWithRelative saleStartDate = sessionDTO.getDate().getSalesStart();
        requests.sort(Comparator.comparingInt(DynamicPriceDTO::getOrder));

        DynamicPriceZone dynamicPriceZone = sessionDynamicPriceConfig.getDynamicPriceZone().stream()
                .filter(zone -> Objects.equals(zone.getIdPriceZone(), idPriceZone))
                .findFirst()
                .orElseGet(() -> {
                    DynamicPriceZone newZone = new DynamicPriceZone();
                    newZone.setIdPriceZone(idPriceZone);
                    newZone.setDynamicPrices(new ArrayList<>());
                    sessionDynamicPriceConfig.getDynamicPriceZone().add(newZone);
                    return newZone;
                });

        initializeAndValidateTranslations(requests, sessionDTO.getEventId());
        List<DynamicPriceDTO> existingDTOsToUpdate = dynamicPriceZone.getDynamicPrices().stream()
                .map(price -> DynamicPriceConverter.toDTO(price, sessionDTO.getRates()))
                .collect(Collectors.toList());

        long activeZoneOrder = (dynamicPriceZone.getActiveZone() != null) ? dynamicPriceZone.getActiveZone() : 0L;
        if (sessionDTO.getStatus().name().equals(SessionStatus.READY.name()) || sessionDTO.getDate().getSalesStart().absolute().isBefore(ZonedDateTime.now())) {
            activeZoneOrder =  activeZoneOrder+1;
        }
        requests.sort(Comparator.comparingInt(DynamicPriceDTO::getOrder));
        for (int i = 1; i < requests.size(); i++) {
            if (requests.get(i).getOrder() != requests.get(i-1).getOrder() +1) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_ORDER_SEQUENCE);
            }
        }
        if (dynamicPriceZone.getActiveZone() != null) {
            final int maxExistingOrder = dynamicPriceZone.getDynamicPrices().isEmpty() ? -1 : dynamicPriceZone.getDynamicPrices().size() - 1;

            if (activeZoneOrder >= maxExistingOrder + 1) {
                requests = requests.stream()
                        .filter(req -> req.getOrder() > maxExistingOrder)
                        .collect(Collectors.toList());
            } else {
                int safeActiveZoneOrder = Math.min((int) activeZoneOrder, existingDTOsToUpdate.size());
                existingDTOsToUpdate = existingDTOsToUpdate.subList(0, safeActiveZoneOrder);

                final long finalActiveZoneOrder = activeZoneOrder;
                requests = requests.stream()
                    .filter(req -> req.getOrder() >= finalActiveZoneOrder || req.getOrder() > maxExistingOrder)
                    .collect(Collectors.toList());
            }
                
            requests.forEach(request -> DynamicPriceValidator.validateDynamicPrice(request, creationDate, saleStartDate));
        } else {
            dynamicPriceZone.setActiveZone(0L);
            requests.forEach(request -> DynamicPriceValidator.validateDynamicPrice(request, creationDate, saleStartDate));
        }
        existingDTOsToUpdate.addAll(requests);
        List<DynamicPrice> dynamicPrices = existingDTOsToUpdate.stream()
                .map(DynamicPriceConverter::toEntity)
                .toList();

        List<Long> sessionIdList = Collections.singletonList(sessionId);
        dynamicPriceZone.setDefaultPrice(DynamicPriceConverter.toDynamicPriceDTOList(eventTemplateService.getPrices(sessionDTO.getEventId(), sessionDTO.getVenueConfigId(), sessionIdList, null, null), idPriceZone));
        DynamicPriceValidator.validateCapacitySequence(existingDTOsToUpdate);
        DynamicPriceValidator.validateDateSequence(existingDTOsToUpdate);
        dynamicPriceZone.setDynamicPrices(dynamicPrices);
        sessionDynamicPriceConfig.setActive(true);
        sessionConfigCouchDao.upsert(String.valueOf(sessionId), sessionConfig);
        refreshDataService.refreshSession(sessionId, "createOrUpdateSessionDynamicPrices");
    }

    public void deleteSessionDynamicPrice(Long sessionId, Long idPriceZone, Integer orderId) {
        SessionConfig sessionConfig = sessionConfigCouchDao.get(String.valueOf(sessionId));
        SessionDTO sessionDTO = sessionService.getSessionWithoutEventId(sessionId);

        if (sessionConfig == null || sessionConfig.getSessionDynamicPriceConfig() == null) {
            throw new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICE_CONFIG_NOT_FOUND);
        }

        SessionDynamicPriceConfig sessionDynamicPriceConfig = sessionConfig.getSessionDynamicPriceConfig();
        DynamicPriceZone dynamicPriceZone = sessionDynamicPriceConfig.getDynamicPriceZone().stream()
                .filter(zone -> zone.getIdPriceZone().equals(idPriceZone))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICE_ZONE_NOT_FOUND));

        long activeZoneOrder = (dynamicPriceZone.getActiveZone() != null) ? dynamicPriceZone.getActiveZone() : 0L;
        boolean isSessionActive = sessionDTO.getStatus().name().equals(SessionStatus.READY.name()) ||
                sessionDTO.getDate().getSalesStart().absolute().isBefore(ZonedDateTime.now());
        if (orderId < activeZoneOrder) {
            throw new OneboxRestException(MsEventErrorCode.CANNOT_DELETE_PASS_DYNAMIC_PRICE);
        } else if (orderId == activeZoneOrder && isSessionActive) {
            throw new OneboxRestException(MsEventErrorCode.CANNOT_DELETE_ACTIVE_DYNAMIC_PRICE);
        }

        boolean priceExists = dynamicPriceZone.getDynamicPrices().stream()
                .anyMatch(p -> p.getOrder().equals(orderId));
        if (!priceExists) {
            throw new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICE_NOT_FOUND);
        }

        dynamicPriceZone.getDynamicPrices().removeIf(p -> p.getOrder().equals(orderId));
        reorderAndSetStatuses(dynamicPriceZone.getDynamicPrices());

        DynamicPriceValidator.validateReordering(dynamicPriceZone.getDynamicPrices());

        if (dynamicPriceZone.getDynamicPrices().isEmpty()) {
            sessionDynamicPriceConfig.getDynamicPriceZone().remove(dynamicPriceZone);
            if (sessionDynamicPriceConfig.getDynamicPriceZone().isEmpty()) {
                sessionDynamicPriceConfig.setActive(false);
            }
        }
        sessionConfigCouchDao.upsert(String.valueOf(sessionId), sessionConfig);
    }

    private void initializeAndValidateTranslations(List<DynamicPriceDTO> requests, Long eventId) {
        Set<String> validCodes = eventLanguageDao.findByEventId(eventId).stream()
                .map(EventLanguageRecord::getCode)
                .collect(Collectors.toUnmodifiableSet());

        requests.forEach(req -> {
            List<DynamicPriceTranslationDTO> translations = Optional.ofNullable(req.getTranslationsDTO())
                    .orElseGet(() -> {
                        List<DynamicPriceTranslationDTO> list = new ArrayList<>();
                        req.setTranslationsDTO(list);
                        return list;
                    });

            Set<String> existing = translations.stream()
                    .map(DynamicPriceTranslationDTO::getLanguage)
                    .collect(Collectors.toSet());

            existing.stream()
                    .filter(code -> !validCodes.contains(code))
                    .findFirst()
                    .ifPresent(code -> {
                        throw new OneboxRestException(MsEventErrorCode.INVALID_LANGUAGE_CODE);
                    });

            validCodes.stream()
                    .filter(code -> !existing.contains(code))
                    .forEach(code -> {
                        DynamicPriceTranslationDTO t = new DynamicPriceTranslationDTO();
                        t.setLanguage(code);
                        t.setValue(req.getName());
                        translations.add(t);
                    });
        });
    }

    private void reorderAndSetStatuses(List<DynamicPrice> prices) {
        prices.sort(Comparator.comparingInt(DynamicPrice::getOrder));

        int newOrder = 0;
        for (DynamicPrice price : prices) {
            price.setOrder(newOrder);
            newOrder++;
        }
    }

    public DynamicPriceZoneDTO getActive(Long eventId, Long sessionId, Long idPriceZone) {
        SessionConfig sessionConfig = sessionConfigCouchDao.get(String.valueOf(sessionId));

        if (sessionConfig == null || sessionConfig.getSessionDynamicPriceConfig() == null
                || CollectionUtils.isEmpty(sessionConfig.getSessionDynamicPriceConfig().getDynamicPriceZone()) || BooleanUtils.isNotTrue(sessionConfig.getSessionDynamicPriceConfig().getActive())) {
            DynamicPriceZoneDTO dynamicPriceZoneDTONotActive = new DynamicPriceZoneDTO();
            dynamicPriceZoneDTONotActive.setIdPriceZone(idPriceZone);
            dynamicPriceZoneDTONotActive.setActive(false);
            return dynamicPriceZoneDTONotActive;
        }

        Event eventCatalog  = catalogEventCouchDao.get(eventId.toString());
        if (eventCatalog == null || CollectionUtils.isEmpty(eventCatalog.getRates())) {
            throw new OneboxRestException(MsEventErrorCode.SESSION_NOT_FOUND);
        }
        SessionDynamicPriceConfig config = sessionConfig.getSessionDynamicPriceConfig();

        DynamicPriceZone dynamicPriceZone = config.getDynamicPriceZone().stream()
                .filter(zone -> zone.getIdPriceZone().equals(idPriceZone))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICE_ZONE_NOT_FOUND));

        Long actualActiveZone = dynamicPriceZone.getActiveZone();

       if(actualActiveZone != null && CollectionUtils.isNotEmpty(dynamicPriceZone.getDynamicPrices())) {
            Long capacity = validateLastOrder(dynamicPriceZone, eventCatalog, sessionId, idPriceZone);
            nextOrder(capacity, dynamicPriceZone, eventCatalog, sessionId, idPriceZone);

            if (!Objects.equals(actualActiveZone, dynamicPriceZone.getActiveZone())) {
                sessionConfigCouchDao.upsert(String.valueOf(sessionId), sessionConfig);
                refreshDataService.refreshSession(sessionId, "getActive");
            }
        }

       return DynamicPriceConverter.convertZone(dynamicPriceZone, eventCatalog, config.getActive());
    }

    private Long validateLastOrder(DynamicPriceZone dynamicPriceZone, Event eventCatalog, Long idSession, Long idPriceZone) {
        Long capacity = null;
        if (dynamicPriceZone.getDynamicPrices() == null || dynamicPriceZone.getDynamicPrices().isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICE_NOT_FOUND);
        }
        DynamicPrice dynamicPrice  = dynamicPriceZone.getDynamicPrices().get(dynamicPriceZone.getDynamicPrices().size() - 1);
        if(dynamicPriceZone.getActiveZone() != null && dynamicPriceZone.getDynamicPrices() != null && dynamicPriceZone.getDynamicPrices().size() > dynamicPriceZone.getActiveZone()) {
         if(dynamicPriceZone.getActiveZone() == 0){
             dynamicPrice = dynamicPriceZone.getDynamicPrices().get(dynamicPriceZone.getActiveZone().intValue());
         } else {
             dynamicPrice = dynamicPriceZone.getDynamicPrices().get(dynamicPriceZone.getActiveZone().intValue()-1);
         }
        }
        if (dynamicPrice.getConditionTypes().contains(ConditionType.CAPACITY)) {
            capacity = getCapacityPriceZone(eventCatalog, idSession, idPriceZone, dynamicPrice.getCapacity().longValue());
            if (capacity < dynamicPrice.getCapacity()) {
                dynamicPriceZone.setActiveZone(dynamicPrice.getOrder().longValue());
            }
        }
        return capacity;
    }

    private DynamicPrice nextOrder(Long capacity, DynamicPriceZone dynamicPriceZone, Event eventCatalog, Long idSession , Long idPriceZone) {
        DynamicPrice dynamicPrice  = dynamicPriceZone.getDynamicPrices().get(dynamicPriceZone.getDynamicPrices().size() - 1);
        if (dynamicPriceZone.getActiveZone() != null && dynamicPriceZone.getDynamicPrices() != null && dynamicPriceZone.getDynamicPrices().size() > dynamicPriceZone.getActiveZone()) {
            dynamicPrice = dynamicPriceZone.getDynamicPrices().get(dynamicPriceZone.getActiveZone().intValue());
        }

        boolean nextOrder = false;
        if (dynamicPrice.getConditionTypes().contains(ConditionType.CAPACITY)) {
            if(capacity == null) {
                capacity = getCapacityPriceZone(eventCatalog, idSession, idPriceZone, dynamicPrice.getCapacity().longValue());
            }
            if (capacity > dynamicPrice.getCapacity()) {
                nextOrder = true;
            }
        }
        if (dynamicPrice.getConditionTypes().contains(ConditionType.DATE)) {
            if (dynamicPrice.getValidDate().isBefore(ZonedDateTime.now())) {
                nextOrder = true;
            }
        }

        if (nextOrder) {
            if (dynamicPrice.getOrder()+1 > dynamicPriceZone.getActiveZone()) {
                dynamicPriceZone.setActiveZone(dynamicPrice.getOrder()+1L);
                dynamicPrice = nextOrder(capacity, dynamicPriceZone, eventCatalog, idSession, idPriceZone);
            }
        }

        return dynamicPrice;
    }

    private Long getCapacityPriceZone(Event eventCatalog, Long idSession, Long idPriceZone, Long dynamicPriceCapacity){
            SessionOccupationsSearchRequest request = new SessionOccupationsSearchRequest();
            SessionWithQuotasDTO session = new SessionWithQuotasDTO();
            session.setSessionId(idSession);
            request.setSessions(Collections.singletonList(session));
            request.setEventType(EventType.byId(eventCatalog.getEventType()));
            List<SessionOccupationByPriceZoneDTO> occupations = sessionOccupationRepository.searchOccupationsByPriceZones(request);
            if (CollectionUtils.isEmpty(occupations)) {
                throw new OneboxRestException(MsEventErrorCode.PRICE_OCCUPATION_NOT_FOUND);
            }
            Long capacity = occupations.stream()
                    .flatMap(occupation -> occupation.getOccupation().stream())
                    .filter(occupation -> occupation.getPriceZoneId().equals(idPriceZone))
                    .map(occupation -> {
                        Map<TicketStatus, Long> statusMap = occupation.getStatus();
                        return statusMap.getOrDefault(TicketStatus.SOLD, 0L) +
                                statusMap.getOrDefault(TicketStatus.BLOCKED_SYSTEM, 0L) +
                                statusMap.getOrDefault(TicketStatus.VALIDATED, 0L) +
                                statusMap.getOrDefault(TicketStatus.BOOKED, 0L) +
                                statusMap.getOrDefault(TicketStatus.ISSUED, 0L) +
                                statusMap.getOrDefault(TicketStatus.INVITATION, 0L);
                    })
                    .reduce(Long::sum)
                    .orElse(0L);
            if (capacity >= dynamicPriceCapacity - TICKETS_CACHE) {
               capacity = sessionOccupationRepository.countSessionOccupationsByPriceZones(idSession, idPriceZone);
            }
            return capacity;
        }
}

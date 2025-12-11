package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.Tax;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.enums.TaxType;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventRates;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.MaxBuyingLimit;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketDatasourceStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketFilter;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketReleaseSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTickets;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSalesType;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketSeatsSummary;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.events.EventsService;
import es.onebox.mgmt.events.converter.EventConverter;
import es.onebox.mgmt.events.dto.EventVenueTemplateDTO;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.seasontickets.amqp.createsession.CreateSeasonTicketSessionMessage;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketConverter;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketFilterConverter;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketStatusConverter;
import es.onebox.mgmt.seasontickets.dto.AdditionalConfigDTO;
import es.onebox.mgmt.seasontickets.dto.BaseSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.SearchSeasonTicketsResponse;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSearchFilter;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSearchResultDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketValidationsRequestDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketValidationsResponseDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketOperativeDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketStatusRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketAssignationStatus;
import es.onebox.mgmt.seasontickets.enums.SeatMappingStatus;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.SessionsService;
import es.onebox.mgmt.venues.converter.VenueTemplateConverter;
import es.onebox.mgmt.venues.dto.CreateTemplateRequestDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.GENERIC_ERROR;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;

@Service
public class SeasonTicketService {

    private final SecurityManager securityManager;
    private final SeasonTicketValidationService validationService;
    private final EntitiesRepository entitiesRepository;
    private final UsersRepository usersRepository;
    private final SeasonTicketRepository seasonTicketRepository;
    private final MasterdataService masterdataService;
    private final VenuesRepository venuesRepository;
    private final DefaultProducer createSeasonTicketSessionProducer;
    private final SeasonTicketRenewalsService seasonTicketRenewalsService;
    private final SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;
    private final TicketsRepository ticketsRepository;
    private final SeasonTicketSessionsService seasonTicketSessionsService;
    private final EventChannelsRepository eventChannelsRepository;
    private final OrdersRepository ordersRepository;
    private final EventsService eventsService;
    private final ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper;

    private static final Double DEFAULT_CUSTOMER_PERCENTAGE = 30.0;
    private static final Integer DEFAULT_MAX_RELEASES = 3;
    private static final Integer DEFAULT_RELEASE_MAX_DELAY_TIME = 3;
    private static final Integer DEFAULT_RECOVER_MAX_DELAY_TIME = 3;

    private final SessionsService sessionsService;
    private final EventsRepository eventsRepository;
    private final DispatcherRepository dispatcherRepository;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    @Autowired
    public SeasonTicketService(SecurityManager securityManager, SeasonTicketValidationService validationService,
                               EntitiesRepository entitiesRepository, UsersRepository usersRepository,
                               SeasonTicketRepository seasonTicketRepository, VenuesRepository venuesRepository,
                               MasterdataService masterdataService,
                               @Qualifier("createSeasonTicketSessionProducer") DefaultProducer createSeasonTicketSessionProducer,
                               SeasonTicketRenewalsService seasonTicketRenewalsService, SeasonTicketChangeSeatsService seasonTicketChangeSeatsService,
                               TicketsRepository ticketsRepository,
                               @Lazy SeasonTicketSessionsService seasonTicketSessionsService,
                               EventChannelsRepository eventChannelsRepository, OrdersRepository ordersRepository,
                               EventsService eventsService, ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper,
                               SessionsService sessionsService, EventsRepository eventsRepository,
                               DispatcherRepository dispatcherRepository, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.securityManager = securityManager;
        this.validationService = validationService;
        this.entitiesRepository = entitiesRepository;
        this.usersRepository = usersRepository;
        this.seasonTicketRepository = seasonTicketRepository;
        this.masterdataService = masterdataService;
        this.venuesRepository = venuesRepository;
        this.createSeasonTicketSessionProducer = createSeasonTicketSessionProducer;
        this.seasonTicketRenewalsService = seasonTicketRenewalsService;
        this.seasonTicketChangeSeatsService = seasonTicketChangeSeatsService;
        this.ticketsRepository = ticketsRepository;
        this.seasonTicketSessionsService = seasonTicketSessionsService;
        this.eventChannelsRepository = eventChannelsRepository;
        this.ordersRepository = ordersRepository;
        this.eventsService = eventsService;
        this.releasedSeatsQuotaHelper = releasedSeatsQuotaHelper;
        this.sessionsService = sessionsService;
        this.eventsRepository = eventsRepository;
        this.dispatcherRepository = dispatcherRepository;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public SeasonTicketDTO getSeasonTicket(Long seasonTicketId) {
        SeasonTicket seasonTicket = getAndCheckSeasonTicket(seasonTicketId);

        SeasonTicketDTO seasonTicketResponse = SeasonTicketConverter.fromMsEvent(seasonTicket, masterdataService.getCurrencies());

        if(seasonTicket.getProducer() != null && seasonTicket.getProducer().getId() != null && seasonTicket.getInvoicePrefixId() != null) {
            InvoicePrefix invoicePrefix = entitiesRepository.getInvoicePrefix(seasonTicket.getProducer().getId(), seasonTicket.getInvoicePrefixId());
            if(invoicePrefix != null) {
                SeasonTicketConverter.addInvoicePrefix(seasonTicketResponse, invoicePrefix);
            }
        }

        // when season ticket is created, it has no session for a while, and cannot query capacity updating,
        // in this case, it has not any capacity update/generation active neither.
        if (seasonTicket.getId() != null && seasonTicket.getSessionId() != null) {
            seasonTicketResponse.setUpdatingCapacity(
                    ticketsRepository.getSessionCapacityUpdating(seasonTicket.getId(), seasonTicket.getSessionId())
            );
            seasonTicketResponse.setGeneratingCapacity(
                    ticketsRepository.getSessionCapacityGenerating(seasonTicket.getId(), seasonTicket.getSessionId())
            );
        }

        augmentSeasonTicketDTO(seasonTicketResponse, seasonTicket);
        if (seasonTicket.getSessionId() != null) {
            seasonTicketResponse.setHasSales(ticketsRepository.getSessionSalesAmount(seasonTicket.getSessionId()) > 0L);
            seasonTicketResponse.setHasSalesRequest(getEventHasSaleRequests(seasonTicketId));
        } else {
            seasonTicketResponse.setHasSales(Boolean.FALSE);
            seasonTicketResponse.setHasSalesRequest(Boolean.FALSE);
        }

        if (seasonTicket.getInventoryProvider() != null) {
            Provider provider = seasonTicket.getInventoryProvider();
            AdditionalConfigDTO additionalConfigDTO = new AdditionalConfigDTO();
            additionalConfigDTO.setInventoryProvider(InventoryProviderEnum.getByCode(provider.getCode()));
            seasonTicketResponse.setAdditionalConfig(additionalConfigDTO);
        }

        return seasonTicketResponse;
    }

    public void deleteSeasonTicket(Long seasonTicketId) {
        SeasonTicket st = seasonTicketRepository.getSeasonTicket(seasonTicketId);

        if (st == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(st.getEntityId());

        InventoryProviderService inventoryProviderService = sessionsService.getInventoryProvider(st.getEntityId(), st.getInventoryProvider());
        inventoryProviderService.deleteSeasonTicket(st.getEntityId(), st);
    }


    private void augmentSeasonTicketDTO(BaseSeasonTicketDTO seasonTicketDTO, SeasonTicket seasonTicket) {
        if (!CommonUtils.isEmpty(seasonTicketDTO.getVenueTemplates())) {
            Map<Long, String> venueCountries = new HashMap<>();
            for (Venue venue : seasonTicket.getVenues()) {
                String country = masterdataService.getCountry(venue.getCountryId().longValue()).getCode();
                venueCountries.put(venue.getId(), country);
            }
            for (EventVenueTemplateDTO venue : seasonTicketDTO.getVenueTemplates()) {
                venue.getVenue().setCountry(venueCountries.get(venue.getVenue().getId()));
            }
        }
    }

    public SearchSeasonTicketsResponse searchSeasonTickets(SeasonTicketSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);

        Long countryId = null;
        if (filter.getCountry() != null) {
            countryId = masterdataService.getCountryIdByCode(filter.getCountry()).longValue();
        }
        List<Currency> currencies;
        currencies = masterdataService.getCurrencies();

        SeasonTicketFilter seasonTicketFilter = SeasonTicketFilterConverter.toMs(SecurityUtils.getUserOperatorId(), filter, countryId, currencies);
        SeasonTickets seasonTickets = seasonTicketRepository.getSeasonTickets(seasonTicketFilter);

        SearchSeasonTicketsResponse response = new SearchSeasonTicketsResponse();
        response.setData(seasonTickets.getData().stream().map(e -> {
            SeasonTicketSearchResultDTO seasonTicketSearchResultDTO = SeasonTicketConverter.fromMsEvent(e, new SeasonTicketSearchResultDTO(), currencies);
            augmentSeasonTicketDTO(seasonTicketSearchResultDTO, e);
            return seasonTicketSearchResultDTO;
        }).toList());
        response.setMetadata(seasonTickets.getMetadata());

        return response;
    }

    public void updateSeasonTicket(Long seasonTicketId, UpdateSeasonTicketRequestDTO body) {

        SeasonTicket seasonTicketToUpdate = seasonTicketRepository.getSeasonTicket(seasonTicketId);

        if (seasonTicketToUpdate == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND,
                    "No season ticket found with id: " + body, null);
        }

        securityManager.checkEntityAccessible(seasonTicketToUpdate.getEntityId());

        validateBody(body, seasonTicketToUpdate);

        SeasonTicket seasonTicketDTO = SeasonTicketConverter.toMsEvent(body);
        seasonTicketDTO.setId(seasonTicketId);

        Operator entityOperator = entitiesRepository.getCachedOperator(seasonTicketToUpdate.getEntityId());

        handleMulticurrency(seasonTicketId, body, entityOperator, seasonTicketToUpdate, seasonTicketDTO);

        if (seasonTicketDTO.getCategory() != null && seasonTicketDTO.getCategory().getId() != null) {
            validationService.checkCategory(seasonTicketDTO.getCategory().getId().intValue());
        }
        if (seasonTicketDTO.getCustomCategory() != null && seasonTicketDTO.getCustomCategory().getId() != null) {
            validationService.checkCustomCategory(seasonTicketToUpdate.getEntityId(),
                    seasonTicketDTO.getCustomCategory().getId());
        }

        handleOperative(body, seasonTicketDTO, seasonTicketToUpdate);

        handleInteractiveVenue(body, seasonTicketToUpdate);

        fillLanguages(body, seasonTicketDTO, seasonTicketToUpdate.getEntityId());

        seasonTicketRepository.updateSeasonTicket(seasonTicketId, seasonTicketDTO);
    }

    private void handleInteractiveVenue(UpdateSeasonTicketRequestDTO body, SeasonTicket seasonTicketToUpdate) {
        if (body.getSettings() != null && body.getSettings().getInteractiveVenue() != null) {

            SettingsInteractiveVenueDTO settingsInteractiveVenueDTO = body.getSettings().getInteractiveVenue();

            if(BooleanUtils.isTrue(settingsInteractiveVenueDTO.getAllowInteractiveVenue()) && settingsInteractiveVenueDTO.getInteractiveVenueType() == null){
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG);
            }

            Entity entity = entitiesRepository.getCachedEntity(seasonTicketToUpdate.getEntityId());

            if (entity.getInteractiveVenue() == null || CommonUtils.isFalse(entity.getInteractiveVenue().getEnabled())) {
                throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_EVENT_INTERACTIVE_VENUE_UPDATE);
            }
            InteractiveVenueType interactiveVenueType = body.getSettings().getInteractiveVenue().getInteractiveVenueType();
            if (interactiveVenueType != null && entity.getInteractiveVenue().getAllowedVenues().stream().noneMatch(vt -> vt.name().equals(interactiveVenueType.name()))) {
                 throw ExceptionBuilder.build(ApiMgmtErrorCode.INTERACTIVE_VENUE_TYPE_NOT_FROM_ENTITY, interactiveVenueType.name());
            }
        }
    }

    private void handleOperative(
            UpdateSeasonTicketRequestDTO body, SeasonTicket seasonTicketDTO, SeasonTicket seasonTicketToUpdate) {
        // Operative
        if (body.getSettings() != null && body.getSettings().getOperative() != null) {
            UpdateSeasonTicketOperativeDTO operative = body.getSettings().getOperative();

            // Max buying limit
            if (operative.getMaxBuyingLimit() != null) {
                MaxBuyingLimit maxBuyingLimit = new MaxBuyingLimit();
                if (CommonUtils.isTrue(operative.getMaxBuyingLimit().getOverride())) {
                    maxBuyingLimit.setValue(operative.getMaxBuyingLimit().getValue());
                } else {
                    maxBuyingLimit.setValue(null);
                }
                seasonTicketDTO.setMaxBuyingLimit(maxBuyingLimit);
            }

            // Season ticket custom data
            seasonTicketDTO.setMemberMandatory(operative.getMemberMandatory());
            boolean hasSales = ticketsRepository.getSessionSalesAmount(seasonTicketToUpdate.getSessionId()) > 0L;
            if (hasSales && body.getSettings().getOperative().getRenewal() != null && body.getSettings().getOperative().getRenewal().getAutomatic() != null){
                throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_AUTO_RENEWAL_NOT_ALLOWED);
            }
            seasonTicketRenewalsService.setRenewalData(seasonTicketDTO, operative);
            seasonTicketChangeSeatsService.setChangeSeatData(seasonTicketDTO, operative);
            initSeasonTicketReleaseSeat(seasonTicketToUpdate, operative.getAllowReleaseSeat());
        }
    }

    private void handleMulticurrency(
            Long seasonTicketId, UpdateSeasonTicketRequestDTO body, Operator entityOperator,
            SeasonTicket seasonTicketToUpdate, SeasonTicket seasonTicketDTO) {
        UpdateSeasonTicketStatus updateStatus;
        if (BooleanUtils.isTrue(entityOperator.getUseMultiCurrency()) && body.getCurrencyCode() != null) {
            if (getEventHasSaleRequests(seasonTicketId)
                    || ordersRepository.eventHasOrders(seasonTicketId)) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_UPDATE_EVENT_CURRENCY_HAS_SALES);
            }

            String seasonTicketStatus = null;

            if (seasonTicketToUpdate.getStatus() != null) {
                seasonTicketStatus = seasonTicketToUpdate.getStatus().name();
            } else if (seasonTicketDTO.getStatus() != null) {
                seasonTicketStatus = seasonTicketDTO.getStatus().name();
            }

            if (seasonTicketStatus != null && (seasonTicketStatus.equals(SeasonTicketStatus.READY.name()) || seasonTicketStatus.equals(SeasonTicketStatus.PENDING_PUBLICATION.name()))) {
                throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_UPDATE_CURRENCY_IS_PUBLISHED);
            }

            if (body.getStatus() != null) {
                updateStatus = new UpdateSeasonTicketStatus();
                updateStatus.setStatus(SeasonTicketConverter.toMsEvent(body.getStatus()));
                seasonTicketRepository.updateSeasonTicketStatus(seasonTicketId, updateStatus);
            }

            seasonTicketDTO.setCurrencyId(entityOperator.getCurrencies().getSelected().stream()
                    .filter(currency -> currency.getCode().equals(body.getCurrencyCode()))
                    .map(Currency::getId)
                    .findAny()
                    .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CURRENCY_NOT_MATCH_OPERATOR)));

            resetCurrencyValues(seasonTicketId, seasonTicketToUpdate, seasonTicketDTO);
        }
    }

    private void resetCurrencyValues(Long seasonTicketId, SeasonTicket seasonTicketToUpdate, SeasonTicket seasonTicketDTO) {
        if (seasonTicketDTO.getCurrencyId() != null) {
            if (seasonTicketToUpdate.getSalesGoalRevenue() != null) {
                seasonTicketDTO.setSalesGoalRevenue(BigDecimal.valueOf(0));
            }
            eventsService.resetEventCurrencyValues(seasonTicketId, seasonTicketDTO.getCurrencyId());
        }
    }

    private void validateBody(UpdateSeasonTicketRequestDTO body, SeasonTicket seasonTicketToUpdate) {
        if (body != null && body.getSettings() != null && body.getSettings().getOperative() != null
            && body.getSettings().getOperative().getSecondaryMarket() != null
                && CommonUtils.isTrue(body.getSettings().getOperative().getSecondaryMarket().getEnable())) {

            if (body.getSettings().getOperative().getSecondaryMarket().getStartDate() == null
               || body.getSettings().getOperative().getSecondaryMarket().getEndDate() == null) {
                    throw new OneboxRestException(ApiMgmtErrorCode.DATES_ARE_MANDATORY);
            } else if (!body.getSettings().getOperative().getSecondaryMarket().getStartDate()
                        .isBefore(body.getSettings().getOperative().getSecondaryMarket().getEndDate())) {
                throw new OneboxRestException(ApiMgmtErrorCode.START_GREATER_END_DATE);
            }

            Entity entity = entitiesRepository.getCachedEntity(seasonTicketToUpdate.getEntityId());
            if (CommonUtils.isFalse(entity.getUseSecondaryMarket())) {
                throw new OneboxRestException(ApiMgmtErrorCode.SECONDARY_MARKET_NOT_ALLOWED_BY_ENTITY);
            }
        }
    }

    private void fillLanguages(UpdateSeasonTicketRequestDTO seasonTicketRequestDTO, SeasonTicket seasonTicket, Long seasonTicketEntityId) {
        Entity entity = entitiesRepository.getCachedEntity(seasonTicketEntityId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        if (seasonTicketRequestDTO.getSettings() != null && seasonTicketRequestDTO.getSettings().getLanguages() != null) {
            LanguagesDTO seasonTicketLanguages = seasonTicketRequestDTO.getSettings().getLanguages();
            if (!CommonUtils.isEmpty(seasonTicketLanguages.getSelected())) {
                seasonTicket.setLanguages(new ArrayList<>());
                for (String langCode : seasonTicketLanguages.getSelected()) {
                    boolean isDefault = langCode.equals(seasonTicketLanguages.getDefaultLanguage());
                    String locale = ConverterUtils.checkLanguage(langCode, languages);
                    Long languageId = languages.get(locale);
                    if (entity.getSelectedLanguages().stream().noneMatch(l -> l.getId().equals(languageId))) {
                        throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
                    }
                    seasonTicket.getLanguages().add(new EventLanguage(languageId, isDefault));
                }
            }
        }
    }

    public Long createSeasonTicket(CreateSeasonTicketRequestDTO seasonTicketData) {

        checkCreationData(seasonTicketData);
        boolean hasExternalAccessControl = validateCreationExternalSeasonTicket(seasonTicketData);

        Entity entity = entitiesRepository.getCachedEntity(seasonTicketData.getEntityId());
        String authUsername = SecurityUtils.getUsername();
        User authUser = usersRepository.getUser(authUsername, entity.getOperator().getId(),
                SecurityUtils.getApiKey());

        Operator entityOperator = entitiesRepository.getCachedOperator(seasonTicketData.getEntityId());

        Long currencyId = entityOperator.getCurrency().getId().longValue();

        if (BooleanUtils.isTrue(entityOperator.getUseMultiCurrency())) {
            if (seasonTicketData.getCurrencyCode() != null) {
                currencyId = entityOperator.getCurrencies().getSelected().stream()
                        .filter(currency -> currency.getCode().equals(seasonTicketData.getCurrencyCode()))
                        .map(Currency::getId)
                        .findAny()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CURRENCY_NOT_MATCH_OPERATOR));
            } else {
                currencyId = entityOperator.getCurrencies().getSelected().stream()
                        .filter(currency -> currency.getCode().equals(entityOperator.getCurrencies().getDefaultCurrency()))
                        .map(Currency::getId)
                        .findAny()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.ERROR_OPERATOR_WITHOUT_MULTICURRENCY_DEFAULT));
            }
        }

        Long defaultInvoicePrefixId = eventsService.getDefaultInvoicePrefixId(seasonTicketData.getProducerId());

        Provider provider = null;
        if (seasonTicketData.getAdditionalConfig() != null && seasonTicketData.getAdditionalConfig().getInventoryProvider() != null) {
            provider = Provider.valueOf(seasonTicketData.getAdditionalConfig().getInventoryProvider().name());
        }
        InventoryProviderService inventoryProviderService = sessionsService.getInventoryProvider(seasonTicketData.getEntityId(), provider);

        CreateSeasonTicketData createSeasonTicketData = new CreateSeasonTicketData(seasonTicketData.getName(), seasonTicketData.getEntityId(), seasonTicketData.getProducerId(), seasonTicketData.getCategoryId(), seasonTicketData.getCustomCategoryId(), entity.getLanguage().getId().intValue(), currencyId,
                defaultInvoicePrefixId, SeasonTicketConverter.convertToDatasource(seasonTicketData.getAdditionalConfig()));
        EventConverter.addAuthContact(authUser, createSeasonTicketData);

        Long seasonTicketId = inventoryProviderService.createSeasonTicket(createSeasonTicketData);

        CreateTemplateRequestDTO request = buildCreateTemplateRequest(seasonTicketData, seasonTicketId);
        CreateVenueTemplateRequest venueTemplateRequest = VenueTemplateConverter.convertToDatasource(request);
        Long venueTemplateId = venuesRepository.createVenueTemplate(venueTemplateRequest);

        // Resolve taxes if automatic_taxes is enabled
        Long resolvedTaxId = seasonTicketData.getTaxId();
        Long resolvedChargesTaxId = seasonTicketData.getChargesTaxId();
        List<Long> resolvedTicketTaxIds = new ArrayList<>();
        List<Long> resolvedChargeTaxIds = new ArrayList<>();
        if (BooleanUtils.isTrue(seasonTicketData.getAutomaticTaxes())) {
            VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(seasonTicketData.getVenueConfigId());
            if (venueTemplate == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
            }
            List<Tax> ticketTaxes = entitiesRepository.getEntityTaxes(seasonTicketData.getEntityId(), seasonTicketId, venueTemplate.getVenue().getId(), TaxType.TICKET);
            List<Tax> chargesTaxes = entitiesRepository.getEntityTaxes(seasonTicketData.getEntityId(), seasonTicketId, venueTemplate.getVenue().getId(), TaxType.CHARGES);
            if (ticketTaxes.isEmpty() || chargesTaxes.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_TAXES_NOT_FOUND);
            }
            resolvedTaxId = ticketTaxes.get(0).getId();
            resolvedChargesTaxId = chargesTaxes.get(0).getId();
            resolvedTicketTaxIds = ticketTaxes.stream().map(Tax::getId).toList();
            resolvedChargeTaxIds = chargesTaxes.stream().map(Tax::getId).toList();
        }

        if (provider == null && BooleanUtils.isFalse(hasExternalAccessControl)) {
            enqueueCreateSeasonTicketSession(seasonTicketData, seasonTicketId, venueTemplateId, resolvedTaxId, resolvedChargesTaxId, resolvedTicketTaxIds, resolvedChargeTaxIds);
		} else {
			CreateSessionData creationData = createSessionData(seasonTicketData, venueTemplateId, seasonTicketId,
					resolvedTaxId, resolvedChargesTaxId, resolvedTicketTaxIds, resolvedChargeTaxIds);
			inventoryProviderService.createSession(seasonTicketId, creationData);
		}

        checkAndProcessExternalSeasonTicket(seasonTicketData, seasonTicketId);

        return seasonTicketId;

    }


	private CreateSessionData createSessionData(CreateSeasonTicketRequestDTO seasonTicketData, Long venueTemplateId,
			Long seasonTicketId,
			Long taxId, Long chargesTaxId,
			List<Long> ticketTaxIds, List<Long> chargeTaxIds) {

        CreateSessionData creationData = new CreateSessionData();
        creationData.setEntityId(seasonTicketData.getEntityId());
        creationData.setName(seasonTicketData.getName());
        creationData.setTaxId(taxId);
        creationData.setChargeTaxId(chargesTaxId);
        creationData.setTicketTaxIds(ticketTaxIds);
        creationData.setChargeTaxIds(chargeTaxIds);
        creationData.setVenueConfigId(venueTemplateId);
        ZonedDateTime now = ZonedDateTime.now();
        if (seasonTicketData.getStartDate() != null) {
            creationData.setSessionStartDate(seasonTicketData.getStartDate());
        } else {
            creationData.setSessionStartDate(now);
        }

        if (seasonTicketData.getEndDate() != null) {
            creationData.setSessionEndDate(seasonTicketData.getEndDate());
        }
        creationData.setSalesEndDate(now.plusYears(1));
        creationData.setSalesStartDate(now);
        creationData.setPublishDate(now);
        if (seasonTicketData.getAdditionalConfig() != null) {
            creationData.setExternalSessionId(seasonTicketData.getAdditionalConfig().getExternalEventId());
        }

        EventRates eventRates = eventsRepository.getEventRates(seasonTicketId);
        if (eventRates != null && CollectionUtils.isNotEmpty(eventRates.getData())) {
            List<Rate> rates = eventRates.getData().stream().map(eventRate -> {
                Rate rate = new Rate();
                rate.setId(eventRate.getId());
                rate.setDefaultRate(eventRate.isDefaultRate());
                return rate;
            }).toList();
            creationData.setRates(rates);
        }
        creationData.setSeasonPass(Boolean.TRUE);
        creationData.setSeasonTicket(Boolean.TRUE);
        creationData.setSaleType(SessionSalesType.INDIVIDUAL.getType());

        return creationData;
    }

    private static CreateTemplateRequestDTO buildCreateTemplateRequest(CreateSeasonTicketRequestDTO seasonTicketData,Long seasonTicketId) {

        CreateTemplateRequestDTO request = new CreateTemplateRequestDTO();
        request.setName(seasonTicketData.getName());
        request.setEventId(seasonTicketId);
        request.setEntityId(seasonTicketData.getEntityId());
        request.setType(VenueTemplateTypeDTO.NORMAL);
        request.setFromTemplateId(seasonTicketData.getVenueConfigId());
        if (seasonTicketData.getAdditionalConfig() != null) {
            es.onebox.mgmt.venues.dto.AdditionalConfigDTO inventory = new es.onebox.mgmt.venues.dto.AdditionalConfigDTO();
            inventory.setInventoryProvider(seasonTicketData.getAdditionalConfig().getInventoryProvider());
            request.setAdditionalConfig(inventory);
        }
        return request;
    }

    private void checkCreationData(CreateSeasonTicketRequestDTO seasonTicketData) {

        if (seasonTicketData.getVenueConfigId() == null) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "venueConfigId is mandatory", null);
        }

        securityManager.checkEntityAccessible(seasonTicketData.getEntityId());
        validationService.checkCategory(seasonTicketData.getCategoryId());

        // Producer
        Producer producer = entitiesRepository.getProducer(seasonTicketData.getProducerId());
        if (producer == null) {
            throw new OneboxRestException(NOT_FOUND, "Producer not found", null);
        }
        if (producer.getEntity() == null || producer.getEntity().getId() == null) {
            throw new OneboxRestException(GENERIC_ERROR, "Producer is not attached to any entity", null);
        }
        if (!SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR) && !producer.getEntity().getId().equals(seasonTicketData.getEntityId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCER_NOT_FROM_ENTITY);
        }

        // Tax ids - skip validation when automatic_taxes is true
        if (BooleanUtils.isNotTrue(seasonTicketData.getAutomaticTaxes())) {
            List<EntityTax> taxes = entitiesRepository.getTaxes(seasonTicketData.getEntityId());
            if (taxes == null || taxes.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_TAXES_NOT_FOUND);
            }
            Set<Long> entityTaxIds = taxes.stream()
                    .map(EntityTax::getIdImpuesto)
                    .map(Integer::longValue)
                    .collect(Collectors.toSet());
            if (!entityTaxIds.contains(seasonTicketData.getTaxId())) {
                throw new OneboxRestException(ApiMgmtErrorCode.TICKET_TAX_ID_NOT_FOUND);
            }
            if (!entityTaxIds.contains(seasonTicketData.getChargesTaxId())) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHARGES_TAX_ID_NOT_FOUND);
            }
        }

        // VenueConfigId
        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(seasonTicketData.getVenueConfigId());
        if (!validationService.validateSeasonTicketVenueTemplate(venueTemplate, seasonTicketData.getVenueConfigId())) {
            throw OneboxRestException
                    .builder(NOT_FOUND)
                    .setMessage("There's no template with the given id")
                    .build();
        }
    }


    private void enqueueCreateSeasonTicketSession(CreateSeasonTicketRequestDTO seasonTicketData, Long seasonTicketId, Long venueTemplateId,
                                                  Long taxId, Long chargesTaxId, List<Long> ticketTaxIds, List<Long> chargeTaxIds) {

        CreateSeasonTicketSessionMessage sessionData = new CreateSeasonTicketSessionMessage();
        sessionData.setSeasonTicketId(seasonTicketId);
        sessionData.setName(seasonTicketData.getName());
        sessionData.setTaxId(taxId);
        sessionData.setChargeTaxId(chargesTaxId);
        sessionData.setTicketTaxIds(ticketTaxIds);
        sessionData.setChargeTaxIds(chargeTaxIds);
        sessionData.setVenueConfigId(venueTemplateId);

        try {
            createSeasonTicketSessionProducer.sendMessage(sessionData);
        } catch (Exception e) {
            throw new OneboxRestException(GENERIC_ERROR);
        }
    }

    public SeasonTicketStatusResponseDTO getSeasonTicketStatus(Long seasonTicketId) {
        getAndCheckSeasonTicket(seasonTicketId);

        SeasonTicketDatasourceStatus seasonTicketInternalStatus = seasonTicketRepository.getSeasonTicketStatus(seasonTicketId);

        SeasonTicketStatusResponseDTO response = new SeasonTicketStatusResponseDTO();
        response.setSeasonTicketId(seasonTicketId.intValue());
        response.setGenerationStatus(SeasonTicketStatusConverter.convertInternalGenerationStatus(seasonTicketInternalStatus.getGenerationStatus()));
        response.setStatus(SeasonTicketStatusConverter.convertStatus(seasonTicketInternalStatus.getStatus()));

        return response;
    }

    public SeasonTicket getAndCheckSeasonTicket(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "seasonTicketId must be a positive integer", null);
        }

        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null || SeasonTicketStatus.DELETED.equals(seasonTicket.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND, "No season ticket found with id: " + seasonTicketId, null);
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
        return seasonTicket;
    }

    public void updateSeasonTicketStatus(Long seasonTicketId, UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO) {
        getAndCheckSeasonTicket(seasonTicketId);

        UpdateSeasonTicketStatus updateStatus = new UpdateSeasonTicketStatus();
        updateStatus.setStatus(SeasonTicketConverter.toMsEvent(updateSeasonTicketStatusRequestDTO.getStatus()));

        seasonTicketRepository.updateSeasonTicketStatus(seasonTicketId, updateStatus);
    }

    public SeasonTicketValidationsResponseDTO getValidations(Long seasonTicketId, SeasonTicketValidationsRequestDTO requestDTO) {
        SeasonTicket seasonTicket = getAndCheckSeasonTicket(seasonTicketId);

        SeasonTicketValidationsResponseDTO responseDTO = new SeasonTicketValidationsResponseDTO();

        boolean getAllFlags = requestDTO.getHasLinkableSeats() == null && requestDTO.getHasAssignedSessions() == null &&
                requestDTO.getHasPendingRenewals() == null;

        if (getAllFlags || Boolean.TRUE.equals(requestDTO.getHasLinkableSeats())) {
            // search linkable Seats
            SeasonTicketSeatsSummary summaryResponse = ticketsRepository.getSeasonTicketSeatsSummary(seasonTicket.getSessionId());
            if (summaryResponse != null) {
                boolean response = Boolean.TRUE.equals(summaryResponse.getHasLinkableSeats());
                responseDTO.setHasLinkableSeats(response);
            } else {
                responseDTO.setHasLinkableSeats(false);
            }
        }

        if (getAllFlags || Boolean.TRUE.equals(requestDTO.getHasAssignedSessions())) {
            // search assigned sessions
            SeasonTicketSessionsSearchFilter sessionsSearchFilter = new SeasonTicketSessionsSearchFilter();
            sessionsSearchFilter.setAssignationStatus(SeasonTicketAssignationStatus.ASSIGNED);
            SeasonTicketSessionsResponse sessionsResponse = seasonTicketSessionsService.getSessions(sessionsSearchFilter, seasonTicketId);
            responseDTO.setHasAssignedSessions(CollectionUtils.isNotEmpty(sessionsResponse.getData()));
        }

        if (getAllFlags || Boolean.TRUE.equals(requestDTO.getHasPendingRenewals())) {
            SeasonTicketRenewalFilter filter = new SeasonTicketRenewalFilter();
            filter.setMappingStatus(SeatMappingStatus.NOT_MAPPED);
            SeasonTicketRenewalsResponse renewalsResponse = seasonTicketRenewalsService.getRenewalsSeasonTicket(seasonTicketId, filter);
            if (renewalsResponse != null && renewalsResponse.getSummary() != null &&
                    renewalsResponse.getSummary().getNotMappedImports() != null) {
                boolean hasPendingRenewals = renewalsResponse.getSummary().getNotMappedImports() > 0;
                responseDTO.setHasPendingRenewals(hasPendingRenewals);
            } else {
                responseDTO.setHasPendingRenewals(false);
            }
        }

        return responseDTO;
    }

    private boolean getEventHasSaleRequests(Long eventId) {
        EventChannels eventChannels = eventChannelsRepository.getEventChannels(eventId, null);
        return (eventChannels != null && eventChannels.getData() != null && !eventChannels.getData().isEmpty());
    }

    private void initSeasonTicketReleaseSeat(SeasonTicket seasonTicketToUpdate, Boolean allowReleaseSeat) {
        if (!(BooleanUtils.isFalse(seasonTicketToUpdate.getAllowReleaseSeat()) && BooleanUtils.isTrue(allowReleaseSeat))) {
            seasonTicketToUpdate.setAllowReleaseSeat(allowReleaseSeat);
            return;
        }
        initDefaultConfiguration(seasonTicketToUpdate.getId());
        releasedSeatsQuotaHelper.initReleasedSeatsQuota(seasonTicketToUpdate.getId());
    }

    private void initDefaultConfiguration(Long seasonTicketId) {
        SeasonTicketReleaseSeat seasonTicketReleaseSeat = seasonTicketRepository.getSeasonTicketReleaseSeat(seasonTicketId);
        if (seasonTicketReleaseSeat == null) {
            seasonTicketReleaseSeat = new SeasonTicketReleaseSeat();
            seasonTicketReleaseSeat.setCustomerPercentage(DEFAULT_CUSTOMER_PERCENTAGE);
            seasonTicketReleaseSeat.setMaxReleases(DEFAULT_MAX_RELEASES);
            seasonTicketReleaseSeat.setReleaseMaxDelayTime(DEFAULT_RELEASE_MAX_DELAY_TIME);
            seasonTicketReleaseSeat.setRecoverMaxDelayTime(DEFAULT_RECOVER_MAX_DELAY_TIME);
            seasonTicketRepository.updateSeasonTicketReleaseSeat(seasonTicketId, seasonTicketReleaseSeat);
        }
    }

    public void updateSeasonTicketExternalInventory(Long seasonTicketId) {

        SeasonTicket seasonTicketToUpdate = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicketToUpdate == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND,
                    "No season ticket found with id: " + seasonTicketId, null);
        }

        securityManager.checkEntityAccessible(seasonTicketToUpdate.getEntityId());

        if (SeasonTicketStatus.CANCELLED.equals(seasonTicketToUpdate.getStatus())
                || SeasonTicketStatus.FINISHED.equals(seasonTicketToUpdate.getStatus())
                || SeasonTicketStatus.DELETED.equals(seasonTicketToUpdate.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_INVALID_STATUS);
        }

        //ADD OTHER PROVIDERS HERE WHEN CONNECTOR LOGIC IS IMPLEMENTED
        if( Provider.SGA.equals(seasonTicketToUpdate.getInventoryProvider()) ){
            dispatcherRepository.updateSeasonTicketInventory(
                    seasonTicketToUpdate.getEntityId(),
                    seasonTicketToUpdate.getId()
            );
        }else {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_INVALID_INVENTORY_PROVIDER);
        }
    }

    private void checkAndProcessExternalSeasonTicket(CreateSeasonTicketRequestDTO seasonTicketData, Long seasonTicketId) {
        if (seasonTicketData.getVenueConfigId() == null) {
            return;
        }
        VenueTemplate venueTemplate = venuesRepository.getCachedVenueTemplate(seasonTicketData.getVenueConfigId());
        if (venueTemplate == null || venueTemplate.getVenue() == null || venueTemplate.getVenue().getId() == null) {
            return;
        }
        Long venueId = venueTemplate.getVenue().getId();

        List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);
        if (CollectionUtils.isEmpty(venueAccessControlSystems)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(venueAccessControlSystems)) {
            venueAccessControlSystems.stream().distinct().forEach(accessControlSystem -> {
                ExternalAccessControlHandler externalAccessControlHandler;
                externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

                if (externalAccessControlHandler == null) {
                    return;
                }

                externalAccessControlHandler.createSeasonTicket(seasonTicketData.getEntityId(), seasonTicketId);
            });
        }
    }

    private boolean validateCreationExternalSeasonTicket(CreateSeasonTicketRequestDTO seasonTicketData) {

        if (seasonTicketData.getVenueConfigId() == null) {
            return false;
        }
        VenueTemplate venueTemplate = venuesRepository.getCachedVenueTemplate(seasonTicketData.getVenueConfigId());
        if (venueTemplate == null || venueTemplate.getVenue() == null || venueTemplate.getVenue().getId() == null) {
            return false;
        }
        Long venueId = venueTemplate.getVenue().getId();

        List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);
        if (CollectionUtils.isEmpty(venueAccessControlSystems)) {
            return false;
        }

        venueAccessControlSystems.stream().distinct().forEach(accessControlSystem -> {
            ExternalAccessControlHandler externalAccessControlHandler;
            externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

            if (externalAccessControlHandler == null) {
                return;
            }

            externalAccessControlHandler.validateCreateSeasonTicket(seasonTicketData.getCustomCategoryId(), seasonTicketData.getStartDate(), seasonTicketData.getEndDate());
        });
        return true;
    }

}
package es.onebox.mgmt.seasontickets;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInteractiveVenue;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.Tax;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.enums.TaxType;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventVenueViewConfig;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSalesType;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.events.EventsService;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.AdditionalConfigDTO;
import es.onebox.mgmt.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketsSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketOperativeDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketRenewalDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketsSettingsDTO;
import es.onebox.mgmt.seasontickets.service.ReleasedSeatsQuotaHelper;
import es.onebox.mgmt.seasontickets.service.SeasonTicketChangeSeatsService;
import es.onebox.mgmt.seasontickets.service.SeasonTicketRenewalsService;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import es.onebox.mgmt.seasontickets.service.SeasonTicketValidationService;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.sessions.SessionsService;
import es.onebox.oauth2.resource.context.ChannelAuthenticationData;
import es.onebox.oauth2.resource.utils.TokenParam;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeasonTicketServiceTest {

    private static final String API_KEY = "xxx555xxx";

    @Mock
    private SecurityManager securityManager;

    @Mock
    private SeasonTicketRepository seasonTicketRepository;

    @Mock
    private TicketsRepository ticketsRepository;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private SeasonTicketRenewalsService seasonTicketRenewalsService;

    @Mock
    private SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;

    @Mock
    private ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper;

    @Mock
    private MasterdataService masterdataService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private VenuesRepository venuesRepository;

    @Mock
    private SessionsService sessionsService;

    @Mock
    private InventoryProviderService inventoryProviderService;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private SeasonTicketValidationService seasonTicketValidationService;

    @Mock
    private EventsService eventsService;

    @Mock
    private DefaultProducer createSeasonTicketSessionProducer;

    @Mock
    private AccessControlSystemsRepository accessControlSystemsRepository;

    @InjectMocks
    private SeasonTicketService seasonTicketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateSeasonTicket_WhenHasSalesAndAutomaticRenewal_ShouldThrowException() {
        // Given
        Long seasonTicketId = 1L;
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setId(seasonTicketId);
        seasonTicket.setSessionId(1L);
        seasonTicket.setEntityId(1L);

        UpdateSeasonTicketRequestDTO requestDTO = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketsSettingsDTO settings = new UpdateSeasonTicketsSettingsDTO();
        UpdateSeasonTicketOperativeDTO operative = new UpdateSeasonTicketOperativeDTO();
        UpdateSeasonTicketRenewalDTO renewal = new UpdateSeasonTicketRenewalDTO();
        renewal.setAutomatic(true);
        operative.setRenewal(renewal);
        settings.setOperative(operative);
        requestDTO.setSettings(settings);

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        Map<String, Long> languages = new HashMap<>();
        languages.put("es_ES", 1L);

        // When
        when(seasonTicketRepository.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicket);
        when(ticketsRepository.getSessionSalesAmount(anyLong())).thenReturn(1L);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(masterdataService.getLanguagesByIdAndCode()).thenReturn(languages);
        doNothing().when(seasonTicketRenewalsService).setRenewalData(any(), any());
        doNothing().when(seasonTicketChangeSeatsService).setChangeSeatData(any(), any());

        // Then
        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> seasonTicketService.updateSeasonTicket(seasonTicketId, requestDTO));

        assertEquals(ApiMgmtErrorCode.SEASON_TICKET_AUTO_RENEWAL_NOT_ALLOWED.toString(), exception.getErrorCode());
        verify(seasonTicketRepository).getSeasonTicket(seasonTicketId);
        verify(ticketsRepository).getSessionSalesAmount(seasonTicket.getSessionId());
    }

    @Test
    void updateSeasonTicket_WhenNoSalesAndAutomaticRenewal_ShouldUpdateSuccessfully() {
        // Given
        Long seasonTicketId = 1L;
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setId(seasonTicketId);
        seasonTicket.setSessionId(1L);
        seasonTicket.setEntityId(1L);

        UpdateSeasonTicketRequestDTO requestDTO = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketsSettingsDTO settings = new UpdateSeasonTicketsSettingsDTO();
        UpdateSeasonTicketOperativeDTO operative = new UpdateSeasonTicketOperativeDTO();
        UpdateSeasonTicketRenewalDTO renewal = new UpdateSeasonTicketRenewalDTO();
        renewal.setAutomatic(true);
        operative.setRenewal(renewal);
        settings.setOperative(operative);
        requestDTO.setSettings(settings);

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        Map<String, Long> languages = new HashMap<>();
        languages.put("es_ES", 1L);

        // When
        when(seasonTicketRepository.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicket);
        when(ticketsRepository.getSessionSalesAmount(anyLong())).thenReturn(0L);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(masterdataService.getLanguagesByIdAndCode()).thenReturn(languages);
        doNothing().when(seasonTicketRenewalsService).setRenewalData(any(), any());
        doNothing().when(seasonTicketChangeSeatsService).setChangeSeatData(any(), any());

        // Then
        seasonTicketService.updateSeasonTicket(seasonTicketId, requestDTO);

        verify(seasonTicketRepository).getSeasonTicket(seasonTicketId);
        verify(ticketsRepository).getSessionSalesAmount(seasonTicket.getSessionId());
        verify(seasonTicketRepository).updateSeasonTicket(anyLong(), any());
    }

    @Test
    public void shouldThrowExceptionWhenVenueFromSeasonTicketsSettingsIsEmpty(){

        Long seasonTicketId = 1L;
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setId(seasonTicketId);
        seasonTicket.setSessionId(1L);
        seasonTicket.setEntityId(1L);

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        UpdateSeasonTicketRequestDTO requestDTO = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketsSettingsDTO settings = new UpdateSeasonTicketsSettingsDTO();
        SettingsInteractiveVenueDTO interactiveVenueSettings = new SettingsInteractiveVenueDTO();
        interactiveVenueSettings.setAllowInteractiveVenue(true);

        requestDTO.setSettings(settings);
        settings.setInteractiveVenue(interactiveVenueSettings);

        when(seasonTicketRepository.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicket);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);

        OneboxRestException exception = Assert.assertThrows(OneboxRestException.class, () -> {
            seasonTicketService.updateSeasonTicket(seasonTicketId, requestDTO);
        });

        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.getMessage(), exception.getMessage());
        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.getErrorCode(), exception.getErrorCode());

    }

    @Test
    public void shouldThrowExceptionWhenVenueFromEntityIsEmpty(){

        Long seasonTicketId = 1L;
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setId(seasonTicketId);
        seasonTicket.setSessionId(1L);
        seasonTicket.setEntityId(1L);

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        UpdateSeasonTicketRequestDTO requestDTO = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketsSettingsDTO settings = new UpdateSeasonTicketsSettingsDTO();
        SettingsInteractiveVenueDTO interactiveVenueSettings = new SettingsInteractiveVenueDTO();
        interactiveVenueSettings.setAllowInteractiveVenue(true);
        interactiveVenueSettings.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);

        Entity entity = new Entity();

        requestDTO.setSettings(settings);
        settings.setInteractiveVenue(interactiveVenueSettings);

        when(seasonTicketRepository.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicket);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(entitiesRepository.getCachedEntity(anyLong())).thenReturn(entity);

        OneboxRestException exception = Assert.assertThrows(OneboxRestException.class, () -> {
            seasonTicketService.updateSeasonTicket(seasonTicketId, requestDTO);
        });

        assertEquals(ApiMgmtErrorCode.FORBIDDEN_EVENT_INTERACTIVE_VENUE_UPDATE.getMessage(), exception.getMessage());
        assertEquals(ApiMgmtErrorCode.FORBIDDEN_EVENT_INTERACTIVE_VENUE_UPDATE.getErrorCode(), exception.getErrorCode());

    }

    @Test
    public void shouldThrowExceptionWhenNotSingleVenueFromSeasonTicketsSettingsMatchesWithVenuesFromEntity(){

        Long seasonTicketId = 1L;
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setId(seasonTicketId);
        seasonTicket.setSessionId(1L);
        seasonTicket.setEntityId(1L);

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        UpdateSeasonTicketRequestDTO requestDTO = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketsSettingsDTO settings = new UpdateSeasonTicketsSettingsDTO();
        SettingsInteractiveVenueDTO interactiveVenueSettings = new SettingsInteractiveVenueDTO();
        interactiveVenueSettings.setAllowInteractiveVenue(true);
        interactiveVenueSettings.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);

        Entity entity = new Entity();
        EntityInteractiveVenue entityInteractiveVenue = new EntityInteractiveVenue();
        entityInteractiveVenue.setEnabled(true);
        entityInteractiveVenue.setAllowedVenues(List.of(
                es.onebox.mgmt.datasources.common.enums.InteractiveVenueType.VENUE_3D_MMC_V1));
        entity.setInteractiveVenue(entityInteractiveVenue);

        requestDTO.setSettings(settings);
        settings.setInteractiveVenue(interactiveVenueSettings);

        when(seasonTicketRepository.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicket);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(entitiesRepository.getCachedEntity(anyLong())).thenReturn(entity);

        OneboxRestException exception = Assert.assertThrows(OneboxRestException.class, () -> {
            seasonTicketService.updateSeasonTicket(seasonTicketId, requestDTO);
        });

        assertEquals(ApiMgmtErrorCode.INTERACTIVE_VENUE_TYPE_NOT_FROM_ENTITY.getErrorCode(), exception.getErrorCode());

    }

    @Test
    public void shouldPassWhenVenuesFromEntityAndSeasonTicketsSettingsAreEnableAndMatch(){

        Long seasonTicketId = 1L;
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setId(seasonTicketId);
        seasonTicket.setSessionId(1L);
        seasonTicket.setEntityId(1L);

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        UpdateSeasonTicketRequestDTO requestDTO = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketsSettingsDTO settings = new UpdateSeasonTicketsSettingsDTO();
        SettingsInteractiveVenueDTO interactiveVenueSettings = new SettingsInteractiveVenueDTO();
        interactiveVenueSettings.setAllowInteractiveVenue(true);
        interactiveVenueSettings.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);

        Entity entity = new Entity();
        EntityInteractiveVenue entityInteractiveVenue = new EntityInteractiveVenue();
        entityInteractiveVenue.setEnabled(true);
        entityInteractiveVenue.setAllowedVenues(List.of(
                es.onebox.mgmt.datasources.common.enums.InteractiveVenueType.VENUE_3D_PACIFA));
        entity.setInteractiveVenue(entityInteractiveVenue);

        requestDTO.setSettings(settings);
        settings.setInteractiveVenue(interactiveVenueSettings);

        when(seasonTicketRepository.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicket);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(entitiesRepository.getCachedEntity(anyLong())).thenReturn(entity);

        assertDoesNotThrow(() -> seasonTicketService.updateSeasonTicket(seasonTicketId, requestDTO));

    }


    @Test
    public void shouldMapCorrectlyWhenExpectedAttributesAreProvided(){
        SeasonTicket oldConfigSeasonTicket = new SeasonTicket();
        EventVenueViewConfig eventVenueViewConfig = new EventVenueViewConfig();
        // no InteractiveVenueType interactiveVenueType;
        eventVenueViewConfig.setUse3dVenueModuleV2(true);
        eventVenueViewConfig.setUseSector3dView(true);
        eventVenueViewConfig.setUseSeat3dView(true);
        oldConfigSeasonTicket.setEventVenueViewConfig(eventVenueViewConfig);

        SeasonTicketDTO expectedSeasonTicketDTO = new SeasonTicketDTO();
        SeasonTicketsSettingsDTO expectedSettings = new SeasonTicketsSettingsDTO();
        SettingsInteractiveVenueDTO expectedInteractiveVenue = new SettingsInteractiveVenueDTO();
        expectedInteractiveVenue.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V2);
        expectedInteractiveVenue.setAllowSector3dView(true);
        expectedInteractiveVenue.setAllowSeat3dView(true);
        expectedSettings.setInteractiveVenue(expectedInteractiveVenue);
        expectedSeasonTicketDTO.setSettings(expectedSettings);

        when(seasonTicketRepository.getSeasonTicket(1L)).thenReturn(oldConfigSeasonTicket);

        SeasonTicketDTO seasonTicketActual = seasonTicketService.getSeasonTicket(1L);

        InteractiveVenueType interactiveVenueTypeActual = seasonTicketActual.getSettings().getInteractiveVenue().getInteractiveVenueType();
        InteractiveVenueType interactiveVenueExpected = expectedSeasonTicketDTO.getSettings().getInteractiveVenue().getInteractiveVenueType();

        boolean useSeat3DViewActual = seasonTicketActual.getSettings().getInteractiveVenue().getAllowSeat3dView();
        boolean useSeat3DViewExpected = expectedSeasonTicketDTO.getSettings().getInteractiveVenue().getAllowSeat3dView();

        boolean useSector3DViewActual = seasonTicketActual.getSettings().getInteractiveVenue().getAllowSector3dView();
        boolean useSectorDViewExpected = expectedSeasonTicketDTO.getSettings().getInteractiveVenue().getAllowSector3dView();

        Assertions.assertEquals(interactiveVenueTypeActual, interactiveVenueExpected);
        Assertions.assertEquals(useSeat3DViewActual, useSeat3DViewExpected);
        Assertions.assertEquals(useSector3DViewActual, useSectorDViewExpected);

    }

    @Test
    void createSeasonTicket_provider_SGA() {
        // Given
        Long entityId = 1L, producerId = 2L, venueConfigId = 3L, ticketTaxId = 4L, chargesTaxId = 5L, seasonTicketId = 10L, venueTemplateId = 100L;
        Integer categoryId = 6;
        String eventName = "Evento Test";
        String currencyCode = "EUR";

        AdditionalConfigDTO additionalConfigDTO = new AdditionalConfigDTO();
        additionalConfigDTO.setInventoryProvider(InventoryProviderEnum.SGA);

        Producer producer = new Producer();
        producer.setEntity(new Entity());
        producer.getEntity().setId(entityId);

        List<EntityTax> taxes = new ArrayList<>();

        EntityTax tax = new EntityTax();
        tax.setIdImpuesto(chargesTaxId.intValue());
        taxes.add(tax);

        tax = new EntityTax();
        tax.setIdImpuesto(ticketTaxId.intValue());
        taxes.add(tax);

        VenueTemplate venueTemplate = new VenueTemplate();
        venueTemplate.setId(venueConfigId);

        Entity entity = new Entity();
        entity.setId(entityId);
        entity.setOperator(new Entity());
        entity.getOperator().setId(entityId);
        entity.setLanguage(new IdValueCodeDTO(1l, "ES", "ES"));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);
        operator.setCurrency(new IdValueDTO(1, "EUR"));

        User user = mock(User.class);

        CreateSeasonTicketRequestDTO seasonTicketData = getCreateStRequest(eventName, entityId, producerId, categoryId,
                venueConfigId, ticketTaxId, chargesTaxId, currencyCode, additionalConfigDTO);

        prepareAuthData();

        when(entitiesRepository.getProducer(producerId)).thenReturn(producer);
        when(entitiesRepository.getTaxes(entityId)).thenReturn(taxes);
        when(venuesRepository.getVenueTemplate(venueConfigId)).thenReturn(venueTemplate);
        when(seasonTicketValidationService.validateSeasonTicketVenueTemplate(venueTemplate, venueConfigId)).thenReturn(true);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);
        when(usersRepository.getUser(anyString(), anyLong(), anyString())).thenReturn(user);
        when(sessionsService.getInventoryProvider(eq(entityId), eq(es.onebox.mgmt.datasources.ms.event.dto.event.Provider.SGA)))
                .thenReturn(inventoryProviderService);
        when(inventoryProviderService.createSeasonTicket(any())).thenReturn(seasonTicketId);
        when(venuesRepository.createVenueTemplate(any())).thenReturn(venueTemplateId);
        when(eventsRepository.getEventRates(any())).thenReturn(null);


        // When
        Long result = seasonTicketService.createSeasonTicket(seasonTicketData);

        // Then
        assertEquals(seasonTicketId, result);
        verify(inventoryProviderService).createSeasonTicket(any());
        verify(venuesRepository).createVenueTemplate(any());
        verify(inventoryProviderService).createSession(anyLong(), argThat(createSessionData -> {
            assertEquals(SessionSalesType.INDIVIDUAL.getType(), createSessionData.getSaleType());
            assertEquals(entityId, createSessionData.getEntityId());
            assertEquals(eventName, createSessionData.getName());
            assertEquals(ticketTaxId, createSessionData.getTaxId());
            assertEquals(chargesTaxId, createSessionData.getChargeTaxId());
            assertEquals(venueTemplateId, createSessionData.getVenueConfigId());
            assertEquals(true, createSessionData.getSeasonTicket());
            assertEquals(true, createSessionData.getSeasonTicket());
            return true;
        }));
    }

    private static CreateSeasonTicketRequestDTO getCreateStRequest(String eventName, Long entityId, Long producerId, Integer categoryId, Long venueConfigId, Long ticketTaxId, Long chargesTaxId, String currencyCode, AdditionalConfigDTO additionalConfigDTO) {
        CreateSeasonTicketRequestDTO seasonTicketData = new CreateSeasonTicketRequestDTO();
        seasonTicketData.setName(eventName);
        seasonTicketData.setEntityId(entityId);
        seasonTicketData.setProducerId(producerId);
        seasonTicketData.setCategoryId(categoryId);
        seasonTicketData.setVenueConfigId(venueConfigId);
        seasonTicketData.setTaxId(ticketTaxId);
        seasonTicketData.setChargesTaxId(chargesTaxId);
        seasonTicketData.setCurrencyCode(currencyCode);
        seasonTicketData.setAdditionalConfig(additionalConfigDTO);
        return seasonTicketData;
    }

    private void prepareAuthData() {
        ChannelAuthenticationData authData = mock(ChannelAuthenticationData.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.ROLE_OPR_MGR.name()));
        when(authData.getAuthorities()).thenReturn(authorities);

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "ABC", null, null);
        Map<String, Object> of = new HashMap<>();
        of.put("clientId", 1);
        of.put(TokenParam.AUTH_INFO.value(), Map.of(API_KEY, "test"));
        DefaultOAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(of, authorities);
        BearerTokenAuthentication authentication = new BearerTokenAuthentication(principal, token, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void updateSeasonTicketRenewalDTO_WhenBankAccountIdIsNegative_ShouldFailValidation() {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        UpdateSeasonTicketRenewalDTO renewalDTO = new UpdateSeasonTicketRenewalDTO();
        renewalDTO.setBankAccountId(-1L); // ID negativo
        
        // When
        Set<ConstraintViolation<UpdateSeasonTicketRenewalDTO>> violations = validator.validate(renewalDTO);
        
        // Then
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("bankAccountId") 
                        && v.getMessage().contains("Bank account ID must be greater than 0")));
    }

    @Test
    void updateSeasonTicketRenewalDTO_WhenBankAccountIdIsPositive_ShouldPassValidation() {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        UpdateSeasonTicketRenewalDTO renewalDTO = new UpdateSeasonTicketRenewalDTO();
        renewalDTO.setBankAccountId(123L); // ID positivo
        
        // When
        Set<ConstraintViolation<UpdateSeasonTicketRenewalDTO>> violations = validator.validate(renewalDTO);
        
        // Then
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("bankAccountId")));
    }

    @Test
    void updateSeasonTicketRenewalDTO_WhenBankAccountIdIsNull_ShouldPassValidation() {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        UpdateSeasonTicketRenewalDTO renewalDTO = new UpdateSeasonTicketRenewalDTO();
        renewalDTO.setBankAccountId(null); // ID null (permitido)
        
        // When
        Set<ConstraintViolation<UpdateSeasonTicketRenewalDTO>> violations = validator.validate(renewalDTO);
        
        // Then
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("bankAccountId")));
    }

    @Test
    void createSeasonTicket_WithAutomaticTaxes_ShouldResolveAndUseTaxesFromVenue() {
        // Given
        Long entityId = 1L, producerId = 2L, venueConfigId = 3L, venueId = 100L;
        Long seasonTicketId = 10L, venueTemplateId = 200L;
        Long resolvedTicketTaxId = 50L, resolvedChargesTaxId = 51L;
        Integer categoryId = 6;
        String eventName = "Season Ticket With Automatic Taxes";
        String currencyCode = "EUR";

        Producer producer = new Producer();
        producer.setEntity(new Entity());
        producer.getEntity().setId(entityId);

        // Venue template with venue info
        VenueTemplate venueTemplate = new VenueTemplate();
        venueTemplate.setId(venueConfigId);
        Venue venue = new Venue();
        venue.setId(venueId);
        venueTemplate.setVenue(venue);

        Entity entity = new Entity();
        entity.setId(entityId);
        entity.setOperator(new Entity());
        entity.getOperator().setId(entityId);
        entity.setLanguage(new IdValueCodeDTO(1L, "ES", "ES"));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);
        operator.setCurrency(new IdValueDTO(1, "EUR"));

        User user = mock(User.class);

        // Taxes resolved from venue
        Tax ticketTax = new Tax();
        ticketTax.setId(resolvedTicketTaxId);
        Tax chargesTax = new Tax();
        chargesTax.setId(resolvedChargesTaxId);

        // Create request with automatic_taxes = true and NO tax_id/charges_tax_id
        CreateSeasonTicketRequestDTO seasonTicketData = new CreateSeasonTicketRequestDTO();
        seasonTicketData.setName(eventName);
        seasonTicketData.setEntityId(entityId);
        seasonTicketData.setProducerId(producerId);
        seasonTicketData.setCategoryId(categoryId);
        seasonTicketData.setVenueConfigId(venueConfigId);
        seasonTicketData.setCurrencyCode(currencyCode);
        seasonTicketData.setAutomaticTaxes(true);
        // tax_id and charges_tax_id are intentionally null

        prepareAuthData();

        when(entitiesRepository.getProducer(producerId)).thenReturn(producer);
        when(venuesRepository.getVenueTemplate(venueConfigId)).thenReturn(venueTemplate);
        when(venuesRepository.getCachedVenueTemplate(venueConfigId)).thenReturn(venueTemplate);
        when(seasonTicketValidationService.validateSeasonTicketVenueTemplate(venueTemplate, venueConfigId)).thenReturn(true);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);
        when(usersRepository.getUser(anyString(), anyLong(), anyString())).thenReturn(user);
        when(sessionsService.getInventoryProvider(eq(entityId), eq(null))).thenReturn(inventoryProviderService);
        when(inventoryProviderService.createSeasonTicket(any())).thenReturn(seasonTicketId);
        when(venuesRepository.createVenueTemplate(any())).thenReturn(venueTemplateId);
        when(eventsRepository.getEventRates(any())).thenReturn(null);
        when(eventsService.getDefaultInvoicePrefixId(producerId)).thenReturn(1L);

        // Mock the automatic tax resolution
        when(entitiesRepository.getEntityTaxes(entityId, seasonTicketId, venueId, TaxType.TICKET))
                .thenReturn(Collections.singletonList(ticketTax));
        when(entitiesRepository.getEntityTaxes(entityId, seasonTicketId, venueId, TaxType.CHARGES))
                .thenReturn(Collections.singletonList(chargesTax));

        // When
        Long result = seasonTicketService.createSeasonTicket(seasonTicketData);

        // Then
        assertEquals(seasonTicketId, result);
        verify(inventoryProviderService).createSeasonTicket(any());
        verify(venuesRepository).createVenueTemplate(any());
        verify(entitiesRepository).getEntityTaxes(entityId, seasonTicketId, venueId, TaxType.TICKET);
        verify(entitiesRepository).getEntityTaxes(entityId, seasonTicketId, venueId, TaxType.CHARGES);
    }

    @Test
    void createSeasonTicket_WithAutomaticTaxes_WhenNoTaxesFound_ShouldThrowException() {
        // Given
        Long entityId = 1L, producerId = 2L, venueConfigId = 3L, venueId = 100L;
        Long seasonTicketId = 10L, venueTemplateId = 200L;
        Integer categoryId = 6;
        String eventName = "Season Ticket With Automatic Taxes - No Taxes";
        String currencyCode = "EUR";

        Producer producer = new Producer();
        producer.setEntity(new Entity());
        producer.getEntity().setId(entityId);

        VenueTemplate venueTemplate = new VenueTemplate();
        venueTemplate.setId(venueConfigId);
        Venue venue = new Venue();
        venue.setId(venueId);
        venueTemplate.setVenue(venue);

        Entity entity = new Entity();
        entity.setId(entityId);
        entity.setOperator(new Entity());
        entity.getOperator().setId(entityId);
        entity.setLanguage(new IdValueCodeDTO(1L, "ES", "ES"));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);
        operator.setCurrency(new IdValueDTO(1, "EUR"));

        User user = mock(User.class);

        CreateSeasonTicketRequestDTO seasonTicketData = new CreateSeasonTicketRequestDTO();
        seasonTicketData.setName(eventName);
        seasonTicketData.setEntityId(entityId);
        seasonTicketData.setProducerId(producerId);
        seasonTicketData.setCategoryId(categoryId);
        seasonTicketData.setVenueConfigId(venueConfigId);
        seasonTicketData.setCurrencyCode(currencyCode);
        seasonTicketData.setAutomaticTaxes(true);

        prepareAuthData();

        when(entitiesRepository.getProducer(producerId)).thenReturn(producer);
        when(venuesRepository.getVenueTemplate(venueConfigId)).thenReturn(venueTemplate);
        when(seasonTicketValidationService.validateSeasonTicketVenueTemplate(venueTemplate, venueConfigId)).thenReturn(true);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);
        when(usersRepository.getUser(anyString(), anyLong(), anyString())).thenReturn(user);
        when(sessionsService.getInventoryProvider(eq(entityId), eq(null))).thenReturn(inventoryProviderService);
        when(inventoryProviderService.createSeasonTicket(any())).thenReturn(seasonTicketId);
        when(venuesRepository.createVenueTemplate(any())).thenReturn(venueTemplateId);

        // Return empty lists for taxes - this should trigger the error
        when(entitiesRepository.getEntityTaxes(entityId, null, venueId, TaxType.TICKET))
                .thenReturn(Collections.emptyList());
        when(entitiesRepository.getEntityTaxes(entityId, null, venueId, TaxType.CHARGES))
                .thenReturn(Collections.emptyList());

        // When & Then
        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> seasonTicketService.createSeasonTicket(seasonTicketData));

        assertEquals(ApiMgmtErrorCode.ENTITY_TAXES_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void createSeasonTicket_WithAutomaticTaxes_WhenVenueTemplateNotFound_ShouldThrowException() {
        // Given
        Long entityId = 1L, producerId = 2L, venueConfigId = 3L;
        Long seasonTicketId = 10L, venueTemplateId = 200L;
        Integer categoryId = 6;
        String eventName = "Season Ticket With Automatic Taxes - No Venue";
        String currencyCode = "EUR";

        Producer producer = new Producer();
        producer.setEntity(new Entity());
        producer.getEntity().setId(entityId);

        // First call returns valid template for validation, second call returns null
        VenueTemplate venueTemplate = new VenueTemplate();
        venueTemplate.setId(venueConfigId);

        Entity entity = new Entity();
        entity.setId(entityId);
        entity.setOperator(new Entity());
        entity.getOperator().setId(entityId);
        entity.setLanguage(new IdValueCodeDTO(1L, "ES", "ES"));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);
        operator.setCurrency(new IdValueDTO(1, "EUR"));

        User user = mock(User.class);

        CreateSeasonTicketRequestDTO seasonTicketData = new CreateSeasonTicketRequestDTO();
        seasonTicketData.setName(eventName);
        seasonTicketData.setEntityId(entityId);
        seasonTicketData.setProducerId(producerId);
        seasonTicketData.setCategoryId(categoryId);
        seasonTicketData.setVenueConfigId(venueConfigId);
        seasonTicketData.setCurrencyCode(currencyCode);
        seasonTicketData.setAutomaticTaxes(true);

        prepareAuthData();

        when(entitiesRepository.getProducer(producerId)).thenReturn(producer);
        // First call for validation returns template, second call for tax resolution returns null
        when(venuesRepository.getVenueTemplate(venueConfigId))
                .thenReturn(venueTemplate)
                .thenReturn(null);
        when(seasonTicketValidationService.validateSeasonTicketVenueTemplate(venueTemplate, venueConfigId)).thenReturn(true);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);
        when(usersRepository.getUser(anyString(), anyLong(), anyString())).thenReturn(user);
        when(sessionsService.getInventoryProvider(eq(entityId), eq(null))).thenReturn(inventoryProviderService);
        when(inventoryProviderService.createSeasonTicket(any())).thenReturn(seasonTicketId);
        when(venuesRepository.createVenueTemplate(any())).thenReturn(venueTemplateId);

        // When & Then
        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> seasonTicketService.createSeasonTicket(seasonTicketData));

        assertEquals(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void createSeasonTicket_WithoutAutomaticTaxes_ShouldUseProvidedTaxIds() {
        // Given
        Long entityId = 1L, producerId = 2L, venueConfigId = 3L, venueId = 100L;
        Long ticketTaxId = 4L, chargesTaxId = 5L;
        Long seasonTicketId = 10L, venueTemplateId = 200L;
        Integer categoryId = 6;
        String eventName = "Season Ticket Without Automatic Taxes";
        String currencyCode = "EUR";

        Producer producer = new Producer();
        producer.setEntity(new Entity());
        producer.getEntity().setId(entityId);

        List<EntityTax> taxes = new ArrayList<>();
        EntityTax tax1 = new EntityTax();
        tax1.setIdImpuesto(ticketTaxId.intValue());
        taxes.add(tax1);
        EntityTax tax2 = new EntityTax();
        tax2.setIdImpuesto(chargesTaxId.intValue());
        taxes.add(tax2);

        VenueTemplate venueTemplate = new VenueTemplate();
        venueTemplate.setId(venueConfigId);
        Venue venue = new Venue();
        venue.setId(venueId);
        venueTemplate.setVenue(venue);

        Entity entity = new Entity();
        entity.setId(entityId);
        entity.setOperator(new Entity());
        entity.getOperator().setId(entityId);
        entity.setLanguage(new IdValueCodeDTO(1L, "ES", "ES"));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);
        operator.setCurrency(new IdValueDTO(1, "EUR"));

        User user = mock(User.class);

        // Create request with automatic_taxes = false and explicit tax_id/charges_tax_id
        CreateSeasonTicketRequestDTO seasonTicketData = new CreateSeasonTicketRequestDTO();
        seasonTicketData.setName(eventName);
        seasonTicketData.setEntityId(entityId);
        seasonTicketData.setProducerId(producerId);
        seasonTicketData.setCategoryId(categoryId);
        seasonTicketData.setVenueConfigId(venueConfigId);
        seasonTicketData.setCurrencyCode(currencyCode);
        seasonTicketData.setAutomaticTaxes(false);
        seasonTicketData.setTaxId(ticketTaxId);
        seasonTicketData.setChargesTaxId(chargesTaxId);

        prepareAuthData();

        when(entitiesRepository.getProducer(producerId)).thenReturn(producer);
        when(entitiesRepository.getTaxes(entityId)).thenReturn(taxes);
        when(venuesRepository.getVenueTemplate(venueConfigId)).thenReturn(venueTemplate);
        when(venuesRepository.getCachedVenueTemplate(venueConfigId)).thenReturn(venueTemplate);
        when(seasonTicketValidationService.validateSeasonTicketVenueTemplate(venueTemplate, venueConfigId)).thenReturn(true);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);
        when(usersRepository.getUser(anyString(), anyLong(), anyString())).thenReturn(user);
        when(sessionsService.getInventoryProvider(eq(entityId), eq(null))).thenReturn(inventoryProviderService);
        when(inventoryProviderService.createSeasonTicket(any())).thenReturn(seasonTicketId);
        when(venuesRepository.createVenueTemplate(any())).thenReturn(venueTemplateId);
        when(eventsRepository.getEventRates(any())).thenReturn(null);
        when(eventsService.getDefaultInvoicePrefixId(producerId)).thenReturn(1L);

        // When
        Long result = seasonTicketService.createSeasonTicket(seasonTicketData);

        // Then
        assertEquals(seasonTicketId, result);
        // Verify that getEntityTaxes was NOT called (taxes are provided explicitly)
        verify(entitiesRepository, org.mockito.Mockito.never()).getEntityTaxes(anyLong(), any(), anyLong(), any());
    }

}
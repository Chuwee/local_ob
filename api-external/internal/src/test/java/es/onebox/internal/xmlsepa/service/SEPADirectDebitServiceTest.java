package es.onebox.internal.xmlsepa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketPrice;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalInfoDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;
import es.onebox.common.datasources.ms.event.dto.SeatRenewal;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequest;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequestItem;
import es.onebox.common.datasources.ms.event.dto.XMLSEPAConfigData;
import es.onebox.common.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.core.mail.template.manager.TemplateResolver;
import es.onebox.core.mail.template.manager.model.TemplateContentDTO;
import es.onebox.core.mail.template.manager.model.TemplateScope;
import es.onebox.internal.xmlsepa.dao.SEPAMailTemplateCouchDao;
import es.onebox.internal.xmlsepa.service.mailing.SEPAMailingService;
import es.onebox.internal.xmlsepa.service.sepa.SEPADirectDebitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SEPADirectDebitServiceTest {
    private static final Long SEASON_TICKET_ID = 1L;
    private static final Long ENTITY_ID = 2L;
    private static final Long USER_ID = 3L;
    private static final String IBAN = "DE89370400440532013000";
    private static final String BIC = "DEUTDEBBXXX";
    private static final String LANGUAGE = "es_ES";
    private static final String EXPORT = "exportAutomaticSales";

    @Mock
    private S3BinaryRepository s3AutomaticSalesRepository;

    @Mock
    private SeasonTicketRepository seasonTicketRepository;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private MasterDataRepository masterDataRepository;

    @Mock
    private SEPAMailTemplateCouchDao sepaMailTemplateCouchDao;

    @Mock
    private TemplateResolver templateResolver;

    @Mock
    private SEPAMailingService sepaMailingService;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SEPADirectDebitService sepaDirectDebitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processSEPADirectDebit_WithNullConfiguration_ShouldReturnEarly() {
        when(seasonTicketRepository.getSeasonTicketRenewalConfig(SEASON_TICKET_ID)).thenReturn(null);

        sepaDirectDebitService.processSEPADirectDebit(SEASON_TICKET_ID, USER_ID);

        verify(seasonTicketRepository, times(1)).getSeasonTicketRenewalConfig(SEASON_TICKET_ID);
        verify(objectMapper, never()).convertValue(any(), any(Class.class));
        verify(s3AutomaticSalesRepository, never()).upload(anyString(), any(InputStream.class), anyBoolean(), any(ObjectPolicy.class));
    }

    @Test
    void processSEPADirectDebit_WithNullConfigInConfiguration_ShouldReturnEarly() {
        Long seasonTicketId = 123L;
        SeasonTicketRenewalConfigDTO configDTO = new SeasonTicketRenewalConfigDTO();
        when(seasonTicketRepository.getSeasonTicketRenewalConfig(seasonTicketId)).thenReturn(configDTO);

        sepaDirectDebitService.processSEPADirectDebit(seasonTicketId, USER_ID);

        verify(seasonTicketRepository, times(1)).getSeasonTicketRenewalConfig(seasonTicketId);
        verify(objectMapper, never()).convertValue(any(), any(Class.class));
        verify(s3AutomaticSalesRepository, never()).upload(anyString(), any(InputStream.class), anyBoolean(), any(ObjectPolicy.class));
    }

    @Test
    void processSEPADirectDebit_WithValidConfiguration_ShouldCreateAndUploadSEPA() throws Exception {
        SeasonTicketRenewalConfigDTO configDTO = createSeasonTicketRenewalConfig();
        when(seasonTicketRepository.getSeasonTicketRenewalConfig(SEASON_TICKET_ID)).thenReturn(configDTO);

        List<SeasonTicketPrice> prices = List.of(createSeasonTicketPrice());
        when(seasonTicketRepository.getSeasonTicketPrices(SEASON_TICKET_ID)).thenReturn(prices);

        SeasonTicketRenewalDTO renewal = createRenewal();
        SeasonTicketRenewalsDTO renewals = new SeasonTicketRenewalsDTO();
        renewals.setData(List.of(renewal));
        when(seasonTicketRepository.getSeasonTicketRenewals(eq(SEASON_TICKET_ID), any(SeasonTicketRenewalsFilter.class))).thenReturn(renewals);
        when(seasonTicketRepository.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(createDefaultSeasonTicket());

        Method method = SEPADirectDebitService.class.getDeclaredMethod("getSepaTransactions", Long.class, String.class);
        method.setAccessible(true);
        List<?> transactions = (List<?>) method.invoke(sepaDirectDebitService, SEASON_TICKET_ID, "Example Creditor");

        assertNotNull(transactions);
        assertEquals(1, transactions.size());

        EntityBankAccount bankAccount = createEntityBankAccount();
        when(entitiesRepository.getEntityBankAccount(anyLong(), eq(123L))).thenReturn(bankAccount);
        when(usersRepository.getByIdCached(USER_ID)).thenReturn(createUser());
        when(masterDataRepository.getLanguage(1L)).thenReturn(createLanguage());
        when(sepaMailTemplateCouchDao.getTemplate(anyString(), anyString())).thenReturn(createTemplateContent());

        TemplateResolver.Context mockContext = mock(TemplateResolver.Context.class);
        when(templateResolver.context()).thenReturn(mockContext);
        when(mockContext.of(any(TemplateScope.class), anyString())).thenReturn(mockContext);
        when(mockContext.withDocument(any())).thenReturn(mockContext);
        when(mockContext.withParams(anyMap())).thenReturn(mockContext);
        when(mockContext.build()).thenReturn("Email body");

        when(s3AutomaticSalesRepository.getPublicSignedUrl(anyString(), any())).thenReturn("https://fake-s3-url.com/sepa-file.xml");

        sepaDirectDebitService.processSEPADirectDebit(SEASON_TICKET_ID, USER_ID);

        verify(seasonTicketRepository).getSeasonTicketRenewalConfig(SEASON_TICKET_ID);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<Boolean> booleanCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<ObjectPolicy> objectPolicyCaptor = ArgumentCaptor.forClass(ObjectPolicy.class);

        verify(s3AutomaticSalesRepository).upload(
                pathCaptor.capture(),
                inputStreamCaptor.capture(),
                booleanCaptor.capture(),
                objectPolicyCaptor.capture()
        );

        assertNotNull(pathCaptor.getValue());
        assertEquals(false, booleanCaptor.getValue());
        assertEquals("text/xml", objectPolicyCaptor.getValue().getContentType());
    }

    @Test
    void processSEPADirectDebit_WithMultiplePages_ShouldProcessAllPages() {
        SeasonTicketRenewalConfigDTO configDTO = createSeasonTicketRenewalConfig();
        when(seasonTicketRepository.getSeasonTicketRenewalConfig(SEASON_TICKET_ID)).thenReturn(configDTO);
        EntityBankAccount bankAccount = createEntityBankAccount();
        when(entitiesRepository.getEntityBankAccount(anyLong(), eq(123L))).thenReturn(bankAccount);
        List<SeasonTicketPrice> prices = List.of(createSeasonTicketPrice());
        when(seasonTicketRepository.getSeasonTicketPrices(SEASON_TICKET_ID)).thenReturn(prices);

        SeasonTicketRenewalsDTO renewalsDTO1 = new SeasonTicketRenewalsDTO();
        List<SeasonTicketRenewalDTO> renewals1 = new ArrayList<>();

        for (int i = 0; i < 999; i++) {
            SeasonTicketRenewalDTO renewalItem = createRenewal();
            renewals1.add(renewalItem);
        }

        renewalsDTO1.setData(renewals1);

        SeasonTicketRenewalsDTO renewalsDTO2 = new SeasonTicketRenewalsDTO();
        List<SeasonTicketRenewalDTO> renewals2 = new ArrayList<>();

        SeasonTicketRenewalDTO renewalItem = createRenewal();
        renewals2.add(renewalItem);

        renewalsDTO2.setData(renewals2);

        when(seasonTicketRepository.getSeasonTicketRenewals(eq(SEASON_TICKET_ID), any(SeasonTicketRenewalsFilter.class)))
                .thenReturn(renewalsDTO1)
                .thenReturn(renewalsDTO2);

        SeasonTicketDTO seasonTicketDTO = createDefaultSeasonTicket();
        when(seasonTicketRepository.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(seasonTicketDTO);

        when(usersRepository.getByIdCached(USER_ID)).thenReturn(createUser());
        when(masterDataRepository.getLanguage(1L)).thenReturn(createLanguage());
        when(sepaMailTemplateCouchDao.getTemplate(anyString(), anyString())).thenReturn(createTemplateContent());

        TemplateResolver.Context mockContext = mock(TemplateResolver.Context.class);
        when(templateResolver.context()).thenReturn(mockContext);
        when(mockContext.of(any(TemplateScope.class), anyString())).thenReturn(mockContext);
        when(mockContext.withDocument(any())).thenReturn(mockContext);
        when(mockContext.withParams(anyMap())).thenReturn(mockContext);
        when(mockContext.build()).thenReturn("Email body");

        when(s3AutomaticSalesRepository.getPublicSignedUrl(anyString(), any())).thenReturn("https://fake-s3-url.com/sepa-file.xml");

        sepaDirectDebitService.processSEPADirectDebit(SEASON_TICKET_ID, USER_ID);

        verify(seasonTicketRepository, times(1)).getSeasonTicketRenewalConfig(SEASON_TICKET_ID);

        verify(s3AutomaticSalesRepository, times(1)).upload(
                anyString(),
                any(InputStream.class),
                eq(false),
                any(ObjectPolicy.class)
        );
    }

    @Test
    void getFilePath_ShouldReturnCorrectPath() throws Exception {
        SeasonTicketDTO seasonTicketDTO = createDefaultSeasonTicket();
        when(seasonTicketRepository.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(seasonTicketDTO);

        Method getFilePathMethod = SEPADirectDebitService.class.getDeclaredMethod("getFilePath", SeasonTicketDTO.class);
        getFilePathMethod.setAccessible(true);

        String result = (String) getFilePathMethod.invoke(sepaDirectDebitService, seasonTicketDTO);

        assertNotNull(result);

        assertTrue(result.startsWith(SEPADirectDebitService.ENTITIES_PATH + ENTITY_ID + SEPADirectDebitService.FOLDER_SEPARATOR));
        assertTrue(result.contains(SEPADirectDebitService.SEASON_TICKETS_PATH + SEASON_TICKET_ID + SEPADirectDebitService.FOLDER_SEPARATOR));
        assertTrue(result.contains(SEPADirectDebitService.SEPA_PATH));
        assertTrue(result.startsWith(SEPADirectDebitService.ENTITIES_PATH));
        assertTrue(result.endsWith(SEPADirectDebitService.EXTENSION_XML));
        assertTrue(result.contains(SEPADirectDebitService.BASE_FILENAME));
    }

    @Test
    void getSepaTransactions_ShouldUpdateRenewals() throws Exception {
        SeasonTicketRenewalDTO renewal = createRenewal();
        SeasonTicketRenewalsDTO renewals = new SeasonTicketRenewalsDTO();
        renewals.setData(List.of(renewal));
        List<SeasonTicketPrice> prices = List.of(createSeasonTicketPrice());
        when(seasonTicketRepository.getSeasonTicketPrices(SEASON_TICKET_ID)).thenReturn(prices);

        when(seasonTicketRepository.getSeasonTicketRenewals(eq(SEASON_TICKET_ID), any(SeasonTicketRenewalsFilter.class))).thenReturn(renewals);
        when(seasonTicketRepository.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(createDefaultSeasonTicket());

        Method method = SEPADirectDebitService.class.getDeclaredMethod("getSepaTransactions", Long.class, String.class);
        method.setAccessible(true);

        List<?> transactions = (List<?>) method.invoke(sepaDirectDebitService, SEASON_TICKET_ID, "Example Creditor");

        assertNotNull(transactions);
        assertEquals(1, transactions.size());

        ArgumentCaptor<UpdateRenewalRequest> captor = ArgumentCaptor.forClass(UpdateRenewalRequest.class);
        verify(seasonTicketRepository, times(1)).updateSeasonTicketRenewals(eq(SEASON_TICKET_ID), captor.capture());

        List<UpdateRenewalRequestItem> updates = captor.getValue().getItems();
        assertNotNull(updates);
        assertEquals(1, updates.size());
    }


    private static XMLSEPAConfigData createDefaultSEPAConfig() {
        XMLSEPAConfigData config = new XMLSEPAConfigData();
        config.setCreditorId("DE98ZZZ09999999999");
        config.setIban(IBAN);
        config.setBic(BIC);
        config.setName("Example Creditor");
        return config;
    }

    private static SeasonTicketDTO createDefaultSeasonTicket() {
        SeasonTicketDTO ticket = new SeasonTicketDTO();
        ticket.setId(SEASON_TICKET_ID);
        ticket.setEntityId(ENTITY_ID);
        ticket.setAllowRenewal(true);
        ticket.setRenewal(createRenewalInfo());
        return ticket;
    }

    private static SeasonTicketRenewalInfoDTO createRenewalInfo() {
        SeasonTicketRenewalInfoDTO out = new SeasonTicketRenewalInfoDTO();
        out.setAutoRenewal(true);
        return out;
    }

    private static SeasonTicketRenewalDTO createRenewal() {
        SeasonTicketRenewalDTO dto = new SeasonTicketRenewalDTO();
        dto.setUserId("12345");
        dto.setName("Fran");
        dto.setAddress("Real");
        dto.setPostalCode("30110");
        dto.setCity("Murcia");
        dto.setCountry("ES");
        dto.setIban(IBAN);
        dto.setBic(BIC);
        dto.setActualRateId(2L);
        dto.setActualSeat(createSeatRenewal());
        return dto;
    }

    private static SeatRenewal createSeatRenewal() {
        SeatRenewal seatRenewal = new SeatRenewal();
        seatRenewal.setSeatId(1L);
        seatRenewal.setPrizeZoneId(1L);
        return seatRenewal;
    }

    private static User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setLanguageId(1);
        return user;
    }

    private static Language createLanguage() {
        Language language = new Language();
        language.setId(1L);
        language.setCode(LANGUAGE);
        return language;
    }

    private static TemplateContentDTO createTemplateContent() {
        TemplateContentDTO content = new TemplateContentDTO();
        content.setSubject("SEPA Direct Debit Notification");
        return content;
    }

    private static SeasonTicketRenewalConfigDTO createSeasonTicketRenewalConfig() {
        SeasonTicketRenewalConfigDTO out = new SeasonTicketRenewalConfigDTO();
        out.setBankAccountId(123L);
        return out;
    }

    private static EntityBankAccount createEntityBankAccount() {
        EntityBankAccount out = new EntityBankAccount();
        out.setName("Example Creditor");
        out.setIban("ES7620770024003102575766");
        out.setBic("BKBKESMMXXX");
        out.setCc("ES12123NIF");
        return out;
    }

    private static SeasonTicketPrice createSeasonTicketPrice() {
        SeasonTicketPrice price = new SeasonTicketPrice();
        price.setPriceTypeId(1L);
        price.setRateId(2);
        price.setPrice(100.0);
        return price;
    }
}
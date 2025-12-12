package es.onebox.service;

import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.fever.dto.city.FeverCitiesDTO;
import es.onebox.fever.dto.city.FeverCityDTO;
import es.onebox.fever.dto.city.FeverCityData;
import es.onebox.fever.repository.FeverRepository;
import es.onebox.fever.service.EntityWebhookService;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class EntityWebhookServiceTest {

    private static final String ENTITY_ID = "1";

    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private MasterDataRepository masterDataRepository;
    @Mock
    private FeverRepository feverRepository;

    @InjectMocks
    private EntityWebhookService entityWebhookService;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendEntityData_update_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("UPDATE");
        webhookFever.getNotificationMessage().setId(ENTITY_ID);
        EntityDTO entity = generateEntity();
        entity.setId(1L);
        entity.setName("name");
        entity.setEmail("email");
        entity.setSocialReason("socialReason");
        entity.setNif("nif");
        entity.setCountryId(1);

        when(entitiesRepository.getById(Long.valueOf(ENTITY_ID))).thenReturn(entity);
        List<CountryDTO> countries = generateMasterDataCountries();
        when(masterDataRepository.countries()).thenReturn(countries);
        FeverCityData barcelona = generateFvCities("Barcelona");
        when(feverRepository.getCity("Barcelona", "ES")).thenReturn(barcelona);
        FeverCityData montevideo = generateFvCities("Montevideo");
        when(feverRepository.getCity("Montevideo", "UY")).thenReturn(montevideo);

        WebhookFeverDTO response = entityWebhookService.sendEntityFvZoneData(webhookFever);
        Assertions.assertEquals(ENTITY_ID, response.getFeverMessage().getId());
        Assertions.assertEquals(entity.getEmail(), response.getFeverMessage().getEntityDetail().getEmail());
        Assertions.assertEquals(entity.getNif(), response.getFeverMessage().getEntityDetail().getNif());
        Assertions.assertEquals(entity.getName(), response.getFeverMessage().getEntityDetail().getName());
        Assertions.assertEquals(entity.getSocialReason(), response.getFeverMessage().getEntityDetail().getBusinessName());
        Assertions.assertEquals(entity.getPhone(), response.getFeverMessage().getEntityDetail().getPhone());

        Assertions.assertEquals(entity.getAddress(), response.getFeverMessage().getEntityDetail().getAddress().getAddress());
        Assertions.assertEquals(countries.get(0).getCode(), response.getFeverMessage().getEntityDetail().getAddress().getCountryCode());
        Assertions.assertEquals(barcelona.getData().getCities().get(0).getId(), response.getFeverMessage().getEntityDetail().getAddress().getCityCriteriaId());

        Assertions.assertEquals(entity.getInvoiceAddress(), response.getFeverMessage().getEntityDetail().getInvoiceAddress().getAddress());
        Assertions.assertEquals(countries.get(1).getCode(), response.getFeverMessage().getEntityDetail().getInvoiceAddress().getCountryCode());
        Assertions.assertEquals(montevideo.getData().getCities().get(0).getId(), response.getFeverMessage().getEntityDetail().getInvoiceAddress().getCityCriteriaId());
    }

    @Test
    public void sendEntityData_update_notEntity_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("UPDATE");
        webhookFever.getNotificationMessage().setId(ENTITY_ID);

        when(entitiesRepository.getById(Long.valueOf(ENTITY_ID))).thenReturn(null);

        WebhookFeverDTO response = entityWebhookService.sendEntityFvZoneData(webhookFever);
        Assertions.assertNull(response.getFeverMessage().getId());
        Assertions.assertEquals(Boolean.FALSE, response.getAllowSend());
    }

    @Test
    public void sendEntityData_create_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("CREATE");
        webhookFever.getNotificationMessage().setId(ENTITY_ID);
        EntityDTO entity = generateEntity();
        entity.setId(1L);
        entity.setName("name");
        entity.setEmail("email");
        entity.setSocialReason("socialReason");
        entity.setNif("nif");
        entity.setCountryId(1);
        entity.setExternalReference(null);

        when(entitiesRepository.getById(Long.valueOf(ENTITY_ID))).thenReturn(entity);
        List<CountryDTO> countries = generateMasterDataCountries();
        when(masterDataRepository.countries()).thenReturn(countries);
        FeverCityData barcelona = generateFvCities("Barcelona");
        when(feverRepository.getCity("Barcelona", "ES")).thenReturn(barcelona);
        FeverCityData montevideo = generateFvCities("Montevideo");
        when(feverRepository.getCity("Montevideo", "UY")).thenReturn(montevideo);

        WebhookFeverDTO response = entityWebhookService.sendEntityFvZoneData(webhookFever);
        Assertions.assertEquals(ENTITY_ID, response.getFeverMessage().getId());
        Assertions.assertEquals(entity.getEmail(), response.getFeverMessage().getEntityDetail().getEmail());
        Assertions.assertEquals(entity.getNif(), response.getFeverMessage().getEntityDetail().getNif());
        Assertions.assertEquals(entity.getName(), response.getFeverMessage().getEntityDetail().getName());
        Assertions.assertEquals(entity.getSocialReason(), response.getFeverMessage().getEntityDetail().getBusinessName());
        Assertions.assertEquals(entity.getPhone(), response.getFeverMessage().getEntityDetail().getPhone());

        Assertions.assertEquals(entity.getAddress(), response.getFeverMessage().getEntityDetail().getAddress().getAddress());
        Assertions.assertEquals(countries.get(0).getCode(), response.getFeverMessage().getEntityDetail().getAddress().getCountryCode());
        Assertions.assertEquals(barcelona.getData().getCities().get(0).getId(), response.getFeverMessage().getEntityDetail().getAddress().getCityCriteriaId());

        Assertions.assertEquals(entity.getInvoiceAddress(), response.getFeverMessage().getEntityDetail().getInvoiceAddress().getAddress());
        Assertions.assertEquals(countries.get(1).getCode(), response.getFeverMessage().getEntityDetail().getInvoiceAddress().getCountryCode());
        Assertions.assertEquals(montevideo.getData().getCities().get(0).getId(), response.getFeverMessage().getEntityDetail().getInvoiceAddress().getCityCriteriaId());
    }

    @Test
    public void sendEntityData_create_externalReferenceSet_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage("CREATE");
        webhookFever.getNotificationMessage().setId(ENTITY_ID);
        EntityDTO entity = generateEntity();
        entity.setId(1L);
        entity.setName("name");
        entity.setEmail("email");
        entity.setSocialReason("socialReason");
        entity.setNif("nif");
        entity.setCountryId(1);

        when(entitiesRepository.getById(Long.valueOf(ENTITY_ID))).thenReturn(entity);

        WebhookFeverDTO response = entityWebhookService.sendEntityFvZoneData(webhookFever);
        Assertions.assertNull(response.getFeverMessage().getId());
        Assertions.assertEquals(Boolean.FALSE, response.getAllowSend());
    }

    @Test
    public void sendEntityData_noAction_doNothing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage(null);
        webhookFever.getNotificationMessage().setId(ENTITY_ID);

        WebhookFeverDTO response = entityWebhookService.sendEntityFvZoneData(webhookFever);
        Assertions.assertNull(response.getFeverMessage().getId());
        Assertions.assertEquals(Boolean.FALSE, response.getAllowSend());
    }

    private WebhookFeverDTO generateWebhookMessage(String headerAction) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("ob-action")).thenReturn(headerAction);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        FeverMessageDTO message = new FeverMessageDTO();
        return new WebhookFeverDTO(notificationMessage, req, message);
    }

    private EntityDTO generateEntity() {
        EntityDTO entity = new EntityDTO();
        entity.setAllowFeverZone(Boolean.TRUE);
        entity.setPhone("testPhone");
        entity.setName("testName");
        entity.setCountryId(1);
        entity.setAddress("testAddress");
        entity.setCity("Barcelona");
        entity.setInvoiceAddress("testInvoiceAddress");
        entity.setInvoiceCity("Montevideo");
        entity.setInvoiceCountryId(2);
        entity.setEmail("testEmail");
        entity.setSocialReason("testSocialReason");
        entity.setExternalReference("12345");
        entity.setNif("testNif");
        return entity;
    }

    private List<CountryDTO> generateMasterDataCountries() {
        List<CountryDTO> countries = new ArrayList<>();
        CountryDTO es = new CountryDTO();
        es.setCode("ES");
        es.setId(1);
        es.setName("España");
        es.setNumericCode("1");

        CountryDTO uy = new CountryDTO();
        uy.setCode("UY");
        uy.setId(2);
        uy.setName("Uruguay");
        uy.setNumericCode("2");

        countries.add(es);
        countries.add(uy);
        return countries;
    }

    private FeverCityData generateFvCities(String cityName) {
        FeverCityDTO city = new FeverCityDTO();

        if(cityName.equals("Barcelona")) {
            city.setId(1005424);
            city.setCanonicalName("Barcelona,Catalonia,Spain");
            city.setStatus("active");
            city.setCountryId(2724);
            city.setName("Barcelona");
        }

        if(cityName.equals("Montevideo")) {
            city.setId(1012872);
            city.setCanonicalName("Montevideo,Uruguay");
            city.setStatus("active");
            city.setCountryId(2858);
            city.setName("Montevideo");
        }

        List<FeverCityDTO> feverCitiesList = new ArrayList<>();
        feverCitiesList.add(city);

        FeverCitiesDTO feverCities = new FeverCitiesDTO();
        feverCities.setCities(feverCitiesList);

        FeverCityData feverCityData = new FeverCityData();
        feverCityData.setData(feverCities);
        return feverCityData;
    }
}

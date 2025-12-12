package es.onebox.internal.automaticsales.service;

import es.onebox.common.datasources.ms.channel.dto.ChannelFormField;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.AttendantField;
import es.onebox.common.datasources.ms.event.dto.AttendantsConfigDTO;
import es.onebox.common.datasources.ms.event.dto.AttendantsFields;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.automaticsales.processsales.dto.SaleDTO;
import es.onebox.internal.automaticsales.processsales.service.ValidationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ValidationServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MsEventRepository msEventRepository;

    @InjectMocks
    private ValidationService validationService;

    private List<SaleDTO> validSales;
    private List<SaleDTO> invalidSales;
    private AttendantsConfigDTO activeConfigAllChannels;
    private AttendantsConfigDTO activeConfigChannels;
    private AttendantsConfigDTO inactiveConfig;
    private AttendantsFields attendantsFields;
    private ChannelFormsResponse channelFormsResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestData();
    }
    
    private void setupTestData() {

        validSales = new ArrayList<>();
        SaleDTO validSale = new SaleDTO();
        validSale.setName("User");
        validSale.setFirstSurname("Test");
        validSale.setSecondSurname("A");
        validSale.setDni("00000000T");
        validSale.setPhone("666777888");
        validSale.setEmail("user@test.com ");
        validSales.add(validSale);

        invalidSales = new ArrayList<>();
        SaleDTO invalidSale = new SaleDTO();
        invalidSale.setName("User");
        invalidSale.setFirstSurname("Test");
        validSale.setSecondSurname("B");
        invalidSale.setDni("00000000T");
        invalidSale.setPhone("666888999");
        invalidSales.add(invalidSale);

        activeConfigAllChannels = new AttendantsConfigDTO();
        activeConfigAllChannels.setActive(true);
        activeConfigAllChannels.setAllChannelsActive(true);

        activeConfigChannels = new AttendantsConfigDTO();
        activeConfigChannels.setActive(true);
        activeConfigChannels.setActiveChannels(List.of(2L));
        
        inactiveConfig = new AttendantsConfigDTO();
        inactiveConfig.setActive(false);

        attendantsFields = new AttendantsFields();
        List<AttendantField> fields = new ArrayList<>();
        
        AttendantField emailField = new AttendantField();
        emailField.setKey("ATTENDANT_MAIL");
        emailField.setMandatory(true);
        
        AttendantField nameField = new AttendantField();
        nameField.setKey("ATTENDANT_NAME");
        nameField.setMandatory(true);
        
        fields.add(emailField);
        fields.add(nameField);
        attendantsFields.setData(fields);

        channelFormsResponse = new ChannelFormsResponse();
        List<ChannelFormField> purchaseFields = new ArrayList<>();
        
        ChannelFormField emailChannelField = new ChannelFormField();
        emailChannelField.setKey("email");
        emailChannelField.setName("Email");
        emailChannelField.setMandatory(true);
        
        ChannelFormField nameChannelField = new ChannelFormField();
        nameChannelField.setKey("firstName");
        nameChannelField.setName("Nombre");
        nameChannelField.setMandatory(true);
        
        purchaseFields.add(emailChannelField);
        purchaseFields.add(nameChannelField);
        channelFormsResponse.setPurchase(purchaseFields);
    }
    
    @Test
    public void validateEventFieldsWithoutConfigTest() {
        when(msEventRepository.getAttendantsConfig(anyLong())).thenReturn(null);
        assertDoesNotThrow(() -> validationService.validateEventFields(invalidSales, 1L, 1L));
    }
    
    @Test
    public void validateEventFieldsWithInactiveConfigTest() {
        when(msEventRepository.getAttendantsConfig(anyLong())).thenReturn(inactiveConfig);
        assertDoesNotThrow(() -> validationService.validateEventFields(invalidSales, 1L, 1L));
    }

    @Test
    public void validateEventFieldsChannelsTest() {
        when(msEventRepository.getAttendantsConfig(anyLong())).thenReturn(activeConfigChannels);
        assertDoesNotThrow(() -> validationService.validateEventFields(invalidSales, 1L, 1L));
    }

    @Test
    public void validateEventFieldsChannelsWithInvalidFieldsTest() {
        when(msEventRepository.getAttendantsConfig(anyLong())).thenReturn(activeConfigChannels);
        when(msEventRepository.getAttendantsFields(anyLong())).thenReturn(attendantsFields);
        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                validationService.validateEventFields(invalidSales, 1L, 2L));
        assertTrue(exception.getMessage().contains("ATTENDANT_MAIL"));
    }
    
    @Test
    public void validateEventFieldsTest() {
        when(msEventRepository.getAttendantsConfig(anyLong())).thenReturn(activeConfigAllChannels);
        when(msEventRepository.getAttendantsFields(anyLong())).thenReturn(attendantsFields);

        assertDoesNotThrow(() -> validationService.validateEventFields(validSales, 1L, 1L));
    }
    
    @Test
    public void validateEventFieldsWithInvalidFieldsTest() {
        when(msEventRepository.getAttendantsConfig(anyLong())).thenReturn(activeConfigAllChannels);
        when(msEventRepository.getAttendantsFields(anyLong())).thenReturn(attendantsFields);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> 
            validationService.validateEventFields(invalidSales, 1L, 1L)
        );
        assertEquals(exception.getErrorCode(), ApiExternalErrorCode.INPUT_EVENT_NULL_VALUES.getErrorCode());
        assertTrue(exception.getMessage().contains("ATTENDANT_MAIL"));
    }
    
    @Test
    public void validateChannelFields() {
        when(channelRepository.getChannelFormByType(anyLong(), anyString())).thenReturn(channelFormsResponse);
        assertDoesNotThrow(() -> validationService.validateChannelFields(validSales, 1L));
    }
    
    @Test
    public void validateChannelFieldsWithInvalidFieldsTest() {
        when(channelRepository.getChannelFormByType(anyLong(), anyString())).thenReturn(channelFormsResponse);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
            validationService.validateChannelFields(invalidSales, 1L)
        );
        assertEquals(exception.getErrorCode(), ApiExternalErrorCode.INPUT_CHANNEL_NULL_VALUES.getErrorCode());
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    public void validateEmailTest() {
        when(channelRepository.getChannelFormByType(anyLong(), anyString())).thenReturn(channelFormsResponse);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                validationService.validateChannelFields(invalidSales, 1L)
        );
        assertEquals(exception.getErrorCode(), ApiExternalErrorCode.INPUT_CHANNEL_NULL_VALUES.getErrorCode());
        assertTrue(exception.getMessage().contains("Email"));
    }
}

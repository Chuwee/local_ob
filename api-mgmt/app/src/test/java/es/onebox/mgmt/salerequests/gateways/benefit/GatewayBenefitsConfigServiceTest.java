package es.onebox.mgmt.salerequests.gateways.benefit;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.BenefitType;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.GatewayBenefitConfiguration;
import es.onebox.mgmt.datasources.ms.payment.repositories.GatewayBenefitsConfigRepository;
import es.onebox.mgmt.exception.ApiMgmtCustomersErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.dto.ChannelSaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.EventSaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BenefitDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BinGroupDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigRequest;
import es.onebox.mgmt.salerequests.service.SaleRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GatewayBenefitsConfigServiceTest {

    @InjectMocks
    private GatewayBenefitsConfigService gatewayBenefitsConfigService;

    @Mock
    private SaleRequestService saleRequestService;

    @Mock
    private GatewayBenefitsConfigRepository gatewayBenefitsConfigRepository;

    @Mock
    private EntitiesRepository entitiesRepository;

    private final Long saleRequestId = 1L;
    private final Long channelId = 2L;
    private final Long eventId = 3L;
    private final String gatewaySid = "gateway123";
    private final String confSid = "conf456";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Entity defaultEntity = new Entity();
        defaultEntity.setId(666L);
        defaultEntity.setAllowGatewayBenefits(true);
        when(entitiesRepository.getCachedEntity(666L)).thenReturn(defaultEntity);
    }

    @Test
    void getGatewayBenefitsConfig() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        GatewayBenefitConfiguration configuration = createMockConfiguration();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.getGatewayBenefitConfiguration(anyString(), anyString(), anyLong(), anyLong()))
            .thenReturn(configuration);

        GatewayBenefitsConfigDTO result = gatewayBenefitsConfigService.getGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        assertEquals(eventId, result.getEventId());
        assertEquals(gatewaySid, result.getGatewaySid());
        assertEquals(confSid, result.getConfSid());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(eq(saleRequestId));
        verify(gatewayBenefitsConfigRepository, times(1))
            .getGatewayBenefitConfiguration( eq(gatewaySid), eq(confSid), eq(channelId), eq(eventId));
    }

    @Test
    void getGatewayBenefitsConfigThrowsExceptionWhenSaleRequestNotFound() {
        when(saleRequestService.getSaleRequestDetail(saleRequestId))
            .thenThrow(new RuntimeException("Sale request not found"));

        assertThrows(RuntimeException.class, () -> 
            gatewayBenefitsConfigService.getGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid));
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, never())
            .getGatewayBenefitConfiguration(anyString(), anyString(), anyLong(), anyLong());
    }

    @Test
    void createGatewayBenefitsConfigSuccess() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        GatewayBenefitsConfigRequest request = createValidRequest();
        GatewayBenefitConfiguration created = createMockConfiguration();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class)))
            .thenReturn(created);

        GatewayBenefitsConfigDTO result = gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        assertEquals(eventId, result.getEventId());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, times(1))
            .createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class));
    }

    @Test
    void createGatewayBenefitsConfigThrowsExceptionWhenCustomValidPeriodWithoutDates() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        GatewayBenefitsConfigRequest request = createRequestWithCustomValidPeriodButNoDates();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> 
            gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request));
        
        assertEquals(ApiMgmtCustomersErrorCode.VALID_PERIOD_IS_MANDATORY.getErrorCode(), exception.getErrorCode());
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, never()).createGatewayBenefitConfiguration(any());
    }

    @Test
    void createGatewayBenefitsConfigSuccessWithEmptyBenefits() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        GatewayBenefitsConfigRequest request = createRequestWithEmptyBenefits();
        GatewayBenefitConfiguration created = createMockConfiguration();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class)))
            .thenReturn(created);

        GatewayBenefitsConfigDTO result = gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);

        assertNotNull(result);
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, times(1))
            .createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class));
    }

    @Test
    void updateGatewayBenefitsConfigSuccess() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        GatewayBenefitsConfigRequest request = createValidRequest();
        GatewayBenefitConfiguration updated = createMockConfiguration();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.updateGatewayBenefitConfiguration(anyString(), anyString(), anyLong(), anyLong(), any(GatewayBenefitConfiguration.class)))
            .thenReturn(updated);

        GatewayBenefitsConfigDTO result = gatewayBenefitsConfigService.updateGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        assertEquals(eventId, result.getEventId());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(eq(saleRequestId));
        verify(gatewayBenefitsConfigRepository, times(1))
            .updateGatewayBenefitConfiguration(eq(gatewaySid), eq(confSid), eq(channelId), eq(eventId), any(GatewayBenefitConfiguration.class));
    }

    @Test
    void deleteGatewayBenefitsConfigSuccess() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        doNothing().when(gatewayBenefitsConfigRepository)
            .deleteGatewayBenefitConfiguration(anyString(), anyString(), anyLong(), anyLong());

        gatewayBenefitsConfigService.deleteGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid);

        verify(saleRequestService, times(1)).getSaleRequestDetail(eq(saleRequestId));
        verify(gatewayBenefitsConfigRepository, times(1))
            .deleteGatewayBenefitConfiguration(eq(gatewaySid), eq(confSid), eq(channelId), eq(eventId));
    }

    @Test
    void deleteGatewayBenefitsConfigThrowsExceptionWhenSaleRequestNotFound() {
        when(saleRequestService.getSaleRequestDetail(saleRequestId))
            .thenThrow(new RuntimeException("Sale request not found"));

        assertThrows(RuntimeException.class, () -> 
            gatewayBenefitsConfigService.deleteGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid));
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, never())
            .deleteGatewayBenefitConfiguration(anyString(), anyString(), anyLong(), anyLong());
    }

    @Test
    void getListGatewayBenefitsConfigsSuccess() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        List<GatewayBenefitConfiguration> configurations = Arrays.asList(
            createMockConfiguration(),
            createMockConfigurationWithDifferentGateway()
        );
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.getListGatewayBenefitConfigurations(anyLong(), anyLong()))
            .thenReturn(configurations);

        List<GatewayBenefitsConfigDTO> result = gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(channelId, result.get(0).getChannelId());
        assertEquals(eventId, result.get(0).getEventId());
        assertEquals(gatewaySid, result.get(0).getGatewaySid());
        assertEquals(confSid, result.get(0).getConfSid());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(eq(saleRequestId));
        verify(gatewayBenefitsConfigRepository, times(1))
            .getListGatewayBenefitConfigurations(eq(channelId), eq(eventId));
    }

    private GatewayBenefitConfiguration createMockConfigurationWithDifferentGateway() {
        GatewayBenefitConfiguration configuration = new GatewayBenefitConfiguration();
        configuration.setChannelId(channelId);
        configuration.setEventId(eventId);
        configuration.setGatewaySid("gateway456");
        configuration.setConfSid("conf789");
        return configuration;
    }

    @Test
    void getListGatewayBenefitsConfigsSuccessWithEmptyList() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        List<GatewayBenefitConfiguration> configurations = Collections.emptyList();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.getListGatewayBenefitConfigurations(anyLong(), anyLong()))
            .thenReturn(configurations);

        List<GatewayBenefitsConfigDTO> result = gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId);

        assertNotNull(result);
        assertEquals(0, result.size());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(eq(saleRequestId));
        verify(gatewayBenefitsConfigRepository, times(1))
            .getListGatewayBenefitConfigurations(eq(channelId), eq(eventId));
    }

    @Test
    void getListGatewayBenefitsConfigsThrowsExceptionWhenSaleRequestNotFound() {
        when(saleRequestService.getSaleRequestDetail(saleRequestId))
            .thenThrow(new RuntimeException("Sale request not found"));

        assertThrows(RuntimeException.class, () -> 
            gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId));
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, never())
            .getListGatewayBenefitConfigurations(anyLong(), anyLong());
    }

    private SaleRequestDetailDTO createSaleRequestDetail() {
        IdNameDTO entityDetail = new IdNameDTO();
        entityDetail.setId(666L);
        
        ChannelSaleRequestDetailDTO channelDetail = new ChannelSaleRequestDetailDTO();
        channelDetail.setId(channelId);
        channelDetail.setEntity(entityDetail);
        
        EventSaleRequestDetailDTO eventDetail = new EventSaleRequestDetailDTO();
        eventDetail.setId(eventId);
        
        SaleRequestDetailDTO saleRequestDetail = new SaleRequestDetailDTO();
        saleRequestDetail.setChannel(channelDetail);
        saleRequestDetail.setEvent(eventDetail);
        
        return saleRequestDetail;
    }

    private GatewayBenefitConfiguration createMockConfiguration() {
        GatewayBenefitConfiguration configuration = new GatewayBenefitConfiguration();
        configuration.setChannelId(channelId);
        configuration.setEventId(eventId);
        configuration.setGatewaySid(gatewaySid);
        configuration.setConfSid(confSid);
        return configuration;
    }

    private GatewayBenefitsConfigRequest createValidRequest() {
        GatewayBenefitsConfigRequest request = new GatewayBenefitsConfigRequest();
        
        BenefitDTO benefit = new BenefitDTO();
        benefit.setType(BenefitType.PRESALE);
        
        BinGroupDTO binGroup = new BinGroupDTO();
        binGroup.setBins(Arrays.asList("123456", "654321"));
        binGroup.setCustomValidPeriod(false);
        
        benefit.setBinGroups(List.of(binGroup));
        request.setBenefits(List.of(benefit));
        
        return request;
    }

    private GatewayBenefitsConfigRequest createRequestWithInstallmentsButNoOptions() {
        GatewayBenefitsConfigRequest request = new GatewayBenefitsConfigRequest();
        
        BenefitDTO benefit = new BenefitDTO();
        benefit.setType(BenefitType.INSTALLMENTS);
        
        BinGroupDTO binGroup = new BinGroupDTO();
        binGroup.setBins(List.of("123456"));
        binGroup.setCustomValidPeriod(false);
        
        benefit.setBinGroups(List.of(binGroup));
        request.setBenefits(List.of(benefit));
        
        return request;
    }

    private GatewayBenefitsConfigRequest createRequestWithCustomValidPeriodButNoDates() {
        GatewayBenefitsConfigRequest request = new GatewayBenefitsConfigRequest();
        
        BenefitDTO benefit = new BenefitDTO();
        benefit.setType(BenefitType.PRESALE);
        
        BinGroupDTO binGroup = new BinGroupDTO();
        binGroup.setBins(List.of("123456"));
        binGroup.setCustomValidPeriod(true);
        
        benefit.setBinGroups(List.of(binGroup));
        request.setBenefits(List.of(benefit));
        
        return request;
    }

    private GatewayBenefitsConfigRequest createRequestWithEmptyBenefits() {
        GatewayBenefitsConfigRequest request = new GatewayBenefitsConfigRequest();
        request.setBenefits(Collections.emptyList());
        return request;
    }

    @Test
    void createGatewayBenefitsConfigThrowsExceptionWhenGatewayBenefitsNotAllowedByEntity() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetailWithEntityBenefits(false);
        GatewayBenefitsConfigRequest request = createValidRequest();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> 
            gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request));
        
        assertEquals(ApiMgmtErrorCode.GATEWAY_BENEFITS_NOT_ALLOWED_BY_ENTITY.getErrorCode(), exception.getErrorCode());
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, never()).createGatewayBenefitConfiguration(any());
    }

    @Test
    void updateGatewayBenefitsConfigThrowsExceptionWhenGatewayBenefitsNotAllowedByEntity() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetailWithEntityBenefits(false);
        GatewayBenefitsConfigRequest request = createValidRequest();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> 
            gatewayBenefitsConfigService.updateGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request));
        
        assertEquals(ApiMgmtErrorCode.GATEWAY_BENEFITS_NOT_ALLOWED_BY_ENTITY.getErrorCode(), exception.getErrorCode());
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, never()).updateGatewayBenefitConfiguration(anyString(), anyString(), anyLong(), anyLong(), any());
    }

    @Test
    void createGatewayBenefitsConfigSuccessWhenGatewayBenefitsAllowedByEntity() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetailWithEntityBenefits(true);
        GatewayBenefitsConfigRequest request = createValidRequest();
        GatewayBenefitConfiguration created = createMockConfiguration();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class)))
            .thenReturn(created);

        GatewayBenefitsConfigDTO result = gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        assertEquals(eventId, result.getEventId());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, times(1))
            .createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class));
    }

    private SaleRequestDetailDTO createSaleRequestDetailWithEntityBenefits(boolean allowed) {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetailWithEntity();
        
        Entity entity = new Entity();
        entity.setId(666L);
        entity.setAllowGatewayBenefits(allowed);
        
        when(entitiesRepository.getCachedEntity(666L)).thenReturn(entity);
        
        return saleRequestDetail;
    }
    
    private SaleRequestDetailDTO createSaleRequestDetailWithEntity() {
        IdNameDTO entityDetail = new IdNameDTO();
        entityDetail.setId(666L);
        
        ChannelSaleRequestDetailDTO channelDetail = new ChannelSaleRequestDetailDTO();
        channelDetail.setId(channelId);
        channelDetail.setEntity(entityDetail);
        
        EventSaleRequestDetailDTO eventDetail = new EventSaleRequestDetailDTO();
        eventDetail.setId(eventId);
        
        SaleRequestDetailDTO saleRequestDetail = new SaleRequestDetailDTO();
        saleRequestDetail.setChannel(channelDetail);
        saleRequestDetail.setEvent(eventDetail);
        
        return saleRequestDetail;
    }
    
    @Test
    void createGatewayBenefitsConfigSuccessWithValidBins() {
        SaleRequestDetailDTO saleRequestDetail = createSaleRequestDetail();
        GatewayBenefitsConfigRequest request = createRequestWithValidBins();
        GatewayBenefitConfiguration created = createMockConfiguration();
        
        when(saleRequestService.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequestDetail);
        when(gatewayBenefitsConfigRepository.createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class)))
            .thenReturn(created);

        GatewayBenefitsConfigDTO result = gatewayBenefitsConfigService.createGatewayBenefitsConfig(saleRequestId, gatewaySid, confSid, request);

        assertNotNull(result);
        assertEquals(channelId, result.getChannelId());
        assertEquals(eventId, result.getEventId());
        
        verify(saleRequestService, times(1)).getSaleRequestDetail(saleRequestId);
        verify(gatewayBenefitsConfigRepository, times(1))
            .createGatewayBenefitConfiguration(any(GatewayBenefitConfiguration.class));
    }

    private GatewayBenefitsConfigRequest createRequestWithInvalidBins() {
        GatewayBenefitsConfigRequest request = new GatewayBenefitsConfigRequest();
        
        BenefitDTO benefit = new BenefitDTO();
        benefit.setType(BenefitType.PRESALE);
        
        BinGroupDTO binGroup = new BinGroupDTO();
        binGroup.setBins(Arrays.asList("123", "4111113", "41111A"));
        binGroup.setCustomValidPeriod(false);
        
        benefit.setBinGroups(List.of(binGroup));
        request.setBenefits(List.of(benefit));
        
        return request;
    }

    private GatewayBenefitsConfigRequest createRequestWithValidBins() {
        GatewayBenefitsConfigRequest request = new GatewayBenefitsConfigRequest();
        
        BenefitDTO benefit = new BenefitDTO();
        benefit.setType(BenefitType.PRESALE);
        
        BinGroupDTO binGroup = new BinGroupDTO();
        binGroup.setBins(Arrays.asList("411111", "550000", "660000"));
        binGroup.setCustomValidPeriod(false);
        
        benefit.setBinGroups(List.of(binGroup));
        request.setBenefits(List.of(benefit));
        
        return request;
    }
}
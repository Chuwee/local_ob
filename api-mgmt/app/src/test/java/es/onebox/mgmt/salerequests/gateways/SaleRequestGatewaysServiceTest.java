package es.onebox.mgmt.salerequests.gateways;

import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsChannelSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsEntitySaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsEventSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfigFilter;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.salerequests.gateways.benefit.GatewayBenefitsConfigService;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BenefitDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import es.onebox.mgmt.salerequests.gateways.dto.GatewayConfigUpdateRequestDTO;
import es.onebox.mgmt.salerequests.gateways.dto.SaleRequestGatewayConfigDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SaleRequestGatewaysServiceTest {

    @InjectMocks
    private SaleRequestGatewaysService saleRequestGatewaysService;

    @Mock
    private SaleRequestsRepository saleRequestsRepository;

    @Mock
    private SecurityManager securityManager;

    @Mock
    private ApiPaymentDatasource apiPaymentDatasource;

    @Mock
    private GatewayBenefitsConfigService gatewayBenefitsConfigService;

    @Mock
    private EntitiesRepository entitiesRepository;

    private final Long saleRequestId = 1L;
    private final Long channelId = 2L;
    private final Long eventId = 3L;
    private final Long entityId = 4L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSaleRequestGatewayConfiguration_WithBenefits_Success() {
        MsSaleRequestDTO saleRequest = createMockSaleRequest();
        ChannelGatewayConfigFilter configFilter = null;
        List<ChannelGatewayConfig> gatewayConfigs = createMockGatewayConfigs(true);
        List<GatewayBenefitsConfigDTO> benefitsConfigs = createMockBenefitsConfigs();
        Entity entity = createMockEntity(true);

        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(apiPaymentDatasource.getChannelGatewayConfigFilter(channelId, "event_" + eventId)).thenReturn(configFilter);
        when(apiPaymentDatasource.getChannelGatewayConfigs(channelId)).thenReturn(gatewayConfigs);
        when(gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId)).thenReturn(benefitsConfigs);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);

        SaleRequestGatewayConfigDTO result = saleRequestGatewaysService.getSaleRequestGatewayConfiguration(saleRequestId);

        assertNotNull(result);
        assertFalse(result.getCustom());
        assertTrue(result.getBenefits());
        assertNotNull(result.getChannelGateways());
        assertEquals(2, result.getChannelGateways().size());

        var gatewayWithBenefits = result.getChannelGateways().stream()
                .filter(g -> "gateway123".equals(g.getGatewaySid()))
                .findFirst()
                .orElse(null);
        assertNotNull(gatewayWithBenefits);
        assertTrue(gatewayWithBenefits.getHasBenefits());
        assertTrue(gatewayWithBenefits.getAllowBenefits());

        var gatewayWithoutBenefits = result.getChannelGateways().stream()
                .filter(g -> "gateway456".equals(g.getGatewaySid()))
                .findFirst()
                .orElse(null);
        assertNotNull(gatewayWithoutBenefits);
        assertFalse(gatewayWithoutBenefits.getHasBenefits());
        assertTrue(gatewayWithoutBenefits.getAllowBenefits());

        verify(saleRequestsRepository).getSaleRequestDetail(saleRequestId);
        verify(apiPaymentDatasource).getChannelGatewayConfigFilter(channelId, "event_" + eventId);
        verify(apiPaymentDatasource).getChannelGatewayConfigs(channelId);
        verify(gatewayBenefitsConfigService).getListGatewayBenefitsConfigs(saleRequestId);
        verify(entitiesRepository).getCachedEntity(entityId);
    }

    @Test
    void getSaleRequestGatewayConfiguration_WithoutBenefits_Success() {
        MsSaleRequestDTO saleRequest = createMockSaleRequest();
        ChannelGatewayConfigFilter configFilter = null;
        List<ChannelGatewayConfig> gatewayConfigs = createMockGatewayConfigs(false);
        List<GatewayBenefitsConfigDTO> benefitsConfigs = Collections.emptyList();
        Entity entity = createMockEntity(false);

        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(apiPaymentDatasource.getChannelGatewayConfigFilter(channelId, "event_" + eventId)).thenReturn(configFilter);
        when(apiPaymentDatasource.getChannelGatewayConfigs(channelId)).thenReturn(gatewayConfigs);
        when(gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId)).thenReturn(benefitsConfigs);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);

        SaleRequestGatewayConfigDTO result = saleRequestGatewaysService.getSaleRequestGatewayConfiguration(saleRequestId);

        assertNotNull(result);
        assertFalse(result.getCustom());
        assertFalse(result.getBenefits());
        assertNotNull(result.getChannelGateways());

        result.getChannelGateways().forEach(gateway -> {
            assertFalse(gateway.getHasBenefits());
            assertFalse(gateway.getAllowBenefits());
        });

        verify(saleRequestsRepository).getSaleRequestDetail(saleRequestId);
        verify(entitiesRepository).getCachedEntity(entityId);
    }

    @Test
    void getSaleRequestGatewayConfiguration_WithCustomFilter_Success() {
        MsSaleRequestDTO saleRequest = createMockSaleRequest();
        ChannelGatewayConfigFilter configFilter = createMockConfigFilter();
        List<ChannelGatewayConfig> gatewayConfigs = createMockGatewayConfigs(false);
        List<GatewayBenefitsConfigDTO> benefitsConfigs = createMockBenefitsConfigs();
        Entity entity = createMockEntity(true);

        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(apiPaymentDatasource.getChannelGatewayConfigFilter(channelId, "event_" + eventId)).thenReturn(configFilter);
        when(apiPaymentDatasource.getChannelGatewayConfigs(channelId)).thenReturn(gatewayConfigs);
        when(gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId)).thenReturn(benefitsConfigs);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);

        SaleRequestGatewayConfigDTO result = saleRequestGatewaysService.getSaleRequestGatewayConfiguration(saleRequestId);

        assertNotNull(result);
        assertTrue(result.getCustom());
        assertTrue(result.getBenefits());

        verify(saleRequestsRepository).getSaleRequestDetail(saleRequestId);
        verify(apiPaymentDatasource).getChannelGatewayConfigFilter(channelId, "event_" + eventId);
        verify(apiPaymentDatasource).getChannelGatewayConfigs(channelId);
        verify(gatewayBenefitsConfigService).getListGatewayBenefitsConfigs(saleRequestId);
        verify(entitiesRepository).getCachedEntity(entityId);
    }

    @Test
    void getSaleRequestGatewayConfiguration_FlagsBenefitsConfigs_Success() {
        MsSaleRequestDTO saleRequest = createMockSaleRequest();
        ChannelGatewayConfigFilter configFilter = null;
        List<ChannelGatewayConfig> gatewayConfigs = createMockGatewayConfigs(true);
        List<GatewayBenefitsConfigDTO> benefitsConfigs = null;
        Entity entity = createMockEntity(false);

        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(apiPaymentDatasource.getChannelGatewayConfigFilter(channelId, "event_" + eventId)).thenReturn(configFilter);
        when(apiPaymentDatasource.getChannelGatewayConfigs(channelId)).thenReturn(gatewayConfigs);
        when(gatewayBenefitsConfigService.getListGatewayBenefitsConfigs(saleRequestId)).thenReturn(benefitsConfigs);
        when(entitiesRepository.getCachedEntity(entityId)).thenReturn(entity);

        SaleRequestGatewayConfigDTO result = saleRequestGatewaysService.getSaleRequestGatewayConfiguration(saleRequestId);

        assertNotNull(result);
        assertFalse(result.getBenefits());
        result.getChannelGateways().forEach(gateway -> {
            assertFalse(gateway.getHasBenefits());
            assertTrue(gateway.getAllowBenefits());
        });
    }

    @Test
    void updateSaleRequestGatewayConfiguration_CustomTrue_Success() {
        MsSaleRequestDTO saleRequest = createMockSaleRequest();
        GatewayConfigUpdateRequestDTO request = createMockUpdateRequest(true);
        List<ChannelGatewayConfig> gatewayConfigs = createMockGatewayConfigs(false);

        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(apiPaymentDatasource.getChannelGatewayConfigs(channelId)).thenReturn(gatewayConfigs);
        doNothing().when(apiPaymentDatasource).updateChannelGatewayConfigFilter(eq(channelId), eq("event_" + eventId), any());

        saleRequestGatewaysService.updateSaleRequestGatewayConfiguration(saleRequestId, request);

        verify(saleRequestsRepository).getSaleRequestDetail(saleRequestId);
        verify(apiPaymentDatasource).getChannelGatewayConfigs(channelId);
        verify(apiPaymentDatasource).updateChannelGatewayConfigFilter(eq(channelId), eq("event_" + eventId), any());
        verify(apiPaymentDatasource, never()).deleteChannelGatewayConfigFilter(anyLong(), anyString());
    }

    @Test
    void updateSaleRequestGatewayConfiguration_CustomFalse_Success() {
        MsSaleRequestDTO saleRequest = createMockSaleRequest();
        GatewayConfigUpdateRequestDTO request = createMockUpdateRequest(false);
        List<ChannelGatewayConfig> gatewayConfigs = createMockGatewayConfigs(false);

        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(apiPaymentDatasource.getChannelGatewayConfigs(channelId)).thenReturn(gatewayConfigs);
        doNothing().when(apiPaymentDatasource).deleteChannelGatewayConfigFilter(channelId, "event_" + eventId);

        saleRequestGatewaysService.updateSaleRequestGatewayConfiguration(saleRequestId, request);

        verify(saleRequestsRepository).getSaleRequestDetail(saleRequestId);
        verify(apiPaymentDatasource).getChannelGatewayConfigs(channelId);
        verify(apiPaymentDatasource).deleteChannelGatewayConfigFilter(channelId, "event_" + eventId);
        verify(apiPaymentDatasource, never()).updateChannelGatewayConfigFilter(anyLong(), anyString(), any());
    }

    private MsSaleRequestDTO createMockSaleRequest() {
        MsChannelSaleRequestDTO channel = new MsChannelSaleRequestDTO();
        channel.setId(channelId);
        MsEntitySaleRequestDTO entity = new MsEntitySaleRequestDTO();
        entity.setId(entityId);
        channel.setEntity(entity);

        MsEventSaleRequestDTO event = new MsEventSaleRequestDTO();
        event.setId(eventId);

        MsSaleRequestDTO saleRequest = new MsSaleRequestDTO();
        saleRequest.setChannel(channel);
        saleRequest.setEvent(event);
        return saleRequest;
    }

    private List<ChannelGatewayConfig> createMockGatewayConfigs(Boolean benefits) {
        ChannelGatewayConfig gateway1 = new ChannelGatewayConfig();
        gateway1.setGatewaySid("gateway123");
        gateway1.setConfSid("conf123");
        gateway1.setInternalName("PayPal");
        gateway1.setDescription("PayPal Gateway");
        gateway1.setAllowBenefits(benefits);

        ChannelGatewayConfig gateway2 = new ChannelGatewayConfig();
        gateway2.setGatewaySid("gateway456");
        gateway2.setConfSid("conf456");
        gateway2.setInternalName("Stripe");
        gateway2.setDescription("Stripe Gateway");
        gateway2.setAllowBenefits(benefits);

        return Arrays.asList(gateway1, gateway2);
    }

    private List<GatewayBenefitsConfigDTO> createMockBenefitsConfigs() {
        GatewayBenefitsConfigDTO benefitsConfig = new GatewayBenefitsConfigDTO();
        benefitsConfig.setChannelId(channelId);
        benefitsConfig.setEventId(eventId);
        benefitsConfig.setGatewaySid("gateway123");
        benefitsConfig.setConfSid("conf123");
        
        BenefitDTO benefit = new BenefitDTO();
        benefitsConfig.setBenefits(List.of(benefit));

        return List.of(benefitsConfig);
    }

    private Entity createMockEntity(boolean allowGatewayBenefits) {
        Entity entity = new Entity();
        entity.setAllowGatewayBenefits(allowGatewayBenefits);
        return entity;
    }

    private ChannelGatewayConfigFilter createMockConfigFilter() {
        ChannelGatewayConfigFilter filter = new ChannelGatewayConfigFilter();
        filter.setId("filter123");
        filter.setIdChannel(channelId.intValue());
        return filter;
    }

    private GatewayConfigUpdateRequestDTO createMockUpdateRequest(boolean custom) {
        GatewayConfigUpdateRequestDTO request = new GatewayConfigUpdateRequestDTO();
        request.setCustom(custom);
        
        if (custom) {
            request.setChannelGateways(createMockChannelGateways());
        }
        
        return request;
    }
    
    private List<es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO> createMockChannelGateways() {
        List<es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO> channelGateways = new java.util.ArrayList<>();
        
        es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO gateway1 = new es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO();
        gateway1.setGatewaySid("gateway123");
        gateway1.setConfigurationSid("conf123");
        gateway1.setActive(true);
        gateway1.setDefaultGateway(true);
        
        es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO gateway2 = new es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO();
        gateway2.setGatewaySid("gateway456");
        gateway2.setConfigurationSid("conf456");
        gateway2.setActive(false);
        gateway2.setDefaultGateway(false);
        
        channelGateways.add(gateway1);
        channelGateways.add(gateway2);
        
        return channelGateways;
    }
} 
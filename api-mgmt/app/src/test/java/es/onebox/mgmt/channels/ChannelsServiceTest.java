package es.onebox.mgmt.channels;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.converter.ChannelFilterConverter;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.dto.ChannelLimitsDTO;
import es.onebox.mgmt.channels.dto.ChannelLimitsTicketsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelPromotionsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelVenuemapDTO;
import es.onebox.mgmt.channels.dto.ChannelsFilter;
import es.onebox.mgmt.channels.dto.ChannelsResponseDTO;
import es.onebox.mgmt.channels.promotions.service.ChannelPromotionsService;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurancePoliciesRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class ChannelsServiceTest {

    @InjectMocks
    private ChannelsService channelsService;

    @Mock
    private SecurityManager securityManager;
    
    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private MasterdataService masterdataService;
    
    @Mock
    private ChannelPromotionsService channelPromotionsService;
    
    @Mock
    private InsurancePoliciesRepository insurancePoliciesRepository;
    
    @Mock
    private ChannelsHelper channelsHelper;
    
    @Mock
    private AvetConfigRepository avetConfigRepository;
    
    @Mock
    private EntitiesRepository entitiesRepository;
    
    @Mock
    private ApiPaymentDatasource apiPaymentDatasource;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getChannelsWithSuperOperatorEntity() {

        ChannelsFilter filter = new ChannelsFilter();
        ChannelFilter channelFilter = new ChannelFilter();
        ChannelsResponse channelsResponse = new ChannelsResponse();
        ChannelsResponseDTO channelsResponseDTO = new ChannelsResponseDTO();

        try(
                MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class);
                MockedStatic<ChannelFilterConverter> channelFilterConverterMock = mockStatic(ChannelFilterConverter.class);
                MockedStatic<ChannelConverter> channelConverterMock = mockStatic(ChannelConverter.class)
        ) {
            doNothing().when(securityManager).checkEntityAccessible(filter);

            securityUtilsMock.when(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR))
                    .thenReturn(true);

            channelFilterConverterMock.when(() -> ChannelFilterConverter.convert(filter, null))
                    .thenReturn(channelFilter);

            when(channelsRepository.getChannels(null, channelFilter))
                    .thenReturn(channelsResponse);

            channelConverterMock.when(() -> ChannelConverter.fromMsChannelsResponse(any(ChannelsResponse.class), any(ChannelsHelper.class)))
                    .thenReturn(channelsResponseDTO);

            ChannelsResponseDTO response = channelsService.getChannels(filter);

            assertNotNull(response);
            assertEquals(channelsResponseDTO, response);

            securityUtilsMock.verify(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR));
            channelFilterConverterMock.verify(() -> ChannelFilterConverter.convert(filter, null));
            verify(channelsRepository, times(1)).getChannels(null, channelFilter);
            channelConverterMock.verify(() -> ChannelConverter.fromMsChannelsResponse(any(ChannelsResponse.class), any(ChannelsHelper.class)));

        }
    }

    @Test
    void updateChannelWhitelabelSettings_success() {
        Long channelId = 123L;

        ChannelWhitelabelSettingsDTO request = mock(ChannelWhitelabelSettingsDTO.class);
        ChannelWhitelabelPromotionsDTO promotions = mock(ChannelWhitelabelPromotionsDTO.class);
        ChannelWhitelabelVenuemapDTO venueMap = mock(ChannelWhitelabelVenuemapDTO.class);

        when(request.getPromotions()).thenReturn(promotions);
        when(promotions.getLocations()).thenReturn(null);

        when(request.getVenueMap()).thenReturn(venueMap);
        when(venueMap.getPreselectedItems()).thenReturn(5);

        ChannelDetailDTO channelDetailDTO = mock(ChannelDetailDTO.class);
        ChannelLimitsDTO limits = mock(ChannelLimitsDTO.class);
        ChannelLimitsTicketsDTO tickets = mock(ChannelLimitsTicketsDTO.class);

        when(channelsService.getChannel(channelId)).thenReturn(channelDetailDTO);
        when(channelDetailDTO.getLimits()).thenReturn(limits);
        when(limits.getTickets()).thenReturn(tickets);
        when(tickets.getPurchaseMax()).thenReturn(10);

        doNothing().when(channelsRepository).updateChannelWhitelabelSettings(eq(channelId), any());

        Assertions.assertDoesNotThrow(() -> channelsService.updateChannelWhitelabelSettings(channelId, request));

        verify(channelsRepository, times(1))
                .updateChannelWhitelabelSettings(eq(channelId), any());
    }

    @Test
    void updateChannelWhitelabelSettings_preselectedItemsExceedsLimit_throwsException() {
        Long channelId = 123L;

        ChannelWhitelabelSettingsDTO request = mock(ChannelWhitelabelSettingsDTO.class);
        ChannelWhitelabelPromotionsDTO promotions = mock(ChannelWhitelabelPromotionsDTO.class);
        ChannelWhitelabelVenuemapDTO venueMap = mock(ChannelWhitelabelVenuemapDTO.class);

        when(request.getPromotions()).thenReturn(promotions);
        when(promotions.getLocations()).thenReturn(null);

        when(request.getVenueMap()).thenReturn(venueMap);
        when(venueMap.getPreselectedItems()).thenReturn(15);

        ChannelDetailDTO channelDetailDTO = mock(ChannelDetailDTO.class);
        ChannelLimitsDTO limits = mock(ChannelLimitsDTO.class);
        ChannelLimitsTicketsDTO tickets = mock(ChannelLimitsTicketsDTO.class);

        when(channelsService.getChannel(channelId)).thenReturn(channelDetailDTO);
        when(channelDetailDTO.getLimits()).thenReturn(limits);
        when(limits.getTickets()).thenReturn(tickets);
        when(tickets.getPurchaseMax()).thenReturn(10);

        OneboxRestException ex = Assertions.assertThrows(OneboxRestException.class,
                () -> channelsService.updateChannelWhitelabelSettings(channelId, request));

        assertEquals(ApiMgmtChannelsErrorCode.PRESELECTED_ITEMS_EXCEEDS_CHANNEL_LIMIT.getErrorCode(), ex.getErrorCode());

        verify(channelsRepository, never()).updateChannelWhitelabelSettings(anyLong(), any());
    }
}
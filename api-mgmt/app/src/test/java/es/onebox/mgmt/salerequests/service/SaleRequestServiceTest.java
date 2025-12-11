package es.onebox.mgmt.salerequests.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsChannelSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsEntitySaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsEventSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestResponseDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.users.dto.UserSelfDTO;
import es.onebox.mgmt.users.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaleRequestServiceTest {

    @Mock
    private SaleRequestsRepository saleRequestsRepository;
    
    @Mock
    private SecurityManager securityManager;
    
    @Mock
    private UsersService usersService;
    
    @Mock
    private EventsRepository eventsRepository;
    
    @Mock
    private ChannelsRepository channelsRepository;
    
    @InjectMocks
    private SaleRequestService saleRequestService;

    private final Long saleRequestId = 1L;
    private final Long eventId = 300L;
    private final Long operatorId = 500L;
    private final Long channelId = 200L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        MsSaleRequestDTO saleRequest = new MsSaleRequestDTO();
        saleRequest.setStatus(MsSaleRequestsStatus.REJECTED);
        
        MsEventSaleRequestDTO event = new MsEventSaleRequestDTO();
        event.setId(eventId);
        saleRequest.setEvent(event);
        
        MsChannelSaleRequestDTO channel = new MsChannelSaleRequestDTO();
        channel.setId(channelId);
        MsEntitySaleRequestDTO entity = new MsEntitySaleRequestDTO();
        Long entityId = 600L;
        entity.setId(entityId);
        channel.setEntity(entity);
        saleRequest.setChannel(channel);

        UserSelfDTO userSelfDTO = new UserSelfDTO();
        Long userId = 400L;
        userSelfDTO.setId(userId);

        ChannelConfig channelConfig = new ChannelConfig();

        doNothing().when(securityManager).checkEntityAccessible(anyLong());
        when(saleRequestsRepository.getSaleRequestDetail(saleRequestId)).thenReturn(saleRequest);
        when(usersService.getAuthUser()).thenReturn(userSelfDTO);
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(channelConfig);
    }

    @Test
    void testUpdateSaleRequestWithDynamicPricesDisabled() {
        Session session = new Session();
        session.setUseDynamicPrices(false);
        
        Sessions sessions = new Sessions();
        sessions.setData(List.of(session));
        
        MsUpdateSaleRequestResponseDTO msResponse = new MsUpdateSaleRequestResponseDTO();
        msResponse.setStatus(MsSaleRequestsStatus.ACCEPTED);
        
        UpdateSaleRequestDTO updateDTO = new UpdateSaleRequestDTO();
        updateDTO.setStatus(SaleRequestsStatus.ACCEPTED);
        
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setV4Enabled(false);
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(channelConfig);
        
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getUserOperatorId).thenReturn(operatorId);
            when(eventsRepository.getSessions(eq(operatorId), eq(eventId), any(SessionSearchFilter.class)))
                .thenReturn(sessions);
            when(saleRequestsRepository.updateSaleRequestStatus(eq(saleRequestId), any(MsUpdateSaleRequestDTO.class)))
                .thenReturn(msResponse);

            UpdateSaleRequestResponseDTO result = saleRequestService.updateSaleRequestStatus(saleRequestId, updateDTO);

            assertNotNull(result);
            assertEquals(SaleRequestsStatus.ACCEPTED, result.getStatus());

            verify(securityManager).checkEntityAccessible(anyLong());
            verify(saleRequestsRepository, times(2)).getSaleRequestDetail(saleRequestId);
            verify(usersService).getAuthUser();
            verify(saleRequestsRepository).updateSaleRequestStatus(eq(saleRequestId), any(MsUpdateSaleRequestDTO.class));
            verify(eventsRepository).getSessions(eq(operatorId), eq(eventId), any(SessionSearchFilter.class));
            verify(channelsRepository).getChannelConfig(channelId);
            securityUtilsMock.verify(SecurityUtils::getUserOperatorId);
        }
    }
    
    @Test
    void testUpdateSaleRequestWithDynamicPricesEnabledButV4Disabled() {
        Session session = new Session();
        session.setUseDynamicPrices(true);
        
        Sessions sessions = new Sessions();
        sessions.setData(List.of(session));
        
        UpdateSaleRequestDTO updateDTO = new UpdateSaleRequestDTO();
        updateDTO.setStatus(SaleRequestsStatus.ACCEPTED);
        
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setV4Enabled(false);
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(channelConfig);
        
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getUserOperatorId).thenReturn(operatorId);
            when(eventsRepository.getSessions(eq(operatorId), eq(eventId), any(SessionSearchFilter.class)))
                .thenReturn(sessions);

            OneboxRestException exception = assertThrows(OneboxRestException.class, 
                () -> saleRequestService.updateSaleRequestStatus(saleRequestId, updateDTO));

            assertEquals(ApiMgmtErrorCode.DYNAMIC_PRICES_REQUIRE_V4_CHANNEL.getErrorCode(), exception.getErrorCode());

            verify(securityManager).checkEntityAccessible(anyLong());
            verify(saleRequestsRepository, times(2)).getSaleRequestDetail(saleRequestId);
            verify(eventsRepository).getSessions(eq(operatorId), eq(eventId), any(SessionSearchFilter.class));
            verify(saleRequestsRepository, never()).updateSaleRequestStatus(anyLong(), any());
            verify(channelsRepository).getChannelConfig(channelId);
            securityUtilsMock.verify(SecurityUtils::getUserOperatorId);
        }
    }
    
    @Test
    void testUpdateSaleRequestWithDynamicPricesEnabledAndV4Enabled() {
        Session session = new Session();
        session.setUseDynamicPrices(true);
        
        Sessions sessions = new Sessions();
        sessions.setData(List.of(session));
        
        MsUpdateSaleRequestResponseDTO msResponse = new MsUpdateSaleRequestResponseDTO();
        msResponse.setStatus(MsSaleRequestsStatus.ACCEPTED);
        
        UpdateSaleRequestDTO updateDTO = new UpdateSaleRequestDTO();
        updateDTO.setStatus(SaleRequestsStatus.ACCEPTED);
        
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setV4Enabled(true);
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(channelConfig);
        
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getUserOperatorId).thenReturn(operatorId);
            when(eventsRepository.getSessions(eq(operatorId), eq(eventId), any(SessionSearchFilter.class)))
                .thenReturn(sessions);
            when(saleRequestsRepository.updateSaleRequestStatus(eq(saleRequestId), any(MsUpdateSaleRequestDTO.class)))
                .thenReturn(msResponse);

            UpdateSaleRequestResponseDTO result = saleRequestService.updateSaleRequestStatus(saleRequestId, updateDTO);

            assertNotNull(result);
            assertEquals(SaleRequestsStatus.ACCEPTED, result.getStatus());

            verify(securityManager).checkEntityAccessible(anyLong());
            verify(saleRequestsRepository, times(2)).getSaleRequestDetail(saleRequestId);
            verify(usersService).getAuthUser();
            verify(saleRequestsRepository).updateSaleRequestStatus(eq(saleRequestId), any(MsUpdateSaleRequestDTO.class));
            verify(eventsRepository).getSessions(eq(operatorId), eq(eventId), any(SessionSearchFilter.class));
            verify(channelsRepository).getChannelConfig(channelId);
            securityUtilsMock.verify(SecurityUtils::getUserOperatorId);
        }
    }
}

package es.onebox.internal.sgtm.service;

import es.onebox.common.access.AccessService;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.notification.dto.NotificationConfigDTO;
import es.onebox.common.datasources.ms.notification.repository.MsNotificationRepository;
import es.onebox.internal.sgtm.datasource.SgtmDatasource;
import es.onebox.internal.sgtm.dto.ChannelExternalToolDTO;
import es.onebox.internal.sgtm.dto.SgtmMessageDTO;
import es.onebox.internal.sgtm.dto.SgtmWebhookRequestDTO;
import es.onebox.internal.sgtm.enums.ChannelExternalToolsNamesDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SgtmServiceTest {
    @Mock
    private SgtmDatasource sgtmDatasource;
    @Mock
    private AccessService accessService;
    @Mock
    private MsNotificationRepository msNotificationRepository;
    @Mock
    private EntitiesRepository entitiesRepository;
    @InjectMocks
    @Spy
    private SgtmService sgtmService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockHeaders(String code, String hookId, String signature, String event, String action, String deliveryId, String xGtmServerPreview) {
        when(httpServletRequest.getHeader(eq("ob-action"))).thenReturn(action);
        when(httpServletRequest.getHeader(eq("ob-event"))).thenReturn(event);
        when(httpServletRequest.getHeader(eq("ob-delivery-id"))).thenReturn(deliveryId);
        when(httpServletRequest.getHeader(eq("ob-hook-id"))).thenReturn(hookId);
        when(httpServletRequest.getHeader(eq("ob-signature"))).thenReturn(signature);
        when(httpServletRequest.getHeader(eq("x-gtm-server-preview"))).thenReturn(xGtmServerPreview);
    }

    @Test
    void processWebhook_success() throws Exception {
        String code = "ORDER123";
        String hookId = "HOOKID";
        String apiKey = "APIKEY";
        String bodyContent = "{\"order\":\"data\"}";
        String signature = es.onebox.common.utils.GeneratorUtils.getHashSHA256(bodyContent + apiKey);
        String event = "event";
        String action = "action";
        String deliveryId = "delivery";
        String xGtmServerPreview = "preview";

        SgtmWebhookRequestDTO request = mock(SgtmWebhookRequestDTO.class);
        when(request.getCode()).thenReturn(code);
        mockHeaders(code, hookId, signature, event, action, deliveryId, xGtmServerPreview);
        when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader(bodyContent)));

        NotificationConfigDTO config = mock(NotificationConfigDTO.class);
        when(config.getApiKey()).thenReturn(apiKey);
        when(config.getEntityId()).thenReturn(1L);
        when(msNotificationRepository.getNotificationConfig(hookId)).thenReturn(config);
        when(accessService.getAccessToken(1L)).thenReturn("token");
        HashMap<String, Object> orderDetail = new HashMap<>();
        when(sgtmDatasource.getOrderDetail(code, "token")).thenReturn(orderDetail);

        doNothing().when(sgtmDatasource).sendToSgtm(any(SgtmMessageDTO.class), eq(xGtmServerPreview));

        assertDoesNotThrow(() -> sgtmService.processWebhook(request, httpServletRequest));
        verify(sgtmDatasource, times(1)).sendToSgtm(any(SgtmMessageDTO.class), eq(xGtmServerPreview));
    }

    @Test
    void processWebhook_missingOrderCode_shouldThrow() throws Exception {
        SgtmWebhookRequestDTO request = mock(SgtmWebhookRequestDTO.class);
        when(request.getCode()).thenReturn("");
        mockHeaders("", "hookId", "sig", "event", "action", "delivery", "preview");
        when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{}")));

        Exception ex = assertThrows(Exception.class, () -> sgtmService.processWebhook(request, httpServletRequest));
        assertTrue(ex.getMessage().contains("BAD_REQUEST_PARAMETER") || ex.getClass().getSimpleName().contains("OneboxRestException"));
    }

    @Test
    void processWebhook_missingConfig_shouldThrow() throws Exception {
        String code = "ORDER123";
        String hookId = "HOOKID";
        SgtmWebhookRequestDTO request = mock(SgtmWebhookRequestDTO.class);
        when(request.getCode()).thenReturn(code);
        mockHeaders(code, hookId, "sig", "event", "action", "delivery", "preview");
        when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{}")));
        when(msNotificationRepository.getNotificationConfig(hookId)).thenReturn(null);

        Exception ex = assertThrows(Exception.class, () -> sgtmService.processWebhook(request, httpServletRequest));
        assertTrue(ex.getMessage().contains("WEBHOOK_CONFIG_NOT_FOUND") || ex.getClass().getSimpleName().contains("OneboxRestException"));
    }

    @Test
    void processWebhook_invalidSignature_shouldThrow() throws Exception {
        String code = "ORDER123";
        String hookId = "HOOKID";
        String apiKey = "APIKEY";
        String bodyContent = "{\"order\":\"data\"}";
        String invalidSignature = "invalidsig";
        SgtmWebhookRequestDTO request = mock(SgtmWebhookRequestDTO.class);
        when(request.getCode()).thenReturn(code);
        mockHeaders(code, hookId, invalidSignature, "event", "action", "delivery", "preview");
        when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader(bodyContent)));
        NotificationConfigDTO config = mock(NotificationConfigDTO.class);
        when(config.getApiKey()).thenReturn(apiKey);
        when(config.getEntityId()).thenReturn(1L);
        when(msNotificationRepository.getNotificationConfig(hookId)).thenReturn(config);

        Exception ex = assertThrows(Exception.class, () -> sgtmService.processWebhook(request, httpServletRequest));
        assertTrue(ex.getMessage().contains("INVALID_WEBHOOK_SIGNATURE") || ex.getClass().getSimpleName().contains("OneboxRestException"));
    }

    @Test
    void processWebhook_sendToSgtmThrows_shouldPropagate() throws Exception {
        String code = "ORDER123";
        String hookId = "HOOKID";
        String apiKey = "APIKEY";
        String bodyContent = "{\"order\":\"data\"}";
        String signature = es.onebox.common.utils.GeneratorUtils.getHashSHA256(bodyContent + apiKey);
        SgtmWebhookRequestDTO request = mock(SgtmWebhookRequestDTO.class);
        when(request.getCode()).thenReturn(code);
        mockHeaders(code, hookId, signature, "event", "action", "delivery", "preview");
        when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader(bodyContent)));
        NotificationConfigDTO config = mock(NotificationConfigDTO.class);
        when(config.getApiKey()).thenReturn(apiKey);
        when(config.getEntityId()).thenReturn(1L);
        when(msNotificationRepository.getNotificationConfig(hookId)).thenReturn(config);
        when(accessService.getAccessToken(1L)).thenReturn("token");
        HashMap<String, Object> orderDetail = new HashMap<>();
        when(sgtmDatasource.getOrderDetail(code, "token")).thenReturn(orderDetail);
        doThrow(new RuntimeException("SGTM error")).when(sgtmDatasource).sendToSgtm(any(SgtmMessageDTO.class), anyString());

        Exception ex = assertThrows(RuntimeException.class, () -> sgtmService.processWebhook(request, httpServletRequest));
        assertTrue(ex.getMessage().contains("SGTM error"));
    }

    @Test
    void processWebhook_sendToSgtmMetaWebhook() throws Exception {

        EntityDTO entityDTO = new EntityDTO();
        entityDTO.setOperator(new EntityDTO());
        entityDTO.getOperator().setId(1L);
        when(httpServletRequest.getHeader(anyString())).thenReturn(null);
        when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("fake-body")));
        when(entitiesRepository.getByIdCached(any(Long.class))).thenReturn(entityDTO);

        Long entityId = 6332L;
        String code = "XVRAD16Y6A7X";

        SgtmWebhookRequestDTO request = new SgtmWebhookRequestDTO();
        request.setEntityId(entityId);
        request.setCode(code);

        List<ChannelExternalToolDTO> activeExternalTools = new ArrayList<>();
        ChannelExternalToolDTO externalTool = new ChannelExternalToolDTO();
        externalTool.setEnabled(true);
        externalTool.setName(ChannelExternalToolsNamesDTO.META_PIXEL);
        activeExternalTools.add(externalTool);
        request.setActiveExternalTools(activeExternalTools);

        List<Long> channelIds = List.of(28189L);

        doNothing().when(sgtmDatasource).sendToSgtm(any(SgtmMessageDTO.class), any());

        try {
            sgtmService.processWebhook(request, httpServletRequest, channelIds);
        } catch (SkipProcessingException ignored) {
        }

        verify(sgtmDatasource, atMostOnce()).sendToSgtm(any(SgtmMessageDTO.class), any());
    }


}


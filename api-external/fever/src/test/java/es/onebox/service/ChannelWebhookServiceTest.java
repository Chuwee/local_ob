package es.onebox.service;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelEventDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormField;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsFilter;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsResponseDTO;
import es.onebox.common.datasources.ms.channel.enums.ChannelEventStatus;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.channel.repository.ChannelEventRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.channel.dto.MsEventSaleRequestDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.common.datasources.webhook.dto.fever.AllowedEntitiesFileData;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.SaleRequestStatus;
import es.onebox.common.datasources.webhook.dto.fever.SaleRequestStatusDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.fever.service.ChannelWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ChannelWebhookServiceTest {

    private static final Long CHANNEL_ID = 1L;
    private static final Long ENTITY_ID = 100L;
    private static final Long ALLOWED_ENTITY_ID = 200L;
    private static final Long EVENT_ID = 1000L;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelEventRepository channelEventRepository;
    @Mock
    private MsEventRepository eventRepository;

    @InjectMocks
    private ChannelWebhookService channelWebhookService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendChannelFormUpdateDetail_allowedSameEntity_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(ENTITY_ID, WhitelabelType.INTERNAL);
        ChannelFormsResponse form = generateChannelFormsResponse();
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);
        when(channelRepository.getChannelFormByType(CHANNEL_ID, "default")).thenReturn(form);

        WebhookFeverDTO result = channelWebhookService.sendChannelFormUpdateDetail(webhookFever);

        Assertions.assertTrue(result.getAllowSend());
        Assertions.assertEquals(CHANNEL_ID, result.getFeverMessage().getChannelId());
        Assertions.assertNotNull(result.getFeverMessage().getChannelFormDetailDTO());
    }

    @Test
    public void sendChannelFormUpdateDetail_allowedExternalEntity_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(ALLOWED_ENTITY_ID, WhitelabelType.EXTERNAL);
        ChannelFormsResponse form = generateChannelFormsResponse();
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);
        when(channelRepository.getChannelFormByType(CHANNEL_ID, "default")).thenReturn(form);

        WebhookFeverDTO result = channelWebhookService.sendChannelFormUpdateDetail(webhookFever);

        Assertions.assertTrue(result.getAllowSend());
        Assertions.assertEquals(CHANNEL_ID, result.getFeverMessage().getChannelId());
        Assertions.assertNotNull(result.getFeverMessage().getChannelFormDetailDTO());
    }

    @Test
    public void sendChannelFormUpdateDetail_notAllowedEntity_returnNotAllowed() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(999L, WhitelabelType.INTERNAL);
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);

        WebhookFeverDTO result = channelWebhookService.sendChannelFormUpdateDetail(webhookFever);

        Assertions.assertFalse(result.getAllowSend());
    }

    @Test
    public void sendChannelFormUpdateDetail_notAllowedExternalEntity_returnNotAllowed() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(999L, WhitelabelType.EXTERNAL);
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);

        WebhookFeverDTO result = channelWebhookService.sendChannelFormUpdateDetail(webhookFever);

        Assertions.assertFalse(result.getAllowSend());
    }

    @Test
    public void sendChannelFormUpdateDetail_notAllowed_skipProcessing() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(999L, WhitelabelType.INTERNAL);
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);

        WebhookFeverDTO result = channelWebhookService.sendChannelFormUpdateDetail(webhookFever);

        Assertions.assertFalse(result.getAllowSend());
        Assertions.assertNull(result.getFeverMessage().getChannelId());
        Assertions.assertNull(result.getFeverMessage().getChannelFormDetailDTO());
    }

    @Test
    public void sendChannelSaleRequestUpdateDetail_allowedEntity_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        webhookFever.getNotificationMessage().setEventId(EVENT_ID);
        ChannelDTO channel = generateChannelDTO(ENTITY_ID, WhitelabelType.INTERNAL);
        ChannelEventDTO channelEvent = generateChannelEventDTO();
        es.onebox.common.datasources.ms.event.dto.ChannelEventDTO msChannelEvent = generateMsChannelEvent();

        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);
        when(channelEventRepository.getChannelEventRelationship(CHANNEL_ID, EVENT_ID)).thenReturn(channelEvent);
        when(eventRepository.getChannelEvent(EVENT_ID, CHANNEL_ID)).thenReturn(msChannelEvent);

        WebhookFeverDTO result = channelWebhookService.sendChannelSaleRequestUpdateDetail(webhookFever);

        Assertions.assertTrue(result.getAllowSend());
        Assertions.assertNotNull(result.getFeverMessage().getSaleRequestStatusDTO());
        SaleRequestStatusDTO saleRequestStatus = result.getFeverMessage().getSaleRequestStatusDTO();
        Assertions.assertEquals(channelEvent.getChannelId(), saleRequestStatus.getChannelId());
        Assertions.assertEquals(channelEvent.getEventId(), saleRequestStatus.getEventId());
        Assertions.assertEquals(SaleRequestStatus.ACCEPTED, saleRequestStatus.getStatus());
    }

    @Test
    public void sendChannelSaleRequestUpdateDetail_notAllowed_returnEarly() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(999L, WhitelabelType.INTERNAL);
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);

        WebhookFeverDTO result = channelWebhookService.sendChannelSaleRequestUpdateDetail(webhookFever);

        Assertions.assertFalse(result.getAllowSend());
        Assertions.assertNull(result.getFeverMessage().getSaleRequestStatusDTO());
    }

    @Test
    public void sendChannelRequiredEventsUpdateDetail_allowedEntity_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(ENTITY_ID, WhitelabelType.INTERNAL);
        
        MsSaleRequestsResponseDTO saleRequestsResponse = generateSaleRequestsResponse();
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);
        when(channelRepository.getSaleRequests(any(MsSaleRequestsFilter.class))).thenReturn(saleRequestsResponse);
        
        Map<Integer, List<Integer>> eventRestrictions = generateEventRestrictions();
        when(channelRepository.getEventSaleRestrictions(CHANNEL_ID)).thenReturn(eventRestrictions);

        WebhookFeverDTO result = channelWebhookService.sendChannelRequiredEventsUpdateDetail(webhookFever);

        Assertions.assertTrue(result.getAllowSend());
        Assertions.assertEquals(CHANNEL_ID, result.getFeverMessage().getChannelId());
        Assertions.assertNotNull(result.getFeverMessage().getRequiredEvents());
    }

    @Test
    public void sendChannelRequiredEventsUpdateDetail_notAllowed_returnEarly() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(999L, WhitelabelType.INTERNAL);
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);

        WebhookFeverDTO result = channelWebhookService.sendChannelRequiredEventsUpdateDetail(webhookFever);

        Assertions.assertFalse(result.getAllowSend());
        Assertions.assertNull(result.getFeverMessage().getChannelId());
        Assertions.assertNull(result.getFeverMessage().getRequiredEvents());
    }

    @Test
    public void sendChannelRequiredEventsUpdateDetail_paginatedRequests_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ChannelDTO channel = generateChannelDTO(ENTITY_ID, WhitelabelType.INTERNAL);
        
        MsSaleRequestsResponseDTO firstPage = generateSaleRequestsResponse();
        firstPage.getMetadata().setTotal(75L); // Force pagination
        
        MsSaleRequestsResponseDTO secondPage = generateSaleRequestsResponse();
        secondPage.getMetadata().setTotal(75L);
        secondPage.getData().clear(); // Empty second page
        
        when(channelRepository.getChannelNoCache(CHANNEL_ID)).thenReturn(channel);
        when(channelRepository.getSaleRequests(any(MsSaleRequestsFilter.class)))
            .thenReturn(firstPage)
            .thenReturn(secondPage);
        
        Map<Integer, List<Integer>> eventRestrictions = generateEventRestrictions();
        when(channelRepository.getEventSaleRestrictions(CHANNEL_ID)).thenReturn(eventRestrictions);

        WebhookFeverDTO result = channelWebhookService.sendChannelRequiredEventsUpdateDetail(webhookFever);

        Assertions.assertTrue(result.getAllowSend());
        Assertions.assertEquals(CHANNEL_ID, result.getFeverMessage().getChannelId());
        Assertions.assertNotNull(result.getFeverMessage().getRequiredEvents());
    }

    private WebhookFeverDTO generateWebhookMessage() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId(CHANNEL_ID.toString());
        
        FeverMessageDTO message = new FeverMessageDTO();
        WebhookFeverDTO webhookFever = new WebhookFeverDTO(notificationMessage, req, message);
        
        AllowedEntitiesFileData allowedEntities = new AllowedEntitiesFileData();
        allowedEntities.setEntityId(ENTITY_ID);
        allowedEntities.setAllowedEntities(List.of(ALLOWED_ENTITY_ID));
        webhookFever.setAllowedEntitiesFileData(allowedEntities);
        
        return webhookFever;
    }

    private ChannelDTO generateChannelDTO(Long entityId, WhitelabelType whitelabelType) {
        ChannelDTO channel = new ChannelDTO();
        channel.setId(CHANNEL_ID);
        channel.setEntityId(entityId);
        channel.setWhitelabelType(whitelabelType);
        channel.setName("Test Channel");
        return channel;
    }

    private ChannelFormsResponse generateChannelFormsResponse() {
        ChannelFormsResponse response = new ChannelFormsResponse();
        
        List<ChannelFormField> purchaseFields = new ArrayList<>();
        ChannelFormField field = new ChannelFormField();
        field.setKey("testKey");
        field.setMandatory(true);
        field.setVisible(true);
        field.setType("text");
        purchaseFields.add(field);
        
        response.setPurchase(purchaseFields);
        response.setBooking(new ArrayList<>());
        response.setIssue(new ArrayList<>());
        response.setPayment(new ArrayList<>());
        response.setMember(new ArrayList<>());
        response.setNewMember(new ArrayList<>());
        
        return response;
    }

    private ChannelEventDTO generateChannelEventDTO() {
        ChannelEventDTO channelEvent = new ChannelEventDTO();
        channelEvent.setChannelId(CHANNEL_ID.intValue());
        channelEvent.setEventId(EVENT_ID.intValue());
        channelEvent.setStatus(ChannelEventStatus.ACCEPTED);
        return channelEvent;
    }

    private MsSaleRequestsResponseDTO generateSaleRequestsResponse() {
        MsSaleRequestsResponseDTO response = new MsSaleRequestsResponseDTO();
        
        List<MsSaleRequestDTO> data = new ArrayList<>();
        MsSaleRequestDTO saleRequest = new MsSaleRequestDTO();
        MsEventSaleRequestDTO event = new MsEventSaleRequestDTO();
        event.setId(EVENT_ID);
        saleRequest.setEvent(event);
        data.add(saleRequest);
        
        response.setData(data);
        
        Metadata metadata = new Metadata();
        metadata.setTotal(1L);
        response.setMetadata(metadata);
        
        return response;
    }

    private Map<Integer, List<Integer>> generateEventRestrictions() {
        Map<Integer, List<Integer>> restrictions = new HashMap<>();
        restrictions.put(EVENT_ID.intValue(), List.of(2000, 3000));
        return restrictions;
    }


    private es.onebox.common.datasources.ms.event.dto.ChannelEventDTO generateMsChannelEvent() {
        es.onebox.common.datasources.ms.event.dto.ChannelEventDTO channelEvent = new es.onebox.common.datasources.ms.event.dto.ChannelEventDTO();
        channelEvent.setChannelId(CHANNEL_ID);
        channelEvent.setEventId(EVENT_ID);
        channelEvent.setChannelEventStatus(2);
        return channelEvent;
    }
}

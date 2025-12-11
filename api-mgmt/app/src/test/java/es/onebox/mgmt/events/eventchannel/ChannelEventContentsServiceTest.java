package es.onebox.mgmt.events.eventchannel;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChannelEventImageConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionPackType;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.dto.channel.EventImageConfigDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ChannelEventContentsServiceTest {

    private static final Long EVENT_ID = 1L;
    private static final Long CHANNEL_ID = 1L;
    private static final Long ENTITY_ID = 100L;

    @Mock
    private EventsRepository eventsRepository;
    @Mock
    private EventChannelsRepository eventChannelsRepository;
    @Mock
    private EventChannelContentsRepository EventChannelContentsRepository;
    @Mock
    private SecurityManager securityManager;
    @InjectMocks
    private ChannelEventContentsService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getChannelEventImagesConfiguration_invalidWhitelabelType_KO() {
        EventChannel eventChannel = new EventChannel();
        EventChannelInfo channelInfo = new EventChannelInfo();
        channelInfo.setWhitelabelType(WhitelabelType.INTERNAL);
        eventChannel.setChannel(channelInfo);
        when(eventChannelsRepository.getEventChannel(EVENT_ID, CHANNEL_ID)).thenReturn(eventChannel);

        Event event = new Event();
        event.setId(EVENT_ID);
        event.setEntityId(ENTITY_ID);
        event.setStatus(EventStatus.READY);
        when(eventsRepository.getCachedEvent(EVENT_ID)).thenReturn(event);

        try {
            service.getChannelEventImagesConfiguration(EVENT_ID, CHANNEL_ID);
        } catch (OneboxRestException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            assertEquals(String.format(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED.getMessage()), e.getMessage());
        }
    }

    @Test
    public void getChannelEventImagesConfiguration_invalidEventConfig_KO() {
        EventChannel eventChannel = new EventChannel();
        EventChannelInfo channelInfo = new EventChannelInfo();
        channelInfo.setWhitelabelType(WhitelabelType.EXTERNAL);
        eventChannel.setChannel(channelInfo);
        when(eventChannelsRepository.getEventChannel(EVENT_ID, CHANNEL_ID)).thenReturn(eventChannel);

        Event event = new Event();
        event.setId(EVENT_ID);
        event.setEntityId(ENTITY_ID);
        event.setStatus(EventStatus.READY);
        event.setSupraEvent(false);
        event.setSessionPackType(SessionPackType.DISABLED);
        when(eventsRepository.getCachedEvent(EVENT_ID)).thenReturn(event);

        try {
            service.getChannelEventImagesConfiguration(EVENT_ID, CHANNEL_ID);
        } catch (OneboxRestException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            assertEquals(String.format(ApiMgmtErrorCode.EVENT_CONFIG_NOT_SUPPORTED.getMessage()), e.getMessage());
        }
    }
    @Test
    public void getChannelEventImagesConfiguration_OK() {
        EventChannel eventChannel = new EventChannel();
        EventChannelInfo channelInfo = new EventChannelInfo();
        channelInfo.setWhitelabelType(WhitelabelType.EXTERNAL);
        eventChannel.setChannel(channelInfo);
        when(eventChannelsRepository.getEventChannel(EVENT_ID, CHANNEL_ID)).thenReturn(eventChannel);

        Event event = new Event();
        event.setId(EVENT_ID);
        event.setEntityId(ENTITY_ID);
        event.setStatus(EventStatus.READY);
        event.setSupraEvent(true);
        event.setSessionPackType(SessionPackType.DISABLED);
        when(eventsRepository.getCachedEvent(EVENT_ID)).thenReturn(event);

        List<ChannelEventImageConfigDTO> imageConfig = ObjectRandomizer.randomListOf(ChannelEventImageConfigDTO.class, 2);
        when(EventChannelContentsRepository.getChannelEventImageConfig(EVENT_ID, CHANNEL_ID)).thenReturn(imageConfig);

        List<EventImageConfigDTO> result = service.getChannelEventImagesConfiguration(EVENT_ID, CHANNEL_ID);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}

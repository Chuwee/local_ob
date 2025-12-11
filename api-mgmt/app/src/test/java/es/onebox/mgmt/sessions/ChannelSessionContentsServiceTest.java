package es.onebox.mgmt.sessions;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.Language;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.event.dto.event.*;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.repository.*;
import es.onebox.mgmt.events.dto.ChannelEventContentImageFilter;
import es.onebox.mgmt.events.dto.ChannelEventContentImageUpdateRequest;
import es.onebox.mgmt.events.enums.ChannelEventContentImageType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChannelSessionImageServiceTest {

    @Mock
    private EventsRepository eventsRepository;
    @Mock
    private SessionsRepository sessionsRepository;
    @Mock
    private EventChannelsRepository eventChannelsRepository;
    @Mock
    private SecurityManager securityManager;
    @Mock
    private MasterdataService masterdataService;
    @Mock
    private ValidationService validationService;
    @Mock
    private ChannelsHelper channelsHelper;

    @InjectMocks
    private ChannelSessionContentsService service;

    private final Long EVENT_ID = 1L;
    private final Long SESSION_ID = 1L;
    private final Long CHANNEL_ID = 1L;
    private final Long ENTITY_ID = 100L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock Event
        Event event = new Event();
        event.setId(EVENT_ID);
        event.setEntityId(ENTITY_ID);
        event.setStatus(EventStatus.IN_PROGRAMMING);
        when(eventsRepository.getEvent(EVENT_ID)).thenReturn(event);

        // Security check
        doNothing().when(securityManager).checkEntityAccessible(ENTITY_ID);

        // Mock Session
        Session session = new Session();
        session.setId(SESSION_ID);
        session.setEventId(EVENT_ID);
        session.setStatus(SessionStatus.IN_PROGRESS);
        when(sessionsRepository.getSession(EVENT_ID, SESSION_ID)).thenReturn(session);
        when(validationService.getAndCheckSession(EVENT_ID, SESSION_ID)).thenReturn(session);

        // Mock EventChannel external whitelabel
        EventChannel eventChannel = new EventChannel();
        EventChannelInfo channelInfo = new EventChannelInfo();
        channelInfo.setWhitelabelType(WhitelabelType.EXTERNAL);
        eventChannel.setChannel(channelInfo);
        when(eventChannelsRepository.getEventChannel(EVENT_ID, CHANNEL_ID)).thenReturn(eventChannel);

        when(eventsRepository.getEvent(EVENT_ID)).thenReturn(mockValidEvent());
    }



    @Test
    void getChannelSessionImages_invalidEventId_throwsException() {
        Long invalidEventId = -1L;

        OneboxRestException ex = assertThrows(OneboxRestException.class, () ->
                service.getChannelSessionImages(invalidEventId, SESSION_ID, CHANNEL_ID, null)
        );

        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_ID.name(), ex.getErrorCode());
    }

    @Test
    void getChannelSessionImages_invalidSessionId_throwsException() {
        Long eventId = 1L;
        Long invalidSessionId = -1L;
        Long channelId = 1L;

        when(eventsRepository.getEvent(eventId)).thenReturn(mockValidEvent());
        when(eventChannelsRepository.getEventChannel(eventId, channelId)).thenReturn(mockValidExternalEventChannel());

        when(validationService.getAndCheckSession(eq(eventId), eq(invalidSessionId)))
                .thenThrow(new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventId and sessionId must be a positive integer", null));

        OneboxRestException ex = assertThrows(OneboxRestException.class, () ->
                service.getChannelSessionImages(eventId, invalidSessionId, channelId, null)
        );

        assertEquals(CoreErrorCode.BAD_PARAMETER.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void getChannelSessionImages_noImages_returnsEmptyList() {
        when(sessionsRepository.getChannelSessionCommunicationElements(eq(EVENT_ID), eq(SESSION_ID), eq(CHANNEL_ID), any(), any()))
                .thenReturn(Collections.emptyList());

        ChannelContentImageListDTO<ChannelEventContentImageType> result =
                service.getChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID, null);

        assertNotNull(result);
        assertTrue(result.getImages().isEmpty());
    }

    @Test
    void getChannelSessionImages_withFilter_returnsFilteredImages() {
        ChannelEventContentImageFilter filter = new ChannelEventContentImageFilter();
        filter.setLanguage("es-ES");
        filter.setType(ChannelEventContentImageType.SQUARE_LANDSCAPE);

        when(masterdataService.getLanguageByCode("es_ES")).thenReturn(1);

        EventCommunicationElement element = new EventCommunicationElement();
        element.setLanguage("es_ES");
        element.setTagId(ChannelEventContentImageType.SQUARE_LANDSCAPE.getTagId());
        element.setValue("https://image.url/example.jpg");
        element.setPosition(1);

        when(sessionsRepository.getChannelSessionCommunicationElements(eq(EVENT_ID), eq(SESSION_ID), eq(CHANNEL_ID), any(), any()))
                .thenReturn(List.of(element));

        ChannelContentImageListDTO<ChannelEventContentImageType> result =
                service.getChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID, filter);

        assertNotNull(result);
        assertEquals(1, result.getImages().size());
        ChannelContentImageDTO<ChannelEventContentImageType> dto = result.getImages().get(0);
        assertEquals("es-ES", dto.getLanguage());
        assertEquals("https://image.url/example.jpg", dto.getImageUrl());
        assertEquals(ChannelEventContentImageType.SQUARE_LANDSCAPE, dto.getType());
    }

    @Test
    void updateChannelSessionImages_invalidImage_throwsException() {
        ChannelEventContentImageUpdateRequest request = setupMocksAndCreateRequest(
                EVENT_ID,
                SESSION_ID,
                CHANNEL_ID,
                "invalidbase64==",
                ChannelEventContentImageType.SQUARE_LANDSCAPE,
                1
        );

        OneboxRestException ex = assertThrows(OneboxRestException.class, () ->
                service.updateChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID, request)
        );

        assertEquals(ApiMgmtErrorCode.IMAGE_INVALID_FILE.name(), ex.getErrorCode());
    }

    @Test
    void updateChannelSessionImages_nullPosition_throwsException() {
        ChannelEventContentImageUpdateRequest request = setupMocksAndCreateRequest(
                EVENT_ID,
                SESSION_ID,
                CHANNEL_ID,
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAgEB9pQ6ZwAAAABJRU5ErkJggg==",
                ChannelEventContentImageType.SQUARE_LANDSCAPE,
                null
        );

        try (MockedStatic<FileUtils> fileUtilsMocked = mockStatic(FileUtils.class)) {
            fileUtilsMocked.when(() -> FileUtils.checkImage(anyString(), any(), any()))
                    .thenAnswer(invocation -> null);

            OneboxRestException ex = assertThrows(OneboxRestException.class, () -> {
                service.updateChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID, request);
            });

            assertEquals(ApiMgmtErrorCode.IMAGE_POSITION_REQUIRED.name(), ex.getErrorCode());
        }
    }

    @Test
    void deleteChannelSessionImages_channelNotExternal_throwsException() {
        // Setup canal no externo
        EventChannel eventChannel = new EventChannel();
        EventChannelInfo channelInfo = new EventChannelInfo();
        channelInfo.setWhitelabelType(WhitelabelType.INTERNAL); // no EXTERNAL
        eventChannel.setChannel(channelInfo);

        when(eventsRepository.getEvent(EVENT_ID)).thenReturn(mockValidEvent());
        when(eventChannelsRepository.getEventChannel(EVENT_ID, CHANNEL_ID)).thenReturn(eventChannel);

        doNothing().when(securityManager).checkEntityAccessible(anyLong());
        when(validationService.getAndCheckSession(EVENT_ID, SESSION_ID)).thenReturn(mockValidSession());

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> {
            service.deleteChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID);
        });

        assertEquals(ApiMgmtErrorCode.WHITELABEL_TYPE_NOT_SUPPORTED.toString(), ex.getErrorCode());
    }

    @Test
    void deleteChannelSessionImages_externalChannel_withImages_deletesImages() {
        mockExternalChannel();

        EventCommunicationElement elem = new EventCommunicationElement();
        elem.setTagId(ChannelEventContentImageType.SQUARE_LANDSCAPE.getTagId());
        elem.setLanguage("es_ES");
        elem.setPosition(1);

        when(sessionsRepository.getChannelSessionCommunicationElements(
                eq(EVENT_ID), eq(SESSION_ID), eq(CHANNEL_ID), isNull(), any()))
                .thenReturn(List.of(elem));

        Map<String, Long> languagesMap = Map.of("es_ES", 1L);
        when(masterdataService.getLanguagesByIdAndCode()).thenReturn(languagesMap);

        doAnswer(invocation -> {
            List<EventCommunicationElement> toDelete = invocation.getArgument(3);
            assertFalse(toDelete.isEmpty());
            return null;
        }).when(sessionsRepository).updateChannelSessionCommunicationElements(anyLong(), anyLong(), anyLong(), anyList());

        service.deleteChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID);

        verify(sessionsRepository).updateChannelSessionCommunicationElements(anyLong(), anyLong(), anyLong(), anyList());
    }

    @Test
    void deleteChannelSessionImages_externalChannel_noImages_doesNotDelete() {
        mockExternalChannel();

        when(sessionsRepository.getChannelSessionCommunicationElements(
                eq(EVENT_ID), eq(SESSION_ID), eq(CHANNEL_ID), isNull(), any()))
                .thenReturn(Collections.emptyList());

        service.deleteChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID);

        verify(sessionsRepository).updateChannelSessionCommunicationElements(anyLong(), anyLong(), anyLong(), eq(Collections.emptyList()));
    }

    @Test
    void deleteChannelSessionImages_invalidSession_throwsException() {
        mockExternalChannel();

        when(validationService.getAndCheckSession(EVENT_ID, SESSION_ID))
                .thenThrow(new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Invalid session", null));

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> {
            service.deleteChannelSessionImages(EVENT_ID, SESSION_ID, CHANNEL_ID);
        });

        assertEquals(CoreErrorCode.BAD_PARAMETER.getErrorCode(), ex.getErrorCode());
    }

    //Mocks
    private void mockExternalChannel() {
        EventChannel eventChannel = new EventChannel();
        EventChannelInfo channelInfo = new EventChannelInfo();
        channelInfo.setWhitelabelType(WhitelabelType.EXTERNAL);
        eventChannel.setChannel(channelInfo);

        when(eventsRepository.getEvent(EVENT_ID)).thenReturn(mockValidEvent());
        when(eventChannelsRepository.getEventChannel(EVENT_ID, CHANNEL_ID)).thenReturn(eventChannel);

        doNothing().when(securityManager).checkEntityAccessible(anyLong());
        when(validationService.getAndCheckSession(EVENT_ID, SESSION_ID)).thenReturn(mockValidSession());
    }

    private ChannelEventContentImageUpdateRequest setupMocksAndCreateRequest(
            Long eventId,
            Long sessionId,
            Long channelId,
            String imageBinary,
            ChannelEventContentImageType type,
            Integer position
    ) {
        ChannelEventContentImageUpdateRequest request = new ChannelEventContentImageUpdateRequest();

        ChannelContentImageDTO<ChannelEventContentImageType> imageDTO = new ChannelContentImageDTO<>();
        imageDTO.setLanguage("es-ES");
        imageDTO.setImageBinary(imageBinary);
        imageDTO.setType(type);
        imageDTO.setPosition(position);

        request.getImages().add(imageDTO);

        ChannelResponse mockChannelResponse = mock(ChannelResponse.class);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(mockChannelResponse);

        Language languages = mock(Language.class);
        when(mockChannelResponse.getLanguages()).thenReturn(languages);
        when(languages.getSelectedLanguages()).thenReturn(List.of(1L));

        Map<Long, String> languagesByIds = Map.of(1L, "es_ES");
        when(masterdataService.getLanguagesByIds()).thenReturn(languagesByIds);

        EventChannel mockEventChannel = mock(EventChannel.class);
        EventChannelInfo mockChannel = mock(EventChannelInfo.class);
        when(eventChannelsRepository.getEventChannel(eventId, channelId)).thenReturn(mockEventChannel);
        when(mockEventChannel.getChannel()).thenReturn(mockChannel);
        when(mockChannel.getWhitelabelType()).thenReturn(WhitelabelType.EXTERNAL);

        when(eventsRepository.getEvent(eventId)).thenReturn(mockValidEvent());
        when(validationService.getAndCheckSession(eventId, sessionId)).thenReturn(mockValidSession());

        return request;
    }

    private Event mockValidEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setEntityId(100L);
        event.setStatus(EventStatus.IN_PROGRAMMING);
        return event;
    }

    private EventChannel mockValidExternalEventChannel() {
        EventChannel ec = new EventChannel();
        ec.setId(1L);
        return ec;
    }

    private Session mockValidSession() {
        Session session = new Session();
        session.setId(1L);
        session.setEventId(1L);
        session.setStatus(SessionStatus.IN_PROGRESS);
        return session;
    }
}

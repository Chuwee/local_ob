package es.onebox.event.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.attendants.dao.EventAttendantConfigCouchDao;
import es.onebox.event.attendants.dao.SessionAttendantConfigCouchDao;
import es.onebox.event.attendants.domain.EventAttendantsConfig;
import es.onebox.event.attendants.domain.SessionAttendantsConfig;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.events.dao.AttendantFieldDao;
import es.onebox.event.events.dao.EventAvetConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventAvetConfig;
import es.onebox.event.events.enums.EventType;
import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AttendantsConfigServiceTest {

    @Mock
    private SessionAttendantConfigCouchDao sessionAttendantConfigCouchDao;
    @Mock
    private EventAttendantConfigCouchDao eventAttendantConfigCouchDao;
    @Mock
    private EventDao eventDao;
    @Mock
    private EventAvetConfigCouchDao eventAvetConfigCouchDao;
    @Mock
    private AttendantFieldDao eventFieldDao;
    @InjectMocks
    private AttendantsConfigService attendantsConfigService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addChannelToAttendants() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.addChannelToAttendantsConfig(null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                        && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("eventId is mandatory"),
                "On null eventId an exception is thrown");

        try {
            attendantsConfigService.addChannelToAttendantsConfig(-1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("eventId is mandatory"),
                "On negative eventId an exception is thrown");

        try {
            attendantsConfigService.addChannelToAttendantsConfig(0L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("eventId is mandatory"),
                "On zero eventId an exception is thrown");

        try {
            attendantsConfigService.addChannelToAttendantsConfig(1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("channelId is mandatory"),
                "On null channelId an exception is thrown");

        try {
            attendantsConfigService.addChannelToAttendantsConfig(1L, -1L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("channelId is mandatory"),
                "On negative channelId an exception is thrown");

        try {
            attendantsConfigService.addChannelToAttendantsConfig(1L, 0L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("channelId is mandatory"),
                "On zero channelId an exception is thrown");

        Long eventId = 1L;
        when(eventAttendantConfigCouchDao.get(String.valueOf(eventId))).thenReturn(null);
        attendantsConfigService.addChannelToAttendantsConfig(eventId, 1L);
        verify(eventAttendantConfigCouchDao, never()).upsert(eq(String.valueOf(eventId)), any(EventAttendantsConfig.class));
        verifyNoInteractions(sessionAttendantConfigCouchDao);

        eventId = 2L;
        EventAttendantsConfig ea = new EventAttendantsConfig();
        ea.setEventId(eventId);
        ea.setAutomaticChannelAssignment(false);
        when(eventAttendantConfigCouchDao.get(String.valueOf(eventId))).thenReturn(ea);
        attendantsConfigService.addChannelToAttendantsConfig(eventId, 1L);
        verify(eventAttendantConfigCouchDao, never()).upsert(eq(String.valueOf(eventId)), any(EventAttendantsConfig.class));
        verifyNoInteractions(sessionAttendantConfigCouchDao);

        ea.setAutomaticChannelAssignment(true);
        when(eventAttendantConfigCouchDao.get(String.valueOf(eventId))).thenReturn(ea);
        attendantsConfigService.addChannelToAttendantsConfig(eventId, 1L);
        verify(eventAttendantConfigCouchDao, times(1)).upsert(String.valueOf(eventId), ea);
        verifyNoInteractions(sessionAttendantConfigCouchDao);

        ea.setCustomConfiguredSessions(new ArrayList<>());
        ea.getCustomConfiguredSessions().add(99L);

        attendantsConfigService.addChannelToAttendantsConfig(eventId, 1L);
    }


    @Test
    void getEventsAttendant() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.getEventsAttendantConfig(null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                        && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("eventId is mandatory"),
                "On null eventId an exception is thrown");

        try {
            attendantsConfigService.getEventsAttendantConfig(-1L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                        && e.getMessage().equals("eventId is mandatory"),
                "On negative eventId an exception is thrown");

        try {
            attendantsConfigService.getEventsAttendantConfig(0L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On zero eventId an exception is thrown");

        attendantsConfigService.getEventsAttendantConfig(1L);
        verify(eventAttendantConfigCouchDao, times(1)).get(String.valueOf(1));
    }

    @Test
    void upsertEventsAttendant() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.upsertEventsAttendantConfig(null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On null eventId an exception is thrown");

        try {
            attendantsConfigService.upsertEventsAttendantConfig(-1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On negative eventId an exception is thrown");

        try {
            attendantsConfigService.upsertEventsAttendantConfig(0L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On zero eventId an exception is thrown");

        try {
            attendantsConfigService.upsertEventsAttendantConfig(1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventAttendantsDTO is mandatory"), "On null eventAttendantsDTO an exception is thrown");

        EventAttendantsConfig dto = new EventAttendantsConfig();
        dto.setEventId(1L);

        CpanelEventoRecord eventDTO = new CpanelEventoRecord();
        eventDTO.setTipoevento(EventType.NORMAL.getId());
        when(eventDao.findById(dto.getEventId().intValue())).thenReturn(eventDTO);

        List<CpanelEventFieldRecord> fieldRecords = new ArrayList<>();
        fieldRecords.add(new CpanelEventFieldRecord());
        when(eventFieldDao.getEventFieldByEventAndFieldGroup(any(), any())).thenReturn(fieldRecords);

        attendantsConfigService.upsertEventsAttendantConfig(1L, dto);
        verify(eventAttendantConfigCouchDao, times(1)).upsert(String.valueOf(1), dto);
    }

    @Test
    void getSessionAttendants() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.getSessionAttendantsConfig(null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On null sessionId an exception is thrown");

        try {
            attendantsConfigService.getSessionAttendantsConfig(-1L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On negative sessionId an exception is thrown");

        try {
            attendantsConfigService.getSessionAttendantsConfig(0L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On zero sessionId an exception is thrown");
        Long sessionId = 1L;
        when(sessionAttendantConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(null);
        SessionAttendantsConfigDTO sessionAttendants = attendantsConfigService.getSessionAttendantsConfig(sessionId);
        assertNull(sessionAttendants, "If there is no session attendant in DB return null");

        sessionId = 2L;
        SessionAttendantsConfig expectedResult = new SessionAttendantsConfig();
        expectedResult.setSessionId(sessionId);
        expectedResult.setAutomaticChannelAssignment(true);
        expectedResult.setAllChannelsActive(false);
        expectedResult.setActive(true);
        expectedResult.setAutofill(false);
        when(sessionAttendantConfigCouchDao.get(String.valueOf(sessionId))).thenReturn(expectedResult);
        sessionAttendants = attendantsConfigService.getSessionAttendantsConfig(sessionId);
        assertNotNull(sessionAttendants);
    }

    @Test
    void createSessionAttendants() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.createSessionAttendantsConfig(null, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On null sessionId an exception is thrown");

        try {
            attendantsConfigService.createSessionAttendantsConfig(-1L, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On negative sessionId an exception is thrown");

        try {
            attendantsConfigService.createSessionAttendantsConfig(0L, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On zero sessionId an exception is thrown");

        try {
            attendantsConfigService.createSessionAttendantsConfig(1L, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On null eventId an exception is thrown");

        try {
            attendantsConfigService.createSessionAttendantsConfig(1L, -1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On negative eventId an exception is thrown");

        try {
            attendantsConfigService.createSessionAttendantsConfig(1L, 0L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On zero eventId an exception is thrown");

        try {
            attendantsConfigService.createSessionAttendantsConfig(1L, 1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionAttendants is mandatory"), "On null attendantsSession an exception is thrown");

        Long sessionId = 1L;
        Long eventId = 1L;
        SessionAttendantsConfig attendants = new SessionAttendantsConfig();
        attendants.setSessionId(sessionId);
        attendants.setAutomaticChannelAssignment(true);
        attendants.setAllChannelsActive(false);
        attendants.setActive(true);
        attendants.setAutofill(false);
        attendantsConfigService.createSessionAttendantsConfig(sessionId, eventId, attendants);
    }

    @Test
    void upsertSessionAttendants() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.upsertSessionAttendantsConfig(null, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On null sessionId an exception is thrown");

        try {
            attendantsConfigService.upsertSessionAttendantsConfig(-1L, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On negative sessionId an exception is thrown");

        try {
            attendantsConfigService.upsertSessionAttendantsConfig(0L, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On zero sessionId an exception is thrown");

        try {
            attendantsConfigService.upsertSessionAttendantsConfig(1L, null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On null eventId an exception is thrown");

        try {
            attendantsConfigService.upsertSessionAttendantsConfig(1L, -1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On negative eventId an exception is thrown");

        try {
            attendantsConfigService.upsertSessionAttendantsConfig(1L, 0L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On zero eventId an exception is thrown");

        try {
            attendantsConfigService.upsertSessionAttendantsConfig(1L, 1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionAttendants is mandatory"), "On null attendantsSession an exception is thrown");

        Long sessionId = 1L;
        Long eventId = 1L;
        SessionAttendantsConfig attendants = new SessionAttendantsConfig();
        attendants.setSessionId(sessionId);
        attendants.setAutomaticChannelAssignment(true);
        attendants.setAllChannelsActive(false);
        attendants.setActive(true);
        attendants.setAutofill(null);

        CpanelEventoRecord eventDTO = new CpanelEventoRecord();
        eventDTO.setTipoevento(EventType.NORMAL.getId());
        when(eventDao.findById(eventId.intValue())).thenReturn(eventDTO);

        List<CpanelEventFieldRecord> fieldRecords = new ArrayList<>();
        fieldRecords.add(new CpanelEventFieldRecord());
        when(eventFieldDao.getEventFieldByEventAndFieldGroup(any(), any())).thenReturn(fieldRecords);

        attendantsConfigService.upsertSessionAttendantsConfig(sessionId, eventId, attendants);
    }

    @Test
    void deleteSessionAttendants() {
        OneboxRestException e = null;
        try {
            attendantsConfigService.deleteSessionAttendantsConfig(null, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e != null
                && e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On null sessionId an exception is thrown");

        try {
            attendantsConfigService.deleteSessionAttendantsConfig(-1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On negative sessionId an exception is thrown");

        try {
            attendantsConfigService.deleteSessionAttendantsConfig(0L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("sessionId is mandatory"), "On zero sessionId an exception is thrown");

        try {
            attendantsConfigService.deleteSessionAttendantsConfig(1L, null);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On null eventId an exception is thrown");

        try {
            attendantsConfigService.deleteSessionAttendantsConfig(1L, -1L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On negative eventId an exception is thrown");

        try {
            attendantsConfigService.deleteSessionAttendantsConfig(1L, 0L);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        assertTrue(e.getErrorCode().equals(CoreErrorCode.BAD_PARAMETER.getErrorCode())
                && e.getMessage().equals("eventId is mandatory"), "On zero eventId an exception is thrown");

        attendantsConfigService.deleteSessionAttendantsConfig(1L, 1L);
    }

    @Test
    void validateAttendantsConfigUpdate() {
        EventAttendantsConfig eventAttendantsConfig = new EventAttendantsConfig();
        CpanelEventoRecord eventDTO = new CpanelEventoRecord();
        EventAvetConfig avetConfig = new EventAvetConfig();
        eventDTO.setTipoevento(EventType.NORMAL.getId());
        Long eventId = 1L;
        Exception e = null;
        try {
            when(eventDao.findById(eventId.intValue())).thenReturn(eventDTO);
            attendantsConfigService.validateAttendantsConfigUpdate(eventAttendantsConfig, eventId);
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
        try {
            eventAttendantsConfig.setAutofill(Boolean.TRUE);
            eventDTO.setTipoevento(EventType.AVET.getId());
            when(eventDao.findById(eventId.intValue())).thenReturn(eventDTO);
            avetConfig.setIsSocket(true);
            when(eventAvetConfigCouchDao.get(eventId.toString())).thenReturn(avetConfig);
            attendantsConfigService.validateAttendantsConfigUpdate(eventAttendantsConfig, eventId);
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
        try {
            eventAttendantsConfig.setAutofill(Boolean.TRUE);
            eventDTO.setTipoevento(EventType.AVET.getId());
            when(eventDao.findById(eventId.intValue())).thenReturn(eventDTO);
            avetConfig.setIsSocket(false);
            when(eventAvetConfigCouchDao.get(eventId.toString())).thenReturn(avetConfig);
            attendantsConfigService.validateAttendantsConfigUpdate(eventAttendantsConfig, eventId);
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
        e = null;
        try {
            eventAttendantsConfig.setAutofill(Boolean.TRUE);
            eventDTO.setTipoevento(EventType.NORMAL.getId());
            when(eventDao.findById(eventId.intValue())).thenReturn(eventDTO);
            attendantsConfigService.validateAttendantsConfigUpdate(eventAttendantsConfig, eventId);
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }

}

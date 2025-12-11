package es.onebox.event.attendants;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.attendants.dao.EventAttendantConfigCouchDao;
import es.onebox.event.attendants.dao.SessionAttendantConfigCouchDao;
import es.onebox.event.attendants.domain.AttendantsConfig;
import es.onebox.event.attendants.domain.EventAttendantsConfig;
import es.onebox.event.attendants.domain.SessionAttendantsConfig;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.attendants.enums.DefaultField;
import es.onebox.event.attendants.enums.FieldGroup;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.AttendantFieldDao;
import es.onebox.event.events.dao.EventAvetConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventAvetConfig;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static es.onebox.event.common.utils.ConverterUtils.updateField;

@Service
public class AttendantsConfigService {

    public static final String SESSION_ID = "sessionId";
    private static final String EVENT_ID = "eventId";
    private static final String CHANNEL_ID = "channelId";

    private final SessionAttendantConfigCouchDao sessionAttendantConfigCouchDao;
    private final EventAttendantConfigCouchDao eventAttendantConfigCouchDao;
    private final EventDao eventDao;
    private final EventAvetConfigCouchDao eventAvetConfigCouchDao;
    private final AttendantFieldDao eventFieldDao;

    @Autowired
    public AttendantsConfigService(SessionAttendantConfigCouchDao sessionAttendantConfigCouchDao,
                                   EventAttendantConfigCouchDao eventAttendantConfigCouchDao,
                                   EventDao eventDao, EventAvetConfigCouchDao eventAvetConfigCouchDao,
                                   AttendantFieldDao eventFieldDao) {
        this.sessionAttendantConfigCouchDao = sessionAttendantConfigCouchDao;
        this.eventAttendantConfigCouchDao = eventAttendantConfigCouchDao;
        this.eventDao = eventDao;
        this.eventAvetConfigCouchDao = eventAvetConfigCouchDao;
        this.eventFieldDao = eventFieldDao;
    }

    public void addChannelToAttendantsConfig(Long eventId, Long channelId) {
        validateIdentifier(eventId, EVENT_ID);
        validateIdentifier(channelId, CHANNEL_ID);
        EventAttendantsConfig ea = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        if (ea == null) {
            return;
        }
        if (CommonUtils.isTrue(ea.isAutomaticChannelAssignment())) {
            if (ea.getActiveChannels() == null) {
                ea.setActiveChannels(new ArrayList<>());
            }
            if (!ea.getActiveChannels().contains(channelId)) {
                ea.getActiveChannels().add(channelId);
                eventAttendantConfigCouchDao.upsert(String.valueOf(eventId), ea);
            }
        }
        if (!CommonUtils.isEmpty(ea.getCustomConfiguredSessions())) {
            List<Key> sessionIds = getSessionKeys(ea.getCustomConfiguredSessions());
            List<SessionAttendantsConfig> attendants = sessionAttendantConfigCouchDao.bulkGet(sessionIds);
            addChannelToSessionsAttendantConfig(attendants, channelId);
        }
    }

    public void deleteChannelAttendantsConfig(Long eventId, Long channelId) {
        validateIdentifier(eventId, EVENT_ID);
        validateIdentifier(channelId, CHANNEL_ID);
        EventAttendantsConfig ea = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        if (ea == null) {
            return;
        }
        List<Long> activeChannels = ea.getActiveChannels();
        if (Boolean.TRUE.equals(ea.isAutomaticChannelAssignment()) && CollectionUtils.isNotEmpty(activeChannels)
                && activeChannels.contains(channelId)) {
            activeChannels.remove(channelId);
            if (CollectionUtils.isEmpty(activeChannels)) {
                ea.setAllChannelsActive(Boolean.TRUE);
                ea.setAutomaticChannelAssignment(Boolean.FALSE);
                ea.setActive(Boolean.FALSE);
            }
            eventAttendantConfigCouchDao.upsert(String.valueOf(eventId), ea);
        }
        if (!CommonUtils.isEmpty(ea.getCustomConfiguredSessions())) {
            List<Key> sessionIds = getSessionKeys(ea.getCustomConfiguredSessions());
            List<SessionAttendantsConfig> attendants = sessionAttendantConfigCouchDao.bulkGet(sessionIds);
            deleteChannelSessionsAttendantConfig(attendants, channelId);
        }
    }

    public EventAttendantsConfigDTO getEventsAttendantConfig(Long eventId) {
        validateIdentifier(eventId, EVENT_ID);
        EventAttendantsConfig eventAttendantsConfig = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        return EventAttendantConfigConverter.toDTO(eventAttendantsConfig);
    }

    public void upsertEventsAttendantConfig(Long eventId, EventAttendantsConfig eventAttendantsDTO) {
        validateIdentifier(eventId, EVENT_ID);
        if (eventAttendantsDTO == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventAttendantsDTO is mandatory", null);
        }
        validateAttendantsConfigUpdate(eventAttendantsDTO, eventId);
        EventAttendantsConfig eac = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        if (eac != null) {
            updateField(eac::setActive, eventAttendantsDTO.isActive());
            updateField(eac::setAutomaticChannelAssignment, eventAttendantsDTO.isAutomaticChannelAssignment());
            updateField(eac::setAllChannelsActive, eventAttendantsDTO.isAllChannelsActive());
            updateField(eac::setAutofill, eventAttendantsDTO.isAutofill());
            updateField(eac::setAllowEditAutofill, eventAttendantsDTO.isAllowEditAutofill());
            updateField(eac::setAllowAttendantsModification, eventAttendantsDTO.getAllowAttendantsModification());
            eac.setActiveChannels(eventAttendantsDTO.getActiveChannels());
            if (!isAvetSocketEvent(eventId)) {
                updateField(eac::setEditAutofillDisallowedSectors, eventAttendantsDTO.getEditAutofillDisallowedSectors());
            }
        } else {
            eac = eventAttendantsDTO;
        }
        eventAttendantConfigCouchDao.upsert(String.valueOf(eventId), eac);
        createDefaultFields(eventId);
    }

    public void createAvetDefaultEventAttendantsConfig(Long eventId) {
        EventAttendantsConfig eventAttendantsConfig = new EventAttendantsConfig();
        eventAttendantsConfig.setActive(Boolean.TRUE);
        eventAttendantsConfig.setAllChannelsActive(Boolean.TRUE);

        upsertEventsAttendantConfig(eventId, eventAttendantsConfig);
    }

    public SessionAttendantsConfigDTO getSessionAttendantsConfig(Long sessionId) {
        validateIdentifier(sessionId, SESSION_ID);
        SessionAttendantsConfig sessionAttendantsConfig = sessionAttendantConfigCouchDao.get(String.valueOf(sessionId));
        return SessionAttendantConfigConverter.toDTO(sessionAttendantsConfig);
    }

    public void createSessionAttendantsConfig(Long sessionId, Long eventId, SessionAttendantsConfig attendants) {
        validateIdentifier(sessionId, SESSION_ID);
        validateIdentifier(eventId, EVENT_ID);
        if (attendants == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "sessionAttendants is mandatory", null);
        }
        sessionAttendantConfigCouchDao.insert(String.valueOf(sessionId), attendants);
        EventAttendantsConfig eventsAttendant = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        if (eventsAttendant == null) {
            eventsAttendant = createEventAttendantConfig(eventId);
        }
        if (eventsAttendant.getCustomConfiguredSessions() == null) {
            eventsAttendant.setCustomConfiguredSessions(new ArrayList<>());
        }
        eventsAttendant.getCustomConfiguredSessions().add(sessionId);
        eventAttendantConfigCouchDao.upsert(String.valueOf(eventId), eventsAttendant);
        createDefaultFields(eventId);
    }

    public void upsertSessionAttendantsConfig(Long sessionId, Long eventId, SessionAttendantsConfig attendants) {
        validateIdentifier(sessionId, SESSION_ID);
        validateIdentifier(eventId, EVENT_ID);
        if (attendants == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "sessionAttendants is mandatory", null);
        }
        validateAttendantsConfigUpdate(attendants, eventId);
        EventAttendantsConfig eventsAttendant = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        SessionAttendantsConfig sac = sessionAttendantConfigCouchDao.get(String.valueOf(sessionId));

        if (sac != null) {
            updateField(sac::setSessionId, attendants.getSessionId());
            updateField(sac::setActive, attendants.isActive());
            updateField(sac::setAutofill, attendants.isAutofill());
            updateField(sac::setAllowEditAutofill, attendants.isAllowEditAutofill());
            updateField(sac::setActiveChannels, attendants.getActiveChannels());
            updateField(sac::setAutomaticChannelAssignment, attendants.isAutomaticChannelAssignment());
            updateField(sac::setAllChannelsActive, attendants.isAllChannelsActive());
        } else {
            sac = attendants;
        }
        sessionAttendantConfigCouchDao.upsert(String.valueOf(sessionId), sac);

        if (eventsAttendant == null) {
            eventsAttendant = createEventAttendantConfig(eventId);
        }
        if (eventsAttendant.getCustomConfiguredSessions() == null) {
            eventsAttendant.setCustomConfiguredSessions(new ArrayList<>());
        }
        if (!eventsAttendant.getCustomConfiguredSessions().contains(sessionId)) {
            eventsAttendant.getCustomConfiguredSessions().add(sessionId);
            eventAttendantConfigCouchDao.upsert(String.valueOf(eventId), eventsAttendant);
        }
        createDefaultFields(eventId);
    }

    public void deleteSessionAttendantsConfig(Long sessionId, Long eventId) {
        validateIdentifier(sessionId, SESSION_ID);
        validateIdentifier(eventId, EVENT_ID);
        if (existSessionAttendantsConfig(sessionId)) {
            sessionAttendantConfigCouchDao.remove(String.valueOf(sessionId));
        }
        EventAttendantsConfig eventsAttendant = eventAttendantConfigCouchDao.get(String.valueOf(eventId));
        if (eventsAttendant != null && eventsAttendant.getCustomConfiguredSessions() != null &&
                eventsAttendant.getCustomConfiguredSessions().contains(sessionId)) {
            eventAttendantConfigCouchDao.upsert(String.valueOf(eventId), eventsAttendant);
        }
    }

    public void validateAttendantsConfigUpdate(AttendantsConfig attendantsConfig, Long eventId) {
        CpanelEventoRecord eventRecord = eventDao.findById(eventId.intValue());

        boolean isAvetEvent = EventType.AVET.getId().equals(eventRecord.getTipoevento());

        if (BooleanUtils.isTrue(attendantsConfig.isAutofill()) && !isAvetEvent) {
            throw OneboxRestException.builder(MsEventErrorCode.ATTENDANT_CONFIG_AUTOFILL_NOT_ALLOWED).build();
        }
    }

    private boolean isAvetSocketEvent(Long eventId) {
        EventAvetConfig eventAvetConfig = eventAvetConfigCouchDao.get(eventId.toString());

        return eventAvetConfig != null &&
                eventAvetConfig.getIsSocket();
    }

    private void createDefaultFields(Long eventId) {
        List<CpanelEventFieldRecord> attendantsFields = eventFieldDao.getEventFieldByEventAndFieldGroup(eventId.intValue(), FieldGroup.EVENT_ATTENDANT);
        if (CollectionUtils.isEmpty(attendantsFields)) {
            for (DefaultField f : DefaultField.values()) {
                CpanelEventFieldRecord cpanelEventField = createEventField(f, eventId);
                eventFieldDao.insert(cpanelEventField);
            }
        }
    }

    private static CpanelEventFieldRecord createEventField(DefaultField defaultField, Long eventId) {
        CpanelEventFieldRecord cpanelEventField = new CpanelEventFieldRecord();
        cpanelEventField.setEventfieldid(null);
        cpanelEventField.setEventid(eventId.intValue());
        cpanelEventField.setFieldorder(ConverterUtils.intToByte(defaultField.getOrder()));
        cpanelEventField.setFieldid(defaultField.getOrder());
        cpanelEventField.setMaxlength(defaultField.getMaxSize());
        cpanelEventField.setMinlength(1);
        cpanelEventField.setMandatory(ConverterUtils.isTrueAsByte(Boolean.TRUE));
        return cpanelEventField;
    }

    private boolean existSessionAttendantsConfig(Long sessionId) {
        //Could be replaced by exists extending DAO features
        return Objects.nonNull(sessionAttendantConfigCouchDao.get(String.valueOf(sessionId)));
    }

    private static void validateIdentifier(Long anId, String valueName) {
        if (anId == null || anId <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, valueName + " is mandatory", null);
        }
    }

    private void addChannelToSessionsAttendantConfig(List<SessionAttendantsConfig> attendants, Long channelId) {
        for (SessionAttendantsConfig sa : attendants) {
            if (CommonUtils.isTrue(sa.isAutomaticChannelAssignment())) {
                if (sa.getActiveChannels() == null) {
                    sa.setActiveChannels(new ArrayList<>());
                }
                if (!sa.getActiveChannels().contains(channelId)) {
                    sa.getActiveChannels().add(channelId);
                    sessionAttendantConfigCouchDao.upsert(sa.getSessionId().toString(), sa);
                }
            }
        }
    }

    private void deleteChannelSessionsAttendantConfig(List<SessionAttendantsConfig> attendants, Long channelId) {
        for (SessionAttendantsConfig sa : attendants) {
            if (CommonUtils.isTrue(sa.isAutomaticChannelAssignment()) && CollectionUtils.isNotEmpty(sa.getActiveChannels())
                    && sa.getActiveChannels().contains(channelId)) {
                sa.getActiveChannels().remove(channelId);
                if (CollectionUtils.isEmpty(sa.getActiveChannels())) {
                    sa.setAllChannelsActive(Boolean.TRUE);
                    sa.setAutomaticChannelAssignment(Boolean.FALSE);
                    sa.setActive(Boolean.FALSE);
                }
                sessionAttendantConfigCouchDao.upsert(sa.getSessionId().toString(), sa);
            }
        }
    }

    private static List<Key> getSessionKeys(List<Long> sessionIds) {
        return sessionIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(sId)});
            return key;
        }).collect(Collectors.toList());

    }

    private static EventAttendantsConfig createEventAttendantConfig(Long eventId) {
        EventAttendantsConfig eventsAttendant = new EventAttendantsConfig();
        eventsAttendant.setEventId(eventId);
        eventsAttendant.setActive(false);
        eventsAttendant.setAllChannelsActive(true);
        eventsAttendant.setAutofill(false);
        eventsAttendant.setAllowAttendantsModification(false);
        return eventsAttendant;
    }
}

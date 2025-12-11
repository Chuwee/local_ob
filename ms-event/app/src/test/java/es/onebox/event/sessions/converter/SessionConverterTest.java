package es.onebox.event.sessions.converter;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.enums.AccessScheduleType;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionConverterTest {

    @Test
    public void fromSession() {
        assertNull(SessionConverter.toSessionDTO(null, Collections.emptyList(), null));
        SessionRecord record = initRecord();
        SessionDTO sessionDTO = SessionConverter.toSessionDTO(record, Collections.emptyList(), null);
        assertTrue(equals(record, sessionDTO));
    }

    @Test
    public void fromSessionPreview() {
        assertNull(SessionConverter.toSessionDTO(null, Collections.emptyList(), null));
        SessionRecord record = initRecord();
        record.setEstado(SessionStatus.READY.getId());
        record.setIspreview(true);
        SessionDTO sessionDTO = SessionConverter.toSessionDTO(record, Collections.emptyList(), null);
        assertEquals(SessionStatus.PREVIEW, sessionDTO.getStatus());
    }

    @Test
    public void fromSessionReady() {
        assertNull(SessionConverter.toSessionDTO(null, Collections.emptyList(), null));
        SessionRecord record = initRecord();
        record.setEstado(SessionStatus.READY.getId());
        record.setIspreview(false);
        SessionDTO sessionDTO = SessionConverter.toSessionDTO(record, Collections.emptyList(), null);
        assertEquals(SessionStatus.READY, sessionDTO.getStatus());
    }

    @Test
    public void fromUpdateSessionPreview() {
        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setStatus(SessionStatus.READY);
        CpanelSesionRecord cpanelSesionRecord = new CpanelSesionRecord();
        SessionConverter.updateRecord(cpanelSesionRecord, request, new CpanelEventoRecord());
        assertEquals(SessionStatus.READY.getId(), cpanelSesionRecord.getEstado());
        assertFalse(cpanelSesionRecord.getIspreview());
    }

    @Test
    public void fromUpdateSessionReady() {
        UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
        request.setStatus(SessionStatus.PREVIEW);
        CpanelSesionRecord cpanelSesionRecord = new CpanelSesionRecord();
        SessionConverter.updateRecord(cpanelSesionRecord, request, new CpanelEventoRecord());
        assertEquals(SessionStatus.READY.getId(), cpanelSesionRecord.getEstado());
        assertTrue(cpanelSesionRecord.getIspreview());
    }

    private SessionRecord initRecord() {
        SessionRecord record = new SessionRecord();
        record.setIdsesion(ObjectRandomizer.random(Integer.class));
        record.setEstado(SessionStatus.SCHEDULED.getId());
        record.setIdevento(ObjectRandomizer.random(Integer.class));
        record.setTipohorarioaccesos(ObjectRandomizer.random(Byte.class));
        record.setFechainiciosesion(ObjectRandomizer.random(Timestamp.class));
        record.setAperturaaccesos(ObjectRandomizer.random(Timestamp.class));
        record.setTipohorarioaccesos(ObjectRandomizer.random(Byte.class));
        record.setEsabono((byte) 1);

        record.setEntityId(10);
        record.setEventType(3);
        record.setEventPackType(SessionType.SEASON_FREE.getType().byteValue());
        record.setVenueTemplateId(ObjectRandomizer.random(Integer.class));
        record.setVenueCountryId(ObjectRandomizer.random(Integer.class));
        record.setVenueId(ObjectRandomizer.random(Integer.class));

        return record;
    }

    private boolean equals(SessionRecord session, SessionDTO dto) {
        return Objects.equals(session.getIdsesion(), dto.getId().intValue())
                && Objects.equals(session.getNombre(), dto.getName())
                && Objects.equals(SessionStatus.byId(session.getEstado()), dto.getStatus())
                && Objects.equals(session.getIdevento(), dto.getEventId().intValue())
                && Objects.equals(session.getEventName(), dto.getEventName())
                && Objects.equals(session.getEntityId(), dto.getEntityId().intValue())
                && Objects.equals(session.getEntityName(), dto.getEntityName())
                && isTipoAbonoCorrect(session, dto)
                && Objects.equals(session.getVenueTemplateId(), dto.getVenueConfigId().intValue())
                && Objects.equals(session.getVenueTZ(), dto.getTimeZone() == null ? null : dto.getTimeZone().getOlsonId())
                && Objects.equals(session.getVenueTZName(), dto.getTimeZone() == null ? null : dto.getTimeZone().getName())
                && Objects.equals(session.getVenueTZOffset(), dto.getTimeZone() == null ? null : dto.getTimeZone().getOffset())
                && dateEquals(session.getFechainiciosesion(), dto.getDate().getStart())
                && dateEquals(session.getFechafinsesion(), dto.getDate().getEnd())
                && dateEqualsIfNotSpecificScheduleType(session.getAperturaaccesos(), dto.getDate().getAdmissionStart(), session)
                && dateEqualsIfNotSpecificScheduleType(session.getCierreaccesos(), dto.getDate().getAdmissionEnd(), session)
                && (session.getTipohorarioaccesos() == null || Objects.equals(AccessScheduleType.byType(session.getTipohorarioaccesos().intValue()), dto.getAccessScheduleType()))
                && Objects.equals(EventType.byId(session.getEventType()), dto.getEventType());
    }


    private boolean isTipoAbonoCorrect(SessionRecord session, SessionDTO dto) {
        // If null or 0: type is session, else type is given by SessionType.
        return (session.getEventPackType() == null || session.getEventPackType() == 0 && dto.getSessionType() == SessionType.SESSION) ||
                (session.getEventPackType() != null && session.getEventPackType() != 0 && dto.getSessionType() == SessionType.getById(session.getEventPackType().intValue()));
    }

    private boolean dateEquals(Timestamp sessionDate, ZonedDateTime dtoDate) {
        return sessionDate == null || Objects.equals(CommonUtils.timestampToZonedDateTime(sessionDate), dtoDate);
    }

    private boolean dateEqualsIfNotSpecificScheduleType(Timestamp sessionDate, ZonedDateTimeWithRelative dtoDate, SessionRecord session) {
        return dateEquals(sessionDate, ConverterUtils.resolveZonedRelativeDateTimeValue(dtoDate, sessionDate))
                || !AccessScheduleType.SPECIFIC.getType().equals(session.getTipohorarioaccesos().intValue());
    }

}

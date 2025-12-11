package es.onebox.event.seasontickets.converter;

import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeasonTicketStatusConverterTest {

    @Test
    public void fromSessionStatusTest() {

        SessionStatus sStatus = SessionStatus.DELETED;
        Boolean isPreview = false;

        SeasonTicketStatusDTO status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.DELETED, status);

        sStatus = SessionStatus.PLANNED;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.SET_UP, status);

        sStatus = SessionStatus.SCHEDULED;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.SET_UP, status);

        sStatus = SessionStatus.READY;
        isPreview = true;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.PENDING_PUBLICATION, status);

        sStatus = SessionStatus.READY;
        isPreview = false;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.READY, status);

        sStatus = SessionStatus.CANCELLED;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.CANCELLED, status);

        sStatus = SessionStatus.CANCELLED;
        isPreview = true;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.CANCELLED, status);

        sStatus = SessionStatus.IN_PROGRESS;
        isPreview = false;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.READY, status);

        sStatus = SessionStatus.FINALIZED;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.FINISHED, status);

        sStatus = SessionStatus.NOT_ACCOMPLISHED;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.CANCELLED, status);

        sStatus = SessionStatus.CANCELLED_EXTERNAL;
        status = SeasonTicketStatusConverter.fromSessionStatus(sStatus, isPreview);
        assertEquals(SeasonTicketStatusDTO.CANCELLED, status);
    }

    @Test
    public void fromSeasonStatusTest() {
        SeasonTicketStatusDTO status;
        SessionRecord record = new SessionRecord();

        status = SeasonTicketStatusDTO.DELETED;
        SeasonTicketStatusConverter.fromSeasonStatus(status, record);
        assertEquals(SessionStatus.DELETED, SessionStatus.byId(record.getEstado()));
        assertEquals(false, record.getIspreview());

        status = SeasonTicketStatusDTO.PENDING_PUBLICATION;
        SeasonTicketStatusConverter.fromSeasonStatus(status, record);
        assertEquals(SessionStatus.READY, SessionStatus.byId(record.getEstado()));
        assertEquals(true, record.getIspreview());

        status = SeasonTicketStatusDTO.READY;
        SeasonTicketStatusConverter.fromSeasonStatus(status, record);
        assertEquals(SessionStatus.READY, SessionStatus.byId(record.getEstado()));
        assertEquals(false, record.getIspreview());

        status = SeasonTicketStatusDTO.SET_UP;
        SeasonTicketStatusConverter.fromSeasonStatus(status, record);
        assertEquals(SessionStatus.SCHEDULED, SessionStatus.byId(record.getEstado()));
        assertEquals(false, record.getIspreview());

        status = SeasonTicketStatusDTO.CANCELLED;
        SeasonTicketStatusConverter.fromSeasonStatus(status, record);
        assertEquals(SessionStatus.CANCELLED, SessionStatus.byId(record.getEstado()));
        assertEquals(false, record.getIspreview());
    }

    @Test
    public void checkIsDeletedTest() {
        SessionRecord record = new SessionRecord();
        record.setIspreview(false);

        record.setEstado(SessionStatus.READY.getId());
        assertFalse(SeasonTicketStatusConverter.checkIsDeleted(record));

        record.setEstado(SessionStatus.CANCELLED.getId());
        assertFalse(SeasonTicketStatusConverter.checkIsDeleted(record));

        record.setEstado(SessionStatus.SCHEDULED.getId());
        assertFalse(SeasonTicketStatusConverter.checkIsDeleted(record));

        record.setEstado(SessionStatus.DELETED.getId());
        assertTrue(SeasonTicketStatusConverter.checkIsDeleted(record));
    }
}

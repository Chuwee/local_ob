package es.onebox.event.seasontickets.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeasonTicketRecordConverterTest {

    @Test
    public void toRecord() {

        CreateSeasonTicketRequestDTO newSeasonTicket = random(CreateSeasonTicketRequestDTO.class);
        CpanelEventoRecord eventRecord = SeasonTicketRecordConverter.toRecord(newSeasonTicket);

        assertEquals(newSeasonTicket.getName(), eventRecord.getNombre());
        assertEquals(newSeasonTicket.getCategoryId(), eventRecord.getIdtaxonomia());
        assertEquals(newSeasonTicket.getEntityId().intValue(), eventRecord.getIdentidad().intValue());
        assertEquals(newSeasonTicket.getProducerId().intValue(), eventRecord.getIdpromotor().intValue());

        assertEquals(newSeasonTicket.getContactPersonName(), eventRecord.getNombreresponsable());
        assertEquals(newSeasonTicket.getContactPersonSurname(), eventRecord.getApellidosresponsable());
        assertEquals(newSeasonTicket.getContactPersonEmail(), eventRecord.getEmailresponsable());
        assertEquals(newSeasonTicket.getContactPersonPhone(), eventRecord.getTelefonoresponsable());

        assertEquals(EventStatus.READY.getId(), eventRecord.getEstado());
        assertEquals(SessionPackType.UNRESTRICTED.getId(), eventRecord.getTipoabono().intValue());
        assertEquals(0, eventRecord.getAforo().intValue());
        assertEquals((byte) 0, eventRecord.getArchivado().intValue());
        assertEquals((byte) 1, eventRecord.getInvitacionusaplantillaticket().intValue());
        assertEquals((byte) 1, eventRecord.getUsardatosfiscalesproductor().intValue());
    }

    @Test
    public void fromSessionRecord(){

        SessionRecord sessionRecord = generateRandomSessionRecord();
        SeasonTicketDTO  seasonTicketDTO =  new SeasonTicketDTO();
        SeasonTicketRecordConverter.fromSessionRecord(seasonTicketDTO, sessionRecord);
        assertEquals(CommonUtils.timestampToZonedDateTime(sessionRecord.getFechapublicacion()),
                seasonTicketDTO.getChannelPublishingDate());
        assertEquals(CommonUtils.timestampToZonedDateTime(sessionRecord.getFechaventa()),
                seasonTicketDTO.getSalesStartingDate());
        assertEquals(seasonTicketDTO.getStatus(), SeasonTicketStatusDTO.READY);
        assertEquals(CommonUtils.timestampToZonedDateTime(sessionRecord.getFechafinsesion()),
                seasonTicketDTO.getSalesEndDate());
        assertEquals(CommonUtils.isTrue(sessionRecord.getEnventa()),
                seasonTicketDTO.getEnableSales());
        assertEquals(CommonUtils.isTrue(sessionRecord.getPublicado()),
                seasonTicketDTO.getEnableChannels());
    }

    private SessionRecord generateRandomSessionRecord(){
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setFechapublicacion(random(Timestamp.class));
        sessionRecord.setEstado(SessionStatus.READY.getId());
        sessionRecord.setIspreview(false);
        sessionRecord.setFechaventa(random(Timestamp.class));
        sessionRecord.setFechafinsesion(random(Timestamp.class));
        sessionRecord.setEnventa(random(Byte.class));
        sessionRecord.setPublicado(random(Byte.class));
        return sessionRecord;
    }
}

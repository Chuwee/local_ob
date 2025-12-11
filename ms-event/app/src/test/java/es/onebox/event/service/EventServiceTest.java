package es.onebox.event.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.EventTransferTicketDTO;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.TaxModeDTO;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.events.service.EventService;
import es.onebox.event.exception.MSEventNotFoundException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dto.SessionTaxDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    EventDao eventDao;

    @Mock
    TaxDao taxDao;

    @Mock
    SessionDao sessionDao;

    @Mock
    EventConfigService eventConfigService;

    @InjectMocks
    EventService eventService;

    @Test
    public void getTaxesBySession() {
        CpanelImpuestoRecord tax = new CpanelImpuestoRecord();
        tax.setIdimpuesto(4);
        tax.setIdoperadora(1);
        tax.setNombre("Sin Impuesto");
        tax.setDescripcion("Sin Impuesto");
        tax.setValor(0.0);
        tax.setDefecto(null);

        when(taxDao.getTicketTaxBySession(anyLong(), anyLong())).thenReturn(tax);
        when(taxDao.getChargesTaxBySession(anyLong(), anyLong())).thenReturn(tax);

        List<SessionTaxDTO> taxes = eventService.getTaxesBySession(3066L, 182160L);

        Assert.notNull(taxes, "Null result");
        Assert.notEmpty(taxes, "Empty result");
        assertEquals(taxes.get(0).getId(), tax.getIdimpuesto().longValue());

        assertEquals(taxes.get(0).getType(), SessionTaxDTO.SessionTaxType.TICKET_TAX.name());
        assertEquals(taxes.get(1).getType(), SessionTaxDTO.SessionTaxType.CHARGES_TAX.name());
    }

    @Test(expected = MSEventNotFoundException.class)
    public void getTaxesBySessionNotFound() {
        when(taxDao.getTicketTaxBySession(anyLong(), anyLong())).thenThrow(new EmptyResultDataAccessException(1));
        when(taxDao.getChargesTaxBySession(anyLong(), anyLong())).thenThrow(new EmptyResultDataAccessException(1));

        eventService.getTaxesBySession(3066L, 182160L);
    }

    @Test
    public void updateEvent() {
        UpdateEventRequestDTO updateEventRequestDTO = new UpdateEventRequestDTO();
        updateEventRequestDTO.setId(2L);
        updateEventRequestDTO.setInvoicePrefixId(2);
        updateEventRequestDTO.setUseProducerFiscalData(true);

        // null prefix id
        EventRecord eventRecord = new EventRecord();
        eventRecord.setUseSimplifiedInvoice((byte) 1);
        eventRecord.setEstado(EventStatus.IN_PROGRESS.getId());
        VenueRecord venueRecord = new VenueRecord();
        Map.Entry<EventRecord, List<VenueRecord>> eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord, Collections.singletonList(venueRecord));

        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.INVOICE_PREFIX_CANNOT_BE_MODIFIED.toString(), ore.getErrorCode());
        }

        // different prefix id
        eventRecord.setInvoiceprefixid(1);
        eventRecord.setEstado(EventStatus.IN_PROGRESS.getId());
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord, Collections.singletonList(venueRecord));
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.INVOICE_PREFIX_CANNOT_BE_MODIFIED.toString(), ore.getErrorCode());
        }

        // used simplified invoice false
        eventRecord.setInvoiceprefixid(10);
        eventRecord.setEstado(EventStatus.IN_PROGRAMMING.getId());
        eventRecord.setUseSimplifiedInvoice((byte) 1);
        eventVenueRecord = new AbstractMap.SimpleEntry<>(eventRecord, Collections.singletonList(venueRecord));
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.PRODUCER_SIMPLIFIED_INVOICE_FLAG.toString(), ore.getErrorCode());
        }

        // change fiscal data. Not expected any error
        eventRecord.setUsardatosfiscalesproductor((byte) 1);
        eventRecord.setInvoiceprefixid(2);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);

        updateEventRequestDTO.setUseProducerFiscalData(false);
        eventService.updateEvent(updateEventRequestDTO);
        assertTrue(true);

        // change invoice prefix
        eventRecord.setUsardatosfiscalesproductor((byte) 0);
        eventRecord.setInvoiceprefixid(null);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);

        updateEventRequestDTO.setUseProducerFiscalData(null);
        updateEventRequestDTO.setInvoicePrefixId(2);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.SIMPLIFIED_INVOICES_CANNOT_BE_USED.toString(), ore.getErrorCode());
        }

        // change fiscal data
        eventRecord.setUsardatosfiscalesproductor((byte) 1);
        eventRecord.setInvoiceprefixid(2);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);

        updateEventRequestDTO.setUseProducerFiscalData(false);
        updateEventRequestDTO.setInvoicePrefixId(null);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.SIMPLIFIED_INVOICES_CANNOT_BE_USED.toString(), ore.getErrorCode());
        }

        // change both fields
        eventRecord.setUsardatosfiscalesproductor((byte) 1);
        eventRecord.setInvoiceprefixid(null);
        when(eventDao.findEvent(anyLong())).thenReturn(eventVenueRecord);

        updateEventRequestDTO.setUseProducerFiscalData(false);
        updateEventRequestDTO.setInvoicePrefixId(2);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.SIMPLIFIED_INVOICES_CANNOT_BE_USED.toString(), ore.getErrorCode());
        }

        eventRecord.setUsardatosfiscalesproductor((byte) 1);
        eventRecord.setInvoiceprefixid(2);
        eventRecord.setTipoevento(EventType.AVET.getId());
        updateEventRequestDTO.setTaxMode(TaxModeDTO.ON_TOP);
        try {
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.EVENT_TAX_MODE_NOT_ALLOWED.toString(), ore.getErrorCode());
        }

        List<Long> idList = List.of(1L);
        EventTransferTicketDTO eventTransferTicketDTO = new EventTransferTicketDTO();
        eventTransferTicketDTO.setMaxTicketTransfers(1);
        eventTransferTicketDTO.setTransferTicketMinDelayTime(1000);
        eventTransferTicketDTO.setAllowedTransferSessions(idList);
        updateEventRequestDTO.setTransfer(eventTransferTicketDTO);
        updateEventRequestDTO.setAllowTransferTicket(true);
        updateEventRequestDTO.setTaxMode(null);

        eventService.updateEvent(updateEventRequestDTO);
        verify(eventConfigService).updateEventTransferTicketConfig(2L, true, eventTransferTicketDTO);

        try {
            eventTransferTicketDTO.setRestrictTransferBySessions(true);
            eventTransferTicketDTO.setAllowedTransferSessions(null);
            updateEventRequestDTO.setTransfer(eventTransferTicketDTO);
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.EMPTY_TRANSFER_TICKET_SESSION_LIST.toString(), ore.getErrorCode());
        }

        List<CpanelSesionRecord> flatSessions = new ArrayList<>();

        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setEventId(List.of(2L));
        sessionSearchFilter.setIds(idList);
        when(sessionDao.findFlatSessions(sessionSearchFilter)).thenReturn(flatSessions);
        try {
            eventTransferTicketDTO.setRestrictTransferBySessions(true);
            eventTransferTicketDTO.setAllowedTransferSessions(idList);
            updateEventRequestDTO.setTransfer(eventTransferTicketDTO);
            eventService.updateEvent(updateEventRequestDTO);
        } catch (OneboxRestException ore) {
            assertEquals(MsEventErrorCode.INVALID_TRANSFER_TICKET_SESSION_LIST.toString(), ore.getErrorCode());
        }

    }

}

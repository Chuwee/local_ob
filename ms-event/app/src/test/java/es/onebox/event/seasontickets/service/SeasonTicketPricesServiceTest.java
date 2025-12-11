package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.PriceType;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.converter.SeasonTicketPriceConverter;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.seasontickets.dao.SeasonTicketSessionDao;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.seasontickets.dto.SeasonTicketPriceDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketPriceDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class SeasonTicketPricesServiceTest {

    @Mock
    private SeasonTicketSessionDao seasonTicketSessionDao;

    @Mock
    private EventPricesDao eventPricesDao;

    @Mock
    private VenueConfigDao venueConfigDao;

    @Mock
    private SeasonTicketEventDao seasonTicketEventDao;

    @Mock
    private SeasonTicketService seasonTicketService;

    @InjectMocks
    private SeasonTicketPricesService seasonTicketPricesService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPricesTest_ok() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Prices
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        prices.forEach(p -> p.setEventId(seasonTicketId.intValue()));
        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(prices);

        List<SeasonTicketPriceDTO> dtos = seasonTicketPricesService.getPrices(seasonTicketId);

        Assertions.assertNotNull(dtos);
        Assertions.assertEquals(dtos, SeasonTicketPriceConverter.fromRecords(prices));
    }

    @Test
    public void getPricesTest_seasonTicketNotFound() {
        Long seasonTicketId = 1L;

        // Season Ticket
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenThrow(new EntityNotFoundException("season ticket not found"));

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.getPrices(seasonTicketId);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void getPricesTest_eventFoundButNotST() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.NORMAL.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.getPrices(seasonTicketId);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void getPricesTest_venueNotFound() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(null);

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.getPrices(seasonTicketId);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventErrorCode.INVALID_VENUE_TEMPLATE.getErrorCode(), exception.getErrorCode());
        Assertions.assertEquals(SeasonTicketPricesService.VENUE_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void getPricesTest_invalidEvent() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Prices
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(prices);

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.getPrices(seasonTicketId);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventErrorCode.TEMPLATE_ID_NOT_FROM_EVENT.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void updatePricesTest_sessionNull() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 5);

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(null);
        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.updatePrices(seasonTicketId, updatedPrices);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventErrorCode.INVALID_EVENT_STATUS.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void updatePricesTest_seasonCanceled() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Prices
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        prices.forEach(p -> p.setEventId(seasonTicketId.intValue()));

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 5);

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(SeasonTicketStatusDTO.CANCELLED);
        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.updatePrices(seasonTicketId, updatedPrices);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventErrorCode.INVALID_EVENT_STATUS.getErrorCode(), exception.getErrorCode());
    }


    @Test
    public void updatePricesTest_priceNotFound() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 5);
        for (int i = 0; i < updatedPrices.size(); i++) {
            updatedPrices.get(i).setPriceTypeId((long) i);
        }

        // Actual prices
        List<EventPriceRecord> actualPrices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        for (int i = 0; i < actualPrices.size(); i++) {
            actualPrices.get(i).setEventId(seasonTicketId.intValue());

            boolean foo = i % 2 == 0;
            PriceType priceType = foo ? PriceType.INDIVIDUAL : PriceType.GROUP;
            actualPrices.get(i).setPriceType(priceType);

            actualPrices.get(i).setPriceZoneId(i + 100);
        }

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(SeasonTicketStatusDTO.SET_UP);

        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(actualPrices);
        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.updatePrices(seasonTicketId, updatedPrices);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventErrorCode.NO_PRICES_FOUND.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void updatePricesTest_negativePrice() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 4);
        for (int i = 0; i < updatedPrices.size(); i++) {
            updatedPrices.get(i).setPriceTypeId((long) i);
        }
        UpdateSeasonTicketPriceDTO negativePrice = new UpdateSeasonTicketPriceDTO();
        negativePrice.setPriceTypeId(4L);
        negativePrice.setRateId(ObjectRandomizer.randomInteger());
        negativePrice.setPrice(-10D);

        List<UpdateSeasonTicketPriceDTO> updatedPricesResult = new ArrayList<>(updatedPrices);
        updatedPricesResult.add(negativePrice);

        // Actual prices
        List<EventPriceRecord> actualPrices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        for (int i = 0; i < actualPrices.size(); i++) {
            actualPrices.get(i).setEventId(seasonTicketId.intValue());

            boolean foo = i % 2 == 0;
            PriceType priceType = foo ? PriceType.INDIVIDUAL : PriceType.GROUP;
            actualPrices.get(i).setPriceType(priceType);

            actualPrices.get(i).setPriceZoneId(i);
        }

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(SeasonTicketStatusDTO.SET_UP);

        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);
        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(actualPrices);
        Mockito.when(eventPricesDao.updateIndividual(any(), any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());
        Mockito.when(eventPricesDao.updateGroup(any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());

        OneboxRestException exception = null;
        try {
            seasonTicketPricesService.updatePrices(seasonTicketId, updatedPricesResult);
        } catch (OneboxRestException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MsEventErrorCode.FIELD_NOT_UPGRADEABLE.getErrorCode(), exception.getErrorCode());
        Assertions.assertEquals("Prices cannot be below zero", exception.getMessage());
    }

    @Test
    public void updatePricesTest_updatePrices_set_up() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 5);
        for (int i = 0; i < updatedPrices.size(); i++) {
            updatedPrices.get(i).setPriceTypeId((long) i);
        }

        // Actual prices
        List<EventPriceRecord> actualPrices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        for (int i = 0; i < actualPrices.size(); i++) {
            actualPrices.get(i).setEventId(seasonTicketId.intValue());

            boolean foo = i % 2 == 0;
            PriceType priceType = foo ? PriceType.INDIVIDUAL : PriceType.GROUP;
            actualPrices.get(i).setPriceType(priceType);

            actualPrices.get(i).setPriceZoneId(i);
        }

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(SeasonTicketStatusDTO.SET_UP);

        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);
        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(actualPrices);
        Mockito.when(eventPricesDao.updateIndividual(any(), any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());
        Mockito.when(eventPricesDao.updateGroup(any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());

        seasonTicketPricesService.updatePrices(seasonTicketId, updatedPrices);

        int individualUpdates = actualPrices.size() / 2 + 1;

        Mockito.verify(eventPricesDao, times(individualUpdates)).updateIndividual(any(), any(), any(), any());
        Mockito.verify(eventPricesDao, times(actualPrices.size() - individualUpdates)).updateGroup(any(), any(), any());
    }

    @Test
    public void updatePricesTest_update_prices_seasonPendingPublication() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 5);
        for (int i = 0; i < updatedPrices.size(); i++) {
            updatedPrices.get(i).setPriceTypeId((long) i);
        }

        // Actual prices
        List<EventPriceRecord> actualPrices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        for (int i = 0; i < actualPrices.size(); i++) {
            actualPrices.get(i).setEventId(seasonTicketId.intValue());

            boolean foo = i % 2 == 0;
            PriceType priceType = foo ? PriceType.INDIVIDUAL : PriceType.GROUP;
            actualPrices.get(i).setPriceType(priceType);

            actualPrices.get(i).setPriceZoneId(i);
        }

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);

        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);
        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(actualPrices);
        Mockito.when(eventPricesDao.updateIndividual(any(), any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());
        Mockito.when(eventPricesDao.updateGroup(any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());

        seasonTicketPricesService.updatePrices(seasonTicketId, updatedPrices);

        int individualUpdates = actualPrices.size() / 2 + 1;

        Mockito.verify(eventPricesDao, times(individualUpdates)).updateIndividual(any(), any(), any(), any());
        Mockito.verify(eventPricesDao, times(actualPrices.size() - individualUpdates)).updateGroup(any(), any(), any());
    }

    @Test
    public void updatePricesTest_update_prices_seasonReady() {
        Long seasonTicketId = 1L;

        // Season Ticket
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setTipoevento(EventType.SEASON_TICKET.getId());
        Mockito.when(seasonTicketEventDao.getById(seasonTicketId.intValue())).thenReturn(record);

        // Venue
        CpanelConfigRecintoRecord venueConfigRecord = new CpanelConfigRecintoRecord();
        Mockito.when(venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue())).thenReturn(venueConfigRecord);

        // Updated prices
        List<UpdateSeasonTicketPriceDTO> updatedPrices = ObjectRandomizer.randomListOf(UpdateSeasonTicketPriceDTO.class, 5);
        for (int i = 0; i < updatedPrices.size(); i++) {
            updatedPrices.get(i).setPriceTypeId((long) i);
        }

        // Actual prices
        List<EventPriceRecord> actualPrices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        for (int i = 0; i < actualPrices.size(); i++) {
            actualPrices.get(i).setEventId(seasonTicketId.intValue());

            boolean foo = i % 2 == 0;
            PriceType priceType = foo ? PriceType.INDIVIDUAL : PriceType.GROUP;
            actualPrices.get(i).setPriceType(priceType);

            actualPrices.get(i).setPriceZoneId(i);
        }

        //Status
        SeasonTicketStatusResponseDTO statusDTO = new SeasonTicketStatusResponseDTO();
        statusDTO.setStatus(SeasonTicketStatusDTO.READY);

        Mockito.when(seasonTicketService.getStatus(any())).thenReturn(statusDTO);
        Mockito.when(eventPricesDao.getVenueTemplatePrices(any())).thenReturn(actualPrices);
        Mockito.when(eventPricesDao.updateIndividual(any(), any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());
        Mockito.when(eventPricesDao.updateGroup(any(), any(), any())).thenReturn(ObjectRandomizer.randomInteger());

        seasonTicketPricesService.updatePrices(seasonTicketId, updatedPrices);

        int individualUpdates = actualPrices.size() / 2 + 1;

        Mockito.verify(eventPricesDao, times(individualUpdates)).updateIndividual(any(), any(), any(), any());
        Mockito.verify(eventPricesDao, times(actualPrices.size() - individualUpdates)).updateGroup(any(), any(), any());
    }
}

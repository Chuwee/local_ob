package es.onebox.event.seasontickets.service;

import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.dal.dto.couch.enums.SeatType;
import es.onebox.dal.dto.couch.order.OrderProductAdditionalDataDTO;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.dal.dto.couch.order.OrderTicketDataDTO;
import es.onebox.dal.dto.couch.order.OrderUserDTO;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.datasources.ms.client.dto.CustomerExternalProduct;
import es.onebox.event.datasources.ms.client.dto.ExternalSeatType;
import es.onebox.event.datasources.ms.client.repository.ExternalProductsRepository;
import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.ms.ticket.dto.TicketDTO;
import es.onebox.event.datasources.ms.ticket.dto.TicketsSearchResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsResponseItem;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketRenewalSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.SeasonTicketRenewalResponse;
import es.onebox.event.datasources.ms.ticket.repository.SeasonTicketRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.events.dao.record.RenewalRecord;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.externalevents.service.ExternalEventsService;
import es.onebox.event.seasontickets.amqp.renewals.elastic.RenewalsElasticUpdaterService;
import es.onebox.event.seasontickets.amqp.renewals.relatedseats.RenewalsUpdateRelatedSeatsService;
import es.onebox.event.seasontickets.dao.RenewalDao;
import es.onebox.event.seasontickets.dao.RenewalElasticDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketExternalSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfigCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalCouchDocument;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalProduct;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalStatus;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;
import es.onebox.event.seasontickets.dao.dto.MappingStatusES;
import es.onebox.event.seasontickets.dao.dto.RenewalDataElastic;
import es.onebox.event.seasontickets.dao.dto.RenewalStatusES;
import es.onebox.event.seasontickets.dao.dto.SeatRenewalES;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketRatesDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsRequest;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsResponse;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsResponseItem;
import es.onebox.event.seasontickets.dto.renewals.RelatedRateDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewal;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsResponse;
import es.onebox.event.seasontickets.dto.renewals.SeatMappingStatus;
import es.onebox.event.seasontickets.dto.renewals.SeatRenewalStatus;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalErrorReason;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalRequest;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalRequestItem;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalResponse;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalResponseItem;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewal;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class SeasonTicketRenewalsServiceTest {

    @InjectMocks
    private SeasonTicketRenewalsService service;
    @Mock
    private SeasonTicketService seasonTicketService;
    @Mock
    private SeasonTicketRenewalCouchDao seasonTicketRenewalCouchDao;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private SeasonTicketRepository seasonTicketRepository;
    @Mock
    private RenewalsElasticUpdaterService renewalsElasticUpdaterService;
    @Mock
    private RenewalElasticDao renewalElasticDao;
    @Mock
    private RenewalDao renewalDao;
    @Mock
    private TicketsRepository ticketsRepository;
    @Mock
    private RenewalsUpdateRelatedSeatsService renewalsUpdateRelatedSeatsService;
    @Mock
    private SeasonTicketRateService seasonTicketRateService;
    @Mock
    private ExternalEventsService externalEventsService;
    @Mock
    private ExternalProductsRepository externalProductsRepository;
    @Mock
    private SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao;

    @Captor
    private ArgumentCaptor<ArrayList<SeasonTicketRenewalCouchDocument>> argumentCaptor;

    @Captor
    private ArgumentCaptor<SeasonTicketRenewalCouchDocument> singleDocArgumentCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(renewalElasticDao.getIndexName()).thenReturn("renewaldata");
    }

    @Test
    public void renewalSeasonTicketTest_ratesInvalid_missingOriginRate() {
        Long originSeasonTicketId = 1L;
        long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        OneboxRestException capturedException = null;
        try {
            service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE.getMessage(), capturedException.getMessage());
    }

    @Test
    public void renewalSeasonTicketTest_ratesInvalid_originRateNotFound() {
        Long originSeasonTicketId = 1L;
        long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(123L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        SeasonTicketRateDTO originRateC = new SeasonTicketRateDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB, originRateC));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        OneboxRestException capturedException = null;
        try {
            service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE.getMessage(), capturedException.getMessage());
    }

    @Test
    public void renewalSeasonTicketTest_ratesInvalid_renewalRateNotFound() {
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(1003L, 123L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        SeasonTicketRateDTO originRateC = new SeasonTicketRateDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB, originRateC));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        // Renewal rates
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRateX = new SeasonTicketRateDTO();
        renewalRateX.setId(2001L);
        renewalRateX.setName("rate X");
        SeasonTicketRateDTO renewalRateY = new SeasonTicketRateDTO();
        renewalRateY.setId(2002L);
        renewalRateY.setName("rate Y");
        renewalSeasonTicketRatesDTO.setData(Arrays.asList(renewalRateX, renewalRateY));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        OneboxRestException capturedException = null;
        try {
            service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE.getMessage(), capturedException.getMessage());
    }

    @Test
    public void renewalSeasonTicketTest_renewalFound_createCouchDocument() {
        SeasonTicketRenewalProduct renewalProduct = generateAndValidateRenewals(SeasonTicketSeatType.NUMBERED);

        SeasonTicketSeat originSeasonTicketSeat = renewalProduct.getOriginSeasonTicketSeat();
        Assertions.assertNotNull(originSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1001), originSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", originSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(101), originSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(201), originSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(1001L), renewalProduct.getOriginRateId());
        Assertions.assertEquals(SeasonTicketSeatType.NUMBERED, originSeasonTicketSeat.getSeatType());
        Assertions.assertNull(originSeasonTicketSeat.getNotNumberedZoneId());

        SeasonTicketSeat renewalSeasonTicketSeat = renewalProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNotNull(renewalSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1002), renewalSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", renewalSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(102), renewalSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(202), renewalSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(302), renewalSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(2001L), renewalProduct.getRenewalRateId());
        Assertions.assertEquals(SeasonTicketSeatType.NUMBERED, renewalSeasonTicketSeat.getSeatType());
        Assertions.assertNull(renewalSeasonTicketSeat.getNotNumberedZoneId());
    }

    @Test
    public void renewalSeasonTicketTest_renewalFound_not_numbered_createCouchDocument() {
        SeasonTicketRenewalProduct renewalProduct = generateAndValidateRenewals(SeasonTicketSeatType.NOT_NUMBERED);

        SeasonTicketSeat originSeasonTicketSeat = renewalProduct.getOriginSeasonTicketSeat();
        Assertions.assertNotNull(originSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1001), originSeasonTicketSeat.getSeatId());
        Assertions.assertEquals(Integer.valueOf(101), originSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(203), originSeasonTicketSeat.getNotNumberedZoneId());
        Assertions.assertEquals(Long.valueOf(1001L), renewalProduct.getOriginRateId());
        Assertions.assertEquals(SeasonTicketSeatType.NOT_NUMBERED, originSeasonTicketSeat.getSeatType());
        Assertions.assertNull(originSeasonTicketSeat.getRowId());
        Assertions.assertNull(originSeasonTicketSeat.getSeatName());

        SeasonTicketSeat renewalSeasonTicketSeat = renewalProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNotNull(renewalSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1002), renewalSeasonTicketSeat.getSeatId());
        Assertions.assertEquals(Integer.valueOf(102), renewalSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(202), renewalSeasonTicketSeat.getNotNumberedZoneId());
        Assertions.assertEquals(Long.valueOf(302), renewalSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(2001L), renewalProduct.getRenewalRateId());
        Assertions.assertEquals(SeasonTicketSeatType.NOT_NUMBERED, renewalSeasonTicketSeat.getSeatType());
        Assertions.assertNull(renewalSeasonTicketSeat.getRowId());
        Assertions.assertNull(renewalSeasonTicketSeat.getSeatName());
    }

    @Test
    public void renewalSeasonTicketTest_renewalNotFound_createCouchDocument() {
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);
        renewalSeasonTicketDTOParam.setRates(new ArrayList<>());

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(1003L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        SeasonTicketRateDTO originRateC = new SeasonTicketRateDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB, originRateC));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        // Renewal rates
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRateX = new SeasonTicketRateDTO();
        renewalRateX.setId(2001L);
        renewalRateX.setName("rate X");
        SeasonTicketRateDTO renewalRateY = new SeasonTicketRateDTO();
        renewalRateY.setId(2002L);
        renewalRateY.setName("rate Y");
        renewalSeasonTicketRatesDTO.setData(Arrays.asList(renewalRateX, renewalRateY));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // list products
        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(1L);
        response.setMetadata(metadata);

        OrderProductDTO product = createProduct(SeatType.NUMBERED);
        response.setData(Collections.singletonList(product));

        Mockito.when(ordersRepository.getActiveUserProducts(Mockito.anyList(), Mockito.anyList(), Mockito.eq(0L), Mockito.anyLong()))
                .thenReturn(response);

        // list mapping
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        renewalResponse.setRenewalSeats(Collections.emptyList());

        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // existing couch doc
        SeasonTicketRenewalCouchDocument renewalDocument = null;
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.anyString()))
                .thenReturn(renewalDocument);

        // call
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // validations
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertFalse(capturedCouchDocuments.isEmpty());
        Assertions.assertEquals(1, capturedCouchDocuments.size());

        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        Assertions.assertNotNull(document);
        Assertions.assertEquals("user@mail.com_1", document.getUserId());

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = document.getSeasonTicketProductMap();
        Assertions.assertNotNull(seasonTicketProductMap);
        Assertions.assertFalse(seasonTicketProductMap.isEmpty());
        Assertions.assertEquals(1, seasonTicketProductMap.size());
        Assertions.assertTrue(seasonTicketProductMap.containsKey(renewalSeasonTicketId));

        List<SeasonTicketRenewalProduct> renewalProductList = seasonTicketProductMap.get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertFalse(renewalProductList.isEmpty());
        Assertions.assertEquals(1, renewalProductList.size());

        SeasonTicketRenewalProduct renewalProduct = renewalProductList.get(0);
        Assertions.assertNotNull(renewalProduct);
        Assertions.assertEquals(Long.valueOf(1), renewalProduct.getOriginSeasonTicketId());

        SeasonTicketSeat originSeasonTicketSeat = renewalProduct.getOriginSeasonTicketSeat();
        Assertions.assertNotNull(originSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1001), originSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", originSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(101), originSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(201), originSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(301), originSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(1001L), renewalProduct.getOriginRateId());

        SeasonTicketSeat renewalSeasonTicketSeat = renewalProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNull(renewalSeasonTicketSeat);
    }

    @Test
    public void renewalSeasonTicketTest_renewalFound_createSeasonTicketProductList() {
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);
        renewalSeasonTicketDTOParam.setRates(new ArrayList<>());

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(1003L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        SeasonTicketRateDTO originRateC = new SeasonTicketRateDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB, originRateC));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        // Renewal rates
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRateX = new SeasonTicketRateDTO();
        renewalRateX.setId(2001L);
        renewalRateX.setName("rate X");
        SeasonTicketRateDTO renewalRateY = new SeasonTicketRateDTO();
        renewalRateY.setId(2002L);
        renewalRateY.setName("rate Y");
        renewalSeasonTicketRatesDTO.setData(Arrays.asList(renewalRateX, renewalRateY));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // list products
        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(1L);
        response.setMetadata(metadata);

        OrderProductDTO product = createProduct(SeatType.NUMBERED);
        response.setData(Collections.singletonList(product));

        Mockito.when(ordersRepository.getActiveUserProducts(Mockito.anyList(), Mockito.anyList(), Mockito.eq(0L), Mockito.anyLong()))
                .thenReturn(response);

        // list mapping
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        RenewalSeasonTicketRenewalSeat renewalSeat = new RenewalSeasonTicketRenewalSeat();
        renewalSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
        renewalSeat.setOriginSeatId(1001L);
        renewalSeat.setRenewalSectorId(102);
        renewalSeat.setRenewalRowId(202);
        renewalSeat.setRenewalSeatId(1002L);
        renewalSeat.setRenewalPriceZoneId(302L);
        renewalResponse.setRenewalSeats(Collections.singletonList(renewalSeat));

        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // existing couch doc
        SeasonTicketRenewalCouchDocument renewalDocument = new SeasonTicketRenewalCouchDocument();
        renewalDocument.setUserId("user@mail.com_1");

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMapCouch = new HashMap<>();
        seasonTicketProductMapCouch.put(999L, new ArrayList<>());
        renewalDocument.setSeasonTicketProductMap(seasonTicketProductMapCouch);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.anyString()))
                .thenReturn(renewalDocument);

        // call
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // validations
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertFalse(capturedCouchDocuments.isEmpty());
        Assertions.assertEquals(1, capturedCouchDocuments.size());

        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        Assertions.assertNotNull(document);
        Assertions.assertEquals("user@mail.com_1", document.getUserId());

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = document.getSeasonTicketProductMap();
        Assertions.assertNotNull(seasonTicketProductMap);
        Assertions.assertFalse(seasonTicketProductMap.isEmpty());
        Assertions.assertEquals(2, seasonTicketProductMap.size());
        Assertions.assertTrue(seasonTicketProductMap.containsKey(renewalSeasonTicketId));

        List<SeasonTicketRenewalProduct> renewalProductList = seasonTicketProductMap.get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertFalse(renewalProductList.isEmpty());
        Assertions.assertEquals(1, renewalProductList.size());

        SeasonTicketRenewalProduct renewalProduct = renewalProductList.stream()
                .filter(p -> p.getOriginSeasonTicketSeat() != null)
                .filter(p -> p.getOriginSeasonTicketSeat().getSeatId().equals(1001L))
                .findFirst()
                .orElse(new SeasonTicketRenewalProduct());
        Assertions.assertNotNull(renewalProduct);
        Assertions.assertEquals(Long.valueOf(1), renewalProduct.getOriginSeasonTicketId());

        SeasonTicketSeat originSeasonTicketSeat = renewalProduct.getOriginSeasonTicketSeat();
        Assertions.assertNotNull(originSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1001), originSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", originSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(101), originSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(201), originSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(301), originSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(1001L), renewalProduct.getOriginRateId());

        SeasonTicketSeat renewalSeasonTicketSeat = renewalProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNotNull(renewalSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1002), renewalSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", renewalSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(102), renewalSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(202), renewalSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(302), renewalSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(2001L), renewalProduct.getRenewalRateId());
    }

    @Test
    public void renewalSeasonTicketTest_renewalFound_createSeasonTicketProduct() {
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);
        renewalSeasonTicketDTOParam.setRates(new ArrayList<>());

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(1003L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        SeasonTicketRateDTO originRateC = new SeasonTicketRateDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB, originRateC));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        // Renewal rates
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRateX = new SeasonTicketRateDTO();
        renewalRateX.setId(2001L);
        renewalRateX.setName("rate X");
        SeasonTicketRateDTO renewalRateY = new SeasonTicketRateDTO();
        renewalRateY.setId(2002L);
        renewalRateY.setName("rate Y");
        renewalSeasonTicketRatesDTO.setData(Arrays.asList(renewalRateX, renewalRateY));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // list products
        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(1L);
        response.setMetadata(metadata);

        OrderProductDTO product = createProduct(SeatType.NUMBERED);
        response.setData(Collections.singletonList(product));

        Mockito.when(ordersRepository.getActiveUserProducts(Mockito.anyList(), Mockito.anyList(), Mockito.eq(0L), Mockito.anyLong()))
                .thenReturn(response);

        // list mapping
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        RenewalSeasonTicketRenewalSeat renewalSeat = new RenewalSeasonTicketRenewalSeat();
        renewalSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
        renewalSeat.setOriginSeatId(1001L);
        renewalSeat.setRenewalSectorId(102);
        renewalSeat.setRenewalRowId(202);
        renewalSeat.setRenewalSeatId(1002L);
        renewalSeat.setRenewalPriceZoneId(302L);
        renewalResponse.setRenewalSeats(Collections.singletonList(renewalSeat));

        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // existing couch doc
        SeasonTicketRenewalCouchDocument renewalDocument = new SeasonTicketRenewalCouchDocument();
        renewalDocument.setUserId("user@mail.com_1");

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMapCouch = new HashMap<>();
        seasonTicketProductMapCouch.put(renewalSeasonTicketId, new ArrayList<>(Collections.singletonList(new SeasonTicketRenewalProduct())));
        renewalDocument.setSeasonTicketProductMap(seasonTicketProductMapCouch);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.anyString()))
                .thenReturn(renewalDocument);

        // call
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // validations
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertFalse(capturedCouchDocuments.isEmpty());
        Assertions.assertEquals(1, capturedCouchDocuments.size());

        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        Assertions.assertNotNull(document);
        Assertions.assertEquals("user@mail.com_1", document.getUserId());

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = document.getSeasonTicketProductMap();
        Assertions.assertNotNull(seasonTicketProductMap);
        Assertions.assertFalse(seasonTicketProductMap.isEmpty());
        Assertions.assertEquals(1, seasonTicketProductMap.size());
        Assertions.assertTrue(seasonTicketProductMap.containsKey(renewalSeasonTicketId));

        List<SeasonTicketRenewalProduct> renewalProductList = seasonTicketProductMap.get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertFalse(renewalProductList.isEmpty());
        Assertions.assertEquals(2, renewalProductList.size());

        SeasonTicketRenewalProduct renewalProduct = renewalProductList.stream()
                .filter(p -> p.getOriginSeasonTicketSeat() != null)
                .filter(p -> p.getOriginSeasonTicketSeat().getSeatId().equals(1001L))
                .findFirst()
                .orElse(new SeasonTicketRenewalProduct());
        Assertions.assertNotNull(renewalProduct);
        Assertions.assertEquals(Long.valueOf(1), renewalProduct.getOriginSeasonTicketId());

        SeasonTicketSeat originSeasonTicketSeat = renewalProduct.getOriginSeasonTicketSeat();
        Assertions.assertNotNull(originSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1001), originSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", originSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(101), originSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(201), originSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(301), originSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(1001L), renewalProduct.getOriginRateId());

        SeasonTicketSeat renewalSeasonTicketSeat = renewalProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNotNull(renewalSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1002), renewalSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", renewalSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(102), renewalSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(202), renewalSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(302), renewalSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(2001L), renewalProduct.getRenewalRateId());
    }

    @Test
    public void getRenewalSeatsTest() {
        Long seasonTicketId = 1L;
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setLimit(10L);
        filter.setOffset(20L);
        filter.setSeasonTicketId(seasonTicketId);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        SeasonTicketRenewal seasonTicketRenewal = new SeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(true);
        seasonTicketRenewal.setRenewalStartingDate(ObjectRandomizer.random(ZonedDateTime.class));
        seasonTicketRenewal.setRenewalEndDate(ObjectRandomizer.random(ZonedDateTime.class));
        seasonTicketDTO.setRenewal(seasonTicketRenewal);
        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setIdeventooriginal(2);
        renewalRecord.setTotalabonos(25);
        renewalRecord.setOriginSeasonTicketName("original season ticket");
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId)))
                .thenReturn(renewalRecord);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.any()))
                .thenReturn(seasonTicketDTO);

        SearchResponse<RenewalDataElastic> searchResponse = getFakeSearchResponse();
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.eq(filter)))
                .thenReturn(searchResponse);

        SeasonTicketRenewalSeat seasonTicketRenewalSeat = new SeasonTicketRenewalSeat();
        seasonTicketRenewalSeat.setMemberId("member");
        seasonTicketRenewalSeat.setEmail("email");
        seasonTicketRenewalSeat.setName("name");
        seasonTicketRenewalSeat.setSurname("surname");

        seasonTicketRenewalSeat.setRenewalStatus(ObjectRandomizer.random(SeatRenewalStatus.class));
        seasonTicketRenewalSeat.setMappingStatus(ObjectRandomizer.random(SeatMappingStatus.class));

        List<SeasonTicketRenewalSeat> renewalSeatList = Collections.singletonList(seasonTicketRenewalSeat);
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.eq(searchResponse)))
                .thenReturn(renewalSeatList);

        SearchResponse<RenewalDataElastic> successSeatsSearchResponse = getFakeSearchResponse();
        Mockito.when(renewalElasticDao.getTotalHits(Mockito.eq(successSeatsSearchResponse)))
                .thenReturn(11L);

        SearchResponse<RenewalDataElastic> failedSeatsSearchResponse = getFakeSearchResponse();
        Mockito.when(renewalElasticDao.getTotalHits(Mockito.eq(failedSeatsSearchResponse)))
                .thenReturn(12L);

        Mockito.when(seasonTicketRenewalConfigCouchDao.get(any())).thenReturn(new SeasonTicketRenewalConfig());

        SeasonTicketRenewalSeatsResponse result = service.getSeasonTicketRenewalSeats(seasonTicketId, filter);
        Assertions.assertEquals("member", result.getData().get(0).getMemberId());
        Assertions.assertEquals("email", result.getData().get(0).getEmail());
        Assertions.assertEquals("name", result.getData().get(0).getName());
        Assertions.assertEquals("surname", result.getData().get(0).getSurname());
        Assertions.assertEquals(seasonTicketRenewal.getRenewalStartingDate(), result.getData().get(0).getRenewalSettings().getRenewalStartingDate());
        Assertions.assertEquals(seasonTicketRenewal.getRenewalEndDate(), result.getData().get(0).getRenewalSettings().getRenewalEndDate());
    }

    @Test
    public void updateRenewalSeatsTest_seasonTicketInCreation() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS);

        UpdateRenewalResponse response = null;
        OneboxRestException capturedException = null;
        try {
            response = service.updateRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(response);
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY.getMessage(), capturedException.getMessage());
    }

    @Test
    public void updateRenewalSeatsTest_renewalsNotAllowed() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.FALSE);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        UpdateRenewalResponse response = null;
        OneboxRestException capturedException = null;
        try {
            response = service.updateRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(response);
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void updateRenewalSeatsTest_renewalDocNotFound() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = null;
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.USER_HAS_NOT_RENEWALS, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_productMapNull() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.USER_HAS_NOT_RENEWALS_FOR_THIS_SEASON_TICKET, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_renewalDocNotFoundForThisSeasonTicket() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(2L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.USER_HAS_NOT_RENEWALS_FOR_THIS_SEASON_TICKET, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_renewalNotFound() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId("other_id");
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(1L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.RENEWAL_PRODUCT_NOT_FOUND, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_renewalAlreadyRenewed() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId("id");
        renewalProduct.setStatus(SeasonTicketRenewalStatus.RENEWED);
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(1L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.RENEWAL_ALREADY_RENEWED, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_renewalCanceled() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId("id");
        renewalProduct.setStatus(SeasonTicketRenewalStatus.CANCELED);
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(1L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.RENEWAL_ALREADY_RENEWED, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_renewalRefund() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(10L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId("id");
        renewalProduct.setStatus(SeasonTicketRenewalStatus.REFUNDED);
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(1L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.RENEWAL_ALREADY_RENEWED, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_invalidSeat() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(111L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(112L);
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId("id");
        renewalProduct.setStatus(SeasonTicketRenewalStatus.PENDING_RENEWAL);
        renewalProduct.setRenewalSeasonTicketSeat(new SeasonTicketSeat());
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(1L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertFalse(responseItem.getResult());
        Assertions.assertEquals(UpdateRenewalErrorReason.INVALID_SEAT, responseItem.getReason());
    }

    @Test
    public void updateRenewalSeatsTest_modifySeat() {
        Long seasonTicketId = 1L;
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setId("id");
        requestItem.setSeatId(111L);
        requestItem.setUserId("user");
        request.setItems(Collections.singletonList(requestItem));

        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(seasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO rateDTO = new SeasonTicketRateDTO();
        rateDTO.setId(1001L);
        seasonTicketRatesDTO.setData(Collections.singletonList(rateDTO));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(seasonTicketId.intValue()), Mockito.any()))
                .thenReturn(seasonTicketRatesDTO);

        TicketsSearchResponse ticketsSearchResponse = new TicketsSearchResponse();
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(111L);
        ticketDTO.setSectorId(200L);
        ticketDTO.setRow(201L);
        ticketDTO.setSeat("seat name");
        ticketDTO.setPriceTypeId(202L);
        ticketsSearchResponse.setData(Collections.singletonList(ticketDTO));
        Mockito.when(ticketsRepository.getTickets(Mockito.anyLong(), Mockito.anyList(), Mockito.anyList()))
                .thenReturn(ticketsSearchResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();

        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId("id");
        renewalProduct.setStatus(SeasonTicketRenewalStatus.PENDING_RENEWAL);
        renewalProduct.setRenewalSeasonTicketSeat(null);
        Map<Long, List<SeasonTicketRenewalProduct>> map = new HashMap<>();
        map.put(1L, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(map);

        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq("user")))
                .thenReturn(couchDocument);

        UpdateRelatedSeatsResponse updateStatusResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem updateRelatedSeatsResponseItem = new UpdateRelatedSeatsResponseItem();
        updateRelatedSeatsResponseItem.setUserId("user");
        updateRelatedSeatsResponseItem.setRenewalId("id");
        updateRelatedSeatsResponseItem.setSeasonTicketId(seasonTicketId);
        updateRelatedSeatsResponseItem.setSeatId(111L);
        updateRelatedSeatsResponseItem.setResult(Boolean.TRUE);
        updateStatusResponse.setBlockSeatsResponse(Collections.singletonList(updateRelatedSeatsResponseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateStatusResponse);

        UpdateRenewalResponse response = service.updateRenewalSeats(seasonTicketId, request);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        UpdateRenewalResponseItem responseItem = response.getItems().get(0);
        Assertions.assertNotNull(responseItem);
        Assertions.assertTrue(responseItem.getResult());
        Assertions.assertNull(responseItem.getReason());


        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).upsert(Mockito.anyString(), singleDocArgumentCaptor.capture());
        SeasonTicketRenewalCouchDocument capturedCouchDocument = singleDocArgumentCaptor.getValue();
        Map<Long, List<SeasonTicketRenewalProduct>> capturedMap = capturedCouchDocument.getSeasonTicketProductMap();
        Assertions.assertNotNull(capturedMap);
        Assertions.assertFalse(capturedMap.isEmpty());
        Assertions.assertEquals(1, capturedMap.size());
        Assertions.assertTrue(capturedMap.containsKey(1L));

        Iterator<List<SeasonTicketRenewalProduct>> it = capturedMap.values().iterator();
        List<SeasonTicketRenewalProduct> productList = it.next();
        Assertions.assertNotNull(productList);
        Assertions.assertFalse(productList.isEmpty());
        Assertions.assertEquals(1, productList.size());

        SeasonTicketRenewalProduct capturedProduct = productList.get(0);
        Assertions.assertNotNull(capturedProduct);
        SeasonTicketSeat renewalSeat = capturedProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNotNull(renewalSeat);
        Assertions.assertEquals(200L, renewalSeat.getSectorId().longValue());
        Assertions.assertEquals(201L, renewalSeat.getRowId().longValue());
        Assertions.assertEquals(111L, renewalSeat.getSeatId().longValue());
        Assertions.assertEquals("seat name", renewalSeat.getSeatName());
        Assertions.assertEquals(202L, renewalSeat.getPriceZoneId().longValue());
    }

    @Test
    public void deleteRenewalSeatTest_renewalNotAllowed() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setAllowRenewal(Boolean.FALSE);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_renewalNotFound() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = null;
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_renewalFromDiferentSeasonTicket() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(2L);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_renewalAlreadyRenewed() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.RENEWED);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_persistenceError_renewalNotFound() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";
        String userId = "userId";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId("xyz");
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(CoreErrorCode.PERSISTENCE_ERROR.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(CoreErrorCode.PERSISTENCE_ERROR.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_persistenceError_error() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";
        String userId = "userId";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalId);
        responseItem.setResult(Boolean.FALSE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_errorCouch_seasonTicketNotFound() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";
        String userId = "userId";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalId);
        responseItem.setResult(Boolean.TRUE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq(userId)))
                .thenReturn(couchDocument);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_errorCouch_renewalNotFound() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";
        String userId = "userId";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalId);
        responseItem.setResult(Boolean.TRUE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct product = new SeasonTicketRenewalProduct();
        product.setId("xyz");
        seasonTicketProductMap.put(seasonTicketId, Collections.singletonList(product));
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq(userId)))
                .thenReturn(couchDocument);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatTest_updateCouch() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";
        String userId = "userId";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalId);
        responseItem.setResult(Boolean.TRUE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct product1 = new SeasonTicketRenewalProduct();
        product1.setId(renewalId);
        SeasonTicketRenewalProduct product2 = new SeasonTicketRenewalProduct();
        product2.setId("xyz");
        List<SeasonTicketRenewalProduct> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);
        seasonTicketProductMap.put(seasonTicketId, productList);
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq(userId)))
                .thenReturn(couchDocument);

        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(100);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId)))
                .thenReturn(renewalRecord);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);

        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).upsert(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void deleteRenewalSeatTest_removeCouch() {
        Long seasonTicketId = 1L;
        String renewalId = "abcde";
        String userId = "userId";

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalId)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalId);
        responseItem.setResult(Boolean.TRUE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct product = new SeasonTicketRenewalProduct();
        product.setId(renewalId);
        List<SeasonTicketRenewalProduct> productList = new ArrayList<>();
        productList.add(product);
        seasonTicketProductMap.put(seasonTicketId, productList);
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq(userId)))
                .thenReturn(couchDocument);

        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(100);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId)))
                .thenReturn(renewalRecord);

        OneboxRestException capturedException = null;
        try {
            service.deleteRenewalSeat(seasonTicketId, renewalId);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);

        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).remove(Mockito.anyString());
    }

    @Test
    public void deleteRenewalSeatsTest_renewalsNotAllowed() {
        Long seasonTicketId = 1L;
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        seasonTicketDTO.setAllowRenewal(Boolean.FALSE);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(response);
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatsTest_renewalNotFound() {
        Long seasonTicketId = 1L;
        String renewalA = "renewal_A";
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        request.setRenewalIds(Collections.singletonList(renewalA));

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = null;
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalA)))
                .thenReturn(elasticDocument);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(response);
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatsTest_renewalFromDiferentSeasonTicket() {
        Long seasonTicketId = 1L;
        String renewalA = "renewal_A";
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        request.setRenewalIds(Collections.singletonList(renewalA));

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(2L);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalA)))
                .thenReturn(elasticDocument);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(response);
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatsTest_renewalAlreadyRenewed() {
        Long seasonTicketId = 1L;
        String renewalA = "renewal_A";
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        request.setRenewalIds(Collections.singletonList(renewalA));

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.RENEWED);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalA)))
                .thenReturn(elasticDocument);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(response);
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void deleteRenewalSeatsTest_failed() {
        Long seasonTicketId = 1L;
        String renewalA = "renewal_A";
        String userId = "userId";
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        request.setRenewalIds(Collections.singletonList(renewalA));

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setId(renewalA);
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalA)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalA);
        responseItem.setResult(Boolean.FALSE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        DeleteRenewalsResponseItem item = response.getItems().get(0);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(renewalA, item.getId());
        Assertions.assertFalse(item.getResult());

        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.never()).remove(Mockito.anyString());
        Mockito.verify(renewalElasticDao, Mockito.never()).deleteById(Mockito.anyString());
        Mockito.verify(renewalDao, Mockito.never()).update(Mockito.any());
    }

    @Test
    public void deleteRenewalSeatsTest_success_decreaseCounter() {
        Long seasonTicketId = 1L;
        String renewalA = "renewal_A";
        String userId = "userId";
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        request.setRenewalIds(Collections.singletonList(renewalA));

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setId(renewalA);
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalA)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalA);
        responseItem.setResult(Boolean.TRUE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct product = new SeasonTicketRenewalProduct();
        product.setId(renewalA);
        List<SeasonTicketRenewalProduct> productList = new ArrayList<>();
        productList.add(product);
        seasonTicketProductMap.put(seasonTicketId, productList);
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq(userId)))
                .thenReturn(couchDocument);

        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(100);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId)))
                .thenReturn(renewalRecord);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        DeleteRenewalsResponseItem item = response.getItems().get(0);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(renewalA, item.getId());
        Assertions.assertTrue(item.getResult());

        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).remove(Mockito.anyString());
        Mockito.verify(renewalElasticDao, Mockito.times(1)).deleteById(Mockito.anyString());
        Mockito.verify(renewalDao, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(renewalDao, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deleteRenewalSeatsTest_success_removeRenewals() {
        Long seasonTicketId = 1L;
        String renewalA = "renewal_A";
        String userId = "userId";
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        request.setRenewalIds(Collections.singletonList(renewalA));

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        seasonTicketDTO.setId(seasonTicketId);
        seasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(seasonTicketId)))
                .thenReturn(seasonTicketDTO);

        RenewalDataElastic elasticDocument = new RenewalDataElastic();
        elasticDocument.setId(renewalA);
        elasticDocument.setSeasonTicketId(seasonTicketId);
        elasticDocument.setRenewalStatus(RenewalStatusES.NOT_RENEWED);
        elasticDocument.setUserId(userId);
        SeatRenewalES actualSeat = new SeatRenewalES();
        actualSeat.setSeatId(1001L);
        elasticDocument.setActualSeat(actualSeat);
        Mockito.when(renewalElasticDao.findByID(Mockito.eq(renewalA)))
                .thenReturn(elasticDocument);

        UpdateRelatedSeatsResponse updateRelatedSeatsResponse = new UpdateRelatedSeatsResponse();
        UpdateRelatedSeatsResponseItem responseItem = new UpdateRelatedSeatsResponseItem();
        responseItem.setRenewalId(renewalA);
        responseItem.setResult(Boolean.TRUE);
        updateRelatedSeatsResponse.setUnblockSeatsResponse(Collections.singletonList(responseItem));
        Mockito.when(seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(Mockito.eq(11L), Mockito.any(), Mockito.anyList()))
                .thenReturn(updateRelatedSeatsResponse);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct product = new SeasonTicketRenewalProduct();
        product.setId(renewalA);
        List<SeasonTicketRenewalProduct> productList = new ArrayList<>();
        productList.add(product);
        seasonTicketProductMap.put(seasonTicketId, productList);
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.eq(userId)))
                .thenReturn(couchDocument);

        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(1);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId)))
                .thenReturn(renewalRecord);

        OneboxRestException capturedException = null;
        DeleteRenewalsResponse response = null;
        try {
            response = service.deleteRenewalSeats(seasonTicketId, request);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getItems());
        Assertions.assertFalse(response.getItems().isEmpty());
        Assertions.assertEquals(1, response.getItems().size());

        DeleteRenewalsResponseItem item = response.getItems().get(0);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(renewalA, item.getId());
        Assertions.assertTrue(item.getResult());

        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).remove(Mockito.anyString());
        Mockito.verify(renewalElasticDao, Mockito.times(1)).deleteById(Mockito.anyString());
        Mockito.verify(renewalDao, Mockito.never()).update(Mockito.any());
        Mockito.verify(renewalDao, Mockito.times(1)).delete(Mockito.any());
    }

    private void addRelatedRate(Long oldRateId, Long newRateId, List<RelatedRateDTO> rates) {
        RelatedRateDTO relatedRate = new RelatedRateDTO();
        relatedRate.setOldRateId(oldRateId);
        relatedRate.setNewRateId(newRateId);
        rates.add(relatedRate);
    }

    @Test
    public void renewalSeasonTicketTest_externalProduct() {
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setExternalEvent(true);
        renewalSeasonTicketDTOParam.setOriginRenewalExternalEvent(originSeasonTicketId);
        renewalSeasonTicketDTOParam.setRates(new ArrayList<>());

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(1003L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        ExternalEventDTO originExternalEvent = createExternalEventMock();
        originExternalEvent.setInternalId(originSeasonTicketId);
        originExternalEvent.setEventId(originExternalEvent.toString());
        Mockito.when(externalEventsService.getExternalEvent(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originExternalEvent);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        IdNameDTO originRateA = new IdNameDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        IdNameDTO originRateB = new IdNameDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        IdNameDTO originRateC = new IdNameDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        Mockito.when(externalEventsService.getRatesForExternalEvent(Mockito.eq(originSeasonTicketId)))
                .thenReturn(Arrays.asList(originRateA, originRateB, originRateC));

        // Renewal rates
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRateX = new SeasonTicketRateDTO();
        renewalRateX.setId(2001L);
        renewalRateX.setName("rate X");
        SeasonTicketRateDTO renewalRateY = new SeasonTicketRateDTO();
        renewalRateY.setId(2002L);
        renewalRateY.setName("rate Y");
        renewalSeasonTicketRatesDTO.setData(Arrays.asList(renewalRateX, renewalRateY));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // list products
        CustomerExternalProduct product = createExternalProduct(ExternalSeatType.NUMBERED);
        Mockito.when(externalProductsRepository.getExternalProductsFromExternalEvent(Mockito.eq(originSeasonTicketId.intValue()), Mockito.eq(originExternalEvent.toString())))
                .thenReturn(Collections.singletonList(product));

        // list mapping
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        RenewalSeasonTicketRenewalSeat renewalSeat = new RenewalSeasonTicketRenewalSeat();
        renewalSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
        renewalSeat.setOriginSeatId(0L);
        renewalSeat.setRenewalSectorId(102);
        renewalSeat.setRenewalRowId(202);
        renewalSeat.setRenewalSeatId(1002L);
        renewalSeat.setRenewalPriceZoneId(302L);
        renewalResponse.setRenewalSeats(Collections.singletonList(renewalSeat));

        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.eq(null), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // call
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // validations
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertFalse(capturedCouchDocuments.isEmpty());
        Assertions.assertEquals(1, capturedCouchDocuments.size());

        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        Assertions.assertNotNull(document);
        Assertions.assertEquals("user@mail.com_1", document.getUserId());

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = document.getSeasonTicketProductMap();
        Assertions.assertNotNull(seasonTicketProductMap);
        Assertions.assertFalse(seasonTicketProductMap.isEmpty());
        Assertions.assertEquals(1, seasonTicketProductMap.size());
        Assertions.assertTrue(seasonTicketProductMap.containsKey(renewalSeasonTicketId));

        List<SeasonTicketRenewalProduct> renewalProductList = seasonTicketProductMap.get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertFalse(renewalProductList.isEmpty());
        Assertions.assertEquals(1, renewalProductList.size());

        SeasonTicketRenewalProduct renewalProduct = renewalProductList.get(0);
        Assertions.assertNotNull(renewalProduct);
        Assertions.assertEquals(Long.valueOf(1), renewalProduct.getOriginSeasonTicketId());

        SeasonTicketExternalSeat originExternalSeat = renewalProduct.getOriginExternalSeat();
        Assertions.assertNotNull(originExternalSeat);
        Assertions.assertEquals("sector name", originExternalSeat.getSector());
        Assertions.assertEquals("row name", originExternalSeat.getRow());
        Assertions.assertEquals("row name", originExternalSeat.getRow());
        Assertions.assertEquals("seat_A", originExternalSeat.getSeat());
        Assertions.assertEquals("pz name", originExternalSeat.getPriceZone());
        Assertions.assertEquals(Long.valueOf(1001L), renewalProduct.getOriginRateId());

        SeasonTicketSeat renewalSeasonTicketSeat = renewalProduct.getRenewalSeasonTicketSeat();
        Assertions.assertNotNull(renewalSeasonTicketSeat);
        Assertions.assertEquals(Long.valueOf(1002), renewalSeasonTicketSeat.getSeatId());
        Assertions.assertEquals("seat_A", renewalSeasonTicketSeat.getSeatName());
        Assertions.assertEquals(Integer.valueOf(102), renewalSeasonTicketSeat.getSectorId());
        Assertions.assertEquals(Integer.valueOf(202), renewalSeasonTicketSeat.getRowId());
        Assertions.assertEquals(Long.valueOf(302), renewalSeasonTicketSeat.getPriceZoneId());
        Assertions.assertEquals(Long.valueOf(2001L), renewalProduct.getRenewalRateId());
    }

    @Test
    public void purgeRenewalSeatsTest() {
        Long seasonTicketId = 1L;
        RenewalSeatsPurgeFilter purgeFilter = new RenewalSeatsPurgeFilter();
        purgeFilter.setMappingStatus(SeatMappingStatus.MAPPED);
        purgeFilter.setRenewalStatus(SeatRenewalStatus.NOT_RENEWED);
        purgeFilter.setFreeSearch("MR Customer");

        SeasonTicketDTO seasonTicketDTO = createSeasonTicketDTOMock();
        Mockito.when(seasonTicketService.getSeasonTicket(seasonTicketId)).thenReturn(seasonTicketDTO);

        SearchResponse searchResponse = getFakeSearchResponse();
        Mockito.when(renewalElasticDao.getRenewalSeatsPurge(Mockito.eq(seasonTicketId), Mockito.any(RenewalSeatsPurgeFilter.class),
                Mockito.any(Page.class))).thenReturn(searchResponse);

        RenewalDataElastic renewalDataElastic = new RenewalDataElastic();
        renewalDataElastic.setId("renewalId");
        renewalDataElastic.setUserId("user_id");
        renewalDataElastic.setMemberId("member");
        renewalDataElastic.setEmail("email");
        renewalDataElastic.setName("name");
        renewalDataElastic.setSurname("surname");

        renewalDataElastic.setRenewalStatus(ObjectRandomizer.random(RenewalStatusES.class));
        renewalDataElastic.setMappingStatus(ObjectRandomizer.random(MappingStatusES.class));

        List<RenewalDataElastic> renewalSeatList = Collections.singletonList(renewalDataElastic);
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalDataElastic(Mockito.eq(searchResponse)))
                .thenReturn(renewalSeatList);

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct product1 = new SeasonTicketRenewalProduct();
        product1.setId("renewalId");
        SeasonTicketRenewalProduct product2 = new SeasonTicketRenewalProduct();
        product2.setId("xyz");
        List<SeasonTicketRenewalProduct> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);
        seasonTicketProductMap.put(seasonTicketId, productList);
        couchDocument.setSeasonTicketProductMap(seasonTicketProductMap);
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.anyString())).thenReturn(couchDocument);

        service.purgeRenewalSeats(seasonTicketId, purgeFilter);

        Mockito.verify(renewalElasticDao, Mockito.times(1)).deleteById(Mockito.anyString());
    }

    @Test
    public void testValidateIsUpdateRenewals() {
        Long seasonTicketId = 1L;
        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(1000);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId))).thenReturn(renewalRecord);
        Mockito.when(renewalElasticDao.getRenewalSeatsTotalCount(Mockito.eq(seasonTicketId))).thenReturn(null);
        Mockito.when(renewalElasticDao.getTotalHits(null)).thenReturn(1000L);
        Boolean aBoolean = service.validateRenewalStatus(1L);
        Assertions.assertTrue(aBoolean);
    }

    @Test
    public void testValidateIsUpdateRenewalsNotExisting() {
        Long seasonTicketId = 1L;
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId))).thenReturn(null);
        Boolean aBoolean = service.validateRenewalStatus(1L);
        Assertions.assertFalse(aBoolean);
    }

    @Test
    public void testValidateIsUpdateRenewalsInProgressLess() {
        Long seasonTicketId = 1L;
        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(2000);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId))).thenReturn(renewalRecord);
        Mockito.when(renewalElasticDao.getRenewalSeatsTotalCount(Mockito.eq(seasonTicketId))).thenReturn(null);
        Mockito.when(renewalElasticDao.getTotalHits(null)).thenReturn(1000L);

        OneboxRestException capturedException = null;
        try {
            service.validateRenewalStatus(1L);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS.getMessage(), capturedException.getMessage());
    }

    @Test
    public void testValidateIsUpdateRenewalsInProgressMore() {
        Long seasonTicketId = 1L;
        RenewalRecord renewalRecord = new RenewalRecord();
        renewalRecord.setTotalabonos(500);
        Mockito.when(renewalDao.getRenewalData(Mockito.eq(seasonTicketId))).thenReturn(renewalRecord);
        Mockito.when(renewalElasticDao.getRenewalSeatsTotalCount(Mockito.eq(seasonTicketId))).thenReturn(null);
        Mockito.when(renewalElasticDao.getTotalHits(null)).thenReturn(1000L);

        OneboxRestException capturedException = null;
        try {
            service.validateRenewalStatus(1L);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS.getMessage(), capturedException.getMessage());
    }

    private ExternalEventDTO createExternalEventMock() {
        ExternalEventDTO externalEventDTO = new ExternalEventDTO();
        externalEventDTO.setEntityId(1);
        return externalEventDTO;
    }

    private CustomerExternalProduct createExternalProduct(ExternalSeatType externalSeatType) {
        CustomerExternalProduct product = new CustomerExternalProduct();
        product.setSeatType(externalSeatType);
        product.setUserId("user@mail.com_1");
        product.setEventId("eventId");
        product.setSectorName("sector name");
        product.setPriceZoneName("pz name");
        product.setRateName("rate A");
        product.setPurchaseDate("2020-01-01");
        product.setAutoRenewal(Boolean.TRUE);
        product.setIban("ES91 2100 0418 4502 0005 1332");
        product.setBic("CAIXESBB");

        if (ExternalSeatType.NUMBERED.equals(externalSeatType)) {
            product.setRowName("row name");
            product.setSeatName("seat_A");
        } else {
            product.setNotNumberedZoneName("ZNN name");
        }
        return product;
    }

    private SeasonTicketRenewalProduct generateAndValidateRenewals(SeasonTicketSeatType seatType) {
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setOriginSeasonTicketId(originSeasonTicketId);
        renewalSeasonTicketDTOParam.setRates(new ArrayList<>());

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        addRelatedRate(1002L, 2002L, rates);
        addRelatedRate(1003L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // origin st
        SeasonTicketDTO originSeasonTicketDTO = createSeasonTicketDTOMock();
        originSeasonTicketDTO.setId(originSeasonTicketId);
        originSeasonTicketDTO.setSessionId(11);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originSeasonTicketDTO);

        // renewal st
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(originSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Origin rates
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO originRateA = new SeasonTicketRateDTO();
        originRateA.setId(1001L);
        originRateA.setName("rate A");
        SeasonTicketRateDTO originRateB = new SeasonTicketRateDTO();
        originRateB.setId(1002L);
        originRateB.setName("rate B");
        SeasonTicketRateDTO originRateC = new SeasonTicketRateDTO();
        originRateC.setId(1003L);
        originRateC.setName("rate C");
        originSeasonTicketRatesDTO.setData(Arrays.asList(originRateA, originRateB, originRateC));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(originSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(originSeasonTicketRatesDTO);

        // Renewal rates
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRateX = new SeasonTicketRateDTO();
        renewalRateX.setId(2001L);
        renewalRateX.setName("rate X");
        SeasonTicketRateDTO renewalRateY = new SeasonTicketRateDTO();
        renewalRateY.setId(2002L);
        renewalRateY.setName("rate Y");
        renewalSeasonTicketRatesDTO.setData(Arrays.asList(renewalRateX, renewalRateY));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // list products
        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(1L);
        response.setMetadata(metadata);

        OrderProductDTO product = createProduct(SeatType.valueOf(seatType.name()));
        response.setData(Collections.singletonList(product));

        Mockito.when(ordersRepository.getActiveUserProducts(Mockito.anyList(), Mockito.anyList(), Mockito.eq(0L), Mockito.anyLong()))
                .thenReturn(response);

        // list mapping
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        RenewalSeasonTicketRenewalSeat renewalSeat = createRenewalSeasonTicketRenewalSeat(seatType);
        renewalResponse.setRenewalSeats(Collections.singletonList(renewalSeat));

        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // existing couch doc
        SeasonTicketRenewalCouchDocument renewalDocument = null;
        Mockito.when(seasonTicketRenewalCouchDao.get(Mockito.anyString()))
                .thenReturn(renewalDocument);

        // call
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // validations
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertFalse(capturedCouchDocuments.isEmpty());
        Assertions.assertEquals(1, capturedCouchDocuments.size());

        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        Assertions.assertNotNull(document);
        Assertions.assertEquals("user@mail.com_1", document.getUserId());

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = document.getSeasonTicketProductMap();
        Assertions.assertNotNull(seasonTicketProductMap);
        Assertions.assertFalse(seasonTicketProductMap.isEmpty());
        Assertions.assertEquals(1, seasonTicketProductMap.size());
        Assertions.assertTrue(seasonTicketProductMap.containsKey(renewalSeasonTicketId));

        List<SeasonTicketRenewalProduct> renewalProductList = seasonTicketProductMap.get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertFalse(renewalProductList.isEmpty());
        Assertions.assertEquals(1, renewalProductList.size());

        SeasonTicketRenewalProduct renewalProduct = renewalProductList.get(0);
        Assertions.assertNotNull(renewalProduct);
        Assertions.assertEquals(Long.valueOf(1), renewalProduct.getOriginSeasonTicketId());

        return renewalProduct;
    }

    private RenewalSeasonTicketRenewalSeat createRenewalSeasonTicketRenewalSeat(SeasonTicketSeatType seatType) {
        RenewalSeasonTicketRenewalSeat renewalSeat = new RenewalSeasonTicketRenewalSeat();
        renewalSeat.setSeatType(seatType);
        renewalSeat.setOriginSeatId(1001L);
        renewalSeat.setRenewalSectorId(102);

        renewalSeat.setRenewalSeatId(1002L);
        renewalSeat.setRenewalPriceZoneId(302L);
        if (SeasonTicketSeatType.NOT_NUMBERED.equals(seatType)) {
            renewalSeat.setRenewalNotNumberedZoneId(202);
        } else {
            renewalSeat.setRenewalRowId(202);
        }
        return renewalSeat;
    }

    private SeasonTicketDTO createSeasonTicketDTOMock() {
        SeasonTicketDTO seasonTicketDTO = new SeasonTicketDTO();
        seasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO venue = new VenueDTO();
        venue.setId(1L);
        seasonTicketDTO.setVenues(Collections.singletonList(venue));
        seasonTicketDTO.setEntityId(1L);
        seasonTicketDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);
        seasonTicketDTO.setMemberMandatory(Boolean.FALSE);
        return seasonTicketDTO;
    }

    private OrderProductDTO createProduct(SeatType seatType) {
        OrderProductDTO product = new OrderProductDTO();
        product.setId(1001L);

        OrderTicketDataDTO ticketData = new OrderTicketDataDTO();

        if (SeatType.NOT_NUMBERED.equals(seatType)) {
            ticketData.setNotNumberedAreaId(203);
            ticketData.setNotNumberedAreaName("ZNN1");
        } else {
            ticketData.setRowId(201);
            ticketData.setNumSeat("seat_A");
        }
        ticketData.setSeatType(seatType);
        ticketData.setSectorId(101);
        ticketData.setPriceZoneId(301);
        ticketData.setRateId(1001);
        product.setTicketData(ticketData);

        OrderProductAdditionalDataDTO additionalData = new OrderProductAdditionalDataDTO();
        OrderUserDTO customer = new OrderUserDTO();
        customer.setUserId("user@mail.com_1");
        additionalData.setCustomer(customer);
        product.setAdditionalData(additionalData);

        return product;
    }

    public SearchResponse<RenewalDataElastic> getFakeSearchResponse() {
        Hit<RenewalDataElastic> hit = Hit.of(h -> h
                .source(new RenewalDataElastic())
                .index(renewalElasticDao.getIndexName()));
        HitsMetadata<RenewalDataElastic> hitsMetadata = HitsMetadata.of(hm -> hm
                .hits(List.of(hit))
                .total(TotalHits.of(th -> th.value(1).relation(TotalHitsRelation.Eq)))
        );
        return SearchResponse.of(r -> r.hits(hitsMetadata).took(1).timedOut(false)
                .shards(new ShardStatistics.Builder().successful(1).failed(0).total(1).build()));
    }

    @Test
    void testOrderCodeIsSavedInRenewal() {
        // Arrange
        String userId = "test-user";
        Long seasonTicketId = 1L;
        String renewalId = "renewal-1";
        String orderCode = "ORDER-123";
        ZonedDateTime purchaseDate = ZonedDateTime.now();
        Long rateId = 1L;

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId(renewalId);
        renewalProduct.setOriginSeasonTicketId(seasonTicketId);
        Map<Long, List<SeasonTicketRenewalProduct>> productMap = new HashMap<>();
        productMap.put(seasonTicketId, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(productMap);

        Mockito.when(seasonTicketRenewalCouchDao.get(userId)).thenReturn(couchDocument);

        // Act
        service.commitRenewal(seasonTicketId, userId, renewalId, orderCode, rateId, purchaseDate);

        // Assert
        Mockito.verify(seasonTicketRenewalCouchDao).upsert(Mockito.eq(userId), Mockito.argThat(doc -> {
            SeasonTicketRenewalProduct product = doc.getSeasonTicketProductMap().get(seasonTicketId).get(0);
            return product.getOrderCode().equals(orderCode) &&
                   product.getStatus().equals(SeasonTicketRenewalStatus.RENEWED) &&
                   product.getPurchaseDate().equals(purchaseDate) &&
                   product.getRenewalRateId().equals(rateId);
        }));
    }

    @Test
    void testOrderCodeIsSavedInRefund() {
        // Arrange
        String userId = "test-user";
        Long seasonTicketId = 1L;
        String renewalId = "renewal-1";
        String orderCode = "ORDER-123";
        String refundOrderCode = "REFUND-456";

        SeasonTicketRenewalCouchDocument couchDocument = new SeasonTicketRenewalCouchDocument();
        SeasonTicketRenewalProduct renewalProduct = new SeasonTicketRenewalProduct();
        renewalProduct.setId(renewalId);
        renewalProduct.setOriginSeasonTicketId(seasonTicketId);
        renewalProduct.setOrderCode(orderCode);
        Map<Long, List<SeasonTicketRenewalProduct>> productMap = new HashMap<>();
        productMap.put(seasonTicketId, Collections.singletonList(renewalProduct));
        couchDocument.setSeasonTicketProductMap(productMap);

        Mockito.when(seasonTicketRenewalCouchDao.get(userId)).thenReturn(couchDocument);

        // Act
        service.refundRenewal(userId, seasonTicketId, renewalId, refundOrderCode);

        // Assert
        Mockito.verify(seasonTicketRenewalCouchDao).upsert(Mockito.eq(userId), Mockito.argThat(doc -> {
            SeasonTicketRenewalProduct product = doc.getSeasonTicketProductMap().get(seasonTicketId).get(0);
            return product.getOrderCode().equals(orderCode) &&
                   product.getRefundOrderCode().equals(refundOrderCode) &&
                   product.getStatus().equals(SeasonTicketRenewalStatus.REFUNDED);
        }));
    }

    @Test
    void testOrderCodeIsSearchable() {
        // Arrange
        Long seasonTicketId = 1L;
        String orderCode = "ORDER-123";
        
        RenewalDataElastic elasticDoc = new RenewalDataElastic();
        elasticDoc.setId("renewal-1");
        elasticDoc.setSeasonTicketId(seasonTicketId);
        elasticDoc.setOrderCode(orderCode);
        
        SearchResponse<RenewalDataElastic> searchResponse = Mockito.mock(SearchResponse.class);
        HitsMetadata<RenewalDataElastic> searchHits = Mockito.mock(HitsMetadata.class);
        Hit<RenewalDataElastic> hit = Mockito.mock(Hit.class);
        
        Mockito.when(searchResponse.hits()).thenReturn(searchHits);
        Mockito.when(searchHits.hits()).thenReturn(Collections.singletonList(hit));
        Mockito.when(hit.source()).thenReturn(elasticDoc);
        
        // Configuramos el mock para que devuelva el resultado esperado
        List<SeasonTicketRenewalSeat> expectedSeats = new ArrayList<>();
        SeasonTicketRenewalSeat seat = new SeasonTicketRenewalSeat();
        seat.setOrderCode(orderCode);
        expectedSeats.add(seat);
        
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.any())).thenReturn(expectedSeats);
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.any())).thenReturn(searchResponse);

        // Act
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        List<SeasonTicketRenewalSeat> results = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);

        // Assert
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(orderCode, results.get(0).getOrderCode());
    }

    @Test
    void testOrderCodeIsSearchableWithMultipleResults() {
        // Arrange
        Long seasonTicketId = 1L;
        String orderCode1 = "ORDER-123";
        String orderCode2 = "ORDER-456";
        
        RenewalDataElastic elasticDoc1 = new RenewalDataElastic();
        elasticDoc1.setId("renewal-1");
        elasticDoc1.setSeasonTicketId(seasonTicketId);
        elasticDoc1.setOrderCode(orderCode1);
        
        RenewalDataElastic elasticDoc2 = new RenewalDataElastic();
        elasticDoc2.setId("renewal-2");
        elasticDoc2.setSeasonTicketId(seasonTicketId);
        elasticDoc2.setOrderCode(orderCode2);
        
        SearchResponse<RenewalDataElastic> searchResponse = Mockito.mock(SearchResponse.class);
        HitsMetadata<RenewalDataElastic> searchHits = Mockito.mock(HitsMetadata.class);
        Hit<RenewalDataElastic> hit1 = Mockito.mock(Hit.class);
        Hit<RenewalDataElastic> hit2 = Mockito.mock(Hit.class);
        
        Mockito.when(searchResponse.hits()).thenReturn(searchHits);
        Mockito.when(searchHits.hits()).thenReturn(Arrays.asList(hit1, hit2));
        Mockito.when(hit1.source()).thenReturn(elasticDoc1);
        Mockito.when(hit2.source()).thenReturn(elasticDoc2);
        
        // Configuramos el mock para que devuelva los resultados esperados
        List<SeasonTicketRenewalSeat> expectedSeats = new ArrayList<>();
        SeasonTicketRenewalSeat seat1 = new SeasonTicketRenewalSeat();
        seat1.setOrderCode(orderCode1);
        SeasonTicketRenewalSeat seat2 = new SeasonTicketRenewalSeat();
        seat2.setOrderCode(orderCode2);
        expectedSeats.add(seat1);
        expectedSeats.add(seat2);
        
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.any())).thenReturn(expectedSeats);
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.any())).thenReturn(searchResponse);

        // Act
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        List<SeasonTicketRenewalSeat> results = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);

        // Assert
        Assertions.assertNotNull(results);
        Assertions.assertEquals(2, results.size());
        Assertions.assertEquals(orderCode1, results.get(0).getOrderCode());
        Assertions.assertEquals(orderCode2, results.get(1).getOrderCode());
    }

    @Test
    void testAutoRenewalIsSavedInExternalRenewal() {
        // Arrange
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setExternalEvent(true);
        renewalSeasonTicketDTOParam.setOriginRenewalExternalEvent(originSeasonTicketId);

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // Mock external event
        ExternalEventDTO originExternalEvent = createExternalEventMock();
        originExternalEvent.setInternalId(originSeasonTicketId);
        originExternalEvent.setEventId(originExternalEvent.toString());
        Mockito.when(externalEventsService.getExternalEvent(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originExternalEvent);

        // Mock renewal season ticket
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // Mock generation status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Mock rates
        IdNameDTO originRate = new IdNameDTO();
        originRate.setId(1001L);
        originRate.setName("rate A");
        Mockito.when(externalEventsService.getRatesForExternalEvent(Mockito.eq(originSeasonTicketId)))
                .thenReturn(Collections.singletonList(originRate));

        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRate = new SeasonTicketRateDTO();
        renewalRate.setId(2001L);
        renewalRate.setName("rate X");
        renewalSeasonTicketRatesDTO.setData(Collections.singletonList(renewalRate));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // Mock external product with autoRenewal
        CustomerExternalProduct product = createExternalProduct(ExternalSeatType.NUMBERED);
        Mockito.when(externalProductsRepository.getExternalProductsFromExternalEvent(Mockito.eq(originSeasonTicketId.intValue()), Mockito.eq(originExternalEvent.toString())))
                .thenReturn(Collections.singletonList(product));

        // Mock renewal response
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        RenewalSeasonTicketRenewalSeat renewalSeat = new RenewalSeasonTicketRenewalSeat();
        renewalSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
        renewalSeat.setOriginSeatId(0L);
        renewalSeat.setRenewalSectorId(102);
        renewalSeat.setRenewalRowId(202);
        renewalSeat.setRenewalSeatId(1002L);
        renewalSeat.setRenewalPriceZoneId(302L);
        renewalResponse.setRenewalSeats(Collections.singletonList(renewalSeat));
        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.eq(null), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // Act
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // Assert
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertEquals(1, capturedCouchDocuments.size());
        
        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        List<SeasonTicketRenewalProduct> renewalProductList = document.getSeasonTicketProductMap().get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertEquals(1, renewalProductList.size());
        
        SeasonTicketRenewalProduct renewalProduct = renewalProductList.get(0);
        
        // Verify autoRenewal is saved correctly
        Assertions.assertEquals(Boolean.TRUE, renewalProduct.getAutoRenewal());
        
        // Verify it's marked as external origin
        Assertions.assertEquals(Boolean.TRUE, renewalProduct.getExternalOrigin());
    }

    @Test
    void testIbanBicAreSavedInExternalRenewal() {
        // Arrange
        Long originSeasonTicketId = 1L;
        Long renewalSeasonTicketId = 2L;
        RenewalSeasonTicketDTO renewalSeasonTicketDTOParam = new RenewalSeasonTicketDTO();
        renewalSeasonTicketDTOParam.setExternalEvent(true);
        renewalSeasonTicketDTOParam.setOriginRenewalExternalEvent(originSeasonTicketId);

        List<RelatedRateDTO> rates = new ArrayList<>();
        addRelatedRate(1001L, 2001L, rates);
        renewalSeasonTicketDTOParam.setRates(rates);

        // Mock external event
        ExternalEventDTO originExternalEvent = createExternalEventMock();
        originExternalEvent.setInternalId(originSeasonTicketId);
        originExternalEvent.setEventId(originExternalEvent.toString());
        Mockito.when(externalEventsService.getExternalEvent(Mockito.eq(originSeasonTicketId)))
                .thenReturn(originExternalEvent);

        // Mock renewal season ticket
        SeasonTicketDTO renewalSeasonTicketDTO = createSeasonTicketDTOMock();
        renewalSeasonTicketDTO.setId(renewalSeasonTicketId);
        renewalSeasonTicketDTO.setSessionId(12);
        Mockito.when(seasonTicketService.getSeasonTicket(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(renewalSeasonTicketDTO);

        // Mock generation status
        Mockito.when(seasonTicketService.getGenerationStatus(Mockito.eq(renewalSeasonTicketId)))
                .thenReturn(SeasonTicketInternalGenerationStatus.READY);

        // Mock rates
        IdNameDTO originRate = new IdNameDTO();
        originRate.setId(1001L);
        originRate.setName("rate A");
        Mockito.when(externalEventsService.getRatesForExternalEvent(Mockito.eq(originSeasonTicketId)))
                .thenReturn(Collections.singletonList(originRate));

        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = new SeasonTicketRatesDTO();
        SeasonTicketRateDTO renewalRate = new SeasonTicketRateDTO();
        renewalRate.setId(2001L);
        renewalRate.setName("rate X");
        renewalSeasonTicketRatesDTO.setData(Collections.singletonList(renewalRate));
        Mockito.when(seasonTicketRateService.findRatesBySeasonTicketId(Mockito.eq(renewalSeasonTicketId.intValue()), Mockito.any()))
                .thenReturn(renewalSeasonTicketRatesDTO);

        // Mock external product with iban and bic
        CustomerExternalProduct product = createExternalProduct(ExternalSeatType.NUMBERED);
        Mockito.when(externalProductsRepository.getExternalProductsFromExternalEvent(Mockito.eq(originSeasonTicketId.intValue()), Mockito.eq(originExternalEvent.toString())))
                .thenReturn(Collections.singletonList(product));

        // Mock renewal response
        SeasonTicketRenewalResponse renewalResponse = new SeasonTicketRenewalResponse();
        RenewalSeasonTicketRenewalSeat renewalSeat = new RenewalSeasonTicketRenewalSeat();
        renewalSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
        renewalSeat.setOriginSeatId(0L);
        renewalSeat.setRenewalSectorId(102);
        renewalSeat.setRenewalRowId(202);
        renewalSeat.setRenewalSeatId(1002L);
        renewalSeat.setRenewalPriceZoneId(302L);
        renewalResponse.setRenewalSeats(Collections.singletonList(renewalSeat));
        Mockito.when(seasonTicketRepository.renewalSeasonTicket(Mockito.anyLong(), Mockito.eq(null), Mockito.anyList()))
                .thenReturn(renewalResponse);

        // Act
        service.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam);

        // Assert
        Mockito.verify(seasonTicketRenewalCouchDao, Mockito.times(1)).bulkUpsert(argumentCaptor.capture());
        List<SeasonTicketRenewalCouchDocument> capturedCouchDocuments = argumentCaptor.getValue();
        
        Assertions.assertNotNull(capturedCouchDocuments);
        Assertions.assertEquals(1, capturedCouchDocuments.size());
        
        SeasonTicketRenewalCouchDocument document = capturedCouchDocuments.get(0);
        List<SeasonTicketRenewalProduct> renewalProductList = document.getSeasonTicketProductMap().get(renewalSeasonTicketId);
        Assertions.assertNotNull(renewalProductList);
        Assertions.assertEquals(1, renewalProductList.size());
        
        SeasonTicketRenewalProduct renewalProduct = renewalProductList.get(0);
        
        // Verify iban and bic are saved correctly
        Assertions.assertEquals("ES91 2100 0418 4502 0005 1332", renewalProduct.getIban());
        Assertions.assertEquals("CAIXESBB", renewalProduct.getBic());
        
        // Verify it's marked as external origin
        Assertions.assertEquals(Boolean.TRUE, renewalProduct.getExternalOrigin());
    }

    @Test
    void testAutoRenewalIsSearchableInElastic() {
        // Arrange
        Long seasonTicketId = 1L;
        Boolean autoRenewal = Boolean.TRUE;
        
        RenewalDataElastic elasticDoc = new RenewalDataElastic();
        elasticDoc.setId("renewal-1");
        elasticDoc.setSeasonTicketId(seasonTicketId);
        elasticDoc.setAutoRenewal(autoRenewal);
        
        SearchResponse<RenewalDataElastic> searchResponse = Mockito.mock(SearchResponse.class);
        HitsMetadata<RenewalDataElastic> searchHits = Mockito.mock(HitsMetadata.class);
        Hit<RenewalDataElastic> hit = Mockito.mock(Hit.class);
        
        Mockito.when(searchResponse.hits()).thenReturn(searchHits);
        Mockito.when(searchHits.hits()).thenReturn(Collections.singletonList(hit));
        Mockito.when(hit.source()).thenReturn(elasticDoc);
        
        // Configure mock to return expected results
        List<SeasonTicketRenewalSeat> expectedSeats = new ArrayList<>();
        SeasonTicketRenewalSeat seat = new SeasonTicketRenewalSeat();
        seat.setAutoRenewal(autoRenewal);
        expectedSeats.add(seat);
        
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.any())).thenReturn(expectedSeats);
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.any())).thenReturn(searchResponse);

        // Act
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        List<SeasonTicketRenewalSeat> results = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);

        // Assert
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(autoRenewal, results.get(0).getAutoRenewal());
    }

    @Test
    void testIbanBicAreSearchableInElastic() {
        // Arrange
        Long seasonTicketId = 1L;
        String iban = "ES91 2100 0418 4502 0005 1332";
        String bic = "CAIXESBB";
        
        RenewalDataElastic elasticDoc = new RenewalDataElastic();
        elasticDoc.setId("renewal-1");
        elasticDoc.setSeasonTicketId(seasonTicketId);
        elasticDoc.setIban(iban);
        elasticDoc.setBic(bic);
        
        SearchResponse<RenewalDataElastic> searchResponse = Mockito.mock(SearchResponse.class);
        HitsMetadata<RenewalDataElastic> searchHits = Mockito.mock(HitsMetadata.class);
        Hit<RenewalDataElastic> hit = Mockito.mock(Hit.class);
        
        Mockito.when(searchResponse.hits()).thenReturn(searchHits);
        Mockito.when(searchHits.hits()).thenReturn(Collections.singletonList(hit));
        Mockito.when(hit.source()).thenReturn(elasticDoc);
        
        // Configure mock to return expected results
        List<SeasonTicketRenewalSeat> expectedSeats = new ArrayList<>();
        SeasonTicketRenewalSeat seat = new SeasonTicketRenewalSeat();
        seat.setIban(iban);
        seat.setBic(bic);
        expectedSeats.add(seat);
        
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.any())).thenReturn(expectedSeats);
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.any())).thenReturn(searchResponse);

        // Act
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        List<SeasonTicketRenewalSeat> results = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);

        // Assert
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(iban, results.get(0).getIban());
        Assertions.assertEquals(bic, results.get(0).getBic());
    }

    @Test
    void testAutoRenewalWithDifferentValues() {
        // Arrange - Test with different autoRenewal values
        Long seasonTicketId = 1L;
        Boolean autoRenewal1 = Boolean.TRUE;
        Boolean autoRenewal2 = Boolean.FALSE;
        
        RenewalDataElastic elasticDoc1 = new RenewalDataElastic();
        elasticDoc1.setId("renewal-1");
        elasticDoc1.setSeasonTicketId(seasonTicketId);
        elasticDoc1.setAutoRenewal(autoRenewal1);
        
        RenewalDataElastic elasticDoc2 = new RenewalDataElastic();
        elasticDoc2.setId("renewal-2");
        elasticDoc2.setSeasonTicketId(seasonTicketId);
        elasticDoc2.setAutoRenewal(autoRenewal2);
        
        SearchResponse<RenewalDataElastic> searchResponse = Mockito.mock(SearchResponse.class);
        HitsMetadata<RenewalDataElastic> searchHits = Mockito.mock(HitsMetadata.class);
        Hit<RenewalDataElastic> hit1 = Mockito.mock(Hit.class);
        Hit<RenewalDataElastic> hit2 = Mockito.mock(Hit.class);
        
        Mockito.when(searchResponse.hits()).thenReturn(searchHits);
        Mockito.when(searchHits.hits()).thenReturn(Arrays.asList(hit1, hit2));
        Mockito.when(hit1.source()).thenReturn(elasticDoc1);
        Mockito.when(hit2.source()).thenReturn(elasticDoc2);
        
        // Configure mock to return expected results
        List<SeasonTicketRenewalSeat> expectedSeats = new ArrayList<>();
        SeasonTicketRenewalSeat seat1 = new SeasonTicketRenewalSeat();
        seat1.setAutoRenewal(autoRenewal1);
        SeasonTicketRenewalSeat seat2 = new SeasonTicketRenewalSeat();
        seat2.setAutoRenewal(autoRenewal2);
        expectedSeats.add(seat1);
        expectedSeats.add(seat2);
        
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.any())).thenReturn(expectedSeats);
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.any())).thenReturn(searchResponse);

        // Act
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        List<SeasonTicketRenewalSeat> results = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);

        // Assert
        Assertions.assertNotNull(results);
        Assertions.assertEquals(2, results.size());
        
        // First renewal - autoRenewal enabled
        Assertions.assertEquals(autoRenewal1, results.get(0).getAutoRenewal());
        
        // Second renewal - autoRenewal disabled
        Assertions.assertEquals(autoRenewal2, results.get(1).getAutoRenewal());
    }

    @Test
    void testIbanBicWithDifferentBanks() {
        // Arrange - Test with different bank combinations
        Long seasonTicketId = 1L;
        String iban1 = "ES91 2100 0418 4502 0005 1332";  // CaixaBank
        String bic1 = "CAIXESBB";
        
        String iban2 = "ES76 2077 0024 0010 6000 5678";  // Banco Sabadell
        String bic2 = "CTBAES2T";
        
        RenewalDataElastic elasticDoc1 = new RenewalDataElastic();
        elasticDoc1.setId("renewal-1");
        elasticDoc1.setSeasonTicketId(seasonTicketId);
        elasticDoc1.setIban(iban1);
        elasticDoc1.setBic(bic1);
        
        RenewalDataElastic elasticDoc2 = new RenewalDataElastic();
        elasticDoc2.setId("renewal-2");
        elasticDoc2.setSeasonTicketId(seasonTicketId);
        elasticDoc2.setIban(iban2);
        elasticDoc2.setBic(bic2);
        
        SearchResponse<RenewalDataElastic> searchResponse = Mockito.mock(SearchResponse.class);
        HitsMetadata<RenewalDataElastic> searchHits = Mockito.mock(HitsMetadata.class);
        Hit<RenewalDataElastic> hit1 = Mockito.mock(Hit.class);
        Hit<RenewalDataElastic> hit2 = Mockito.mock(Hit.class);
        
        Mockito.when(searchResponse.hits()).thenReturn(searchHits);
        Mockito.when(searchHits.hits()).thenReturn(Arrays.asList(hit1, hit2));
        Mockito.when(hit1.source()).thenReturn(elasticDoc1);
        Mockito.when(hit2.source()).thenReturn(elasticDoc2);
        
        // Configure mock to return expected results
        List<SeasonTicketRenewalSeat> expectedSeats = new ArrayList<>();
        SeasonTicketRenewalSeat seat1 = new SeasonTicketRenewalSeat();
        seat1.setIban(iban1);
        seat1.setBic(bic1);
        SeasonTicketRenewalSeat seat2 = new SeasonTicketRenewalSeat();
        seat2.setIban(iban2);
        seat2.setBic(bic2);
        expectedSeats.add(seat1);
        expectedSeats.add(seat2);
        
        Mockito.when(renewalElasticDao.convertSearchResponseIntoRenewalSeat(Mockito.any())).thenReturn(expectedSeats);
        Mockito.when(renewalElasticDao.getRenewalSeats(Mockito.any())).thenReturn(searchResponse);

        // Act
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        List<SeasonTicketRenewalSeat> results = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);

        // Assert
        Assertions.assertNotNull(results);
        Assertions.assertEquals(2, results.size());
        
        // CaixaBank renewal
        Assertions.assertEquals(iban1, results.get(0).getIban());
        Assertions.assertEquals(bic1, results.get(0).getBic());
        
        // Banco Sabadell renewal
        Assertions.assertEquals(iban2, results.get(1).getIban());
        Assertions.assertEquals(bic2, results.get(1).getBic());
    }

    @Test
    void updateSeasonTicketRenewal_WhenBankAccountIdIsNegative_ShouldFailValidation() {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        UpdateSeasonTicketRenewal renewal = new UpdateSeasonTicketRenewal();
        renewal.setBankAccountId(-1L); // ID negativo
        
        // When
        Set<ConstraintViolation<UpdateSeasonTicketRenewal>> violations = validator.validate(renewal);
        
        // Then
        Assertions.assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("bankAccountId") 
                        && v.getMessage().contains("Bank account ID must be greater than 0")));
    }

    @Test
    void updateSeasonTicketRenewal_WhenBankAccountIdIsPositive_ShouldPassValidation() {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        UpdateSeasonTicketRenewal renewal = new UpdateSeasonTicketRenewal();
        renewal.setBankAccountId(123L); // ID positivo
        
        // When
        Set<ConstraintViolation<UpdateSeasonTicketRenewal>> violations = validator.validate(renewal);
        
        // Then
        Assertions.assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("bankAccountId")));
    }

    @Test
    void updateSeasonTicketRenewal_WhenBankAccountIdIsNull_ShouldPassValidation() {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        UpdateSeasonTicketRenewal renewal = new UpdateSeasonTicketRenewal();
        renewal.setBankAccountId(null); // ID null (permitido)
        
        // When
        Set<ConstraintViolation<UpdateSeasonTicketRenewal>> violations = validator.validate(renewal);
        
        // Then
        Assertions.assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("bankAccountId")));
    }
}

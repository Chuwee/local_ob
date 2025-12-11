package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.amqp.tiermodification.TierModificationMessage;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.SaleGroupDao;
import es.onebox.event.events.dao.TierConfigCouchDao;
import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.TierLimitCouchDao;
import es.onebox.event.events.dao.TierSaleGroupCouchDao;
import es.onebox.event.events.dao.TierSaleGroupDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.LimiteCupoRecord;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.dao.record.TierSaleGroupRecord;
import es.onebox.event.events.domain.TierConfig;
import es.onebox.event.events.dto.TierCommunicationElementDTO;
import es.onebox.event.events.dto.TierCondition;
import es.onebox.event.events.dto.TierCreationRequestDTO;
import es.onebox.event.events.dto.TierPriceTypeAvailabilityDTO;
import es.onebox.event.events.dto.TierUpdateRequestDTO;
import es.onebox.event.events.quartz.ScheduleReflectTierPriceService;
import es.onebox.event.events.request.TiersFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventTierErrorCode;
import es.onebox.event.timezone.dao.TimeZoneDao;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierCupoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class EventTierServiceTest {

    private static final Long ID = 1L;


    @Mock
    private EventDao eventDao;
    @Mock
    private TierDao tierDao;
    @Mock
    private PriceTypeConfigDao priceZoneConfigDao;
    @Mock
    private ScheduleReflectTierPriceService scheduleReflectTierPriceService;
    @Mock
    private SaleGroupDao saleGroupDao;
    @Mock
    private TierSaleGroupDao tierSaleGroupDao;
    @Mock
    private TierSaleGroupCouchDao tierSaleGroupCouchDao;
    @Mock
    private TierLimitCouchDao tierLimitCouchDao;
    @Mock
    private PriceZoneAssignmentDao priceZoneAssignmentDao;
    @Mock
    private RefreshDataService refreshDataService;
    @Mock
    private TimeZoneDao timeZoneDao;
    @Mock
    private TierConfigCouchDao tierConfigCouchDao;
    @Mock
    private VenueTemplateDao venueTemplateDao;
    @Mock
    private VenuesRepository venuesRepository;
    @Mock
    private DefaultProducer tierModificationProducer;

    @InjectMocks
    private EventTierService eventTierService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createEventTier() {
        TierCreationRequestDTO tier = null;
        Long eventId = 200L;
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on empty tier data");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_DATA_MANDATORY.getMessage(), e.getMessage());
        }

        tier = new TierCreationRequestDTO();
        when(eventDao.findById(200)).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on non existing event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage());
        }

        eventId = 100L;
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on non tiered event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage());
        }

        event.setUsetieredpricing((byte) 1);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on tier null price zone");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_PRICE_TYPE_MANDATORY.getMessage(), e.getMessage());
        }
        tier.setPriceTypeId(0L);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on tier zero price zone");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_PRICE_TYPE_MANDATORY.getMessage(), e.getMessage());
        }

        tier.setPriceTypeId(100L);
        CpanelZonaPreciosConfigRecord non_matching_zone = new CpanelZonaPreciosConfigRecord();
        non_matching_zone.setIdzona(157);
        when(priceZoneConfigDao.getPriceZoneByEventId(eventId)).thenReturn(Collections.singletonList(non_matching_zone));
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on tier price zone not belonging to the event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_PRICE_TYPE_BELONG_TO_EVENT.getMessage(), e.getMessage());
        }

        CpanelZonaPreciosConfigRecord matching_zone = new CpanelZonaPreciosConfigRecord();
        matching_zone.setIdzona(100);
        when(priceZoneConfigDao.getPriceZoneByEventId(eventId)).thenReturn(Collections.singletonList(matching_zone));
        CpanelTimeZoneGroupRecord tzRecord = new CpanelTimeZoneGroupRecord();
        tzRecord.setZoneid(44);
        when(timeZoneDao.findByPriceZone(tier.getPriceTypeId().intValue())).thenReturn(tzRecord);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on null tier name");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_MANDATORY.getMessage(), e.getMessage());
        }

        tier.setName("");
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on empty tier name");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_MANDATORY.getMessage(), e.getMessage());
        }

        tier.setName("long_tier_name-long_tier_name-long_tier_name-long_tier_name");
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on long tier name");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_MAX_LENGTH.getMessage(), e.getMessage());
        }

        tier.setName("existing_name");
        when(tierDao.countByZoneAndName(tier.getPriceTypeId().intValue(), "existing_name")).thenReturn(1L);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on existing tier name for price zone");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_UNIQUE.getMessage(), e.getMessage());
        }

        tier.setName("non_existing_name");
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on null tier price");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_PRICE_MANDATORY.getMessage(), e.getMessage());
        }

        tier.setPrice(-1.0);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on negative tier price");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_PRICE_POSITIVE.getMessage(), e.getMessage());
        }

        tier.setPrice(1.0);
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on null tier start date");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_START_DATE_MANDATORY.getMessage(), e.getMessage());
        }

        tier.setPrice(1.0);
        ZonedDateTime now = ZonedDateTime.now();
        tier.setStartDate(now);
        event.setFechafin(CommonUtils.zonedDateTimeToTimestamp(now.minusDays(1)));
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on tier start date after event end date");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_START_DATE_AFTER_EVENT.getMessage(), e.getMessage());
        }


        event.setFechafin(CommonUtils.zonedDateTimeToTimestamp(now.plusDays(1)));
        when(tierDao.findByZoneAndStartDate(tier.getPriceTypeId().intValue(),
                CommonUtils.zonedDateTimeToTimestamp(tier.getStartDate()))).thenReturn(new CpanelTierRecord());
        try {
            eventTierService.createEventTier(eventId, tier);
            fail("Fail on tier start date repeated for price zone");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_START_DATE_ALREADY_EXISTS.getMessage(), e.getMessage());
        }

        when(tierDao.findByZoneAndStartDate(tier.getPriceTypeId().intValue(),
                CommonUtils.zonedDateTimeToTimestamp(tier.getStartDate()))).thenReturn(null);
        CpanelTierRecord tierRecord = new CpanelTierRecord();
        tierRecord.setIdtier(1);
        tierRecord.setIdzona(100);
        when(tierDao.insert(any())).thenReturn(tierRecord);
        eventTierService.createEventTier(eventId, tier);
    }

    @Test
    public void getEventTiers() {
        Long eventId = 2L;
        when(eventDao.findById(eventId.intValue())).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.getEventTiers(eventId, null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage());
        }

        eventId = 3L;
        EventRecord event = new EventRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.getEventTiers(eventId, null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage());
        }

        event.setUsetieredpricing((byte) 1);
        TiersFilter filter = new TiersFilter();

        eventTierService.getEventTiers(eventId, filter);
    }

    @Test
    public void deleteEventTier() {
        Long eventId = 2L;
        Long tierId = null;
        when(eventDao.findById(eventId.intValue())).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.deleteEventTier(eventId, tierId);
            fail("On non existing event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage());
        }

        eventId = 3L;
        EventRecord event = new EventRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.getEventTiers(eventId, null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage());
        }
        event.setUsetieredpricing((byte) 1);

        try {
            eventTierService.deleteEventTier(eventId, tierId);
            fail("On null tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = -1L;
        try {
            eventTierService.deleteEventTier(eventId, tierId);
            fail("On negative tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = 0L;
        try {
            eventTierService.deleteEventTier(eventId, tierId);
            fail("On zero tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = 1L;
        when(tierDao.findById(tierId.intValue())).thenReturn(null);
        try {
            eventTierService.deleteEventTier(eventId, tierId);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage());
        }

        CpanelTierRecord foundRecord = new CpanelTierRecord();
        foundRecord.setIdzona(100);
        when(tierDao.findById(tierId.intValue())).thenReturn(foundRecord);

        List<TierRecord> foundTiers = generateTiers(20, 30, 40);

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        try {
            eventTierService.deleteEventTier(eventId, tierId);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage());
        }
        foundTiers.addAll(generateTiers(tierId.intValue()));
        CpanelTierRecord cpanelTierRecord = new CpanelTierRecord();
        cpanelTierRecord.setCondicion(1);
        cpanelTierRecord.setIdtier(1);
        cpanelTierRecord.setFechaInicio(Timestamp.from(Instant.EPOCH));
        cpanelTierRecord.setIdzona(1);
        cpanelTierRecord.setPrecio(20.0);
        when(tierDao.findByPriceType(anyInt())).thenReturn(Collections.singletonList(cpanelTierRecord));
        CpanelConfigRecintoRecord venueTemplate = new CpanelConfigRecintoRecord();
        venueTemplate.setIdconfiguracion(1);
        when(venueTemplateDao.findByPriceTypeId(anyInt())).thenReturn(venueTemplate);
        eventTierService.deleteEventTier(eventId, tierId);
    }

    @Test
    public void updateEventTier() {
        TierUpdateRequestDTO tier = null;
        Long eventId = 200L;
        Long tierId = null;
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on empty tier data");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_DATA_MANDATORY.getMessage(), e.getMessage());
        }

        tier = new TierUpdateRequestDTO();
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("On null tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = -1L;
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("On negative tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = 0L;
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("On zero tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = 1L;
        when(tierDao.findById(tierId.intValue())).thenReturn(null);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage());
        }
        CpanelTierRecord tierRecord = new CpanelTierRecord();
        tierRecord.setIdzona(100);
        tierRecord.setIdtier(tierId.intValue());
        tierRecord.setNombre("old_name");
        tierRecord.setFechaInicio(Timestamp.from(new Date().toInstant()));
        when(tierDao.findById(tierId.intValue())).thenReturn(tierRecord);
        when(tierDao.update(any())).thenReturn(tierRecord);
        when(eventDao.findById(200)).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on non existing event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage());
        }

        eventId = 100L;
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on non tiered event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage());
        }

        event.setUsetieredpricing((byte) 1);
        List<TierRecord> foundTiers = generateTiers(20, 30, 40);

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage());
        }

        foundTiers.addAll(generateTiers(tierId.intValue()));
        tier.setName("");
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on empty tier name");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_MANDATORY.getMessage(), e.getMessage());
        }

        tier.setName("long_tier_name-long_tier_name-long_tier_name-long_tier_name");
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on long tier name");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_MAX_LENGTH.getMessage(), e.getMessage());
        }

        tier.setName("existing_name");
        when(tierDao.countByZoneAndName(tierRecord.getIdzona(), "existing_name")).thenReturn(1L);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on existing tier name for price zone");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NAME_UNIQUE.getMessage(), e.getMessage());
        }


        tier.setName("non_existing_name");
        tier.setCondition(TierCondition.STOCK_OR_DATE);
        tier.setLimit(1);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
        } catch (OneboxRestException e) {
            fail("Do not fail on null tier price");
        }

        tier.setPrice(-1.0);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on negative tier price");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_PRICE_POSITIVE.getMessage(), e.getMessage());
        }

        tier.setPrice(1.0);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
        } catch (OneboxRestException e) {
            fail("Do not fail on null tier start date");
        }

        tier.setPrice(1.0);
        ZonedDateTime now = ZonedDateTime.now();
        tier.setStartDate(now);
        event.setFechafin(CommonUtils.zonedDateTimeToTimestamp(now.minusDays(1)));
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Tier date cannot be after event ends");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_START_DATE_AFTER_EVENT.getMessage(), e.getMessage());
        }

        event.setFechafin(CommonUtils.zonedDateTimeToTimestamp(now.plusDays(1)));
        when(tierDao.findByZoneAndStartDate(tierRecord.getIdzona(),
                CommonUtils.zonedDateTimeToTimestamp(tier.getStartDate()))).thenReturn(new CpanelTierRecord());
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on tier start date repeated for price zone");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_START_DATE_ALREADY_EXISTS.getMessage(), e.getMessage());

        }

        when(tierDao.findByZoneAndStartDate(tierRecord.getIdzona(),
                CommonUtils.zonedDateTimeToTimestamp(tier.getStartDate()))).thenReturn(null);

        tier.setLimit(-1);
        try {
            eventTierService.updateEventTier(eventId, tierId, tier);
            fail("Fail on negative limit");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.LIMIT_BELOW_ZERO.getMessage(), e.getMessage());

        }

        tier.setLimit(1);

        eventTierService.updateEventTier(eventId, tierId, tier);
    }

    @Test
    public void deleteEventTierLimit() {
        Long eventId = 2L;
        Long tierId = null;
        when(eventDao.findById(eventId.intValue())).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On non existing event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage());
        }

        eventId = 3L;
        EventRecord event = new EventRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.deleteEventTierLimit(eventId, null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage());
        }
        event.setUsetieredpricing((byte) 1);

        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On null tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = -1L;
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On negative tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = 0L;
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On zero tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage());
        }

        tierId = 1L;
        when(tierDao.findById(tierId.intValue())).thenReturn(null);
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on tier not found");
        }

        CpanelTierRecord foundRecord = new CpanelTierRecord();
        foundRecord.setIdtier(tierId.intValue());
        foundRecord.setIdzona(100);
        when(tierDao.findById(tierId.intValue())).thenReturn(foundRecord);

        List<TierRecord> foundTiers = generateTiers(20, 30, 40);

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on tier not belonging to event");
        }
        foundTiers.addAll(generateTiers(tierId.intValue()));
        eventTierService.deleteEventTierLimit(eventId, tierId);

    }

    @Test
    public void getEventTier() {
        Long eventId = 200L;
        Long tierId = -1L;
        when(eventDao.findById(200)).thenThrow(new EntityNotFoundException(""));

        try {
            eventTierService.getEventTier(eventId, tierId);
            fail("Fail on non existing event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existing event");
        }

        eventId = 100L;
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.getEventTier(eventId, tierId);
            fail("Fail on non tiered event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage(), "Fail on non tiered event");
        }


        event.setUsetieredpricing((byte) 1);
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On negative tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on negative tierId");
        }

        tierId = 0L;
        try {
            eventTierService.deleteEventTierLimit(eventId, tierId);
            fail("On zero tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on zero tierId");
        }

        tierId = 1L;
        CpanelTierRecord tierRecord = new CpanelTierRecord();
        tierRecord.setIdzona(100);
        tierRecord.setIdtier(tierId.intValue());
        when(tierDao.findById(tierId.intValue())).thenReturn(tierRecord);
        List<TierRecord> foundTiers = generateTiers(20, 30, 40);
        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        try {
            eventTierService.getEventTier(eventId, tierId);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on tier not found");
        }
        foundTiers.addAll(generateTiers(tierId.intValue()));
        TierRecord tier = new TierRecord();
        tier.setIdtier(tierId.intValue());
        tier.setIdzona(1234);
        tier.setCondicion(0);
        when(tierDao.getTier(tierId.intValue())).thenReturn(tier);

        eventTierService.getEventTier(eventId, tierId);
    }

    @Test
    public void createEventTierSaleGroup() {
        Long tierId = 1L;
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, null);
            fail("On null limit, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.INVALID_SALE_GROUP_LIMIT.getMessage(), e.getMessage(), "Fail on null tierId");
        }
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, -1);
            fail("On negative limit, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.INVALID_SALE_GROUP_LIMIT.getMessage(), e.getMessage(), "Fail on null tierId");
        }
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(anyInt())).thenReturn(event);
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On non tiered event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage(), "Fail on non tiered event");
        }
        event.setUsetieredpricing((byte) 1);
        tierId = null;
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On null tier id, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on null tier id");
        }
        tierId = -1L;
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On negative tier id, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on negative tier id");
        }
        when(tierDao.getById(anyInt())).thenReturn(null);
        tierId = 1L;
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On non existent tier, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existent tier");
        }
        tierId = 2L;
        TierRecord tier = new TierRecord();
        tier.setIdtier(tierId.intValue());
        when(tierDao.findByEventId(anyInt(), isNull(), isNull(), isNull())).thenReturn(Arrays.asList(tier));
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On non existent tier, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existent tier");
        }
        tierId = 1L;
        tier.setIdtier(tierId.intValue());
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, null, 1);
            fail("On non existent tier, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existent tier");
        }

        CpanelTierRecord cpanelTierRecord = new CpanelTierRecord();
        cpanelTierRecord.setIdtier(tierId.intValue());
        cpanelTierRecord.setIdzona(100);

        when(tierDao.findById(anyInt())).thenReturn(cpanelTierRecord);
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, -1L, 1);
            fail("On non existent sale group, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.SALEGROUP_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on non existent sale group");
        }
        when(saleGroupDao.findById(anyInt())).thenReturn(null);
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On non existent sale group, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.SALEGROUP_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existent sale group");
        }
        CpanelCuposConfigRecord saleGroup = new CpanelCuposConfigRecord();
        saleGroup.setIdconfiguracion(5);
        when(saleGroupDao.findById(anyInt())).thenReturn(saleGroup);
        CpanelZonaPreciosConfigRecord venueTemplate = new CpanelZonaPreciosConfigRecord();
        venueTemplate.setIdconfiguracion(1);
        when(priceZoneConfigDao.getById(anyInt())).thenReturn(venueTemplate);
        cpanelTierRecord.setIdzona(1);
        try {
            eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On sale group template not mathing tier template, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.SALEGROUP_TEMPLATE_NOT_MATCHING_TIER_TEMPLATE.getMessage(), e.getMessage(),
                    "On sale group template not mathing tier template");
        }
        venueTemplate.setIdconfiguracion(5);
        eventTierService.createEventTierSaleGroup(1L, tierId, 1L, 1);
    }

    @Test
    public void updateEventTierSaleGroup() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 1);
        when(eventDao.findById(anyInt())).thenReturn(event);
        Long tierId = 1L;
        CpanelTierRecord cpanelTierRecord = new CpanelTierRecord();
        cpanelTierRecord.setIdtier(tierId.intValue());
        cpanelTierRecord.setIdzona(100);
        when(tierDao.findById(anyInt())).thenReturn(cpanelTierRecord);
        when(tierSaleGroupDao.getByTierAndSaleGroup(anyInt(), anyInt())).thenReturn(new CpanelTierCupoRecord());
        TierRecord tierRecord = new TierRecord();
        tierRecord.setIdtier(tierId.intValue());
        when(tierDao.findByEventId(anyInt(), isNull(), isNull(), isNull())).thenReturn(Arrays.asList(tierRecord));
        try {
            eventTierService.updateEventTierSaleGroup(1L, tierId, 1L, -1);
            fail("On invalid limit, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.INVALID_SALE_GROUP_LIMIT.getMessage(), e.getMessage(),
                    "On sale group template not mathing tier template");
        }
        when(tierSaleGroupDao.getByTierAndSaleGroup(anyInt(), anyInt())).thenReturn(null);
        try {
            eventTierService.updateEventTierSaleGroup(1L, tierId, 1L, 1);
            fail("On non existent sale group, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.SALE_GROUP_TIER_NOT_FOUND.getMessage(), e.getMessage(),
                    "On non existent sale group");
        }
        CpanelTierCupoRecord tierSaleGroup = new CpanelTierCupoRecord();
        tierSaleGroup.setLimite(10);
        when(tierSaleGroupDao.getByTierAndSaleGroup(anyInt(), anyInt())).thenReturn(tierSaleGroup);

        eventTierService.updateEventTierSaleGroup(1L, tierId, 1L, 1);
    }

    @Test
    public void deleteEventTierSaleGroup() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 1);
        when(eventDao.findById(anyInt())).thenReturn(event);
        Long tierId = 1L;
        CpanelTierRecord cpanelTierRecord = new CpanelTierRecord();
        cpanelTierRecord.setIdtier(tierId.intValue());
        cpanelTierRecord.setIdzona(100);
        when(tierDao.findById(anyInt())).thenReturn(cpanelTierRecord);
        when(tierSaleGroupDao.getByTierAndSaleGroup(anyInt(), anyInt())).thenReturn(new CpanelTierCupoRecord());
        TierRecord tierRecord = new TierRecord();
        tierRecord.setIdtier(tierId.intValue());
        when(tierDao.findByEventId(anyInt(), isNull(), isNull(), isNull())).thenReturn(Arrays.asList(tierRecord));
        when(tierSaleGroupDao.getByTierAndSaleGroup(anyInt(), anyInt())).thenReturn(null);
        try {
            eventTierService.deleteEventTierSaleGroup(1L, tierId, 1L);
            fail("On non existent sale group, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.SALE_GROUP_TIER_NOT_FOUND.getMessage(), e.getMessage(), "On non existent sale group");
        }
        when(tierSaleGroupDao.getByTierAndSaleGroup(anyInt(), anyInt())).thenReturn(new CpanelTierCupoRecord());

        eventTierService.deleteEventTierSaleGroup(1L, tierId, 1L);
    }

    @Test
    public void getTierSaleGroupsAvailabilities() {

        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 1);

        when(eventDao.findById(anyInt())).thenReturn(event);

        TierRecord tier1 = new TierRecord();
        tier1.setIdtier(1);
        tier1.setNombre("Tier1");
        tier1.setIdzona(1);
        tier1.setPriceTypeName("Zona precio");
        tier1.setFechaInicio(Timestamp.from(Instant.EPOCH));
        tier1.setPrecio(20.0);
        tier1.setVenta((byte) 1);
        tier1.setLimite(10);
        tier1.setCondicion(0);

        TierRecord tier2 = new TierRecord();
        tier2.setIdtier(2);
        tier2.setNombre("Tier2");
        tier2.setIdzona(2);
        tier2.setPriceTypeName("Zona precio 2");
        tier2.setFechaInicio(Timestamp.from(Instant.EPOCH));
        tier2.setPrecio(30.0);
        tier2.setVenta((byte) 1);
        tier2.setLimite(15);
        tier2.setCondicion(0);

        when(tierDao.getByEventId(anyInt())).thenReturn(Arrays.asList(tier1, tier2));


        Integer idCupo = random(Integer.class);
        TierSaleGroupRecord tierSaleGroup1 = new TierSaleGroupRecord();
        tierSaleGroup1.setIdcupo(idCupo);
        tierSaleGroup1.setIdtier(tier1.getIdtier());
        tierSaleGroup1.setPriceTypeId(tier1.getIdzona());
        tierSaleGroup1.setTierLimit(tier1.getLimite());

        TierSaleGroupRecord tierSaleGroup2 = new TierSaleGroupRecord();
        tierSaleGroup2.setIdcupo(idCupo);
        tierSaleGroup2.setIdtier(tier2.getIdtier());
        tierSaleGroup2.setPriceTypeId(tier2.getIdzona());
        tierSaleGroup2.setTierLimit(tier2.getLimite());

        when(tierSaleGroupDao.getByTierIds(anyCollection())).thenReturn(Arrays.asList(tierSaleGroup1, tierSaleGroup2));

        Long availability = random(Long.class);
        when(tierSaleGroupCouchDao.get(anyString(), anyString())).thenReturn(availability);

        List<TierPriceTypeAvailabilityDTO> tierSaleGroupsAvailabilities = eventTierService.getTierSaleGroupsAvailabilities(1L);

        assertEquals(2, tierSaleGroupsAvailabilities.size(), "There are 2 tierSaleGroupAvailabilites");

        for (TierPriceTypeAvailabilityDTO tierPriceTypeAvailability : tierSaleGroupsAvailabilities) {

            assertNotNull(tierPriceTypeAvailability.getSaleGroupsAvailabilities().get(idCupo.longValue()),
                    "All availabilities are related to sale group " + idCupo);

            assertEquals(availability.intValue() - 1, tierPriceTypeAvailability.getSaleGroupsAvailabilities().get(idCupo.longValue()).intValue(),
                    "Availability is incorrect");
        }
    }

    @Test
    public void updateCommElements() {
        TierCommunicationElementDTO[] tierCommElements = null;
        Long eventId = 200L;
        Long tierId = null;
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("Fail on empty tier comm elements");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_TRANSLATION_MANDATORY.getMessage(),
                    e.getMessage(), "Fail on empty comm elements");
        }

        tierCommElements = new TierCommunicationElementDTO[]{random(TierCommunicationElementDTO.class),
                random(TierCommunicationElementDTO.class)};
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("On null tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on null tierId");
        }

        tierId = -1L;
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("On negative tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on negative tierId");
        }

        tierId = 0L;
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("On zero tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on zero tierId");
        }

        tierId = 1L;
        CpanelTierRecord tierRecord = new CpanelTierRecord();
        tierRecord.setIdzona(100);
        tierRecord.setIdtier(tierId.intValue());
        tierRecord.setNombre("old_name");
        tierRecord.setFechaInicio(Timestamp.from(new Date().toInstant()));
        when(tierDao.findById(tierId.intValue())).thenReturn(tierRecord);
        when(tierDao.update(any())).thenReturn(tierRecord);
        when(eventDao.findById(200)).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("Fail on non existing event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existing event");
        }

        eventId = 100L;
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("Fail on non tiered event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage(), "Fail on non tiered event");
        }

        event.setUsetieredpricing((byte) 1);
        List<TierRecord> foundTiers = generateTiers(20, 30, 40);

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        try {
            eventTierService.updateCommElements(eventId, tierId, tierCommElements);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on tier not belonging to event");
        }
        foundTiers.addAll(generateTiers(tierId.intValue()));

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        when(tierConfigCouchDao.getOrInitTierConfig(tierId)).thenReturn(new TierConfig());

        eventTierService.updateCommElements(eventId, tierId, tierCommElements);
    }

    @Test
    public void getCommElements() {
        Long eventId = 200L;
        Long tierId = null;
        try {
            eventTierService.getCommElements(eventId, tierId, null);
            fail("On null tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on null tierId");
        }

        tierId = -1L;
        try {
            eventTierService.getCommElements(eventId, tierId, null);
            fail("On negative tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on negative tierId");
        }

        tierId = 0L;
        try {
            eventTierService.getCommElements(eventId, tierId, null);
            fail("On zero tierId, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_ID_MANDATORY.getMessage(), e.getMessage(), "Fail on zero tierId");
        }

        tierId = 1L;
        CpanelTierRecord tierRecord = new CpanelTierRecord();
        tierRecord.setIdzona(100);
        tierRecord.setIdtier(tierId.intValue());
        tierRecord.setNombre("old_name");
        tierRecord.setFechaInicio(Timestamp.from(new Date().toInstant()));
        when(tierDao.findById(tierId.intValue())).thenReturn(tierRecord);
        when(tierDao.update(any())).thenReturn(tierRecord);
        when(eventDao.findById(200)).thenThrow(new EntityNotFoundException(""));
        try {
            eventTierService.getCommElements(eventId, tierId, null);
            fail("Fail on non existing event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getMessage(), e.getMessage(), "Fail on non existing event");
        }

        eventId = 100L;
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 0);
        when(eventDao.findById(eventId.intValue())).thenReturn(event);
        try {
            eventTierService.getCommElements(eventId, tierId, null);
            fail("Fail on non tiered event");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER.getMessage(), e.getMessage(), "Fail on non tiered event");
        }

        event.setUsetieredpricing((byte) 1);
        List<TierRecord> foundTiers = generateTiers(20, 30, 40);

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        try {
            eventTierService.getCommElements(eventId, tierId, null);
            fail("On tier not belonging to event, code should fail");
        } catch (OneboxRestException e) {
            assertEquals(MsEventTierErrorCode.TIER_NOT_FOUND.getMessage(), e.getMessage(), "Fail on tier not belonging to event");
        }
        foundTiers.addAll(generateTiers(tierId.intValue()));

        when(tierDao.findByEventId(eventId.intValue(), null, null, null)).thenReturn(foundTiers);
        when(tierConfigCouchDao.getOrInitTierConfig(tierId)).thenReturn(new TierConfig());

        eventTierService.getCommElements(eventId, tierId, null);
    }

    @Test
    void incrementTierLimitForEventTest() throws Exception {
        Long tierId = ID;
        Long eventId = ID;
        Long saleGroupId = ID;
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setUsetieredpricing((byte) 93);
        List<TierRecord> eventTiers = generateTiers(ID.intValue());
        TierRecord tier = buildTierRecord();

        TierModificationMessage message = buildTierModificationMessage();

        doReturn(event).when(eventDao).findById(eventId.intValue());
        doReturn(eventTiers).when(tierDao).findByEventId(eventId.intValue(), null, null, null);
        doReturn(tier).when(tierDao).getTier(tierId.intValue());
        doReturn(ID).when(tierLimitCouchDao).get(tierId);
        doReturn(ID).when(tierLimitCouchDao).increment(tierId);
        doReturn(ID).when(tierDao).findTierZoneId(tierId.intValue());
        doNothing().when(tierModificationProducer).sendMessage(message);
        doReturn(ID).when(tierSaleGroupCouchDao).increment(tierId, saleGroupId);

        Assertions.assertDoesNotThrow(
            () -> eventTierService.incrementTierLimitForEvent(tierId, eventId, saleGroupId));

    }

    private TierRecord buildTierRecord() {
        TierRecord tierRecord = new TierRecord();
        tierRecord.setIdzona(100);
        tierRecord.setIdtier(ID.intValue());
        tierRecord.setNombre("nombre");
        tierRecord.setFechaInicio(Timestamp.from(new Date().toInstant()));
        tierRecord.setCondicion(ID.intValue());
        tierRecord.setLimite(ID.intValue());
        LimiteCupoRecord limiteCupoRecord = new LimiteCupoRecord();
        limiteCupoRecord.setIdcupo(ID.intValue());
        limiteCupoRecord.setLimite(ID.intValue());
        List<LimiteCupoRecord> limitesCupo = List.of(limiteCupoRecord);
        tierRecord.setLimitesCupo(limitesCupo);
        return tierRecord;
    }

    private TierModificationMessage buildTierModificationMessage() {
        TierModificationMessage message = new TierModificationMessage();
        message.setAction(TierModificationMessage.Action.EVALUATE_TIERS);
        message.setEventId(ID);
        message.setPriceTypeId(ID);
        return message;
    }


    private List<TierRecord> generateTiers(Integer... ids) {
        List<TierRecord> result = new ArrayList<>();
        for (Integer id : ids) {
            TierRecord record = new TierRecord();
            record.setIdtier(id);
            result.add(record);
        }
        return result;
    }

}

package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.domain.RateRestrictions;
import es.onebox.event.common.domain.RatesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.common.request.PriceTypeFilter;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.GroupPricesDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.CreateEventRateDTO;
import es.onebox.event.events.dto.EventRateDateRestrictionDTO;
import es.onebox.event.events.dto.EventRateRestrictionsDTO;
import es.onebox.event.events.dto.PriceTypeDTO;
import es.onebox.event.events.dto.PriceTypesDTO;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.dto.RateRelationsRestrictionDTO;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.RatesDTO;
import es.onebox.event.events.dto.UpdateEventRateDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.priceengine.request.ChannelSubtype;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.promotions.dao.EventRatePromotionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.utils.RateRestrictionsValidator;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.utils.ObjectRandomizer.random;
import static es.onebox.utils.ObjectRandomizer.randomListOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventRateServiceTest {

    @InjectMocks
    private EventRateService eventRateService;

    @InjectMocks
    private CommonRatesService commonRatesService;

    @Mock
    private EventDao eventDao;

    @Mock
    private RateDao rateDao;

    @Mock
    private EventPriceTypeService eventPriceTypeService;

    @Mock
    private PriceZoneAssignmentDao priceZoneAssignmentDao;

    @Mock
    private GroupPricesDao groupPricesDao;

    @Mock
    private ItemDescSequenceDao itemDescSequenceDao;

    @Mock
    private DescPorIdiomaDao descPorIdiomaDao;

    @Mock
    private LanguageDao languageDao;

    @Mock
    private SessionRateDao sessionRateDao;

    @Mock
    private EventRatePromotionDao eventRatePromotionDao;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private VenueTemplateDao venueTemplateDao;

    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    @Mock
    private ChannelEventDao channelEventDao;

    @InjectMocks
    private RateRestrictionsValidator rateRestrictionsValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(eventRateService, "commonRatesService", commonRatesService);
        ReflectionTestUtils.setField(eventRateService, "rateRestrictionsValidator", rateRestrictionsValidator);
    }

    @Test
    void findRatesByEventIdTestOK() {
        when(eventDao.getById(any())).thenReturn(new CpanelEventoRecord());
        when(rateDao.countByEventId(any(Integer.class))).thenReturn(1L);
        when(rateDao.getEventRatesByEventId(any(Integer.class), any(Long.class), any(Long.class)))
                .thenReturn(createEventRatesArray());

        RatesDTO dto = eventRateService.findRatesByEventId(22, new RatesFilter());

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getMetadata());
        Assertions.assertNotNull(dto.getData());
        Assertions.assertEquals(22L, (long) dto.getData().get(0).getId());
        Assertions.assertNotNull(dto.getData().get(0).getId());
        Assertions.assertNotNull(dto.getData().get(0).getName());
        Assertions.assertTrue(dto.getData().get(0).getRestrictive());
    }

    @Test
    void createEventRate() {

        Integer eventId = random(Integer.class);
        CreateEventRateDTO createEventRateDTO = random(CreateEventRateDTO.class);
        CpanelItemDescSequenceRecord cpanelItemDescSequenceRecord = randomCpanelItemDescSequence();
        List<RateRecord> rateRecords = randomListOf(RateRecord.class, 5);

        CpanelTarifaRecord resultCpanelTarifaRecord = new CpanelTarifaRecord();
        resultCpanelTarifaRecord.setIdtarifa(createEventRateDTO.getId().intValue());

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(itemDescSequenceDao.insert(any())).thenReturn(cpanelItemDescSequenceRecord);
        when(languageDao.getIdiomasByCodes(any())).thenReturn(buildLanguages(createEventRateDTO));
        when(rateDao.getRatesByEventId(eventId)).thenReturn(rateRecords);
        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(new EventConfig());

        doThrow(new AssertionError()).when(rateDao).update(any());
        Mockito.doReturn(null).when(rateDao)
                .update(argThat(arg -> arg.getDefecto().equals((byte) 0)));

        doThrow(new AssertionError()).when(rateDao).insert(any());
        Mockito.doReturn(resultCpanelTarifaRecord).when(rateDao)
                .insert(argThat(arg ->
                        arg.getIdtarifa() == null
                                && arg.getNombre().equals(createEventRateDTO.getName())
                                && arg.getDescripcion().equals(createEventRateDTO.getDescription())
                                && BooleanUtils.toBoolean(arg.getDefecto()) == createEventRateDTO.getDefaultRate()
                                && BooleanUtils.toBoolean(arg.getAccesorestrictivo()) == createEventRateDTO.getRestrictive()
                                && arg.getElementocomdescripcion().equals(cpanelItemDescSequenceRecord.getIditem())
                                && arg.getIdevento().equals(eventId)
                ));

        eventRateService.createEventRate(eventId, createEventRateDTO);
    }

    @Test
    void updateEventRates() {

        Integer eventId = random(Integer.class);
        List<UpdateEventRateDTO> ratesDTO = randomListOf(UpdateEventRateDTO.class, 2);
        ratesDTO.get(0).setId(2L);
        ratesDTO.get(0).setDefaultRate(false);
        ratesDTO.get(0).setTranslations(null);
        ratesDTO.get(1).setId(3L);
        ratesDTO.get(1).setDefaultRate(true);
        ratesDTO.get(1).setTranslations(null);

        List<CpanelTarifaRecord> rates = Arrays.asList(createEventRateRecord(1), createEventRateRecord(2), createEventRateRecord(3));

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(rates);

        eventRateService.updateEventRates(eventId, ratesDTO);

        //2 rate updates + 2 uncheck default
        verify(rateDao, times(4)).update(any());
    }

    @Test
    void updateEventRate() {

        Integer eventId = random(Integer.class);
        UpdateEventRateDTO updateEventRateDTO = random(UpdateEventRateDTO.class);
        int rateId = updateEventRateDTO.getId().intValue();
        updateEventRateDTO.setDefaultRate(true);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        updateEventRateDTO.setTranslations(translations);

        List<CpanelTarifaRecord> rates = Arrays.asList(createEventRateRecord(1), createEventRateRecord(rateId));

        when(languageDao.getIdiomasByCodes(any())).thenReturn(buildLanguages(updateEventRateDTO));
        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(rates);
        when(descPorIdiomaDao.getByKey(anyInt(), any())).thenReturn(new CpanelDescPorIdiomaRecord());

        eventRateService.updateEventRate(eventId, rateId, updateEventRateDTO);

        verify(rateDao, times(3)).update(any());

        Mockito.doAnswer(a -> {
            CpanelTarifaRecord rate = (CpanelTarifaRecord) a.getArguments()[0];
            Assertions.assertEquals(rateId, rate.getIdtarifa().intValue());
            Assertions.assertEquals(updateEventRateDTO.getName(), rate.getNombre());
            return Void.class;
        }).when(rateDao).update(any());
    }

    @Test
    void updateEventRate_invalidId() {

        Integer eventId = random(Integer.class);
        UpdateEventRateDTO updateEventRateDTO = random(UpdateEventRateDTO.class);
        updateEventRateDTO.setDefaultRate(false);

        List<CpanelTarifaRecord> rates = Arrays.asList(createEventRateRecord(1), createEventRateRecord(2));

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(rates);

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.updateEventRate(eventId, 3, updateEventRateDTO));
    }

    @Test
    void updateEventRate_invalidName() {

        Integer eventId = random(Integer.class);
        UpdateEventRateDTO rateDTO = random(UpdateEventRateDTO.class);
        rateDTO.setName("test");

        List<CpanelTarifaRecord> rates = Arrays.asList(createEventRateRecord(1),
                createEventRateRecord(2));
        rates.get(1).setNombre("test");

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(rates);

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.updateEventRate(eventId, 1, rateDTO));
    }

    @Test
    void deleteEventRate() {
        Integer eventId = random(Integer.class);
        RateDTO rateDTO = random(RateDTO.class);
        int rateId = rateDTO.getId().intValue();
        rateDTO.setDefaultRate(false);

        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        eventRateRecord.setDefecto((byte) 0);
        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRate(eventId, rateId)).thenReturn(eventRateRecord);

        eventRateService.deleteEventRate(eventId, rateId);

        verify(priceZoneAssignmentDao, times(1)).deleteByRateId(any());
        verify(groupPricesDao, times(1)).deleteByRateId(any());
        verify(rateDao, times(1)).delete(any());
    }

    @Test
    void deleteEventRate_default() {
        Integer eventId = random(Integer.class);
        RateDTO rateDTO = random(RateDTO.class);
        int rateId = rateDTO.getId().intValue();
        rateDTO.setDefaultRate(true);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRate(eventId, rateId)).thenReturn(createEventRateRecord(rateId));

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.deleteEventRate(eventId, rateId));
    }

    @Test
    void getEventRateRestrictionsTest_invalidConfig() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(new EventConfig());

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.getEventRateRestrictions(eventId, rateId));
    }

    @Test
    void getEventRateRestrictionsTest_invalidRestrictions() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        RatesRestrictions ratesRestrictions = new RatesRestrictions();
        EventConfig eventConfig = new EventConfig();
        Restrictions eventRestrictions = new Restrictions();
        eventRestrictions.setRates(ratesRestrictions);
        eventConfig.setRestrictions(eventRestrictions);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(eventConfig);

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.getEventRateRestrictions(eventId, rateId));
    }

    @Test
    void getEventRateRestrictionsTest() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        RatesRestrictions ratesRestrictions = new RatesRestrictions();
        EventConfig eventConfig = new EventConfig();
        Restrictions eventRestrictions = new Restrictions();
        eventRestrictions.setRates(ratesRestrictions);
        eventConfig.setRestrictions(eventRestrictions);
        RateRestrictions restrictions = random(RateRestrictions.class);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        eventConfig.getRestrictions().getRates().put(rateId, restrictions);
        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(eventConfig);

        EventRateRestrictionsDTO response = eventRateService.getEventRateRestrictions(eventId, rateId);

        Assertions.assertNotNull(response);
    }

    @Test
    void updateEventRateRestrictionsTest_invalidRestrictionsFlag() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO));
    }

    @Test
    void updateEventRateRestrictionsTest_invalidRestrictions() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        EventRateDateRestrictionDTO eventDateRestrictionDTO = new EventRateDateRestrictionDTO();
        eventDateRestrictionDTO.setFrom(ZonedDateTime.now());
        eventDateRestrictionDTO.setTo(ZonedDateTime.now().minusDays(1));
        eventRateRestrictionsDTO.setDateRestriction(eventDateRestrictionDTO);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO));
    }

    @Test
    void updateEventRateRestrictionsTest() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        EventRateDateRestrictionDTO eventDateRestrictionDTO = new EventRateDateRestrictionDTO();
        eventDateRestrictionDTO.setFrom(ZonedDateTime.now().minusDays(1));
        eventDateRestrictionDTO.setTo(ZonedDateTime.now());
        eventRateRestrictionsDTO.setDateRestriction(eventDateRestrictionDTO);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));
        when(eventConfigCouchDao.getOrInitEventConfig(eventId.longValue())).thenReturn(new EventConfig());
        doNothing().when(eventConfigCouchDao).upsert(Mockito.eq(eventId.toString()), Mockito.any());

        eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO);
        verify(eventConfigCouchDao).upsert(Mockito.eq(eventId.toString()), Mockito.any());
    }

    @Test
    void updateEventRateRestrictionsTest_KO_RelationRestrictionsRequiredRatesNull() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        RateRelationsRestrictionDTO rateRelationsRestrictionDTO = new RateRelationsRestrictionDTO();
        eventRateRestrictionsDTO.setRateRelationsRestrictionEnabled(true);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        EventRateDateRestrictionDTO eventDateRestrictionDTO = new EventRateDateRestrictionDTO();
        eventDateRestrictionDTO.setFrom(ZonedDateTime.now().minusDays(1));
        eventDateRestrictionDTO.setTo(ZonedDateTime.now());
        eventRateRestrictionsDTO.setDateRestriction(eventDateRestrictionDTO);
        eventRateRestrictionsDTO.setRateRelationsRestriction(rateRelationsRestrictionDTO);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));


        Assertions.assertEquals(Assertions.assertThrowsExactly(OneboxRestException.class, () ->
                eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO)).getErrorCode(),
                "INVALID_RATE_RESTRICTIONS");
    }

    @Test
    void updateEventRateRestrictionsTest_KO_RelationRestrictionsRequiredRateAndRateId() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        RateRelationsRestrictionDTO rateRelationsRestrictionDTO = new RateRelationsRestrictionDTO();
        eventRateRestrictionsDTO.setRateRelationsRestrictionEnabled(true);
        rateRelationsRestrictionDTO.setRequiredRates(List.of(rateId));
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        EventRateDateRestrictionDTO eventDateRestrictionDTO = new EventRateDateRestrictionDTO();
        eventDateRestrictionDTO.setFrom(ZonedDateTime.now().minusDays(1));
        eventDateRestrictionDTO.setTo(ZonedDateTime.now());
        eventRateRestrictionsDTO.setDateRestriction(eventDateRestrictionDTO);
        eventRateRestrictionsDTO.setRateRelationsRestriction(rateRelationsRestrictionDTO);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));


        Assertions.assertEquals(Assertions.assertThrowsExactly(OneboxRestException.class, () ->
                eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO)).getErrorCode(),
                "INVALID_RATE_RESTRICTIONS");
    }

    @Test
    void updateEventRateRestrictionsTest_KO_RelationRestrictionsRequiredRateNotInEvent() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        RateRelationsRestrictionDTO rateRelationsRestrictionDTO = new RateRelationsRestrictionDTO();
        eventRateRestrictionsDTO.setRateRelationsRestrictionEnabled(true);
        rateRelationsRestrictionDTO.setRequiredRates(List.of(rateId - 1));
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        EventRateDateRestrictionDTO eventDateRestrictionDTO = new EventRateDateRestrictionDTO();
        eventDateRestrictionDTO.setFrom(ZonedDateTime.now().minusDays(1));
        eventDateRestrictionDTO.setTo(ZonedDateTime.now());
        eventRateRestrictionsDTO.setDateRestriction(eventDateRestrictionDTO);
        eventRateRestrictionsDTO.setRateRelationsRestriction(rateRelationsRestrictionDTO);


        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        Assertions.assertEquals(Assertions.assertThrowsExactly(OneboxRestException.class, () ->
                eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO)).getErrorCode(),
                "INVALID_RATE_RESTRICTIONS");
    }

    @Test
    void updateEventRateRestrictionsTest_KO_RelationRestrictionsPriceZoneNotInEvent() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        Integer rateInEvent = random(Integer.class);
        UpdateRateRestrictionsDTO eventRateRestrictionsDTO = new UpdateRateRestrictionsDTO();
        eventRateRestrictionsDTO.setDateRestrictionEnabled(true);
        RateRelationsRestrictionDTO rateRelationsRestrictionDTO = new RateRelationsRestrictionDTO();
        eventRateRestrictionsDTO.setRateRelationsRestrictionEnabled(true);
        rateRelationsRestrictionDTO.setRequiredRates(List.of(rateInEvent));
        rateRelationsRestrictionDTO.setRestrictedPriceZones(List.of(2));
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        final CpanelTarifaRecord eventRateInEventRecord = createEventRateRecord(rateInEvent);
        EventRateDateRestrictionDTO eventDateRestrictionDTO = new EventRateDateRestrictionDTO();
        eventDateRestrictionDTO.setFrom(ZonedDateTime.now().minusDays(1));
        eventDateRestrictionDTO.setTo(ZonedDateTime.now());
        eventRateRestrictionsDTO.setDateRestriction(eventDateRestrictionDTO);
        eventRateRestrictionsDTO.setRateRelationsRestriction(rateRelationsRestrictionDTO);
        PriceTypesDTO priceTypesDTO = new PriceTypesDTO();
        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(1L);
        priceTypesDTO.setData(List.of(priceTypeDTO));

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord, eventRateInEventRecord));
        when(rateDao.getEventRate(eventId, rateInEvent)).thenReturn(eventRateInEventRecord);
        when(eventPriceTypeService.getEventPriceTypes(Long.valueOf(eventId), new PriceTypeFilter())).thenReturn(priceTypesDTO);



        Assertions.assertEquals(Assertions.assertThrowsExactly(OneboxRestException.class, () ->
                eventRateService.updateEventRateRestrictions(eventId, rateId, eventRateRestrictionsDTO)).getErrorCode(),
                "INVALID_RATE_RESTRICTIONS");
    }

    @Test
    void rateRestrictionsValidator_KO_ChannelRestrictionIdsDontBelongToEvent() {
        Integer eventId = random(Integer.class);
        List<Integer> channelIds = List.of(1, 2, 3);

        List<EventChannelRecord> channelEvents = new ArrayList<>();
        EventChannelSearchFilter filter = new EventChannelSearchFilter();
        filter.setId(channelIds.stream().map(Integer::longValue).toList());
        when(channelEventDao.findChannelEvents(eventId.longValue(), filter)).thenReturn(channelEvents);

        OneboxRestException ex = Assertions.assertThrows(OneboxRestException.class, () -> rateRestrictionsValidator.validateChannelRestrictions(channelIds, eventId));
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS.toString(), ex.getErrorCode());
    }

    @Test
    void rateRestrictionsValidator_ChannelRestrictionIdsBelongToEvent() {
        Integer eventId = random(Integer.class);
        List<Integer> channelIds = List.of(1, 2, 3);

        List<EventChannelRecord> channelEvents = new ArrayList<>();
        EventChannelRecord eventChannelRecord1 = new EventChannelRecord();
        EventChannelRecord eventChannelRecord2 = new EventChannelRecord();
        EventChannelRecord eventChannelRecord3 = new EventChannelRecord();
        channelEvents.add(eventChannelRecord1);
        channelEvents.add(eventChannelRecord2);
        channelEvents.add(eventChannelRecord3);

        EventChannelSearchFilter filter = new EventChannelSearchFilter();
        filter.setId(channelIds.stream().map(Integer::longValue).toList());
        filter.setSubtype(List.of(ChannelSubtype.PORTAL_WEB, ChannelSubtype.PORTAL_B2B));
        when(channelEventDao.findChannelEvents(eventId.longValue(), filter)).thenReturn(channelEvents);

        Assertions.assertDoesNotThrow(() -> rateRestrictionsValidator.validateChannelRestrictions(channelIds, eventId));
    }

    @Test
    void rateRestrictionsValidator_NullChannelIdList() {
        Integer eventId = random(Integer.class);
        List<Integer> channelIds = Collections.emptyList();
        Assertions.assertDoesNotThrow(() -> rateRestrictionsValidator.validateChannelRestrictions(channelIds, eventId));
    }

    @Test
    void rateRestrictionsValidator_KO_channelRestrictionEnabledButEmptyListChannelRestrictionIds() {
        UpdateRateRestrictionsDTO restrictionDTO = new UpdateRateRestrictionsDTO();
        restrictionDTO.setChannelRestrictionEnabled(true);
        restrictionDTO.setChannelRestriction(Collections.emptyList());

        OneboxRestException ex = Assertions.assertThrows(OneboxRestException.class, () -> rateRestrictionsValidator.validateRateRestrictions(restrictionDTO));
        Assertions.assertEquals(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS.toString(), ex.getErrorCode());

    }

    @Test
    void rateRestrictionsValidator_channelRestrictionEnabledAndListChannelRestrictionIds() {
        UpdateRateRestrictionsDTO restrictionDTO = new UpdateRateRestrictionsDTO();
        restrictionDTO.setChannelRestrictionEnabled(true);
        restrictionDTO.setChannelRestriction(List.of(-1));

        Assertions.assertDoesNotThrow(() -> rateRestrictionsValidator.validateRateRestrictions(restrictionDTO));
    }

    @Test
    void deleteEventRateRestrictionsTest_invalidConfig() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(new EventConfig());

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.deleteEventRateRestrictions(eventId, rateId));
    }

    @Test
    void deleteEventRateRestrictionsTest_invalidRestrictions() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        RatesRestrictions ratesRestrictions = new RatesRestrictions();
        EventConfig eventConfig = new EventConfig();
        Restrictions eventRestrictions = new Restrictions();
        eventRestrictions.setRates(ratesRestrictions);
        eventConfig.setRestrictions(eventRestrictions);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));

        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(eventConfig);

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateService.deleteEventRateRestrictions(eventId, rateId));
    }

    @Test
    void deleteEventRateRestrictionsTest() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);
        RatesRestrictions ratesRestrictions = new RatesRestrictions();
        EventConfig eventConfig = new EventConfig();
        Restrictions eventRestrictions = new Restrictions();
        eventRestrictions.setRates(ratesRestrictions);
        eventConfig.setRestrictions(eventRestrictions);
        RateRestrictions restrictions = random(RateRestrictions.class);
        eventConfig.getRestrictions().getRates().put(rateId, restrictions);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));
        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(eventConfig);
        doNothing().when(eventConfigCouchDao).upsert(Mockito.eq(eventId.toString()), Mockito.any());

        eventRateService.deleteEventRateRestrictions(eventId, rateId);
        verify(eventConfigCouchDao).upsert(Mockito.eq(eventId.toString()), Mockito.any());
    }

    @Test
    void getRestrictedRatesTest_emptyList() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));
        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(new EventConfig());

        List<RateRestrictedDTO> response = eventRateService.getRestrictedRates(eventId);
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getRestrictedRatesTest() {
        Integer eventId = random(Integer.class);
        Integer rateId = random(Integer.class);
        final CpanelTarifaRecord eventRateRecord = createEventRateRecord(rateId);

        RatesRestrictions ratesRestrictions = new RatesRestrictions();
        EventConfig eventConfig = new EventConfig();
        Restrictions eventRestrictions = new Restrictions();
        eventRestrictions.setRates(ratesRestrictions);
        eventConfig.setRestrictions(eventRestrictions);
        RateRestrictions restrictions = random(RateRestrictions.class);
        eventConfig.getRestrictions().getRates().put(rateId, restrictions);

        when(eventDao.getById(eventId)).thenReturn(new CpanelEventoRecord());
        when(rateDao.getEventRates(eventId)).thenReturn(List.of(eventRateRecord));
        when(eventConfigCouchDao.get(eventId.toString())).thenReturn(eventConfig);

        List<RateRestrictedDTO> response = eventRateService.getRestrictedRates(eventId);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(rateId.longValue(), response.get(0).getRate().getId());
    }

    private CpanelTarifaRecord createEventRateRecord(int id) {
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setIdtarifa(id);
        tarifaRecord.setNombre(random(String.class));
        tarifaRecord.setDefecto((byte) 1);
        tarifaRecord.setElementocomdescripcion(1);
        return tarifaRecord;
    }

    private CpanelItemDescSequenceRecord randomCpanelItemDescSequence() {
        CpanelItemDescSequenceRecord result = new CpanelItemDescSequenceRecord();
        result.setIditem(random(Integer.class));
        return result;
    }

    private List<RateRecord> createEventRatesArray() {
        List<RateRecord> rates = new ArrayList<>();
        rates.add(createEventRatesMapper());
        return rates;
    }

    private RateRecord createEventRatesMapper() {
        RateRecord rate = new RateRecord();
        rate.setIdTarifa(22);
        rate.setIdEvento(52);
        rate.setNombre("General");
        rate.setDescripcion("General");
        rate.setDefecto(1);
        rate.setAccesoRestrictivo(1);
        return rate;
    }

    private List<CpanelIdiomaRecord> buildLanguages(CreateEventRateDTO createEventRateDTO) {
        return createEventRateDTO.getTranslations().keySet().stream()
                .map(code -> {
                    CpanelIdiomaRecord cpanelIdiomaRecord = new CpanelIdiomaRecord();
                    cpanelIdiomaRecord.setCodigo(code);
                    cpanelIdiomaRecord.setIdidioma(random(Integer.class));
                    return cpanelIdiomaRecord;
                })
                .collect(Collectors.toList());
    }

    private List<CpanelIdiomaRecord> buildLanguages(UpdateEventRateDTO createEventRateDTO) {
        return createEventRateDTO.getTranslations().keySet().stream()
                .map(code -> {
                    CpanelIdiomaRecord cpanelIdiomaRecord = new CpanelIdiomaRecord();
                    cpanelIdiomaRecord.setCodigo(code);
                    cpanelIdiomaRecord.setIdidioma(random(Integer.class));
                    return cpanelIdiomaRecord;
                })
                .collect(Collectors.toList());
    }
}

package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.services.CommonRatesGroup;
import es.onebox.event.datasources.integration.avet.config.dto.AvetPrice;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.avetintegration.IntegrationAvetService;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.GroupPricesDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.RateGroupDao;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateGroupSessionRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.CreateRateGroupRequestDTO;
import es.onebox.event.events.dto.RateGroupType;
import es.onebox.event.events.dto.RatesGroupDTO;
import es.onebox.event.events.dto.UpdateRateGroupRequestDTO;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.promotions.dao.EventRatePromotionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGrupoTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventRateGroupServiceTest {

    @InjectMocks
    private EventRateGroupService eventRateGroupService;

    @Mock
    private EventDao eventDao;

    @Mock
    private RateDao rateDao;

    @Mock
    private RateGroupDao rateGroupDao;

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
    private IntAvetConfigRepository intAvetConfigRepository;
    @Mock
    private IntegrationAvetService integrationAvetService;

    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    private CpanelEventoRecord eventoRecord;
    private final Integer eventId = 1212;
    private final Integer rateGroupId = 34;
    private final Integer sessionId = 331;
    private final Integer rateId = 11;
    private final String firstExternalDescription = "REQUETESENIOR";
    private final String secondExternalDescription = "GAZTEABONO";
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventoRecord = createCpanelEventoRecord();

        CpanelGrupoTarifaRecord rateGroupGeneral = createCpanelGrupoTarifa(rateGroupId, "General", true, eventId, "NameGeneral");
        CpanelGrupoTarifaRecord rateGroupSub = createCpanelGrupoTarifa(rateGroupId + 1, "SUB25", false, eventId, "sub25");
        CpanelGrupoTarifaRecord rateGroupSenior = createCpanelGrupoTarifa(rateGroupId + 2, "SENIOR", false, eventId, "senior");
        List<CpanelGrupoTarifaRecord> rates = Arrays.asList(rateGroupGeneral, rateGroupSub, rateGroupSenior);
        when(rateGroupDao.getEventRates(eventId)).thenReturn(rates);
        when(rateGroupDao.getEventRate(eventId, rateGroupId)).thenReturn(rateGroupGeneral);
        when(rateGroupDao.getEventRate(eventId, rateGroupId+1)).thenReturn(rateGroupSub);
        when(rateGroupDao.getEventRate(eventId, rateGroupId+2)).thenReturn(rateGroupSenior);
        when(eventDao.getById(eventId)).thenReturn(eventoRecord);

        RateGroupSessionRecord rateGroupSessionRecordGeneral = createGroupSessionRecord(sessionId, rateId, rateGroupId, "General");
        RateGroupSessionRecord rateGroupSessionRecordSub = createGroupSessionRecord(sessionId+1, rateId+1, rateGroupId+1, "Sub");
        RateGroupSessionRecord rateGroupSessionRecordSenior = createGroupSessionRecord(sessionId+2, rateId+2, rateGroupId+2, "Senior");
        List<RateGroupSessionRecord> sessionRatestList =
                Arrays.asList(rateGroupSessionRecordGeneral, rateGroupSessionRecordSub, rateGroupSessionRecordSenior);

        when(rateGroupDao.getSessionsDefaultRates(eventId)).thenReturn(sessionRatestList);
        when(rateGroupDao.getSessionsRatesByRateId(eventId,rateGroupId+2)).thenReturn(sessionRatestList);

        CpanelTarifaRecord cpanelTarifaRecordGeneral = createCpanelTarifaRecord(rateId, "general", rateGroupId);
        CpanelTarifaRecord cpanelTarifaRecordSub = createCpanelTarifaRecord(rateId+1, "sub", rateGroupId+1);
        CpanelTarifaRecord cpanelTarifaRecordSenior = createCpanelTarifaRecord(rateId+2, "senior", rateGroupId+2);
        when(rateDao.getEventRate(eventId, rateId)).thenReturn(cpanelTarifaRecordGeneral);
        when(rateDao.getEventRate(eventId, rateId+1)).thenReturn(cpanelTarifaRecordSub);
        when(rateDao.getEventRate(eventId, rateId+2)).thenReturn(cpanelTarifaRecordSenior);

        CpanelItemDescSequenceRecord cpanelItemDescSequenceRecord = randomCpanelItemDescSequence();
        when(itemDescSequenceDao.insert(any())).thenReturn(cpanelItemDescSequenceRecord);

        List<AvetPrice> avetPricesList = randomListOf(AvetPrice.class, 10);
        avetPricesList.get(0).setPriceDescription(firstExternalDescription);
        avetPricesList.get(1).setPriceDescription(secondExternalDescription);

        when(intAvetConfigRepository.getAvetPrices(eventId)).thenReturn(avetPricesList);
        when(eventConfigCouchDao.get(anyString())).thenReturn(new EventConfig());
    }

    @Test
    void findRatesGroupByEventIdTestOK() {
        when(eventDao.getById(any())).thenReturn(eventoRecord);
        when(rateGroupDao.countByEventId(eventId)).thenReturn(1L);
        when(rateGroupDao.getRatesGroupByEventId(any(Integer.class), any(), any(Long.class), any(Long.class)))
                .thenReturn(createEventRatesArray());

        RatesGroupDTO dto = eventRateGroupService.findRatesGroupByEventId(22, new RatesFilter());

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getMetadata());
        Assertions.assertNotNull(dto.getData());
        Assertions.assertEquals(22L, (long) dto.getData().get(0).getId());
        Assertions.assertNotNull(dto.getData().get(0).getId());
        Assertions.assertNotNull(dto.getData().get(0).getName());
    }

    @Test
    void createEventRateGroup() {
        CreateRateGroupRequestDTO createRateGroupRequestDTO = random(CreateRateGroupRequestDTO.class);
        createRateGroupRequestDTO.setExternalDescription(firstExternalDescription);
        CpanelItemDescSequenceRecord cpanelItemDescSequenceRecord = randomCpanelItemDescSequence();
        CpanelDescPorIdiomaRecord cpanelDescPorIdiomaRecord = randomCpanelDescPorIdiomaRecord();

        CpanelGrupoTarifaRecord resultCpanelGrupoTarifaRecord = new CpanelGrupoTarifaRecord();
        resultCpanelGrupoTarifaRecord.setNombre(createRateGroupRequestDTO.getName());
        resultCpanelGrupoTarifaRecord.setIdgrupotarifa(rateGroupId +4);

        CpanelTarifaRecord resultCpanelTarifaRecord = new CpanelTarifaRecord();
        resultCpanelTarifaRecord.setIdtarifa(rateId+4);
        resultCpanelTarifaRecord.setIdgrupotarifa(rateGroupId +4);

        when(itemDescSequenceDao.insert(any())).thenReturn(cpanelItemDescSequenceRecord);
        when(descPorIdiomaDao.insert(any())).thenReturn(cpanelDescPorIdiomaRecord);
        when(languageDao.getIdiomasByCodes(any())).thenReturn(buildLanguages(createRateGroupRequestDTO));

        doThrow(new AssertionError()).when(rateGroupDao).update(any());
        Mockito.doReturn(null).when(rateGroupDao)
                .update(argThat(arg -> arg.getDefecto().equals((byte) 0)));

        doThrow(new AssertionError()).when(rateGroupDao).insert(any());
        Mockito.doReturn(resultCpanelGrupoTarifaRecord).when(rateGroupDao)
                .insert(argThat(arg ->
                        arg.getIdgrupotarifa() == null
                                && arg.getNombre().equals(createRateGroupRequestDTO.getName())
                                && arg.getElementocomdescripcion().equals(cpanelItemDescSequenceRecord.getIditem())
                                && arg.getIdevento().equals(eventId)
                ));

        CpanelTarifaRecord resultTarifaRecordGeneral = createCpanelTarifaRecord(rateId+4, "General - REQUETESENIOR", rateGroupId+4);

        doThrow(new AssertionError()).when(rateDao).insert(resultTarifaRecordGeneral);
        Mockito.doReturn(resultTarifaRecordGeneral).when(rateDao)
                .insert(argThat(arg ->
                        arg.getIdgrupotarifa() == rateGroupId + 4
                                && arg.getIdevento().equals(eventId)
                                && !CommonUtils.isTrue(arg.getDefecto())
                ));

        eventRateGroupService.createEventRateGroup(eventId, createRateGroupRequestDTO);

        verify(rateGroupDao, times(3)).createSesionRate(any(), any());
    }

    @Test
    void createEventRateGroup_duplicatedExternalDescriptionKO() {
        CreateRateGroupRequestDTO createRateGroupRequestDTO = random(CreateRateGroupRequestDTO.class);
        createRateGroupRequestDTO.setExternalDescription("SUB25");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.createEventRateGroup(eventId, createRateGroupRequestDTO));

        Assertions.assertEquals("ME0127", e.getErrorCode());
    }

    @Test
    void createEventRateGroup_shortExternalDescriptionKO() {
        CreateRateGroupRequestDTO createRateGroupRequestDTO = random(CreateRateGroupRequestDTO.class);
        createRateGroupRequestDTO.setExternalDescription("SUB");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.createEventRateGroup(eventId, createRateGroupRequestDTO));

        Assertions.assertEquals("ME0129", e.getErrorCode());
    }

    @Test
    void createEventRateGroup_externalDescriptionNotMatchAvetKO() {
        CreateRateGroupRequestDTO createRateGroupRequestDTO = random(CreateRateGroupRequestDTO.class);
        createRateGroupRequestDTO.setExternalDescription("NOTMATCH");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.createEventRateGroup(eventId, createRateGroupRequestDTO));

        Assertions.assertEquals("ME0128", e.getErrorCode());
    }

    @Test
    void createEventRateGroup_avetPricesIsNull() {
        CreateRateGroupRequestDTO createRateGroupRequestDTO = random(CreateRateGroupRequestDTO.class);
        createRateGroupRequestDTO.setExternalDescription("NOTMATCH");
        CpanelItemDescSequenceRecord cpanelItemDescSequenceRecord = randomCpanelItemDescSequence();
        CpanelDescPorIdiomaRecord cpanelDescPorIdiomaRecord = randomCpanelDescPorIdiomaRecord();

        List<AvetPrice> avetPricesList = null;
        when(intAvetConfigRepository.getAvetPrices(eventId)).thenReturn(avetPricesList);

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.createEventRateGroup(eventId, createRateGroupRequestDTO));

        Assertions.assertEquals("ME0131", e.getErrorCode());
    }

    @Test
    void createEventRateGroup_duplicatedNameKO() {
        CreateRateGroupRequestDTO createRateGroupRequestDTO = random(CreateRateGroupRequestDTO.class);
        createRateGroupRequestDTO.setExternalDescription("SEENNIOR");
        createRateGroupRequestDTO.setName("NameGeneral");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.createEventRateGroup(eventId, createRateGroupRequestDTO));

        Assertions.assertEquals("ME0007", e.getErrorCode());
    }

    @Test
    void updateEventRateGroup() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription(secondExternalDescription);
        rateGroupDTO.setId((long) rateGroupId);
        rateGroupDTO.setName("New test name");

        when(languageDao.getIdiomasByCodes(any())).thenReturn(buildLanguages(rateGroupDTO));
        when(descPorIdiomaDao.getByKey(anyInt(), any())).thenReturn(new CpanelDescPorIdiomaRecord());

        eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO));

        verify(rateGroupDao, times(1)).update(any());

        Mockito.doAnswer(a -> {
            CpanelGrupoTarifaRecord rate = (CpanelGrupoTarifaRecord) a.getArguments()[0];
            Assertions.assertEquals(rateId, rate.getIdgrupotarifa().intValue());
            Assertions.assertEquals(rateGroupDTO.getName(), rate.getNombre());
            return Void.class;
        }).when(rateGroupDao).update(any());
    }

    @Test
    void updateEventRateGroup_nullIdsKO() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription(secondExternalDescription);
        rateGroupDTO.setId(null);
        rateGroupDTO.setName("New test name");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO)));

        Assertions.assertEquals("ME0014", e.getErrorCode());
    }

    @Test
    void updateEventRateGroup_updateIdNotExistsKO() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription(secondExternalDescription);
        rateGroupDTO.setId((long) 87);
        rateGroupDTO.setName("New test name");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO)));

        Assertions.assertEquals("ME0014", e.getErrorCode());
    }

    @Test
    void updateEventRateGroup_repeatedNameKO() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription(secondExternalDescription);
        rateGroupDTO.setId((long) 35);
        rateGroupDTO.setName("NameGeneral");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO)));

        Assertions.assertEquals("ME0007", e.getErrorCode());
    }

    @Test
    void updateEventRateGroup_repeatedExternalDescriptionKO() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription("SUB25");
        rateGroupDTO.setId((long) 36);
        rateGroupDTO.setName("New test name");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO)));

        Assertions.assertEquals("ME0127", e.getErrorCode());
    }

    @Test
    void updateEventRateGroup_shortExternalDescriptionKO() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription("SUB");
        rateGroupDTO.setId((long) 36);
        rateGroupDTO.setName("New test name");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO)));

        Assertions.assertEquals("ME0129", e.getErrorCode());
    }

    @Test
    void updateEventRateGroup_externalDescriptionNotMatchAvetKO() {
        UpdateRateGroupRequestDTO rateGroupDTO = random(UpdateRateGroupRequestDTO.class);
        HashMap<String, String> translations = new HashMap<>();
        translations.put("es_ES", "textES");
        translations.put("en_EN", "textEN");
        rateGroupDTO.setTranslations(translations);
        rateGroupDTO.setExternalDescription("NOTMATCH");
        rateGroupDTO.setId((long) 36);
        rateGroupDTO.setName("New test name");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO)));

        Assertions.assertEquals("ME0128", e.getErrorCode());
    }

    @Test
    void deleteEventRate() {
        Integer deleteId = 36;
        eventRateGroupService.deleteEventRateGroup(eventId, deleteId);

        verify(rateDao, times(1)).delete(any());
        verify(rateGroupDao, times(1)).deleteSessionRate(any(),any());
        verify(rateGroupDao, times(1)).delete(any());
    }

    @Test
    void deleteEventRate_rateNotFound() {
        Integer deleteId = 90;

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.deleteEventRateGroup(eventId, deleteId));

        Assertions.assertEquals("ME0125", e.getErrorCode());
    }

    @Test
    void deleteEventRate_default() {
        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class, () ->
                eventRateGroupService.deleteEventRateGroup(eventId, rateGroupId));

        Assertions.assertEquals("ME0130", e.getErrorCode());
    }

    @Test
    void testGetPosition() {
        CpanelGrupoTarifaRecord rateGroupRate1 = createCpanelGrupoTarifa(1, "111", false, 1, "Tarifa1");
        rateGroupRate1.setPosition(1);
        rateGroupRate1.setTipo(RateGroupType.RATE.getId().byteValue());

        CpanelGrupoTarifaRecord rateGroupProduct1 = createCpanelGrupoTarifa(3, "333", false, 1, "Producto1");
        rateGroupProduct1.setPosition(1);
        rateGroupProduct1.setTipo(RateGroupType.PRODUCT.getId().byteValue());
        CpanelGrupoTarifaRecord rateGroupProduct2 = createCpanelGrupoTarifa(4, "444", false, 1, "Producto2");
        rateGroupProduct2.setPosition(2);
        rateGroupProduct2.setTipo(RateGroupType.PRODUCT.getId().byteValue());

        List<CpanelGrupoTarifaRecord> rateGroups = List.of(rateGroupRate1, rateGroupProduct1, rateGroupProduct2);

        Assertions.assertEquals(1, CommonRatesGroup.getEventRateGroupPosition(new ArrayList<>(), RateGroupType.PRODUCT));
        Assertions.assertEquals(2, CommonRatesGroup.getEventRateGroupPosition(rateGroups, RateGroupType.RATE));
        Assertions.assertEquals(3, CommonRatesGroup.getEventRateGroupPosition(rateGroups, RateGroupType.PRODUCT));
    }

    private CpanelItemDescSequenceRecord randomCpanelItemDescSequence() {
        CpanelItemDescSequenceRecord result = new CpanelItemDescSequenceRecord();
        result.setIditem(random(Integer.class));
        return result;
    }

    private CpanelDescPorIdiomaRecord randomCpanelDescPorIdiomaRecord() {
        CpanelDescPorIdiomaRecord result = new CpanelDescPorIdiomaRecord();
        result.setIditem(random(Integer.class));
        return result;
    }

    private List<RateGroupRecord> createEventRatesArray() {
        List<RateGroupRecord> rates = new ArrayList<>();
        rates.add(createEventRatesMapper());
        return rates;
    }

    private RateGroupRecord createEventRatesMapper() {
        RateGroupRecord rate = new RateGroupRecord();
        rate.setIdGrupoTarifa(22);
        rate.setIdEvento(52);
        rate.setNombre("General");
        rate.setDefecto(1);
        rate.setDescripcionExterna("SUB25");
        return rate;
    }

    private List<CpanelIdiomaRecord> buildLanguages(CreateRateGroupRequestDTO rateDTO) {
        return rateDTO.getTranslations().keySet().stream()
                .map(code -> {
                    CpanelIdiomaRecord cpanelIdiomaRecord = new CpanelIdiomaRecord();
                    cpanelIdiomaRecord.setCodigo(code);
                    cpanelIdiomaRecord.setIdidioma(random(Integer.class));
                    return cpanelIdiomaRecord;
                })
                .collect(Collectors.toList());
    }

    private List<CpanelIdiomaRecord> buildLanguages(UpdateRateGroupRequestDTO rateDTO) {
        return rateDTO.getTranslations().keySet().stream()
                .map(code -> {
                    CpanelIdiomaRecord cpanelIdiomaRecord = new CpanelIdiomaRecord();
                    cpanelIdiomaRecord.setCodigo(code);
                    cpanelIdiomaRecord.setIdidioma(random(Integer.class));
                    return cpanelIdiomaRecord;
                })
                .collect(Collectors.toList());
    }

    private RateGroupSessionRecord createGroupSessionRecord(Integer idSession,
                                                            Integer rateId,
                                                            Integer rateGroupId,
                                                            String sessionRateName) {
        RateGroupSessionRecord rateGroupSessionRecord = new RateGroupSessionRecord();
        rateGroupSessionRecord.setIdSesion(idSession);
        rateGroupSessionRecord.setIdTarifa(rateId);
        rateGroupSessionRecord.setIdGrupoTarifa(rateGroupId);
        rateGroupSessionRecord.setNombre(sessionRateName);
        return rateGroupSessionRecord;
    }

    public CpanelGrupoTarifaRecord createCpanelGrupoTarifa(Integer idGrupoTarifa, String externalDescription, Boolean defaultVal, Integer idEvento, String nombre){
        CpanelGrupoTarifaRecord cpanelGrupoTarifaRecord = new CpanelGrupoTarifaRecord();
        cpanelGrupoTarifaRecord.setIdgrupotarifa(idGrupoTarifa);
        cpanelGrupoTarifaRecord.setDescripcionexterna(externalDescription);
        cpanelGrupoTarifaRecord.setDefecto((byte) BooleanUtils.toInteger(defaultVal));
        cpanelGrupoTarifaRecord.setElementocomdescripcion(null);
        cpanelGrupoTarifaRecord.setIdevento(idEvento);
        cpanelGrupoTarifaRecord.setNombre(nombre);
        return cpanelGrupoTarifaRecord;
    }

    public CpanelEventoRecord createCpanelEventoRecord(){
        CpanelEventoRecord cpanelEventoRecord = new CpanelEventoRecord();
        eventoRecord = cpanelEventoRecord;
        eventoRecord.setTipoevento(2);
        eventoRecord.setIdevento(eventId);
        return cpanelEventoRecord;
    }

    public CpanelTarifaRecord createCpanelTarifaRecord(Integer rateId, String name, Integer rateGroupId){
        CpanelTarifaRecord cpanelTarifaRecord = new CpanelTarifaRecord();
        cpanelTarifaRecord.setIdtarifa(rateId);
        cpanelTarifaRecord.setNombre(name);
        cpanelTarifaRecord.setDefecto((byte) BooleanUtils.toInteger(false));
        cpanelTarifaRecord.setElementocomdescripcion(null);
        cpanelTarifaRecord.setIdevento(eventId);
        cpanelTarifaRecord.setDescripcion("desc");
        cpanelTarifaRecord.setIdgrupotarifa(rateGroupId);
        return cpanelTarifaRecord;
    }
}

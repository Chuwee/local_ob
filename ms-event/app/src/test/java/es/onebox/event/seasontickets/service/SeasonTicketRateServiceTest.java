package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.GroupPricesDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.seasontickets.amqp.renewals.elastic.RenewalsElasticUpdaterService;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketRatesDTO;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SeasonTicketRateServiceTest {

    @InjectMocks
    private SeasonTicketRateService seasonTicketRateService;

    @Mock
    private SeasonTicketRateServiceHelper seasonTicketRateServiceHelper;
    @Mock
    private SeasonTicketRateValidator seasonTicketRateValidator;

    @Mock
    private RateDao rateDao;
    @Mock
    private CommonRatesService commonRatesService;
    @Mock
    private SessionRateDao sessionRateDao;
    @Mock
    private PriceZoneAssignmentDao priceZoneAssignmentDao;
    @Mock
    private GroupPricesDao groupPricesDao;
    @Mock
    private RenewalsElasticUpdaterService renewalsElasticUpdaterService;

    @Captor
    private ArgumentCaptor<ArrayList<SessionRate>> sessionRatesCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findRatesBySeasonTicketIdTest() {
        Integer seasonTicketId = 1;
        RatesFilter filter = new RatesFilter();

        Integer sessionId = 100;
        List<Integer> visibleIdRates = new ArrayList<>();
        visibleIdRates.add(1001);
        visibleIdRates.add(1003);

        Collection<RateRecord> rateRecords = new ArrayList<>();
        rateRecords.add(createRateRecord(1001, "foo"));
        rateRecords.add(createRateRecord(1002, "boo"));
        rateRecords.add(createRateRecord(1003, "woo"));
        rateRecords.add(createRateRecord(1004, "hoo"));

        when(seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(any()))
                .thenReturn(sessionId);
        when(seasonTicketRateServiceHelper.getVisibleIdRatesFromSession(sessionId))
                .thenReturn(visibleIdRates);
        when(rateDao.getSeasonTicketRates(any(), any(), any()))
                .thenReturn(rateRecords);

        SeasonTicketRatesDTO result = seasonTicketRateService.findRatesBySeasonTicketId(seasonTicketId, filter);

        assertEquals(4, result.getData().size());

        List<String> visibleRateNames = result.getData().stream()
                .filter(r -> Boolean.TRUE.equals(r.getEnabled()))
                .map(RateDTO::getName)
                .collect(Collectors.toList());

        assertTrue(visibleRateNames.contains("foo"));
        assertFalse(visibleRateNames.contains("boo"));
        assertTrue(visibleRateNames.contains("woo"));
        assertFalse(visibleRateNames.contains("hoo"));
    }

    private RateRecord createRateRecord(Integer id, String nombre) {
        RateRecord rate = new RateRecord();
        rate.setIdTarifa(id);
        rate.setNombre(nombre);
        return rate;
    }

    @Test
    public void createSeasonTicketRateNoDefaultTest() {
        Integer seasonTicketId = 1;
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();

        Integer sessionId = 100;
        when(seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(any()))
                .thenReturn(sessionId);

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        Integer rateId = 2000;
        when(commonRatesService.createRate(any(), any(), any()))
                .thenReturn(rateId);

        CommonIdResponse response = seasonTicketRateService.createSeasonTicketRate(seasonTicketId, seasonTicketRateDTO);
        assertEquals(rateId, response.getId());

        verify(commonRatesService, times(0)).unsetDefaultSeasonTicketRate(any(), any());
        verify(sessionRateDao, times(0)).bulkInsertSessionRates(any());
    }

    @Test
    public void createSeasonTicketRateNoDefaultButVisibleTest() {
        Integer seasonTicketId = 1;
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setDefaultRate(Boolean.FALSE);
        seasonTicketRateDTO.setEnabled(Boolean.TRUE);

        Integer sessionId = 100;
        when(seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(any()))
                .thenReturn(sessionId);

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        Integer rateId = 2000;
        when(commonRatesService.createRate(any(), any(), any()))
                .thenReturn(rateId);

        CommonIdResponse response = seasonTicketRateService.createSeasonTicketRate(seasonTicketId, seasonTicketRateDTO);
        assertEquals(rateId, response.getId());

        verify(commonRatesService, times(0)).unsetDefaultSeasonTicketRate(any(), any());
        verify(sessionRateDao, times(1)).bulkInsertSessionRates(any());
    }

    @Test
    public void createSeasonTicketRateDefaultAndVisibleTest() {
        Integer seasonTicketId = 1;
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setDefaultRate(Boolean.TRUE);
        seasonTicketRateDTO.setEnabled(Boolean.TRUE);

        Integer sessionId = 100;
        when(seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(any()))
                .thenReturn(sessionId);

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        Integer rateId = 2000;
        when(commonRatesService.createRate(any(), any(), any()))
                .thenReturn(rateId);

        CommonIdResponse response = seasonTicketRateService.createSeasonTicketRate(seasonTicketId, seasonTicketRateDTO);
        assertEquals(rateId, response.getId());

        verify(commonRatesService, times(1)).unsetDefaultSeasonTicketRate(any(), any());
        verify(sessionRateDao, times(1)).bulkInsertSessionRates(any());
    }

    private CpanelTarifaRecord createTarifaRecord(Integer id, String nombre, Boolean defecto) {
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setIdtarifa(id);
        tarifaRecord.setNombre(nombre);
        tarifaRecord.setDefecto(ConverterUtils.isTrueAsByte(defecto));

        return tarifaRecord;
    }

    @Test
    public void updateSeasonTicketRates2DefaultsTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1001L, "foo", Boolean.FALSE, Boolean.TRUE));
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", Boolean.FALSE, Boolean.TRUE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        OneboxRestException exception = null;
        try {
            seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);
        } catch (OneboxRestException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("No more than 1 default rate allowed", exception.getMessage());
    }

    private SeasonTicketRateDTO createSeasonTicketRateDTO(Long id, String name, Boolean visible, Boolean defaultRate) {
        SeasonTicketRateDTO rate = new SeasonTicketRateDTO();
        rate.setId(id);
        rate.setName(name);
        rate.setEnabled(visible);
        rate.setDefaultRate(defaultRate);

        return rate;
    }

    @Test
    public void updateSeasonTicketRatesAnyDefaultTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1001L, "foo", Boolean.FALSE, Boolean.FALSE));
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", Boolean.FALSE, Boolean.FALSE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.TRUE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        OneboxRestException exception = null;
        try {
            seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);
        } catch (OneboxRestException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Cant disable the default rate, another one must be set as default before.", exception.getMessage());
    }

    @Test
    public void updateSeasonTicketRatesNotExistingRateTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1001L, "foo", Boolean.FALSE, Boolean.TRUE));
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", Boolean.FALSE, Boolean.FALSE));
        modifyRates.add(createSeasonTicketRateDTO(1003L, "yoo", Boolean.FALSE, Boolean.FALSE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        OneboxRestException exception = null;
        try {
            seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);
        } catch (OneboxRestException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Rate not found for season ticket", exception.getMessage());
    }

    @Test
    public void updateSeasonTicketRatesNoDefaultNoVisibleTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", Boolean.FALSE, Boolean.FALSE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        List<CpanelTarifaRecord> visibleRates = new ArrayList<>();
        visibleRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        visibleRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(sessionRateDao.getRatesBySessionId(any()))
                .thenReturn(visibleRates);

        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);

        verify(commonRatesService, times(0)).unsetDefaultSeasonTicketRate(any(), any());

        verify(sessionRateDao, times(1)).bulkInsertSessionRates(sessionRatesCaptor.capture());

        ArrayList<SessionRate> sessionRatesValue = sessionRatesCaptor.getValue();

        List<Integer> sessionRateIds = sessionRatesValue.stream()
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(2, sessionRateIds.size());
        assertTrue(sessionRateIds.contains(1001));
        assertTrue(sessionRateIds.contains(1003));

        List<Integer> defaults = sessionRatesValue.stream()
                .filter(r -> Boolean.TRUE.equals(r.getDefaultRate()))
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(1, defaults.size());
        assertEquals(1001, defaults.get(0));
    }

    @Test
    public void updateSeasonTicketRatesNoDefaultNullEnabledAndNotEnabledTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", null, Boolean.FALSE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        List<CpanelTarifaRecord> visibleRates = new ArrayList<>();
        visibleRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        visibleRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(sessionRateDao.getRatesBySessionId(any()))
                .thenReturn(visibleRates);

        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);

        verify(commonRatesService, times(0)).unsetDefaultSeasonTicketRate(any(), any());

        verify(sessionRateDao, times(1)).bulkInsertSessionRates(sessionRatesCaptor.capture());

        ArrayList<SessionRate> sessionRatesValue = sessionRatesCaptor.getValue();

        List<Integer> sessionRateIds = sessionRatesValue.stream()
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(2, sessionRateIds.size());
        assertTrue(sessionRateIds.contains(1001));
        assertTrue(sessionRateIds.contains(1003));

        List<Integer> defaults = sessionRatesValue.stream()
                .filter(r -> Boolean.TRUE.equals(r.getDefaultRate()))
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(1, defaults.size());
        assertEquals(1001, defaults.get(0));
    }

    @Test
    public void updateSeasonTicketRatesNoDefaultNullEnabledAndButEnabledTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", null, Boolean.FALSE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        List<CpanelTarifaRecord> visibleRates = new ArrayList<>();
        visibleRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        visibleRates.add(createTarifaRecord(1002, "foo", Boolean.FALSE));
        visibleRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(sessionRateDao.getRatesBySessionId(any()))
                .thenReturn(visibleRates);

        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);

        verify(commonRatesService, times(0)).unsetDefaultSeasonTicketRate(any(), any());

        verify(sessionRateDao, times(1)).bulkInsertSessionRates(sessionRatesCaptor.capture());

        ArrayList<SessionRate> sessionRatesValue = sessionRatesCaptor.getValue();

        List<Integer> sessionRateIds = sessionRatesValue.stream()
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(3, sessionRateIds.size());
        assertTrue(sessionRateIds.contains(1001));
        assertTrue(sessionRateIds.contains(1003));

        List<Integer> defaults = sessionRatesValue.stream()
                .filter(r -> Boolean.TRUE.equals(r.getDefaultRate()))
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(1, defaults.size());
        assertEquals(1001, defaults.get(0));
    }

    @Test
    public void updateSeasonTicketRatesNoDefaultButVisibleTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", Boolean.TRUE, Boolean.FALSE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        List<CpanelTarifaRecord> visibleRates = new ArrayList<>();
        visibleRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        visibleRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(sessionRateDao.getRatesBySessionId(any()))
                .thenReturn(visibleRates);

        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);

        verify(commonRatesService, times(0)).unsetDefaultSeasonTicketRate(any(), any());

        verify(sessionRateDao, times(1)).bulkInsertSessionRates(sessionRatesCaptor.capture());

        ArrayList<SessionRate> sessionRatesValue = sessionRatesCaptor.getValue();

        List<Integer> sessionRateIds = sessionRatesValue.stream()
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(3, sessionRateIds.size());
        assertTrue(sessionRateIds.contains(1001));
        assertTrue(sessionRateIds.contains(1002));
        assertTrue(sessionRateIds.contains(1003));

        List<Integer> defaults = sessionRatesValue.stream()
                .filter(r -> Boolean.TRUE.equals(r.getDefaultRate()))
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(1, defaults.size());
        assertEquals(1001, defaults.get(0));
    }

    @Test
    public void updateSeasonTicketRatesDefaultAndVisibleTest() {
        Integer seasonTicketId = 1;
        List<SeasonTicketRateDTO> modifyRates = new ArrayList<>();
        modifyRates.add(createSeasonTicketRateDTO(1002L, "boo", Boolean.TRUE, Boolean.TRUE));

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        seasonTicketRates.add(createTarifaRecord(1002, "boo", Boolean.FALSE));
        seasonTicketRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(rateDao.getSeasonTicketRates(any()))
                .thenReturn(seasonTicketRates);

        List<CpanelTarifaRecord> visibleRates = new ArrayList<>();
        visibleRates.add(createTarifaRecord(1001, "foo", Boolean.TRUE));
        visibleRates.add(createTarifaRecord(1003, "yoo", Boolean.FALSE));
        when(sessionRateDao.getRatesBySessionId(any()))
                .thenReturn(visibleRates);

        seasonTicketRateService.updateSeasonTicketRates(seasonTicketId, modifyRates);

        verify(commonRatesService, times(1)).unsetDefaultSeasonTicketRate(any(), any());

        verify(sessionRateDao, times(1)).bulkInsertSessionRates(sessionRatesCaptor.capture());

        ArrayList<SessionRate> sessionRatesValue = sessionRatesCaptor.getValue();

        List<Integer> sessionRateIds = sessionRatesValue.stream()
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(3, sessionRateIds.size());
        assertTrue(sessionRateIds.contains(1001));
        assertTrue(sessionRateIds.contains(1002));
        assertTrue(sessionRateIds.contains(1003));

        List<Integer> defaults = sessionRatesValue.stream()
                .filter(r -> Boolean.TRUE.equals(r.getDefaultRate()))
                .map(SessionRate::getRateId)
                .collect(Collectors.toList());
        assertEquals(1, defaults.size());
        assertEquals(1002, defaults.get(0));
    }
}

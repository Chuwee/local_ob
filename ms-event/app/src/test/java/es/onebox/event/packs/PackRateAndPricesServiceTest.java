package es.onebox.event.packs;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.PackRateDao;
import es.onebox.event.packs.dto.CreatePackRateDTO;
import es.onebox.event.packs.dto.PackPriceDTO;
import es.onebox.event.packs.dto.UpdatePackPriceDTO;
import es.onebox.event.packs.dto.UpdatePackRateDTO;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.events.dao.record.PriceRecord;
import es.onebox.event.packs.service.PackRateAndPricesService;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PackRateAndPricesServiceTest {

    private static final Integer PACK_ID = 1;
    private static final Integer RATE_ID = 1;
    private static final Integer ZONE_ID = 1;
    private static final Integer ITEM_ID = 1;

    @Mock
    private PackDao packsDao;
    @Mock
    private RateDao rateDao;
    @Mock
    private PackRateDao packRateDao;
    @Mock
    private PackItemsDao packItemsDao;
    @Mock
    private PriceZoneAssignmentDao priceZoneAssignmentDao;
    @Mock
    private SessionDao sessionDao;
    @InjectMocks
    private PackRateAndPricesService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPackRatesTest() {
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setIdtarifa(RATE_ID);
        CpanelTarifaPackRecord packRecord = new CpanelTarifaPackRecord();
        packRecord.setIdtarifa(RATE_ID);
        List<CpanelTarifaPackRecord> rates = List.of(packRecord);
        validatePack();

        when(packRateDao.getRatesByPackId(PACK_ID)).thenReturn(rates);
        when(rateDao.search(any())).thenReturn(List.of(tarifaRecord));

        List<RateDTO> ratesResponse = service.getPackRates(PACK_ID.longValue());

        assertNotNull(ratesResponse);
        assertEquals(ratesResponse.size(), rates.size());
        assertEquals(ratesResponse.get(0).getId().intValue(), tarifaRecord.getIdtarifa());
    }

    @Test
    public void createPackRateTest() {
        CreatePackRateDTO createPackRateDTO = new CreatePackRateDTO();
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setIdtarifa(RATE_ID);
        CpanelTarifaPackRecord packRecord = new CpanelTarifaPackRecord();
        packRecord.setIdtarifa(RATE_ID);
        List<CpanelTarifaPackRecord> rates = List.of(packRecord);
        CpanelTarifaPackRecord tarifaPackRecord = new CpanelTarifaPackRecord();
        tarifaPackRecord.setIdtarifa(RATE_ID);
        tarifaPackRecord.setIdpack(PACK_ID);
        CpanelPackItemRecord mainItem = new CpanelPackItemRecord();
        mainItem.setIditem(ITEM_ID);
        validatePack();

        when(packRateDao.getRatesByPackId(PACK_ID)).thenReturn(rates);
        when(rateDao.insert(any())).thenReturn(tarifaRecord);
        doNothing().when(packRateDao).resetDefaultsByPackId(PACK_ID);
        when(packRateDao.insert(any())).thenReturn(tarifaPackRecord);
        when(packItemsDao.getPackMainItemRecordById(PACK_ID)).thenReturn(mainItem);
        //doNothing().when(packsHelper).createPackVenueTemplatePriceZonesPrices(mainItem, RATE_ID);

        IdDTO rate = service.createPackRate(PACK_ID.longValue(), createPackRateDTO);

        assertNotNull(rate);
        assertEquals(rate.getId(), RATE_ID.longValue());
    }

    @Test
    public void updatePackRateTest() {
        UpdatePackRateDTO updatePackRateDTO = new UpdatePackRateDTO();
        updatePackRateDTO.setDefaultRate(Boolean.TRUE);
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setIdtarifa(RATE_ID);
        CpanelTarifaPackRecord tarifaPackRecord = new CpanelTarifaPackRecord();
        tarifaPackRecord.setIdtarifa(RATE_ID);
        tarifaPackRecord.setIdpack(PACK_ID);
        validatePack();

        when(rateDao.findById(RATE_ID)).thenReturn(tarifaRecord);
        when(packRateDao.findPackRateById(RATE_ID, PACK_ID)).thenReturn(tarifaPackRecord);
        when(rateDao.update(tarifaRecord)).thenReturn(tarifaRecord);
        doNothing().when(packRateDao).resetDefaultsByPackId(PACK_ID);
        when(packRateDao.update(any())).thenReturn(tarifaPackRecord);

        service.updatePackRate(PACK_ID.longValue(), RATE_ID.longValue(), updatePackRateDTO);

        verify(rateDao).findById(RATE_ID);
        verify(packRateDao).findPackRateById(RATE_ID, PACK_ID);
        verify(rateDao).update(tarifaRecord);
        verify(packRateDao).resetDefaultsByPackId(PACK_ID);
        verify(packRateDao).update(any());
    }

    @Test
    public void deletePackRateTest() {
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setIdtarifa(RATE_ID);
        CpanelTarifaPackRecord tarifaPackRecord = new CpanelTarifaPackRecord();
        tarifaPackRecord.setIdtarifa(RATE_ID);
        tarifaPackRecord.setIdpack(PACK_ID);
        validatePack();

        when(rateDao.findById(RATE_ID)).thenReturn(tarifaRecord);
        when(packRateDao.findPackRateById(RATE_ID, PACK_ID)).thenReturn(tarifaPackRecord);
        doNothing().when(priceZoneAssignmentDao).deleteByRateId(RATE_ID);
        when(packRateDao.delete(tarifaPackRecord)).thenReturn(RATE_ID);
        when(rateDao.delete(tarifaRecord)).thenReturn(RATE_ID);

        service.deletePackRate(PACK_ID.longValue(), RATE_ID.longValue());

        verify(priceZoneAssignmentDao).deleteByRateId(RATE_ID);
        verify(packRateDao).delete(tarifaPackRecord);
        verify(rateDao).delete(tarifaRecord);
    }

    @Test
    public void getPackPricesTest() {
        CpanelTarifaPackRecord packRecord = new CpanelTarifaPackRecord();
        packRecord.setIdtarifa(RATE_ID);
        List<CpanelTarifaPackRecord> rates = List.of(packRecord);
        PriceRecord packPriceRecord = new PriceRecord();
        packPriceRecord.setRateId(RATE_ID);
        packPriceRecord.setPriceZoneId(ZONE_ID);
        List<PriceRecord> priceRecords = List.of(packPriceRecord);
        validatePack();

        when(packRateDao.getRatesByPackId(PACK_ID)).thenReturn(rates);
        when(priceZoneAssignmentDao.getPrices(RATE_ID)).thenReturn(priceRecords);

        List<PackPriceDTO> prices = service.getPackPrices(PACK_ID.longValue());

        assertNotNull(prices);
        assertEquals(priceRecords.size(), prices.size());
        assertEquals(prices.get(0).getRateId().intValue(), RATE_ID);
        assertEquals(prices.get(0).getPriceTypeId().intValue(), ZONE_ID);
    }

    @Test
    public void updatePackPrices() {
        UpdatePackPriceDTO updatePackPriceDTO = new UpdatePackPriceDTO();
        updatePackPriceDTO.setRateId(RATE_ID);
        updatePackPriceDTO.setPriceTypeId(ZONE_ID);
        updatePackPriceDTO.setPrice(0d);
        List<UpdatePackPriceDTO> prices = List.of(updatePackPriceDTO);
        validatePack();

        doNothing().when(priceZoneAssignmentDao).updatePrices(ZONE_ID, RATE_ID, 0d);

        service.updatePackPrices(PACK_ID.longValue(), prices);

        verify(priceZoneAssignmentDao).updatePrices(ZONE_ID, RATE_ID, 0d);
    }

    @Test
    public void refreshPacks_empty() {
        validatePack();

        when(packRateDao.getRatesByPackId(PACK_ID)).thenReturn(List.of());

        CpanelPackItemRecord mainItem = new CpanelPackItemRecord();
        mainItem.setIditem(ITEM_ID);
        mainItem.setTipoitem(PackItemType.EVENT.getId());
        when(packItemsDao.getPackMainItemRecordById(PACK_ID)).thenReturn(mainItem);

        //main item rates
        CpanelTarifaRecord rate1 = new CpanelTarifaRecord();
        rate1.setIdtarifa(11);
        rate1.setDefecto((byte) 1);
        rate1.setNombre("rate1");
        CpanelTarifaRecord rate2 = new CpanelTarifaRecord();
        rate2.setIdtarifa(12);
        rate2.setNombre("rate2");
        when(rateDao.getEventRates(ITEM_ID)).thenReturn(List.of(rate1, rate2));

        //New rates for pack
        doAnswer(a -> {
            CpanelTarifaRecord rateRequest = (CpanelTarifaRecord) a.getArguments()[0];
            CpanelTarifaRecord response = new CpanelTarifaRecord();
            if (rateRequest.getNombre().equals("rate1")) {
                assertEquals("rate1", rateRequest.getNombre());
                assertEquals(1, rateRequest.getDefecto().intValue());
                response.setIdtarifa(111);

            } else {
                assertEquals("rate2", rateRequest.getNombre());
                assertEquals(0, rateRequest.getDefecto().intValue());
                response.setIdtarifa(112);
            }
            return response;
        }).when(rateDao).insert(any());

        //New pack_rates with rates relation
        doAnswer(a -> {
            CpanelTarifaPackRecord rateRequest = (CpanelTarifaPackRecord) a.getArguments()[0];
            if (rateRequest.getIdtarifa().equals(111)) {
                assertEquals(11, rateRequest.getIdtarifaevento());
                assertEquals(PACK_ID, rateRequest.getIdpack());
            } else {
                assertEquals(112, rateRequest.getIdtarifa());
                assertEquals(PACK_ID, rateRequest.getIdpack());
                assertEquals(12, rateRequest.getIdtarifaevento());
            }

            CpanelTarifaRecord newRate1 = new CpanelTarifaRecord();
            newRate1.setIdtarifa(1);
            return newRate1;
        }).when(packRateDao).insert(any());

        service.refreshPackRates(PACK_ID.longValue());

        verify(rateDao, times(2)).insert(any());
        verify(packRateDao, times(2)).insert(any());
    }

    @Test
    public void refreshPacks_existing_legacy() {
        validatePack();

        int packRateId = 111;

        CpanelTarifaPackRecord currentPackRate = new CpanelTarifaPackRecord();
        currentPackRate.setIdtarifa(packRateId);
        currentPackRate.setDefecto(true);
        currentPackRate.setIdpack(PACK_ID);
        when(packRateDao.getRatesByPackId(PACK_ID)).thenReturn(List.of(currentPackRate));
        CpanelTarifaRecord existingRate = new CpanelTarifaRecord();
        existingRate.setIdtarifa(packRateId);
        when(rateDao.findById(packRateId)).thenReturn(existingRate);
        when(packRateDao.findPackRateById(packRateId, PACK_ID)).thenReturn(currentPackRate);

        CpanelPackItemRecord mainItem = new CpanelPackItemRecord();
        mainItem.setIditem(ITEM_ID);
        mainItem.setTipoitem(PackItemType.SESSION.getId());
        when(packItemsDao.getPackMainItemRecordById(PACK_ID)).thenReturn(mainItem);

        CpanelSesionRecord session = new CpanelSesionRecord();
        session.setIdrelacionentidadrecinto(1);
        when(sessionDao.findById(ITEM_ID)).thenReturn(session);

        //main item rates
        CpanelTarifaRecord rate1 = new CpanelTarifaRecord();
        rate1.setIdtarifa(11);
        rate1.setDefecto((byte) 1);
        rate1.setNombre("rate1");
        CpanelTarifaRecord rate2 = new CpanelTarifaRecord();
        rate2.setIdtarifa(12);
        rate2.setNombre("rate2");
        when(rateDao.getRatesBySession(ITEM_ID)).thenReturn(List.of(rate1, rate2));

        //New rates for pack
        doAnswer(a -> {
            CpanelTarifaRecord rateRequest = (CpanelTarifaRecord) a.getArguments()[0];
            CpanelTarifaRecord response = new CpanelTarifaRecord();
            assertEquals("rate1", rateRequest.getNombre());
            assertEquals(1, rateRequest.getDefecto().intValue());
            response.setIdtarifa(111);
            return response;
        }).when(rateDao).update(any());


        //New rates for pack
        doAnswer(a -> {
            CpanelTarifaRecord rateRequest = (CpanelTarifaRecord) a.getArguments()[0];
            CpanelTarifaRecord response = new CpanelTarifaRecord();
            assertEquals("rate2", rateRequest.getNombre());
            assertEquals(0, rateRequest.getDefecto().intValue());
            response.setIdtarifa(112);
            return response;
        }).when(rateDao).insert(any());

        //New pack_rates with rates relation
        doAnswer(a -> {
            CpanelTarifaPackRecord rateRequest = (CpanelTarifaPackRecord) a.getArguments()[0];
            assertEquals(112, rateRequest.getIdtarifa());
            assertEquals(PACK_ID, rateRequest.getIdpack());
            assertEquals(12, rateRequest.getIdtarifaevento());
            CpanelTarifaRecord response = new CpanelTarifaRecord();
            response.setIdtarifa(1);
            return response;
        }).when(packRateDao).insert(any());

        service.refreshPackRates(PACK_ID.longValue());

        verify(rateDao, times(1)).update(any());
        verify(rateDao, times(1)).insert(any());

        verify(packRateDao, times(1)).insert(any());
        verify(packRateDao, times(1)).update(any());

    }

    private void validatePack() {
        CpanelPackRecord packRecord = new CpanelPackRecord();
        packRecord.setTipo(PackType.AUTOMATIC.getId());
        when(packsDao.getPackRecordById(PACK_ID)).thenReturn(packRecord);
    }
}

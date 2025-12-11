package es.onebox.event.seasontickets.service.changeseats;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.seasontickets.dao.SeasonTicketChangeSeatDao;
import es.onebox.event.seasontickets.dao.SeasonTicketChangeSeatPricesDao;
import es.onebox.event.seasontickets.dao.SeasonTicketChangedSeatQuotasDao;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelation;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelationDTO;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelations;
import es.onebox.event.seasontickets.dto.changeseat.LimitChangeSeatQuotas;
import es.onebox.event.seasontickets.dto.changeseat.UpdateChangeSeatSeasonTicketPriceRelation;
import es.onebox.event.seasontickets.dto.changeseat.UpdateChangeSeatSeasonTicketPriceRelations;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatPricesRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SeasonTicketChangeSeatsServiceTest {

    @Mock
    private SeasonTicketChangeSeatPricesDao pricesDao;
    @Mock
    private SeasonTicketDao seasonTicketDao;
    @Mock
    private SeasonTicketChangeSeatDao changeSeatDao;
    @Mock
    private SeasonTicketChangedSeatQuotasDao quotasDao;
    @InjectMocks
    private SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;
    @Captor
    private ArgumentCaptor<List<CpanelSeasonTicketChangeSeatPricesRecord>> captor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchChangeSeatPricesTable() {
        Long seasonTicketId = 1L;
        when(pricesDao.searchChangeSeatPricesTable(seasonTicketId, null)).thenReturn(new ArrayList<>());

        List<ChangeSeatSeasonTicketPriceRelationDTO> result =
                seasonTicketChangeSeatsService.searchChangeSeatPricesTable(seasonTicketId, null);

        verify(pricesDao, times(1)).searchChangeSeatPricesTable(seasonTicketId, null);
        assertEquals(0, result.size());
    }


    @Test
    public void testCreateChangeSeatPricesRelations_NoPreviousRelations() {
        Long seasonTicketId = 1L;
        ChangeSeatSeasonTicketPriceRelations priceRelations = getPriceRelations();
        when(pricesDao.searchChangeSeatPricesTable(eq(seasonTicketId), isNull())).thenReturn(new ArrayList<>());

        seasonTicketChangeSeatsService.createChangeSeatPricesRelations(seasonTicketId, priceRelations);

        verify(pricesDao, times(1)).bulkInsertRecords(captor.capture());
        List<CpanelSeasonTicketChangeSeatPricesRecord> capturedInsertList = captor.getValue();
        assertEquals(capturedInsertList.size(), priceRelations.size());
    }

    @Test
    public void testCreateChangeSeatPricesRelations_PreviousRelations() {
        Long seasonTicketId = 1L;
        ChangeSeatSeasonTicketPriceRelations priceRelations = getPriceRelations();
        List<SeasonTicketChangeSeatPricesRecord> pricesTable = getPriceRecords();
        when(pricesDao.searchChangeSeatPricesTable(eq(seasonTicketId), isNull())).thenReturn(pricesTable);

        seasonTicketChangeSeatsService.createChangeSeatPricesRelations(seasonTicketId, priceRelations);

        verify(pricesDao, times(1)).bulkInsertRecords(captor.capture());
        List<CpanelSeasonTicketChangeSeatPricesRecord> capturedInsertList = captor.getValue();
        assertEquals(capturedInsertList.size(), 1);
        CpanelSeasonTicketChangeSeatPricesRecord insertedRecord = capturedInsertList.get(0);
        assertEquals(1, insertedRecord.getIdsourcepricetype());
        assertEquals(3, insertedRecord.getIdtargetpricetype());
        assertEquals(2, insertedRecord.getIdrate());
    }

    @Test
    public void testUpdateChangeSeatPricesRelations_RelationsToUpdateExist() {
        Long seasonTicketId = 1L;
        UpdateChangeSeatSeasonTicketPriceRelations updatePriceRelations = getUpdatePriceRelations();
        List<SeasonTicketChangeSeatPricesRecord> pricesTable = getPriceRecords();
        when(pricesDao.searchChangeSeatPricesTable(eq(seasonTicketId), isNull())).thenReturn(pricesTable);

        seasonTicketChangeSeatsService.updateChangeSeatPricesRelations(seasonTicketId, updatePriceRelations);

        verify(pricesDao, times(1)).bulkUpdateRecords(captor.capture());
        List<CpanelSeasonTicketChangeSeatPricesRecord> capturedInsertList = captor.getValue();
        assertEquals(capturedInsertList.size(), updatePriceRelations.size());
        assertEquals(
                capturedInsertList.stream().filter(r -> r.getIdpricerelation() == 1 || r.getIdpricerelation() == 2).count(),
                updatePriceRelations.size()
        );
    }


    @Test
    public void testUpdateChangeSeatPricesRelations_RelationNotFound() {
        Long seasonTicketId = 1L;
        UpdateChangeSeatSeasonTicketPriceRelations updatePriceRelations = getUpdatePriceRelations();
        updatePriceRelations.add(createUpdatePriceRelation(8L, 3.0));

        List<SeasonTicketChangeSeatPricesRecord> pricesTable = getPriceRecords();
        when(pricesDao.searchChangeSeatPricesTable(eq(seasonTicketId), isNull())).thenReturn(pricesTable);

        seasonTicketChangeSeatsService.updateChangeSeatPricesRelations(seasonTicketId, updatePriceRelations);

        verify(pricesDao, times(1)).bulkUpdateRecords(captor.capture());
        List<CpanelSeasonTicketChangeSeatPricesRecord> capturedInsertList = captor.getValue();
        assertEquals(2, capturedInsertList.size());
        assertEquals(
                2,
                capturedInsertList.stream()
                        .filter(r -> r.getIdpricerelation() == 1 || r.getIdpricerelation() == 2 || r.getIdpricerelation() == 8).count()
        );
    }

    private ChangeSeatSeasonTicketPriceRelations getPriceRelations() {
        ChangeSeatSeasonTicketPriceRelations priceRelations = new ChangeSeatSeasonTicketPriceRelations();
        priceRelations.add(getPriceRelation(1L, 2L, 1L, 5.0));
        priceRelations.add(getPriceRelation(1L, 3L, 1L, 7.0));
        priceRelations.add(getPriceRelation(1L, 2L, 2L, 8.0));
        priceRelations.add(getPriceRelation(1L, 3L, 2L, 9.0));
        return priceRelations;
    }

    private ChangeSeatSeasonTicketPriceRelation getPriceRelation(Long sourcePriceTypeId, Long targetPriceTypeId, Long rateId, Double value) {
        ChangeSeatSeasonTicketPriceRelation relation = new ChangeSeatSeasonTicketPriceRelation();
        relation.setSourcePriceTypeId(sourcePriceTypeId);
        relation.setTargetPriceTypeId(targetPriceTypeId);
        relation.setRateId(rateId);
        relation.setValue(value);
        return relation;
    }

    private List<SeasonTicketChangeSeatPricesRecord> getPriceRecords() {
        return List.of(
                getPriceRecord(1, 1, 2, 1),
                getPriceRecord(2, 1, 3, 1),
                getPriceRecord(3, 1, 2, 2)
        );
    }
    private SeasonTicketChangeSeatPricesRecord getPriceRecord(Integer id, Integer sourcePrice, Integer targetPrice, Integer rateId) {
        SeasonTicketChangeSeatPricesRecord seasonTicketChangeSeatPricesRecord = new SeasonTicketChangeSeatPricesRecord();
        seasonTicketChangeSeatPricesRecord.setIdpricerelation(id);
        seasonTicketChangeSeatPricesRecord.setIdsourcepricetype(sourcePrice);
        seasonTicketChangeSeatPricesRecord.setIdtargetpricetype(targetPrice);
        seasonTicketChangeSeatPricesRecord.setIdrate(rateId);
        return seasonTicketChangeSeatPricesRecord;
    }

    private UpdateChangeSeatSeasonTicketPriceRelations getUpdatePriceRelations() {
        UpdateChangeSeatSeasonTicketPriceRelations updatePriceRelations = new UpdateChangeSeatSeasonTicketPriceRelations();
        updatePriceRelations.add(createUpdatePriceRelation(1L, 5.0));
        updatePriceRelations.add(createUpdatePriceRelation(2L, 7.0));
        return updatePriceRelations;
    }

    private UpdateChangeSeatSeasonTicketPriceRelation createUpdatePriceRelation(Long id, Double value) {
        UpdateChangeSeatSeasonTicketPriceRelation relation = new UpdateChangeSeatSeasonTicketPriceRelation();
        relation.setRelationId(id);
        relation.setValue(value);
        return relation;
    }

    @Test
    public void testUpdateSeasonTicketChangeSeat() {
        Integer seasonTicketId = 1;
        UpdateSeasonTicketChangeSeat updateChangeSeat = ObjectRandomizer.random(UpdateSeasonTicketChangeSeat.class);
        LimitChangeSeatQuotas limit = new LimitChangeSeatQuotas();
        limit.setEnable(true);
        limit.setQuotaIds(List.of(1, 2));
        updateChangeSeat.setLimitChangeSeatQuotas(limit);

        CpanelSeasonTicketRecord seasonTicketRecord = new CpanelSeasonTicketRecord();
        when(seasonTicketDao.getById(seasonTicketId)).thenReturn(seasonTicketRecord);
        CpanelSeasonTicketChangeSeatRecord changeSeatRecord = new CpanelSeasonTicketChangeSeatRecord();
        when(changeSeatDao.getById(seasonTicketId)).thenReturn(changeSeatRecord);

        seasonTicketChangeSeatsService.updateSeasonTicketChangeSeat(seasonTicketId, updateChangeSeat);

        verify(seasonTicketDao, times(1)).getById(seasonTicketId);
        verify(seasonTicketDao, times(1)).update(
                SeasonTicketChangeSeatConverter.toRecord(seasonTicketRecord, updateChangeSeat)
        );

        verify(changeSeatDao, times(1)).insertOrUpdate(seasonTicketId, limit.getEnable());

        verify(quotasDao, times(1)).deleteBySeasonTicketId(seasonTicketId);
        verify(quotasDao, times(1)).insert(seasonTicketId, limit.getQuotaIds());
    }

    @Test
    public void testUpdateSeasonTicketChangeSeat_ChangeSeatQuotasAreNotLimited() {
        Integer seasonTicketId = 1;
        UpdateSeasonTicketChangeSeat updateChangeSeat = ObjectRandomizer.random(UpdateSeasonTicketChangeSeat.class);
        LimitChangeSeatQuotas limit = new LimitChangeSeatQuotas();
        limit.setEnable(false);
        updateChangeSeat.setLimitChangeSeatQuotas(limit);

        CpanelSeasonTicketRecord seasonTicketRecord = new CpanelSeasonTicketRecord();
        when(seasonTicketDao.getById(seasonTicketId)).thenReturn(seasonTicketRecord);
        CpanelSeasonTicketChangeSeatRecord changeSeatRecord = new CpanelSeasonTicketChangeSeatRecord();
        when(changeSeatDao.getById(seasonTicketId)).thenReturn(changeSeatRecord);

        seasonTicketChangeSeatsService.updateSeasonTicketChangeSeat(seasonTicketId, updateChangeSeat);

        verify(seasonTicketDao, times(1)).getById(seasonTicketId);
        verify(seasonTicketDao, times(1)).update(
                SeasonTicketChangeSeatConverter.toRecord(seasonTicketRecord, updateChangeSeat)
        );

        verify(changeSeatDao, times(1)).insertOrUpdate(seasonTicketId, limit.getEnable());

        verify(quotasDao, times(1)).deleteBySeasonTicketId(seasonTicketId);
        verifyNoMoreInteractions(quotasDao);
    }

}

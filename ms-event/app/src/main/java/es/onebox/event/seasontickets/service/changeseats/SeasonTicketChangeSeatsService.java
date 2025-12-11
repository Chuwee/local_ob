package es.onebox.event.seasontickets.service.changeseats;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.seasontickets.dao.SeasonTicketChangeSeatDao;
import es.onebox.event.seasontickets.dao.SeasonTicketChangeSeatPricesDao;
import es.onebox.event.seasontickets.dao.SeasonTicketChangedSeatQuotasDao;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceFilter;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelationDTO;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelations;
import es.onebox.event.seasontickets.dto.changeseat.LimitChangeSeatQuotas;
import es.onebox.event.seasontickets.dto.changeseat.SeasonTicketChangeSeatDTO;
import es.onebox.event.seasontickets.dto.changeseat.UpdateChangeSeatSeasonTicketPriceRelations;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatPricesRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonTicketChangeSeatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonTicketChangeSeatsService.class);
    private final SeasonTicketDao seasonTicketDao;
    private final SeasonTicketChangeSeatPricesDao pricesDao;
    private final SeasonTicketChangeSeatDao changeSeatDao;
    private final SeasonTicketChangedSeatQuotasDao quotasDao;

    @Autowired
    public SeasonTicketChangeSeatsService(SeasonTicketDao seasonTicketDao, SeasonTicketChangeSeatPricesDao pricesDao,
                                          SeasonTicketChangeSeatDao changeSeatDao, SeasonTicketChangedSeatQuotasDao quotasDao) {
        this.seasonTicketDao = seasonTicketDao;
        this.pricesDao = pricesDao;
        this.changeSeatDao = changeSeatDao;
        this.quotasDao = quotasDao;
    }

    public List<ChangeSeatSeasonTicketPriceRelationDTO> searchChangeSeatPricesTable(Long seasonTicketId, ChangeSeatSeasonTicketPriceFilter seasonTicketPriceFilter) {
        return pricesDao.searchChangeSeatPricesTable(seasonTicketId, seasonTicketPriceFilter).stream().map(SeasonTicketChangeSeatConverter::toDTO).collect(Collectors.toList());
    }

    public void createChangeSeatPricesRelations(Long seasonTicketId, ChangeSeatSeasonTicketPriceRelations priceRelations) {
        if (CollectionUtils.isEmpty(priceRelations)) {
            return;
        }
        List<SeasonTicketChangeSeatPricesRecord> pricesTable = pricesDao.searchChangeSeatPricesTable(seasonTicketId, null);
        List<CpanelSeasonTicketChangeSeatPricesRecord> insertList = priceRelations.stream()
                .filter(price -> pricesTable.stream().noneMatch(p -> price.getSourcePriceTypeId().equals(p.getIdsourcepricetype().longValue())
                        && price.getTargetPriceTypeId().equals(p.getIdtargetpricetype().longValue()) && price.getRateId().equals(p.getIdrate().longValue())))
                .map(price -> SeasonTicketChangeSeatConverter.toRecord(seasonTicketId, price))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(insertList)) {
            pricesDao.bulkInsertRecords(insertList);
        }
    }

    public void updateChangeSeatPricesRelations(Long seasonTicketId, UpdateChangeSeatSeasonTicketPriceRelations updatePriceRelations) {
        if (CollectionUtils.isEmpty(updatePriceRelations)) {
            return;
        }
        List<SeasonTicketChangeSeatPricesRecord> pricesTable = pricesDao.searchChangeSeatPricesTable(seasonTicketId, null);
        List<CpanelSeasonTicketChangeSeatPricesRecord> updateList = updatePriceRelations.stream()
                .filter(price -> pricesTable.stream().anyMatch(p -> price.getRelationId().equals(p.getIdpricerelation().longValue())))
                .map(SeasonTicketChangeSeatConverter::toRecord)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(updateList)) {
            pricesDao.bulkUpdateRecords(updateList);
        }
    }

    public SeasonTicketChangeSeatDTO getSeasonTicketChangeSeat(Integer seasonTicketId) {
        return SeasonTicketChangeSeatConverter.fromRecords(
                seasonTicketDao.getById(seasonTicketId),
                changeSeatDao.findById(seasonTicketId),
                quotasDao.getBySeasonTicketId(seasonTicketId)
        );
    }

    @MySQLWrite
    public void updateSeasonTicketChangeSeat(Integer seasonTicketId, UpdateSeasonTicketChangeSeat updateChangeSeat) {
        seasonTicketDao.update(
            SeasonTicketChangeSeatConverter.toRecord(seasonTicketDao.getById(seasonTicketId), updateChangeSeat
        ));
        LimitChangeSeatQuotas limitChangeSeatQuotas = updateChangeSeat.getLimitChangeSeatQuotas();
        if (limitChangeSeatQuotas != null) {
            validateLimitChangeSeatQuota(limitChangeSeatQuotas);
            changeSeatDao.insertOrUpdate(seasonTicketId, limitChangeSeatQuotas.getEnable());
            quotasDao.deleteBySeasonTicketId(seasonTicketId);
            if (limitChangeSeatQuotas.getEnable()) {
                quotasDao.insert(seasonTicketId, limitChangeSeatQuotas.getQuotaIds());
            }
        }
    }

    private void validateLimitChangeSeatQuota(LimitChangeSeatQuotas limitChangeSeatQuotas) {
        if (BooleanUtils.isTrue(limitChangeSeatQuotas.getEnable()) &&
                CollectionUtils.isEmpty(limitChangeSeatQuotas.getQuotaIds())) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

}

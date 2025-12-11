package es.onebox.event.surcharges.manager;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventSecondaryMarketDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoMercadoSecundarioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

import java.util.List;
import java.util.stream.Collectors;

public class SurchargeManagerSecondaryMarketChannel extends SurchargeManager {

    private final RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao;

    public SurchargeManagerSecondaryMarketChannel(RangeDao rangeDao, RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao, EventDao eventDao) {
        super(rangeDao, eventDao);
        this.rangeSurchargeEventSecMktDao = rangeSurchargeEventSecMktDao;
    }

    @Override
    public void insertSurchages(Long eventId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelRangoRecargoEventoMercadoSecundarioRecord(eventId.intValue(), rangeId))
                .forEach(record -> rangeSurchargeEventSecMktDao.insert(record));
    }

    @Override
    protected void insertLimits(Long eventId) {
        // Event level secondary market range limits are not allowed. See validations related to MsEventERrorCode.SECONDARY_MARKET_CANT_HAVE_LIMIT
    }

    @Override
    public void deleteSurchargesAndRanges(Long eventId) {
        List<Integer> rangeIds = rangeSurchargeEventSecMktDao.getByEventId(eventId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        rangeSurchargeEventSecMktDao.deleteByEventId(eventId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }

}

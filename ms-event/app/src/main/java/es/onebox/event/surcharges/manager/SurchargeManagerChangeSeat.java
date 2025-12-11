package es.onebox.event.surcharges.manager;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventChangeSeatDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoCambioLocalidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

import java.util.List;
import java.util.stream.Collectors;

public class SurchargeManagerChangeSeat extends SurchargeManager {

    private final RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao;

    public SurchargeManagerChangeSeat(RangeDao rangeDao, RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao, EventDao eventDao) {
        super(rangeDao, eventDao);
        this.rangeSurchargeEventChangeSeatDao = rangeSurchargeEventChangeSeatDao;
    }

    @Override
    public void insertSurchages(Long eventId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelRangoRecargoEventoCambioLocalidadRecord(eventId.intValue(), rangeId))
                .forEach(record -> rangeSurchargeEventChangeSeatDao.insert(record));
    }

    @Override
    protected void insertLimits(Long eventId) {
    }

    @Override
    public void deleteSurchargesAndRanges(Long eventId) {
        List<Integer> rangeIds = rangeSurchargeEventChangeSeatDao.getByEventId(eventId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        rangeSurchargeEventChangeSeatDao.deleteByEventId(eventId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }

}

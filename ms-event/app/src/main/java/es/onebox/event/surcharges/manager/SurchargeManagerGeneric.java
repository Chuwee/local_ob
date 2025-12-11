package es.onebox.event.surcharges.manager;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.jooq.Field;

import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;

public class SurchargeManagerGeneric extends SurchargeManager {

    RangeSurchargeEventDao rangeSurchargeEventDao;

    public SurchargeManagerGeneric(RangeDao rangeDao, RangeSurchargeEventDao rangeSurchargeEventDao, EventDao eventDao) {
        super(rangeDao, eventDao);
        this.rangeSurchargeEventDao = rangeSurchargeEventDao;
    }

    @Override
    protected void insertSurchages(Long eventId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelRangoRecargoEventoRecord(eventId.intValue(), rangeId))
                .forEach(record -> rangeSurchargeEventDao.insert(record));
    }

    @Override
    protected void insertLimits(Long eventId) {
        if (getLimit() != null) {
            EventRecord eventRecord = new EventRecord();
            eventRecord.setIdevento(eventId.intValue());
            eventRecord.setRecomendarrecargoscanal(getLimit().getEnabled() ? (byte)1 :0);
            if (getLimit().getEnabled()) {
                eventRecord.setRecargominimo(getLimit().getMin());
                eventRecord.setRecargomaximo(getLimit().getMax());
            }
            eventDao.update(eventRecord, new Field[] {CPANEL_EVENTO.RECOMENDARRECARGOSCANAL, CPANEL_EVENTO.RECARGOMINIMO, CPANEL_EVENTO.RECARGOMAXIMO});
        }
    }

    @Override
    public void deleteSurchargesAndRanges(Long eventId) {
        List<Integer> rangeIds = rangeSurchargeEventDao.getByEventId(eventId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        rangeSurchargeEventDao.deleteByEventId(eventId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }
}

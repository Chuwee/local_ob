package es.onebox.event.surcharges.manager;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventInvitationDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoInvRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.jooq.Field;

import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;

public class SurchargeManagerInvitation extends SurchargeManager {

    RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao;

    public SurchargeManagerInvitation(RangeDao rangeDao, RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao, EventDao eventDao) {
        super(rangeDao, eventDao);
        this.rangeSurchargeEventInvitationDao = rangeSurchargeEventInvitationDao;
    }

    @Override
    public void insertSurchages(Long eventId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelRangoRecargoEventoInvRecord(eventId.intValue(), rangeId))
                .forEach(record -> rangeSurchargeEventInvitationDao.insert(record));
    }

    @Override
    protected void insertLimits(Long eventId) {
        if (getLimit() != null) {
            EventRecord eventRecord = new EventRecord();
            eventRecord.setIdevento(eventId.intValue());
            eventRecord.setRecomendarrecargosinvcanal(getLimit().getEnabled() ? (byte)1 :0);
            if (getLimit().getEnabled()) {
                eventRecord.setRecargoinvminimo(getLimit().getMin());
                eventRecord.setRecargoinvmaximo(getLimit().getMax());
            }
            eventDao.update(eventRecord, new Field[] {CPANEL_EVENTO.RECOMENDARRECARGOSINVCANAL, CPANEL_EVENTO.RECARGOINVMINIMO, CPANEL_EVENTO.RECARGOINVMAXIMO});
        }
    }

    @Override
    public void deleteSurchargesAndRanges(Long eventId) {
        List<Integer> rangeIds = rangeSurchargeEventInvitationDao.getByEventId(eventId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        rangeSurchargeEventInvitationDao.deleteByEventId(eventId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }

}

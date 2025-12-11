package es.onebox.event.surcharges.manager;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventPromotionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoPromocionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.jooq.Field;

import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;

public class SurchargeManagerPromotion extends SurchargeManager {

    RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao;

    public SurchargeManagerPromotion(RangeDao rangeDao, RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao, EventDao eventDao) {
        super(rangeDao, eventDao);
        this.rangeSurchargeEventPromotionDao = rangeSurchargeEventPromotionDao;
    }

    @Override
    public void insertSurchages(Long eventId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelRangoRecargoEventoPromocionRecord(eventId.intValue(), rangeId))
                .forEach(record -> rangeSurchargeEventPromotionDao.insert(record));
    }

    @Override
    protected void insertLimits(Long eventId) {
        if (getLimit() != null) {
            EventRecord eventRecord = new EventRecord();
            eventRecord.setIdevento(eventId.intValue());
            eventRecord.setRecomendarrecargospromocioncanal(getLimit().getEnabled() ? (byte)1 :0);
            if (getLimit().getEnabled()) {
                eventRecord.setRecargopromocionminimo(getLimit().getMin());
                eventRecord.setRecargopromocionmaximo(getLimit().getMax());
            }
            eventRecord.setAllowchannelusealternativecharges(isAllowChannelUseAlternativeCharges());
            eventDao.update(eventRecord, new Field[] {CPANEL_EVENTO.RECOMENDARRECARGOSPROMOCIONCANAL, CPANEL_EVENTO.RECARGOPROMOCIONMINIMO, CPANEL_EVENTO.RECARGOPROMOCIONMAXIMO, CPANEL_EVENTO.ALLOWCHANNELUSEALTERNATIVECHARGES});
        }
    }

    @Override
    public void deleteSurchargesAndRanges(Long eventId) {
        List<Integer> rangeIds = rangeSurchargeEventPromotionDao.getByEventId(eventId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        rangeSurchargeEventPromotionDao.deleteByEventId(eventId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }

}

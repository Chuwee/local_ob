package es.onebox.event.events.manager;


import es.onebox.event.events.dao.ChannelEventPromotionSurchargeRangeDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoCanalEventoPromocionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.jooq.Field;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.tables.CpanelCanalEvento.CPANEL_CANAL_EVENTO;

public class ChannelEventSurchargeManagerPromotion extends ChannelEventSurchargeManager{
    ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao;

    public ChannelEventSurchargeManagerPromotion(RangeDao rangeDao, ChannelEventDao channelEventDao, ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao) {
        super(rangeDao, channelEventDao);
        this.channelEventPromotionSurchargeRangeDao = channelEventPromotionSurchargeRangeDao;
    }

    @Override
    protected void insertChannelEventSurcharges(Integer channelEventId, List<Integer> rangeIds){
        Timestamp now = Timestamp.from(Instant.now());
        rangeIds.stream().map(rangeId -> new CpanelRangoRecargoCanalEventoPromocionRecord(channelEventId, rangeId, now, now ))
                .forEach(record -> channelEventPromotionSurchargeRangeDao.insert(record));
    }

    protected void insertChannelEventConfiguration(Integer channelEventId){
        CpanelCanalEventoRecord channelEventRecord = new CpanelCanalEventoRecord();
        channelEventRecord.setIdcanaleevento(channelEventId.intValue());
        channelEventRecord.setUsarecargoeventopromocion(getEnabledRanges()? 0 : (byte)1);
        if(getLimit()!= null){
            channelEventRecord.setRecomendarrecargospromocioncanal(getLimit().getEnabled() ? (byte)1 :0);
            if (getLimit().getEnabled()) {
                channelEventRecord.setRecargopromocionminimo(getLimit().getMin());
                channelEventRecord.setRecargopromocionmaximo(getLimit().getMax());
            }
            channelEventRecord.setAllowchannelusealternativecharges(isAllowChannelUseAlternativeCharges());
        }
        channelEventDao.update(channelEventRecord, new Field[]{CPANEL_CANAL_EVENTO.RECOMENDARRECARGOSPROMOCIONCANAL,
                CPANEL_CANAL_EVENTO.RECARGOPROMOCIONMINIMO, CPANEL_CANAL_EVENTO.RECARGOPROMOCIONMAXIMO, CPANEL_CANAL_EVENTO.USARECARGOEVENTOPROMOCION, CPANEL_CANAL_EVENTO.ALLOWCHANNELUSEALTERNATIVECHARGES});
    }

    public void deleteChannelEventSurchargesAndRanges(Integer channelEventId){
        List<Integer> rangeIds = channelEventPromotionSurchargeRangeDao.getByChannelEventId(channelEventId).stream()
                .map(CpanelRangoRecord::getIdrango).collect(Collectors.toList());
        channelEventPromotionSurchargeRangeDao.deleteByChannelEventId(channelEventId);
        rangeDao.deleteByIds(rangeIds);
    }
}

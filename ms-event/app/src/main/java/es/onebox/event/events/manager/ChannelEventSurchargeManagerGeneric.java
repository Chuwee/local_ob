package es.onebox.event.events.manager;

import es.onebox.event.events.dao.ChannelEventSurchargeRangeDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.jooq.Field;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.tables.CpanelCanalEvento.CPANEL_CANAL_EVENTO;

public class ChannelEventSurchargeManagerGeneric  extends ChannelEventSurchargeManager{

    ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;

    public ChannelEventSurchargeManagerGeneric(RangeDao rangeDao,  ChannelEventDao channelEventDao, ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao) {
        super(rangeDao, channelEventDao);
        this.channelEventSurchargeRangeDao = channelEventSurchargeRangeDao;
    }

    protected void insertChannelEventSurcharges(Integer channelEventId, List<Integer> rangeIds){
        Timestamp now = Timestamp.from(Instant.now());
        rangeIds.stream().map(rangeId -> new CpanelRangoRecargoCanalEventoRecord(channelEventId, rangeId, now, now ))
                .forEach(record -> channelEventSurchargeRangeDao.insert(record));
    }

    protected void insertChannelEventConfiguration(Integer channelEventId){
        CpanelCanalEventoRecord channelEventRecord = new CpanelCanalEventoRecord();
        channelEventRecord.setIdcanaleevento(channelEventId.intValue());
        channelEventRecord.setUsarecargoevento(getEnabledRanges()? 0 : (byte)1);
        if(getLimit()!= null){
            channelEventRecord.setRecomendarrecargoscanal(getLimit().getEnabled() ? (byte)1 :0);
            if (getLimit().getEnabled()) {
                channelEventRecord.setRecargominimo(getLimit().getMin());
                channelEventRecord.setRecargomaximo(getLimit().getMax());
            }
        }
        channelEventDao.update(channelEventRecord, new Field[]{CPANEL_CANAL_EVENTO.RECOMENDARRECARGOSCANAL,
                CPANEL_CANAL_EVENTO.RECARGOMINIMO, CPANEL_CANAL_EVENTO.RECARGOMAXIMO, CPANEL_CANAL_EVENTO.USARECARGOEVENTO});
    }

    public void deleteChannelEventSurchargesAndRanges(Integer channelEventId){
        List<Integer> rangeIds = channelEventSurchargeRangeDao.getByChannelEventId(channelEventId).stream()
                .map(CpanelRangoRecord::getIdrango).collect(Collectors.toList());
        channelEventSurchargeRangeDao.deleteByChannelEventId(channelEventId);
        rangeDao.deleteByIds(rangeIds);
    }
}

package es.onebox.event.events.manager;


import es.onebox.event.events.dao.ChannelEventInvitationSurchargeRangeDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoCanalEventoInvRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.jooq.Field;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.tables.CpanelCanalEvento.CPANEL_CANAL_EVENTO;

public class ChannelEventSurchargeManagerInvitation extends ChannelEventSurchargeManager{
    ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao;

    public ChannelEventSurchargeManagerInvitation(RangeDao rangeDao, ChannelEventDao channelEventDao, ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao) {
        super(rangeDao, channelEventDao);
        this.channelEventInvitationSurchargeRangeDao = channelEventInvitationSurchargeRangeDao;
    }

    @Override
    protected void insertChannelEventSurcharges(Integer channelEventId, List<Integer> rangeIds){
        Timestamp now = Timestamp.from(Instant.now());
        rangeIds.stream().map(rangeId -> new CpanelRangoRecargoCanalEventoInvRecord(channelEventId, rangeId, now, now ))
                .forEach(record -> channelEventInvitationSurchargeRangeDao.insert(record));
    }

    protected void insertChannelEventConfiguration(Integer channelEventId){
        if(getLimit()!= null){
            CpanelCanalEventoRecord channelEventRecord = new CpanelCanalEventoRecord();
            channelEventRecord.setIdcanaleevento(channelEventId.intValue());
            channelEventRecord.setRecomendarrecargosinvcanal(getLimit().getEnabled() ? (byte)1 :0);
            if (getLimit().getEnabled()) {
                channelEventRecord.setRecargoinvminimo(getLimit().getMin());
                channelEventRecord.setRecargoinvmaximo(getLimit().getMax());
            }
            channelEventDao.update(channelEventRecord, new Field[]{CPANEL_CANAL_EVENTO.RECOMENDARRECARGOSINVCANAL,
                    CPANEL_CANAL_EVENTO.RECARGOINVMINIMO, CPANEL_CANAL_EVENTO.RECARGOINVMAXIMO});
        }
    }

    public void deleteChannelEventSurchargesAndRanges(Integer channelEventId){
        List<Integer> rangeIds = channelEventInvitationSurchargeRangeDao.getByChannelEventId(channelEventId).stream()
                .map(CpanelRangoRecord::getIdrango).collect(Collectors.toList());
        channelEventInvitationSurchargeRangeDao.deleteByChannelEventId(channelEventId);
        rangeDao.deleteByIds(rangeIds);
    }
}

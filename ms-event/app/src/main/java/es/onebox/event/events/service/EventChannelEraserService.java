package es.onebox.event.events.service;

import es.onebox.event.events.dao.EventChannelCommissionRangeDao;
import es.onebox.event.events.dao.EventChannelInvitationSurchargeRangeDao;
import es.onebox.event.events.dao.EventChannelPromotionCommissionRangeDao;
import es.onebox.event.events.dao.EventChannelPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.EventChannelSurchargeRangeDao;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventChannelEraserService {

    private final EventChannelDao eventChannelDao;
    private final EventChannelCommissionRangeDao eventChannelCommissionRangeDao;
    private final EventChannelPromotionCommissionRangeDao eventChannelPromotionCommissionRangeDao;
    private final EventChannelSurchargeRangeDao eventChannelSurchargeRangeDao;
    private final EventChannelInvitationSurchargeRangeDao eventChannelInvitationSurchargeRangeDao;
    private final EventChannelPromotionSurchargeRangeDao eventChannelPromotionSurchargeRangeDao;

    @Autowired
    public EventChannelEraserService(EventChannelDao eventChannelDao,
                                     EventChannelCommissionRangeDao eventChannelCommissionRangeDao,
                                     EventChannelPromotionCommissionRangeDao eventChannelPromotionCommissionRangeDao,
                                     EventChannelSurchargeRangeDao eventChannelSurchargeRangeDao,
                                     EventChannelInvitationSurchargeRangeDao eventChannelInvitationSurchargeRangeDao,
                                     EventChannelPromotionSurchargeRangeDao eventChannelPromotionSurchargeRangeDao) {
        this.eventChannelDao = eventChannelDao;
        this.eventChannelCommissionRangeDao = eventChannelCommissionRangeDao;
        this.eventChannelPromotionCommissionRangeDao = eventChannelPromotionCommissionRangeDao;
        this.eventChannelSurchargeRangeDao = eventChannelSurchargeRangeDao;
        this.eventChannelInvitationSurchargeRangeDao = eventChannelInvitationSurchargeRangeDao;
        this.eventChannelPromotionSurchargeRangeDao = eventChannelPromotionSurchargeRangeDao;
    }

    @MySQLWrite
    public void delete(Long eventId, Long channelId) {
        eventChannelDao.getEventChannel(eventId.intValue(), channelId.intValue())
                .ifPresent(this::delete);
    }

    private void delete(CpanelEventoCanalRecord eventChannel) {
        Integer eventChannelId = eventChannel.getIdeventocanal();
        eventChannelCommissionRangeDao.deleteByEventChannelId(eventChannelId);
        eventChannelPromotionCommissionRangeDao.deleteByEventChannelId(eventChannelId);
        eventChannelSurchargeRangeDao.deleteByEventChannelId(eventChannelId);
        eventChannelInvitationSurchargeRangeDao.deleteByEventChannelId(eventChannelId);
        eventChannelPromotionSurchargeRangeDao.deleteByEventChannelId(eventChannelId);
        eventChannelDao.delete(eventChannel);
    }
}

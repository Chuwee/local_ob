package es.onebox.event.events.service;

import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.events.dao.ChannelEventInvitationSurchargeRangeDao;
import es.onebox.event.events.dao.ChannelEventPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.ChannelEventSurchargeRangeDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelEventEraserService {

    private final ChannelEventDao channelEventDao;
    private final SalesGroupAssignmentDao salesGroupAssignmentDao;
    private final ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;
    private final ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao;
    private final ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao;

    @Autowired
    public ChannelEventEraserService(ChannelEventDao channelEventDao,
                                     SalesGroupAssignmentDao salesGroupAssignmentDao,
                                     ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao,
                                     ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao,
                                     ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao) {
        this.channelEventDao = channelEventDao;
        this.salesGroupAssignmentDao = salesGroupAssignmentDao;
        this.channelEventSurchargeRangeDao = channelEventSurchargeRangeDao;
        this.channelEventInvitationSurchargeRangeDao = channelEventInvitationSurchargeRangeDao;
        this.channelEventPromotionSurchargeRangeDao = channelEventPromotionSurchargeRangeDao;
    }

    @MySQLWrite
    public void delete(Long channelId, Long eventId) {
        channelEventDao.getChannelEvent(channelId.intValue(), eventId.intValue())
                .ifPresent(this::delete);
    }

    private void delete(CpanelCanalEventoRecord channelEvent) {
        Integer channelEventId = channelEvent.getIdcanaleevento();
        salesGroupAssignmentDao.deleteByChannelEventId(channelEventId);
        channelEventSurchargeRangeDao.deleteByChannelEventId(channelEventId);
        channelEventInvitationSurchargeRangeDao.deleteByChannelEventId(channelEventId);
        channelEventPromotionSurchargeRangeDao.deleteByChannelEventId(channelEventId);
        channelEventDao.delete(channelEvent);
    }
}

package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.catalog.elasticsearch.service.PackIndexationService;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.context.PackIndexationContext;
import es.onebox.event.catalog.elasticsearch.exception.PackContextLoaderException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.packs.utils.PackUtils;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelPackIndexer {

    private final PackIndexationService packIndexationService;
    private final ChannelPackDataIndexer channelPackDataIndexer;
    private final RefreshDataService refreshDataService;
    private final SessionDao sessionDao;

    public ChannelPackIndexer(PackIndexationService packIndexationService,
                              ChannelPackDataIndexer channelPackDataIndexer,
                              RefreshDataService refreshDataService,
                              SessionDao sessionDao) {
        this.packIndexationService = packIndexationService;
        this.channelPackDataIndexer = channelPackDataIndexer;
        this.refreshDataService = refreshDataService;
        this.sessionDao = sessionDao;
    }

    public void indexChannelPacks(Long channelId, Long packId, Boolean mustUpdateEvent, EventIndexationType eventIndexationType, boolean isFullUpsert) {
        List<Long> filteredChannelIds = channelId == null ? null : List.of(channelId);

        try {
            PackIndexationContext ctx = packIndexationService.preparePackContext(packId, filteredChannelIds, isFullUpsert);
            channelPackDataIndexer.indexChannelPacks(ctx);
            //channelPackDataIndexer.indexChannelPackPrices(ctx);
            migrateAutomaticMainPackItem(ctx.getPackDetailRecord(), ctx.getPackItemRecords(), mustUpdateEvent, eventIndexationType);

        } catch (PackContextLoaderException e) {
            channelPackDataIndexer.disableChannelPackForAllChannels(packId, e);
        }
    }

    private void migrateAutomaticMainPackItem(CpanelPackRecord packRecord,
                                              List<CpanelPackItemRecord> packItemRecords,
                                              Boolean mustUpdateEvent,
                                              EventIndexationType eventIndexationType) {
        if (!PackType.isAutomatic(packRecord) || BooleanUtils.isNotTrue(mustUpdateEvent)) return;

        CpanelPackItemRecord mainItem = PackUtils.getMainPackItemRecord(packItemRecords);
        if (mainItem == null) return;

        long itemId = mainItem.getIditem().longValue();
        if (PackUtils.isEvent(mainItem)) {
            refreshDataService.refreshEvent(itemId, "refreshPackMainItem", eventIndexationType, false);
        } else if (PackUtils.isSession(mainItem)) {
            refreshDataService.refreshEvent(sessionDao.getEventId(itemId), "refreshPackMainItem", eventIndexationType, false);
        }
    }
}

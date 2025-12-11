package es.onebox.event.priceengine.surcharges;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurchargesBuilder;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class CatalogSurchargeService {

    private static final int EVENT_CACHE_TTL = 10;
    private static final int CHANNEL_EVENT_CACHE_TTL = 2 * 60;
    private static final int CHANNEL_CACHE_TTL = 5 * 60;

    private final SurchargeRangeDao surchargeRangeDao;
    private final CacheRepository localCacheRepository;

    @Autowired
    public CatalogSurchargeService(SurchargeRangeDao surchargeRangeDao, CacheRepository localCacheRepository) {
        this.surchargeRangeDao = surchargeRangeDao;
        this.localCacheRepository = localCacheRepository;
    }

    public ChannelEventSurcharges getSurchargeRangesByChannelEventRelationShips(CpanelCanalEventoRecord channelEventRecord, CpanelEventoCanalRecord eventChannelRecord) {
        //Promoter surcharges
        List<CpanelRangoRecord> promoterMainSurcharges = getPromoterMainSurcharges(channelEventRecord);
        List<CpanelRangoRecord> promoterPromotionSurcharges = getPromoterPromotionSurcharges(channelEventRecord);
        List<CpanelRangoRecord> promoterInvitationSurcharges = getPromoterInvitationSurcharges(channelEventRecord.getIdevento());
        List<CpanelRangoRecord> promoterSecondaryMarketSurcharges = getPromoterSecondaryMarketSurcharges(channelEventRecord.getIdevento());

        //channel surcharges
        List<CpanelRangoRecord> channelMainSurcharges = getChannelMainSurcharges(eventChannelRecord.getIdeventocanal());
        List<CpanelRangoRecord> channelPromotionSurcharges = getChannelPromotionSurcharges(eventChannelRecord);
        List<CpanelRangoRecord> channelInvitationSurcharges = getChannelInvitationSurcharges(eventChannelRecord.getIdcanal());
        List<CpanelRangoRecord> channelSecondaryMarketSurcharges = getChannelSecondaryMarketSurcharges(eventChannelRecord.getIdcanal());

        return ChannelEventSurchargesBuilder.builder()
                .promoterMainSurcharges(promoterMainSurcharges)
                .promoterPromotionSurcharges(promoterPromotionSurcharges)
                .promoterInvitationSurcharges(promoterInvitationSurcharges)
                .promoterSecondaryMarketSurcharges(promoterSecondaryMarketSurcharges)
                .channelMainSurcharges(channelMainSurcharges)
                .channelPromotionSurcharges(channelPromotionSurcharges)
                .channelInvitationSurcharges(channelInvitationSurcharges)
                .channelSecondaryMarketSurcharges(channelSecondaryMarketSurcharges)
                .build();
    }

    private List<CpanelRangoRecord> getPromoterMainSurcharges(CpanelCanalEventoRecord channelEventRecord) {
        if (CommonUtils.isTrue(channelEventRecord.getUsarecargoevento())) {
            Integer eventId = channelEventRecord.getIdevento();
            List<CpanelRangoRecord> rangeRecords = localCacheRepository.cached("evSurcharges", EVENT_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getEventSurchargeRangesByEventId(eventId), new Object[]{eventId});
            if (CollectionUtils.isEmpty(rangeRecords)) {
                rangeRecords = localCacheRepository.cached("evEntSurcharges", EVENT_CACHE_TTL, SECONDS,
                        () -> surchargeRangeDao.getEntitySurchargeRangesByEventId(eventId), new Object[]{eventId});
            }
            return rangeRecords;
        } else {
            Integer id = channelEventRecord.getIdcanaleevento();
            return localCacheRepository.cached("ceMainSurcharges", CHANNEL_EVENT_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getChannelEventSurchargeRangesByChannelEventId(id), new Object[]{id});
        }
    }

    private List<CpanelRangoRecord> getPromoterPromotionSurcharges(CpanelCanalEventoRecord channelEventRecord) {
        if (CommonUtils.isTrue(channelEventRecord.getUsarecargoeventopromocion())) {
            Integer eventId = channelEventRecord.getIdevento();
            return localCacheRepository.cached("evPromotionSurcharges", EVENT_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getEventPromotionSurchargeRangesByEventId(eventId), new Object[]{eventId});
        } else {
            Integer id = channelEventRecord.getIdcanaleevento();
            return localCacheRepository.cached("cePromotionSurcharges", CHANNEL_EVENT_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getChannelEventPromotionSurchargeRangesByChannelEventId(id), new Object[]{id});
        }
    }

    private List<CpanelRangoRecord> getPromoterInvitationSurcharges(Integer eventId) {
        return localCacheRepository.cached("evInvSurcharges", EVENT_CACHE_TTL, SECONDS,
                () -> surchargeRangeDao.getEventInvitationSurchargeRangesByEventId(eventId), new Object[]{eventId});
    }

    private List<CpanelRangoRecord> getPromoterSecondaryMarketSurcharges(Integer eventId) {
        List<CpanelRangoRecord> rangeRecords = localCacheRepository.cached("evSMSurcharges", EVENT_CACHE_TTL, SECONDS,
                () -> surchargeRangeDao.getEventSecondaryMarketSurchargeRangesByEventId(eventId), new Object[]{eventId});
        if (CollectionUtils.isEmpty(rangeRecords)) {
            rangeRecords = localCacheRepository.cached("evEntSMSurcharges", EVENT_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getEntitySecondaryMarketSurchargeRangesByEventId(eventId), new Object[]{eventId});
        }
        return rangeRecords;
    }

    public List<CpanelRangoRecord> getChannelMainSurcharges(Integer eventChannelId) {
        return localCacheRepository.cached("ecMainSurcharges", CHANNEL_EVENT_CACHE_TTL, SECONDS,
                () -> surchargeRangeDao.getEventChannelSurchargeRangesByEventChannelId(eventChannelId), new Object[]{eventChannelId});
    }

    private List<CpanelRangoRecord> getChannelPromotionSurcharges(CpanelEventoCanalRecord eventChannelRecord) {
        if (CommonUtils.isTrue(eventChannelRecord.getAplicarrecargoscanalespecificos())) {
            Integer id = eventChannelRecord.getIdeventocanal();
            return localCacheRepository.cached("ecPromotionSurcharges", CHANNEL_EVENT_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getEventChannelPromotionSurchargeRangesByEventChannelId(id), new Object[]{id});
        } else {
            Integer id = eventChannelRecord.getIdcanal();
            return localCacheRepository.cached("cPromotionSurcharges", CHANNEL_CACHE_TTL, SECONDS,
                    () -> surchargeRangeDao.getChannelPromotionSurchargeRangesByChannelId(id), new Object[]{id});
        }
    }

    private List<CpanelRangoRecord> getChannelInvitationSurcharges(Integer channelId) {
        return localCacheRepository.cached("cInvitationSurcharges", CHANNEL_CACHE_TTL, SECONDS,
                () -> surchargeRangeDao.getChannelInvitationSurchargeRangesByChannelId(channelId), new Object[]{channelId});
    }

    private List<CpanelRangoRecord> getChannelSecondaryMarketSurcharges(Integer channelId) {
        return localCacheRepository.cached("cSMSurcharges", CHANNEL_CACHE_TTL, SECONDS,
                () -> surchargeRangeDao.getChannelInvitationSurchargeRangesByChannelId(channelId), new Object[]{channelId});
    }
}

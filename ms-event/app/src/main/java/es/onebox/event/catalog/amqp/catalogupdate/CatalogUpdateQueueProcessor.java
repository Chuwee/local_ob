package es.onebox.event.catalog.amqp.catalogupdate;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelEventAgencyDataIndexer;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelEventDataIndexer;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.client.dto.ClientEntity;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.service.EventChannelB2BService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CatalogUpdateQueueProcessor extends DefaultProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogUpdateQueueProcessor.class);

    private final ChannelEventDataIndexer channelEventDataIndexer;
    private final ChannelEventAgencyDataIndexer channelEventAgencyDataIndexer;
    private final CacheRepository localCacheRepository;
    private final EventChannelB2BService eventChannelB2BService;
    private final ChannelDao channelDao;

    public CatalogUpdateQueueProcessor(ChannelEventDataIndexer channelEventDataIndexer,
                                       ChannelEventAgencyDataIndexer channelEventAgencyDataIndexer,
                                       CacheRepository localCacheRepository, EventChannelB2BService eventChannelB2BService,
                                       ChannelDao channelDao) {
        this.channelEventDataIndexer = channelEventDataIndexer;
        this.channelEventAgencyDataIndexer = channelEventAgencyDataIndexer;
        this.localCacheRepository = localCacheRepository;
        this.eventChannelB2BService = eventChannelB2BService;
        this.channelDao = channelDao;
    }

    @Override
    public void execute(Exchange exchange) {
        CatalogUpdateMessage message = exchange.getIn().getBody(CatalogUpdateMessage.class);
        List<Long> channelIds = message.getChannelIds();
        Long eventId = message.getEventId();
        if (CollectionUtils.isNotEmpty(channelIds)) {
            var channels = channelsInfo(channelIds);
            for (Long channelId : channelIds) {
                ChannelInfo channelInfo = channels.get(channelId);
                if (channelInfo.notB2BChannel()) {
                    try {
                        channelEventDataIndexer.indexChannelEvent(channelId, eventId);
                    } catch (Exception e) {
                        LOG.error("[CATALOG_UPDATE2ES] channelId: {} - Error updating catalog", channelId, e);
                    }
                } else {
                    try {
                        List<Long> agencyIds = extractChannelAgencies(channelInfo);
                        if (CollectionUtils.isNotEmpty(agencyIds)) {
                            agencyIds.forEach(agency -> channelEventAgencyDataIndexer.indexChannelEventAgency(channelId, eventId, agency));
                        }
                    } catch (Exception e) {
                        LOG.error("[CATALOG_UPDATE2ES] B2B channelId: {} - Error updating catalog", channelId, e);
                    }
                }
            }

        }

    }

    private List<Long> extractChannelAgencies(ChannelInfo channelInfo) {
        List<ClientEntity> agencies = this.eventChannelB2BService.searchChannelAgencies(channelInfo.getEntityId());
        return agencies.stream().map(c -> c.getId().longValue()).collect(Collectors.toList());
    }

    public Map<Long, ChannelInfo> channelsInfo(List<Long> ids) {
        return localCacheRepository.cached(LocalCache.CHANNELS_KEY, LocalCache.CHANNELS_TTL, TimeUnit.SECONDS, () -> {
            List<ChannelInfo> channelInfo = channelDao.getByIds(ids);
            return channelInfo.stream().collect(Collectors.toMap(ChannelInfo::getId, Function.identity()));
        }, new Object[]{ids});
    }
}

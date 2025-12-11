package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.catalog.converter.CatalogPackPriceSimulationConverter;
import es.onebox.event.catalog.converter.ChannelCatalogPackConverter;
import es.onebox.event.catalog.dao.CatalogChannelPackCouchDao;
import es.onebox.event.catalog.dao.ChannelPackPriceCouchDao;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPricesDocument;
import es.onebox.event.catalog.elasticsearch.dao.PackElasticDao;
import es.onebox.event.catalog.elasticsearch.context.PackIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.exception.PackContextLoaderException;
import es.onebox.event.packs.dao.PackChannelDao;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesSimulation;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static es.onebox.event.packs.utils.PackUtils.PACK_CATALOG_REFRESH;

@Service
public class ChannelPackDataIndexer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelPackDataIndexer.class);

    private final CatalogChannelPackCouchDao channelPacksCouchDao;
    private final PackElasticDao packElasticDao;
    private final PackChannelDao packChannelDao;
    private final ChannelPackPriceCouchDao channelPackPriceCouchDao;

    public ChannelPackDataIndexer(CatalogChannelPackCouchDao channelPacksCouchDao,
                                  PackElasticDao packElasticDao,
                                  PackChannelDao packChannelDao,
                                  ChannelPackPriceCouchDao channelPackPriceCouchDao) {
        this.channelPacksCouchDao = channelPacksCouchDao;
        this.packElasticDao = packElasticDao;
        this.packChannelDao = packChannelDao;
        this.channelPackPriceCouchDao = channelPackPriceCouchDao;
    }

    @MySQLWrite
    public void indexChannelPacks(PackIndexationContext ctx) {
        List<ChannelPack> channelPacks = buildChannelPacks(ctx);
        if (CollectionUtils.isEmpty(channelPacks)) return;

        channelPacks.forEach(channelPack -> {
            channelPacksCouchDao.upsert(channelPack);
            packElasticDao.upsertChannelPack(channelPack);
        });
    }

    private List<ChannelPack> buildChannelPacks(PackIndexationContext ctx) {
        return ctx.getChannelIds().stream()
                .map(channelId -> buildChannelPack(ctx, channelId))
                .filter(Objects::nonNull)
                .toList();
    }

    private ChannelPack buildChannelPack(PackIndexationContext ctx, Long channelId) {
        Exception ex = ctx.getChannelContextExceptionsByChannelId().get(channelId);
        return ex == null ? ChannelCatalogPackConverter.buildChannelPack(ctx, channelId) : buildDisabledChannelPack(ctx, channelId, ex);
    }

    private ChannelPack buildDisabledChannelPack(PackIndexationContext ctx, Long channelId, Exception ex) {
        ChannelPack channelPack = channelPacksCouchDao.get(channelId, ctx.getPackId());
        if (channelPack == null) return null;

        return fillDisabledChannelPack(channelPack, ex);
    }

    private static ChannelPack fillDisabledChannelPack(ChannelPack channelPack, Exception ex) {
        channelPack.setStatus(ex instanceof PackContextLoaderException packEx ? packEx.getStatus() : PackStatus.INACTIVE);
        channelPack.setOnSale(false);
        channelPack.setForSale(false);
        return channelPack;
    }

    @MySQLWrite
    public void disableChannelPackForAllChannels(Long packId, PackContextLoaderException e) {
        List<CpanelPackCanalRecord> packChannels = packChannelDao.getPackChannels(packId);
        if (CollectionUtils.isEmpty(packChannels)) return;

        LOGGER.error("{} Disabling pack {} in all channels. Cause: {}", PACK_CATALOG_REFRESH, packId, e.getMessage());
        packChannels.stream()
                .map(packChannel -> channelPacksCouchDao.get(packChannel.getIdcanal().longValue(), packId))
                .filter(Objects::nonNull)
                .forEach(channelPack -> {
                    fillDisabledChannelPack(channelPack, e);
                    channelPacksCouchDao.upsert(channelPack);
                    packElasticDao.upsertChannelPack(channelPack);
                });
    }

    public void indexChannelPackPrices(PackIndexationContext ctx) {
        List<ChannelPackPricesDocument> docs = buildChannelPackPrices(ctx);
        if (CollectionUtils.isEmpty(docs)) return;

        channelPackPriceCouchDao.bulkUpsert(docs);
    }

    private List<ChannelPackPricesDocument> buildChannelPackPrices(PackIndexationContext ctx) {
        return ctx.getChannelIds().stream()
                .map(channelId -> buildChannelPackPrices(ctx, channelId))
                .filter(Objects::nonNull)
                .toList();
    }

    private ChannelPackPricesDocument buildChannelPackPrices(PackIndexationContext ctx, Long channelId) {
        if (ctx.getChannelContextExceptionsByChannelId().containsKey(channelId)) return null;

        PackVenueConfigPricesSimulation simulation = ctx.getPackVenueConfigMapSimulationByChannelId().get(channelId);
        return CatalogPackPriceSimulationConverter
                .toCouchChannelPackPricesDocument(channelId, ctx.getPackId(), ctx.getPricingType(), simulation);
    }
}

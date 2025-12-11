package es.onebox.event.catalog.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.converter.CatalogPackPriceSimulationConverter;
import es.onebox.event.catalog.dao.ChannelPackPriceCouchDao;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPricesDocument;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventPackErrorCode;
import es.onebox.event.catalog.dao.CatalogChannelPackCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackFilter;
import es.onebox.event.catalog.dto.packs.ChannelPacks;
import es.onebox.event.catalog.elasticsearch.dao.PackElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.pack.PackData;
import es.onebox.event.catalog.dto.packs.CatalogPackPricesSimulationDTO;
import es.onebox.event.packs.enums.PackStatus;
import org.springframework.stereotype.Service;

@Service
public class ChannelCatalogPackService {

    private final CatalogChannelPackCouchDao channelPacksCouchDao;
    private final PackElasticDao packElasticDao;
    private final ChannelPackPriceCouchDao channelPackPriceCouchDao;

    public ChannelCatalogPackService(CatalogChannelPackCouchDao channelPacksCouchDao,
                                     PackElasticDao packElasticDao,
                                     ChannelPackPriceCouchDao channelPackPriceCouchDao) {
        this.channelPacksCouchDao = channelPacksCouchDao;
        this.packElasticDao = packElasticDao;
        this.channelPackPriceCouchDao = channelPackPriceCouchDao;
    }

    public ChannelPack getPack(Long channelId, Long packId) {
        ChannelPack channelPack = channelPacksCouchDao.get(channelId, packId);
        validateChannelPackNotFound(channelPack, channelId);
        return channelPack;
    }

    private static void validateChannelPackNotFound(ChannelPack channelPack, Long channelId) {
        if (channelPack == null || PackStatus.DELETED.equals(channelPack.getStatus()) || !channelPack.getChannelId().equals(channelId)) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_NOT_FOUND);
        }
    }

    public ChannelPacks searchPacks(Long channelId, ChannelPackFilter filter) {
        ElasticSearchResults<PackData> result = packElasticDao.searchChannelPacks(channelId, filter);

        ChannelPacks channelPacks = new ChannelPacks();
        channelPacks.setData(result.getResults().stream().map(PackData::getChannelPack).toList());
        channelPacks.setMetadata(result.getMetadata());
        return channelPacks;
    }

    public CatalogPackPricesSimulationDTO getPriceSimulation(Long channelId, Long packId) {
        ChannelPackPricesDocument doc = channelPackPriceCouchDao.get(channelId, packId);
        if (doc == null || doc.getSimulation() == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_PACK_PRICES_NOT_FOUND)
                    .setMessage("ChannelPackPrices not found for channel: " + channelId + " - pack: " + packId).build();
        }
        return CatalogPackPriceSimulationConverter.couchToDTO(doc);
    }

}

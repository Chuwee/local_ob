package es.onebox.mgmt.channels.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.packs.converter.PacksConverter;
import es.onebox.mgmt.packs.dto.CreatePackRateDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypesRequestDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.mgmt.packs.dto.prices.PackPriceDTO;
import es.onebox.mgmt.packs.dto.prices.UpdatePackPriceRequestListDTO;
import es.onebox.mgmt.packs.dto.rates.PackRateDTO;
import es.onebox.mgmt.packs.dto.rates.UpdatePackRateDTO;
import es.onebox.mgmt.packs.enums.PriceTypeRangeDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesRequest;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackRate;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChannelPackPricesService {

    private final ChannelsRepository channelsRepository;
    private final ChannelsHelper channelsHelper;

    @Autowired
    public ChannelPackPricesService(ChannelsRepository channelsRepository, ChannelsHelper channelsHelper) {
        this.channelsRepository = channelsRepository;
        this.channelsHelper = channelsHelper;
    }

    public PackItemPriceTypesResponseDTO getPackItemPriceTypes(Long channelId, Long packId, Long packItemId) {
        return PacksConverter.toPackDTO(channelsRepository.getPackItemPriceTypes(channelId, packId, packItemId));
    }

    public void updatePackItemPriceTypes(Long channelId, Long packId, Long packItemId, PackItemPriceTypesRequestDTO priceTypesRequest) {
        if (PriceTypeRangeDTO.RESTRICTED.equals(priceTypesRequest.getSelectionType()) && CollectionUtils.isEmpty(priceTypesRequest.getPriceTypeIds())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
        }
        PackItemPriceTypesRequest request = PacksConverter.toMs(priceTypesRequest);
        channelsRepository.updatePackItemPriceTypes(channelId, packId, packItemId, request);
    }

    public List<PackRateDTO> getPackRates(Long channelId, Long packId) {
        channelsHelper.getAndCheckChannel(channelId);

        List<PackRate> rates = channelsRepository.getPackRates(channelId, packId);
        return PacksConverter.toRatesDTO(rates);
    }

    public IdDTO createPackRates(Long channelId, Long packId, CreatePackRateDTO rate) {
        channelsHelper.getAndCheckChannel(channelId);

        return channelsRepository.createPackRates(channelId, packId, PacksConverter.toMs(rate));
    }

    public void refreshPackRates(Long channelId, Long packId) {
        channelsHelper.getAndCheckChannel(channelId);
        channelsRepository.refreshPackRates(channelId, packId);
    }

    public void updatePackRate(Long channelId, Long packId, Long rateId, UpdatePackRateDTO rate) {
        channelsHelper.getAndCheckChannel(channelId);

        channelsRepository.updatePackRate(channelId, packId, rateId, PacksConverter.toMs(rate));
    }

    public void deletePackRate(Long channelId, Long packId, Long rateId) {
        channelsHelper.getAndCheckChannel(channelId);

        channelsRepository.deletePackRate(channelId, packId, rateId);
    }

    public List<PackPriceDTO> getPackPrices(Long channelId, Long packId) {
        channelsHelper.getAndCheckChannel(channelId);

        List<PackPrice> prices = channelsRepository.getPackPrices(channelId, packId);
        return PacksConverter.toPricesDTO(prices);
    }

    public void updatePackPrice(Long channelId, Long packId, UpdatePackPriceRequestListDTO packPriceDTO) {
        channelsHelper.getAndCheckChannel(channelId);

        channelsRepository.updatePackPrices(channelId, packId, PacksConverter.toMs(packPriceDTO));
    }
} 

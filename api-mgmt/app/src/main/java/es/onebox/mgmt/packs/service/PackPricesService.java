package es.onebox.mgmt.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
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
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PackPricesService {

    private final PacksRepository packsRepository;

    @Autowired
    public PackPricesService(PacksRepository packsRepository) {
        this.packsRepository = packsRepository;
    }

    public PackItemPriceTypesResponseDTO getPackItemPriceTypes(Long packId, Long packItemId) {
        return PacksConverter.toPackDTO(packsRepository.getPackItemPriceTypes(packId, packItemId));
    }

    public void updatePackItemPriceTypes(Long packId, Long packItemId, PackItemPriceTypesRequestDTO priceTypesRequest) {
        if (PriceTypeRangeDTO.RESTRICTED.equals(priceTypesRequest.getSelectionType()) && CollectionUtils.isEmpty(priceTypesRequest.getPriceTypeIds())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
        }
        PackItemPriceTypesRequest request = PacksConverter.toMs(priceTypesRequest);
        packsRepository.updatePackItemPriceTypes(packId, packItemId, request);
    }

    public List<PackRateDTO> getPackRates(Long packId) {

        List<PackRate> rates = packsRepository.getPackRates(packId);
        return PacksConverter.toRatesDTO(rates);
    }

    public IdDTO createPackRates(Long packId, CreatePackRateDTO rate) {
        return packsRepository.createPackRates(packId, PacksConverter.toMs(rate));
    }

    public void refreshPackRates(Long packId) {
        packsRepository.refreshPackRates(packId);
    }

    public void updatePackRate(Long packId, Long rateId, UpdatePackRateDTO rate) {
        packsRepository.updatePackRate(packId, rateId, PacksConverter.toMs(rate));
    }

    public void deletePackRate(Long packId, Long rateId) {
        packsRepository.deletePackRate(packId, rateId);
    }

    public List<PackPriceDTO> getPackPrices(Long packId) {
        List<PackPrice> prices = packsRepository.getPackPrices(packId);
        return PacksConverter.toPricesDTO(prices);
    }

    public void updatePackPrice(Long packId, UpdatePackPriceRequestListDTO packPriceDTO) {
        packsRepository.updatePackPrices(packId, PacksConverter.toMs(packPriceDTO));
    }
} 

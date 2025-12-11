package es.onebox.mgmt.channels.packs.service;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.repository.OrderProductsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.packs.converter.PacksConverter;
import es.onebox.mgmt.packs.dto.BasePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.dto.PackDTO;
import es.onebox.mgmt.packs.dto.PackDetailDTO;
import es.onebox.mgmt.packs.dto.UpdatePackDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import es.onebox.mgmt.packs.service.PacksValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ChannelPacksService {

    private final ChannelsRepository channelsRepository;
    private final PacksHelper packsHelper;
    private final ChannelsHelper channelsHelper;
    private final PacksValidationService packsValidationService;
    private final OrderProductsRepository orderProductsRepository;
    private static final Long PRODUCTS_LIMIT = 10L;

    @Autowired
    ChannelPacksService(ChannelsRepository channelsRepository,
                        PacksHelper packsHelper,
                        ChannelsHelper channelsHelper,
                        PacksValidationService packsValidationService,
                        OrderProductsRepository orderProductsRepository) {
        this.channelsRepository = channelsRepository;
        this.packsHelper = packsHelper;
        this.channelsHelper = channelsHelper;
        this.packsValidationService = packsValidationService;
        this.orderProductsRepository = orderProductsRepository;
    }

    public List<PackDTO> getPacks(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        List<Pack> packs = channelsRepository.getPacks(channelId);

        return PacksConverter.toPacksDTO(packs);
    }

    public PackDetailDTO getPack(Long channelId, Long packId) {
        channelsHelper.getAndCheckChannel(channelId);
        PackDetail pack = channelsRepository.getPack(channelId, packId);
        PackDetailDTO packDTO = PacksConverter.toPackDetailDTO(pack);
        ProductSearchRequest productSearchRequest = buildProductSearchReq(packId);
        ProductSearchResponse productSearchResp = orderProductsRepository.searchProducts(productSearchRequest);
        packDTO.setHasSales(packHasOrders(productSearchResp));
        return packDTO;
    }

    public Boolean packHasOrders(ProductSearchResponse productSearchResp) {
        Long packSales = productSearchResp.getMetadata().getTotal();
        return packSales > 0;
    }

    private ProductSearchRequest buildProductSearchReq(Long packId) {
        ProductSearchRequest filter = new ProductSearchRequest();
        filter.setPackIds(Collections.singletonList(packId));
        filter.setOffset(0L);
        filter.setLimit(PRODUCTS_LIMIT);
        return filter;
    }

    public PackDTO createPack(Long channelId, CreatePackDTO createPackDTO) {
        packsValidationService.validateCreatePack(channelId, createPackDTO);
        Pack pack = channelsRepository.createPack(channelId, PacksConverter.toMs(createPackDTO));

        return PacksConverter.toPackDTO(pack);
    }

    public void updatePack(Long channelId, Long packId, UpdatePackDTO updatePackDTO) {
        packsValidationService.validateUpdateChannelPack(channelId, packId, updatePackDTO);
        channelsRepository.updatePack(channelId, packId, PacksConverter.toMs(updatePackDTO));
    }

    public void deletePack(Long channelId, Long packId) {
        channelsHelper.getAndCheckChannel(channelId);
        channelsRepository.deletePack(channelId, packId);
    }

    public List<BasePackItemDTO> getPackItems(Long channelId, Long packId) {
        channelsHelper.getAndCheckChannel(channelId);
        PackType packType = channelsRepository.getPack(channelId, packId).getType();
        List<PackItem> items = channelsRepository.getPackItems(channelId, packId);
        if (CollectionUtils.isEmpty(items)) {
            return new ArrayList<>();
        }
        PackItem mainEventPackItem = packsHelper.getMainEventItem(items);
        Event mainEventPackItemEvent = packsHelper.getMainEventPackItemEvent(mainEventPackItem);
        VenueTemplate mainEvenPackItemVenueTemplate = packsHelper.getMainEvenPackItemVenueTemplate(mainEventPackItem);

        List<PackItem> sessionPackItems = packsHelper.getSessionPackItems(items);
        Map<Long, Session> sessionMap = packsHelper.getPackItemSessionMap(sessionPackItems);
        Map<Long, List<IdNameDTO>> priceTypeMap = packsHelper.getPackItemPriceTypesMap(packType, sessionPackItems, mainEventPackItem, sessionMap);

        List<PackItem> productPackItems = packsHelper.getProductPackItems(items);
        Map<Long, Product> productMap = packsHelper.getPackItemProductMap(productPackItems);
        Map<Long, ProductVariant> variantMap = packsHelper.getPackItemProductVariantMap(packType, productPackItems);
        Map<Long, DeliveryPoint> deliveryPointMap = packsHelper.getPackItemDeliveryPointMap(packType, productPackItems);

        return PacksConverter.toPackDTO(packType, items, null, mainEventPackItemEvent, mainEvenPackItemVenueTemplate,
                sessionMap, priceTypeMap, productMap, variantMap, deliveryPointMap);
    }

    public void createPackItems(Long channelId, Long packId, CreatePackItemsDTO createPackItemDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        Pack pack = packsHelper.getAndCheckPack(channelId, packId);
        List<PackItem> items = channelsRepository.getPackItems(channelId, packId);

        packsValidationService.validateCreatePackItems(pack.getType(), items, createPackItemDTO);
        packsHelper.addVariantsToSimpleProducts(pack.getType(), createPackItemDTO);
        channelsRepository.createPackItems(channelId, packId, PacksConverter.toMs(createPackItemDTO));
    }

    public void updatePackItem(Long channelId, Long packId, Long packItemId, UpdatePackItemDTO updatePackItemDTO) {
        packsValidationService.validateUpdatePackItem(channelId, packId, packItemId, updatePackItemDTO);
        channelsRepository.updatePackItem(channelId, packId, packItemId, PacksConverter.toMs(updatePackItemDTO));
    }

    public void deletePackItem(Long channelId, Long packId, Long packItemId) {
        packsValidationService.validateDeletePackItem(channelId, packId, packItemId);
        channelsRepository.deletePackItem(channelId, packId, packItemId);
    }
}

package es.onebox.mgmt.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.Channel;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAcceptRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItemSubItemsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksFilterRequest;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannel;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.repository.OrderProductsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.packs.converter.PacksConverter;
import es.onebox.mgmt.packs.dto.BasePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.dto.PackDTO;
import es.onebox.mgmt.packs.dto.PackDetailDTO;
import es.onebox.mgmt.packs.dto.PackItemSubitemFilterDTO;
import es.onebox.mgmt.packs.dto.PackItemSubitemResponseDTO;
import es.onebox.mgmt.packs.dto.PacksFilterDTO;
import es.onebox.mgmt.packs.dto.PacksResponseDTO;
import es.onebox.mgmt.packs.dto.UpdatePackDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemSubitemsRequestDTO;
import es.onebox.mgmt.packs.dto.channels.CreatePackChannelDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelDetailDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelsDTO;
import es.onebox.mgmt.packs.dto.channels.UpdatePackChannelDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PacksService {

    private final PacksRepository packsRepository;
    private final PacksHelper packsHelper;
    private final PacksValidationService packsValidationService;
    private final OrderProductsRepository orderProductsRepository;
    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;

    @Autowired
    PacksService(PacksRepository packsRepository,
                 PacksHelper packsHelper,
                 PacksValidationService packsValidationService,
                 OrderProductsRepository orderProductsRepository,
                 ChannelsRepository channelsRepository,
                 SecurityManager securityManager) {
        this.packsRepository = packsRepository;
        this.packsHelper = packsHelper;
        this.packsValidationService = packsValidationService;
        this.orderProductsRepository = orderProductsRepository;
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
    }

    public PacksResponseDTO getPacks(PacksFilterDTO request) {
        securityManager.checkEntityAccessible(request);

        PacksFilterRequest filter = PacksConverter.convertFilter(SecurityUtils.getUserOperatorId(), request);
        PacksResponse packs = packsRepository.getPacks(filter);
        return PacksConverter.toPacksResponseDTO(packs);
    }

    public PackDetailDTO getPack(Long packId) {
        PackDetail pack = packsRepository.getPack(packId);
        Long packSales = searchPackSales(packId, null);

        PackDetailDTO response = PacksConverter.toPackDetailDTO(pack);
        response.setHasSales(packSales > 0);
        return response;
    }

    public PackDTO createPack(CreatePackDTO request) {
        securityManager.checkEntityAccessible(request.getEntityId());

        packsValidationService.validateCreatePackMainItem(request);
        Pack pack = packsRepository.createPack(PacksConverter.toMs(request));

        return PacksConverter.toPackDTO(pack);
    }

    public void updatePack(Long packId, UpdatePackDTO updatePackDTO) {
        packsValidationService.validateUpdatePack(packId, updatePackDTO);
        packsRepository.updatePack(packId, PacksConverter.toMs(updatePackDTO));
    }

    public void deletePack(Long packId) {
        PackDetail pack = packsRepository.getPack(packId);
        securityManager.checkEntityAccessible(pack.getEntityId());

        if (searchPackSales(packId, null) > 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_HAS_SALES);
        }

        packsRepository.deletePack(packId);
    }

    public List<BasePackItemDTO> getPackItems(Long packId) {
        List<PackItem> items = packsRepository.getPackItems(packId);
        if (CollectionUtils.isEmpty(items)) {
            return new ArrayList<>();
        }
        PackItem mainItem = packsHelper.getMainItem(items);
        PackItem mainEventPackItem = packsHelper.getMainEventItem(items);
        Event mainEventPackItemEvent = packsHelper.getMainEventPackItemEvent(mainEventPackItem);
        VenueTemplate mainEvenPackItemVenueTemplate = packsHelper.getMainEvenPackItemVenueTemplate(mainEventPackItem);

        PackType packType = packsRepository.getPack(packId).getType();
        List<PackItem> sessionPackItems = packsHelper.getSessionPackItems(items);
        Map<Long, Session> sessionMap = packsHelper.getPackItemSessionMap(sessionPackItems);
        Map<Long, List<IdNameDTO>> priceTypeMap = packsHelper.getPackItemPriceTypesMap(packType, sessionPackItems, mainEventPackItem, sessionMap);

        List<PackItem> productPackItems = packsHelper.getProductPackItems(items);
        Map<Long, Product> productMap = packsHelper.getPackItemProductMap(productPackItems);
        Map<Long, ProductVariant> variantMap = packsHelper.getPackItemProductVariantMap(packType, productPackItems);
        Map<Long, DeliveryPoint> deliveryPointMap = packsHelper.getPackItemDeliveryPointMap(packType, productPackItems);

        return PacksConverter.toPackDTO(packType, items, mainItem, mainEventPackItemEvent, mainEvenPackItemVenueTemplate,
                sessionMap, priceTypeMap, productMap, variantMap, deliveryPointMap);
    }

    public void createPackItems(Long packId, CreatePackItemsDTO createPackItemDTO) {
        Pack pack = packsHelper.getAndCheckPack(packId);
        List<PackItem> items = packsRepository.getPackItems(packId);

        packsValidationService.validateCreatePackItems(pack.getType(), items, createPackItemDTO);
        packsHelper.addVariantsToSimpleProducts(pack.getType(), createPackItemDTO);
        packsRepository.createPackItems(packId, PacksConverter.toMs(createPackItemDTO));
    }

    public void updatePackItem(Long packId, Long packItemId, UpdatePackItemDTO updatePackItemDTO) {
        packsValidationService.validateUpdatePackItem(packId, packItemId, updatePackItemDTO);
        packsRepository.updatePackItem(packId, packItemId, PacksConverter.toMs(updatePackItemDTO));
    }

    public void deletePackItem(Long packId, Long packItemId) {
        packsValidationService.validateDeletePackItem(packId, packItemId);
        packsRepository.deletePackItem(packId, packItemId);
    }

    public PackItemSubitemResponseDTO getPackItemSubitems(Long packId, Long packItemId, PackItemSubitemFilterDTO packItemSubitemFilterDTO){
        packsHelper.getAndCheckPack(packId);
        packsHelper.getAndCheckPackItem(packId, packItemId);

        PackItemSubItemsResponse packItemSubitemResponse = packsRepository.getPackItemSubitems(packId, packItemId, packItemSubitemFilterDTO);
        return PacksConverter.toPackItemSubitemResponseDTO(packItemSubitemResponse);
    }

    public void updatePackItemSubitems(Long packId, Long packItemId, UpdatePackItemSubitemsRequestDTO request) {
        packsHelper.getAndCheckPack(packId);
        packsHelper.getAndCheckPackItem(packId, packItemId);

        packsRepository.updatePackItemSubitems(packId, packItemId, PacksConverter.toMs(request));
    }

    public PackChannelsDTO getPackChannels(Long packId) {
        Pack pack = packsHelper.getAndCheckPack(packId);
        securityManager.checkEntityAccessible(pack.getEntityId());

        PackChannels packChannels = packsRepository.getPackChannels(packId);

        return PacksConverter.toPackChannelsDTO(packChannels);
    }

    public PackChannelDetailDTO getPackChannel(Long packId, Long channelId) {
        Pack pack = packsHelper.getAndCheckPack(packId);
        securityManager.checkEntityAccessible(pack.getEntityId());

        PackChannel packChannel = packsRepository.getPackChannel(packId, channelId);
        return PacksConverter.toPackChannelDetailDTO(packChannel);
    }

    public void createPackChannels(Long packId, CreatePackChannelDTO request) {
        packsHelper.getAndCheckPack(packId);
        validatePackChannelsCreation(request.getChannelIds());

        packsRepository.createPackChannels(packId, request.getChannelIds());
    }

    public void updatePackChannel(Long packId, Long channelId, UpdatePackChannelDTO request) {
        packsHelper.getAndCheckPack(packId);
        packsRepository.updatePackChannel(packId, channelId, PacksConverter.toMs(request));
    }

    public void deletePackChannel(Long packId, Long channelId) {
        validatePackChannelModification(packId, channelId);

        if (searchPackSales(packId, channelId) > 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_HAS_SALES);
        }

        packsRepository.deletePackChannel(packId, channelId);
    }

    public void requestChannelApproval(Long packId, Long channelId) {
        PackDetail pack = validatePackChannelModification(packId, channelId);

        PackChannel packChannel = packsRepository.getPackChannel(packId, channelId);

        packsRepository.requestChannelApproval(packId, channelId, SecurityUtils.getUserId());

        channelsRepository.createPackSaleRequest(packId, channelId);
        if (SecurityUtils.hasAnyRole(Roles.ROLE_CNL_MGR, Roles.ROLE_OPR_MGR) &&
                packChannel.getChannel().getEntityId().equals(pack.getEntityId())) {
            ChannelAcceptRequest request = new ChannelAcceptRequest();
            request.setUserId(SecurityUtils.getUserId());
            channelsRepository.acceptPackRequest(packId, channelId, request);
        }
    }

    private Long searchPackSales(Long packId, Long channelId) {
        ProductSearchRequest filter = new ProductSearchRequest();
        filter.setPackIds(Collections.singletonList(packId));
        if (channelId != null) {
            filter.setChannelIds(Collections.singletonList(packId));
        }
        filter.setOffset(0L);
        filter.setLimit(0L);
        return orderProductsRepository.searchProducts(filter).getMetadata().getTotal();
    }

    private void validatePackChannelsCreation(List<Long> channelIds) {
        ChannelFilter filter = new ChannelFilter();
        filter.setChannelIds(channelIds);
        filter.setLimit((long) channelIds.size());

        List<ChannelStatus> notDeletedStatuses = Arrays.stream(ChannelStatus.values()).collect(Collectors.toList());
        notDeletedStatuses.remove(ChannelStatus.DELETED);
        filter.setStatus(notDeletedStatuses);

        ChannelsResponse channelsResponse = channelsRepository.getChannels(SecurityUtils.getUserOperatorId(), filter);
        if (channelsResponse.getData().size() != channelIds.size()) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        for (Channel channel : channelsResponse.getData()) {
            if (channel == null || channel.getStatus() == ChannelStatus.DELETED) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
            }
            securityManager.checkEntityAccessibleWithVisibility(channel.getEntityId());
        }
    }

    private PackDetail validatePackChannelModification(Long packId, Long channelId) {
        PackDetail pack = packsHelper.getAndCheckPack(packId);
        securityManager.checkEntityAccessible(pack.getEntityId());

        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (channel == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        return pack;
    }
}

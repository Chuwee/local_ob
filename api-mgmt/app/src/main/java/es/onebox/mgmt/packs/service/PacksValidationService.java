package es.onebox.mgmt.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.dto.CreatePackMainItemDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypeMappingRequestDTO;
import es.onebox.mgmt.packs.dto.UpdatePackDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemDTO;
import es.onebox.mgmt.packs.enums.PackRangeType;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import es.onebox.mgmt.products.enums.ProductType;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacksValidationService {

    private final ChannelsRepository channelsRepository;
    private final ProductsRepository productsRepository;
    private final SessionsRepository sessionsRepository;
    private final PacksRepository packsRepository;
    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;
    private final SecurityManager securityManager;
    private final ChannelsHelper channelsHelper;
    private final PacksHelper packsHelper;

    public PacksValidationService(ChannelsRepository channelsRepository,
                                  ProductsRepository productsRepository,
                                  SessionsRepository sessionsRepository,
                                  PacksRepository packsRepository,
                                  VenuesRepository venuesRepository,
                                  ValidationService validationService,
                                  SecurityManager securityManager,
                                  ChannelsHelper channelsHelper,
                                  PacksHelper packsHelper) {
        this.channelsRepository = channelsRepository;
        this.productsRepository = productsRepository;
        this.sessionsRepository = sessionsRepository;
        this.packsRepository = packsRepository;
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
        this.securityManager = securityManager;
        this.channelsHelper = channelsHelper;
        this.packsHelper = packsHelper;
    }

    public void validateCreatePack(Long channelId, CreatePackDTO createPackDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        if (PackTypeDTO.AUTOMATIC.equals(createPackDTO.getType())) {
            validateCreatePackMainItem(createPackDTO);
        }
    }

    public void validateUpdateChannelPack(Long channelId, Long packId, UpdatePackDTO updatePackDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        validateUpdatePackActive(channelId, packId, updatePackDTO);
        validateUpdatePackPeriod(updatePackDTO);
    }

    public void validateUpdatePack(Long packId, UpdatePackDTO updatePackDTO) {
        validateUpdatePackActive(null, packId, updatePackDTO);
        validateUpdatePackPeriod(updatePackDTO);
    }

    public void validateCreatePackItems(PackType packType, List<PackItem> items, CreatePackItemsDTO createPackItemDTO) {
        validateCreateNoEventPackItems(createPackItemDTO);
        validateCreateNewPackItemsCombination(items, createPackItemDTO);

        List<CreatePackItemDTO> createSessionPackItems = packsHelper.getCreateSessionPackItems(createPackItemDTO);
        if (CollectionUtils.isNotEmpty(createSessionPackItems)) {
            PackItem mainItem = packsHelper.getMainItem(items);
            validateCreateSessionPackItems(packType, createSessionPackItems, mainItem);
        }

        List<CreatePackItemDTO> createProductPackItems = packsHelper.getCreateProductPackItems(createPackItemDTO);
        if (CollectionUtils.isNotEmpty(createProductPackItems)) {
            validateCreateProductsPackItems(packType, createProductPackItems);
        }
    }

    public void validateUpdatePackItem(Long channelId, Long packId, Long packItemId, UpdatePackItemDTO updatePackItemDTO) {
        PackType packType = channelsRepository.getPack(channelId, packId).getType();
        switch (packType) {
            case MANUAL ->
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_FROM_MANUAL_PACK_CANNOT_BE_UPDATED);
            case AUTOMATIC -> {
                PackItem packItem = packsHelper.getAndCheckPackItem(channelId, packId, packItemId);
                validateUpdatePackItemFromAutomaticPack(channelId, packId, packItem, updatePackItemDTO);
            }
        }
    }

    public void validateUpdatePackItem(Long packId, Long packItemId, UpdatePackItemDTO updatePackItemDTO) {
        PackItem packItem = packsHelper.getAndCheckPackItem(packId, packItemId);
        validateUpdatePackItemFromAutomaticPack(packId, packItem, updatePackItemDTO);
    }

    public void validateDeletePackItem(Long channelId, Long packId, Long packItemId) {
        channelsHelper.getAndCheckChannel(channelId);
        PackItem packItem = packsHelper.getAndCheckPackItem(channelId, packId, packItemId);
        if (BooleanUtils.isTrue(packItem.getMain())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_CANNOT_BE_DELETED);
        }
    }

    public void validateDeletePackItem(Long packId, Long packItemId) {
        PackItem packItem = packsHelper.getAndCheckPackItem(packId, packItemId);
        if (BooleanUtils.isTrue(packItem.getMain())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_CANNOT_BE_DELETED);
        }
    }

    public void validateGetPackUrlsByChannel(Long channelId) {
        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        if (channelConfig != null && BooleanUtils.isNotTrue(channelConfig.getV4Enabled())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_URLS_NOT_AVAILABLE);
        }
    }


    public void validateCreatePackMainItem(CreatePackDTO request) {
        if (request.getType() == null || !PackTypeDTO.AUTOMATIC.equals(request.getType())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (request.getMainItem() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_MANDATORY);
        }
        if (request.getMainItem().getItemId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_ITEM_ID_CANNOT_BE_NULL);
        }
        if (request.getMainItem().getType() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_TYPE_CANNOT_BE_NULL);
        }

        switch (request.getMainItem().getType()) {
            case EVENT -> validateCreateEventPackMainItem(request.getMainItem());
            case SESSION -> validateCreateSessionPackMainItem(request.getMainItem());
            case PRODUCT -> throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_TYPE_CANNOT_BE_PRODUCT);
        }
    }

    private void validateUpdatePackActive(Long channelId, Long packId, UpdatePackDTO request) {
        if (BooleanUtils.isTrue(request.getActive())) {
            if (channelId == null) {
                Pack pack = packsRepository.getPack(packId);
                if (!packsHelper.isDateConfigured(pack) && request.getPackPeriod() == null) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PACK_DATES_REQUIRED);
                }
                List<PackItem> items = packsRepository.getPackItems(packId);
                if (CollectionUtils.isEmpty(items) || items.size() < 2 || packsHelper.hasNoSessionsOrProductItems(items)) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_CANNOT_BE_ACTIVATED);
                }
            } else {
                Pack pack = channelsRepository.getPack(channelId, packId);
                if (!packsHelper.isDateConfigured(pack) && request.getPackPeriod() == null) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PACK_DATES_REQUIRED);
                }
                List<PackItem> items = channelsRepository.getPackItems(channelId, packId);
                if (CollectionUtils.isEmpty(items) || items.size() < 2 || packsHelper.hasNoSessionsOrProductItems(items)) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_CANNOT_BE_ACTIVATED);
                }
            }
        }
    }

    private void validateUpdatePackPeriod(UpdatePackDTO updatePackDTO) {
        if (updatePackDTO.getPackPeriod() != null && PackRangeType.CUSTOM.equals(updatePackDTO.getPackPeriod().getType())) {
            if (updatePackDTO.getPackPeriod().getStartDate() == null || updatePackDTO.getPackPeriod().getEndDate() == null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PACK_DATES_REQUIRED);
            }
            if (updatePackDTO.getPackPeriod().getEndDate().isBefore(updatePackDTO.getPackPeriod().getStartDate())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PACK_DATES_END_BEFORE_START);
            }
        }
    }

    private void validateCreateNoEventPackItems(CreatePackItemsDTO createPackItemDTO) {
        if (createPackItemDTO.stream().anyMatch(item -> PackItemType.EVENT.equals(item.getType()))) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_CANNOT_BE_EVENT_TYPE);
        }
    }

    private void validateCreateNewPackItemsCombination(List<PackItem> items, CreatePackItemsDTO createPackItemDTO) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        for (CreatePackItemDTO newItem : createPackItemDTO) {
            for (PackItem item : items) {
                if (item.getItemId().equals(newItem.getItemId()) && item.getType().equals(newItem.getType())) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEMS_ALREADY_IN_PACK);
                }
            }
        }
    }

    private void validateCreateSessionPackItems(PackType packType, List<CreatePackItemDTO> sessionPackItems, PackItem mainItem) {
        List<Session> sessions = packsHelper.getSessionsFromCreateSessionPackItems(sessionPackItems);
        sessions.forEach(packsHelper::validateSessionStatus);
        switch (packType) {
            case AUTOMATIC -> sessionPackItems.forEach(item -> {
                Session session = packsHelper.getSessionFromCreateSessionPackItem(item, sessions);
                validateCreateSessionPackItemFromAutomaticPack(item, mainItem, session);
            });
            case MANUAL -> {
                if (sessionPackItems.stream().anyMatch(item -> item.getPriceTypeId() != null)) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_FROM_MANUAL_PACK_CANNOT_HAVE_PRICE_TYPE_ID);
                }
            }
        }
    }

    private void validateCreateSessionPackItemFromAutomaticPack(CreatePackItemDTO item, PackItem mainItem, Session session) {
        boolean sessionHasMainTemplate = packsHelper.sessionHasMainTemplate(session, mainItem);
        if (sessionHasMainTemplate && item.getPriceTypeId() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_WITH_MAIN_TEMPLATE_CANNOT_HAVE_PRICE_TYPE_ID);
        }
        if (!sessionHasMainTemplate) {
            if (item.getPriceTypeId() == null && item.getPriceTypeMapping() == null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_PRICE_TYPE_ID_CANNOT_BE_NULL);
            } else if (item.getPriceTypeId() != null && item.getPriceTypeMapping() != null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_PRICE_TYPE_ID_OR_MAPPING);
            }
            if (item.getPriceTypeId() != null) {
                validatePriceTypeInSession(session, item.getPriceTypeId().longValue());
            } else {
                validatePriceTypeMappingInSession(mainItem, item.getPriceTypeMapping());
            }
        }
    }

    private void validateCreateProductsPackItems(PackType packType, List<CreatePackItemDTO> createProductPackItems) {
        switch (packType) {
            case MANUAL -> createProductPackItems.forEach(this::validateCreateProductPackItemFromManualPack);
            case AUTOMATIC -> createProductPackItems.forEach(this::validateCreateProductPackItemFromAutomaticPack);
        }
    }

    private void validateCreateProductPackItemFromManualPack(CreatePackItemDTO item) {
        if (item.getVariantId() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_FROM_MANUAL_PACK_CANNOT_HAVE_VARIANT_ID);
        }
        if (item.getDeliveryPointId() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_FROM_MANUAL_PACK_CANNOT_HAVE_DELIVERY_POINT_ID);
        }
        if (item.getSharedBarcode() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_FROM_MANUAL_PACK_CANNOT_HAVE_SHARED_BARCODE);
        }
    }

    private void validateCreateProductPackItemFromAutomaticPack(CreatePackItemDTO item) {
        Product product = validationService.getAndCheckProduct(item.getItemId());
        validateCreatePackItemVariantId(item, product.getProductType());
        validateCreatePackItemDeliveryPointId(item);
        if (item.getSharedBarcode() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_SHARED_BARCODE_CANNOT_BE_NULL);
        }
    }

    private void validateCreatePackItemVariantId(CreatePackItemDTO item, ProductType productType) {
        if (ProductType.VARIANT.equals(productType)) {
            if (item.getVariantId() == null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_VARIANT_ID_CANNOT_BE_NULL);
            }
            if (productsRepository.getProductVariant(item.getItemId(), item.getVariantId().longValue()) == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_VARIANT_NOT_FOUND);
            }
        } else if (item.getVariantId() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_SIMPLE_PRODUCT_CANNOT_HAVE_VARIANTS);
        }
    }

    private void validateCreatePackItemDeliveryPointId(CreatePackItemDTO item) {
        if (item.getDeliveryPointId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_DELIVERY_POINT_ID_CANNOT_BE_NULL);
        }
        DeliveryPoint deliveryPoint = productsRepository.getDeliveryPoint(item.getDeliveryPointId().longValue());
        securityManager.checkEntityAccessibleWithVisibility(deliveryPoint.getEntity().getId());
    }

    private void validateUpdatePackItemFromAutomaticPack(Long channelId,
                                                         Long packId,
                                                         PackItem packItem,
                                                         UpdatePackItemDTO updatePackItemDTO) {
        switch (packItem.getType()) {
            case EVENT ->
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_EVENT_CANNOT_BE_UPDATED);
            case SESSION -> {
                List<PackItem> items = channelsRepository.getPackItems(channelId, packId);
                PackItem mainItem = packsHelper.getMainItem(items);
                validateUpdatePackItemPriceTypeId(packItem, updatePackItemDTO, mainItem);
            }
            case PRODUCT -> {
                validateUpdatePackItemVariantId(packItem, updatePackItemDTO);
                validateUpdatePackItemDeliveryPointId(updatePackItemDTO);
            }
        }
    }

    private void validateUpdatePackItemFromAutomaticPack(Long packId,
                                                         PackItem packItem,
                                                         UpdatePackItemDTO updatePackItemDTO) {
        switch (packItem.getType()) {
            case EVENT ->
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_EVENT_CANNOT_BE_UPDATED);
            case SESSION -> {
                List<PackItem> items = packsRepository.getPackItems(packId);
                PackItem mainItem = packsHelper.getMainItem(items);
                validateUpdatePackItemPriceTypeId(packItem, updatePackItemDTO, mainItem);
            }
            case PRODUCT -> {
                validateUpdatePackItemVariantId(packItem, updatePackItemDTO);
                validateUpdatePackItemDeliveryPointId(updatePackItemDTO);
            }
        }
    }

    private void validateUpdatePackItemPriceTypeId(PackItem packItem, UpdatePackItemDTO request, PackItem mainItem) {
        if (request.getPriceTypeId() != null) {
            if (BooleanUtils.isTrue(packItem.getMain())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_MAIN_ITEM_CANNOT_HAVE_PRICE_TYPE_ID);
            }
            Session session = sessionsRepository.getSession(packItem.getItemId());
            if (packsHelper.sessionHasMainTemplate(session, mainItem)) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_WITH_MAIN_TEMPLATE_CANNOT_HAVE_PRICE_TYPE_ID);
            }
            validatePriceTypeInSession(session, request.getPriceTypeId().longValue());
        } else if (request.getPriceTypeMapping() != null) {
            validatePriceTypeMappingInSession(mainItem, request.getPriceTypeMapping());
        }

    }

    private void validateUpdatePackItemVariantId(PackItem packItem, UpdatePackItemDTO updatePackItemDTO) {
        if (updatePackItemDTO.getVariantId() == null) {
            return;
        }

        validationService.getAndCheckProduct(packItem.getItemId());
        productsRepository.getProductVariant(packItem.getItemId(), updatePackItemDTO.getVariantId().longValue());
    }

    private void validateUpdatePackItemDeliveryPointId(UpdatePackItemDTO updatePackItemDTO) {
        if (updatePackItemDTO.getDeliveryPointId() == null) {
            return;
        }

        DeliveryPoint deliveryPoint = productsRepository.getDeliveryPoint(updatePackItemDTO.getDeliveryPointId().longValue());
        securityManager.checkEntityAccessibleWithVisibility(deliveryPoint.getEntity().getId());
    }

    private void validatePriceTypeInSession(Session session, Long priceTypeId) {
        PriceTypes priceTypes = sessionsRepository.getPriceTypes(session.getEventId(), session.getId());
        if (priceTypes.getData().stream().noneMatch(priceType -> priceType.getId().equals(priceTypeId))) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION);
        }
    }

    private void validatePriceTypeMappingInSession(PackItem mainItem, List<PackItemPriceTypeMappingRequestDTO> requestPriceTypeMapping) {
        List<Long> sourcePriceTypes;
        if (PackItemType.EVENT.equals(mainItem.getType())) {
            sourcePriceTypes = venuesRepository.getPriceTypes(mainItem.getVenueTemplateId().longValue()).stream()
                    .map(PriceType::getId).collect(Collectors.toList());
        } else {
            Session mainItemSession = sessionsRepository.getSession(mainItem.getItemId());
            sourcePriceTypes = sessionsRepository.getPriceTypes(mainItemSession.getEventId(), mainItemSession.getId()).
                    getData().stream().map(es.onebox.mgmt.datasources.ms.event.dto.session.PriceType::getId).toList();
        }
        List<Integer> requestSourcePriceTypes = requestPriceTypeMapping.stream().map(PackItemPriceTypeMappingRequestDTO::getSourcePriceTypeId).toList();
        if (sourcePriceTypes.size() != requestPriceTypeMapping.size() ||
                !sourcePriceTypes.stream().allMatch(p -> requestSourcePriceTypes.contains(p.intValue()))) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION);
        }
        //Only supports 1 target priceType for each source - already not enabled for multiple target
        for (PackItemPriceTypeMappingRequestDTO item : requestPriceTypeMapping) {
            if (CollectionUtils.isEmpty(item.getTargetPriceTypeId()) || item.getTargetPriceTypeId().size() != 1) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.INVALID_TARGET_PRICE_TYPE_ID);
            }
        }
    }

    private void validateCreateSessionPackMainItem(CreatePackMainItemDTO packMainItemDTO){
        if (packMainItemDTO.getSubItemIds() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_SESSION_CANNOT_HAVE_SUB_ITEMS);
        }
        if (packMainItemDTO.getVenueTemplateId() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_SESSION_CANNOT_HAVE_VENUE_TEMPLATE_ID);
        }

        Session session = validationService.getAndCheckVisibilitySession(packMainItemDTO.getItemId());
        packsHelper.validateSessionStatus(session);
    }

    private void validateCreateEventPackMainItem(CreatePackMainItemDTO packMainItemDTO){
        if (packMainItemDTO.getVenueTemplateId() == null && PackItemType.EVENT.equals(packMainItemDTO.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_MAIN_ITEM_VENUE_TEMPLATE_ID_CANNOT_BE_NULL);
        }

        Event event = validationService.getAndCheckEvent(packMainItemDTO.getItemId());
        packsHelper.validateEventStatus(event);
    }

}

package es.onebox.mgmt.packs.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.categories.CategoryDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackItems;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItemSubItemsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItemSubitem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksFilterRequest;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItemSubitemsRequest;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannel;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannelSettingsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannels;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.UpdatePackChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.events.dto.PriceTypeDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.packs.dto.BasePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypeMappingRequestDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.dto.CreatePackMainItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackRateDTO;
import es.onebox.mgmt.packs.dto.PackDTO;
import es.onebox.mgmt.packs.dto.PackDetailDTO;
import es.onebox.mgmt.packs.dto.PackEventDTO;
import es.onebox.mgmt.packs.dto.PackItemEventDataDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypeMappingDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypesRequestDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.mgmt.packs.dto.PackItemProductDataDTO;
import es.onebox.mgmt.packs.dto.PackItemSessionDataDTO;
import es.onebox.mgmt.packs.dto.PackItemSubitemDTO;
import es.onebox.mgmt.packs.dto.PackItemSubitemResponseDTO;
import es.onebox.mgmt.packs.dto.PackItemVenueDTO;
import es.onebox.mgmt.packs.dto.PackItemVenueTemplateDTO;
import es.onebox.mgmt.packs.dto.PackPeriodDTO;
import es.onebox.mgmt.packs.dto.PackPricingDTO;
import es.onebox.mgmt.packs.dto.PackPromotionDTO;
import es.onebox.mgmt.packs.dto.PackSessionDateDTO;
import es.onebox.mgmt.packs.dto.PackSettingsDTO;
import es.onebox.mgmt.packs.dto.PackUISettings;
import es.onebox.mgmt.packs.dto.PacksFilterDTO;
import es.onebox.mgmt.packs.dto.PacksResponseDTO;
import es.onebox.mgmt.packs.dto.UpdatePackDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemSubitemsRequestDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelDetailDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelEntityInfoDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelInfoDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelStatusInfoDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelsDTO;
import es.onebox.mgmt.packs.dto.channels.PackInfoDTO;
import es.onebox.mgmt.packs.dto.channels.UpdatePackChannelDTO;
import es.onebox.mgmt.packs.dto.prices.PackPriceDTO;
import es.onebox.mgmt.packs.dto.prices.UpdatePackPriceDTO;
import es.onebox.mgmt.packs.dto.prices.UpdatePackPriceRequestListDTO;
import es.onebox.mgmt.packs.dto.rates.PackRateDTO;
import es.onebox.mgmt.packs.dto.rates.UpdatePackRateDTO;
import es.onebox.mgmt.packs.enums.PackRangeType;
import es.onebox.mgmt.packs.enums.PackStatus;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import es.onebox.mgmt.packs.enums.PriceTypeRangeDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PacksConverter {

    private PacksConverter() {
    }

    public static PackDetailDTO toPackDetailDTO(PackDetail pack) {
        PackDetailDTO packDetailDTO = mapPackDTO(pack, new PackDetailDTO());
        packDetailDTO.setTax(pack.getTax());
        if (ObjectUtils.anyNotNull(pack.getBaseCategory(), pack.getCustomCategory())) {
            PackSettingsDTO settings = new PackSettingsDTO();
            CategoriesDTO categories = new CategoriesDTO();
            if (pack.getBaseCategory() != null) {
                CategoryDTO base = new CategoryDTO();
                base.setId(pack.getBaseCategory().getId());
                base.setDescription(pack.getBaseCategory().getDescription());
                base.setCode(pack.getBaseCategory().getCode());
                categories.setBase(base);
            }
            if (pack.getCustomCategory() != null) {
                CategoryDTO custom = new CategoryDTO();
                custom.setId(pack.getCustomCategory().getId());
                custom.setDescription(pack.getCustomCategory().getDescription());
                custom.setCode(pack.getCustomCategory().getCode());
                categories.setCustom(custom);
            }
            settings.setCategories(categories);
            packDetailDTO.setSettings(settings);
        }

        return packDetailDTO;
    }

    public static List<PackDTO> toPacksDTO(List<Pack> packs) {
        if (CollectionUtils.isEmpty(packs)) {
            return new ArrayList<>();
        }
        return packs.stream().map(PacksConverter::toPackDTO).collect(Collectors.toList());
    }

    public static PacksResponseDTO toPacksResponseDTO(PacksResponse response) {
        PacksResponseDTO dto = new PacksResponseDTO();
        dto.setMetadata(response.getMetadata());
        dto.setData(response.getData().stream().map(PacksConverter::toPackDTO).collect(Collectors.toList()));
        return dto;
    }

    public static PackDTO toPackDTO(Pack pack) {
        return mapPackDTO(pack, new PackDTO());
    }

    public static <T extends PackDTO> T mapPackDTO(Pack pack, T packDTO) {
        packDTO.setId(pack.getId());
        packDTO.setName(pack.getName());
        packDTO.setActive(pack.getActive());
        packDTO.setType(PackTypeDTO.valueOf(pack.getType().name()));
        if (pack.getEntityId() != null || StringUtils.isNotBlank(pack.getEntityName())) {
            packDTO.setEntity(new IdNameDTO(pack.getEntityId(), pack.getEntityName()));
        }
        packDTO.setChannelId(pack.getChannelId());
        PackPromotionDTO packPromotionDTO = new PackPromotionDTO();
        packPromotionDTO.setEnabled(false);
        if (pack.getPromotionId() != null) {
            packPromotionDTO.setEnabled(true);
            packPromotionDTO.setPromotionId(pack.getPromotionId());
        }
        packDTO.setPromotion(packPromotionDTO);
        PackPeriodDTO packPeriodDTO = new PackPeriodDTO();
        if (pack.getPackRangeType() != null) {
            packPeriodDTO.setType(PackRangeType.getByName(pack.getPackRangeType().name()));
        }
        packPeriodDTO.setStartDate(pack.getCustomStartSaleDate());
        packPeriodDTO.setEndDate(pack.getCustomEndSaleDate());
        packDTO.setPackPeriod(packPeriodDTO);
        PackPricingDTO packPricingDTO = new PackPricingDTO();
        packPricingDTO.setType(pack.getPricingType());
        packPricingDTO.setPriceIncrement(pack.getPriceIncrement());
        packDTO.setPricing(packPricingDTO);

        packDTO.setPackUISettings(new PackUISettings());
        packDTO.getPackUISettings().setShowDate(pack.getShowDate());
        packDTO.getPackUISettings().setShowDateTime(pack.getShowDateTime());
        packDTO.getPackUISettings().setShowMainDate(pack.getShowMainDate());
        packDTO.getPackUISettings().setShowMainVenue(pack.getShowMainVenue());
        packDTO.setSuggested(pack.getSuggested());
        packDTO.setUnifiedPrice(pack.getUnifiedPrice());

        return packDTO;
    }

    public static CreatePack toMs(CreatePackDTO createPackDTO) {
        CreatePack createPack = new CreatePack();
        createPack.setName(createPackDTO.getName());
        createPack.setEntityId(createPackDTO.getEntityId());
        createPack.setType(PackType.getByName(createPackDTO.getType().name()));
        createPack.setTaxId(createPackDTO.getTaxId());
        createPack.setUnifiedPrice(createPackDTO.getUnifiedPrice());
        if (PackTypeDTO.AUTOMATIC.equals(createPackDTO.getType())) {
            createPack.setMainItem(toMs(createPackDTO.getMainItem()));
        }
        return createPack;
    }

    public static CreatePackItems toMs(CreatePackItemsDTO createPackItemsDTO) {
        if (CollectionUtils.isEmpty(createPackItemsDTO)) {
            return null;
        }
        CreatePackItems items = new CreatePackItems();
        createPackItemsDTO.forEach(item -> items.add(toMs(item)));

        return items;
    }

    private static CreatePackItem toMs(CreatePackItemDTO request) {
        CreatePackItem createPackItem = new CreatePackItem();
        createPackItem.setItemId(request.getItemId());
        createPackItem.setType(request.getType());
        createPackItem.setDisplayItemInChannels(request.getDisplayItemInChannels());
        if (PackItemType.SESSION.equals(request.getType())) {
            createPackItem.setPriceTypeId(request.getPriceTypeId());
            if (request.getPriceTypeMapping() != null) {
                createPackItem.setPriceTypeMapping(request.getPriceTypeMapping().stream().collect(Collectors.toMap(
                        PackItemPriceTypeMappingRequestDTO::getSourcePriceTypeId,
                        PackItemPriceTypeMappingRequestDTO::getTargetPriceTypeId)));
        }
        }
        if (PackItemType.PRODUCT.equals(request.getType())) {
            createPackItem.setVariantId(request.getVariantId());
            createPackItem.setDeliveryPointId(request.getDeliveryPointId());
            createPackItem.setSharedBarcode(request.getSharedBarcode());
        }
        return createPackItem;
    }

    private static CreatePackItem toMs(CreatePackMainItemDTO createPackMainItemDTO) {
        CreatePackItem createPackItem = new CreatePackItem();
        createPackItem.setItemId(createPackMainItemDTO.getItemId());
        createPackItem.setType(createPackMainItemDTO.getType());
        createPackItem.setSubItemIds(createPackMainItemDTO.getSubItemIds());
        createPackItem.setVenueTemplateId(createPackMainItemDTO.getVenueTemplateId());

        return createPackItem;
    }

    public static UpdatePack toMs(UpdatePackDTO updatePackDTO) {
        UpdatePack updatePack = new UpdatePack();
        updatePack.setName(updatePackDTO.getName());
        updatePack.setActive(updatePackDTO.getActive());
        updatePack.setPackRangeType(updatePackDTO.getPackPeriod() != null && updatePackDTO.getPackPeriod().getType() != null ?
                es.onebox.mgmt.datasources.ms.channel.enums.PackRangeType.getByName(updatePackDTO.getPackPeriod().getType().name()) : null);
        if (updatePackDTO.getPricing() != null) {
            updatePack.setPricingType(updatePackDTO.getPricing().getType());
            updatePack.setPriceIncrement(updatePackDTO.getPricing().getPriceIncrement());
        }
        updatePack.setCustomStartSaleDate(updatePackDTO.getPackPeriod() != null ? updatePackDTO.getPackPeriod().getStartDate() : null);
        updatePack.setCustomEndSaleDate(updatePackDTO.getPackPeriod() != null ? updatePackDTO.getPackPeriod().getEndDate() : null);

        if (updatePackDTO.getPackUISettings() != null) {
            updatePack.setShowDate(updatePackDTO.getPackUISettings().getShowDate());
            updatePack.setShowDateTime(updatePackDTO.getPackUISettings().getShowDateTime());
            updatePack.setShowMainVenue(updatePackDTO.getPackUISettings().getShowMainVenue());
            updatePack.setShowMainDate(updatePackDTO.getPackUISettings().getShowMainDate());
        }
        PackSettingsDTO settings = updatePackDTO.getSettings();
        if (settings != null && settings.getCategories() != null) {
            if (settings.getCategories().getBase() != null) {
                updatePack.setBaseCategoryId(settings.getCategories().getBase().getId());
            }
            if (settings.getCategories().getCustom() != null) {
                updatePack.setCustomCategoryId(settings.getCategories().getCustom().getId());
            }
        }
        if (updatePackDTO.getTaxId() != null) {
            updatePack.setTaxId(updatePackDTO.getTaxId());
        }
        if (updatePackDTO.getUnifiedPrice() != null) {
            updatePack.setUnifiedPrice(updatePackDTO.getUnifiedPrice());
        }
        if (updatePackDTO.getSuggested() != null) {
            updatePack.setSuggested(updatePackDTO.getSuggested());
        }

        return updatePack;
    }

    public static List<BasePackItemDTO> toPackDTO(PackType packType,
                                                  List<PackItem> items,
                                                  PackItem mainItem,
                                                  Event mainEventPackItemEvent,
                                                  VenueTemplate venueTemplate,
                                                  Map<Long, Session> sessions,
                                                  Map<Long, List<IdNameDTO>> priceTypes,
                                                  Map<Long, Product> products,
                                                  Map<Long, ProductVariant> variants,
                                                  Map<Long, DeliveryPoint> deliveryPoints) {
        return CollectionUtils.isEmpty(items) ? new ArrayList<>() : items.stream()
                .map(item -> {
                    BasePackItemDTO itemDTO = new BasePackItemDTO();
                    itemDTO.setId(item.getPackItemId());
                    itemDTO.setType(item.getType());
                    itemDTO.setItemId(item.getItemId());
                    itemDTO.setDisplayItemInChannels(item.getDisplayItemInChannels());
                    itemDTO.setInformativePrice(item.getInformativePrice());
                    if (PackType.AUTOMATIC.equals(packType)) {
                        itemDTO.setMain(BooleanUtils.isTrue(item.getMain()));
                    }
                    switch (item.getType()) {
                        case EVENT -> {
                            itemDTO.setEventData(convertToEventDataDTO(packType, item, venueTemplate));
                            itemDTO.setName(mainEventPackItemEvent.getName());
                        }
                        case SESSION -> {
                            Session session = sessions.get(item.getPackItemId());
                            itemDTO.setSessionData(convertToSessionDataDTO(packType, mainItem, item, session, priceTypes));
                            itemDTO.setName(session.getName());
                        }
                        case PRODUCT -> {
                            Product product = products.get(item.getPackItemId());
                            itemDTO.setProductData(convertToProductDataDTO(packType, item, variants, deliveryPoints));
                            itemDTO.setName(product.getName());
                        }
                    }
                    return itemDTO;
                })
                .toList();
    }

    private static PackItemEventDataDTO convertToEventDataDTO(PackType packType,
                                                              PackItem item,
                                                              VenueTemplate venueTemplate) {
        if (!PackType.AUTOMATIC.equals(packType) || BooleanUtils.isNotTrue(item.getMain())) {
            return null;
        }

        PackItemEventDataDTO eventDataDTO = new PackItemEventDataDTO();
        PackItemVenueDTO venueDTO = new PackItemVenueDTO();
        venueDTO.setId(venueTemplate.getVenue().getId());
        venueDTO.setName(venueTemplate.getVenue().getName());

        PackItemVenueTemplateDTO venueTemplateDTO = new PackItemVenueTemplateDTO();
        venueTemplateDTO.setVenue(venueDTO);
        venueTemplateDTO.setId(venueTemplate.getId());
        venueTemplateDTO.setName(venueTemplate.getName());
        eventDataDTO.setVenueTemplate(venueTemplateDTO);
        return eventDataDTO;
    }

    private static PackItemSessionDataDTO convertToSessionDataDTO(PackType packType,
                                                                  PackItem mainItem, PackItem item,
                                                                  Session session, Map<Long, List<IdNameDTO>> priceTypes) {
        PackItemSessionDataDTO sessionDataDTO = new PackItemSessionDataDTO();

        PackEventDTO packEventDTO = new PackEventDTO();
        packEventDTO.setId(session.getEventId());
        packEventDTO.setName(session.getEventName());
        sessionDataDTO.setEvent(packEventDTO);

        PackSessionDateDTO datesDTO = new PackSessionDateDTO();
        datesDTO.setStart(session.getDate().getStart());
        datesDTO.setEnd(session.getDate().getEnd());
        sessionDataDTO.setDates(datesDTO);

        PackItemVenueDTO venueDTO = new PackItemVenueDTO();
        venueDTO.setId(session.getVenueId());
        venueDTO.setName(session.getVenueName());

        PackItemVenueTemplateDTO venueTemplateDTO = new PackItemVenueTemplateDTO();
        venueTemplateDTO.setVenue(venueDTO);
        venueTemplateDTO.setId(session.getVenueConfigId());
        venueTemplateDTO.setName(session.getVenueConfigName());
        sessionDataDTO.setVenueTemplate(venueTemplateDTO);

        if (PackType.AUTOMATIC.equals(packType) && BooleanUtils.isNotTrue(item.getMain())) {
            if (item.getPriceTypeId() != null) {
                IdNameDTO priceType = priceTypes.get(item.getPackItemId()).stream()
                        .filter(p -> p.getId().equals(item.getPriceTypeId().longValue()))
                        .findFirst()
                        .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.PRICE_TYPE_NOT_FOUND));
            sessionDataDTO.setPriceType(new IdNameDTO(priceType.getId(), priceType.getName()));
            } else if (item.getPriceTypeMapping() != null) {
                Map<Long, IdNameDTO> mainItemPriceTypesById = priceTypes.get(mainItem.getPackItemId()).stream()
                        .collect(Collectors.toMap(IdNameDTO::getId, Function.identity()));
                List<IdNameDTO> itemPriceTypes = priceTypes.get(item.getPackItemId());
                sessionDataDTO.setPriceTypeMapping(new ArrayList<>());
                for (Map.Entry<Integer, List<Integer>> entry : item.getPriceTypeMapping().entrySet()) {
                    Long sourcePriceTypeId = entry.getKey().longValue();
                    PackItemPriceTypeMappingDTO mapping = new PackItemPriceTypeMappingDTO();
                    mapping.setSourcePriceTypeId(new IdNameDTO(sourcePriceTypeId, mainItemPriceTypesById.get(sourcePriceTypeId).getName()));
                    mapping.setTargetPriceTypeId(new ArrayList<>());
                    for (Integer targetPriceType : entry.getValue()) {
                        IdNameDTO priceType = itemPriceTypes.stream()
                                .filter(p -> p.getId().equals(targetPriceType.longValue()))
                                .findFirst()
                                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.PRICE_TYPE_NOT_FOUND));
                        mapping.getTargetPriceTypeId().add(priceType);
                    }
                    sessionDataDTO.getPriceTypeMapping().add(mapping);
                }
            }
        }

        return sessionDataDTO;
    }

    private static PackItemProductDataDTO convertToProductDataDTO(PackType packType,
                                                                  PackItem item,
                                                                  Map<Long, ProductVariant> variants,
                                                                  Map<Long, DeliveryPoint> deliveryPoints) {
        if (!PackType.AUTOMATIC.equals(packType)) {
            return null;
        }

        PackItemProductDataDTO productDataDTO = new PackItemProductDataDTO();

        if (variants.containsKey(item.getPackItemId())) {
            ProductVariant variant = variants.get(item.getPackItemId());
            productDataDTO.setVariant(new IdNameDTO(variant.getId(), variant.getName()));
        }

        DeliveryPoint deliveryPoint = deliveryPoints.get(item.getPackItemId());
        productDataDTO.setDeliveryPoint(new IdNameDTO(deliveryPoint.getId(), deliveryPoint.getName()));
        productDataDTO.setSharedBarcode(item.getSharedBarcode());

        return productDataDTO;
    }

    public static List<PackRateDTO> toRatesDTO(List<PackRate> packRates) {
        if (CollectionUtils.isEmpty(packRates)) {
            return new ArrayList<>();
        }
        return packRates.stream().map(PacksConverter::toPackDTO).collect(Collectors.toList());
    }

    private static PackRateDTO toPackDTO(PackRate packRate) {
        PackRateDTO packRateDTO = new PackRateDTO();
        packRateDTO.setId(packRate.getId());
        packRateDTO.setName(packRate.getName());
        packRateDTO.setIsDefault(packRate.isDefaultRate());
        packRateDTO.setRestrictiveAccess(packRate.isRestrictive());

        return packRateDTO;
    }

    public static CreatePackRate toMs(CreatePackRateDTO packRateDTO) {
        CreatePackRate packRate = new CreatePackRate();
        packRate.setName(packRateDTO.getName());
        packRate.setDefaultRate(packRateDTO.getDefault());
        packRate.setRestrictive(packRateDTO.getRestrictiveAccess());

        return packRate;
    }

    public static UpdatePackRate toMs(UpdatePackRateDTO packRateDTO) {
        UpdatePackRate packRate = new UpdatePackRate();
        packRate.setName(packRateDTO.getName());
        packRate.setDefault(packRateDTO.getDefault());
        packRate.setRestrictive(packRateDTO.getRestrictiveAccess());

        return packRate;
    }

    public static List<PackPriceDTO> toPricesDTO(List<PackPrice> packPrices) {
        if (CollectionUtils.isEmpty(packPrices)) {
            return new ArrayList<>();
        }
        return packPrices.stream().map(PacksConverter::toPackDTO).collect(Collectors.toList());
    }

    private static PackPriceDTO toPackDTO(PackPrice packPrice) {
        PackPriceDTO packPriceDTO = new PackPriceDTO();
        IdNameDTO idNameDTO = new IdNameDTO();
        idNameDTO.setId(packPrice.getRateId().longValue());
        idNameDTO.setName(packPrice.getRateName());
        packPriceDTO.setRate(idNameDTO);
        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(packPrice.getPriceTypeId());
        priceTypeDTO.setCode(packPrice.getPriceTypeCode());
        priceTypeDTO.setDescription(packPrice.getPriceTypeDescription());
        packPriceDTO.setPriceType(priceTypeDTO);
        packPriceDTO.setValue(packPrice.getPrice());

        return packPriceDTO;
    }

    public static List<UpdatePackPrice> toMs(UpdatePackPriceRequestListDTO updatePackPricesDTO) {
        if (CollectionUtils.isEmpty(updatePackPricesDTO)) {
            return null;
        }

        return updatePackPricesDTO.getPrices().stream().map(PacksConverter::toMs).collect(Collectors.toList());
    }

    public static UpdatePackPrice toMs(UpdatePackPriceDTO updatePackPriceDTO) {
        UpdatePackPrice updatePackPrice = new UpdatePackPrice();
        updatePackPrice.setPriceTypeId(updatePackPriceDTO.getPriceTypeId());
        updatePackPrice.setRateId(updatePackPriceDTO.getRateId());
        updatePackPrice.setPrice(updatePackPriceDTO.getValue());

        return updatePackPrice;
    }

    public static UpdatePackItem toMs(UpdatePackItemDTO request) {
        UpdatePackItem updatePackItem = new UpdatePackItem();
        updatePackItem.setPriceTypeId(request.getPriceTypeId());
        if (request.getPriceTypeMapping() != null) {
            updatePackItem.setPriceTypeMapping(request.getPriceTypeMapping().stream().collect(Collectors.toMap(
                    PackItemPriceTypeMappingRequestDTO::getSourcePriceTypeId,
                    PackItemPriceTypeMappingRequestDTO::getTargetPriceTypeId)));
        }
        updatePackItem.setVariantId(request.getVariantId());
        updatePackItem.setDeliveryPointId(request.getDeliveryPointId());
        updatePackItem.setSharedBarcode(request.getSharedBarcode());
        updatePackItem.setDisplayItemInChannels(request.getDisplayItemInChannels());
        updatePackItem.setInformativePrice(request.getInformativePrice());
        return updatePackItem;

    }

    public static PackItemPriceTypesResponseDTO toPackDTO(PackItemPriceTypesResponse response) {
        if (response == null) {
            return null;
        }
        PackItemPriceTypesResponseDTO dto = new PackItemPriceTypesResponseDTO();
        dto.setPriceTypes(response.getPriceTypes());
        dto.setSelectionType(PriceTypeRangeDTO.valueOf(response.getSelectionType().name()));
        return dto;
    }

    public static PackItemPriceTypesRequest toMs(PackItemPriceTypesRequestDTO packItemPriceTypesRequestDTO) {
        PackItemPriceTypesRequest packItemPriceTypesRequest = new PackItemPriceTypesRequest();
        packItemPriceTypesRequest.setSelectionType(packItemPriceTypesRequestDTO.getSelectionType());
        packItemPriceTypesRequest.setPriceTypeIds(packItemPriceTypesRequestDTO.getPriceTypeIds());
        return packItemPriceTypesRequest;
    }

    public static PacksFilterRequest convertFilter(long userOperatorId, PacksFilterDTO request) {
        PacksFilterRequest filter = new PacksFilterRequest();
        filter.setOperatorId(userOperatorId);
        filter.setEntityId(request.getEntityId());
        filter.setName(request.getName());
        filter.setOffset(request.getOffset());
        filter.setEventId(request.getEventId());
        filter.setStatus(request.getStatus());
        filter.setLimit(request.getLimit());
        filter.setSort(request.getSort());
        return filter;
    }

    public static PackChannelsDTO toPackChannelsDTO(PackChannels packChannels) {
        if (packChannels == null) {
            return null;
        }
        PackChannelsDTO dto = new PackChannelsDTO();
        dto.setData(toPackChannelDTOList(packChannels.getData()));
        dto.setMetadata(packChannels.getMetadata());
        return dto;
    }

    public static List<PackChannelDTO> toPackChannelDTOList(List<PackChannel> packChannels) {
        if (CollectionUtils.isEmpty(packChannels)) {
            return new ArrayList<>();
        }
        return packChannels
                .stream()
                .map(PacksConverter::toPackChannelDTO)
                .collect(Collectors.toList());
    }

    public static PackChannelDTO toPackChannelDTO(PackChannel packChannel) {
        PackChannelDTO dto = new PackChannelDTO();
        if (packChannel == null) {
            return null;
        }

        PackInfoDTO info = fillPackInfo(packChannel);
        dto.setPack(info);

        PackChannelInfoDTO channel = fillPackChannelInfo(packChannel);
        dto.setChannel(channel);

        PackChannelStatusInfoDTO status = fillPackChannelStatus(packChannel);
        dto.setStatus(status);

        return dto;
    }

    public static PackChannelDetailDTO toPackChannelDetailDTO(PackChannel packChannel) {
        PackChannelDetailDTO dto = new PackChannelDetailDTO();
        if (packChannel == null) {
            return null;
        }

        PackInfoDTO info = fillPackInfo(packChannel);
        dto.setPack(info);

        PackChannelInfoDTO channel = fillPackChannelInfo(packChannel);
        dto.setChannel(channel);

        PackChannelStatusInfoDTO status = fillPackChannelStatus(packChannel);
        dto.setStatus(status);

        if (packChannel.getSettings() != null) {
            PackChannelSettingsDTO settings = new PackChannelSettingsDTO();
            settings.setSuggested(packChannel.getSettings().getSuggested());
            settings.setOnSaleForLoggedUsers(packChannel.getSettings().getOnSaleForLoggedUsers());
            dto.setSettings(settings);
        }

        return dto;
    }

    private static PackChannelStatusInfoDTO fillPackChannelStatus(PackChannel packChannel) {
        PackChannelStatusInfoDTO status = new PackChannelStatusInfoDTO();
        status.setRequest(packChannel.getStatus().getRequest());
        return status;
    }

    private static PackChannelInfoDTO fillPackChannelInfo(PackChannel packChannel) {
        PackChannelInfoDTO channel = new PackChannelInfoDTO();
        channel.setId(packChannel.getChannel().getId());
        channel.setName(packChannel.getChannel().getName());
        channel.setType(ChannelSubtype.getById(packChannel.getChannel().getType().getIdSubtipo()));
        PackChannelEntityInfoDTO channelEntity = new PackChannelEntityInfoDTO();
        channelEntity.setId(packChannel.getChannel().getEntityId());
        channelEntity.setName(packChannel.getChannel().getEntityName());
        channelEntity.setLogo(packChannel.getChannel().getEntityLogo());
        channel.setEntity(channelEntity);
        return channel;
    }

    private static PackInfoDTO fillPackInfo(PackChannel packChannel) {
        PackInfoDTO info = new PackInfoDTO();
        info.setId(packChannel.getPack().getId());
        info.setName(packChannel.getPack().getName());
        info.setStatus(PackStatus.valueOf(packChannel.getPack().getStatus().name()));
        return info;
    }

    public static UpdatePackChannel toMs(UpdatePackChannelDTO in) {
        UpdatePackChannel out = new UpdatePackChannel();
        out.setSuggested(in.getSuggested());
        out.setOnSaleForLoggedUsers(in.getOnSaleForLoggedUsers());
        return out;
    }

    public static UpdatePackItemSubitemsRequest toMs(UpdatePackItemSubitemsRequestDTO request) {
        UpdatePackItemSubitemsRequest msRequest = new UpdatePackItemSubitemsRequest();
        msRequest.setSubitemIds(request.getSubitemIds());
        return msRequest;
    }

    public static PackItemSubitemResponseDTO toPackItemSubitemResponseDTO(PackItemSubItemsResponse response) {
        PackItemSubitemResponseDTO dto = new PackItemSubitemResponseDTO();
        dto.setMetadata(response.getMetadata());
        dto.setData(
                response.getData()
                        .stream()
                        .map(PacksConverter::toPackItemSubitemDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public static PackItemSubitemDTO toPackItemSubitemDTO(PackItemSubitem item) {
        PackItemSubitemDTO dto = new PackItemSubitemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setStartDate(item.getStartDate());
        return dto;
    }
}

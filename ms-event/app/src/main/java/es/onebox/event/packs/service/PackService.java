package es.onebox.event.packs.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.dto.product.ProductCatalogDTO;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.service.CatalogService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventPackErrorCode;
import es.onebox.event.packs.converter.PackConverter;
import es.onebox.event.packs.dao.PackCommunicationElementDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemSubsetDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.PackItemsPriceTypeDao;
import es.onebox.event.packs.dao.PackPriceTypeMappingDao;
import es.onebox.event.packs.dao.domain.PackRecord;
import es.onebox.event.packs.dto.CreatePackItemDTO;
import es.onebox.event.packs.dto.CreatePackItemsDTO;
import es.onebox.event.packs.dto.PackCreateRequest;
import es.onebox.event.packs.dto.PackDTO;
import es.onebox.event.packs.dto.PackDetailDTO;
import es.onebox.event.packs.dto.PackItemDTO;
import es.onebox.event.packs.dto.PackItemPriceTypeRequest;
import es.onebox.event.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.event.packs.dto.UpdatePackItemSubitemsRequestDTO;
import es.onebox.event.packs.dto.PackItemSubsetDTO;
import es.onebox.event.packs.dto.PackItemSubsetsFilter;
import es.onebox.event.packs.dto.PackItemSubsetsResponseDTO;
import es.onebox.event.packs.dto.PackUpdateRequest;
import es.onebox.event.packs.dto.PacksFilterRequest;
import es.onebox.event.packs.dto.PacksResponse;
import es.onebox.event.packs.dto.PriceTypeRange;
import es.onebox.event.packs.dto.UpdatePackItemDTO;
import es.onebox.event.packs.enums.PackItemSubsetType;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.packs.utils.PackUtils;
import es.onebox.event.products.dto.ProductEventsDTO;
import es.onebox.event.products.dto.ProductEventsFilterDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingFilterDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.products.service.ProductEventService;
import es.onebox.event.products.service.ProductSessionService;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sorting.SessionField;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemZonaPrecioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PackService {

    private static final int DEFAULT_COM_ELEMENT_POSITION = 1;
    private static final int PACK_TITLE_COM_ELEMENT_TAG_ID = 1;

    private final PackDao packsDao;
    private final PackItemsDao packItemsDao;
    private final SessionDao sessionDao;
    private final PackItemSubsetDao packItemSubsetDao;
    private final PackCommunicationElementDao packCommunicationElementDao;
    private final PackItemsPriceTypeDao packItemsPriceTypeDao;
    private final PackPriceTypeMappingDao packPriceTypeMappingDao;
    private final TaxDao taxDao;
    private final EntitiesRepository entitiesRepository;
    private final VenuesRepository venuesRepository;
    private final PackRateAndPricesService packRateAndPricesService;
    private final ProductEventService productEventService;
    private final ProductSessionService productSessionService;
    private final CatalogService catalogService;

    @Autowired
    public PackService(PackDao packsDao,
                       PackItemsDao packItemsDao, SessionDao sessionDao, PackItemSubsetDao packItemSubsetDao,
                       PackCommunicationElementDao packCommunicationElementDao,
                       PackItemsPriceTypeDao packItemsPriceTypeDao, PackPriceTypeMappingDao packPriceTypeMappingDao,
                       TaxDao taxDao,
                       EntitiesRepository entitiesRepository,
                       VenuesRepository venuesRepository,
                       PackRateAndPricesService packRateAndPricesService,
                       ProductEventService productEventService,
                       ProductSessionService productSessionService,
                       CatalogService catalogService) {
        this.packsDao = packsDao;
        this.packItemsDao = packItemsDao;
        this.sessionDao = sessionDao;
        this.packItemSubsetDao = packItemSubsetDao;
        this.packCommunicationElementDao = packCommunicationElementDao;
        this.packItemsPriceTypeDao = packItemsPriceTypeDao;
        this.packPriceTypeMappingDao = packPriceTypeMappingDao;
        this.taxDao = taxDao;
        this.entitiesRepository = entitiesRepository;
        this.venuesRepository = venuesRepository;
        this.packRateAndPricesService = packRateAndPricesService;
        this.productEventService = productEventService;
        this.productSessionService = productSessionService;
        this.catalogService = catalogService;
    }

    @MySQLRead
    public PacksResponse searchPacks(PacksFilterRequest filter) {
        List<PackRecord> packRecords = packsDao.getPackRecords(filter);
        Long count = packsDao.countByFilter(filter);
        return PackConverter.convert(packRecords, count, filter);
    }

    @MySQLRead
    public PackDetailDTO getPackById(Long packId) {
        PackDetailRecord packRecord = getAndCheckPackDetail(packId);
        return PackConverter.toPackDetailDTO(packRecord);
    }

    @MySQLWrite
    public PackDTO createPack(PackCreateRequest request) {
        validatePackItemSubsets(request);
        validatePackTax(request.getEntityId(), request.getTaxId());
        Integer packId = packsDao.insert(PackConverter.toRecord(request)).getIdpack();
        CpanelPackItemRecord packMainItemRecord = packItemsDao.insert(PackConverter.toMainItemRecord(request.getMainItem(), packId));

        if (isPackItemWithSubsets(request)) {
            insertPackItemSubsets(packMainItemRecord.getIdpackitem(), request.getMainItem().getSubItemIds());
        }

        if (!PackItemType.PRODUCT.equals(PackItemType.getById(packMainItemRecord.getTipoitem())) && packMainItemRecord.getIditem() != null) {
            PackItemPriceTypeRequest visibilityRequest = new PackItemPriceTypeRequest();
            visibilityRequest.setSelectionType(PriceTypeRange.ALL);
            updatePackItemPriceTypes(packId.longValue(), packMainItemRecord.getIdpackitem().longValue(), visibilityRequest);
        }
        packRateAndPricesService.refreshPackRates(packId.longValue());

        PackDetailRecord packDetailRecord = packsDao.getPackDetailRecordById(packId);
        return PackConverter.toDTO(packDetailRecord);
    }

    @MySQLWrite
    public void updatePack(Long packId, PackUpdateRequest request) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        validateUpdatePack(packRecord, request);
        PackConverter.toRecord(packRecord, request);

        packsDao.update(packRecord);
    }

    @MySQLWrite
    public void deletePack(Long packId) {
        getAndCheckPack(packId);

        packItemSubsetDao.deleteAllSubsetsByPackId(packId.intValue());
        packItemsDao.deleteAllPackItems(packId.intValue());
        packsDao.deletePackById(packId.intValue());
    }

    @MySQLRead
    public List<PackItemDTO> getPackItems(Long packId) {
        getAndCheckPack(packId);

        List<CpanelPackItemRecord> items = packItemsDao.getPackItemRecordsById(packId.intValue());
        Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> priceTypeMappingsByTargetItem = new HashMap<>();
        for (CpanelPackItemRecord item : items) {
            if (CommonUtils.isTrue(item.getZonapreciomapping())) {
                List<CpanelPackZonaPrecioMappingRecord> mappings = packPriceTypeMappingDao.getPackTargetItemMappings(item.getIdpackitem());
                priceTypeMappingsByTargetItem.putAll(mappings.stream().collect(
                        Collectors.groupingBy(CpanelPackZonaPrecioMappingRecord::getIdtargetpackitem,
                                Collectors.mapping(Function.identity(), Collectors.toList()))));
            }
        }

        return PackConverter.toItemsDTO(items, priceTypeMappingsByTargetItem);
    }

    @MySQLWrite
    public void createPackItems(Long packId, CreatePackItemsDTO request) {
        validateCreatePackItems(packId, request);

        getAndCheckPack(packId);
        CpanelPackItemRecord mainItem = packItemsDao.getPackMainItemRecordById(packId.intValue());

        if (CollectionUtils.isNotEmpty(request)) {
            for (CreatePackItemDTO newItemRequest : request) {
                CpanelPackItemRecord newItem = packItemsDao.insert(PackConverter.toRecord(newItemRequest, packId));
                createItemPriceTypeMapping(mainItem, newItemRequest, newItem);
            }
        }
    }

    private void createItemPriceTypeMapping(CpanelPackItemRecord mainItem, CreatePackItemDTO newItemRequest, CpanelPackItemRecord newItem) {
        if (MapUtils.isNotEmpty(newItemRequest.getPriceTypeMapping())) {
            for (Map.Entry<Integer, List<Integer>> mappingRequest : newItemRequest.getPriceTypeMapping().entrySet()) {
                Integer sourcePriceType = mappingRequest.getKey();
                for (Integer targetPriceType : mappingRequest.getValue()) {
                    CpanelPackZonaPrecioMappingRecord itemMapping = new CpanelPackZonaPrecioMappingRecord(
                            mainItem.getIdpackitem(), sourcePriceType, newItem.getIdpackitem(), targetPriceType);
                    packPriceTypeMappingDao.insert(itemMapping);
                }
            }
        }
    }

    @MySQLWrite
    public void updatePackItem(Long packId, Long packItemId, UpdatePackItemDTO request) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        CpanelPackItemRecord packItemRecord = getAndCheckPackItem(packItemId);
        CpanelPackItemRecord mainItem = null;
        if (MapUtils.isNotEmpty(request.getPriceTypeMapping())) {
            validatePriceTypeMapping(packItemRecord.getIditem().longValue(), request.getPriceTypeMapping());
            mainItem = packItemsDao.getPackMainItemRecordById(packId.intValue());
        }

        updatePackItemRecord(packRecord, packItemRecord, mainItem, request);
        packItemsDao.update(packItemRecord);
    }

    @MySQLWrite
    public void deletePackItem(Long packId, Long packItemId) {
        getAndCheckPack(packId);
        CpanelPackItemRecord packItemRecord = getAndCheckPackItem(packItemId);

        if (PackUtils.isEvent(packItemRecord) && BooleanUtils.isTrue(packItemRecord.getPrincipal())) {
            packItemSubsetDao.deleteAllSubsetsByPackItemId(packItemId.intValue());
        }
        packItemsDao.deletePackItemRecordById(packItemId.intValue());
    }

    @MySQLRead
    public PackItemPriceTypesResponseDTO getPackItemPriceTypes(Long packId, Long packItemId) {
        getAndCheckPack(packId);
        CpanelPackItemRecord packItemRecord = getAndCheckPackItem(packItemId);
        PriceTypeRange priceTypeRange = PriceTypeRange.getByType(packItemRecord.getZonapreciotiposeleccion());

        return switch (priceTypeRange) {
            case RESTRICTED -> {
                Long venueTemplateId = getVenueTemplateIdFromPackItem(packItemRecord);
                List<IdNameCodeDTO> priceTypes = venuesRepository.getPriceTypes(venueTemplateId);

                if (CollectionUtils.isEmpty(priceTypes)) {
                    throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
                }
                List<CpanelPackItemZonaPrecioRecord> packItemZonaPrecioList =
                        packItemsPriceTypeDao.getPackItemPriceTypesById(packItemId.intValue());
                if (CollectionUtils.isEmpty(packItemZonaPrecioList)) {
                    throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
                }
                yield PackConverter.toPackItemPriceTypesResponseDTO(packItemZonaPrecioList, priceTypes);
            }
            case ALL -> {
                PackItemPriceTypesResponseDTO dto = new PackItemPriceTypesResponseDTO();
                dto.setSelectionType(PriceTypeRange.ALL);
                yield dto;
            }
        };
    }

    @MySQLWrite
    public void updatePackItemPriceTypes(Long packId, Long packItemId, PackItemPriceTypeRequest request) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        CpanelPackItemRecord packItemRecord = getAndCheckPackItem(packItemId);

        Long venueTemplateId = getVenueTemplateIdFromPackItem(packItemRecord);
        List<IdNameCodeDTO> priceTypes = venuesRepository.getPriceTypes(venueTemplateId);
        List<Integer> priceTypeIds = priceTypes.stream().map(priceType -> priceType.getId().intValue()).toList();

        if (PriceTypeRange.RESTRICTED.equals(request.getSelectionType()) &&
                !new HashSet<>(priceTypeIds).containsAll(request.getPriceTypeIds())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
        }

        UpdatePackItemDTO updatePackItemDTO = new UpdatePackItemDTO();
        updatePackItemDTO.setPriceTypeRange(request.getSelectionType());
        updatePackItemRecord(packRecord, packItemRecord, null, updatePackItemDTO);
        packItemsDao.update(packItemRecord);

        packItemsPriceTypeDao.deletePackItemPriceTypesByConfigIdAndPackId(packItemId.intValue());
        if (PriceTypeRange.RESTRICTED.equals(request.getSelectionType())) {
            bulkInsertItemPriceTypes(request.getPriceTypeIds(), packItemId.intValue());
        }
    }

    @MySQLWrite
    public void createPackCommElements(Long packId) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        fillCommunicationElements(packRecord);
    }

    @MySQLRead
    public Long getPackMainItemEventId(Integer packId) {
        CpanelPackItemRecord mainItem = packItemsDao.getPackMainItemRecordById(packId);
        if (mainItem == null) {
            return null;
        }
        if (PackUtils.isEvent(mainItem)) {
            return mainItem.getIditem().longValue();
        }
        return catalogService.getSession(mainItem.getIditem()).getEventId();
    }

    @MySQLRead
    public PackItemSubsetsResponseDTO getPackItemSubsets(Long packId, Long packItemId, PackItemSubsetsFilter filter) {
        getAndCheckPack(packId);
        getAndCheckPackItem(packItemId);

        Long total = packItemSubsetDao.countSubsetsByPackItemId(packItemId.intValue());

        if (Objects.equals(total, 0L)) {
            return new PackItemSubsetsResponseDTO(List.of(), MetadataBuilder.build(filter, total));
        }

        var subsetRecords = packItemSubsetDao.getSubsetsByPackItemId(packItemId.intValue(), filter);

        var sessionIds = subsetRecords.stream()
                .filter(r -> PackItemSubsetType.SESSION.equals(PackItemSubsetType.getById(r.getType())))
                .map(r -> Long.valueOf(r.getIdsubitem()))
                .toList();

        var packItemSubsets = getPackItemSubsetsSessions(sessionIds);

        return PackConverter.toPackItemSubsetsResponseDTO(packItemSubsets, total, filter);
    }

    @MySQLWrite
    public void updatePackItemSubsets(Long packId, Long packItemId, UpdatePackItemSubitemsRequestDTO request) {
        List<Integer> requestedSubItemIds = request.getSubitemIds() != null ? request.getSubitemIds() : List.of();
        validatePackItemSubsetsForUpdate(packId, packItemId , requestedSubItemIds);

        packItemSubsetDao.deleteAllSubsetsByPackItemId(packItemId.intValue());

        if (CollectionUtils.isNotEmpty(requestedSubItemIds)) {
            insertPackItemSubsets(packItemId.intValue(), requestedSubItemIds);
        }
    }

    private List<PackItemSubsetDTO> getPackItemSubsetsSessions(List<Long> sessionIds) {
        var sessionList = sessionDao.findSessionsById(sessionIds);
        return PackConverter.toPackItemSubsetDTOList(sessionList);
    }


    private Long getVenueTemplateIdFromPackItem(CpanelPackItemRecord packItemRecord) {
        Long venueTemplateId = null;
        switch (PackItemType.getById(packItemRecord.getTipoitem())) {
            case EVENT -> venueTemplateId = packItemRecord.getIdconfiguracion().longValue();
            case SESSION -> venueTemplateId = getSessionItemTemplate(packItemRecord.getIditem());
            case PRODUCT -> throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
        }
        return venueTemplateId;
    }

    private Long getSessionItemTemplate(Integer sessionId) {
        Long venueTemplateId;
        Session catalogSession = catalogService.getSession(sessionId);
        if (catalogSession != null && catalogSession.getVenueConfigId() != null) {
            venueTemplateId = catalogSession.getVenueConfigId();
        } else {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_BAD_REQUEST);
        }
        return venueTemplateId;
    }

    private void bulkInsertItemPriceTypes(List<Integer> zoneIds, Integer packItemId) {
        List<CpanelPackItemZonaPrecioRecord> packItemZonaPrecioRecords = new ArrayList<>();
        zoneIds.forEach(priceTypeIds -> {
            CpanelPackItemZonaPrecioRecord record = new CpanelPackItemZonaPrecioRecord();
            record.setIdzonaprecio(priceTypeIds);
            record.setIdpackitem(packItemId);

            packItemZonaPrecioRecords.add(record);
        });

        if (CollectionUtils.isNotEmpty(packItemZonaPrecioRecords)) {
            packItemsPriceTypeDao.bulkInsert(packItemZonaPrecioRecords);
        }
    }

    private PackDetailRecord getAndCheckPackDetail(Long packId) {
        PackDetailRecord packRecord = packsDao.getPackDetailRecordById(packId.intValue());
        if (packRecord == null || PackStatus.DELETED.getId().equals(packRecord.getEstado())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_NOT_FOUND);
        }
        return packRecord;
    }

    private CpanelPackRecord getAndCheckPack(Long packId) {
        CpanelPackRecord packRecord = packsDao.getPackRecordById(packId.intValue());
        if (packRecord == null || PackStatus.DELETED.getId().equals(packRecord.getEstado())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_NOT_FOUND);
        }
        return packRecord;
    }

    private CpanelPackItemRecord getAndCheckPackItem(Long packItemId) {
        CpanelPackItemRecord packItemRecord = packItemsDao.getPackItemRecordById(packItemId.intValue());
        if (packItemRecord == null) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_NOT_FOUND);
        }
        return packItemRecord;
    }

    private void validateUpdatePack(CpanelPackRecord packRecord, PackUpdateRequest request) {
        if (BooleanUtils.isTrue(request.getActive())) {
            List<CpanelPackItemRecord> packItemRecords = packItemsDao.getPackItemRecordsById(packRecord.getIdpack());
            CpanelPackItemRecord mainPackItemRecord = PackUtils.getMainPackItemRecord(packItemRecords);
            if (PackUtils.isEvent(mainPackItemRecord)) {
                validateMainEventHasPublishedSessions(mainPackItemRecord);
            }
            List<CpanelPackItemRecord> productPackItemRecords = PackUtils.getProductPackItemRecords(packItemRecords);
            if (CollectionUtils.isNotEmpty(productPackItemRecords)) {
                validateProductsForPackActive(productPackItemRecords, mainPackItemRecord);
            }
        }
        if (request.getTaxId() != null) {
            validatePackTax(packRecord.getIdentidad().longValue(), request.getTaxId());
        }
    }

    private void validatePackTax(Long entityId, Long taxId) {
        List<Long> taxesByEntity = taxDao.getTaxesByEntity(entityId);
        if (!taxesByEntity.contains(taxId)) {
            throw OneboxRestException.builder(MsEventErrorCode.INVALID_ENTITY_TAX).build();
        }
    }

    private void validateCreatePackItems(Long packId, CreatePackItemsDTO request) {
        if (CollectionUtils.isEmpty(request)) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        validateNewItemCurrencies(packId, request);
        validatePriceTypeMappings(request);
    }

    private void validatePriceTypeMappings(CreatePackItemsDTO request) {
        for (CreatePackItemDTO item : request) {
            if (PackItemType.SESSION.equals(item.getType()) && item.getPriceTypeMapping() != null) {
                validatePriceTypeMapping(item.getItemId(), item.getPriceTypeMapping());
            }
        }
    }

    private void validatePriceTypeMapping(Long itemId, Map<Integer, List<Integer>> requestMapping) {
        Long sessionTemplate = getSessionItemTemplate(itemId.intValue());
        List<IdNameCodeDTO> itemPriceTypes = venuesRepository.getPriceTypes(sessionTemplate);
        List<Long> sessionPriceTypeIds = itemPriceTypes.stream().map(IdNameCodeDTO::getId).toList();
        for (Map.Entry<Integer, List<Integer>> entry : requestMapping.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue()) ||
                    entry.getValue().size() != 1 ||
                    !sessionPriceTypeIds.contains(entry.getValue().get(0).longValue())
            ) {
                throw OneboxRestException.builder(MsEventPackErrorCode.PACK_ITEM_PRICE_TYPES_INVALID_MAPPING).build();
            }
        }
    }

    private void fillCommunicationElements(CpanelPackRecord pack) {
        EntityDTO entity = entitiesRepository.getEntity(pack.getIdentidad());
        for (IdDTO languageId : entity.getSelectedLanguages()) {
            CpanelElementosComPackRecord newRecord = new CpanelElementosComPackRecord();
            newRecord.setIdpack(pack.getIdpack());
            newRecord.setIdtag(PACK_TITLE_COM_ELEMENT_TAG_ID);
            newRecord.setIdioma(languageId.getId().intValue());
            newRecord.setPosition(DEFAULT_COM_ELEMENT_POSITION);
            newRecord.setDestino(1);
            newRecord.setValor(pack.getNombre());
            packCommunicationElementDao.insert(newRecord);
        }
    }

    private void updatePackItemRecord(CpanelPackRecord packRecord,
                                      CpanelPackItemRecord packItemRecord,
                                      CpanelPackItemRecord mainItem, UpdatePackItemDTO request) {
        if (PackUtils.isSession(packItemRecord)
                && PackType.isAutomatic(packRecord)
                && BooleanUtils.isNotTrue(packItemRecord.getPrincipal())) {
            if (request.getPriceTypeId() != null && packItemRecord.getIdzonaprecio() != null) {
                ConverterUtils.updateField(packItemRecord::setIdzonaprecio, request.getPriceTypeId());
            } else if (MapUtils.isNotEmpty(request.getPriceTypeMapping()) && CommonUtils.isTrue(packItemRecord.getZonapreciomapping())) {
                packPriceTypeMappingDao.deleteTargetPackItem(packItemRecord.getIdpackitem());
                for (Map.Entry<Integer, List<Integer>> mappingRequest : request.getPriceTypeMapping().entrySet()) {
                    Integer sourcePriceType = mappingRequest.getKey();
                    for (Integer targetPriceType : mappingRequest.getValue()) {
                        CpanelPackZonaPrecioMappingRecord itemMapping = new CpanelPackZonaPrecioMappingRecord(
                                mainItem.getIdpackitem(), sourcePriceType, packItemRecord.getIdpackitem(), targetPriceType);
                        packPriceTypeMappingDao.insert(itemMapping);
                    }
                }
            }

        } else if (PackUtils.isProduct(packItemRecord)) {
            if (PackType.isAutomatic(packRecord)) {
                ConverterUtils.updateField(packItemRecord::setIdvariante, request.getVariantId());
                ConverterUtils.updateField(packItemRecord::setIdpuntoentrega, request.getDeliveryPointId());
            }
            ConverterUtils.updateField(packItemRecord::setCodigodebarrascompartido, request.getSharedBarcode());
        }
        if (request.getDisplayItemInChannels() != null) {
            ConverterUtils.updateField(packItemRecord::setMostraritemenchannels,
                    ConverterUtils.isTrueAsByte(request.getDisplayItemInChannels()));
        }
        if (BooleanUtils.isFalse(packItemRecord.getPrincipal()) && request.getInformativePrice() != null) {
            ConverterUtils.updateField(packItemRecord::setPrecioinformativo, request.getInformativePrice());
        }
        if (request.getPriceTypeRange() != null) {
            ConverterUtils.updateField(packItemRecord::setZonapreciotiposeleccion, request.getPriceTypeRange().getType());
        }
    }

    private void validateMainEventHasPublishedSessions(CpanelPackItemRecord packItemMainEvent) {
        /*
        TODO evaluate on promoter scope - check all channels?
        boolean hasPublishedSession = true;
        //packsHelper.getMainEventFirstSession(channelId, packItemMainEvent) != null;
        if (!hasPublishedSession) {
            throw ExceptionBuilder.build(MsEventPackErrorCode.PACK_CANNOT_BE_ENABLED_EVENT_HAS_NO_PUBLISHED_SESSIONS);
        }
        */
    }

    private void validateProductsForPackActive(List<CpanelPackItemRecord> productPackItems, CpanelPackItemRecord mainPackItem) {
        switch (PackUtils.getType(mainPackItem)) {
            case EVENT -> {
                Long mainEventId = mainPackItem.getIditem().longValue();
                for (CpanelPackItemRecord productPackItem : productPackItems) {
                    Long productId = productPackItem.getIditem().longValue();
                    ProductCatalogDTO product = catalogService.findCatalogProduct(productId);
                    validateProductIsActive(product, productId);
                    validateProductIsOnSaleForMainEvent(productId, mainEventId);
                }
            }
            case SESSION -> {
                Long mainSessionId = mainPackItem.getIditem().longValue();
                Long mainEventId = catalogService.getSession(mainSessionId.intValue()).getEventId();
                for (CpanelPackItemRecord productPackItem : productPackItems) {
                    Long productId = productPackItem.getIditem().longValue();
                    ProductCatalogDTO product = catalogService.findCatalogProduct(productId);
                    Long variantId = productPackItem.getIdvariante().longValue();
                    ProductStockType stockType = product.getStockType();
                    validateProductIsActive(product, productId);
                    validateProductIsOnSaleForMainSession(productId, mainEventId, mainSessionId);
                    validateProductHasStockForMainSession(productId, variantId, stockType, mainSessionId);
                }
            }
        }
    }

    private void validateProductIsActive(ProductCatalogDTO product, Long productId) {
        if (product == null || !ProductState.ACTIVE.equals(product.getState())) {
            throw ExceptionBuilder.build(MsEventPackErrorCode.PACK_CANNOT_BE_ENABLED_PRODUCT_IS_NOT_ACTIVE, productId);
        }
    }

    private void validateProductIsOnSaleForMainEvent(Long productId, Long eventId) {
        if (!packProductIsOnSaleForEvent(productId, eventId)) {
            throw ExceptionBuilder.build(MsEventPackErrorCode.PACK_CANNOT_BE_ENABLED_PRODUCT_IS_NOT_RELATED, productId);
        }
    }

    private void validateProductIsOnSaleForMainSession(Long productId, Long eventId, Long sessionId) {
        if (!packProductIsOnSaleForSession(productId, eventId, sessionId)) {
            throw ExceptionBuilder.build(MsEventPackErrorCode.PACK_CANNOT_BE_ENABLED_PRODUCT_IS_NOT_RELATED, productId);
        }
    }

    private void validateProductHasStockForMainSession(Long productId, Long variantId, ProductStockType stockType, Long sessionId) {
        boolean hasStock = packProductHasStockForMainSession(productId, variantId, stockType, sessionId);
        if (!hasStock) {
            throw ExceptionBuilder.build(MsEventPackErrorCode.PACK_CANNOT_BE_ENABLED_PRODUCT_WITHOUT_STOCK, productId);
        }
    }

    private boolean packProductIsOnSaleForEvent(Long productId, Long eventId) {
        if (eventId == null || productId == null) {
            return false;
        }

        ProductEventsFilterDTO filter = new ProductEventsFilterDTO();
        filter.setSessionSelectionType(SelectionType.ALL);
        filter.setStatus(ProductEventStatus.ACTIVE);
        filter.setEventIds(List.of(eventId));
        return CollectionUtils.isNotEmpty(productEventService.getProductEvents(productId, filter));
    }

    private boolean packProductIsOnSaleForSession(Long productId, Long eventId, Long sessionId) {
        if (eventId == null || productId == null || sessionId == null) {
            return false;
        }

        ProductEventsFilterDTO filter = new ProductEventsFilterDTO();
        filter.setStatus(ProductEventStatus.ACTIVE);
        filter.setEventIds(List.of(eventId));
        ProductEventsDTO productEvents = productEventService.getProductEvents(productId, filter);
        if (CollectionUtils.isEmpty(productEvents)) {
            return false;
        }
        if (SelectionType.ALL.equals(productEvents.get(0).getSessionsSelectionType())) {
            return true;
        }
        ProductSessionsPublishingFilterDTO publishingFilter = new ProductSessionsPublishingFilterDTO();
        publishingFilter.setSessionIds(List.of(sessionId));
        ProductSessionsPublishingDTO publishingSessions = productSessionService.getPublishingSessions(productId, eventId, publishingFilter);
        return publishingSessions != null && CollectionUtils.isNotEmpty(publishingSessions.getSessions());
    }

    private boolean packProductHasStockForMainSession(Long productId, Long variantId, ProductStockType stockType, Long sessionId) {
        return switch (stockType) {
            case UNBOUNDED -> true;
            case BOUNDED -> {
                Long stock = catalogService.getProductVariantStock(productId, variantId);
                yield stock != null && stock > 0;
            }
            case SESSION_BOUNDED -> {
                Long stock = catalogService.getProductVariantSessionStock(productId, variantId, sessionId);
                yield stock != null && stock > 0;
            }
        };
    }

    void validateNewItemCurrencies(Long packId, CreatePackItemsDTO request) {
        Long packMainItemEventId = getPackMainItemEventId(packId.intValue());
        Event event = catalogService.getEvent(packMainItemEventId.intValue());
        Integer mainItemCurrency = event.getCurrency();

        Map<PackItemType, Set<Long>> itemIdsByPackItemType = getItemIdsByPackItemType(request);
        validateNewItemsCurrenciesWithPackCurrency(itemIdsByPackItemType, mainItemCurrency);
    }

    private static Map<PackItemType, Set<Long>> getItemIdsByPackItemType(CreatePackItemsDTO request) {
        return request.stream()
                .collect(Collectors.groupingBy(
                        CreatePackItemDTO::getType,
                        Collectors.mapping(CreatePackItemDTO::getItemId, Collectors.toSet())));
    }

    private void validateNewItemsCurrenciesWithPackCurrency(Map<PackItemType, Set<Long>> itemIdsByPackItemType,
                                                            Integer packCurrencyId) {
        Set<Long> eventIds = getEventsAndValidate(itemIdsByPackItemType);
        Set<Long> productIds = itemIdsByPackItemType.get(PackItemType.PRODUCT);

        if (CollectionUtils.isNotEmpty(eventIds)) {
            List<Event> events = getAndValidateEvents(eventIds);

            boolean anyEventDontMatchCurrency = events.stream()
                    .anyMatch(event -> !packCurrencyId.equals(event.getCurrency()));

            if (anyEventDontMatchCurrency) {
                throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEMS_MUST_HAVE_SAME_CURRENCY);
            }
        }

        if (CollectionUtils.isNotEmpty(productIds)) {
            List<ProductCatalogDTO> products = getAndValidateProducts(productIds);

            boolean anyProductDontMatchCurrency = products.stream()
                    .anyMatch(product -> !packCurrencyId.equals(product.getCurrencyId()));

            if (anyProductDontMatchCurrency) {
                throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEMS_MUST_HAVE_SAME_CURRENCY);
            }
        }
    }

    private Set<Long> getEventsAndValidate(Map<PackItemType, Set<Long>> itemIdsByPackItemType) {
        Set<Long> sessionIds = itemIdsByPackItemType.get(PackItemType.SESSION);
        Set<Long> eventIds = itemIdsByPackItemType.get(PackItemType.EVENT);

        if (CollectionUtils.isNotEmpty(sessionIds)) {
            Set<Integer> sessionIdsSet = sessionIds.stream()
                    .map(Long::intValue)
                    .collect(Collectors.toSet());

            List<Session> sessions = new ArrayList<>();
            for (Integer sessionId : sessionIdsSet) {
                Session session = catalogService.getSession(sessionId);
                sessions.add(session);
            }

            if (CollectionUtils.isEmpty(sessions) || sessions.size() != sessionIdsSet.size()) {
                throw new OneboxRestException(MsEventErrorCode.SESSION_NOT_FOUND);
            }

            Set<Long> sessionEventIds = sessions.stream().map(Session::getEventId).collect(Collectors.toSet());
            if (eventIds == null) {
                eventIds = sessionEventIds;
            } else {
                eventIds.addAll(sessionEventIds);
            }
        }
        return eventIds;
    }

    private List<Event> getAndValidateEvents(Set<Long> eventIds) {
        List<Event> events = new ArrayList<>();
        for (Long eventId : eventIds) {
            Event event = catalogService.getEvent(eventId.intValue());
            events.add(event);
        }
        if (CollectionUtils.isEmpty(events) || events.size() != eventIds.size()) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        return events;
    }

    private List<ProductCatalogDTO> getAndValidateProducts(Set<Long> productIds) {
        List<ProductCatalogDTO> products = new ArrayList<>();
        for (Long productId : productIds) {
            ProductCatalogDTO product = catalogService.findCatalogProduct(productId);
            products.add(product);

        }
        if (CollectionUtils.isEmpty(products) || products.size() != productIds.size()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    private boolean isPackItemWithSubsets(PackCreateRequest request) {
        return PackItemType.EVENT.equals(request.getMainItem().getType()) &&
                CollectionUtils.isNotEmpty(request.getMainItem().getSubItemIds());
    }

    private void validatePackItemSubsets(PackCreateRequest request) {
        if (!isPackItemWithSubsets(request)){
            return;
        }

        validateSubItemIds(request.getMainItem().getItemId(), request.getMainItem().getVenueTemplateId().longValue(), request.getMainItem().getSubItemIds());
    }

    private void validatePackItemSubsetsForUpdate(Long packId, Long packItemId, List<Integer> subItemIds) {
        getAndCheckPack(packId);
        CpanelPackItemRecord packItemRecord = getAndCheckPackItem(packItemId);

        if (!PackUtils.isEvent(packItemRecord) && BooleanUtils.isFalse(packItemRecord.getPrincipal())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_INVALID_FOR_SUBSETS);
        }

        validateSubItemIds(packItemRecord.getIditem().longValue(), packItemRecord.getIdconfiguracion().longValue(), subItemIds);
    }


    private void validateSubItemIds(Long eventId, Long venueConfigId, List<Integer> subItemIds) {
        if (CollectionUtils.isEmpty(subItemIds)) {
            return;
        }

        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setEventId(List.of(eventId));
        filter.setVenueConfigId(venueConfigId);
        filter.setIds(subItemIds.stream().map(Integer::longValue).toList());
        filter.setStatus(List.of(
                SessionStatus.PLANNED,
                SessionStatus.READY,
                SessionStatus.IN_PROGRESS,
                SessionStatus.SCHEDULED,
                SessionStatus.PREVIEW
        ));
        filter.setFields(List.of(SessionField.VENUE_TEMPLATE_ID.getRequestField(), SessionField.ID.getRequestField()));

        List<SessionRecord> sessionRecords = sessionDao.findSessions(filter, null);

        Set<Integer> foundIds = sessionRecords.stream()
                .map(SessionRecord::getIdsesion)
                .collect(Collectors.toSet());

        if (!foundIds.containsAll(subItemIds)) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_ITEM_SUBSETS_NOT_FOUND);
        }
    }



    private void insertPackItemSubsets(Integer packItemId, List<Integer> subItemIds) {
        List<CpanelPackItemSubsetRecord> subsetRecords = new ArrayList<>();
        for (Integer subItemId : subItemIds) {
            CpanelPackItemSubsetRecord subsetRecord = new CpanelPackItemSubsetRecord();
            subsetRecord.setIdpackitem(packItemId);
            subsetRecord.setIdsubitem(subItemId);
            subsetRecord.setType(PackItemSubsetType.SESSION.getId());
            subsetRecords.add(subsetRecord);
        }

        if (CollectionUtils.isNotEmpty(subsetRecords)) {
            packItemSubsetDao.bulkInsert(subsetRecords);
        }
    }

}

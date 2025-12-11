package es.onebox.event.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.catalog.dao.CatalogChannelPackCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventPackErrorCode;
import es.onebox.event.packs.converter.PackConverter;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackItemSubsetDao;
import es.onebox.event.packs.dao.PackItemsDao;
import es.onebox.event.packs.dao.PackRateDao;
import es.onebox.event.packs.dto.CreatePackRateDTO;
import es.onebox.event.packs.dto.PackPriceDTO;
import es.onebox.event.packs.dto.UpdatePackPriceDTO;
import es.onebox.event.packs.dto.UpdatePackRateDTO;
import es.onebox.event.packs.enums.PackItemSubsetType;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.packs.utils.PackUtils;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PackRateAndPricesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackRateAndPricesService.class);

    private final PackDao packsDao;
    private final RateDao rateDao;
    private final PackRateDao packRateDao;
    private final PackItemsDao packItemsDao;
    private final PackItemSubsetDao packItemSubsetDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final SessionDao sessionDao;
    private final EventDao eventDao;
    private final CatalogChannelPackCouchDao catalogChannelPackCouchDao;
    private final SessionRateDao sessionRateDao;

    @Autowired
    public PackRateAndPricesService(PackDao packsDao,
                                    RateDao rateDao,
                                    PackRateDao packRateDao,
                                    PackItemsDao packItemsDao, PackItemSubsetDao packItemSubsetDao,
                                    PriceZoneAssignmentDao priceZoneAssignmentDao,
                                    SessionDao sessionDao, EventDao eventDao, CatalogChannelPackCouchDao catalogChannelPackCouchDao, SessionRateDao sessionRateDao) {
        this.packsDao = packsDao;
        this.rateDao = rateDao;
        this.packRateDao = packRateDao;
        this.packItemsDao = packItemsDao;
        this.packItemSubsetDao = packItemSubsetDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.sessionDao = sessionDao;
        this.eventDao = eventDao;
        this.catalogChannelPackCouchDao = catalogChannelPackCouchDao;
        this.sessionRateDao = sessionRateDao;
    }

    @MySQLRead
    public List<RateDTO> getPackRates(Long packId) {
        getAndCheckPack(packId);
        List<CpanelTarifaPackRecord> rates = packRateDao.getRatesByPackId(packId.intValue());
        if (CollectionUtils.isEmpty(rates)) {
            return null;
        }
        Map<Integer, CpanelTarifaPackRecord> ratesById = rates.stream()
                .collect(Collectors.toMap(CpanelTarifaPackRecord::getIdtarifa, Function.identity()));

        return PackConverter.toRatesDTO(ratesById, rateDao.search(ratesById.keySet().stream().toList()));
    }

    @MySQLWrite
    public IdDTO createPackRate(Long packId, CreatePackRateDTO packRateDTO) {
        getAndCheckPack(packId);
        List<CpanelTarifaPackRecord> rates = packRateDao.getRatesByPackId(packId.intValue());

        CpanelTarifaRecord tarifaRecord = PackConverter.toRecord(packRateDTO, packId);
        Integer rateId = rateDao.insert(tarifaRecord).getIdtarifa();

        CpanelTarifaPackRecord tarifaPackRecord = PackConverter.toRecord(packRateDTO, rates, rateId, packId);

        if (CollectionUtils.isNotEmpty(rates) && BooleanUtils.isTrue(packRateDTO.getDefaultRate())) {
            packRateDao.resetDefaultsByPackId(packId.intValue());
        }
        packRateDao.insert(tarifaPackRecord);

        CpanelPackItemRecord mainItem = packItemsDao.getPackMainItemRecordById(packId.intValue());
        createPackVenueTemplatePriceZonesPrices(mainItem, rateId);

        return new IdDTO(rateId.longValue());
    }

    @MySQLWrite
    public void updatePackRate(Long packId, Long rateId, UpdatePackRateDTO updatePackRateDTO) {
        getAndCheckPack(packId);
        CpanelTarifaRecord tarifaRecord = rateDao.findById(rateId.intValue());
        CpanelTarifaPackRecord tarifaPackRecord = packRateDao.findPackRateById(rateId.intValue(), packId.intValue());
        if (tarifaRecord == null || tarifaPackRecord == null) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_RATE_NOT_FOUND);
        }
        PackConverter.updateRecord(tarifaRecord, updatePackRateDTO);
        rateDao.update(tarifaRecord);

        if (BooleanUtils.isTrue(updatePackRateDTO.getDefaultRate())) {
            packRateDao.resetDefaultsByPackId(packId.intValue());
            PackConverter.updateRecord(tarifaPackRecord, updatePackRateDTO);
            packRateDao.update(tarifaPackRecord);
        }
    }

    @MySQLWrite
    public void refreshPackRates(Long packId) {
        CpanelPackRecord pack = getAndCheckPack(packId);
        if (PackStatus.ACTIVE.getId().equals(pack.getEstado())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_STATUS_INVALID);
        }

        List<CpanelTarifaPackRecord> currentPackRates = packRateDao.getRatesByPackId(packId.intValue());

        CpanelPackItemRecord mainItem = packItemsDao.getPackMainItemRecordById(packId.intValue());
        List<CpanelTarifaRecord> mainItemRates = null;
        PackItemType itemType = PackUtils.getType(mainItem);
        if (PackUtils.isEvent(mainItem)) {
            mainItemRates = rateDao.getEventRates(mainItem.getIditem());
        } else if (PackItemType.SESSION.equals(itemType)) {
            mainItemRates = rateDao.getRatesBySession(mainItem.getIditem());
        }

        if (CollectionUtils.isNotEmpty(mainItemRates)) {

            //Create pack rates from scratch
            if (CollectionUtils.isEmpty(currentPackRates)) {
                LOGGER.info("[CREATE PACK RATES] packId: {} - Create all rates from main item: {} of type: {}",
                        packId, mainItem.getIditem(), itemType);
                for (CpanelTarifaRecord mainItemRate : mainItemRates) {
                    createPackRateFromMainItem(mainItemRate, CommonUtils.isTrue(mainItemRate.getDefecto()), packId);
                }
            } else {
                LOGGER.info("[REFRESH PACK RATES] packId: {} - Reload existing rates from main item: {} of type: {}",
                        packId, mainItem.getIditem(), itemType);

                Map<Integer, CpanelTarifaPackRecord> currentRatesByRelatedId = currentPackRates.stream()
                        .filter(p -> p.getIdtarifaevento() != null)
                        .collect(Collectors.toMap(CpanelTarifaPackRecord::getIdtarifaevento, Function.identity()));

                //Refresh from legacy without relationsx
                if (currentRatesByRelatedId.isEmpty() && currentPackRates.size() == 1) {
                    for (CpanelTarifaRecord mainItemRate : mainItemRates) {
                        if (CommonUtils.isTrue(mainItemRate.getDefecto())) {
                            Integer packRateId = currentPackRates.get(0).getIdtarifa();
                            updatePackRateFromMainItem(packRateId, mainItemRate, packId);
                        } else {
                            createPackRateFromMainItem(mainItemRate, false, packId);
                        }
                    }
                } else {
                    //Refresh already mapped with relatedId pack rates
                    Set<CpanelTarifaRecord> toCreateRates = new HashSet<>();
                    for (CpanelTarifaRecord mainItemRate : mainItemRates) {
                        CpanelTarifaPackRecord rateMatch = currentRatesByRelatedId.get(mainItemRate.getIdtarifa());
                        if (rateMatch != null) {
                            LOGGER.info("[REFRESH PACK RATES] packId: {} - Update found pack rate data with id: {}", packId, rateMatch.getIdtarifa());
                            updatePackRateFromMainItem(rateMatch.getIdtarifa(), mainItemRate, packId);
                            currentPackRates.removeIf(r -> mainItemRate.getIdtarifa().equals(r.getIdtarifaevento()));
                        } else {
                            toCreateRates.add(mainItemRate);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(toCreateRates)) {
                        LOGGER.info("[REFRESH PACK RATES] packId: {} - Merge pack rates - create {} new rates",
                                packId, toCreateRates.size());
                        for (CpanelTarifaRecord toCreate : toCreateRates) {
                            createPackRateFromMainItem(toCreate, CommonUtils.isTrue(toCreate.getDefecto()), packId);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(currentPackRates)) {
                        LOGGER.info("[REFRESH PACK RATES] packId: {} - Merge pack rates - delete {} unmatched with event pack rates",
                                packId, toCreateRates.size());
                        for (CpanelTarifaPackRecord toDelete : currentPackRates) {
                            deletePackRate(packId, toDelete.getIdtarifa().longValue());
                        }
                    }
                }
            }
        }
    }

    @MySQLWrite
    public void deletePackRate(Long packId, Long rateId) {
        getAndCheckPack(packId);
        CpanelTarifaRecord tarifaRecord = rateDao.findById(rateId.intValue());
        CpanelTarifaPackRecord tarifaPackRecord = packRateDao.findPackRateById(rateId.intValue(), packId.intValue());
        if (tarifaRecord == null || tarifaPackRecord == null) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_RATE_NOT_FOUND);
        }

        priceZoneAssignmentDao.deleteByRateId(rateId.intValue());
        packRateDao.delete(tarifaPackRecord);
        rateDao.delete(tarifaRecord);
    }

    @MySQLRead
    public List<PackPriceDTO> getPackPrices(Long packId) {
        CpanelPackRecord pack = getAndCheckPack(packId);

        List<CpanelTarifaPackRecord> rates = packRateDao.getRatesByPackId(packId.intValue());
        CpanelPackItemRecord mainItem = packItemsDao.getPackMainItemRecordById(pack.getIdpack());
        Set<Integer> validQuotaIds = new HashSet<>();
        if (PackType.isAutomatic(pack) && BooleanUtils.isTrue(isAvetSmartBookingEventPack(mainItem))) {
            List<CpanelTarifaRecord> sessionsRates = new ArrayList<>();
            List<Integer> sessionsPackFilter = getSessionFilter(mainItem);
            if (CollectionUtils.isNotEmpty(sessionsPackFilter)) {
                sessionsRates = sessionRateDao.getRatesBySessionIds(sessionsPackFilter);
            }
            if (CollectionUtils.isNotEmpty(sessionsRates)) {
                validQuotaIds.addAll(sessionsRates.stream().map(CpanelTarifaRecord::getIdtarifa).toList());
            }
        }
        if (CollectionUtils.isEmpty(rates)) {
            return null;
        }

        if (CollectionUtils.isNotEmpty(validQuotaIds)) {
            rates.removeIf(rate -> !validQuotaIds.contains(rate.getIdtarifaevento()));
        }
        return PackConverter.toPricesDTO(rates.stream().map(CpanelTarifaPackRecord::getIdtarifa)
                .map(priceZoneAssignmentDao::getPrices).flatMap(List::stream).collect(Collectors.toList()));
    }

    private List<Integer> getSessionFilter(CpanelPackItemRecord mainItem) {
        if (mainItem == null || !PackItemType.EVENT.equals(PackUtils.getType(mainItem))) {
            return Collections.emptyList();
        }

        List<CpanelPackItemSubsetRecord> subsetRecords = packItemSubsetDao.getSubsetsByPackItemId(mainItem.getIdpackitem());
        if (CollectionUtils.isEmpty(subsetRecords)) {
            return Collections.emptyList();
        }

        return subsetRecords.stream()
                .filter(r -> PackItemSubsetType.SESSION.equals(PackItemSubsetType.getById(r.getType())))
                .map(CpanelPackItemSubsetRecord::getIdsubitem)
                .toList();
    }

    private Boolean isAvetSmartBookingEventPack(CpanelPackItemRecord mainItem) {
        Boolean isAvetSmartBookingEventPack = false;
        if (mainItem != null && PackItemType.EVENT.getId().equals(mainItem.getTipoitem())
                && BooleanUtils.isTrue(mainItem.getPrincipal()) && mainItem.getIdconfiguracion() != null) {
            Map.Entry<EventRecord, List<VenueRecord>> eventMap = eventDao.findEvent(mainItem.getIditem().longValue());
            EventRecord event = eventMap.getKey();
            List<VenueRecord> venueConfigs = eventMap.getValue();
            //Events FCB has normal and activity template
            if (event != null && EventUtils.isAvet(event.getTipoevento()) && CollectionUtils.isNotEmpty(venueConfigs) ) {
                Boolean hasNormalTemplate = venueConfigs.stream().anyMatch( venueRecord -> venueRecord.getVenueConfigType() != null && EventUtils.isAvetTemplate(venueRecord.getVenueConfigType().getId()));
                Boolean hasActivityTemplate = venueConfigs.stream().anyMatch( venueRecord -> venueRecord.getVenueConfigType() != null && EventUtils.isActivityTemplate(venueRecord.getVenueConfigType().getId()));
                Boolean hasMainItemTemplate = venueConfigs.stream().anyMatch( venueRecord -> venueRecord.getVenueConfigId() != null && venueRecord.getVenueConfigId().equals(mainItem.getIdconfiguracion().longValue()));
                return BooleanUtils.isTrue(hasNormalTemplate && hasActivityTemplate && hasMainItemTemplate);
            }
        }
        return isAvetSmartBookingEventPack;
    }

    @MySQLWrite
    public void updatePackPrices(Long packId, List<UpdatePackPriceDTO> prices) {
        getAndCheckPack(packId);
        if (CollectionUtils.isEmpty(prices)) {
            return;
        }
        prices.forEach(price -> priceZoneAssignmentDao.updatePrices(
                price.getPriceTypeId(), price.getRateId(), NumberUtils.scale(price.getPrice()).doubleValue()));
    }

    private CpanelPackRecord getAndCheckPack(Long packId) {
        CpanelPackRecord packRecord = packsDao.getPackRecordById(packId.intValue());
        if (packRecord == null || PackStatus.DELETED.getId().equals(packRecord.getEstado())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_NOT_FOUND);
        }
        return packRecord;
    }

    private void createPackRateFromMainItem(CpanelTarifaRecord mainItemRate, boolean defaultRate, Long packId) {
        CreatePackRateDTO packRate = new CreatePackRateDTO();
        packRate.setName(mainItemRate.getNombre());
        packRate.setDescription(mainItemRate.getDescripcion());
        packRate.setDefaultRate(defaultRate);
        packRate.setRelatedRateId(mainItemRate.getIdtarifa());
        createPackRate(packId, packRate);
    }

    private void updatePackRateFromMainItem(Integer rateId, CpanelTarifaRecord mainItemRate, Long packId) {
        UpdatePackRateDTO request = new UpdatePackRateDTO();
        request.setName(mainItemRate.getNombre());
        request.setDescription(mainItemRate.getDescripcion());
        request.setDefaultRate(CommonUtils.isTrue(mainItemRate.getDefecto()));
        updatePackRate(packId, rateId.longValue(), request);
    }

    private void createPackVenueTemplatePriceZonesPrices(CpanelPackItemRecord mainPackItem, Integer rateId) {
        List<CpanelZonaPreciosConfigRecord> templatePriceZones = getCpanelZonaPreciosConfigRecordList(mainPackItem);

        if (CollectionUtils.isNotEmpty(templatePriceZones)) {
            for (CpanelZonaPreciosConfigRecord priceZone : templatePriceZones) {
                CpanelAsignacionZonaPreciosRecord pz = new CpanelAsignacionZonaPreciosRecord();
                pz.setIdtarifa(rateId);
                pz.setIdzona(priceZone.getIdzona());
                pz.setPrecio(9999.99);
                priceZoneAssignmentDao.insert(pz);
            }
        }
    }

    private List<CpanelZonaPreciosConfigRecord> getCpanelZonaPreciosConfigRecordList(CpanelPackItemRecord mainPackItem) {
        if (PackUtils.isSession(mainPackItem)) {
            CpanelSesionRecord mainSession = sessionDao.findById(mainPackItem.getIditem());
            return priceZoneAssignmentDao.getVenueTemplatePriceZones(mainSession.getIdrelacionentidadrecinto());
        }
        if (PackUtils.isEvent(mainPackItem)) {
            return priceZoneAssignmentDao.getVenueTemplatePriceZonesByTemplateId(mainPackItem.getIdconfiguracion());
        }
        return null;
    }

}

package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.venue.dto.CommunicationElementType;
import es.onebox.event.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.amqp.tiermodification.TierModificationMessage;
import es.onebox.event.events.converter.TierConverter;
import es.onebox.event.events.converter.TierTranslationConverter;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.SaleGroupDao;
import es.onebox.event.events.dao.TierConfigCouchDao;
import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.TierLimitCouchDao;
import es.onebox.event.events.dao.TierSaleGroupCouchDao;
import es.onebox.event.events.dao.TierSaleGroupDao;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.dao.record.TierSaleGroupRecord;
import es.onebox.event.events.domain.TierConfig;
import es.onebox.event.events.dto.TierCommElemFilterDTO;
import es.onebox.event.events.dto.TierCommunicationElementDTO;
import es.onebox.event.events.dto.TierCondition;
import es.onebox.event.events.dto.TierCreationRequestDTO;
import es.onebox.event.events.dto.TierDTO;
import es.onebox.event.events.dto.TierExtendedDTO;
import es.onebox.event.events.dto.TierPriceTypeAvailabilityDTO;
import es.onebox.event.events.dto.TierSalesGroupLimitDTO;
import es.onebox.event.events.dto.TierUpdateRequestDTO;
import es.onebox.event.events.dto.TiersDTO;
import es.onebox.event.events.quartz.ScheduleReflectTierPriceService;
import es.onebox.event.events.request.TiersFilter;
import es.onebox.event.events.utils.EvaluableTierWrapper;
import es.onebox.event.events.utils.TierEvaluator;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventTierErrorCode;
import es.onebox.event.timezone.dao.TimeZoneDao;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierCupoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import java.util.HashMap;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class EventTierService {

    private static final int TIER_NAME_MAX_LENGTH = 50;
    public static final int COMM_ELEM_MAX_VALUE_LENGTH = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTierService.class);

    @Autowired
    private EventDao eventDao;
    @Autowired
    private TierDao tierDao;
    @Autowired
    private PriceTypeConfigDao priceZoneConfigDao;
    @Autowired
    private ScheduleReflectTierPriceService scheduleReflectTierPriceService;
    @Autowired
    private PriceZoneAssignmentDao priceZoneAssignmentDao;
    @Autowired
    private RefreshDataService refreshDataService;
    @Autowired
    private SaleGroupDao saleGroupDao;
    @Autowired
    private TierSaleGroupDao tierSaleGroupDao;
    @Autowired
    private TierSaleGroupCouchDao tierSaleGroupCouchDao;
    @Autowired
    private TierLimitCouchDao tierLimitCouchDao;
    @Autowired
    private TimeZoneDao timeZoneDao;
    @Autowired
    private TierConfigCouchDao tierConfigCouchDao;
    @Autowired
    private VenuesRepository venuesRepository;
    @Autowired
    private VenueTemplateDao venueTemplateDao;
    @Autowired
    private DefaultProducer tierModificationProducer;

    @MySQLWrite
    public CommonIdResponse createEventTier(Long eventId, TierCreationRequestDTO tierDTO) {
        validatePresentTier(tierDTO);
        CpanelEventoRecord event = validateEvent(eventId);
        if (tierDTO.getPriceTypeId() == null || tierDTO.getPriceTypeId() <= 0) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_PRICE_TYPE_MANDATORY);
        } else if (priceZoneConfigDao.getPriceZoneByEventId(eventId)
                .stream().noneMatch(pz -> tierDTO.getPriceTypeId().intValue() == pz.getIdzona())) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_PRICE_TYPE_BELONG_TO_EVENT);
        }
        validateTierName(tierDTO.getName(), tierDTO.getPriceTypeId());
        validateTierPrice(tierDTO.getPrice());
        validateTierStartDate(tierDTO.getStartDate(), tierDTO.getPriceTypeId(), event.getFechafin());
        CpanelTierRecord record = TierConverter.convert(tierDTO);
        record.setVenta((byte) 1);
        record.setTimezone(timeZoneDao.findByPriceZone(tierDTO.getPriceTypeId().intValue()).getZoneid());
        CpanelTierRecord tier = tierDao.insert(record);
        if (tierDTO.getStartDate() != null && tierDTO.getStartDate().isAfter(ZonedDateTime.now())) {
            scheduleReflectTierPriceService.schedule(eventId, tier.getIdtier().longValue(), tierDTO.getStartDate());
        } else {
            sendEvaluationMessage(eventId, tier.getIdzona().longValue());
        }
        return new CommonIdResponse(tier.getIdtier());
    }

    @MySQLRead
    public TiersDTO getEventTiers(Long eventId, TiersFilter tiersFilter) {
        validateEvent(eventId);
        TiersDTO result = new TiersDTO();
        Integer venueTemplateId = ConverterUtils.longToInt(tiersFilter.getVenueTemplateId());
        result.setMetadata(MetadataBuilder.build(tiersFilter, tierDao.countByEventId(eventId.intValue(), venueTemplateId)));

        List<TierDTO> tiers = TierConverter.convert(tierDao.findByEventId(eventId.intValue(), venueTemplateId,
                ConverterUtils.longToInt(tiersFilter.getLimit()), ConverterUtils.longToInt(tiersFilter.getOffset())));

        List<EvaluableTierWrapper> wrappedTiers = TierConverter.fromDTOs(tiers);
        fillTierStock(wrappedTiers);
        // Get active event tiers will also set the active property as collateral!
        List<EvaluableTierWrapper> activeEventTiers = TierEvaluator.getActiveEventTiers(wrappedTiers);

        if (CommonUtils.isTrue(tiersFilter.getActive())) {
            tiers = TierConverter.toDTOs(activeEventTiers);
        }
        result.setData(tiers);
        return result;
    }

    @MySQLRead
    public TierExtendedDTO getEventTier(Long eventId, Long tierId) {
        validateEvent(eventId);
        validateTierId(tierId);
        validateTierBelongsToEvent(tierId, eventId);
        TierRecord tier = tierDao.getTier(tierId.intValue());
        if (tier == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NOT_FOUND);
        }
        TierExtendedDTO result = TierConverter.convertExtended(tier);
        result.setActive(evaluateTier(tier));
        return result;
    }

    @MySQLWrite
    public TierDTO updateEventTier(Long eventId, Long tierId, TierUpdateRequestDTO tierDTO) {
        validatePresentTier(tierDTO);
        validateTierId(tierId);
        CpanelTierRecord tier = findTier(tierId);
        CpanelEventoRecord event = validateEvent(eventId);
        validateTierBelongsToEvent(tierId, eventId);
        if (tierDTO.getName() != null && !tier.getNombre().equals(tierDTO.getName())) {
            validateTierNameContent(tierDTO.getName(), tier.getIdzona().longValue());
        }
        if (tierDTO.getPrice() != null) {
            validateTierPriceContent(tierDTO.getPrice());
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (tierDTO.getStartDate() != null && !tier.getFechaInicio().toInstant().equals(tierDTO.getStartDate().toInstant())) {
            validateTierStartDateContent(tierDTO.getStartDate(), tier.getIdzona().longValue(), event.getFechafin());
        }
        if (tierDTO.getLimit() != null) {
            validateTierLimit(tierDTO.getLimit());
            if (tierLimitCouchDao.exists(tierId)) {
                if (tier.getLimite() == null) {
                    LOGGER.error("Found tier limit document but limit not present in mysql: {}", tierId);
                    tierLimitCouchDao.updateCounter(tierId, tierDTO.getLimit().longValue() + 1);
                } else {
                    Long soldTicketsLimit = tierLimitCouchDao.get(tierId);
                    long counterValue = tierDTO.getLimit() - (tier.getLimite() - soldTicketsLimit);
                    counterValue = Math.max(counterValue, 0L);
                    tierLimitCouchDao.updateCounter(tierId, counterValue);
                }
            } else {
                // Tier limit counter is always actual availability + 1
                tierLimitCouchDao.insert(tierId, tierDTO.getLimit().longValue() + 1);
            }
        }
        if (tierDTO.getCondition() != null) {
            validateCondition(tier.getLimite(), tierDTO.getCondition(), tierDTO.getLimit());
            if (tierDTO.getCondition().equals(TierCondition.DATE)) {
                tier.setLimite(null);
            }
        }
        TierConverter.updateRecord(tier, tierDTO);
        tier = tierDao.update(tier);

        if (tierDTO.getStartDate() != null) {
            if (tierDTO.getStartDate().isAfter(now)) {
                scheduleReflectTierPriceService.updateSchedule(eventId, tierId, tierDTO.getStartDate());
            } else {
                scheduleReflectTierPriceService.unschedule(tierId);
            }
        }
        sendEvaluationMessage(eventId, tier.getIdzona().longValue());
        return TierConverter.convert(tier, timeZoneDao.findById(tier.getTimezone()));
    }

    private void sendEvaluationMessage(Long eventId, Long priceTypeId) {
        TierModificationMessage message = new TierModificationMessage();
        message.setAction(TierModificationMessage.Action.EVALUATE_TIERS);
        message.setEventId(eventId);
        message.setPriceTypeId(priceTypeId);
        try {
            tierModificationProducer.sendMessage(message);
        } catch (Exception e) {
            LOGGER.error("Error while sending message for tier evaluation price type id: {}", priceTypeId, e);
        }
    }

    @MySQLWrite
    public void deleteEventTier(Long eventId, Long tierId) {
        validateEvent(eventId);
        validateTierId(tierId);
        CpanelTierRecord tier = findTier(tierId);
        Integer tierPriceTypeId = tier.getIdzona();
        validateTierBelongsToEvent(tierId, eventId);

        List<TierSaleGroupRecord> tierSaleGroupRelations = tierSaleGroupDao.getByTierIds(Collections.singleton(tierId));
        for (TierSaleGroupRecord relation : tierSaleGroupRelations) {
            String saleGroupId = relation.getIdcupo().toString();
            String tierIdString = tierId.toString();
            if (tierSaleGroupCouchDao.exists(tierIdString, saleGroupId)) {
                tierSaleGroupCouchDao.remove(tierIdString, saleGroupId);
            }
        }
        tierSaleGroupDao.deleteByTierId(tierId.intValue());
        tierDao.delete(tierId.intValue());
        scheduleReflectTierPriceService.unschedule(tierId);
        if (tierLimitCouchDao.exists(tierId)) {
            tierLimitCouchDao.remove(tierId);
        }
        if (tierConfigCouchDao.exists(tierId)) {
            tierConfigCouchDao.remove(tierId);
        }
        sendEvaluationMessage(eventId, tierPriceTypeId.longValue());
    }

    @MySQLWrite
    public void deleteEventTierLimit(Long eventId, Long tierId) {
        validateEvent(eventId);
        validateTierId(tierId);
        CpanelTierRecord tier = findTier(tierId);
        validateTierBelongsToEvent(tierId, eventId);
        tier.setLimite(null);
        tierDao.update(tier);
        tierLimitCouchDao.remove(tierId);
        sendEvaluationMessage(eventId, tier.getIdzona().longValue());
    }

    @MySQLWrite
    public void createEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId, Integer limit) {
        if (limit == null || limit < 0) {
            throw new OneboxRestException(MsEventTierErrorCode.INVALID_SALE_GROUP_LIMIT);
        }
        validateEvent(eventId);
        validateTierId(tierId);
        CpanelTierRecord tier = findTier(tierId);
        validateTierBelongsToEvent(tierId, eventId);
        CpanelCuposConfigRecord saleGroup = validateSaleGroup(saleGroupId);
        validateTierSaleGroupNotExists(tierId, saleGroupId);
        validatePriceTypeBelongsToVenueTemplate(saleGroup.getIdconfiguracion(), tier.getIdzona());
        CpanelTierCupoRecord saleGroupTier = new CpanelTierCupoRecord();
        saleGroupTier.setIdcupo(saleGroupId.intValue());
        saleGroupTier.setIdtier(tierId.intValue());
        saleGroupTier.setLimite(limit);
        tierSaleGroupDao.insert(saleGroupTier);
        // Tier sale group limit counter is always actual availability + 1
        tierSaleGroupCouchDao.insert(tierId, saleGroupId, limit + 1);
        sendEvaluationMessage(eventId, tier.getIdzona().longValue());
    }

    @MySQLWrite
    public void updateEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId, Integer limit) {
        if (limit == null || limit < 0) {
            throw new OneboxRestException(MsEventTierErrorCode.INVALID_SALE_GROUP_LIMIT);
        }
        validateEvent(eventId);
        validateTierId(tierId);
        validateTierBelongsToEvent(tierId, eventId);
        CpanelTierCupoRecord saleGroupTier = validateSaleGroupTier(tierId, saleGroupId);
        Long previousAvailability = tierSaleGroupCouchDao.get(String.valueOf(tierId), String.valueOf(saleGroupId));
        int counterValue = limit - (saleGroupTier.getLimite() - previousAvailability.intValue());
        counterValue = Math.max(counterValue, 0);
        saleGroupTier.setLimite(limit);
        tierSaleGroupDao.update(saleGroupTier);
        tierSaleGroupCouchDao.updateCounter(tierId, saleGroupId, counterValue);
        CpanelTierRecord tier = findTier(tierId);
        sendEvaluationMessage(eventId, tier.getIdzona().longValue());
    }

    @MySQLWrite
    public void deleteEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId) {
        validateEvent(eventId);
        validateTierId(tierId);
        validateTierBelongsToEvent(tierId, eventId);
        validateSaleGroupTier(tierId, saleGroupId);
        tierSaleGroupDao.delete(tierId.intValue(), saleGroupId.intValue());
        tierSaleGroupCouchDao.remove(String.valueOf(tierId), String.valueOf(saleGroupId));
        CpanelTierRecord tier = findTier(tierId);
        sendEvaluationMessage(eventId, tier.getIdzona().longValue());
    }

    @MySQLWrite
    public void deleteAllTierSaleGroup(Long eventId, IdDTO salesGroupWrapper) {
        if (salesGroupWrapper == null) {
            throw new OneboxRestException(MsEventTierErrorCode.SALEGROUP_ID_MANDATORY);
        }
        validateEvent(eventId);
        Long salesGroupId = salesGroupWrapper.getId();
        validateSaleGroup(salesGroupId);
        List<CpanelTierCupoRecord> tierSaleGroups = tierSaleGroupDao.getBySaleGroupId(salesGroupId.intValue());
        tierSaleGroups.forEach(relation -> {
            String tierId = relation.getIdtier().toString();
            String saleGroupIdStr = relation.getIdcupo().toString();
            if (tierSaleGroupCouchDao.exists(tierId, saleGroupIdStr)) {
                tierSaleGroupCouchDao.remove(tierId, saleGroupIdStr);
            }
        });
        tierSaleGroupDao.deleteBySaleGroupId(salesGroupId.intValue());

        Set<Integer> tierIds = tierSaleGroups.stream()
                .map(CpanelTierCupoRecord::getIdtier)
                .collect(Collectors.toSet());

        tierDao.getByIds(tierIds).stream()
                .map(CpanelTierRecord::getIdzona)
                .distinct()
                .forEach(zoneId -> sendEvaluationMessage(eventId, zoneId.longValue()));
    }

    @MySQLWrite
    public void deleteTiersForPriceType(Long eventId, IdDTO priceTypeWrapper) {
        if (priceTypeWrapper == null || priceTypeWrapper.getId() == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_PRICE_MANDATORY);
        }
        Long priceTypeId = priceTypeWrapper.getId();
        validateEvent(eventId);
        List<CpanelTierRecord> tiers = tierDao.getByPriceType(priceTypeId.intValue());
        List<Long> tierIds = tiers.stream()
                .map(CpanelTierRecord::getIdtier)
                .map(Integer::longValue)
                .collect(Collectors.toList());

        tierSaleGroupDao.getByTierIds(tierIds).forEach(relation -> {
            String tierId = relation.getIdtier().toString();
            String saleGroupId = relation.getIdcupo().toString();
            if (tierSaleGroupCouchDao.exists(tierId, saleGroupId)) {
                tierSaleGroupCouchDao.remove(tierId, saleGroupId);
            }
        });

        tierIds.forEach(tierId -> {
            tierSaleGroupDao.deleteByTierId(tierId.intValue());
            scheduleReflectTierPriceService.unschedule(tierId);
            if (tierLimitCouchDao.exists(tierId)) {
                tierLimitCouchDao.remove(tierId);
            }
        });

        tiers.forEach(t -> tierSaleGroupDao.deleteByTierId(t.getIdtier()));
        tierDao.bulkDelete(tiers);

        sendEvaluationMessage(eventId, priceTypeId);
    }

    @MySQLRead
    public long decrementEventTierLimit(Long eventId, Long tierId) {
        Long result = tierLimitCouchDao.get(tierId);
        if (result <= 1L) {
            LOGGER.info("Counter is 1 or less, evaluating and executing tier: {}", tierId);
            sendEvaluationMessage(eventId, tierDao.findTierZoneId(tierId.intValue()));
            throw new OneboxRestException(MsEventTierErrorCode.TIER_LIMIT_REACHED);
        }
        result = tierLimitCouchDao.decrement(tierId);
        if (result == 1L) {
            LOGGER.info("Counter is 1, evaluating and executing tier: {}", tierId);
            sendEvaluationMessage(eventId,  tierDao.findTierZoneId(tierId.intValue()));
        }
        if (result == 0L) {
            LOGGER.info("Counter is 0, evaluating and executing tier: {}", tierId);
            sendEvaluationMessage(eventId,  tierDao.findTierZoneId(tierId.intValue()));
            throw new OneboxRestException(MsEventTierErrorCode.TIER_LIMIT_REACHED);
        }
        return result;
    }

    @MySQLRead
    public long incrementEventTierLimit(Long eventId, Long tierId) {
        Long currentCounter = tierLimitCouchDao.get(tierId);
        Long incrementedCounter = tierLimitCouchDao.increment(tierId);
        if (currentCounter <= 1L) {
            sendEvaluationMessage(eventId, tierDao.findTierZoneId(tierId.intValue()));
        }
        return incrementedCounter;
    }

    public long decrementEventTierSaleGroupLimit(Long eventId, Long tierId, Long saleGroupId) {
        Long result = tierSaleGroupCouchDao.get(tierId, saleGroupId);
        if (result <= 1L) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_SALE_GROUP_LIMIT_REACHED);
        }
        result = tierSaleGroupCouchDao.decrement(tierId, saleGroupId);
        if (result == 0L) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_SALE_GROUP_LIMIT_REACHED);
        }
        return result;
    }

    public long incrementEventTierSaleGroupLimit(Long eventId, Long tierId, Long saleGroupId) {
        return tierSaleGroupCouchDao.increment(tierId, saleGroupId);
    }


    public void evaluateAndExecuteTier(long eventId, int priceTypeId) {
        List<EvaluableTierWrapper> zoneTiers = TierConverter.fromRecords(tierDao.findByPriceType(priceTypeId));
        fillTierStock(zoneTiers);
        EvaluableTierWrapper wrapper = TierEvaluator.getActivePriceTypeTier(zoneTiers);
        if (wrapper != null) {
            CpanelTierRecord activeTier = wrapper.getTierRecord();
            applyTierChanges(eventId, activeTier.getIdtier(), activeTier.getIdzona(), activeTier.getPrecio());
        }
    }

    @MySQLWrite
    public void applyTierChanges(long eventId, int tierId, int priceTypeId, double price) {
        LOGGER.info("Active tier is: {}, updating price type data!!", tierId);
        priceZoneAssignmentDao.updatePrices(priceTypeId, price);
        CpanelConfigRecintoRecord venueTemplate = venueTemplateDao.findByPriceTypeId(priceTypeId);
        venuesRepository.deletePriceTypeCommElements(venueTemplate.getIdconfiguracion().longValue(), (long) priceTypeId);
        TierConfig tc = tierConfigCouchDao.getOrInitTierConfig((long) tierId);
        if (tc == null || tc.getTierTranslation() == null) {
            return;
        }
        List<PriceTypeCommunicationElement> commElements = new ArrayList<>();
        Map<String, String> names = tc.getTierTranslation().getName();
        Map<String, String> descriptions = tc.getTierTranslation().getDescription();
        if (names != null && !names.isEmpty()) {
            commElements.addAll(names.entrySet().stream()
                    .map(e -> new PriceTypeCommunicationElement(e.getKey(), e.getValue(), CommunicationElementType.NAME))
                    .collect(Collectors.toList()));
        }
        if (descriptions != null && !descriptions.isEmpty()) {
            commElements.addAll(descriptions.entrySet().stream()
                    .map(e -> new PriceTypeCommunicationElement(e.getKey(), e.getValue(), CommunicationElementType.DESCRIPTION))
                    .collect(Collectors.toList()));
        }
        if (!commElements.isEmpty()) {
            venuesRepository.upsertPriceTypeCommElements(venueTemplate.getIdconfiguracion().longValue(),
                    (long) priceTypeId, commElements);
        }
        refreshDataService.refreshEvent(eventId, "applyTierChanges");
    }

    public List<TierPriceTypeAvailabilityDTO> getTierSaleGroupsAvailabilities(Long eventId) {
        validateEvent(eventId);

        List<EvaluableTierWrapper> tiers = TierConverter.fromRecords(tierDao.getByEventId(eventId.intValue()));
        fillTierStock(tiers);
        Map<Long, TierDTO> activeTiers = TierEvaluator.getActiveEventTiers(tiers).stream()
                .map(EvaluableTierWrapper::getTierDTO)
                .collect(Collectors.toMap(TierDTO::getPriceTypeId, Function.identity()));

        List<TierSaleGroupRecord> relations = tierSaleGroupDao.getByTierIds(activeTiers.values().stream()
                .map(TierDTO::getId).collect(Collectors.toList()));
        Map<Integer, List<TierSaleGroupRecord>> relationsByPriceType = relations.stream()
                .collect(Collectors.groupingBy(TierSaleGroupRecord::getPriceTypeId));

        List<TierPriceTypeAvailabilityDTO> tptAvailabilities = new ArrayList<>();

        for (Map.Entry<Long, TierDTO> priceTypeToTier : activeTiers.entrySet()) {

            Long priceTypeId = priceTypeToTier.getKey();
            Long tierId = priceTypeToTier.getValue().getId();

            TierPriceTypeAvailabilityDTO tptAvailability = new TierPriceTypeAvailabilityDTO(priceTypeId);
            if (tierLimitCouchDao.exists(tierId)) {
                // Tier limit counter is always actual availability + 1
                tptAvailability.setPriceTypeLimit(tierLimitCouchDao.get(tierId).intValue() - 1);
            }

            relationsByPriceType.computeIfAbsent(priceTypeId.intValue(), ArrayList::new);

            for (TierSaleGroupRecord saleGroup : relationsByPriceType.get(priceTypeId.intValue())) {
                // Tier sale group limit counter is always actual availability + 1
                Integer availability = tierSaleGroupCouchDao.get(
                        String.valueOf(saleGroup.getIdtier()),
                        String.valueOf(saleGroup.getIdcupo())).intValue() - 1;
                tptAvailability.getSaleGroupsAvailabilities().put(saleGroup.getIdcupo().longValue(), availability);
            }

            tptAvailabilities.add(tptAvailability);

        }
        return tptAvailabilities;
    }

    public void updateCommElements(Long eventId, Long tierId,
                                   TierCommunicationElementDTO[] communicationElements) {
        checkTierCommElements(communicationElements);
        validateTierId(tierId);
        validateEvent(eventId);
        validateTierBelongsToEvent(tierId, eventId);
        TierConfig tc = tierConfigCouchDao.getOrInitTierConfig(tierId);
        TierTranslationConverter.fromDTO(tc, communicationElements);
        tierConfigCouchDao.upsert(tierId.toString(), tc);
        CpanelTierRecord tier = findTier(tierId);
        sendEvaluationMessage(eventId, tier.getIdzona().longValue());
    }

    public List<TierCommunicationElementDTO> getCommElements(Long eventId, Long tierId, TierCommElemFilterDTO filter) {
        if (filter != null && filter.getLanguage() != null) {
            validateLanguage(filter.getLanguage());
        }
        validateTierId(tierId);
        validateEvent(eventId);
        validateTierBelongsToEvent(tierId, eventId);
        TierConfig tc = tierConfigCouchDao.getOrInitTierConfig(tierId);
        return filteredCommElems(filter, TierTranslationConverter.toDTO(tc.getTierTranslation()));
    }

    private List<TierCommunicationElementDTO> filteredCommElems(TierCommElemFilterDTO filter,
                                                                List<TierCommunicationElementDTO> translations) {
        if (filter == null) {
            return translations;
        }
        return translations.stream()
                .filter(elem -> isNull(filter.getLanguage()) || filter.getLanguage().equals(elem.getLang()))
                .filter(elem -> isNull(filter.getType()) || filter.getType().equals(elem.getCommunicationElementType()))
                .collect(Collectors.toList());
    }


    private void checkTierCommElements(TierCommunicationElementDTO[] communicationElements) {
        if (communicationElements == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_TRANSLATION_MANDATORY);
        }
        for (TierCommunicationElementDTO elem : communicationElements) {
            if (elem.getValue() == null || elem.getValue().isEmpty() || elem.getValue().length() > COMM_ELEM_MAX_VALUE_LENGTH) {
                throw new OneboxRestException(MsEventTierErrorCode.INVALID_COMM_ELEM_VALUE);
            }
            validateLanguage(elem.getLang());
        }
    }

    private void validateLanguage(String lang) {
        try {
            // Converts languageTag IETF BCP47 to locale (es-ES to es_ES)
            Locale.forLanguageTag(lang);
        } catch (IllegalArgumentException e) {
            throw new OneboxRestException(MsEventTierErrorCode.INVALID_COMM_ELEM_LANG);
        }
    }


    private void validateCondition(Integer recordLimit, TierCondition newCondition, Integer newLimit) {
        if (newCondition.equals(TierCondition.STOCK_OR_DATE) && recordLimit == null && newLimit == null) {
            throw new OneboxRestException(MsEventTierErrorCode.STOCK_CONDITION_WITHOUT_LIMIT);
        }
    }

    private void validateTierLimit(Integer limit) {
        if (limit <= 0L) {
            throw new OneboxRestException(MsEventTierErrorCode.LIMIT_BELOW_ZERO);
        }
    }

    private void validateTierSaleGroupNotExists(Long tierId, Long saleGroupId) {
        CpanelTierCupoRecord result = tierSaleGroupDao.getByTierAndSaleGroup(tierId.intValue(), saleGroupId.intValue());
        if (result != null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_SALE_GROUP_ALREADY_EXISTS);
        }
    }

    private CpanelTierCupoRecord validateSaleGroupTier(Long tier, Long saleGroupId) {
        CpanelTierCupoRecord result = tierSaleGroupDao.getByTierAndSaleGroup(tier.intValue(), saleGroupId.intValue());
        if (result == null) {
            throw new OneboxRestException(MsEventTierErrorCode.SALE_GROUP_TIER_NOT_FOUND);
        }
        return result;
    }

    private void validatePriceTypeBelongsToVenueTemplate(Integer venueTemplateId, Integer priceTypeId) {
        CpanelZonaPreciosConfigRecord priceType = priceZoneConfigDao.getById(priceTypeId);
        if (!priceType.getIdconfiguracion().equals(venueTemplateId)) {
            throw new OneboxRestException(MsEventTierErrorCode.SALEGROUP_TEMPLATE_NOT_MATCHING_TIER_TEMPLATE);
        }

    }

    private CpanelCuposConfigRecord validateSaleGroup(Long saleGroupId) {
        if (saleGroupId == null || saleGroupId <= 0) {
            throw new OneboxRestException(MsEventTierErrorCode.SALEGROUP_ID_MANDATORY);
        }
        CpanelCuposConfigRecord saleGroup = saleGroupDao.findById(saleGroupId.intValue());
        if (saleGroup == null) {
            throw new OneboxRestException(MsEventTierErrorCode.SALEGROUP_NOT_FOUND);
        }
        return saleGroup;
    }


    private boolean evaluateTier(CpanelTierRecord tier) {
        List<EvaluableTierWrapper> zoneTiers = TierConverter.fromRecords(tierDao.findByPriceType(tier.getIdzona()));
        fillTierStock(zoneTiers);
        EvaluableTierWrapper activeTier = TierEvaluator.getActivePriceTypeTier(zoneTiers);
        return activeTier != null && tier.getIdtier().equals(activeTier.getTierRecord().getIdtier());
    }

    private void validatePresentTier(Object tierDTO) {
        if (tierDTO == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_DATA_MANDATORY);
        }
    }

    private void validateTierId(Long tierId) {
        if (tierId == null || tierId < 1L) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_ID_MANDATORY);
        }
    }

    private CpanelTierRecord findTier(Long tierId) {
        CpanelTierRecord r = tierDao.findById(tierId.intValue());
        if (r == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NOT_FOUND);
        }
        return r;
    }

    private CpanelEventoRecord validateEvent(Long eventId) {
        CpanelEventoRecord event;
        try {
            event = eventDao.findById(eventId.intValue());
        } catch (EntityNotFoundException enf) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        if (event.getUsetieredpricing().equals((byte) 0)) {
            throw new OneboxRestException(MsEventTierErrorCode.EVENT_CANNOT_USE_TIER);
        }
        return event;
    }

    private void validateTierName(String tierName, Long priceTypeId) {
        if (tierName == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NAME_MANDATORY);
        } else {
            validateTierNameContent(tierName, priceTypeId);
        }
    }

    private void validateTierNameContent(String tierName, Long priceTypeId) {
        if (tierName.isEmpty()) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NAME_MANDATORY);
        }
        if (tierName.length() > TIER_NAME_MAX_LENGTH) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NAME_MAX_LENGTH);
        }
        if (tierDao.countByZoneAndName(priceTypeId.intValue(), tierName) > 0) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NAME_UNIQUE);
        }
    }

    private void validateTierPrice(Double price) {
        if (price == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_PRICE_MANDATORY);
        } else {
            validateTierPriceContent(price);
        }
    }

    private void validateTierPriceContent(Double price) {
        if (price == null || price < 0) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_PRICE_POSITIVE);
        }
    }

    private void validateTierStartDate(ZonedDateTime tierStartDate, Long tierPriceTypeId, Timestamp eventEndDate) {
        if (tierStartDate == null) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_START_DATE_MANDATORY);
        } else {
            validateTierStartDateContent(tierStartDate, tierPriceTypeId, eventEndDate);
        }
    }

    private void validateTierStartDateContent(ZonedDateTime tierStartDate, Long tierPriceTypeId, Timestamp eventEndDate) {
        if (eventEndDate != null && tierStartDate.isAfter(CommonUtils.timestampToZonedDateTime(eventEndDate))) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_START_DATE_AFTER_EVENT);
        } else {
            if (tierDao.findByZoneAndStartDate(tierPriceTypeId.intValue(),
                    CommonUtils.zonedDateTimeToTimestamp(tierStartDate)) != null) {
                throw new OneboxRestException(MsEventTierErrorCode.TIER_START_DATE_ALREADY_EXISTS);
            }
        }
    }

    private void validateTierBelongsToEvent(Long tierId, Long eventId) {
        List<TierRecord> eventTiers = tierDao.findByEventId(eventId.intValue(), null, null, null);
        if (eventTiers.stream().noneMatch(t -> t.getIdtier() == tierId.intValue())) {
            throw new OneboxRestException(MsEventTierErrorCode.TIER_NOT_FOUND);
        }
    }

    private void fillTierStock(List<EvaluableTierWrapper> tiers) {
        tiers.stream()
                .filter(tier -> TierCondition.STOCK_OR_DATE.equals(tier.getCondition()))
                .filter(tier -> tierLimitCouchDao.exists(tier.getId().longValue()))
                // Tier limit is always actual availability +1
                .forEach(tier -> tier.setStock(tierLimitCouchDao.get(tier.getId().longValue()) - 1));
    }

    public void incrementTierLimitForEvent(Long tierId, Long eventId, Long saleGroupId) {
        Map<Long, Set<Long>> tierSalesGroup = new HashMap<>();
        TierExtendedDTO tierExtendedDTO = getEventTier(eventId, tierId);
        tierSalesGroup.put(tierId, new HashSet<>());
        var salesGroupLimit = tierExtendedDTO.getSalesGroupLimit();
        addTierSalesGroupLimit(tierId, salesGroupLimit, tierSalesGroup);
        incrementEventTier(tierExtendedDTO, tierId, eventId, saleGroupId, tierSalesGroup);

    }

    private void incrementEventTier(TierExtendedDTO tierExtended, Long tierId, Long eventId,
        Long saleGroupId,
        Map<Long, Set<Long>> tierSalesGroup) {
        incrementEventTierLimitIfLimitIsNotNull(tierId, eventId, tierExtended);
        incrementEventTierSaleGroupIfContainsSaleGroupId(tierId, eventId, saleGroupId,
            tierSalesGroup);
    }

    private void incrementEventTierSaleGroupIfContainsSaleGroupId(Long tierId, Long eventId,
        Long saleGroupId,
        Map<Long, Set<Long>> tierSalesGroup) {
        if (tierSalesGroup.get(tierId).contains(saleGroupId)) {
            incrementEventTierSaleGroupLimit(eventId, tierId, saleGroupId);
        }
    }

    private void incrementEventTierLimitIfLimitIsNotNull(Long tierId, Long eventId,
        TierExtendedDTO tierExtended) {
        if (tierExtended.getLimit() != null) {
            incrementEventTierLimit(eventId, tierId);
        }
    }


    private static void addTierSalesGroupLimit(Long tierId,
        List<TierSalesGroupLimitDTO> salesGroupLimit,
        Map<Long, Set<Long>> tierSalesGroup) {
        if (salesGroupLimit != null) {
            tierSalesGroup.get(tierId)
                .addAll(salesGroupLimit.stream()
                    .map(TierSalesGroupLimitDTO::getId)
                    .collect(Collectors.toSet()));
        }
    }
}

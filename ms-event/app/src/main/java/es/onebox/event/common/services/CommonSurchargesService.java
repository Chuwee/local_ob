package es.onebox.event.common.services;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.dao.EventInvSurchargeRangeDao;
import es.onebox.event.events.dao.EventPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.EventSurchargeRangeDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.event.common.converters.CommonRangeConverter;
import es.onebox.event.surcharges.dao.RangeSurchargeEntityDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventChangeSeatDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventInvitationDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventPromotionDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventSecondaryMarketDao;
import es.onebox.event.surcharges.dto.RangeDTO;
import es.onebox.event.surcharges.dto.SurchargeLimitDTO;
import es.onebox.event.surcharges.dto.SurchargeListDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import es.onebox.event.surcharges.manager.SurchargeManager;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoInvRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoPromocionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static es.onebox.event.common.utils.RangesUtils.defaultRanges;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.CHANGE_SEAT;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.GENERIC;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.INVITATION;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.PROMOTION;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.SECONDARY_MARKET_PROMOTER;

@Service
public class CommonSurchargesService {

    protected final SurchargeRangeDao surchargeRangeDao;
    protected final RangeSurchargeEventDao rangeSurchargeEventDao;
    protected final RangeSurchargeEntityDao rangeSurchargeEntityDao;
    protected final RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao;
    protected final RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao;
    protected final EventSurchargeRangeDao eventSurchargeRangeDao;
    protected final EventInvSurchargeRangeDao eventInvSurchargeRangeDao;
    protected final EventPromotionSurchargeRangeDao eventPromotionSurchargeRangeDao;
    protected final RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao;
    protected final RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao;

    @Autowired
    public CommonSurchargesService(RangeSurchargeEventDao rangeSurchargeEventDao,
                                   RangeSurchargeEntityDao rangeSurchargeEntityDao,
                                   RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao,
                                   RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao,
                                   SurchargeRangeDao surchargeRangeDao, EventSurchargeRangeDao eventSurchargeRangeDao,
                                   EventInvSurchargeRangeDao eventInvSurchargeRangeDao,
                                   EventPromotionSurchargeRangeDao eventPromotionSurchargeRangeDao,
                                   RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao,
                                   RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao) {
        this.rangeSurchargeEventDao = rangeSurchargeEventDao;
        this.rangeSurchargeEntityDao = rangeSurchargeEntityDao;
        this.rangeSurchargeEventInvitationDao = rangeSurchargeEventInvitationDao;
        this.rangeSurchargeEventPromotionDao = rangeSurchargeEventPromotionDao;
        this.surchargeRangeDao = surchargeRangeDao;
        this.eventSurchargeRangeDao = eventSurchargeRangeDao;
        this.eventInvSurchargeRangeDao = eventInvSurchargeRangeDao;
        this.eventPromotionSurchargeRangeDao = eventPromotionSurchargeRangeDao;
        this.rangeSurchargeEventSecMktDao = rangeSurchargeEventSecMktDao;
        this.rangeSurchargeEventChangeSeatDao = rangeSurchargeEventChangeSeatDao;
    }

    public void getSurcharges(CpanelEventoRecord eventoRecord, SurchargeTypeDTO type, List<SurchargesDTO> surcharges) {
        switch (type) {
            case GENERIC -> {
                List<CpanelRangoRecord> genericRangeRecords =
                        rangeSurchargeEventDao.getByEventId(eventoRecord.getIdevento());
                if (CollectionUtils.isEmpty(genericRangeRecords)) {
                    if (eventoRecord.getIdcurrency() != null) {
                        List<CpanelRangoRecord> entityRangeRecords = rangeSurchargeEntityDao.getByEntityIdAndCurrencyId(eventoRecord.getIdentidad(), eventoRecord.getIdcurrency());
                        genericRangeRecords = CollectionUtils.isNotEmpty(entityRangeRecords)
                                ? entityRangeRecords
                                : Arrays.asList(defaultRanges(eventoRecord.getIdcurrency()));
                    } else { // TODO delete after all event entities are migrated to multicurrency usage
                        List<CpanelRangoRecord> entityRangeRecords = rangeSurchargeEntityDao.getByEntityId(eventoRecord.getIdentidad());
                        genericRangeRecords = CollectionUtils.isNotEmpty(entityRangeRecords)
                                ? entityRangeRecords
                                : Arrays.asList(defaultRanges(null));
                    }
                }
                SurchargesDTO surchargesDTO = getSurchargeByType(genericRangeRecords, GENERIC);
                surchargesDTO.setLimit(getSurchargeLimit(eventoRecord.getRecomendarrecargoscanal(), eventoRecord.getRecargominimo(), eventoRecord.getRecargomaximo()));
                surcharges.add(surchargesDTO);
            }
            case PROMOTION -> {
                List<CpanelRangoRecord> promotionRangeRecords =
                        rangeSurchargeEventPromotionDao.getByEventId(eventoRecord.getIdevento());
                if (CollectionUtils.isEmpty(promotionRangeRecords)) {
                    promotionRangeRecords = Arrays.asList(defaultRanges(eventoRecord.getIdcurrency()));
                }
                SurchargesDTO surchargesDTO = getSurchargeByType(promotionRangeRecords, PROMOTION);
                surchargesDTO.setLimit(getSurchargeLimit(eventoRecord.getRecomendarrecargospromocioncanal(), eventoRecord.getRecargopromocionminimo(), eventoRecord.getRecargopromocionmaximo()));
                surchargesDTO.setAllowChannelUseAlternativeCharges(eventoRecord.getAllowchannelusealternativecharges());
                surcharges.add(surchargesDTO);
            }
            case INVITATION -> {
                List<CpanelRangoRecord> invitationRangeRecords =
                        rangeSurchargeEventInvitationDao.getByEventId(eventoRecord.getIdevento());
                if (CollectionUtils.isEmpty(invitationRangeRecords)) {
                    invitationRangeRecords = Arrays.asList(defaultRanges(eventoRecord.getIdcurrency()));
                    ;
                }
                SurchargesDTO surchargesDTO = getSurchargeByType(invitationRangeRecords, INVITATION);
                surchargesDTO.setLimit(getSurchargeLimit(eventoRecord.getRecomendarrecargosinvcanal(), eventoRecord.getRecargoinvminimo(), eventoRecord.getRecargoinvmaximo()));
                surcharges.add(surchargesDTO);

            }
            case SECONDARY_MARKET_PROMOTER -> {
                List<CpanelRangoRecord> secMktRangeRecords =
                        rangeSurchargeEventSecMktDao.getByEventId(eventoRecord.getIdevento());
                if (CollectionUtils.isEmpty(secMktRangeRecords)) {
                    if (eventoRecord.getIdcurrency() != null) {
                        List<CpanelRangoRecord> entityRangeRecords = rangeSurchargeEntityDao.getByEntityIdAndCurrencyId(eventoRecord.getIdentidad(), eventoRecord.getIdcurrency());
                        secMktRangeRecords = CollectionUtils.isNotEmpty(entityRangeRecords)
                                ? entityRangeRecords
                                : Arrays.asList(defaultRanges(eventoRecord.getIdcurrency()));
                    } else { // TODO delete after all event entities are migrated to multicurrency usage
                        List<CpanelRangoRecord> entityRangeRecords = rangeSurchargeEntityDao.getByEntityId(eventoRecord.getIdentidad());
                        secMktRangeRecords = CollectionUtils.isNotEmpty(entityRangeRecords)
                                ? entityRangeRecords
                                : Arrays.asList(defaultRanges(null));
                    }
                }
                SurchargesDTO surchargesDTO = getSurchargeByType(secMktRangeRecords, SECONDARY_MARKET_PROMOTER);
                surchargesDTO.setLimit(getSurchargeLimit(eventoRecord.getRecomendarrecargoscanal(), eventoRecord.getRecargominimo(), eventoRecord.getRecargomaximo()));
                surcharges.add(surchargesDTO);
            }
            case SECONDARY_MARKET_CHANNEL -> {
                // secondary market channel charges there arent ready

            }
            case CHANGE_SEAT -> {
                // only when the event is not season ticket and allow relocations
                surcharges.add(getChangeSeatSurcharges(eventoRecord));
            }
            default -> throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
        }
    }

    private SurchargeLimitDTO getSurchargeLimit(Byte limit, Double min, Double max) {
        SurchargeLimitDTO surchargeLimitDTO = new SurchargeLimitDTO();
        surchargeLimitDTO.setEnabled(CommonUtils.isTrue(limit));
        if (surchargeLimitDTO.getEnabled()) {
            surchargeLimitDTO.setMin(min);
            surchargeLimitDTO.setMax(max);
        }
        return surchargeLimitDTO;
    }

    public void validateSetSurcharge(CpanelEventoRecord eventRecord, SurchargeManager surchargeManager, SurchargeTypeDTO type) {
        if (eventRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }

        if (surchargeManager.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.AT_LEAST_ONE_RANGE);
        }

        if (surchargeManager.getRanges().stream().anyMatch(range -> Objects.isNull(range.getFrom()))) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_FROM_RANGE_MANDATORY);
        }

        if (surchargeManager.getRanges().stream().anyMatch(range -> Objects.isNull(range.getFixed()) && Objects.isNull(range.getPercentage()))) {
            throw new OneboxRestException(MsEventErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if (surchargeManager.isInitialRangeDuplicated()) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_DUPLICATED_FROM_RANGE);
        }

        if (surchargeManager.getRanges().stream().noneMatch(range -> range.getFrom() == 0)) {
            throw new OneboxRestException(MsEventErrorCode.FROM_RANGE_ZERO_MANDATORY);
        }

        if (isMinGreaterThanMax(surchargeManager)) {
            throw new OneboxRestException(MsEventErrorCode.MIN_SURCHARGE_GREATER_THAN_MAX);
        }

        if (SECONDARY_MARKET_PROMOTER.equals(type)) {
            if (surchargeManager.getLimit() != null && (CommonUtils.isTrue(surchargeManager.getLimit().getEnabled())
                    || surchargeManager.getLimit().getMin() != null
                    || surchargeManager.getLimit().getMax() != null)) {
                throw new OneboxRestException(MsEventErrorCode.SECONDARY_MARKET_CANT_HAVE_LIMIT);
            }
        } else if (surchargeManager.getLimit() != null && surchargeManager.getLimit().getEnabled()) {
            if (surchargeManager.getLimit().getMin() == null || surchargeManager.getLimit().getMax() == null) {
                throw new OneboxRestException(MsEventErrorCode.WHEN_LIMIT_ENABLED_IS_MANDATORY);
            } else if (surchargeManager.getLimit().getMin().compareTo(surchargeManager.getLimit().getMax()) >= 0) {
                throw new OneboxRestException(MsEventErrorCode.WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX);
            }
        }

        if (surchargeManager.isAllowChannelUseAlternativeCharges() == null) {
            surchargeManager.setAllowChannelUseAlternativeCharges(false);
        }
    }

    public boolean hasTypesDuplicated(SurchargeListDTO surchargeListDTO) {
        return surchargeListDTO.stream()
                .anyMatch(surchargeDTO ->
                        surchargeListDTO.stream()
                                .filter(singleSurcharge -> singleSurcharge.getType().equals(surchargeDTO.getType()))
                                .count() > 1);
    }

    private boolean isMinGreaterThanMax(SurchargeManager surchargeManager) {
        return surchargeManager.getRanges().stream()
                .filter(range -> range.getMin() != null && range.getMax() != null)
                .anyMatch(range -> range.getMin() > range.getMax());
    }

    protected SurchargesDTO getSurchargeByType(List<CpanelRangoRecord> rangeRecords, SurchargeTypeDTO type) {
        SurchargesDTO surcharge = new SurchargesDTO();
        List<RangeDTO> ranges = CommonRangeConverter.fromRecords(rangeRecords);
        surcharge.setRanges(ranges);
        surcharge.setType(type);
        return surcharge;
    }

    public void initEventSurcharges(CpanelEventoRecord eventoRecord) {
        initTicketSurcharges(eventoRecord);
        initInvitationSurcharges(eventoRecord);
        initPromotionSurcharges(eventoRecord);
    }

    private void initPromotionSurcharges(CpanelEventoRecord eventoRecord) {
        CpanelRangoRecord newEventPromotionRange = insertRangeRecord(eventoRecord.getIdcurrency());
        CpanelRangoRecargoEventoPromocionRecord cpanelRangoRecargoEventoPromocionRecord = new CpanelRangoRecargoEventoPromocionRecord();
        cpanelRangoRecargoEventoPromocionRecord.setIdevento(eventoRecord.getIdevento());
        cpanelRangoRecargoEventoPromocionRecord.setIdrango(newEventPromotionRange.getIdrango());
        eventPromotionSurchargeRangeDao.insert(cpanelRangoRecargoEventoPromocionRecord);
    }

    private void initInvitationSurcharges(CpanelEventoRecord eventoRecord) {
        CpanelRangoRecord newEventInvRange = insertRangeRecord(eventoRecord.getIdcurrency());
        CpanelRangoRecargoEventoInvRecord cpanelRangoRecargoEventoInvRecord = new CpanelRangoRecargoEventoInvRecord();
        cpanelRangoRecargoEventoInvRecord.setIdevento(eventoRecord.getIdevento());
        cpanelRangoRecargoEventoInvRecord.setIdrango(newEventInvRange.getIdrango());
        eventInvSurchargeRangeDao.insert(cpanelRangoRecargoEventoInvRecord);
    }

    private void initTicketSurcharges(CpanelEventoRecord eventoRecord) {
        // get generic entity ranges
        List<CpanelRangoRecord> entityRanges = rangeSurchargeEntityDao.getByEntityIdAndCurrencyId(eventoRecord.getIdentidad(), eventoRecord.getIdcurrency());
        // TODO delete after all event entities are migrated to multicurrency usage. Old entities may use null currency as default currency
        if (entityRanges.isEmpty()) {
            entityRanges = rangeSurchargeEntityDao.getByEntityId(eventoRecord.getIdentidad());
            entityRanges.removeIf(range -> range.getIdcurrency() != null && !range.getIdcurrency().equals(eventoRecord.getIdcurrency()));
        }
        if (entityRanges.isEmpty()) {
            // create empty range
            CpanelRangoRecord newEventRange = insertRangeRecord(eventoRecord.getIdcurrency());
            CpanelRangoRecargoEventoRecord cpanelRangoRecargoEventoRecord = new CpanelRangoRecargoEventoRecord();
            cpanelRangoRecargoEventoRecord.setIdevento(eventoRecord.getIdevento());
            cpanelRangoRecargoEventoRecord.setIdrango(newEventRange.getIdrango());
            eventSurchargeRangeDao.insert(cpanelRangoRecargoEventoRecord);
        } else {
            entityRanges.forEach(entityRange -> {
                // duplicate range
                entityRange.setIdrango(null);
                Integer rangeId = surchargeRangeDao.insertInto(entityRange);

                // associate it with the event
                CpanelRangoRecargoEventoRecord cpanelRangoRecargoEventoRecord = new CpanelRangoRecargoEventoRecord();
                cpanelRangoRecargoEventoRecord.setIdevento(eventoRecord.getIdevento());
                cpanelRangoRecargoEventoRecord.setIdrango(rangeId);
                eventSurchargeRangeDao.insert(cpanelRangoRecargoEventoRecord);
            });
        }
    }

    protected CpanelRangoRecord insertRangeRecord(Integer currencyId) {
        CpanelRangoRecord eventRange = new CpanelRangoRecord();
        eventRange.setValor(0d);
        eventRange.setRangominimo(0d);
        eventRange.setRangomaximo(0d);
        eventRange.setNombrerango(getRangeName(eventRange));
        eventRange.setIdcurrency(currencyId);
        eventRange.setIdrango(surchargeRangeDao.insertInto(eventRange));

        return eventRange;
    }

    protected String getRangeName(CpanelRangoRecord range) {
        return ((range.getRangominimo() != null) ? range.getRangominimo().toString() : "0") + "-" +
                ((range.getRangomaximo() != null) ? range.getRangomaximo().toString() : "0");
    }

    protected SurchargesDTO getChangeSeatSurcharges(CpanelEventoRecord eventRecord) {
        List<CpanelRangoRecord> changeSeatRangeRecords =
                rangeSurchargeEventChangeSeatDao.getByEventId(eventRecord.getIdevento());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(changeSeatRangeRecords)) {
            changeSeatRangeRecords = Arrays.asList(defaultRanges(eventRecord.getIdcurrency()));
        }
        return getSurchargeByType(changeSeatRangeRecords, CHANGE_SEAT);
    }
}

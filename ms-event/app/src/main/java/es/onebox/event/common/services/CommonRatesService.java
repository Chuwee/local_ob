package es.onebox.event.common.services;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.converter.RateConverter;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dto.CreateEventRateDTO;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.promotions.dao.EventRatePromotionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommonRatesService {

    private static final String DEFAULT_RATE_NAME = "General";
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final DescPorIdiomaDao descPorIdiomaDao;
    private final LanguageDao languageDao;
    private final RateDao rateDao;
    private final VenueTemplateDao venueTemplateDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final SessionRateDao sessionRateDao;
    private final EventRatePromotionDao eventRatePromotionDao;
    private final OrdersRepository ordersRepository;

    @Autowired
    public CommonRatesService(ItemDescSequenceDao itemDescSequenceDao, DescPorIdiomaDao descPorIdiomaDao, LanguageDao languageDao, RateDao rateDao, VenueTemplateDao venueTemplateDao, PriceZoneAssignmentDao priceZoneAssignmentDao, SessionRateDao sessionRateDao, EventRatePromotionDao eventRatePromotionDao, OrdersRepository ordersRepository) {
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.descPorIdiomaDao = descPorIdiomaDao;
        this.languageDao = languageDao;
        this.rateDao = rateDao;
        this.venueTemplateDao = venueTemplateDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.sessionRateDao = sessionRateDao;
        this.eventRatePromotionDao = eventRatePromotionDao;
        this.ordersRepository = ordersRepository;
    }

    public Integer insertRateTranslations(Map<String, String> translations) {

        Map<String, CpanelIdiomaRecord> availableLanguages = getAvailableLanguages(translations);

        if (translations != null && !translations.isEmpty()) {
            CpanelItemDescSequenceRecord cpanelItemDescSequence = new CpanelItemDescSequenceRecord();
            cpanelItemDescSequence.setDescripcion("rate item");
            Integer idItem = itemDescSequenceDao.insert(cpanelItemDescSequence).getIditem();
            translations.forEach((code, value) -> {
                CpanelDescPorIdiomaRecord cpanelDescPorIdioma = new CpanelDescPorIdiomaRecord();
                cpanelDescPorIdioma.setIditem(idItem);
                cpanelDescPorIdioma.setIdidioma(availableLanguages.get(code).getIdidioma());
                cpanelDescPorIdioma.setDescripcion(value);
                descPorIdiomaDao.insert(cpanelDescPorIdioma);
            });
            return idItem;
        }
        return null;
    }

    public void updateRateTranslations(Integer itemId, Map<String, String> translations) {
        Map<String, CpanelIdiomaRecord> availableLanguages = getAvailableLanguages(translations);
        for (Map.Entry<String, String> texts : translations.entrySet()) {
            Integer langId = availableLanguages.get(texts.getKey()).getIdidioma();
            descPorIdiomaDao.upsert(itemId, langId, texts.getValue());
        }
    }

    private Map<String, CpanelIdiomaRecord> getAvailableLanguages(Map<String, String> translations) {
        Map<String, CpanelIdiomaRecord> idiomaRecords;
        if (translations != null && !translations.isEmpty()) {
            idiomaRecords = languageDao.getIdiomasByCodes(new ArrayList<>(translations.keySet())).stream()
                    .collect(Collectors.toMap(CpanelIdiomaRecord::getCodigo, Function.identity()));
            if (idiomaRecords.size() != translations.size()) {
                throw OneboxRestException
                        .builder(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE)
                        .setMessage("Language not found for rate")
                        .build();
            }
        } else {
            idiomaRecords = new HashMap<>();
        }
        return idiomaRecords;
    }

    public void unsetDefaultEventRate(List<CpanelTarifaRecord> eventRates) {
        eventRates.stream()
                .filter(rate -> BooleanUtils.toBoolean(rate.getDefecto()))
                .forEach(rate -> {
                    rate.setDefecto((byte) BooleanUtils.toInteger(false));
                    rateDao.update(rate);
                });
    }

    public void unsetDefaultSeasonTicketRate(List<CpanelTarifaRecord> seasonTicketRates, Integer sessionId) {
        unsetDefaultEventRate(seasonTicketRates);
        sessionRateDao.cleanRatesForSessionId(sessionId);
    }

    public Integer createRate(RateDTO rateDTO, Integer eventId, Integer itemDescSequenceId) {
        CpanelTarifaRecord cpanelTarifaRecord = RateConverter.toRecord(rateDTO, eventId, itemDescSequenceId);
        return rateDao.insert(cpanelTarifaRecord).getIdtarifa();
    }

    public Integer createEventRate(CreateEventRateDTO eventRateDTO, Integer eventId, Integer itemDescSequenceId, Integer position) {
        CpanelTarifaRecord cpanelTarifaRecord = RateConverter.toRecord(eventRateDTO, eventId, itemDescSequenceId, position);
        return rateDao.insert(cpanelTarifaRecord).getIdtarifa();
    }

    public void createDefaultEventRate(Integer eventId) {
        CpanelTarifaRecord result = new CpanelTarifaRecord();
        result.setIdevento(eventId);
        result.setNombre(DEFAULT_RATE_NAME);
        result.setDescripcion(DEFAULT_RATE_NAME);
        result.setDefecto((byte) 1);
        rateDao.insert(result);
    }

    public void updateEventVenueTemplatePriceZones(Integer eventId, Integer rateId) {
        final Map<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> templatesPriceZones =
                venueTemplateDao.getEventVenueTemplatesWithPriceZones(eventId);

        for (Map.Entry<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> templatePriceZone : templatesPriceZones.entrySet()) {
            for (CpanelZonaPreciosConfigRecord priceZone : templatePriceZone.getValue()) {
                CpanelAsignacionZonaPreciosRecord pz = new CpanelAsignacionZonaPreciosRecord();
                pz.setIdtarifa(rateId);
                pz.setIdzona(priceZone.getIdzona());
                pz.setPrecio(0.0);
                priceZoneAssignmentDao.insert(pz);
            }
        }
    }

    public void updateSeasonTicketVenueTemplatePriceZones(Integer seasonTicketId, Integer rateId) {
        updateEventVenueTemplatePriceZones(seasonTicketId, rateId);
    }

    public CpanelTarifaRecord checkEventRateToDelete(EventType eventType, Integer eventId, Integer rateId) {
        CpanelTarifaRecord eventRate = rateDao.getEventRate(eventId, rateId);

        //AVET events should not check this ones
        if (!EventType.AVET.equals(eventType)) {
            checkDefaultRate(eventRate);
            checkRateInSession(rateId);
        }

        checkRatePromotion(rateId);
        checkOrdersWithRate(rateId);
        return eventRate;
    }

    public CpanelTarifaRecord checkSeasonTicketRateToDelete(Integer seasonTicketId, Integer rateId) {
        CpanelTarifaRecord eventRate = rateDao.getSeasonTicketRate(seasonTicketId, rateId);
        checkDefaultRate(eventRate);
        checkRatePromotion(rateId);
        checkOrdersWithRate(rateId);
        return eventRate;
    }

    private void checkDefaultRate(CpanelTarifaRecord eventRate) {
        if (eventRate == null || CommonUtils.isTrue(eventRate.getDefecto())) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Rate not found for event").build();
        }
    }

    private void checkRateInSession(Integer rateId) {
        if (sessionRateDao.countByRateId(rateId) > 0) {
            throw OneboxRestException.builder(MsEventRateErrorCode.RATE_HAS_SESSIONS).
                    setMessage("Rate: " + rateId + " already has sessions").build();
        }
    }

    private void checkRatePromotion(Integer rateId) {
        if (eventRatePromotionDao.countByRateId(rateId) > 0) {
            throw OneboxRestException.builder(MsEventRateErrorCode.RATE_HAS_PROMOTIONS).
                    setMessage("Rate: " + rateId + " already has promotions").build();
        }
    }

    private void checkOrdersWithRate(Integer rateId) {
        if (ordersRepository.numberOperations(
                null, Collections.singletonList(OrderState.PAID), Collections.singletonList(rateId)) > 0) {
            throw OneboxRestException.builder(MsEventRateErrorCode.RATE_HAS_SALES).
                    setMessage("Rate: " + rateId + " already has sales").build();
        }
    }

    public void checkEventRate(Integer eventId, Integer rateId) {
        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId);
        CpanelTarifaRecord eventRate = eventRates.stream().
                filter(r -> r.getIdtarifa().equals(rateId)).findAny().orElse(null);
        if (eventRate == null) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Update event: " + eventId + " - Rate not found for event").build();
        }
    }

}

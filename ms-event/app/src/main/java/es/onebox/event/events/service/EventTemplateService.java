package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.events.converter.EventTemplatePriceConverter;
import es.onebox.event.events.converter.EventTemplateRestrictionConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.events.dto.EventTemplateRestrictionDTO;
import es.onebox.event.events.dto.UpdateEventTemplatePriceDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.PriceType;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.prices.PriceBuilder;
import es.onebox.event.events.prices.PriceBuilderFactory;
import es.onebox.event.events.prices.enums.PriceBuilderType;
import es.onebox.event.events.prices.enums.PriceTypeFilter;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.dto.UpdateSaleRestrictionDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventTemplateService {

    private static final List<EventStatus> ALLOWED_UPDATE_STATUS =
            Arrays.asList(EventStatus.PLANNED, EventStatus.IN_PROGRAMMING, EventStatus.READY);

    private final EventDao eventDao;
    private final EventPricesDao eventPricesDao;
    private final PriceTypeConfigDao priceTypeConfigDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final PriceBuilderFactory priceBuilderFactory;

    @Autowired
    public EventTemplateService(EventDao eventDao,
                                EventPricesDao eventPricesDao,
                                PriceTypeConfigDao priceTypeConfigDao,
                                EventConfigCouchDao eventConfigCouchDao, PriceBuilderFactory priceBuilderFactory) {
        this.eventDao = eventDao;
        this.eventPricesDao = eventPricesDao;
        this.priceTypeConfigDao = priceTypeConfigDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.priceBuilderFactory = priceBuilderFactory;
    }

    @MySQLRead
    public List<EventTemplatePriceDTO> getPrices(Long eventId, Long templateId) {
        return getPrices(eventId, templateId, null, null, null);
    }

    @MySQLRead
    public List<EventTemplatePriceDTO> getPrices(Long eventId, Long templateId, List<Long> sessionIdList, List<Integer> groupRateList, List<Integer> groupRateProductList) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (templateId == null || templateId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_MANDATORY);
        }
        CpanelConfigRecintoRecord template = eventDao.getEventVenueTemplate(eventId, templateId, null);
        if (template == null) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_NOT_FROM_EVENT);
        }

        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));

        PriceBuilder priceBuilder = priceBuilderFactory.getPriceBuilder(PriceBuilderType.getByProvider(EventUtils.getInventoryProvider(eventConfig)));

        List<EventPriceRecord> prices = priceBuilder.getVenueTemplatePrices(templateId.intValue(), eventId.intValue(), sessionIdList, groupRateList, groupRateProductList);

        if (prices.stream().anyMatch(p -> p.getEventId() == null || !p.getEventId().equals(eventId.intValue()))) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_NOT_FROM_EVENT);
        }

        List<EventPriceRecord> individualPrices = prices.stream().
                filter(p -> PriceType.INDIVIDUAL.equals(p.getPriceType())).collect(Collectors.toList());
        List<EventTemplatePriceDTO> response = EventTemplatePriceConverter.fromRecords(individualPrices);

        if (VenueTemplateType.ACTIVITY.getId().equals(template.getTipoplantilla()) ||
                VenueTemplateType.THEME_PARK.getId().equals(template.getTipoplantilla())) {
            List<EventPriceRecord> groupPriceRecords = prices.stream().
                    filter(p -> PriceType.GROUP.equals(p.getPriceType())).toList();
            //Create group prices based on combination of individual, overriding price if exists on DB
            for (EventPriceRecord individualPrice : individualPrices) {
                EventTemplatePriceDTO groupPrice = EventTemplatePriceConverter.fromRecord(individualPrice);
                EventPriceRecord groupRecord = groupPriceRecords.stream().filter(g -> g.getPriceZoneId().equals(individualPrice.getPriceZoneId()) &&
                        g.getRateId().equals(individualPrice.getRateId())).findFirst().orElse(null);
                groupPrice.setPriceType(PriceType.GROUP);
                groupPrice.setPrice(groupRecord != null ? groupRecord.getPrice() : 0.0d);
                response.add(groupPrice);
            }
        }

        return response;
    }

    @MySQLWrite
    public void updatePrices(Long eventId, Long templateId, List<UpdateEventTemplatePriceDTO> prices) {
        if (prices.isEmpty()) {
            return;
        }

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        if (event == null || (!EventType.AVET.getId().equals(event.getTipoevento()) && !ALLOWED_UPDATE_STATUS.contains(EventStatus.byId(event.getEstado())))) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_EVENT_STATUS);
        }

        List<EventTemplatePriceDTO> currentPrices = getPrices(eventId, templateId);

        for (UpdateEventTemplatePriceDTO p : prices) {
            PriceType priceType = EventUtils.checkPrices(currentPrices, p.getPrice(), p.getPriceTypeId(), p.getPriceType());

            Integer avetPriceId = null;
            if (p.getAdditionalConfig() != null && p.getAdditionalConfig().getAvetPriceId() != null) {
                avetPriceId = p.getAdditionalConfig().getAvetPriceId().intValue();
            }

            int updateRows = 0;
            if (PriceType.INDIVIDUAL.equals(priceType)) {
                updateRows = eventPricesDao.updateIndividual(p.getPriceTypeId().intValue(), p.getRateId(), roundPrice(p.getPrice()), avetPriceId);
            } else if (PriceType.GROUP.equals(priceType)) {
                updateRows = eventPricesDao.updateGroup(p.getPriceTypeId().intValue(), p.getRateId(), roundPrice(p.getPrice()));
                if (updateRows == 0) {
                    updateRows = eventPricesDao.addGroup(p.getPriceTypeId().intValue(), p.getRateId(), roundPrice(p.getPrice()));
                }
            }
            if (updateRows == 0) {
                throw new OneboxRestException(MsEventErrorCode.NO_PRICES_FOUND,
                        "Price does not exists for price-type: " + p.getPriceTypeId() + " - rate: " + p.getRateId(), null);
            }
        }
    }

    public EventTemplateRestrictionDTO getVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId) {
        checkEventTemplate(eventId, templateId);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (eventConfig.getRestrictions() == null || eventConfig.getRestrictions().getPriceZones() == null) {
            throw new OneboxRestException(MsEventErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND);
        }

        PriceZoneRestriction priceZoneRestriction = eventConfig.getRestrictions().getPriceZones().get(priceTypeId.intValue());

        if (priceZoneRestriction == null) {
            throw new OneboxRestException(MsEventErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND);
        }

        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypesById(templateId);
        validatePriceTypeInTemplate(priceTypeId, priceTypesById);

        return EventTemplateRestrictionConverter.fromRecord(priceTypeId, priceZoneRestriction, priceTypesById);
    }

    public void createVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId,
                                                         UpdateSaleRestrictionDTO request) {
         checkEventTemplate(eventId, templateId);
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypesById(templateId);
        validatePriceTypeInTemplate(priceTypeId, priceTypesById);
        request.getRequiredPriceTypeIds().forEach(id -> validatePriceTypeInTemplate(id, priceTypesById));
        if ((request.getLockedTicketsNumber() != null && request.getRequiredTicketsNumber() != null) ||
                (request.getLockedTicketsNumber() == null && request.getRequiredTicketsNumber() == null)) {
            throw new OneboxRestException(MsEventSessionErrorCode.TICKET_NUMBER_EXCLUSION_INPUT);
        }
        if(request.getRequiredPriceTypeIds().stream()
                .anyMatch(priceTypeId::equals)){
            throw new OneboxRestException(MsEventSessionErrorCode.CIRCULAR_PRICE_TYPE_RESTRICTION);
        }
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        if (eventConfig.getRestrictions() == null) {
            eventConfig.setRestrictions(new Restrictions());
        }
        if (eventConfig.getRestrictions().getPriceZones() == null) {
            eventConfig.getRestrictions().setPriceZones(new PriceZonesRestrictions());
        }
        PriceZoneRestriction pzr = EventTemplateRestrictionConverter.convert(request.getRequiredPriceTypeIds(),
                request.getRequiredTicketsNumber(), request.getLockedTicketsNumber());

        eventConfig.getRestrictions().getPriceZones().put(priceTypeId.intValue(), pzr);

        eventConfigCouchDao.upsert(eventId.toString(), eventConfig);
    }

    public void deleteVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId) {
        checkEventTemplate(eventId, templateId);
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypesById(templateId);
        validatePriceTypeInTemplate(priceTypeId, priceTypesById);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());

        if (eventConfig.getRestrictions() == null || eventConfig.getRestrictions().getPriceZones() == null
                || eventConfig.getRestrictions().getPriceZones().get(priceTypeId.intValue()) == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND);
        }

        eventConfig.getRestrictions().getPriceZones().remove(priceTypeId.intValue());

        eventConfigCouchDao.upsert(eventId.toString(), eventConfig);
    }

    public List<IdNameDTO> getRestrictedPriceTypes(Long eventId, Long templateId) {
        CpanelConfigRecintoRecord template = eventDao.getEventVenueTemplate(eventId, templateId, null);
        if (template == null) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_NOT_FROM_EVENT);
        }

        checkEventTemplate(eventId, templateId);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (eventConfig == null || eventConfig.getRestrictions() == null || eventConfig.getRestrictions().getPriceZones() == null) {
            return Collections.emptyList();
        }

        PriceZonesRestrictions priceZonesRestrictions = eventConfig.getRestrictions().getPriceZones();
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypesById(templateId);

        return EventTemplateRestrictionConverter.fromRecord(priceZonesRestrictions, priceTypesById);
    }

    public void resetEventVenueTemplatesPricesCurrency(Long eventId) {
        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        if (event == null || (!EventType.AVET.getId().equals(event.getTipoevento()) && !ALLOWED_UPDATE_STATUS.contains(EventStatus.byId(event.getEstado())))) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_EVENT_STATUS);
        }
        List<EventPriceRecord> prices = eventPricesDao.getBasePricesByEventId(eventId, PriceTypeFilter.ALL);

        prices.forEach(eventPriceRecord -> {
            if (PriceType.INDIVIDUAL.equals(eventPriceRecord.getPriceType())) {
                eventPricesDao.updateIndividual(eventPriceRecord.getPriceZoneId(), eventPriceRecord.getRateId(), roundPrice((double) 0), null);
            } else if (PriceType.GROUP.equals(eventPriceRecord.getPriceType())) {
                eventPricesDao.updateGroup(eventPriceRecord.getPriceZoneId(), eventPriceRecord.getRateId(), roundPrice((double) 0));
            }
        });
    }

    private CpanelConfigRecintoRecord checkEventTemplate(Long eventId, Long templateId) {
        CpanelConfigRecintoRecord template = eventDao.getEventVenueTemplate(eventId, templateId, null);
        if (template == null) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_NOT_FROM_EVENT);
        }
        return template;
    }

    private Map<Integer, CpanelZonaPreciosConfigRecord> getPriceTypesById(Long templateId) {
        return priceTypeConfigDao.findByVenueTemplateId(templateId.intValue()).stream()
                .collect(Collectors.toMap(CpanelZonaPreciosConfigRecord::getIdzona, Function.identity()));
    }

    private void validatePriceTypeInTemplate(Long priceTypeId, Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById) {
        if (!priceTypesById.containsKey(priceTypeId.intValue())) {
            throw new OneboxRestException(MsEventErrorCode.PRICE_TYPE_NOT_FOUND);
        }
    }

    private Double roundPrice(Double price) {
        return NumberUtils.scale(price).doubleValue();
    }
}

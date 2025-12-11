package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.attendants.EventAttendantConfigConverter;
import es.onebox.event.attendants.dto.AttendantsConfig;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dto.ChangeSeatExpiryTime;
import es.onebox.event.catalog.dto.ChangeSeatNewTicketSelection;
import es.onebox.event.catalog.dto.ChangeSeatPrice;
import es.onebox.event.catalog.dto.ChangeSeatRefund;
import es.onebox.event.catalog.dto.ChangeSeatVoucherExpiry;
import es.onebox.event.catalog.dto.ChangeSeatsConfig;
import es.onebox.event.catalog.dto.ChangeSeatsExpiry;
import es.onebox.event.catalog.dto.ReallocationChannel;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceType;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceTypeCommElement;
import es.onebox.event.catalog.elasticsearch.builder.EventDataBuilder;
import es.onebox.event.catalog.elasticsearch.builder.EventDataComElementsBuilder;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.EventElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.event.SeasonPackSettings;
import es.onebox.event.catalog.elasticsearch.exception.CatalogIndexerException;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.CountrySubdivisionDao;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.TourDao;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatExpiry;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventWhitelabelSettings;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.exception.EventIndexationFullReload;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.event.user.dao.UserDao;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventDataIndexer {

    private final EventElasticDao eventElasticDao;
    private final CatalogEventCouchDao catalogEventCouchDao;
    private final CustomTaxonomyDao customTaxonomyDao;
    private final UserDao userDao;
    private final TourDao tourDao;
    private final EntityDao entityDao;
    private final CountrySubdivisionDao countrySubdivisionDao;
    private final EntitiesRepository entitiesRepository;
    private final EventRelatedDataSupplier eventRelatedDataSupplier;
    private final StaticDataContainer staticDataContainer;
    private final CacheRepository localCacheRepository;

    @Autowired
    public EventDataIndexer(EventElasticDao eventElasticDao,
                            CatalogEventCouchDao catalogEventCouchDao,
                            UserDao userDao,
                            TourDao tourDao,
                            CustomTaxonomyDao customTaxonomyDao,
                            EntityDao entityDao, CountrySubdivisionDao countrySubdivisionDao,
                            EntitiesRepository entitiesRepository,
                            EventRelatedDataSupplier eventRelatedDataSupplier,
                            StaticDataContainer staticDataContainer,
                            CacheRepository localCacheRepository) {
        this.eventElasticDao = eventElasticDao;
        this.catalogEventCouchDao = catalogEventCouchDao;
        this.userDao = userDao;
        this.tourDao = tourDao;
        this.customTaxonomyDao = customTaxonomyDao;
        this.entityDao = entityDao;
        this.countrySubdivisionDao = countrySubdivisionDao;
        this.eventRelatedDataSupplier = eventRelatedDataSupplier;
        this.entitiesRepository = entitiesRepository;
        this.staticDataContainer = staticDataContainer;
        this.localCacheRepository = localCacheRepository;
    }

    public void indexEvent(EventIndexationContext ctx) {
        try {
            EventData eventData = buildEvent(ctx);

            eventElasticDao.upsert(eventData, eventData.getId(), false);
            catalogEventCouchDao.upsert(eventData.getEvent().getEventId().toString(), eventData.getEvent());

            ctx.addDocumentIndexed(eventData);
        } catch (Exception e) {
            throw new CatalogIndexerException(String.format("[CATALOG-INDEXER] event:%d . Error indexing event", ctx.getEventId()), e);
        }
    }

    EventData buildEvent(EventIndexationContext ctx) {
        Integer eventId = ctx.getEvent().getIdevento();

        switch (ctx.getType()) {
            case PARTIAL_BASIC -> {
                EventData eventData = getEventData(eventId);
                updateBasicEventInfo(eventData, ctx.getEvent());
                return eventData;
            }
            case PARTIAL_COM_ELEMENTS -> {
                EventData eventData = getEventData(eventId);
                updateComElementsEventInfo(eventData);
                return eventData;
            }
        }

        //Basic data
        CpanelEventoRecord eventRecord = ctx.getEvent();

        //ComElements data
        List<CpanelIdiomaComEventoRecord> languages = eventRelatedDataSupplier.getCommunicationLanguages(eventId);
        List<CpanelElementosComEventoRecord> communicationElementRecords = eventRelatedDataSupplier.getCommunicationElements(eventId);
        Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> emailCommElements = eventRelatedDataSupplier.getEmailCommElements(eventId);

        //Full data
        List<RateRecord> rates = eventRelatedDataSupplier.getRates(eventId);
        List<RateGroupRecord> rateGroups = eventRelatedDataSupplier.getRatesGroup(eventId);
        updateEventRatesInfo(rates, rateGroups);
        List<CpanelAtributosEventoRecord> eventAttributes = eventRelatedDataSupplier.getAttributes(eventId);

        BaseTaxonomyDao.TaxonomyInfo customTaxonomy = null;
        Integer taxonomyId = eventRecord.getIdtaxonomiapropia();
        if (taxonomyId != null) {
            customTaxonomy = localCacheRepository.cached(LocalCache.TAXONOMY_KEY, LocalCache.TAXONOMY_TTL, TimeUnit.SECONDS,
                    () -> customTaxonomyDao.getTaxonomyInfo(taxonomyId), new Object[]{taxonomyId});
        }

        var eventEntity = ctx.getEntity();
        IdNameCodeDTO eventEntitySubdivision = null;
        if (eventEntity.getCountrySubdivisionId() != null) {
            eventEntitySubdivision = localCacheRepository.cached(LocalCache.COUNTRY_SUB_KEY, LocalCache.COUNTRY_SUB_TTL, TimeUnit.SECONDS,
                    () -> countrySubdivisionDao.getCountrySubInfo(eventEntity.getCountrySubdivisionId()), new Object[]{eventEntity.getCountrySubdivisionId()});
        }

        ProducerDTO producer = localCacheRepository.cached(LocalCache.PRODUCER_KEY, LocalCache.PRODUCER_TTL, TimeUnit.SECONDS,
                () -> entitiesRepository.getProducerRaw(eventRecord.getIdpromotor()), new Object[]{eventRecord.getIdpromotor()});
        IdNameCodeDTO eventPromoterSubdivision = null;
        if (producer.getCountrySubdivisionId() != null) {
            eventPromoterSubdivision = localCacheRepository.cached(LocalCache.COUNTRY_SUB_KEY, LocalCache.COUNTRY_SUB_TTL, TimeUnit.SECONDS,
                    () -> countrySubdivisionDao.getCountrySubInfo(producer.getCountrySubdivisionId()), new Object[]{producer.getCountrySubdivisionId()});
        }

        InvoicePrefix invoicePrefix = null;
        if (eventRecord.getIdpromotor() != null && eventRecord.getInvoiceprefixid() != null) {
            invoicePrefix = entitiesRepository.getInvoicePrefix(eventRecord.getInvoiceprefixid());
        }
        String ownerUserName = null;
        if (eventRecord.getCreadopor() != null) {
            ownerUserName = localCacheRepository.cached(LocalCache.USERNAME_KEY, LocalCache.USERNAME_TTL, TimeUnit.SECONDS,
                    () -> userDao.getUserNameById(eventRecord.getCreadopor()), new Object[]{eventRecord.getCreadopor()});
        }
        String modifyUserName = null;
        if (eventRecord.getModificadopor() != null) {
            modifyUserName = localCacheRepository.cached(LocalCache.USERNAME_KEY, LocalCache.USERNAME_TTL, TimeUnit.SECONDS,
                    () -> userDao.getUserNameById(eventRecord.getModificadopor()), new Object[]{eventRecord.getModificadopor()});
        }

        CpanelGiraRecord tourRecord = null;
        EntityDao.EntityInfo tourEntity = null;
        Integer tourId = eventRecord.getIdgira();
        if (tourId != null) {
            tourRecord = localCacheRepository.cached(LocalCache.TOUR_KEY, LocalCache.TOUR_TTL, TimeUnit.SECONDS,
                    () -> tourDao.getById(tourId), new Object[]{tourId});
            Integer tourEntityId = tourRecord.getIdentidad();
            tourEntity = localCacheRepository.cached(LocalCache.ENTITYINFO_KEY, LocalCache.ENTITYINFO_TTL, TimeUnit.SECONDS,
                    () -> entityDao.getEntityInfo(tourEntityId), new Object[]{tourEntityId});
        }

        Boolean mandatoryLogin = null;
        Integer customerMaxSeats = null;
        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = ctx.getSeasonTicket();
        if (cpanelSeasonTicketRecord != null) {
            if (BooleanUtils.isTrue(cpanelSeasonTicketRecord.getIsmembermandatory())
                    || BooleanUtils.isTrue(cpanelSeasonTicketRecord.getRegistermandatory())) {
                mandatoryLogin = Boolean.TRUE;
            }
            customerMaxSeats = cpanelSeasonTicketRecord.getCustomermaxseats();
        }

        Map<Integer, List<VenuePriceTypeCommElement>> priceTypesCommElemsById = getPriceTypesComElements(ctx);

        EventWhitelabelSettings whitelabelSettings = ctx.getEventConfig() == null ? null : ctx.getEventConfig().getWhitelabelSettings();

        AttendantsConfig attendantConfig = EventAttendantConfigConverter.toAttendantConfig(ctx.getEventAttendantsConfig());

        SeasonPackSettings seasonPackSettings = buildSeasonPackSettings(eventRecord, ctx.getSessions());

        ChangeSeatsConfig changeSeatsConfig = buildEventChangeSeatConfig(ctx.getEventConfig());

        EventData eventData = EventDataBuilder.builder()
                //Basic
                .eventRecord(eventRecord)
                //Full
                .staticDataContainer(staticDataContainer)
                .languages(languages)
                .promotions(ctx.getEventPromotions())
                .rates(rates)
                .rateGroups(rateGroups)
                .eventAttributes(eventAttributes)
                .customTaxonomy(customTaxonomy)
                .entity(eventEntity)
                .entitySubdivision(eventEntitySubdivision)
                .promoter(producer)
                .promoterSubdivision(eventPromoterSubdivision)
                .usePromoterFiscalData(CommonUtils.isTrue(eventRecord.getUsardatosfiscalesproductor()))
                .venues(ctx.getVenues())
                .ownerUserName(ownerUserName)
                .modifyUserName(modifyUserName)
                .tour(tourRecord)
                .tourEntity(tourEntity)
                .prices(ctx.getPrices())
                .mandatoryLogin(mandatoryLogin)
                .customerMaxSeats(customerMaxSeats)
                .priceTypeCommunicationElements(priceTypesCommElemsById)
                .invoicePrefix(invoicePrefix)
                .eventAttendantFields(ctx.getEventAttendantFields())
                .whitelabelSettings(whitelabelSettings)
                .eventAttendantsConfig(attendantConfig)
                .seasonPackSettings(seasonPackSettings)
                .changeSeatsConfig(changeSeatsConfig)
                .build();

        EventDataComElementsBuilder.builder(eventData.getEvent())
                .staticDataContainer(staticDataContainer)
                .languages(languages)
                .communicationElementRecords(communicationElementRecords)
                .emailCommunicationElements(emailCommElements)
                .buildComElements();

        ctx.setVenueTemplatePrices(eventData.getEvent().getPrices());

        return eventData;
    }

    private EventData getEventData(Integer eventId) {
        Event event = catalogEventCouchDao.get(eventId.toString());
        if (event == null) {
            throw new EventIndexationFullReload("event not found into CB");
        }
        EventData eventData = EventDataBuilder.buildEventData(eventId.longValue());
        eventData.setEvent(event);
        return eventData;
    }

    private void updateBasicEventInfo(EventData eventData, CpanelEventoRecord eventRecord) {
        EventDataBuilder.builder(eventData.getEvent())
                .staticDataContainer(staticDataContainer)
                .eventRecord(eventRecord)
                .buildBasicEventInfo();
    }

    private void updateComElementsEventInfo(EventData eventData) {
        Integer eventId = eventData.getEvent().getEventId().intValue();
        List<CpanelIdiomaComEventoRecord> languages = eventRelatedDataSupplier.getCommunicationLanguages(eventId);
        List<CpanelElementosComEventoRecord> communicationElementRecords = eventRelatedDataSupplier.getCommunicationElements(eventId);
        Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> emailCommElements = eventRelatedDataSupplier.getEmailCommElements(eventId);

        EventDataComElementsBuilder.builder(eventData.getEvent())
                .staticDataContainer(staticDataContainer)
                .languages(languages)
                .communicationElementRecords(communicationElementRecords)
                .emailCommunicationElements(emailCommElements)
                .buildComElements();
    }

    private ChangeSeatsConfig buildEventChangeSeatConfig(EventConfig eventConfig) {
        if (eventConfig == null || eventConfig.getEventChangeSeatConfig() == null) {
            return null;
        }

        EventChangeSeatConfig config = eventConfig.getEventChangeSeatConfig();
        ChangeSeatsConfig out = new ChangeSeatsConfig();
        out.setAllowChangeSeat(config.getAllowChangeSeat());
        out.setChangeType(config.getChangeType());
        out.setEventChangeSeatExpiry(buildEventChangeSeatExpiry(config.getEventChangeSeatExpiry()));
        out.setNewTicketSelection(buildNewTicketSelection(config.getNewTicketSelection()));
        out.setReallocationChannel(buildReallocationChannel(config.getReallocationChannel()));

        return out;

    }

    private ChangeSeatsExpiry buildEventChangeSeatExpiry(EventChangeSeatExpiry in) {
        if (in == null) {
            return null;
        }

        ChangeSeatsExpiry out = new ChangeSeatsExpiry();
        out.setTimeOffsetLimitUnit(in.getTimeOffsetLimitUnit());
        out.setTimeOffsetLimitAmount(in.getTimeOffsetLimitAmount());

        return out;
    }

    private ChangeSeatNewTicketSelection buildNewTicketSelection(es.onebox.event.events.domain.eventconfig.ChangeSeatNewTicketSelection in) {
        if (in == null) {
            return null;
        }

        ChangeSeatNewTicketSelection out = new ChangeSeatNewTicketSelection();
        out.setAllowedSessions(in.getAllowedSessions());
        out.setSameDateOnly(in.getSameDateOnly());
        out.setTickets(in.getTickets());

        ChangeSeatPrice price = new ChangeSeatPrice();
        price.setType(in.getPrice().getType());
        price.setRefund(getRefund(in));
        out.setPrice(price);

        return out;
    }

    private ChangeSeatRefund getRefund(es.onebox.event.events.domain.eventconfig.ChangeSeatNewTicketSelection in) {
        if (in.getPrice().getRefund() != null) {
            ChangeSeatRefund changeSeatRefund = new ChangeSeatRefund();
            changeSeatRefund.setType(in.getPrice().getRefund().getType());
            if (in.getPrice().getRefund().getVoucherExpiry() != null) {
                changeSeatRefund.setVoucherExpiry(buildVoucherExpiry(in.getPrice().getRefund().getVoucherExpiry()));
            }
            return changeSeatRefund;
        }
        return null;
    }

    private ChangeSeatVoucherExpiry buildVoucherExpiry(es.onebox.event.events.domain.eventconfig.ChangeSeatVoucherExpiry in) {
        if (in == null) {
            return null;
        }

        ChangeSeatVoucherExpiry out = new ChangeSeatVoucherExpiry();
        out.setEnabled(in.getEnabled());
        if (in.getExpiryTime() != null) {
            ChangeSeatExpiryTime changeSeatExpiryTime = new ChangeSeatExpiryTime();
            changeSeatExpiryTime.setTimeOffsetLimitAmount(in.getExpiryTime().getTimeOffsetLimitAmount());
            changeSeatExpiryTime.setTimeOffsetLimitUnit(in.getExpiryTime().getTimeOffsetLimitUnit());
            out.setExpiryTime(changeSeatExpiryTime);
        }
        return out;
    }


    private ReallocationChannel buildReallocationChannel(es.onebox.event.events.domain.eventconfig.ReallocationChannel in) {
        if (in == null) {
            return null;
        }

        ReallocationChannel out = new ReallocationChannel();
        out.setId(in.getId());
        out.setApplyToAllChannelTypes(in.getApplyToAllChannelTypes());
        return out;

    }

    private SeasonPackSettings buildSeasonPackSettings(CpanelEventoRecord eventRecord, List<SessionForCatalogRecord> sessions) {
        if (EventType.SEASON_TICKET.getId().equals(eventRecord.getTipoevento())) {
            Long sessionID = !sessions.isEmpty() ? sessions.get(0).getIdsesion().longValue() : null;
            return new SeasonPackSettings(sessionID);
        }
        return null;
    }


    private Map<Integer, List<VenuePriceTypeCommElement>> getPriceTypesComElements(EventIndexationContext ctx) {
        Map<Integer, List<VenuePriceTypeCommElement>> priceTypesCommElemsById = new HashMap<>();
        Map<Integer, List<EventPriceRecord>> pricesByTemplate = ctx.getPrices().stream().collect(Collectors.groupingBy(EventPriceRecord::getVenueConfigId));
        for (Map.Entry<Integer, List<EventPriceRecord>> templatePrices : pricesByTemplate.entrySet()) {
            VenueDescriptor venueDescriptor = ctx.getVenueDescriptor().get(templatePrices.getKey());
            if (venueDescriptor != null) {
                Map<Long, VenuePriceType> priceTypesById = venueDescriptor.getPriceTypes().stream().
                        collect(Collectors.toMap(VenuePriceType::getId, Function.identity()));
                if (MapUtils.isNotEmpty(priceTypesById)) {
                    for (EventPriceRecord price : templatePrices.getValue()) {
                        VenuePriceType venuePriceTypes = priceTypesById.get(price.getPriceZoneId().longValue());
                        priceTypesCommElemsById.put(price.getPriceZoneId(), venuePriceTypes.getCommElements());
                    }
                }
            }
        }
        return priceTypesCommElemsById;
    }

    public void updateEventRatesInfo(List<RateRecord> rates, List<RateGroupRecord> rateGroups) {
        if (CollectionUtils.isEmpty(rateGroups)) {
            return;
        }
        Map<Integer, List<Integer>> rateGroupsByRate = rateGroups.stream()
                .flatMap(rateGroup -> rateGroup.getTarifas().stream()
                        .map(rateId -> new AbstractMap.SimpleEntry<>(rateId, rateGroup.getIdGrupoTarifa())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
        for (RateRecord rate : rates) {
            rate.setGruposTarifas(rateGroupsByRate.get(rate.getIdTarifa()));
        }
    }
}

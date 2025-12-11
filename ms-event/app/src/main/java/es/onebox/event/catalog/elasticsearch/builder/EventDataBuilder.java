package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.attendants.dto.AttendantsConfig;
import es.onebox.event.catalog.dto.ChangeSeatsConfig;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceTypeCommElement;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.PriceZonePrice;
import es.onebox.event.catalog.elasticsearch.dto.Promotion;
import es.onebox.event.catalog.elasticsearch.dto.RateGroup;
import es.onebox.event.catalog.elasticsearch.dto.RatePrice;
import es.onebox.event.catalog.elasticsearch.dto.Venue;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantField;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.event.EventRate;
import es.onebox.event.catalog.elasticsearch.dto.event.SeasonPackSettings;
import es.onebox.event.catalog.elasticsearch.enums.RateGroupType;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.catalog.elasticsearch.utils.VenueUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.venue.dto.CommunicationElementType;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.domain.eventconfig.EventWhitelabelSettings;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_EVENT;

public class EventDataBuilder {

    private final Event data;

    private StaticDataContainer staticDataContainer;
    private CpanelEventoRecord eventRecord;
    private List<CpanelIdiomaComEventoRecord> languages;
    private List<EventPromotion> promotions;
    private List<RateRecord> rates;
    private List<RateGroupRecord> rateGroups;
    private List<CpanelAtributosEventoRecord> eventAttributes;
    private List<VenueRecord> venues;
    private BaseTaxonomyDao.TaxonomyInfo customTaxonomy;
    private CpanelGiraRecord tour;
    private EntityDao.EntityInfo tourEntity;
    private EntityDTO entity;
    private IdNameCodeDTO entitySubdivision;
    private ProducerDTO producer;
    private IdNameCodeDTO producerCountrySub;
    private Boolean usePromoterFiscalData;
    private String ownerUserName;
    private String modifyUserName;
    private List<EventPriceRecord> prices;
    private Boolean mandatoryLogin;
    private Integer customerMaxSeats;
    private Map<Integer, List<VenuePriceTypeCommElement>> priceTypeCommunicationElements;
    private InvoicePrefix invoicePrefix;
    private List<EventAttendantField> eventAttendantFields;
    private EventWhitelabelSettings whitelabelSettings;
    private AttendantsConfig attendantsConfig;
    private SeasonPackSettings seasonPackSettings;
    private ChangeSeatsConfig changeSeatsConfig;

    private EventDataBuilder(Event data) {
        super();
        this.data = data;
    }

    public static EventDataBuilder builder() {
        return new EventDataBuilder(new Event());
    }

    public static EventDataBuilder builder(Event data) {
        return new EventDataBuilder(data);
    }

    public static EventData buildEventData(Long eventId) {
        EventData eventData = new EventData();
        eventData.setId(EventDataUtils.getEventKey(eventId));
        eventData.setJoin(new JoinField(KEY_EVENT, null));
        return eventData;
    }

    public EventData build() {
        EventData eventData = buildEventData(eventRecord.getIdevento().longValue());
        buildBasicEventInfo();

        fillAdditionalEventInfo(data);
        fillEventAttributesInfo(data);
        fillTaxonomy(data);
        fillEntityAndPromoter(data);
        fillVenueInfo(data);
        fillUserInfo(data);
        fillTourInfo(data);
        fillEventPromotionsInfo(data);
        fillEventRates(data);
        fillEventRateGroup(data);
        fillEventPrices(data);

        eventData.setEvent(data);
        return eventData;
    }

    public void buildBasicEventInfo() {
        data.setEventId(eventRecord.getIdevento().longValue());
        data.setEventName(eventRecord.getNombre());
        data.setEventDescription(eventRecord.getDescripcion());
        data.setEventType(eventRecord.getTipoevento() != null ? eventRecord.getTipoevento().byteValue() : null);
        data.setEventStatus(eventRecord.getEstado());
        data.setPurchaseEventDate(eventRecord.getFechaventa());
        data.setPurchaseEventDateOlsonId(staticDataContainer.getTimeZone(eventRecord.getFechaventatz()));
        data.setBeginEventDate(eventRecord.getFechainicio());
        data.setBeginEventDateOlsonId(staticDataContainer.getTimeZone(eventRecord.getFechainiciotz()));
        data.setEndEventDate(eventRecord.getFechafin());
        data.setEndEventDateOlsonId(staticDataContainer.getTimeZone(eventRecord.getFechafintz()));
        data.setCreateEventDate(eventRecord.getFechaalta());
        data.setPublishEventDate(eventRecord.getFechapublicacion());
        data.setPublishEventDateOlsonId(staticDataContainer.getTimeZone(eventRecord.getFechapublicaciontz()));
        data.setModificationEventDate(eventRecord.getFechamodificacion());
        data.setStatusModificationEventDate(eventRecord.getFechacambioestado());
        data.setCurrency(eventRecord.getIdcurrency());
        data.setExternalReference(eventRecord.getExternalreference());
        data.setPromoterRef(eventRecord.getReferenciapromotor());
        data.setChargePersonName(eventRecord.getNombreresponsable());
        data.setChargePersonSurname(eventRecord.getApellidosresponsable());
        data.setChargePersonEmail(eventRecord.getEmailresponsable());
        data.setChargePersonPhone(eventRecord.getTelefonoresponsable());
        data.setChargePersonPosition(eventRecord.getCargoresponsable());
        data.setEventCapacity(eventRecord.getAforo());
        data.setArchived(CommonUtils.isTrue(eventRecord.getArchivado()));
        data.setEventSeasonType(eventRecord.getTipoabono());
        data.setEnabledBookingEvent(CommonUtils.isTrue(eventRecord.getPermitereservas()));
        data.setTypeExpirationBookingEvent(eventRecord.getTipocaducidadreserva());
        data.setUnitsExpirationBookingEvent(eventRecord.getNumunidadescaducidad());
        data.setTypeUnitsExpirationBookingEvent(eventRecord.getTipounidadescaducidad());
        data.setTypeLimitDateBookingEvent(eventRecord.getTipofechalimitereserva());
        data.setUnitsLimitBookingEvent(eventRecord.getNumunidadeslimite());
        data.setTypeUnitsLimitBookingEvent(eventRecord.getTipounidadeslimite());
        data.setTypeLimitBookingEvent(eventRecord.getTipolimite());
        data.setLimitBookingEventDate(eventRecord.getFechalimite());
        data.setBeginBookingEventDate(eventRecord.getFechainicioreserva());
        data.setBeginBookingEventDateOlsonId(staticDataContainer.getTimeZone(eventRecord.getFechainicioreservatz()));
        data.setEndBookingEventDate(eventRecord.getFechafinreserva());
        data.setEndBookingEventDateOlsonId(staticDataContainer.getTimeZone(eventRecord.getFechafinreservatz()));
        data.setUseCommunicationElementsTour(CommonUtils.isTrue(eventRecord.getUsaelementoscomgira()));
        data.setCodeAdmissionAge(eventRecord.getIdcalificacionedad());
        data.setAdmissionAge(staticDataContainer.getAdmission(eventRecord.getIdcalificacionedad()));
        data.setSupraEvent(CommonUtils.isTrue(eventRecord.getEssupraevento()));
        data.setGiftTicket(CommonUtils.isTrue(eventRecord.getEntradaregalo()));
        data.setUseTieredPricing(CommonUtils.isTrue(eventRecord.getUsetieredpricing()));
        data.setAllowChannelUseAlternativeCharges(eventRecord.getAllowchannelusealternativecharges());
    }

    public EventDataBuilder eventRecord(CpanelEventoRecord eventRecord) {
        this.eventRecord = eventRecord;
        return this;
    }

    public EventDataBuilder staticDataContainer(StaticDataContainer staticDataContainer) {
        this.staticDataContainer = staticDataContainer;
        return this;
    }

    public EventDataBuilder priceTypeCommunicationElements(Map<Integer, List<VenuePriceTypeCommElement>> priceTypeCommunicationElements) {
        this.priceTypeCommunicationElements = priceTypeCommunicationElements;
        return this;
    }

    public EventDataBuilder languages(List<CpanelIdiomaComEventoRecord> languages) {
        this.languages = languages;
        return this;
    }

    public EventDataBuilder promotions(List<EventPromotion> promotions) {
        this.promotions = promotions;
        return this;
    }

    public EventDataBuilder rates(List<RateRecord> rates) {
        this.rates = rates;
        return this;
    }

    public EventDataBuilder rateGroups(List<RateGroupRecord> rateGroups) {
        this.rateGroups = rateGroups;
        return this;
    }

    public EventDataBuilder eventAttributes(List<CpanelAtributosEventoRecord> eventAttributes) {
        this.eventAttributes = eventAttributes;
        return this;
    }

    public EventDataBuilder customTaxonomy(BaseTaxonomyDao.TaxonomyInfo customTaxonomy) {
        this.customTaxonomy = customTaxonomy;
        return this;
    }

    public EventDataBuilder entity(EntityDTO entity) {
        this.entity = entity;
        return this;
    }

    public EventDataBuilder entitySubdivision(IdNameCodeDTO subdivision) {
        this.entitySubdivision = subdivision;
        return this;
    }

    public EventDataBuilder promoter(ProducerDTO promoter) {
        this.producer = promoter;
        return this;
    }

    public EventDataBuilder promoterSubdivision(IdNameCodeDTO subdivision) {
        this.producerCountrySub = subdivision;
        return this;
    }

    public EventDataBuilder usePromoterFiscalData(Boolean usePromoterFiscalData) {
        this.usePromoterFiscalData = usePromoterFiscalData;
        return this;
    }

    public EventDataBuilder venues(List<VenueRecord> venues) {
        this.venues = venues;
        return this;
    }

    public EventDataBuilder ownerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
        return this;
    }

    public EventDataBuilder modifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
        return this;
    }

    public EventDataBuilder tour(CpanelGiraRecord tour) {
        this.tour = tour;
        return this;
    }

    public EventDataBuilder tourEntity(EntityDao.EntityInfo tourEntity) {
        this.tourEntity = tourEntity;
        return this;
    }

    public EventDataBuilder prices(List<EventPriceRecord> prices) {
        this.prices = prices;
        return this;
    }

    public EventDataBuilder mandatoryLogin(Boolean mandatoryLogin) {
        this.mandatoryLogin = mandatoryLogin;
        return this;
    }

    public EventDataBuilder customerMaxSeats(Integer customerMaxSeats) {
        this.customerMaxSeats = customerMaxSeats;
        return this;
    }

    public EventDataBuilder invoicePrefix(InvoicePrefix invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
        return this;
    }

    public EventDataBuilder eventAttendantFields(List<EventAttendantField> eventAttendantFields) {
        this.eventAttendantFields = eventAttendantFields;
        return this;
    }

    public EventDataBuilder whitelabelSettings(EventWhitelabelSettings whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
        return this;
    }

    public EventDataBuilder eventAttendantsConfig(AttendantsConfig attendantsConfig) {
        this.attendantsConfig = attendantsConfig;
        return this;
    }

    public EventDataBuilder seasonPackSettings(SeasonPackSettings seasonPackSettings) {
        this.seasonPackSettings = seasonPackSettings;
        return this;
    }

    public EventDataBuilder changeSeatsConfig(ChangeSeatsConfig changeSeatsConfig) {
        this.changeSeatsConfig = changeSeatsConfig;
        return this;
    }

    private void fillAdditionalEventInfo(Event event) {
        List<String> languageCodes = languages.stream()
                .map(eventLanguage -> staticDataContainer.getLanguage(eventLanguage.getIdidioma()))
                .collect(Collectors.toList());
        languages.stream()
                .filter(eventLanguage -> CommonUtils.isTrue(eventLanguage.getDefecto()))
                .findFirst()
                .ifPresent(defaultLanguage -> event.setEventDefaultLanguage(staticDataContainer.getLanguage(defaultLanguage.getIdidioma())));

        if (invoicePrefix != null) {
            event.setInvoicePrefixId(invoicePrefix.getId().longValue());
            event.setInvoicePrefix(invoicePrefix.getPrefix());
        }

        event.setEventLanguages(languageCodes);
        event.setMandatoryLogin(mandatoryLogin);
        event.setCustomerMaxSeats(customerMaxSeats);
        event.setAttendantFields(eventAttendantFields);
        event.setWhitelabelSettings(whitelabelSettings);
        event.setAttendantsConfig(attendantsConfig);
        event.setSeasonPackSettings(seasonPackSettings);
        event.setChangeSeatConfig(changeSeatsConfig);
    }

    private void fillEventAttributesInfo(Event event) {
        if (CollectionUtils.isNotEmpty(eventAttributes)) {
            Set<Integer> attributeIds = eventAttributes.stream().map(CpanelAtributosEventoRecord::getIdatributo).collect(Collectors.toSet());
            Set<Integer> attributeValuesId = eventAttributes.stream().map(CpanelAtributosEventoRecord::getIdvalor).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(attributeIds)) {
                event.setEventAttributesId(new ArrayList<>(attributeIds));
            }
            if (CollectionUtils.isNotEmpty(attributeValuesId)) {
                event.setEventAttributesValueId(attributeValuesId.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
    }

    private void fillTaxonomy(Event event) {
        if (eventRecord.getIdtaxonomia() != null) {
            BaseTaxonomyDao.TaxonomyInfo baseTaxonomy = staticDataContainer.getBaseTaxonomy(eventRecord.getIdtaxonomia());
            if (baseTaxonomy != null) {
                event.setTaxonomyId(baseTaxonomy.id());
                event.setTaxonomyCode(baseTaxonomy.code());
                event.setTaxonomyDescription(baseTaxonomy.desc());
                if (baseTaxonomy.parentId() != null) {
                    BaseTaxonomyDao.TaxonomyInfo parentBaseTaxonomy = staticDataContainer.getBaseTaxonomy(baseTaxonomy.parentId());
                    if (parentBaseTaxonomy != null) {
                        event.setTaxonomyParentId(parentBaseTaxonomy.id());
                        event.setTaxonomyParentCode(parentBaseTaxonomy.code());
                        event.setTaxonomyParentDescription(parentBaseTaxonomy.desc());
                    }
                }
            }
        }
        if (eventRecord.getIdtaxonomiapropia() != null && customTaxonomy != null) {
            event.setCustomTaxonomyId(customTaxonomy.id());
            event.setCustomTaxonomyCode(customTaxonomy.code());
            event.setCustomTaxonomyDescription(customTaxonomy.desc());
        }
    }

    private void fillEntityAndPromoter(Event event) {
        Entity eventEntity = fillEntityData(entity, entitySubdivision);
        Entity producerEntity = fillProducerData(producer, producerCountrySub);

        event.setEntity(eventEntity);
        event.setPromoter(producerEntity);
        event.setUsePromoterFiscalData(usePromoterFiscalData);

        // Fill legacy producer/entity fields
        if (Boolean.TRUE.equals(usePromoterFiscalData)) {
            fillProducerDataLegacy(event, producer, producerCountrySub);
        } else {
            fillEntityDataLegacy(event, entity, entitySubdivision);
        }
    }

    private Entity fillEntityData(EntityDTO entity, IdNameCodeDTO subdivision) {
        Entity result = new Entity();
        result.setUsesExternalManagement(entity.getUseExternalManagement());
        result.setId(entity.getId());
        result.setOperatorId(entity.getOperator().getId());
        result.setOperatorStatus(entity.getOperator().getState().getState());
        result.setStatus(entity.getState().getState());
        result.setName(entity.getName());
        result.setCorporateName(entity.getSocialReason());
        result.setFiscalCode(entity.getNif());
        result.setAddress(entity.getAddress());
        result.setCity(entity.getCity());
        result.setPostalCode(entity.getPostalCode());

        result.setCountryId(entity.getCountryId());
        if (entity.getCountryId() != null) {
            IdNameCodeDTO country = staticDataContainer.getCountry(entity.getCountryId());
            if (country != null) {
                result.setCountryName(country.getName());
                result.setCountryCode(country.getCode());
            }
        }

        result.setCountrySubdivisionId(entity.getCountrySubdivisionId());
        if (entity.getCountrySubdivisionId() != null && subdivision != null) {
            result.setCountrySubdivisionName(subdivision.getName());
            result.setCountrySubdivisionCode(subdivision.getCode());
        }

        return result;
    }

    private Entity fillProducerData(ProducerDTO producer, IdNameCodeDTO subdivision) {
        Entity result = new Entity();
        result.setId(producer.getId());
        result.setOperatorId(producer.getOperator().getId());
        result.setOperatorStatus(producer.getOperator().getStatus().getState());
        result.setStatus(producer.getStatus().getId());
        result.setName(producer.getName());
        result.setCorporateName(producer.getSocialReason());
        result.setFiscalCode(producer.getNif());
        result.setAddress(producer.getAddress());
        result.setCity(producer.getCity());
        result.setPostalCode(producer.getPostalCode());
        result.setEmail(producer.getEmail());

        result.setCountryId(producer.getCountryId());
        if (producer.getCountryId() != null) {
            IdNameCodeDTO country = staticDataContainer.getCountry(entity.getCountryId());
            if (country != null) {
                result.setCountryName(country.getName());
                result.setCountryCode(country.getCode());
            }
        }

        result.setCountrySubdivisionId(producer.getCountrySubdivisionId());
        if (producer.getCountrySubdivisionId() != null && subdivision != null) {
            result.setCountrySubdivisionName(subdivision.getName());
            result.setCountrySubdivisionCode(subdivision.getCode());
        }

        return result;
    }

    private void fillEntityDataLegacy(Event event, EntityDTO entity, IdNameCodeDTO subdivision) {
        event.setEntityId(entity.getId());
        event.setOperatorId(entity.getOperator().getId());
        event.setOperatorStatus(entity.getOperator().getState().getState());
        event.setEntityStatus(entity.getState().getState());
        event.setEntityName(entity.getName());
        event.setEntityCorporateName(entity.getSocialReason());
        event.setEntityFiscalCode(entity.getNif());
        event.setEntityAddress(entity.getAddress());
        event.setEntityCity(entity.getCity());
        event.setEntityPostalCode(entity.getPostalCode());

        event.setEntityCountryId(entity.getCountryId());
        if (entity.getCountryId() != null) {
            IdNameCodeDTO country = staticDataContainer.getCountry(entity.getCountryId());
            if (country != null) {
                event.setEntityCountryName(country.getName());
                event.setEntityCountryCode(country.getCode());
            }
        }

        event.setEntityCountrySubdivisionId(entity.getCountrySubdivisionId());
        if (entity.getCountrySubdivisionId() != null && subdivision != null) {
            event.setEntityCountrySubdivisionName(subdivision.getName());
            event.setEntityCountrySubdivisionCode(subdivision.getCode());
        }
        event.setEntityUsesExternalManagement(entity.getUseExternalManagement());
    }

    private void fillProducerDataLegacy(Event event, ProducerDTO producer, IdNameCodeDTO subdivision) {
        event.setEntityId(producer.getEntity().getId().intValue());

        event.setOperatorId(producer.getOperator().getId());
        event.setOperatorStatus(producer.getOperator().getStatus().getState());
        event.setEntityStatus(producer.getStatus().getId());
        event.setEntityName(producer.getName());
        event.setEntityCorporateName(producer.getSocialReason());
        event.setEntityFiscalCode(producer.getNif());
        event.setEntityAddress(producer.getAddress());
        event.setEntityCity(producer.getCity());
        event.setEntityPostalCode(producer.getPostalCode());

        event.setEntityCountryId(producer.getCountryId());
        if (producer.getCountryId() != null) {
            IdNameCodeDTO country = staticDataContainer.getCountry(entity.getCountryId());
            if (country != null) {
                event.setEntityCountryName(country.getName());
                event.setEntityCountryCode(country.getCode());
            }
        }

        event.setEntityCountrySubdivisionId(producer.getCountrySubdivisionId());
        if (producer.getCountrySubdivisionId() != null && subdivision != null) {
            event.setEntityCountrySubdivisionName(subdivision.getName());
            event.setEntityCountrySubdivisionCode(subdivision.getCode());
        }
    }


    private void fillVenueInfo(Event event) {
        event.setVenues(venues.stream().map(this::buildVenue).collect(Collectors.toList()));
        event.setMultiVenue(event.getVenues().size() > 1);
        event.setMultiLocation(VenueUtils.areMultiLocation(venues));
    }

    private Venue buildVenue(VenueRecord v) {
        return Venue.builder()
                .withId(v.getId())
                .withName(v.getName())
                .withEntityId(v.getEntityId())
                .withEntityName(v.getEntityName())
                .withContactMail(v.getContactMail())
                .withContactName(v.getContactName())
                .withContactPhone(v.getContactPhone())
                .withContactRole(v.getContactRole())
                .withContactSurname(v.getContactSurname())
                .withCoordenates(v.getCoordenates())
                .withCountry(v.getCountry())
                .withCountryCode(v.getCountryCode())
                .withManagementCompany(v.getManagementCompany())
                .withMunicipality(v.getMunicipality())
                .withAddress(v.getAddress())
                .withOwnerCompany(v.getOwnerCompany())
                .withPostalCode(v.getPostalCode())
                .withProvince(v.getProvince())
                .withProvinceCode(v.getProvinceCode())
                .withTimeZone(v.getTimeZone())
                .withImage(v.getImagePath())
                .withGooglePlaceId(v.getGooglePlaceId())
                .build();
    }

    private void fillUserInfo(Event event) {
        if (eventRecord.getCreadopor() != null) {
            event.setOwnerUserId(eventRecord.getCreadopor());
            event.setOwnerUserName(ownerUserName);
        }
        if (eventRecord.getModificadopor() != null) {
            event.setModifyUserId(eventRecord.getModificadopor());
            event.setModifyUserName(modifyUserName);
        }
    }

    private void fillTourInfo(Event event) {
        if (eventRecord.getIdgira() != null) {
            event.setTourId(eventRecord.getIdgira());
            if (tour != null) {
                event.setTourName(tour.getNombre());
                event.setTourPromoterRef(tour.getReferenciapromotor());
            }
            if (tourEntity != null) {
                event.setTourEntityId(tourEntity.id());
                event.setTourOperatorId(tourEntity.operatorId());
            }
        }
    }

    private void fillEventPromotionsInfo(Event event) {
        if (CollectionUtils.isNotEmpty(promotions)) {

            List<Promotion> promos = promotions.stream()
                    .map(p -> {
                        Promotion promotion = new Promotion();
                        promotion.setEventPromotionTemplateId(p.getEventPromotionTemplateId());
                        promotion.setPromotionTemplateId(p.getPromotionTemplateId());
                        promotion.setEventId(p.getEventId());
                        promotion.setName(p.getName());
                        promotion.setType(p.getType());
                        promotion.setActive(p.getActive());
                        promotion.setStatus(p.getStatus());
                        promotion.setCommunicationElements(p.getCommElements());
                        promotion.setSelfManaged(p.getSelfManaged());
                        promotion.setRestrictiveAccess(p.getRestrictiveAccess());
                        return promotion;
                    })
                    .collect(Collectors.toList());

            event.setPromotions(promos);
        }
    }

    private void fillEventRates(Event event) {
        if (CollectionUtils.isNotEmpty(rates)) {
            event.setRates(rates.stream()
                    .map(this::convert)
                    .collect(Collectors.toList()));
        }
    }

    private void fillEventRateGroup(Event event) {
        if (CollectionUtils.isNotEmpty(rateGroups)) {
            event.setRateGroups(rateGroups.stream()
                    .map(this::convert)
                    .toList());
        }
    }

    private EventRate convert(RateRecord record) {
        Integer rateId = record.getIdTarifa();
        Integer eventId = record.getIdEvento();
        EventRate rate = new EventRate();
        rate.setId(rateId == null ? null : rateId.longValue());
        rate.setEventId(eventId == null ? null : eventId.longValue());
        rate.setName(record.getNombre());
        rate.setDefaultRate(CommonUtils.isTrue(record.getDefecto()));
        rate.setPosition(record.getPosition());
        rate.setRestrictiveAccess(record.getAccesoRestrictivo() != null ? CommonUtils.isTrue(record.getAccesoRestrictivo()) : null);
        rate.setTranslations(record.getTranslations());
        rate.setRateGroups(record.getGruposTarifas());

        return rate;
    }

    private RateGroup convert(RateGroupRecord rateGroupRecord) {
        RateGroup rateGroup = new RateGroup();
        rateGroup.setId(rateGroupRecord.getIdGrupoTarifa());
        rateGroup.setName(rateGroupRecord.getNombre());
        rateGroup.setRates(rateGroupRecord.getTarifas().stream().toList());
        rateGroup.setPosition(rateGroupRecord.getPosition());
        rateGroup.setTranslations(rateGroupRecord.getTranslations());
        rateGroup.setType(RateGroupType.fromId(rateGroupRecord.getTipo()));

        return rateGroup;
    }

    private void fillEventPrices(Event event) {
        if (CollectionUtils.isNotEmpty(prices)) {
            List<VenueTemplatePrice> venueTemplatePrices = new ArrayList<>();

            prices.forEach(p -> {
                VenueTemplatePrice venueTemplatePrice = venueTemplatePrices.stream()
                        .filter(vp -> p.getVenueConfigId().equals(vp.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            VenueTemplatePrice vtp = new VenueTemplatePrice();
                            venueTemplatePrices.add(vtp);
                            return vtp;
                        });

                venueTemplatePrice.setId(p.getVenueConfigId());
                venueTemplatePrice.setName(p.getVenueConfigName());

                if (CollectionUtils.isEmpty(venueTemplatePrice.getPriceZones())) {
                    venueTemplatePrice.setPriceZones(new ArrayList<>());
                }

                PriceZonePrice priceZonePrice = venueTemplatePrice.getPriceZones().stream()
                        .filter(zp -> zp.getId().equals(p.getPriceZoneId()))
                        .findFirst()
                        .orElseGet(() -> {
                            PriceZonePrice pzp = new PriceZonePrice();
                            venueTemplatePrice.getPriceZones().add(pzp);
                            return pzp;
                        });

                priceZonePrice.setId(p.getPriceZoneId());
                priceZonePrice.setDescription(p.getPriceZoneDescription());
                priceZonePrice.setPriority(p.getPriceZonePriority());
                priceZonePrice.setCode(p.getPriceZoneCode());
                priceZonePrice.setColor(p.getPriceZoneColor());
                priceZonePrice.setRestrictiveAccess(p.getPriceZoneRestrictiveAccess() != null ?
                        CommonUtils.isTrue(p.getPriceZoneRestrictiveAccess()) : null);

                List<VenuePriceTypeCommElement> ptces = priceTypeCommunicationElements.get(p.getPriceZoneId());
                if (!CommonUtils.isEmpty(ptces)) {
                    priceZonePrice.setTranslatedNames(new HashMap<>());
                    priceZonePrice.setTranslatedDescriptions(new HashMap<>());
                    for (VenuePriceTypeCommElement ptce : ptces) {
                        if (CommunicationElementType.NAME.name().equals(ptce.getType())) {
                            priceZonePrice.getTranslatedNames().put(ptce.getLang(), ptce.getValue());
                        }
                        if (CommunicationElementType.DESCRIPTION.name().equals(ptce.getType())) {
                            priceZonePrice.getTranslatedDescriptions().put(ptce.getLang(), ptce.getValue());
                        }
                    }
                }

                if (CollectionUtils.isEmpty(priceZonePrice.getRates())) {
                    priceZonePrice.setRates(new ArrayList<>());
                }
                Long rateId = p.getRateId().longValue();
                RatePrice ratePrice = priceZonePrice.getRates().stream()
                        .filter(rate -> rate.getId().equals(rateId))
                        .findFirst()
                        .orElseGet(() -> {
                            RatePrice rp = new RatePrice();
                            priceZonePrice.getRates().add(rp);
                            return rp;
                        });
                ratePrice.setId(rateId);
                ratePrice.setName(p.getRateName());
                ratePrice.setDefaultRate(p.getRateDefault());
                ratePrice.setPrice(p.getPrice());
            });
            event.setPrices(venueTemplatePrices);
        }
    }
}

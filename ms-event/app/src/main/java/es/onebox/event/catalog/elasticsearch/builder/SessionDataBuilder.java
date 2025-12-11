package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.entity.templateszones.dto.EntityTemplateZonesDTO;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;
import es.onebox.event.catalog.elasticsearch.dto.CustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneLimit;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateRestrictions;
import es.onebox.event.catalog.elasticsearch.dto.VirtualQueue;
import es.onebox.event.catalog.elasticsearch.dto.session.PresalesRedirectionPolicy;
import es.onebox.event.catalog.elasticsearch.dto.session.PresalesSettings;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRate;
import es.onebox.event.catalog.elasticsearch.dto.session.VenueProviderConfig;
import es.onebox.event.catalog.elasticsearch.dto.session.external.ExternalData;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleConfig;
import es.onebox.event.catalog.elasticsearch.enums.VirtualQueueVersion;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.sessions.domain.sessionconfig.PresalesRedirectionLinkMode;
import es.onebox.event.sessions.domain.sessionconfig.PriceTypeLimit;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionPresalesConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_SESSION;

public class SessionDataBuilder {

    private Session data;

    private StaticDataContainer staticDataContainer;
    private SessionForCatalogRecord sessionRecord;
    private CpanelEventoRecord eventRecord;
    private List<CpanelTarifaRecord> rates;
    private Long venueId;
    private IdNameCodeDTO externalVenue;
    private String timeZone;
    private Boolean isGraphic;
    private List<Long> promotions;
    private List<Long> relatedSeasonSessionIds;
    private Long venueConfigId;
    private IdNameCodeDTO externalVenueConfig;
    private Integer venueTemplateType;
    private List<VenueQuota> venueQuotas;
    private List<String> ipRestrictedCountries;
    private Map<Long, PriceZoneRestriction> priceZonesRestrictions;
    private Map<Long, RateRestrictions> ratesRestrictions;
    private Map<Long, PriceZoneLimit> priceZoneLimit;
    private CustomersLimits customersLimits;
    private VirtualQueue virtualQueue;
    private ProducerDTO promoter;
    private IdNameCodeDTO promoterCountrySubdivision;
    private Boolean isSmartBooking;
    private Long relatedSessionId;
    private List<PresaleConfig> presales;
    private VenueProviderConfig venueProviderConfig;
    private ExternalData externalData;
    private SessionLoyaltyPointsConfig loyaltyPointsConfig;
    private SessionPresalesConfig sessionPresalesConfig;
    private List<SessionTaxInfo> taxes;
    private List<SessionTaxInfo> invitationTaxes;
    private List<SessionTaxInfo> surchargeTaxes;
    private Map<Long, Set<EntityTemplateZonesDTO>> entityTemplatesZonesByPriceZoneId;

    private SessionDataBuilder(Session data) {
        this.data = data;
    }

    public static SessionDataBuilder builder() {
        return new SessionDataBuilder(new Session());
    }

    public static SessionDataBuilder builder(Session data) {
        return new SessionDataBuilder(data);
    }

    public static SessionData buildSessionData(Long sessionId, Long eventId) {
        SessionData sessionData = new SessionData();
        sessionData.setId(EventDataUtils.getSessionKey(sessionId));
        sessionData.setJoin(new JoinField(KEY_SESSION, EventDataUtils.getEventKey(eventId)));
        return sessionData;
    }

    public SessionData build() {
        Long sessionId = sessionRecord.getIdsesion().longValue();
        Long eventId = sessionRecord.getIdevento().longValue();

        SessionData sessionData = buildSessionData(sessionId, eventId);
        buildBasicSessionInfo();

        fillAdditionalSessionInfo();
        fillPromoterData();
        fillVenueInfo();

        sessionData.setSession(data);
        return sessionData;
    }

    public void buildBasicSessionInfo() {
        //SessionData
        data.setSessionId(sessionRecord.getIdsesion().longValue());
        data.setEventId(sessionRecord.getIdevento().longValue());
        data.setSessionName(sessionRecord.getNombre());
        data.setSessionStatus(sessionRecord.getEstado().byteValue());
        data.setReference(sessionRecord.getReference());
        data.setExternalReference(sessionRecord.getExternalreference());
        data.setBeginSessionDate(sessionRecord.getFechainiciosesion());
        data.setEndSessionDate(sessionRecord.getFechafinsesion());
        data.setRealEndSessionDate(sessionRecord.getFecharealfinsesion());
        data.setPublished(CommonUtils.isTrue(sessionRecord.getPublicado()));
        data.setPublishSessionDate(sessionRecord.getFechapublicacion());
        data.setBeginBookingDate(sessionRecord.getFechainicioreserva());
        data.setEndBookingDate(sessionRecord.getFechafinreserva());
        data.setBeginAdmissionDate(sessionRecord.getAperturaaccesos());
        data.setEndAdmissionDate(sessionRecord.getCierreaccesos());
        data.setSeasonPackSession(CommonUtils.isTrue(sessionRecord.getEsabono()));
        data.setShowDate(sessionRecord.getShowdate());
        data.setShowDateTime(sessionRecord.getShowdatetime());
        data.setShowUnconfirmedDate(CommonUtils.isTrue(sessionRecord.getShowunconfirmeddate()));
        data.setNoFinalDate(CommonUtils.isTrue(sessionRecord.getFechanodefinitiva()));
        data.setUseCaptcha(CommonUtils.isTrue(sessionRecord.getCaptcha()));
        data.setAllowPartialRefund(CommonUtils.isTrue(sessionRecord.getAllowpartialrefund()));
        data.setShowSchedule(CommonUtils.isTrue(sessionRecord.getMostrarhorario()));
        data.setCheckOrphanSeats(CommonUtils.isTrue(sessionRecord.getCheckorphanseats()));
        data.setSessionMaxTickets(sessionRecord.getNummaxlocalidadessesion() != null ? sessionRecord.getNummaxlocalidadessesion().longValue() : null);
        data.setOrderMaxTickets(sessionRecord.getNummaxlocalidadescompra() != null ? sessionRecord.getNummaxlocalidadescompra().longValue() : null);
        //EventData
        data.setEventName(eventRecord.getNombre());
        data.setEventStatus(eventRecord.getEstado().byteValue());
        data.setEntityId(eventRecord.getIdentidad().longValue());
        data.setProducerId(eventRecord.getIdpromotor().longValue());
        data.setEventType(eventRecord.getTipoevento().longValue());
        data.setEventSeasonType(eventRecord.getTipoabono());
    }

    public SessionDataBuilder sessionRecord(SessionForCatalogRecord sessionRecord) {
        this.sessionRecord = sessionRecord;
        return this;
    }

    public SessionDataBuilder eventRecord(CpanelEventoRecord eventRecord) {
        this.eventRecord = eventRecord;
        return this;
    }

    public SessionDataBuilder rates(List<CpanelTarifaRecord> rates) {
        this.rates = rates;
        return this;
    }

    public SessionDataBuilder venueId(Long venueId) {
        this.venueId = venueId;
        return this;
    }

    public SessionDataBuilder externalVenue(IdNameCodeDTO externalVenue) {
        this.externalVenue = externalVenue;
        return this;
    }

    public SessionDataBuilder timeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public SessionDataBuilder staticDataContainer(StaticDataContainer staticDataContainer) {
        this.staticDataContainer = staticDataContainer;
        return this;
    }

    public SessionDataBuilder promotions(List<Long> promotions) {
        this.promotions = promotions;
        return this;
    }

    public SessionDataBuilder relatedSeasonSessionIds(List<Long> relatedSeasonSessionIds) {
        this.relatedSeasonSessionIds = relatedSeasonSessionIds;
        return this;
    }

    public SessionDataBuilder venueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
        return this;
    }

    public SessionDataBuilder externalVenueConfig(IdNameCodeDTO externalVenueConfig) {
        this.externalVenueConfig = externalVenueConfig;
        return this;
    }

    public SessionDataBuilder venueQuotas(List<VenueQuota> quotas) {
        this.venueQuotas = quotas;
        return this;
    }

    public SessionDataBuilder promoter(ProducerDTO promoter) {
        this.promoter = promoter;
        return this;
    }

    public SessionDataBuilder priceZonesRestrictions(Map<Long, PriceZoneRestriction> pzr) {
        this.priceZonesRestrictions = pzr;
        return this;
    }

    public SessionDataBuilder ratesRestrictions(Map<Long, RateRestrictions> rr) {
        this.ratesRestrictions = rr;
        return this;
    }


    public SessionDataBuilder promoterCountrySubdivision(IdNameCodeDTO promoterCountrySubdivision) {
        this.promoterCountrySubdivision = promoterCountrySubdivision;
        return this;
    }

    public SessionDataBuilder ipRestrictedCountries(SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getRestrictions() != null && sessionConfig.getRestrictions().getCountryConfig() != null) {
            this.ipRestrictedCountries = sessionConfig.getRestrictions().getCountryConfig().getCountries();
        }
        return this;
    }

    public SessionDataBuilder priceZoneLimit(SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getPriceTypeLimits() != null) {
            this.priceZoneLimit = sessionConfig.getPriceTypeLimits().stream()
                    .collect(Collectors.toMap(PriceTypeLimit::getId,
                            v -> new PriceZoneLimit(v.getMax().longValue(),
                                    NumberUtils.zeroIfNull(v.getMin()).longValue())));
        }
        return this;
    }

    public SessionDataBuilder customersLimits(SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getCustomersLimits() != null) {
            CustomersLimits customersLimits = new CustomersLimits();
            if (sessionConfig.getCustomersLimits().getPriceTypeLimits() != null) {
                customersLimits.setPriceZoneLimit(sessionConfig.getCustomersLimits().getPriceTypeLimits().stream()
                        .collect(Collectors.toMap(PriceTypeLimit::getId,
                                v -> new PriceZoneLimit(v.getMax().longValue(),
                                        NumberUtils.zeroIfNull(v.getMin()).longValue()))));
                this.customersLimits = customersLimits;
            } else {
                customersLimits.setMin(sessionConfig.getCustomersLimits().getMin() != null ? sessionConfig.getCustomersLimits().getMin().longValue() : null);
                customersLimits.setMax(sessionConfig.getCustomersLimits().getMax() != null ? sessionConfig.getCustomersLimits().getMax().longValue() : null);
                this.customersLimits = customersLimits;
            }
        }
        return this;
    }

    public SessionDataBuilder virtualQueue(SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getQueueItConfig() != null) {
            VirtualQueue virtualQueue = new VirtualQueue();
            virtualQueue.setEnabled(sessionConfig.getQueueItConfig().isActive());
            virtualQueue.setAlias(sessionConfig.getQueueItConfig().getAlias());
            virtualQueue.setVersion(VirtualQueueVersion.getFromConfigValue(sessionConfig.getQueueItConfig().getVersion()));
            this.virtualQueue = virtualQueue;
        }
        return this;
    }

    public SessionDataBuilder isGraphic(Boolean isGraphic) {
        this.isGraphic = isGraphic;
        return this;
    }

    public SessionDataBuilder isSmartBooking(Boolean isSmartBooking) {
        this.isSmartBooking = isSmartBooking;
        return this;
    }

    public SessionDataBuilder relatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
        return this;
    }

    public SessionDataBuilder venueTemplateType(Integer venueTemplateType) {
        this.venueTemplateType = venueTemplateType;
        return this;
    }

    public SessionDataBuilder presales(List<PresaleConfig> presales) {
        this.presales = presales;
        return this;
    }

    public SessionDataBuilder venueProviderConfig(VenueProviderConfig venueProviderConfig) {
        this.venueProviderConfig = venueProviderConfig;
        return this;
    }

    public SessionDataBuilder externalData(ExternalData externalData) {
        this.externalData = externalData;
        return this;
    }

    public SessionDataBuilder loyaltyPointsConfig(SessionLoyaltyPointsConfig loyaltyPointsConfig) {
        this.loyaltyPointsConfig = loyaltyPointsConfig;
        return this;
    }

    public SessionDataBuilder sessionPresalesConfig(SessionConfig sessionConfig) {
        if (sessionConfig != null) {
            this.sessionPresalesConfig = sessionConfig.getSessionPresalesConfig();
        }
        return this;
    }

    public SessionDataBuilder taxes(List<SessionTaxInfo> taxes) {
        this.taxes = taxes;
        return this;
    }

    public SessionDataBuilder invitationTaxes(List<SessionTaxInfo> invitationTaxes) {
        this.invitationTaxes = invitationTaxes;
        return this;
    }

    public SessionDataBuilder surchargeTaxes(List<SessionTaxInfo> surchargeTaxes) {
        this.surchargeTaxes = surchargeTaxes;
        return this;
    }

    public SessionDataBuilder entityTemplatesZonesByPriceZoneId(Map<Long, Set<EntityTemplateZonesDTO>> entityTemplatesZonesByPriceZoneId){
        this.entityTemplatesZonesByPriceZoneId = entityTemplatesZonesByPriceZoneId;
        return this;
    }

    private void fillAdditionalSessionInfo() {
        if (CollectionUtils.isNotEmpty(rates)) {
            data.setRates(rates.stream().map(this::convert).collect(Collectors.toList()));
        }
        data.setTaxes(this.taxes);
        data.setInvitationTaxes(this.invitationTaxes);
        data.setSurchargesTaxes(this.surchargeTaxes);
        data.setPromotions(promotions);
        data.setRelatedSeasonSessionIds(this.relatedSeasonSessionIds);
        data.setPriceZoneLimit(this.priceZoneLimit);
        data.setCustomersLimits(this.customersLimits);
        data.setPriceZonesRestrictions(this.priceZonesRestrictions);
        data.setRatesRestrictions(this.ratesRestrictions);
        data.setIpRestrictedCountries(this.ipRestrictedCountries);
        data.setVirtualQueue(this.virtualQueue);
        data.setSmartBooking(this.isSmartBooking);
        data.setRelatedSessionId(this.relatedSessionId);
        data.setPresales(this.presales);
        data.setLoyaltyPointsConfig(this.loyaltyPointsConfig);
        data.setPresalesSettings(convert(this.sessionPresalesConfig));
        data.setExternalData(this.externalData);
        data.setEntityTemplatesZonesByPriceZoneId(this.entityTemplatesZonesByPriceZoneId);
    }

    private void fillPromoterData() {
        Entity result = new Entity();
        result.setId(promoter.getId());
        result.setOperatorId(promoter.getOperator().getId());
        result.setOperatorStatus(promoter.getOperator().getStatus().getState());
        result.setStatus(promoter.getStatus().getId());
        result.setName(promoter.getName());
        result.setCorporateName(promoter.getSocialReason());
        result.setFiscalCode(promoter.getNif());
        result.setAddress(promoter.getAddress());
        result.setCity(promoter.getCity());
        result.setPostalCode(promoter.getPostalCode());

        result.setCountryId(promoter.getCountryId());
        if (promoter.getCountryId() != null) {
            IdNameCodeDTO country = staticDataContainer.getCountry(promoter.getCountryId());
            if (country != null) {
                result.setCountryName(country.getName());
                result.setCountryCode(country.getCode());
            }
        }

        result.setCountrySubdivisionId(promoter.getCountrySubdivisionId());
        if (promoter.getCountrySubdivisionId() != null && promoterCountrySubdivision != null) {
            result.setCountrySubdivisionName(promoterCountrySubdivision.getName());
            result.setCountrySubdivisionCode(promoterCountrySubdivision.getCode());
        }

        data.setPromoter(result);
    }

    private void fillVenueInfo() {
        data.setVenueConfigId(this.venueConfigId);
        data.setExternalVenueConfig(this.externalVenueConfig);
        data.setVenueId(this.venueId);
        data.setExternalVenue(this.externalVenue);
        data.setVenueQuotas(this.venueQuotas);
        data.setVenueProviderConfig(this.venueProviderConfig);
        data.setVenueTemplateType(this.venueTemplateType);
        data.setGraphic(this.isGraphic);
        data.setTimeZone(this.timeZone);
    }

    private SessionRate convert(CpanelTarifaRecord record) {
        SessionRate rate = new SessionRate();
        Integer id = record.getIdtarifa();
        rate.setId(id == null ? null : id.longValue());
        rate.setName(record.getNombre());
        rate.setSessionId(sessionRecord.getIdsesion().longValue());
        rate.setDefaultRate(CommonUtils.isTrue(record.getDefecto()));
        rate.setRestrictiveAccess(record.getAccesorestrictivo() != null ? CommonUtils.isTrue(record.getAccesorestrictivo()) : null);
        rate.setPosition(record.getPosition());
        return rate;
    }

    private PresalesSettings convert(SessionPresalesConfig sessionPresalesConfig) {
        if (sessionPresalesConfig == null
                || sessionPresalesConfig.getPresalesRedirectionPolicy() == null) {
            return null;
        }

        PresalesRedirectionPolicy presalesRedirectionPolicy = new PresalesRedirectionPolicy();
        presalesRedirectionPolicy.setMode(
                sessionPresalesConfig.getPresalesRedirectionPolicy().getMode().equals(
                        PresalesRedirectionLinkMode.CATALOG)
                        ? es.onebox.event.catalog.elasticsearch.enums.PresalesRedirectionLinkMode.ORIGIN
                        : es.onebox.event.catalog.elasticsearch.enums.PresalesRedirectionLinkMode.CUSTOM);
        presalesRedirectionPolicy.setUrl(
                sessionPresalesConfig.getPresalesRedirectionPolicy().getValue());

        PresalesSettings presalesSettings = new PresalesSettings();
        presalesSettings.setRedirectPolicy(presalesRedirectionPolicy);
        return presalesSettings;
    }


}

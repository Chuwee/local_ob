package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateSessionsLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.CreateEventTierSaleGroup;
import es.onebox.mgmt.datasources.ms.event.dto.EventPassbookTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.Tier;
import es.onebox.mgmt.datasources.ms.event.dto.TierCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.TierExtended;
import es.onebox.mgmt.datasources.ms.event.dto.Tiers;
import es.onebox.mgmt.datasources.ms.event.dto.UpdateEventTierSaleGroup;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.EventCustomerType;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerTypes;
import es.onebox.mgmt.datasources.ms.event.dto.event.Attribute;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventRates;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import es.onebox.mgmt.datasources.ms.event.dto.event.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.event.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroup;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroupType;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestricted;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.RatesGroup;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateRateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketPresaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.EventSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SeasonTicketSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SessionSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.CloneSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalSessionConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.LinkedSession;
import es.onebox.mgmt.datasources.ms.event.dto.session.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionBulkUpdateResponse;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionExternalSessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionGroupConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionsGroups;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagRequest;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagResponse;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagsResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.entities.dto.AttributeRequestValuesDTO;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestriction;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictionCreate;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictions;
import es.onebox.mgmt.events.avetrestrictions.mapper.UpdateAvetSectorRestriction;
import es.onebox.mgmt.events.dto.TierChannelContentFilter;
import es.onebox.mgmt.export.enums.ExportType;
import es.onebox.mgmt.salerequests.dto.PriceTypeFilter;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsExportFilter;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.dto.SessionsGroupsSearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public EventsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public Event getEvent(Long eventId) {
        return msEventDatasource.getEvent(eventId);
    }

    @Cached(key = "events.event", expires = 10)
    public Event getCachedEvent(@CachedArg Long eventId) {
        return msEventDatasource.getEvent(eventId);
    }

    public Events getEvents(EventSearchFilter filter) {
        return msEventDatasource.getEvents(filter);
    }

    public Long create(CreateEventData eventData) {
        return msEventDatasource.createEvent(eventData);
    }

    public void updateEvent(Event updateEvent) {
        msEventDatasource.updateEvent(updateEvent);
    }

    public Sessions getSessions(Long userOperatorId, Long eventId, SessionSearchFilter filter) {
        return msEventDatasource.getSessions(userOperatorId, eventId, filter);
    }

    public Sessions getSessionsByEventIds(Long userOperatorId, List<Long> eventIds, SessionSearchFilter filter) {
        return msEventDatasource.getSessionsByEventIds(userOperatorId, eventIds, filter);
    }

    public Session getSessionWithoutEventId(Long sessionId) {
        return msEventDatasource.getSessionWithoutEventId(sessionId);
    }

    public Long createSession(Long eventId, CreateSessionData sessionData) {
        return msEventDatasource.createSession(eventId, sessionData);
    }

    public List<Long> createSessions(Long eventId, List<CreateSessionData> sessionsData) {
        return msEventDatasource.createSessions(eventId, sessionsData);
    }

    public Long cloneSession(Long eventId, Long sourceSessionId, CloneSessionData sessionData) {
        return msEventDatasource.cloneSession(eventId, sourceSessionId, sessionData);
    }

    public void updateSession(Long eventId, Session updateSession) {
        msEventDatasource.updateSession(eventId, updateSession);
    }

    public SessionBulkUpdateResponse updateSessions(Long eventId, List<Long> ids, Session sessionData, Boolean preview) {
        return msEventDatasource.updateSessions(eventId, ids, sessionData, preview);
    }

    public ExternalSessionConfig getSessionExternalSessions(Long sessionId) {
        return msEventDatasource.getSessionExternalSessions(sessionId);
    }

    public void updateSessionExternalSessions(Long sessionId, SessionExternalSessions updateSession) {
        msEventDatasource.updateSessionExternalSessions(sessionId, updateSession);
    }

    public List<Attribute> getEventAttributes(Long eventId) {
        return msEventDatasource.getEventAttributes(eventId);
    }

    public List<Attribute> getSessionAttributes(Long eventId, Long sessionId) {
        return msEventDatasource.getSessionAttributes(eventId, sessionId);
    }

    public List<VenueTemplatePrice> getVenueTemplatePrices(Long eventId, Long templateId, List<Long> sessionIdList, List<Integer> rateGroupList, List<Integer> rateGroupProductList) {
        return msEventDatasource.getVenueTemplatePrices(eventId, templateId, sessionIdList, rateGroupList, rateGroupProductList);
    }

    public void updateVenueTemplatePrices(Long eventId, Long templateId, List<VenueTemplatePrice> prices) {
        msEventDatasource.updateVenueTemplatePrices(eventId, templateId, prices);
    }

    public List<PreSaleConfigDTO> getSessionPreSale(Long eventId, Long sessionId){
        return msEventDatasource.getSessionPreSale(eventId, sessionId);
    }

    public PreSaleConfigDTO createSessionPreSale(Long eventId, Long sessionId, PreSaleConfigDTO preSale){
        return msEventDatasource.createSessionPreSale(eventId, sessionId, preSale);
    }

    public void updateSessionPreSale(Long eventId, Long sessionId, Long presalesId, PreSaleConfigDTO preSale){
        msEventDatasource.updateSessionPreSale(eventId, sessionId, presalesId, preSale);
    }

    public void deleteSessionPreSale(Long eventId, Long sessionId, Long presalesId){
        msEventDatasource.deleteSessionPreSale(eventId, sessionId, presalesId);
    }

    public List<SeasonTicketPresaleConfigDTO> getSeasonTicketPresale(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketPresale(seasonTicketId);
    }

    public SeasonTicketPresaleConfigDTO createSeasonTicketPresale(Long seasonTicketId, SeasonTicketPresaleConfigDTO preSale) {
        return msEventDatasource.createSeasonTicketPresale(seasonTicketId, preSale);
    }

    public void updateSeasonTicketPresale(Long seasonTicketId, Long presalesId, SeasonTicketPresaleConfigDTO preSale) {
        msEventDatasource.updateSeasonTicketPresale(seasonTicketId, presalesId, preSale);
    }

    public void deleteSeasonTicketPresale(Long seasonTicketId, Long presalesId) {
        msEventDatasource.deleteSeasonTicketPresale(seasonTicketId, presalesId);
    }

    public EventRates getEventRates(Long eventId) {
        return msEventDatasource.getEventRates(eventId.intValue());
    }

    public EventRates getSessionRates(Long eventId, Long sessionId) {
        return msEventDatasource.getSessionRates(eventId.intValue(), sessionId.intValue());
    }

    public Long createEventRate(Long eventId, Rate rate) {
        return msEventDatasource.createEventRate(eventId.intValue(), rate);
    }

    public void updateEventRate(Long eventId, Long rateId, Rate rate) {
        msEventDatasource.updateEventRate(eventId, rateId, rate);
    }

    public void updateEventRates(Long eventId, List<Rate> rates) {
        msEventDatasource.updateEventRates(eventId, rates);
    }

    public void deleteEventRate(Long eventId, Long rateId) {
        msEventDatasource.deleteEventRate(eventId, rateId);
    }

    public List<RateRestricted> getRestrictedRates(Long eventId) {
        return msEventDatasource.getRestrictedRates(eventId);
    }

    public List<IdNameCodeDTO> getRatesExternalTypes(Long eventId) {
        return msEventDatasource.getRatesExternalTypes(eventId);
    }

    public RateRestrictions getRateRestrictions(Long eventId, Long rateId) {
        return msEventDatasource.getEventRateRestrictions(eventId, rateId);
    }

    public void updateRateRestrictions(Long eventId, Long rateId, UpdateRateRestrictions restriction) {
        msEventDatasource.updateRateRestrictions(eventId, rateId, restriction);
    }

    public void deleteEventRateRestrictions(Long eventId, Long rateId) {
        msEventDatasource.deleteEventRateRestrictions(eventId, rateId);
    }

    public void setSurcharge(Long eventId, List<EventSurcharge> requests) {
        msEventDatasource.setSurcharge(eventId, requests);
    }

    public void deleteSurcharge(Long eventId) {
        msEventDatasource.deleteSurcharge(eventId);
    }

    public List<EventSurcharge> getSurcharges(Long eventId, List<SurchargeTypeDTO> types) {
        return msEventDatasource.getSurcharges(eventId, types);
    }

    public Tiers getEventTiers(Long eventId, Long venueTemplateId, Boolean active, Long limit, Long offset) {
        return msEventDatasource.getEventTiers(eventId, venueTemplateId, active, limit, offset);
    }

    public TierExtended getEventTier(Long eventId, Long tierId) {
        return msEventDatasource.getEventTier(eventId, tierId);
    }


    public Long createEventTier(Long eventId, Tier tier) {
        return msEventDatasource.createEventTier(eventId, tier);
    }

    public Tier updateEventTier(Long eventId, Long tierId, Tier tier) {
        return msEventDatasource.updateEventTier(eventId, tierId, tier);
    }

    public void deleteEventTier(Long eventId, Long tierId) {
        msEventDatasource.deleteEventTier(eventId, tierId);
    }

    public void deleteEventTierLimit(Long eventId, Long tierId) {
        msEventDatasource.deleteEventTierLimit(eventId, tierId);
    }

    public void createEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId, Integer limit) {
        CreateEventTierSaleGroup createEventTierSaleGroup = new CreateEventTierSaleGroup();
        createEventTierSaleGroup.setSaleGroupId(saleGroupId);
        createEventTierSaleGroup.setLimit(limit);
        msEventDatasource.createEventTierSaleGroup(eventId, tierId, createEventTierSaleGroup);
    }

    public void updateEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId, Integer limit) {
        UpdateEventTierSaleGroup updateEventTierSaleGroup = new UpdateEventTierSaleGroup();
        updateEventTierSaleGroup.setLimit(limit);
        msEventDatasource.updateEventTierSaleGroup(eventId, tierId, saleGroupId, updateEventTierSaleGroup);
    }

    public void deleteEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId) {
        msEventDatasource.deleteEventTierSaleGroup(eventId, tierId, saleGroupId);
    }

    public void upsertTierCommElements(Long eventId, Long tierId, List<TierCommunicationElement> commElements) {
        msEventDatasource.upsertTierCommElements(eventId, tierId, commElements);
    }

    public List<TierCommunicationElement> getTierCommElements(Long eventId, Long tierId, TierChannelContentFilter filter) {
        return msEventDatasource.getTierCommElements(eventId, tierId, filter);
    }

    public EventPassbookTemplates getEventPassbookTemplateCode(Long eventId) {
        return msEventDatasource.getEventPassbookTemplateCode(eventId);
    }

    public void putEventAttributes(Long eventId, AttributeRequestValuesDTO attributeRequestValuesDTO) {
        msEventDatasource.putEventAttributes(eventId, attributeRequestValuesDTO);
    }

    public void putSessionAttributes(Long eventId, Long sessionId, AttributeRequestValuesDTO attributeRequestValuesDTO) {
        msEventDatasource.putSessionAttributes(eventId, sessionId, attributeRequestValuesDTO);
    }

    public SessionGroupConfig getSessionGroup(Long eventId, Long sessionId) {
        return msEventDatasource.getSessionGroup(eventId, sessionId);
    }

    public void updateSessionGroupConfig(Long eventId, Long sessionId, SessionGroupConfig config) {
        msEventDatasource.updateSessionGroup(eventId, sessionId, config);
    }

    public void deleteSessionGroupConfig(Long eventId, Long sessionId) {
        msEventDatasource.deleteSessionGroup(eventId, sessionId);
    }

    public List<LinkedSession> getLinkedSessions(Long eventId, Long sessionId) {
        return msEventDatasource.getLinkedSessions(eventId, sessionId);
    }

    public PriceTypes getPriceTypes(Long eventId, PriceTypeFilter filter) {
        return msEventDatasource.getPriceTypes(eventId, filter);
    }

    public SessionsGroups getSessionsGroups(Long operatorId, Long eventId, SessionsGroupsSearchFilter filter) {
        return msEventDatasource.getSessionsGroups(operatorId, eventId, filter);
    }

    public VenueTemplatePriceTypeRestriction getVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId) {
        return msEventDatasource.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
    }

    public void resetEventVenueTemplatesPricesCurrency(Long eventId) {
        msEventDatasource.resetEventVenueTemplatesPricesCurrency(eventId);
    }

    public void upsertVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId, CreateVenueTemplatePriceTypeRestriction restrictions) {
        msEventDatasource.upsertVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, restrictions);
    }

    public void deleteVenueTemplatePriceTypeRestriction(Long eventId, Long templateId, Long priceTypeId) {
        msEventDatasource.deleteVenueTemplatePriceTypeRestriction(eventId, templateId, priceTypeId);
    }

    public List<IdNameDTO> getAllVenueTemplateRestrictions(Long eventId, Long templateId) {
        return msEventDatasource.getAllVenueTemplateRestrictions(eventId, templateId);
    }

    public RatesGroup getEventRatesGroup(Long eventId, RateGroupType type) {
        return msEventDatasource.getEventRatesGroup(eventId.intValue(), type);
    }

    public Long createEventRateGroup(Long eventId, RateGroup rate) {
        return msEventDatasource.createEventRateGroup(eventId.intValue(), rate);
    }

    public void updateEventRatesGroup(Long eventId, List<RateGroup> rates) {
        msEventDatasource.updateEventRatesGroup(eventId, rates);
    }

    public void updateEventRateGroup(Long eventId, Long rateId, RateGroup rate) {
        msEventDatasource.updateEventRateGroup(eventId, rateId, rate);
    }

    public void deleteEventRateGroup(Long eventId, Long rateId) {
        msEventDatasource.deleteEventRateGroup(eventId, rateId);
    }

    public ExportProcess generateSeasonTicketsRenewalsReport(Long seasonTicketId, SeasonTicketRenewalsExportFilter filter,
                                                             SeasonTicketRenewalFilter queryParams) {
        return msEventDatasource.generateRenewalsReport(seasonTicketId, filter, queryParams);
    }

    public ExportProcess generatePriceSimulationsReport(Long saleRequestId, PriceSimulationExportFilter filter) {
            return msEventDatasource.generatePriceSimulation(saleRequestId, filter);
    }

    public ExportProcess getExportStatus(String exportId, Long userId, ExportType exportType) {
        return msEventDatasource.getExportStatus(exportId, userId, exportType);
    }

    public String createAvetSectorRestriction(Long eventId, AvetSectorRestrictionCreate avetSectorRestrictionCreate) {
        return msEventDatasource.createAvetSectorRestriction(eventId, avetSectorRestrictionCreate);
    }

    public AvetSectorRestrictions getAvetSectorRestrictions(Long eventId) {
        return msEventDatasource.getAvetSectorRestrictions(eventId);
    }

    public AvetSectorRestriction getAvetSectorRestriction(Long eventId, String restrictionId) {
        return msEventDatasource.getAvetSectorRestriction(eventId, restrictionId);
    }

    public void updateAvetSectorRestriction(Long eventId, String restrictionId, UpdateAvetSectorRestriction updateAvetSectorRestriction) {
        msEventDatasource.updateAvetSectorRestriction(eventId, restrictionId, updateAvetSectorRestriction);
    }

    public void deleteAvetSectorRestriction(Long eventId, String restrictionId) {
        msEventDatasource.deleteAvetSectorRestriction(eventId, restrictionId);
    }

    public SessionTagsResponse getSessionTags(Long eventId, Long sessionId){
        return msEventDatasource.getSessionTags(eventId, sessionId);
    }

    public SessionTagResponse createSessionTag(Long eventId, Long sessionId, SessionTagRequest sessionTagRequest) {
        return msEventDatasource.createSessionTag(eventId, sessionId, sessionTagRequest);
    }

    public void updateSessionTag(Long eventId, Long sessionId, Long positionId, SessionTagRequest sessionTagRequest) {
       msEventDatasource.updateSessionTag(eventId, sessionId, positionId, sessionTagRequest);
    }

    public void deleteSessionTag(Long eventId, Long sessionId, Long positionId) {
        msEventDatasource.deleteSessionTag(eventId, sessionId, positionId);
    }

    public EventSecondaryMarketConfig getEventSecondaryMarketConfig(Long eventId) {
        return msEventDatasource.getEventSecondaryMarketConfig(eventId);
    }

    public void createEventSecondaryMarketConfig(Long eventId, EventSecondaryMarketConfig eventSecondaryMarketConfig) {
        msEventDatasource.createEventSecondaryMarketConfig(eventId, eventSecondaryMarketConfig);
    }

    public SeasonTicketSecondaryMarketConfig getSeasonTicketSecondaryMarketConfig(Long eventId) {
        return msEventDatasource.getSeasonTicketSecondaryMarketConfig(eventId);
    }

    public void createSeasonTicketSecondaryMarketConfig(Long eventId, SeasonTicketSecondaryMarketConfig seasonTicketSecondaryMarketConfig) {
        msEventDatasource.createSeasonTicketSecondaryMarketConfig(eventId, seasonTicketSecondaryMarketConfig);
    }

    public SessionSecondaryMarketConfig getSessionSecondaryMarketConfig(Long sessionId) {
        return msEventDatasource.getSessionSecondaryMarketConfig(sessionId);
    }

    public void createSessionSecondaryMarketConfig(Long sessionId, SessionSecondaryMarketConfig sessionSecondaryMarketConfig) {
        msEventDatasource.createSessionSecondaryMarketConfig(sessionId, sessionSecondaryMarketConfig);
    }

    public void deleteSessionSecondaryMarketConfig(Long sessionId) {
        msEventDatasource.deleteSessionSecondaryMarketConfig(sessionId);
    }

    public LoyaltyPointsConfig getLoyaltyPointsConfig(Long eventId, Long sessionId) {
        return msEventDatasource.getLoyaltyPointsConfig(eventId, sessionId);
    }

    public void updateLoyaltyPointsConfig(Long eventId, Long sessionId, UpdateSessionsLoyaltyPointsConfig updateSessionsLoyaltyPointsConfig) {
        msEventDatasource.updateLoyaltyPointsConfig(eventId, sessionId, updateSessionsLoyaltyPointsConfig);
    }

    public List<EventCustomerType> getEventCustomerTypes(Long eventId) {
        return msEventDatasource.getEventCustomerTypes(eventId);
    }
    public void putEventCustomerTypes(Long eventId, UpdateEventCustomerTypes dto) {
        msEventDatasource.putEventCustomerTypes(eventId, dto);
    }
}

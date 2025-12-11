package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.catalog.dao.couch.ChannelConfigCB;
import es.onebox.event.catalog.dao.couch.TemplateElementInfo;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dao.record.SessionTaxesForCatalogRecord;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantField;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.entity.templateszones.dto.EntityTemplateZonesDTO;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.packs.dao.domain.PackChannelItemsRecord;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelColectivoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventIndexationContext extends BaseIndexationContext<ChannelSessionForEventIndexation, ChannelSessionAgencyForEventIndexation> {

    private final CpanelEventoRecord event;
    private EventIndexationType type;

    private EventConfig eventConfig;
    private List<SessionForCatalogRecord> sessions;
    private List<SessionForCatalogRecord> allSessions;
    private List<SessionTaxesForCatalogRecord> allSessionTaxes;
    private List<SessionForCatalogRecord> deletedSessions;
    private EntityDTO entity;

    private Map<Long, SessionConfig> sessionConfigs;
    private List<CpanelCanalEventoRecord> channelEvents;
    private List<EventChannelForCatalogRecord> eventChannels;
    private List<VenueRecord> venues;
    private List<IdNameCodeDTO> externalVenues;
    private List<IdNameCodeDTO> externalVenueTemplates;
    private Map<Long, Long> venuesBySession;
    private Map<Long, Long> venueTemplatesBySession;
    private Map<Long, SessionDao.VenueTemplateInfo> venueTemplateInfos;
    private List<VenueTemplatePrice> venueTemplatePrices;
    private List<EventPriceRecord> prices;
    private List<EventPromotionRecord> promotions;
    private Map<Long, ChannelEventSurcharges> channelSurcharges;
    private CpanelSeasonTicketRecord seasonTicket;
    private EventAttendantsConfigDTO eventAttendantsConfig;
    private List<EventAttendantField> eventAttendantFields;
    private EventSecondaryMarketConfigDTO eventSecondaryMarketConfig;
    private Map<Long, Set<SessionRate>> ratesBySession;
    private Map<Long, Long> defaultRateBySession;
    private Map<Integer, List<Integer>> sessionPacksBySession;
    private Map<Integer, List<Integer>> sessionsBySessionPack;
    private Map<Integer, List<CpanelElementosComEventoRecord>> comElementsBySession;
    private Map<Integer, VenueDescriptor> venueDescriptor;
    private Long sessionFilter;
    private Map<String, List<Long>> channelSessionProducts;
    private Boolean useTiers;
    private Map<Long, List<PresaleRecord>> sessionPresaleConfigMap;
    private List<CpanelColectivoRecord> presaleCollectives;
    private Map<Integer, ChannelAttributes> channelAttributesByChannelId;
    private Map<Long, Map<Long, ChannelAgency>> channelAgencies;
    private Map<Integer, ChannelConfigCB> channelConfigsCB;
    private Map<Long, Map<Long, Set<String>>> templateElementInfoTags;
    private Map<Long, List<TemplateElementInfo>> templateElementInfoByTemplateId;
    private Map<Integer, EntityTemplateZonesDTO> templateZonesById;
    private List<PackChannelItemsRecord> relatedPacksItems;
    private Map<Integer, List<CpanelPackRecord>> packsBySession;
    private Map<Integer, ChannelPack> packsWithSessionFilterByPackId;


    public EventIndexationContext(CpanelEventoRecord event) {
        super(event);
        this.event = event;
        this.type = EventIndexationType.FULL;
        this.channelSurcharges = new HashMap<>();
    }

    public EventIndexationContext(CpanelEventoRecord event, EventIndexationType type) {
        super(event);
        this.event = event;
        this.type = type != null ? type : EventIndexationType.FULL;
        this.channelSurcharges = new HashMap<>();
    }

    @Override
    public CpanelEventoRecord getEvent() {
        return event;
    }

    public EventIndexationType getType() {
        return type;
    }

    public void setType(EventIndexationType type) {
        this.type = type;
    }

    public void setSessions(List<SessionForCatalogRecord> sessions) {
        this.sessions = sessions;
    }

    public List<SessionForCatalogRecord> getSessions() {
        return sessions;
    }

    public SessionForCatalogRecord getSession(Integer sessionId) {
        return sessions.stream().filter(s -> s.getIdsesion().equals(sessionId)).findAny().orElse(null);
    }

    public List<SessionForCatalogRecord> getAllSessions() {
        return allSessions;
    }

    public void setAllSessions(List<SessionForCatalogRecord> allSessions) {
        this.allSessions = allSessions;
    }

    public List<SessionTaxesForCatalogRecord> getAllSessionTaxes() { return allSessionTaxes; }

    public void setAllSessionTaxes(List<SessionTaxesForCatalogRecord> allSessionTaxes) { this.allSessionTaxes = allSessionTaxes; }

    public List<SessionForCatalogRecord> getDeletedSessions() {
        return deletedSessions;
    }

    public void setDeletedSessions(List<SessionForCatalogRecord> deletedSessions) {
        this.deletedSessions = deletedSessions;
    }

    public void setChannelEvents(List<CpanelCanalEventoRecord> channelEvents) {
        this.channelEvents = channelEvents;
    }

    public List<CpanelCanalEventoRecord> getChannelEvents() {
        return channelEvents;
    }

    public CpanelCanalEventoRecord getChannelEvent(Integer channelId) {
        return channelEvents.stream().filter(ec -> ec.getIdcanal().equals(channelId)).findAny().orElse(null);
    }

    public void setEventChannels(List<EventChannelForCatalogRecord> eventChannels) {
        this.eventChannels = eventChannels;
    }

    public List<EventChannelForCatalogRecord> getEventChannels() {
        return eventChannels;
    }

    public Optional<EventChannelForCatalogRecord> getEventChannel(Integer channelId) {
        return eventChannels.stream().filter(ec -> ec.getIdcanal().equals(channelId)).findAny();
    }

    public List<EventAttendantField> getEventAttendantFields() {
        return eventAttendantFields;
    }

    public void setEventAttendantFields(List<EventAttendantField> eventAttendantFields) {
        this.eventAttendantFields = eventAttendantFields;
    }

    public EventSecondaryMarketConfigDTO getEventSecondaryMarketConfig() {
        return eventSecondaryMarketConfig;
    }

    public void setEventSecondaryMarketConfig(EventSecondaryMarketConfigDTO eventSecondaryMarketConfig) {
        this.eventSecondaryMarketConfig = eventSecondaryMarketConfig;
    }

    public void setVenues(List<VenueRecord> venues) {
        this.venues = venues;
    }

    public List<VenueRecord> getVenues() {
        return venues;
    }

    public List<IdNameCodeDTO> getExternalVenues() {
        return externalVenues;
    }

    public void setExternalVenues(List<IdNameCodeDTO> externalVenues) {
        this.externalVenues = externalVenues;
    }

    public List<IdNameCodeDTO> getExternalVenueTemplates() {
        return externalVenueTemplates;
    }

    public void setExternalVenueTemplates(List<IdNameCodeDTO> externalVenueTemplates) {
        this.externalVenueTemplates = externalVenueTemplates;
    }

    public void setVenuesBySession(Map<Long, Long> venuesBySession) {
        this.venuesBySession = venuesBySession;
    }

    public Optional<VenueRecord> getVenueBySessionId(Long sessionId) {
        Long venueId = venuesBySession.get(sessionId);
        if (venueId == null || venues == null) {
            return Optional.empty();
        }
        return venues.stream().filter(venue -> venueId.equals(venue.getId())).findFirst();
    }

    public List<EventPriceRecord> getPrices() {
        return prices;
    }

    public void setPrices(List<EventPriceRecord> prices) {
        this.prices = prices;
    }

    public List<EventPromotionRecord> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<EventPromotionRecord> promotions) {
        this.promotions = promotions;
    }

    public List<VenueTemplatePrice> getVenueTemplatePrices() {
        return venueTemplatePrices;
    }

    public void setVenueTemplatePrices(List<VenueTemplatePrice> venueTemplatePrices) {
        this.venueTemplatePrices = venueTemplatePrices;
    }

    public Map<Long, ChannelEventSurcharges> getChannelSurcharges() {
        return channelSurcharges;
    }

    public void setChannelSurcharges(Map<Long, ChannelEventSurcharges> channelSurcharges) {
        this.channelSurcharges = channelSurcharges;
    }

    public CpanelSeasonTicketRecord getSeasonTicket() {
        return seasonTicket;
    }

    public void setSeasonTicket(CpanelSeasonTicketRecord seasonTicket) {
        this.seasonTicket = seasonTicket;
    }

    public void setEntity(EntityDTO entity) {
        this.entity = entity;
    }

    public EntityDTO getEntity() {
        return entity;
    }

    public EventAttendantsConfigDTO getEventAttendantsConfig() {
        return eventAttendantsConfig;
    }

    public void setEventAttendantsConfig(EventAttendantsConfigDTO eventAttendantsConfig) {
        this.eventAttendantsConfig = eventAttendantsConfig;
    }

    public Map<Long, Long> getVenueTemplatesBySession() {
        return venueTemplatesBySession;
    }

    public void setVenueTemplatesBySession(Map<Long, Long> venueTemplatesBySession) {
        this.venueTemplatesBySession = venueTemplatesBySession;
    }

    public Map<Long, SessionDao.VenueTemplateInfo> getVenueTemplateInfos() {
        return venueTemplateInfos;
    }

    public void setVenueTemplateInfos(Map<Long, SessionDao.VenueTemplateInfo> venueTemplateInfos) {
        this.venueTemplateInfos = venueTemplateInfos;
    }

    public Map<Long, Set<SessionRate>> getRatesBySession() {
        return ratesBySession;
    }

    public void setRatesBySession(Map<Long, Set<SessionRate>> ratesBySession) {
        this.ratesBySession = ratesBySession;
    }

    public Map<Long, Long> getDefaultRateBySession() {
        return defaultRateBySession;
    }

    public void setDefaultRateBySession(Map<Long, Long> defaultRateBySession) {
        this.defaultRateBySession = defaultRateBySession;
    }

    public Map<Integer, List<Integer>> getSessionPacksBySession() {
        return sessionPacksBySession;
    }

    public void setSessionPacksBySession(Map<Integer, List<Integer>> sessionPacksBySession) {
        this.sessionPacksBySession = sessionPacksBySession;
    }

    public Map<Integer, List<CpanelElementosComEventoRecord>> getComElementsBySession() {
        return comElementsBySession;
    }

    public void setComElementsBySession(Map<Integer, List<CpanelElementosComEventoRecord>> comElementsBySession) {
        this.comElementsBySession = comElementsBySession;
    }

    public Map<Integer, VenueDescriptor> getVenueDescriptor() {
        return venueDescriptor;
    }

    public void setVenueDescriptor(Map<Integer, VenueDescriptor> venueDescriptor) {
        this.venueDescriptor = venueDescriptor;
    }

    public Long getSessionFilter() {
        return sessionFilter;
    }

    public void setSessionFilter(Long sessionFilter) {
        this.sessionFilter = sessionFilter;
    }

    public Map<Long, SessionConfig> getSessionConfigs() {
        return sessionConfigs;
    }

    public void setSessionConfigs(Map<Long, SessionConfig> sessionConfigs) {
        this.sessionConfigs = sessionConfigs;
    }

    public EventConfig getEventConfig() {
        return eventConfig;
    }

    public void setEventConfig(EventConfig eventConfig) {
        this.eventConfig = eventConfig;
    }

    public Map<Long, Long> getVenuesBySession() {
        return venuesBySession;
    }

    public Map<String, List<Long>> getChannelSessionProducts() {
        return channelSessionProducts;
    }

    public void setChannelSessionProducts(Map<String, List<Long>> channelSessionProducts) {
        this.channelSessionProducts = channelSessionProducts;
    }

    public Boolean getUseTiers() {
        return useTiers;
    }

    public void setUseTiers(Boolean useTiers) {
        this.useTiers = useTiers;
    }

    public Map<Long, List<PresaleRecord>> getSessionPresaleConfigMap() {
        return sessionPresaleConfigMap;
    }

    public void setSessionPresaleConfigMap(Map<Long, List<PresaleRecord>> sessionPresaleConfigMap) {
        this.sessionPresaleConfigMap = sessionPresaleConfigMap;
    }

    public void setPresaleCollectives(List<CpanelColectivoRecord> presaleCollectives) {
        this.presaleCollectives = presaleCollectives;
    }

    public List<CpanelColectivoRecord> getPresaleCollectives() {
        return presaleCollectives;
    }

    public Map<Integer, ChannelAttributes> getChannelAttributesByChannelId() {
        return channelAttributesByChannelId;
    }

    public void setChannelAttributesByChannelId(Map<Integer, ChannelAttributes> channelAttributesByChannelId) {
        this.channelAttributesByChannelId = channelAttributesByChannelId;
    }

    public Map<Integer, List<Integer>> getSessionsBySessionPack() {
        return sessionsBySessionPack;
    }

    public void setSessionsBySessionPack(Map<Integer, List<Integer>> sessionsBySessionPack) {
        this.sessionsBySessionPack = sessionsBySessionPack;
    }

    public Map<Long, Map<Long, ChannelAgency>> getChannelsWithAgencies() {
        return channelAgencies;
    }

    public Map<Long, ChannelAgency> getChannelAgencies(Long channelId) {
        if (this.channelAgencies == null) {
            return null;
        }
        return this.channelAgencies.get(channelId);
    }

    public void setChannelsWithAgencies(Map<Long, Map<Long, ChannelAgency>> channelAgencies) {
        this.channelAgencies = channelAgencies;
    }

    public Map<Integer, ChannelConfigCB> getChannelConfigsCB() {
        return channelConfigsCB;
    }

    public void setChannelConfigsCB(Map<Integer, ChannelConfigCB> channelConfigsCB) {
        this.channelConfigsCB = channelConfigsCB;
    }


    public Map<Long, Map<Long, Set<String>>> getTemplateElementInfoTags() {
        return templateElementInfoTags;
    }

    public void setTemplateElementInfoTags(Map<Long, Map<Long, Set<String>>> templateElementInfoTags) {
        this.templateElementInfoTags = templateElementInfoTags;
    }

    public List<PackChannelItemsRecord> getRelatedPacksItems() {
        return relatedPacksItems;
    }

    public void setRelatedPacksItems(List<PackChannelItemsRecord> relatedPacksItems) {
        this.relatedPacksItems = relatedPacksItems;
    }

    public Map<Integer, List<CpanelPackRecord>> getPacksBySession() {
        return packsBySession;
    }

    public void setPacksBySession(Map<Integer, List<CpanelPackRecord>> packsBySession) {
        this.packsBySession = packsBySession;
    }

    public Map<Long, List<TemplateElementInfo>> getTemplateElementInfoByTemplateId() {
        return templateElementInfoByTemplateId;
    }

    public void setTemplateElementInfoByTemplateId(Map<Long, List<TemplateElementInfo>> templateElementInfoByTemplateId) {
        this.templateElementInfoByTemplateId = templateElementInfoByTemplateId;
    }

    public Map<Integer, EntityTemplateZonesDTO> getTemplateZonesById() {
        return templateZonesById;
    }

    public void setTemplateZonesById(Map<Integer, EntityTemplateZonesDTO> templateZonesById) {
        this.templateZonesById = templateZonesById;
    }

    public Map<Integer, ChannelPack> getPacksWithSessionFilterByPackId() {
        return packsWithSessionFilterByPackId;
    }

    public void setPacksWithSessionFilterByPackId(Map<Integer, ChannelPack> packsWithSessionFilterByPackId) {
        this.packsWithSessionFilterByPackId = packsWithSessionFilterByPackId;
    }
}
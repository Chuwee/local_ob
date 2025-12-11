package es.onebox.event.catalog.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.converter.CatalogPriceSimulationConverter;
import es.onebox.event.catalog.converter.ChannelCatalogSessionConverter;
import es.onebox.event.catalog.dao.ChannelSessionPriceCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import es.onebox.event.catalog.dto.ChannelCatalogEventDetailDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventSessionsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogEventsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogProductsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogSessionDetailDTO;
import es.onebox.event.catalog.dto.ChannelCatalogSessionsResponse;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogProductsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogTypeFilter;
import es.onebox.event.catalog.dto.price.CatalogVenueConfigPricesSimulationDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionWithParent;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.catalog.dto.presales.PresaleResolverType;
import jakarta.validation.Valid;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ChannelCatalogService {

    private final EventPromotionCouchDao eventPromotionCouchDao;
    private final ChannelSessionPriceCouchDao channelSessionPriceCouchDao;
    private final ChannelCatalogAgencyService channelCatalogAgencyService;
    private final ChannelCatalogDefaultService channelCatalogDefaultService;
    private final ChannelsRepository channelsRepository;
    private final String s3domain;
    private final String fileBasePath;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final PresaleProviderResolverFactory presaleProviderResolverFactory;

    @Autowired
    public ChannelCatalogService(@Value("${onebox.repository.S3SecureUrl}") String s3domain,
                                 @Value("${onebox.repository.fileBasePath}") String fileBasePath,
                                 ChannelsRepository channelsRepository,
                                 EventPromotionCouchDao eventPromotionCouchDao,
                                 ChannelSessionPriceCouchDao channelSessionPriceCouchDao,
                                 ChannelCatalogAgencyService channelCatalogAgencyService,
                                 ChannelCatalogDefaultService channelCatalogDefaultService,
                                 EventConfigCouchDao eventConfigCouchDao,
                                 PresaleProviderResolverFactory presaleProviderResolverFactory) {
        this.presaleProviderResolverFactory = presaleProviderResolverFactory;
        this.s3domain = s3domain;
        this.fileBasePath = fileBasePath;
        this.channelsRepository = channelsRepository;
        this.eventPromotionCouchDao = eventPromotionCouchDao;
        this.channelSessionPriceCouchDao = channelSessionPriceCouchDao;
        this.channelCatalogAgencyService = channelCatalogAgencyService;
        this.channelCatalogDefaultService = channelCatalogDefaultService;
        this.eventConfigCouchDao = eventConfigCouchDao;
    }

    public ChannelCatalogEventsResponse searchEvents(final Long channelId, ChannelCatalogEventsFilter filter) {
        filter = Optional.ofNullable(filter).orElse(new ChannelCatalogEventsFilter());
        validateChannel(channelId, filter);
        return from(filter).searchEvents(channelId, filter);

    }

    public ChannelCatalogEventDetailDTO getEventDetail(final Long channelId, final Long eventId, final ChannelCatalogTypeFilter filter) {
        validateChannel(channelId, filter);
        Adapter service = from(filter);
        return service.getEventDetail(channelId, eventId, filter);
    }

    public ChannelCatalogEventSessionsResponse searchSessions(final Long channelId, final Long eventId, ChannelCatalogEventSessionsFilter filter) {
        filter = Optional.ofNullable(filter).orElse(new ChannelCatalogEventSessionsFilter());
        validateChannel(channelId, filter);
        Adapter service = from(filter);
        EventData eventData = new EventData();
        eventData.setEvent(service.getCatalogEvent(eventId));
        return from(filter).searchSessions(channelId, eventId, filter, eventData);
    }

    public ChannelCatalogSessionsResponse searchSessions(final Long channelId, ChannelCatalogSessionsFilter filter) {
        filter = Optional.ofNullable(filter).orElse(new ChannelCatalogSessionsFilter());
        validateChannel(channelId, filter);
        return from(filter).searchSessions(channelId, filter);
    }

    public ChannelCatalogSessionDetailDTO getSessionDetail(final Long channelId, Long eventId, final Long sessionId, final ChannelCatalogTypeFilter filter) {
        ChannelConfigDTO channelConfig = validateChannel(channelId, filter);
        Adapter service = from(filter);
        Long agencyId = resolveAgencyId(filter);
        ChannelSession channelSession = service.getCatalogChannelSession(channelId, sessionId, agencyId);
        eventId = Optional.ofNullable(eventId).orElse(channelSession.getEventId());
        Event event = service.getCatalogEvent(eventId);
        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        Session session = service.getCatalogSession(sessionId);
        PresaleProviderResolver presaleProviderResolver = presaleProviderResolverFactory.getPresaleResolver(
                PresaleResolverType.getByProvider(EventUtils.getInventoryProvider(eventConfig))
        );
        Map<Long, Provider> presaleProviders = presaleProviderResolver.resolvePresaleProviders(session);
        ChannelEvent channelEvent = service.getCatalogChannelEvent(channelId, eventId, agencyId);
        EventSecondaryMarketConfigDTO eventSecondaryMarketConfig = service.getEventSecondaryMarketConfig(eventId, channelSession);
        EventData eventData = new EventData();
        eventData.setEvent(event);
        SessionData sessionData = new SessionData();
        sessionData.setSession(session);
        ChannelSessionWithParent channelSessionWithParent = new ChannelSessionWithParent();
        channelSessionWithParent.setChannelSession(channelSession);
        channelSessionWithParent.setSessionData(sessionData);
        return ChannelCatalogSessionConverter.convert(channelSessionWithParent, eventData, channelEvent,
                service.getS3Repository(s3domain, fileBasePath), eventPromotionCouchDao, eventSecondaryMarketConfig, channelConfig, presaleProviders);
    }

    public CatalogVenueConfigPricesSimulationDTO getPriceSimulationByChannelAndSession(final Long channelId, final Long sessionId, final ChannelCatalogTypeFilter filter) {
        validateChannel(channelId, filter);
        ChannelSessionPricesDocument doc;
        Long agencyId = resolveAgencyId(filter);
        doc = channelSessionPriceCouchDao.get(channelId, sessionId);
        if (doc == null || doc.getSimulation() == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_SESSION_PRICES_NOT_FOUND).
                    setMessage("ChannelSessionPrices not found for channel: " + channelId + " - session: " + sessionId).build();
        }
        ChannelSessionAgency csa = null;
        if (agencyId != null) {
            csa = this.channelCatalogAgencyService.getCatalogChannelSession(channelId, sessionId, agencyId);
        }
        return CatalogPriceSimulationConverter.convertToDTO(doc, csa);
    }

    // WARNING - This is a first approach to a channel product catalog and it is still in development. Avoid its usage until it is finished
    public ChannelCatalogProductsResponse searchProducts(Long channelId, @Valid ChannelCatalogProductsFilter filter) {
        validateChannelConfig(channelId);
        return channelCatalogDefaultService.searchProducts(channelId, filter);
    }

    private ChannelConfigDTO validateChannel(final Long channelId, final ChannelCatalogTypeFilter filter) {
        ChannelConfigDTO channelConfig = validateChannelConfig(channelId);
        if (ChannelSubtype.PORTAL_B2B.getIdSubtipo() == channelConfig.getChannelType() && (filter == null
                || (filter.getAgencyId() == null && BooleanUtils.isNotTrue(filter.getForceRootChannel())))) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_AGENCY_ID_MANDATORY);
        }
        return channelConfig;
    }

    private ChannelConfigDTO validateChannelConfig(Long channelId) {
        ChannelConfigDTO channelConfig = this.channelsRepository.getChannelConfigCached(channelId);
        if (channelConfig == null) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND);
        }
        return channelConfig;
    }

    private Adapter from(ChannelCatalogTypeFilter filter) {
        return filter != null && filter.getAgencyId() != null ? channelCatalogAgencyService : channelCatalogDefaultService;
    }

    private static Long resolveAgencyId(ChannelCatalogTypeFilter filter) {
        return filter != null && filter.getAgencyId() != null ? filter.getAgencyId() : null;
    }
}

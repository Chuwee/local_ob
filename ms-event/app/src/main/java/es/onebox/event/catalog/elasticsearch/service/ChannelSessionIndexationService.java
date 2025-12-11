package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.catalog.dao.SBSessionsCouchDao;
import es.onebox.event.catalog.dao.couch.smartbooking.SBSession;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionAgencyForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionAgencyForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionPriceZones;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionAgencyElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItem;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.utils.CatalogPublishableUtils;
import es.onebox.event.catalog.elasticsearch.utils.IndexerUtils;
import es.onebox.event.catalog.elasticsearch.utils.PresaleConfigUtil;
import es.onebox.event.catalog.utils.EventContextUtils;
import es.onebox.event.catalog.utils.VenueDescriptorUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SectorOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationsSearchRequest;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionVenueContainerSearchRequest;
import es.onebox.event.datasources.ms.ticket.enums.CapacityType;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SessionOccupationRepository;
import es.onebox.event.datasources.ms.ticket.repository.SessionRepository;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelSurchargesTaxesOrigin;
import es.onebox.event.events.enums.EventChannelSurchargesTaxesOrigin;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.packs.dao.domain.PackChannelItemsRecord;
import es.onebox.event.packs.dto.PackItemDTO;
import es.onebox.event.packs.dto.RelatedPackDTO;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.event.secondarymarket.converter.EventSecondaryMarketConverter;
import es.onebox.event.secondarymarket.converter.SessionSecondaryMarketConverter;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.dao.SessionSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketConfig;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChannelSessionIndexationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSessionIndexationService.class);

    private final SessionElasticDao sessionElasticDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final SessionRateDao sessionRateDao;
    private final SeasonSessionDao seasonSessionDao;
    private final EventCommunicationElementDao eventCommElemDao;
    private final SessionRepository sessionRepository;
    private final SessionOccupationRepository sessionOccupationRepository;
    private final ChannelSessionElasticDao channelSessionElasticDao;
    private final AttendantsConfigService attendantsConfigService;
    private final SBSessionsCouchDao sbSessionsCouchDao;
    private final SessionSecondaryMarketConfigCouchDao sessionSecondaryMarketConfigCouchDao;
    private final EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao;
    private final ChannelSessionAgencyElasticDao channelAgencySessionElasticDao;

    @Autowired
    public ChannelSessionIndexationService(SessionElasticDao sessionElasticDao,
                                           SessionConfigCouchDao sessionConfigCouchDao,
                                           SessionRateDao sessionRateDao,
                                           SeasonSessionDao seasonSessionDao,
                                           EventCommunicationElementDao eventCommElemDao,
                                           SessionRepository sessionRepository,
                                           SessionOccupationRepository sessionOccupationRepository,
                                           ChannelSessionElasticDao channelSessionElasticDao,
                                           AttendantsConfigService attendantsConfigService,
                                           SBSessionsCouchDao sbSessionsCouchDao,
                                           SessionSecondaryMarketConfigCouchDao sessionSecondaryMarketConfigCouchDao,
                                           EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao,
                                           ChannelSessionAgencyElasticDao channelAgencySessionElasticDao) {
        this.sessionElasticDao = sessionElasticDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.sessionRateDao = sessionRateDao;
        this.seasonSessionDao = seasonSessionDao;
        this.eventCommElemDao = eventCommElemDao;
        this.sessionRepository = sessionRepository;
        this.sessionOccupationRepository = sessionOccupationRepository;
        this.channelSessionElasticDao = channelSessionElasticDao;
        this.attendantsConfigService = attendantsConfigService;
        this.sbSessionsCouchDao = sbSessionsCouchDao;
        this.sessionSecondaryMarketConfigCouchDao = sessionSecondaryMarketConfigCouchDao;
        this.eventSecondaryMarketConfigCouchDao = eventSecondaryMarketConfigCouchDao;
        this.channelAgencySessionElasticDao = channelAgencySessionElasticDao;
    }

    public void prepareChannelSessionsToIndex(EventIndexationContext ctx) {
        List<CpanelCanalEventoRecord> channels = getPublishableChannels(ctx);
        fillSessionsBulkInfo(ctx);
        for (SessionForCatalogRecord s : ctx.getSessions()) {
            if (CatalogPublishableUtils.isSessionToIndex(s)) {
                    Long sessionId = s.getIdsesion().longValue();
                    VenueDescriptor venueDescriptor = EventContextUtils.getVenueDescriptorBySessionId(ctx, sessionId);
                    Integer defaultQuotaId = VenueDescriptorUtils.getDefaultQuota(venueDescriptor);
                    Boolean isActivity = EventUtils.isActivityTemplate(venueDescriptor.getType());
                    Boolean isSeasonSession = ConverterUtils.isByteAsATrue(s.getEsabono());

                    CapacityType type = isActivity ? CapacityType.SIMPLE : CapacityType.NORMAL;
                    List<Long> sessionQuotas = sessionRepository.getSessionQuotas(sessionId, type, isSeasonSession);
                    SessionAttendantsConfigDTO sessionAttendants = attendantsConfigService.getSessionAttendantsConfig(sessionId);
                    SessionSecondaryMarketConfigDTO sessionSecondaryMarketConfig = getSessionSecondaryMarketConfig(ctx, sessionId);

                    ChannelSessionIndexationContext sessionContext = ChannelSessionIndexationContext
                            .ChannelSessionIndexationContextBuilder.builder()
                            .defaultQuotaId(defaultQuotaId.longValue())
                            .isActivity(isActivity)
                            .venueDescriptor(venueDescriptor)
                            .sessionAttendants(sessionAttendants)
                            .sessionSecondaryMarketConfig(sessionSecondaryMarketConfig)
                            .sessionQuotas(sessionQuotas)
                            .build();

                    List<ChannelSessionForEventIndexation> channelSessions = buildChannelSessionForEventContext(ctx, sessionContext, s, channels);
                    ctx.getChannelSessionsToIndex().addAll(channelSessions);

                    List<ChannelSessionAgencyForEventIndexation> channelAgencySessions = buildChannelAgencySessionsForEventContext(ctx, sessionContext, s, channels);
                    if (CollectionUtils.isNotEmpty(channelAgencySessions)) {
                        ctx.getChannelAgencySessionsToIndex().addAll(channelAgencySessions);
                    }
            }
        }
        fillPriceZones(ctx.getChannelSessionsToIndex(), ctx);
        fillPriceZones(ctx.getChannelAgencySessionsToIndex(), ctx);
    }

    public void prepareComElementsChannelSessionsToIndex(EventIndexationContext ctx) {
        List<Long> sessionIds = ctx.getSessions().stream().map(s -> s.getIdsesion().longValue()).toList();
        List<CpanelElementosComEventoRecord> comElements = eventCommElemDao.getEventCommunicationElementsBySessionIds(sessionIds);
        ctx.setComElementsBySession(comElements.stream().collect(Collectors.groupingBy(CpanelElementosComEventoRecord::getIdsesion)));
    }

    public void prepareChannelSessionsToIndex(OccupationIndexationContext ctx) {
        Long eventId = ctx.getEventId();
        Long sessionId = ctx.getSessionId();
        List<ChannelSessionData> channelSessions;
        if (sessionId == null) {
            channelSessions = channelSessionElasticDao.getByEventId(eventId);
        } else {
            channelSessions = channelSessionElasticDao.getBySessionId(sessionId, eventId);
        }

        buildChannelSessionsForOccupationContext(ctx, channelSessions);
        buildChannelAgencySessionsForOccupationContext(ctx, sessionId, eventId);
    }

    private List<ChannelSessionForEventIndexation> buildChannelSessionForEventContext(EventIndexationContext ctx,
                                                                                      ChannelSessionIndexationContext sessionContext,
                                                                                      SessionForCatalogRecord session,
                                                                                      List<CpanelCanalEventoRecord> channels) {

        Map<String, List<SessionOccupationVenueContainer>> venueOccupationCache = new HashMap<>();
        return channels.stream().map(channelEvent -> {
            Long channelId = channelEvent.getIdcanal().longValue();
            ChannelInfo channelInfo = ctx.getChannelInfo(channelId);
            List<Long> quotas = ctx.getQuotasByChannel(channelId);
            if (Boolean.TRUE.equals(sessionContext.getActivity()) && CollectionUtils.isEmpty(quotas)) {
                quotas = List.of(sessionContext.getDefaultQuotaId());
            }
            if (invalidQuotas(quotas, sessionContext.getSessionQuotas())) {
                return null;
            }
            ChannelSessionForEventIndexation channelSession = new ChannelSessionForEventIndexation();
            fillChannelSessionInfo(ctx, sessionContext, session, channelEvent, channelSession, channelInfo, quotas);
            fillProducts(ctx, channelSession);
            fillRelatedPacks(ctx, channelSession, channelId);
            fillSessionVenueDescriptor(channelSession, ctx, venueOccupationCache);
            return channelSession;
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }

    private static List<ChannelTaxInfo> getChannelSurchargesTaxes(EventIndexationContext ctx, Long channelId, ChannelInfo channelInfo, SessionForCatalogRecord session) {
        EventChannelForCatalogRecord eventChannel = ctx.getEventChannel(channelId.intValue()).orElse(null);
        if (channelInfo == null || eventChannel == null) {
            return List.of();
        }

        EventChannelSurchargesTaxesOrigin ecOrigin = EventChannelSurchargesTaxesOrigin.getById(eventChannel.getSurchargestaxesorigin());
        if (ecOrigin == null) {
            return List.of();
        }
        return switch (ecOrigin) {
            case SALE_REQUEST -> eventChannel.getSurchargesTaxes();
            case CHANNEL -> getTaxesFromChannel(channelInfo, session);
            case EVENT -> buildSessionChannelTaxes(session);
        };
    }

    private static List<ChannelTaxInfo> getTaxesFromChannel(ChannelInfo channelInfo, SessionForCatalogRecord session) {
        ChannelSurchargesTaxesOrigin cOrigin = channelInfo.getSurchargesTaxesOrigin();
        if (cOrigin == null) {
            return List.of();
        }
        return switch (cOrigin) {
            case CHANNEL -> channelInfo.getSurchargesTaxes() == null ? List.of() : channelInfo.getSurchargesTaxes();
            case EVENT -> buildSessionChannelTaxes(session);
        };
    }

    private static List<ChannelTaxInfo> buildSessionChannelTaxes(SessionForCatalogRecord session) {
        if (session == null || session.getSurchargesTaxId() == null) {
            return List.of();
        }
        ChannelTaxInfo taxInfo = TaxSimulationUtils.createTaxInfo(
                session.getSurchargesTaxId(),
                session.getSurchargesTaxValue(),
                session.getSurchargesTaxName(),
                ChannelTaxInfo::new
        );
        return List.of(taxInfo);
    }

    private void fillRelatedPacks(EventIndexationContext ctx, ChannelSessionForEventIndexation channelSession, Long channelId) {
        if (CollectionUtils.isNotEmpty(ctx.getRelatedPacksItems())) {
            var relatedPacks = ctx.getRelatedPacksItems().stream()
                    .filter(packsChannel -> packsChannel.getIdcanal().equals(channelId.intValue())).toList();
            channelSession.setRelatedPacksByPackId(buildRelatedPacksByPackIdWithItems(channelSession.getSessionId(),
                    relatedPacks, ctx.getPacksBySession(), ctx.getPacksWithSessionFilterByPackId()));
        }
    }

    private Map<Long, RelatedPackDTO> buildRelatedPacksByPackIdWithItems(Long sessionId, List<PackChannelItemsRecord> in,
                                                                         Map<Integer, List<CpanelPackRecord>> packDetails,
                                                                         Map<Integer, ChannelPack> packsWithSessionFilterByPackId) {
        Map<Long, RelatedPackDTO> relatedPacks = new HashMap<>();
        Map<Long, List<PackChannelItemsRecord>> packChannelItemsByPackId = in.stream()
                        .collect(Collectors.groupingBy(pack -> pack.getIdpack().longValue()));

        for (Map.Entry<Long, List<PackChannelItemsRecord>> entry : packChannelItemsByPackId.entrySet()) {
            Long pack = entry.getKey();
            List<PackChannelItemsRecord> packItemRecords = entry.getValue();

                CpanelPackRecord cpanelPackRecord = packDetails.values().stream()
                        .flatMap(List::stream)
                        .filter(cpanelPack -> cpanelPack.getIdpack().equals(pack.intValue()))
                        .findFirst().orElse(null);
                if (cpanelPackRecord != null) {
                    if (MapUtils.isNotEmpty(packsWithSessionFilterByPackId) && packsWithSessionFilterByPackId.containsKey(pack.intValue())) {
                        ChannelPack sessionFilter = packsWithSessionFilterByPackId.get(pack.intValue());
                        if (!sessionFilter.getItems().stream().map(ChannelPackItem::getItemId).collect(Collectors.toSet()).contains(sessionId)) {
                            continue; //if sessionId not in sub sessions pack, then not added to relatedPacks Map
                        }
                    }
                    RelatedPackDTO relatedPack = new RelatedPackDTO();

                    var packItemRecord = packItemRecords.get(0);

                    relatedPack.setName(cpanelPackRecord.getNombre());
                    Boolean suggested = ConverterUtils.isByteAsATrue(packItemRecord.getSugerirpack());
                    if (Boolean.TRUE.equals(suggested)) {
                        relatedPack.setSuggested(ConverterUtils.isByteAsATrue(packItemRecord.getSugerirpack()));
                    }

                    Boolean onSaleForLoggedUsers = ConverterUtils.isByteAsATrue(packItemRecord.getOnsaleforloggedusers());
                    if (Boolean.TRUE.equals(onSaleForLoggedUsers)) {
                        relatedPack.setOnSaleForLoggedUsers(true);
                    }

                    List<PackItemDTO> packItems = new ArrayList<>();
                    for (PackChannelItemsRecord packItem : packItemRecords) {
                        PackItemDTO item = new PackItemDTO();
                        item.setItemId(packItem.getItemId());
                        item.setName(packItem.getItemName());
                        item.setType(PackItemType.getById(packItem.getItemType()));

                        if (BooleanUtils.isTrue(packItem.getMainItem())) {
                            item.setMain(true);
                        }
                        packItems.add(item);
                    }

                    relatedPack.setItems(packItems);
                    relatedPacks.put(packItemRecord.getIdpack().longValue(), relatedPack);
                }
        }
        return relatedPacks;
    }

    private List<ChannelSessionAgencyForEventIndexation> buildChannelAgencySessionsForEventContext(EventIndexationContext ctx,
                                                                                                   ChannelSessionIndexationContext sessionContext,
                                                                                                   SessionForCatalogRecord session,
                                                                                                   List<CpanelCanalEventoRecord> channels) {
        if (MapUtils.isEmpty(ctx.getChannelsWithAgencies())) {
            return null;
        }
        Map<String, List<SessionOccupationVenueContainer>> venueOccupationCache = new HashMap<>();

        return channels.stream().filter(c -> includeB2B(c, ctx)).map(channelEvent -> {
            Long channelId = channelEvent.getIdcanal().longValue();
            if (!ctx.getChannelsWithAgencies().containsKey(channelId)) {
                return null;
            }
            Map<Long, ChannelAgency> agencies = ctx.getChannelsWithAgencies().get(channelId);

            return agencies.values().stream().map(agency -> {
                ChannelInfo channelInfo = ctx.getChannelInfo(channelId);
                Long agencyId = agency.getId();
                List<Long> sessionQuotas = sessionContext.getSessionQuotas();
                List<Long> channelQuotas = ctx.getQuotasByChannel(channelId);
                List<Long> quotas;
                if (Boolean.TRUE.equals(sessionContext.getActivity()) && CollectionUtils.isEmpty(channelQuotas) && Boolean.TRUE.equals(agency.getAllQuotas())) {
                    quotas = List.of(sessionContext.getDefaultQuotaId());
                } else {
                    if (BooleanUtils.isTrue(agency.getAllQuotas())) {
                        quotas = CollectionUtils.isNotEmpty(channelQuotas) && CollectionUtils.isNotEmpty(sessionQuotas) ?
                                sessionQuotas.stream().filter(channelQuotas::contains).toList() : sessionQuotas;
                    } else {
                        quotas = CollectionUtils.isNotEmpty(channelQuotas) && CollectionUtils.isNotEmpty(agency.getQuotas()) ?
                                agency.getQuotas().stream().filter(channelQuotas::contains).toList() : agency.getQuotas();
                    }
                }

                if (invalidQuotas(quotas, sessionQuotas)) {
                    return null;
                }
                ChannelSessionAgencyForEventIndexation cs = new ChannelSessionAgencyForEventIndexation();
                cs.setAgencyId(agencyId);
                fillChannelSessionInfo(ctx, sessionContext, session, channelEvent, cs, channelInfo, quotas);
                fillProducts(ctx, cs);
                fillRelatedPacks(ctx, cs, channelInfo.getId());
                fillSessionVenueDescriptor(cs, ctx, venueOccupationCache);
                return cs;
            }).filter(Objects::nonNull).collect(Collectors.toList());

        }).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());
    }


    private void buildChannelSessionsForOccupationContext(OccupationIndexationContext ctx, List<ChannelSessionData> channelSessions) {
        if (CollectionUtils.isEmpty(channelSessions)) {
            return;
        }

        Map<String, List<SessionOccupationVenueContainer>> venueOccupationCache = new HashMap<>();
        boolean checkSB = EventType.AVET.equals(ctx.getEventType());
        Set<Long> sbSessionIds = new HashSet<>();
        var channelSessionsToIndex = channelSessions.stream()
                .map(channelSessionData -> {
                    ChannelSession doc = channelSessionData.getChannelSession();
                    ChannelSessionForOccupationIndexation idx = buildChannelSessionForOccupationContext(ctx, doc, new ChannelSessionForOccupationIndexation(), extractQuotasByChannel(ctx, doc), venueOccupationCache);
                    if (checkSB && EventType.ACTIVITY.getId().equals(idx.getVenueTemplateType())) {
                        sbSessionIds.add(channelSessionData.getChannelSession().getSessionId());
                    }
                    return idx;
                }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(sbSessionIds)) {
            Map<Long, SBSession> sbSessions = sbSessionsCouchDao.bulkGet(sbSessionIds).stream().collect(Collectors.toMap(v -> v.getSessionId().longValue(), Function.identity()));
            ctx.setSbBySession(sbSessions);
        }

        fillPriceZones(channelSessionsToIndex, ctx);
        ctx.getChannelSessionsToIndex().addAll(channelSessionsToIndex);
    }


    private void buildChannelAgencySessionsForOccupationContext(OccupationIndexationContext ctx, Long sessionId, Long eventId) {
        var channelAgencies = ctx.getChannelsAgencies();
        if (MapUtils.isNotEmpty(channelAgencies)) {
            Map<String, List<SessionOccupationVenueContainer>> venueOccupationCache = new HashMap<>();
            var channelAgencySessionsToIndex = channelAgencies
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .distinct()
                    .map(agencyId -> {
                        List<ChannelSessionAgencyData> channelAgencySessions;
                        if (sessionId == null) {
                            channelAgencySessions = channelAgencySessionElasticDao.getByEventId(eventId, agencyId);
                        } else {
                            channelAgencySessions = channelAgencySessionElasticDao.getBySessionId(sessionId, eventId, agencyId);
                        }
                        return channelAgencySessions.stream()
                                .map(channelAgencySessionData -> buildChannelAgencySessionForOccupationContext(ctx, channelAgencySessionData, venueOccupationCache)).
                                filter(Objects::nonNull)
                                .collect(Collectors.toList());
                    }).flatMap(List::stream).collect(Collectors.toList());
            fillPriceZones(channelAgencySessionsToIndex, ctx);
            ctx.setChannelAgencySessionsToIndex(channelAgencySessionsToIndex);
        }
    }

    private <T extends ForOccupationIndexation<C>, C extends ChannelSession> T buildChannelSessionForOccupationContext(
            OccupationIndexationContext ctx,
            C channelSession,
            T response,
            List<Long> quotas,
            Map<String, List<SessionOccupationVenueContainer>> venueOccupationCache) {
        Long channelId = channelSession.getChannelId();
        Long sessionId = channelSession.getSessionId();
        Long eventId = channelSession.getEventId();
        Optional<SessionData> optSessionData = ctx.getSessionOr(sessionId, id -> sessionElasticDao.get(id, eventId));
        if (optSessionData.isPresent()) {
            Session session = optSessionData.get().getSession();
            EventType eventTypeFromTemplate = Optional
                    .ofNullable(IndexerUtils.getEventTypeByVenueTemplateType(session.getVenueTemplateType()))
                    .orElse(ctx.getEventType());
            if (EventType.ACTIVITY.equals(eventTypeFromTemplate) && CollectionUtils.isEmpty(quotas)) {
                quotas = session.getVenueQuotas().stream()
                        .filter(VenueQuota::getDefaultQuota)
                        .map(VenueQuota::getId)
                        .map(Integer::longValue)
                        .collect(Collectors.toList());
            }
        }
        response.setChannelId(channelId);
        response.setSessionId(sessionId);
        response.setChannelSessionIndexed(channelSession);
        response.setQuotas(quotas);
        response.setMustBeIndexed(true);
        optSessionData.ifPresent(sessionData -> response.setVenueTemplateType(sessionData.getSession().getVenueTemplateType()));
        fillSessionVenueDescriptor(response, ctx, venueOccupationCache);
        return response;
    }

    private ChannelSessionAgencyForOccupationIndexation buildChannelAgencySessionForOccupationContext(OccupationIndexationContext ctx,
                                                                                                      ChannelSessionAgencyData channelAgencySessionData,
                                                                                                      Map<String, List<SessionOccupationVenueContainer>> venueOccupationCache) {
        ChannelSessionAgency channelAgencySession = channelAgencySessionData.getChannelSessionAgency();
        Long channelId = channelAgencySession.getChannelId();
        Long agencyId = channelAgencySession.getAgencyId();
        var agencies = ctx.getChannelAgencies(channelId);
        if (CollectionUtils.isEmpty(agencies)) {
            return null;
        }
        if (!agencies.contains(agencyId)) {
            return null;
        }
        ChannelSessionAgencyForOccupationIndexation response = buildChannelSessionForOccupationContext(ctx, channelAgencySessionData.getChannelSessionAgency(),
                new ChannelSessionAgencyForOccupationIndexation(), extractQuotasByChannel(ctx, channelAgencySession), venueOccupationCache);
        response.setAgencyId(agencyId);
        return response;
    }

    private void fillSessionsBulkInfo(EventIndexationContext ctx) {
        List<Long> sessionIds = ctx.getSessions().stream().map(s -> s.getIdsesion().longValue()).toList();

        var sessionConfigs = sessionConfigCouchDao.bulkGet(sessionIds).stream().
                collect(Collectors.toMap(v -> v.getSessionId().longValue(), Function.identity()));
        ctx.setSessionConfigs(sessionConfigs);

        Set<Long> sbSessionIds = sessionIds.stream()
                .filter(s -> EventUtils.isSmartBookingSession(ctx, s.intValue()))
                .collect(Collectors.toSet());
        Map<Long, SBSession> sbSessions = sbSessionsCouchDao.bulkGet(sbSessionIds).stream().
                collect(Collectors.toMap(v -> v.getSessionId().longValue(), Function.identity()));
        ctx.setSbBySession(sbSessions);

        Map<Long, List<SessionRate>> sessionsRatesInfo = sessionRateDao.getSessionRatesByEventId(ctx.getEventId());
        Map<Long, Set<SessionRate>> ratesBySession = new HashMap<>();
        Map<Long, Long> defaultRatesBySession = new HashMap<>();
        for (Map.Entry<Long, List<SessionRate>> sessionRatesInfo : sessionsRatesInfo.entrySet()) {
            Long sessionId = sessionRatesInfo.getKey();
            ratesBySession.computeIfAbsent(sessionId, id -> new HashSet<>());
            for (SessionRate rate : sessionRatesInfo.getValue()) {
                ratesBySession.get(sessionId).add(rate);
                if (Boolean.TRUE.equals(rate.getDefaultRate())) {
                    defaultRatesBySession.put(sessionId, rate.getRateId().longValue());
                }
            }
        }
        ctx.setRatesBySession(ratesBySession);
        ctx.setDefaultRateBySession(defaultRatesBySession);

        var sessionPacksBySessions = seasonSessionDao.findSessionPacksBySessionForSessionIds(sessionIds);
        ctx.setSessionPacksBySession(sessionPacksBySessions);

        List<CpanelElementosComEventoRecord> comElements = eventCommElemDao.getEventCommunicationElementsBySessionIds(sessionIds);
        ctx.setComElementsBySession(comElements.stream().collect(Collectors.groupingBy(CpanelElementosComEventoRecord::getIdsesion)));
    }

    private <T extends ChannelSessionPriceZones> void fillSessionVenueDescriptor(T cs, BaseIndexationContext<?, ?> ctx, Map<String, List<SessionOccupationVenueContainer>> venueCache) {
        if (cs != null && Boolean.TRUE.equals(cs.getMustBeIndexed())) {
            EventType eventType = ctx.getEventType();
            switch (eventType) {
                case AVET, NORMAL, SEASON_TICKET -> {
                    List<Long> quotas = null;
                    if (CollectionUtils.isNotEmpty(cs.getQuotas())) {
                        quotas = cs.getQuotas().stream().sorted().toList();
                    }
                    Long sessionId = cs.getSessionId();
                    String key = eventType + "_" + sessionId + "_" + StringUtils.join(quotas, ":");
                    String hashedKey = DigestUtils.md5Hex(key);
                    if (venueCache != null && venueCache.containsKey(hashedKey)) {
                        List<SessionOccupationVenueContainer> containerOccupations = copyContainer(venueCache.get(hashedKey));
                        cs.setContainerOccupations(copyContainer(containerOccupations));
                        return;
                    }
                    SessionVenueContainerSearchRequest request = new SessionVenueContainerSearchRequest();
                    request.setQuotas(quotas);
                    request.setEventType(eventType);
                    var sessionOccupationVenueContainers = sessionOccupationRepository.searchOccupationsByContainer(sessionId, request);
                    if (venueCache != null) {
                        venueCache.put(hashedKey, sessionOccupationVenueContainers);
                    }
                    cs.setContainerOccupations(sessionOccupationVenueContainers);
                }
                default -> {
                }
            }
        }
    }


    private void fillPriceZones(List<? extends ChannelSessionPriceZones> channelSessions, BaseIndexationContext<?, ?> ctx) {

        Map<EventType, List<SessionWithQuotasDTO>> eventTemplateWithQuotas = getEventTypeSessionWithQuotasMap(channelSessions, ctx.getEventType());
        for (Map.Entry<EventType, List<SessionWithQuotasDTO>> entry : eventTemplateWithQuotas.entrySet()) {

            SessionOccupationsSearchRequest request = new SessionOccupationsSearchRequest();
            request.setEventType(entry.getKey());
            request.setSessions(entry.getValue());

            var priceZoneOccupations = sessionOccupationRepository.searchOccupationsByPriceZones(request);

            priceZoneOccupations.forEach(priceZoneOccupation -> {
                SessionWithQuotasDTO sessionWithQuotas = priceZoneOccupation.getSession();
                var sessionPriceZoneOccupations = priceZoneOccupation.getOccupation();
                List<Long> updatedPriceZones = new ArrayList<>();
                List<Long> priceZones = sessionPriceZoneOccupations.stream()
                        .map(SessionPriceZoneOccupationDTO::getPriceZoneId)
                        .collect(Collectors.toList());

                channelSessions.stream()
                        .filter(channelSession ->
                                IndexerUtils.hasSameQuotas(channelSession, sessionWithQuotas))
                        .forEach(channelSession -> {
                            OccupationAppender.fillSecondaryMarketConfig(ctx, channelSession, sessionWithQuotas, sessionPriceZoneOccupations, updatedPriceZones);
                            OccupationAppender.fillPairSeatInPriceTypes(ctx, channelSession.getSessionId(),
                                    channelSession.getVenueTemplateType(), sessionPriceZoneOccupations);
                            channelSession.setPriceZoneOccupations(sessionPriceZoneOccupations);
                            channelSession.setPriceZones(priceZones);
                            List<Long> priceZonesWithAvailability = sessionPriceZoneOccupations.stream()
                                    .filter(this::hasAvailability)
                                    .map(SessionPriceZoneOccupationDTO::getPriceZoneId)
                                    .collect(Collectors.toList());
                            channelSession.setPriceZonesWithAvailability(priceZonesWithAvailability);
                        });
            });
        }
    }

    private Map<EventType, List<SessionWithQuotasDTO>> getEventTypeSessionWithQuotasMap(List<? extends ChannelSessionPriceZones> channelSessions, EventType eventType) {

        Map<EventType, List<ChannelSessionPriceZones>> evenTypeSessionWithQuotasMap = new EnumMap<>(EventType.class);
        channelSessions.forEach(channelSession -> {
            EventType eventTypeFromTemplate = Optional
                    .ofNullable(IndexerUtils.getEventTypeByVenueTemplateType(channelSession.getVenueTemplateType()))
                    .orElse(eventType);
            if (!evenTypeSessionWithQuotasMap.containsKey(eventTypeFromTemplate)) {
                evenTypeSessionWithQuotasMap.put(eventTypeFromTemplate, new ArrayList<>());
            }
            evenTypeSessionWithQuotasMap.get(eventTypeFromTemplate).add(channelSession);
        });

        Map<EventType, List<SessionWithQuotasDTO>> mapSessionsWithQuotas = new EnumMap<>(EventType.class);
        evenTypeSessionWithQuotasMap.forEach((k, v) -> {
            List<SessionWithQuotasDTO> sessionsWithQuotas = v.stream()
                    .map(this::convertToSessionWithQuotas)
                    .distinct()
                    .collect(Collectors.toList());
            mapSessionsWithQuotas.put(k, sessionsWithQuotas);
        });

        return mapSessionsWithQuotas;
    }

    private static boolean invalidQuotas(List<Long> channelQuotas, List<Long> sessionQuotas) {
        if (channelQuotas == null) {
            return false;
        }
        return channelQuotas.stream().noneMatch(sessionQuotas::contains);
    }

    private SessionWithQuotasDTO convertToSessionWithQuotas(ChannelSessionPriceZones channelSession) {
        return buildSessionWithQuotas(channelSession.getSessionId(), channelSession.getQuotas());
    }

    private static SessionWithQuotasDTO buildSessionWithQuotas(Long sessionId, List<Long> quotas) {
        SessionWithQuotasDTO sessionWithQuotas = new SessionWithQuotasDTO();
        sessionWithQuotas.setSessionId(sessionId);
        sessionWithQuotas.setQuotas(IndexerUtils.sortAndDistinct(quotas));
        return sessionWithQuotas;
    }

    private static Boolean includeB2B(CpanelCanalEventoRecord ce, EventIndexationContext ctx) {
        Long channelId = ce.getIdcanal().longValue();
        ChannelInfo channelInfo = ctx.getChannelInfo(channelId);
        if (channelInfo != null) {
            return channelInfo.isB2BChannel();
        }
        return true;
    }

    private static List<Long> extractQuotasByChannel(OccupationIndexationContext ctx, ChannelSession channelSession) {
        return ctx.getQuotasByChannel(channelSession.getChannelId());
    }

    private boolean hasAvailability(SessionPriceZoneOccupationDTO occupation) {
        if (BooleanUtils.isTrue(occupation.getUnlimited())) {
            return true;
        }
        Map<TicketStatus, Long> status = occupation.getStatus();
        if (status != null && status.containsKey(TicketStatus.AVAILABLE)) {
            Long available = status.get(TicketStatus.AVAILABLE);
            return available != null && available > 0L;
        }
        return false;
    }

    private static void fillPresale(EventIndexationContext ctx, Long sessionId, Long channelId, ChannelSessionForEventIndexation channelSession) {
        var sessionPresaleConfigMap = ctx.getSessionPresaleConfigMap();
        if (MapUtils.isNotEmpty(sessionPresaleConfigMap) && CollectionUtils.isNotEmpty(sessionPresaleConfigMap.get(sessionId))) {
            List<PresaleRecord> presales = sessionPresaleConfigMap.get(sessionId);
            Boolean hasPresales = BooleanUtils.isTrue(PresaleConfigUtil.channelHasValidOrFutureAndEnabledPresales(presales, channelId)) ? Boolean.TRUE : null;
            channelSession.setPresales(hasPresales);
        }
    }

    private void fillProducts(EventIndexationContext ctx, ChannelSessionForEventIndexation channelSession) {
        String channelSessionKey = buildChannelSessionKey(channelSession.getChannelId(), channelSession.getSessionId());
        Map<String, List<Long>> channelSessionProducts = ctx.getChannelSessionProducts();
        if (channelSessionProducts != null && channelSessionProducts.containsKey(channelSessionKey)) {
            channelSession.setProducts(channelSessionProducts.get(channelSessionKey));
        } else {
            channelSession.setProducts(new ArrayList<>());
        }
    }

    private static void fillChannelSessionInfo(EventIndexationContext ctx,
                                               ChannelSessionIndexationContext sessionContext,
                                               SessionForCatalogRecord session,
                                               CpanelCanalEventoRecord channelEvent,
                                               ChannelSessionForEventIndexation cs,
                                               ChannelInfo channelInfo,
                                               List<Long> quotas) {
        Long sessionId = session.getIdsesion().longValue();
        Long channelId = channelEvent.getIdcanal().longValue();
        cs.setChannelId(channelId);
        cs.setSessionId(sessionId);
        cs.setSession(session);
        cs.setChannelEvent(channelEvent);
        cs.setChannel(channelInfo);
        cs.setQuotas(quotas);
        cs.setVenueTemplateType(sessionContext.getVenueDescriptor().getType());
        cs.setSessionAttendantsConfig(sessionContext.getSessionAttendants());
        cs.setEventAttendantsConfig(ctx.getEventAttendantsConfig());
        cs.setSecondaryMarketConfig(sessionContext.getSessionSecondaryMarketConfig());
        cs.setChannelSurchargesTaxes(getChannelSurchargesTaxes(ctx, channelId, channelInfo, session));

        fillPresale(ctx, sessionId, channelId, cs);

        if (CatalogPublishableUtils.isEventChannelSessionIndexable(session, ctx.getEvent(), channelEvent)) {
            cs.setMustBeIndexed(Boolean.TRUE);
        } else {
            cs.setMustBeIndexed(Boolean.FALSE);
        }
    }

    private String buildChannelSessionKey(Long channelId, Long sessionId) {
        return channelId + "_" + sessionId;
    }

    private SessionSecondaryMarketConfigDTO getSessionSecondaryMarketConfig(EventIndexationContext ctx, Long sessionId) {
        if (BooleanUtils.isTrue(ctx.getEntity().getUseSecondaryMarket())) {
            try {
                SessionConfig sessionConfig = ctx.getSessionConfigs().get(sessionId);
                SessionSecondaryMarketConfig sessionSecMkt = sessionSecondaryMarketConfigCouchDao.get(sessionId.toString());
                EventSecondaryMarketConfigDTO eventSecMktDTO = ctx.getEventSecondaryMarketConfig();
                if (eventSecMktDTO == null) {
                    EventSecondaryMarketConfig eventSecMkt = eventSecondaryMarketConfigCouchDao.get(String.valueOf(ctx.getEventId()));
                    eventSecMktDTO = EventSecondaryMarketConverter.toDTO(eventSecMkt);
                    ctx.setEventSecondaryMarketConfig(eventSecMktDTO);
                }
                if (sessionSecMkt != null) {
                    return SessionSecondaryMarketConverter.toDTO(sessionSecMkt, sessionConfig);
                }
                return SessionSecondaryMarketConverter.toDTO(null, eventSecMktDTO, sessionConfig);
            } catch (Exception e) {
                LOGGER.error("[CHANNEL INDEXATION] - Error on secondary market location search for event {}", ctx.getEventId());
            }
        }
        return null;
    }

    private static List<CpanelCanalEventoRecord> getPublishableChannels(EventIndexationContext ctx) {
        return ctx.getChannelEvents().stream()
                .filter(channelEvent -> ctx.getEventChannel(channelEvent.getIdcanal()).isPresent())
                .filter(CatalogPublishableUtils::isPublishable)
                .collect(Collectors.toList());
    }

    private List<SessionOccupationVenueContainer> copyContainer(List<SessionOccupationVenueContainer> containerOccupations) {
        if (containerOccupations == null) {
            return null;
        }
        List<SessionOccupationVenueContainer> result = new ArrayList<>();
        containerOccupations.forEach(containerOccupation -> {
            SessionOccupationVenueContainer out = new SessionOccupationVenueContainer();
            out.setId(containerOccupation.getId());
            copyOccupation(containerOccupation, out);
            copySectorOccupation(containerOccupation.getSectors(), out);
            copyPriceZoneOccupation(containerOccupation.getPriceZones(), out);
            result.add(out);
        });
        return result;
    }

    private static void copyOccupation(SessionOccupationVenueContainer containerOccupation, SessionOccupationVenueContainer out) {
        SessionOccupationDTO occupation = containerOccupation.getOccupation();
        SessionOccupationDTO outOccupation = new SessionOccupationDTO();
        outOccupation.setUnlimited(occupation.getUnlimited());
        Map<TicketStatus, Long> status = occupation.getStatus();
        if (MapUtils.isNotEmpty(status)) {
            outOccupation.setStatus(status.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        out.setOccupation(outOccupation);
    }

    private static void copyPriceZoneOccupation(List<SessionPriceZoneOccupationDTO> priceZones, SessionOccupationVenueContainer out) {
        if (CollectionUtils.isNotEmpty(priceZones)) {
            List<SessionPriceZoneOccupationDTO> outPriceZones = priceZones
                    .stream()
                    .map(ChannelSessionIndexationService::copyPriceZone)
                    .collect(Collectors.toList());
            out.setPriceZones(outPriceZones);
        }
    }

    private static void copySectorOccupation(List<SectorOccupationDTO> sectors, SessionOccupationVenueContainer out) {
        if (CollectionUtils.isNotEmpty(sectors)) {
            List<SectorOccupationDTO> outSectors = sectors
                    .stream()
                    .map(ChannelSessionIndexationService::copySectorOccupation)
                    .collect(Collectors.toList());
            out.setSectors(outSectors);
        }
    }

    private static SessionPriceZoneOccupationDTO copyPriceZone(SessionPriceZoneOccupationDTO s) {
        SessionPriceZoneOccupationDTO out = new SessionPriceZoneOccupationDTO();
        out.setPriceZoneId(s.getPriceZoneId());
        out.setUnlimited(s.getUnlimited());
        out.setLimit(s.getLimit());
        if (MapUtils.isNotEmpty(s.getAdditionalProperties())) {
            out.setAdditionalProperties(s.getAdditionalProperties().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        Map<TicketStatus, Long> status = s.getStatus();
        if (MapUtils.isNotEmpty(status)) {
            out.setStatus(status.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        return out;
    }

    private static SectorOccupationDTO copySectorOccupation(SectorOccupationDTO s) {
        SectorOccupationDTO out = new SectorOccupationDTO();
        out.setId(s.getId());
        out.setUnlimited(s.getUnlimited());
        Map<TicketStatus, Long> status = s.getStatus();
        if (MapUtils.isNotEmpty(status)) {
            out.setStatus(status.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        return out;
    }


}

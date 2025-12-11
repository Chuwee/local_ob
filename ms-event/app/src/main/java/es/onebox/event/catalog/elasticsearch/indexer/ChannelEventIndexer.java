package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.logger.util.LogUtil;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.builder.ChannelEventComElementsBuilder;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogInfo;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogSessionInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventPostBookingQuestions;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.PostBookingQuestion;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.PostBookingQuestionChoice;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.PostBookingQuestionTranslation;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.PostBookingQuestionType;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.utils.CatalogPublishableUtils;
import es.onebox.event.catalog.elasticsearch.utils.ChannelCatalogInfoMerger;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dao.EventChannelCommElemDao;
import es.onebox.event.events.dao.record.CommElementRecord;
import es.onebox.event.events.dao.record.EmailCommElementRecord;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.postbookingquestions.dao.EventChannelPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.EventPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionCouchDao;
import es.onebox.event.events.postbookingquestions.dao.record.EventPostBookingQuestionRecord;
import es.onebox.event.events.postbookingquestions.domain.Choice;
import es.onebox.event.events.postbookingquestions.domain.Translation;
import es.onebox.event.events.postbookingquestions.enums.EventChannelsPBQType;
import es.onebox.event.taxonomy.dao.CustomTaxonomyDao;
import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.event.config.LocalCache.CHANNEL_BANNER_KEY;
import static es.onebox.event.config.LocalCache.CHANNEL_COM_TTL;
import static es.onebox.event.config.LocalCache.EVENT_CHANNEL_BANNER_KEY;
import static es.onebox.event.config.LocalCache.EVENT_CHANNEL_COM_TTL;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class ChannelEventIndexer {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected final CustomTaxonomyDao customTaxonomyDao;
    protected final EmailCommunicationElementDao emailCommunicationElementDao;
    protected final EventChannelCommElemDao eventChannelCommElemDao;
    private final ChannelEventCommunicationElementDao channelEventCommunicationElementDao;
    private final EventPostBookingQuestionDao eventPostBookingQuestionDao;
    private final EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao;
    protected final ChannelsRepository channelsRepository;
    protected final CacheRepository localCacheRepository;
    private final PostBookingQuestionCouchDao postBookingQuestionCouchDao;

    protected ChannelEventIndexer(CustomTaxonomyDao customTaxonomyDao,
                                  EmailCommunicationElementDao emailCommunicationElementDao,
                                  EventChannelCommElemDao eventChannelCommElemDao,
                                  ChannelEventCommunicationElementDao channelEventCommunicationElementDao,
                                  EventPostBookingQuestionDao eventPostBookingQuestionDao,
                                  EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao,
                                  ChannelsRepository channelsRepository,
                                  CacheRepository localCacheRepository, PostBookingQuestionCouchDao postBookingQuestionCouchDao) {
        this.customTaxonomyDao = customTaxonomyDao;
        this.emailCommunicationElementDao = emailCommunicationElementDao;
        this.eventChannelCommElemDao = eventChannelCommElemDao;
        this.channelEventCommunicationElementDao = channelEventCommunicationElementDao;
        this.eventPostBookingQuestionDao = eventPostBookingQuestionDao;
        this.eventChannelPostBookingQuestionDao = eventChannelPostBookingQuestionDao;
        this.channelsRepository = channelsRepository;
        this.localCacheRepository = localCacheRepository;
        this.postBookingQuestionCouchDao = postBookingQuestionCouchDao;
    }

    protected static ChannelCatalogEventInfo updateChannelEventBillboard(Integer eventId, ChannelCatalogEventInfo in, ChannelAttributes channelsAttrs) {
        ChannelCatalogEventInfo out = in == null ? new ChannelCatalogEventInfo() : in;

        Boolean onCatalog = channelsAttrs.getHiddenBillboardEvents() == null || !channelsAttrs.getHiddenBillboardEvents().contains(eventId);
        out.setOnCatalog(onCatalog);

        Integer catalogPosition = channelsAttrs.getCustomEventsOrder() != null && channelsAttrs.getCustomEventsOrder().contains(eventId) ?
                channelsAttrs.getCustomEventsOrder().indexOf(eventId) : null;
        out.setCatalogPosition(catalogPosition);
        out.setHighlighted(catalogPosition != null);

        Boolean extended = channelsAttrs.getExtendedEvents() != null && channelsAttrs.getExtendedEvents().contains(eventId);
        out.setExtended(extended);

        List<Integer> carouselEventsOrder = channelsAttrs.getCarouselEventsOrder();
        boolean onCarousel = carouselEventsOrder != null && carouselEventsOrder.contains(eventId);
        out.setOnCarousel(onCarousel);

        Integer carouselPosition = onCarousel ? carouselEventsOrder.indexOf(eventId) : null;
        out.setCarouselPosition(carouselPosition);

        return out;
    }

    protected static <T extends ChannelSession> List<VenueRecord> getVenues(EventIndexationContext ctx, List<T> channelSessions) {
        return channelSessions.stream()
                .map(cs -> ctx.getVenueBySessionId(cs.getSessionId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    protected static Timestamp getSessionEndTimestamp(CpanelSesionRecord session, EventIndexationContext ctx) {
        if (CommonUtils.isTrue(session.getEsabono())) {
            //TODO check
            List<Integer> sessionsBySessionPackId = ctx.getSessionsBySessionPack().get(session.getIdsesion());
            if (Objects.nonNull(sessionsBySessionPackId) && !sessionsBySessionPackId.isEmpty()) {
                var sessionsInPack = ctx.getSessions().stream().filter(s -> sessionsBySessionPackId.contains(s.getIdsesion())).toList();
                return sessionsInPack.stream()
                        .map(sessionInPack -> sessionInPack.getFecharealfinsesion() == null ? sessionInPack.getFechainiciosesion() : sessionInPack.getFecharealfinsesion())
                        .max(Comparator.comparing(Function.identity(), Comparator.nullsLast(Timestamp::compareTo)))
                        .orElseGet(session::getFechainiciosesion);
            }
        }
        return session.getFecharealfinsesion() == null ? session.getFechainiciosesion() : session.getFecharealfinsesion();
    }

    protected static void addBillboardInfo(ChannelCatalogEventInfo in, CpanelCanalEventoRecord channelEventRecord, ChannelAttributes channelAttributes, CpanelEventoRecord event) {
        if (ChannelEventStatus.ACCEPTED.getId().equals(channelEventRecord.getEstadorelacion())) {
            if (in == null) {
                in = new ChannelCatalogEventInfo();
            }
            if (channelAttributes != null) {
                updateChannelEventBillboard(event.getIdevento(), in, channelAttributes);
            } else {
                Boolean onCatalog = EventStatus.READY.getId().equals(event.getEstado()) ? Boolean.TRUE : Boolean.FALSE;
                in.setOnCatalog(onCatalog);
                in.setHighlighted(Boolean.FALSE);
            }
        }
    }

    protected static Boolean allowAlternativePromoterSurcharges(CpanelEventoCanalRecord eventChannelRecord, CpanelEventoRecord event,
                                                                CpanelCanalEventoRecord channelEvent) {

        boolean applyChannelSpecificSurcharges = CommonUtils.isTrue(eventChannelRecord.getAplicarrecargoscanalespecificos());

        boolean eventAllowsAlternativeCharges = BooleanUtils.isTrue(event.getAllowchannelusealternativecharges());

        boolean channelEventAllowsAlternativeCharges = BooleanUtils.isTrue(channelEvent.getAllowchannelusealternativecharges());

        if (applyChannelSpecificSurcharges) {
            // If channel event allows specific surcharges the condition is true
            return channelEventAllowsAlternativeCharges;
        }
        // If the channel has generic surcharges, apply only if the event surcharges are alternative
        return eventAllowsAlternativeCharges;
    }

    protected void print(List<String> idsToRemove, String reason) {
        if (CollectionUtils.isNotEmpty(idsToRemove)) {
            LOGGER.info("[EVENT2ES] Removing old channel events: {} - reason: {}", LogUtil.collectionToString(idsToRemove), reason);
        }
    }

    protected static Optional<Timestamp> getLastPublishedSession(EventIndexationContext ctx, List<SessionForCatalogRecord> sessions) {
        return sessions.stream().filter(s -> s.getPublicado() != null && s.getPublicado() == 1)
                .map(s -> ChannelEventIndexer.getSessionEndTimestamp(s, ctx))
                .max(Comparator.comparing(Function.identity(), Comparator.nullsLast(Timestamp::compareTo)));
    }

    protected static List<SessionForCatalogRecord> getPublishableSessionPacks(List<SessionForCatalogRecord> sessions) {
        return sessions.stream().filter(s -> CommonUtils.isTrue(s.getEsabono())
                && CommonUtils.isFalse(s.getIspreview())
                && CatalogPublishableUtils.isSessionPublished(s)).toList();
    }

    protected static List<SessionForCatalogRecord> getPublishableSessions(List<SessionForCatalogRecord> sessions) {
        return sessions.stream().filter(s -> !CommonUtils.isTrue(s.getEsabono())
                && CommonUtils.isFalse(s.getIspreview())
                && CatalogPublishableUtils.isSessionPublished(s)).toList();
    }

    protected static void updateBasicChannelEventInfo(CpanelCanalEventoRecord channelEventRecord, ChannelEvent channelEvent,
                                                      List<SessionForCatalogRecord> sessions,
                                                      List<? extends ChannelCatalogSessionInfo> channelSessions,
                                                      EventIndexationContext ctx) {

        List<SessionForCatalogRecord> publishableSessions = getPublishableSessions(sessions);
        Optional<Timestamp> firstPublishedDateSession = publishableSessions.stream().map(CpanelSesionRecord::getFechapublicacion).min(Timestamp::compareTo);

        List<SessionForCatalogRecord> publishableSessionPacks = getPublishableSessionPacks(sessions);
        Optional<Timestamp> firstPublishedDateSeasonPack = publishableSessionPacks.stream().map(CpanelSesionRecord::getFechapublicacion).min(Timestamp::compareTo);

        boolean hasPublishableSessions = !publishableSessions.isEmpty();
        boolean hasPublishableSessionPacks = !publishableSessionPacks.isEmpty();

        Optional<Timestamp> lastSessionTimeStamp = getLastPublishedSession(ctx, sessions);
        Date endEventDate = lastSessionTimeStamp.map(timestamp -> new Date(timestamp.getTime())).orElseGet(channelEventRecord::getFechafin);

        firstPublishedDateSession.ifPresent(timestamp -> channelEvent.setFirstPublishedSession(timestamp.toLocalDateTime().atZone(ZoneOffset.UTC)));
        firstPublishedDateSeasonPack.ifPresent(timestamp -> channelEvent.setFirstPublishedSessionPack(timestamp.toLocalDateTime().atZone(ZoneOffset.UTC)));
        channelEvent.setHasSessions(hasPublishableSessions);
        channelEvent.setHasSessionPacks(hasPublishableSessionPacks);
        channelEvent.setEndChannelEventDate(endEventDate);
        if (channelEvent.getCatalogInfo() != null) {
            channelEvent.getCatalogInfo().setForSale(channelSessions.stream().anyMatch(ChannelCatalogInfo::getForSale));
            channelEvent.getCatalogInfo().setDate(ChannelCatalogInfoMerger.mergeDates(channelSessions));
        }
        if(ctx.getEventConfig() != null) {
            channelEvent.setPhoneValidationRequired(ctx.getEventConfig().getPhoneVerificationRequired());
            channelEvent.setAttendantVerificationRequired(ctx.getEventConfig().getAttendantVerificationRequired());
        }
    }

    protected void updateComElementsChannelEventInfo(CpanelCanalEventoRecord channelEventRecord, ChannelEvent channelEvent) {
        Integer channelId = channelEventRecord.getIdcanal();
        Integer eventId = channelEventRecord.getIdevento();

        Object[] ecCacheKey = {eventId, channelId};
        List<EmailCommElementRecord> ecBannerPromoter = localCacheRepository.cached(EVENT_CHANNEL_BANNER_KEY + "Promoter", EVENT_CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsEventChannelPromoterBanner(eventId, channelId), ecCacheKey);
        List<EmailCommElementRecord> ecBannerChannel = localCacheRepository.cached(EVENT_CHANNEL_BANNER_KEY + "Channel", EVENT_CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsEventChannelChannelBanner(eventId, channelId), ecCacheKey);
        List<EmailCommElementRecord> ecBannerChannelLink = localCacheRepository.cached(EVENT_CHANNEL_BANNER_KEY + "ChannelLink", EVENT_CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsEventChannelChannelBannerLink(eventId, channelId), ecCacheKey);
        List<EmailCommElementRecord> ecBannerHeader = localCacheRepository.cached(EVENT_CHANNEL_BANNER_KEY + "Header", EVENT_CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsEventChannelHeaderBanner(eventId, channelId), ecCacheKey);

        Object[] channelCacheKey = {channelId};
        List<EmailCommElementRecord> cBanner = localCacheRepository.cached(CHANNEL_BANNER_KEY, CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsChannelBanner(channelId), channelCacheKey);
        List<EmailCommElementRecord> cBannerLink = localCacheRepository.cached(CHANNEL_BANNER_KEY + "Link", CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsChannelBannerLink(channelId), channelCacheKey);
        List<EmailCommElementRecord> cBannerHeader = localCacheRepository.cached(CHANNEL_BANNER_KEY + "Header", CHANNEL_COM_TTL, SECONDS,
                () -> emailCommunicationElementDao.getEmailCommElementsChannelHeaderBanner(channelId), channelCacheKey);

        List<CommElementRecord> ecBannerSquare = null;
        if (channelEventCommunicationElementDao != null) {
            Integer channelEventId = channelEventRecord.getIdcanaleevento();
            ecBannerSquare = localCacheRepository.cached(EVENT_CHANNEL_BANNER_KEY + "Square", EVENT_CHANNEL_COM_TTL, SECONDS,
                    () -> channelEventCommunicationElementDao.getCommElementByEventIdAndChannelId(channelEventId), ecCacheKey);
        }

        ChannelEventComElementsBuilder.builder(channelEvent)
                .eventChannelBannerPromoter(ecBannerPromoter)
                .eventChannelBannerChannel(ecBannerChannel)
                .eventChannelBannerChannelLink(ecBannerChannelLink)
                .eventChannelBannerHeader(ecBannerHeader)
                .eventChannelBannerSquare(ecBannerSquare)
                .channelBannerChannel(cBanner)
                .channelBannerChannelLink(cBannerLink)
                .channelBannerHeader(cBannerHeader)
                .buildComElements();
    }

    protected ChannelEventPostBookingQuestions getChannelEventPostBookingQuestions(EventIndexationContext ctx, Long channelId) {

        boolean enabled = ctx.getEventConfig() != null
                && ctx.getEventConfig().getPostBookingQuestionsConfig() != null
                && BooleanUtils.isTrue(ctx.getEventConfig().getPostBookingQuestionsConfig().getEnabled());
        if (enabled) {
            ChannelEventPostBookingQuestions postBookingQuestions = new ChannelEventPostBookingQuestions();
            if (EventChannelsPBQType.ALL.equals(ctx.getEventConfig().getPostBookingQuestionsConfig().getType())) {

                List<EventPostBookingQuestionRecord> eventPostBookingQuestions =
                        eventPostBookingQuestionDao.getEventPostBookingQuestions(ctx.getEventConfig().getEventId());

                if (CollectionUtils.isNotEmpty(eventPostBookingQuestions)) {
                    postBookingQuestions = addPostBookingQuestions(postBookingQuestionCouchDao.bulkGet(eventPostBookingQuestions
                            .stream().map(EventPostBookingQuestionRecord::getIdExterno).toList()));
                }
            } else {
                List<EventPostBookingQuestionRecord> eventChannelsPostBookingQuestions =
                        eventChannelPostBookingQuestionDao.getEventChannelsPostBookingQuestions(
                                ctx.getEventConfig().getEventId(),
                                channelId.intValue()
                        );
                if (CollectionUtils.isNotEmpty(eventChannelsPostBookingQuestions)) {
                    postBookingQuestions = addPostBookingQuestions(postBookingQuestionCouchDao.bulkGet(eventChannelsPostBookingQuestions
                            .stream().map(EventPostBookingQuestionRecord::getIdExterno).toList()));
                }
            }

            if (postBookingQuestions != null && CollectionUtils.isNotEmpty(postBookingQuestions.getQuestions())) {
                return postBookingQuestions;
            }
        }

        return null;
    }

    private ChannelEventPostBookingQuestions addPostBookingQuestions(List<es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion> in) {

        if (in == null) {
            return null;
        }
        ChannelEventPostBookingQuestions out = new ChannelEventPostBookingQuestions();
        out.setQuestions(new ArrayList<>());
        for (es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion inQuestion: in) {
            PostBookingQuestion outQuestion = new PostBookingQuestion();
            outQuestion.setId(inQuestion.getId());
            outQuestion.setLabel(addPostBookingQuestionTranslation(inQuestion.getLabel()));
            outQuestion.setMessage(addPostBookingQuestionTranslation(inQuestion.getMessage()));
            outQuestion.setType(PostBookingQuestionType.valueOf(inQuestion.getType().name()));
            outQuestion.setChoices(addPostBookingQuestionsChoices(inQuestion.getChoices()));

            out.getQuestions().add(outQuestion);
        }
        return out;
    }

    private PostBookingQuestionTranslation addPostBookingQuestionTranslation(Translation in) {

        if (in == null) {
            return null;
        }
        PostBookingQuestionTranslation out = new PostBookingQuestionTranslation();
        out.setDefaultValue(in.getDefaultValue());
        out.setTranslations(in.getTranslations());

        return out;
    }

    private Set<PostBookingQuestionChoice> addPostBookingQuestionsChoices(Set<Choice> in) {

        if (in == null) {
            return null;
        }
        Set<PostBookingQuestionChoice> out = new HashSet<>();
        for (Choice inChoice: in) {
            PostBookingQuestionChoice outChoice = new PostBookingQuestionChoice();
            outChoice.setId(inChoice.getId());
            outChoice.setLabel(addPostBookingQuestionTranslation(inChoice.getLabel()));
            outChoice.setValue(inChoice.getPosition());
            outChoice.setAdditionalQuestion(addPostBookingQuestionTranslation(inChoice.getAdditionalFreeTextChoiceQuestion()));
            out.add(outChoice);
        }

        return out;
    }
}

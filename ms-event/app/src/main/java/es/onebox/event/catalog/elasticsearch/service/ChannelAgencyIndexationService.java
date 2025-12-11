package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dao.ChannelConfigCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelConfigCB;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.OccupationIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.ChannelAgency;
import es.onebox.event.catalog.elasticsearch.utils.IndexerUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.client.dto.ClientEntity;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionsRequest;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.dto.ChannelEventB2BQuotaAssignationDTO;
import es.onebox.event.events.dto.ChannelEventB2BQuotaAssignationsDTO;
import es.onebox.event.events.dto.conditions.ProfessionalClientConditions;
import es.onebox.event.events.service.EventChannelB2BService;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static es.onebox.event.config.LocalCache.CLIENT_CONDITIONS_KEY;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class ChannelAgencyIndexationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelAgencyIndexationService.class);

    private final ChannelEventDao channelEventDao;
    private final CacheRepository localCacheRepository;
    private final ChannelConfigCouchDao channelConfigCouchDao;
    private final EventChannelB2BService eventChannelB2BService;

    public ChannelAgencyIndexationService(ChannelEventDao channelEventDao,
                                          CacheRepository localCacheRepository,
                                          ChannelConfigCouchDao channelConfigCouchDao,
                                          EventChannelB2BService eventChannelB2BService) {
        this.channelEventDao = channelEventDao;
        this.localCacheRepository = localCacheRepository;
        this.channelConfigCouchDao = channelConfigCouchDao;
        this.eventChannelB2BService = eventChannelB2BService;
    }

    public void prepareB2BContext(EventIndexationContext ctx) {
        var b2bChannels = getB2BChannelInfos(ctx);
        if (CollectionUtils.isEmpty(b2bChannels)) {
            return;
        }
        List<CpanelCanalEventoRecord> channelEvents = extractChannelEvents(ctx);

        var agencyHasAllQuotas = channelEvents.stream()
                .collect(Collectors.toMap(ce -> ce.getIdcanal().longValue(),
                        ce -> ConverterUtils.isByteAsATrue(ce.getTodosgruposventaagencias())));

        ctx.setChannelsWithAgencies(prepareChannelAgencies(ctx, b2bChannels, agencyHasAllQuotas));
        ctx.setChannelConfigsCB(getChannelConfigCB(b2bChannels));
    }

    private List<ChannelInfo> getB2BChannelInfos(EventIndexationContext ctx) {
        return ctx.getChannelEvents()
                .stream()
                .map(ce -> ctx.getChannelInfo(ce.getIdcanal().longValue()))
                .filter(Objects::nonNull)
                .filter(shouldIndex(ctx))
                .collect(Collectors.toList());
    }



    private Map<Integer, ChannelConfigCB> getChannelConfigCB(List<ChannelInfo> b2bChannels) {
        if (CollectionUtils.isNotEmpty(b2bChannels)) {
            List<Long> channelIds = b2bChannels.stream().map(ChannelInfo::getId).toList();
            List<ChannelConfigCB> channelConfigCBs = channelConfigCouchDao.bulkGet(channelIds);
            if (CollectionUtils.isNotEmpty(channelConfigCBs)) {
                return channelConfigCBs.stream().collect(Collectors.toMap(ChannelConfigCB::getId, Function.identity()));
            }
        }
        return null;
    }

    public Map<Long, List<Long>> prepareB2BContext(OccupationIndexationContext ctx) {
        var b2bChannels = ctx.getChannels()
                .values()
                .stream()
                .filter(ChannelInfo::isB2BChannel)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(b2bChannels)) {
            return null;
        }
        var channelsWithAgencies = new HashMap<Long, List<Long>>();

        b2bChannels.forEach(b2bChannel -> {
            List<ClientEntity> agencies = this.eventChannelB2BService.searchChannelAgencies(b2bChannel.getEntityId());
            if (CollectionUtils.isNotEmpty(agencies)) {
                List<Long> ids = agencies.stream().map(c -> c.getId().longValue()).collect(Collectors.toList());
                channelsWithAgencies.put(b2bChannel.getId(), ids);
            }
        });
        return channelsWithAgencies;
    }

    private Map<Long, Map<Long, ChannelAgency>> prepareChannelAgencies(EventIndexationContext ctx,
                                                                       List<ChannelInfo> b2bChannels,
                                                                       Map<Long, Boolean> agencyHasAllQuotas) {
        var channelsWithAgencies = new HashMap<Long, Map<Long, ChannelAgency>>();
        b2bChannels.forEach(b2bChannel -> {
            List<ClientEntity> agencies = this.eventChannelB2BService.searchChannelAgencies(b2bChannel.getEntityId());
            if (CollectionUtils.isNotEmpty(agencies)) {
                Boolean hasAllQuotas = agencyHasAllQuotas.get(b2bChannel.getId());
                if (Boolean.TRUE.equals(hasAllQuotas)) {
                    var agenciesInfo = agencies.stream().map(s -> {
                                Long agencyId = s.getId().longValue();
                                ProfessionalClientConditions response = this.searchConditions(ctx.getEventId(), agencyId, ctx.getEntity().getOperator().getId(), s.getEntityId(), ctx.getEvent().getIdcurrency());
                                return new ChannelAgency(agencyId, response);
                            }
                    ).collect(Collectors.toMap(ChannelAgency::getId, Function.identity()));
                    channelsWithAgencies.put(b2bChannel.getId(), agenciesInfo);
                } else {
                    prepareAgencyQuota(ctx, b2bChannel, agencies, channelsWithAgencies);
                }
            }
        });
        return channelsWithAgencies;
    }

    private void prepareAgencyQuota(EventIndexationContext ctx,
                                    ChannelInfo b2bChannel,
                                    List<ClientEntity> agencies,
                                    Map<Long, Map<Long, ChannelAgency>> channelsWithAgencies) {
        ChannelEventB2BQuotaAssignationsDTO assignations = this.eventChannelB2BService.getChannelEventB2BAssignations(ctx.getEventId(), b2bChannel.getId());
        if (CollectionUtils.isNotEmpty(assignations)) {
            var agenciesInfo = new HashMap<Long, ChannelAgency>();
            agencies.stream().mapToLong(agency -> agency.getId().longValue()).forEach(agencyId -> {
                List<Long> quotas = assignations.stream()
                        .filter(a -> a.getAllClients() || a.getClients().contains(agencyId))
                        .map(ChannelEventB2BQuotaAssignationDTO::getQuota)
                        .map(IdNameDTO::getId)
                        .collect(Collectors.toList());
                ProfessionalClientConditions conditions = this.searchConditions(ctx.getEventId(), agencyId, ctx.getEntity().getOperator().getId(), ctx.getEntity().getId(), ctx.getEvent().getIdcurrency());
                agenciesInfo.put(agencyId, new ChannelAgency(agencyId, quotas, conditions));
            });
            channelsWithAgencies.put(b2bChannel.getId(), agenciesInfo);
        }
    }

    private <T extends BaseIndexationContext<?, ?>> List<CpanelCanalEventoRecord> extractChannelEvents(T ctx) {
        List<CpanelCanalEventoRecord> channelEvents;
        if (ctx instanceof EventIndexationContext ectx) {
            channelEvents = ectx.getChannelEvents();
        } else {
            channelEvents = IndexerUtils.getChannelEvents(channelEventDao.getChannelEvents(ctx.getEventId()), ctx.getEvent());
        }
        return channelEvents;
    }

    private ProfessionalClientConditions searchConditions(Long eventId, Long agencyId, Integer operatorId, Integer entityId, Integer currencyId) {
        return localCacheRepository.cached(CLIENT_CONDITIONS_KEY, 1, MINUTES,
                () -> fetchConditions(eventId, agencyId, operatorId, entityId, currencyId) , new Object[]{eventId, agencyId});
    }

    private ProfessionalClientConditions fetchConditions(Long eventId, Long agencyId, Integer operatorId, Integer entityId, Integer currencyId) {
        ConditionsRequest req = new ConditionsRequest();
        req.setOperatorId(Long.valueOf(operatorId));
        req.setClientEntityId(agencyId);
        req.setEventId(eventId);
        req.setEntityId(Long.valueOf(entityId));
        try {
            return this.eventChannelB2BService.getChannelAgencyConditions(req, currencyId);
        } catch (Exception e) {
            LOGGER.warn("[CHANNEL AGENCY INDEXATION] Error on search conditions for agency {} - {}", agencyId, e.getMessage());
            return null;
        }
    }

    private static Predicate<ChannelInfo> shouldIndex(EventIndexationContext ctx) {
        return c -> c.isB2BChannel() && c.getEntityId() != null && c.getEntityId().equals(ctx.getEvent().getIdentidad().longValue());
    }


}

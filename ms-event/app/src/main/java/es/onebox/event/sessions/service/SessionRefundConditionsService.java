package es.onebox.event.sessions.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.venue.dto.BlockingReason;
import es.onebox.event.datasources.ms.venue.repository.BlockingReasonsRepository;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.converter.SessionRefundConditionsConverter;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionRefundConditions;
import es.onebox.event.sessions.domain.sessionconfig.refundconditions.SessionConditionsMap;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.SessionRefundConditionsDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.annotation.MySQLRead;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static es.onebox.event.sessions.utils.RefundConditionsUtils.buildRefundConditionsMap;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class SessionRefundConditionsService {

    private final SeasonSessionDao seasonSessionDao;
    private final SessionValidationHelper sessionValidationHelper;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final PriceTypeConfigDao priceTypeConfigDao;
    private final RateDao rateDao;
    private final BlockingReasonsRepository blockingReasonsRepository;
    private final SessionRefundConditionsValidationService validationService;

    @Autowired
    public SessionRefundConditionsService(final SeasonSessionDao seasonSessionDao,
                                          final SessionValidationHelper sessionValidationHelper,
                                          final SessionConfigCouchDao sessionConfigCouchDao,
                                          final PriceTypeConfigDao priceTypeConfigDao, final RateDao rateDao,
                                          final SessionRefundConditionsValidationService validationService,
                                          final BlockingReasonsRepository blockingReasonsRepository){

        this.seasonSessionDao = seasonSessionDao;
        this.sessionValidationHelper = sessionValidationHelper;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.priceTypeConfigDao = priceTypeConfigDao;
        this.rateDao = rateDao;
        this.validationService = validationService;
        this.blockingReasonsRepository = blockingReasonsRepository;
    }

    @MySQLRead
    public SessionRefundConditionsDTO getRefundConditions(Long eventId, Long sessionId) {
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId,sessionId);
        SessionRefundConditions src = getSessionRefundConditions(sessionId);
        return SessionRefundConditionsConverter.convert(src);
    }

    private SessionRefundConditions getSessionRefundConditions(Long sessionId) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        SessionRefundConditions src = sessionConfig.getSessionRefundConditions();
        if(isNull(src)){
            throw ExceptionBuilder.build(MsEventSessionErrorCode.SESSION_REFUND_CONDITIONS_NOT_FOUND);
        }

        return src;
    }

    private void setDefaultSessionPackBlockingReason(SessionRefundConditions src, Long venueTemplateId) {
        List<BlockingReason> blockingReasons =  blockingReasonsRepository.getBlockingReasons(venueTemplateId);
        if(CollectionUtils.isNotEmpty(blockingReasons)){
            BlockingReason defaultBlockingReason = blockingReasons.stream()
                    .filter(BlockingReason::getDefault)
                    .findFirst()
                    .orElseThrow(() -> ExceptionBuilder.build(MsEventSessionErrorCode.DEFAULT_BLOCKING_REASON_NOT_FOUND));
            src.setRefundedSessionPackSeatBlockReasonId(defaultBlockingReason.getId());
        }
    }

    @MySQLRead
    public void initRefundConditions(final Long seasonSessionId, final CreateSessionDTO createData) {
        Map<Long, SessionConditionsMap> refundConditionsMap = calculateRefundConditionsMap(seasonSessionId,
                createData.getSeasonSessions(), createData.getVenueConfigId());

        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(seasonSessionId);
        sessionConfig.setSessionRefundConditions(new SessionRefundConditions());
        sessionConfig.getSessionRefundConditions().setSeasonPackAutomaticCalculateConditions(true);
        sessionConfig.getSessionRefundConditions().setSeasonPassRefundConditions(refundConditionsMap);
        sessionConfig.getSessionRefundConditions().setRefundedSeatStatus(TicketStatus.AVAILABLE);
        setDefaultSessionPackBlockingReason(sessionConfig.getSessionRefundConditions(),createData.getVenueConfigId());
        sessionConfigCouchDao.upsert(seasonSessionId.toString(), sessionConfig);
    }

    public void updateRefundConditionsMap(final Long seasonSessionId, final Long venueTemplateId) {
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(seasonSessionId);
        Map<Long, SessionConditionsMap> currentMap = sessionConfig.getSessionRefundConditions().getSeasonPassRefundConditions();

        Map<Long, SessionConditionsMap> updatedMap = calculateRefundConditionsMap(currentMap, seasonSessionId,
                venueTemplateId);

        sessionConfig.getSessionRefundConditions().setSeasonPassRefundConditions(updatedMap);
        sessionConfigCouchDao.upsert(seasonSessionId.toString(), sessionConfig);
    }

    private Map<Long, SessionConditionsMap> calculateRefundConditionsMap(final Map<Long, SessionConditionsMap> currentMap,
                                                                         final Long seasonSessionId, List<Long> sessionIds,
                                                                         final Long venueConfigId) {

        List<ZonaPreciosConfigRecord> priceTypes = priceTypeConfigDao.getPriceZone(venueConfigId, null);
        Collection<RateRecord> rates = rateDao.getRatesBySessionId(seasonSessionId.intValue(), 1000L, 0L);

        if(CollectionUtils.isEmpty(sessionIds)){
            sessionIds = seasonSessionDao.findSessionsBySessionPackId(seasonSessionId);
        }

        return buildRefundConditionsMap(currentMap, sessionIds, priceTypes, rates);
    }

    private Map<Long, SessionConditionsMap> calculateRefundConditionsMap(final Long seasonSessionId, final Long venueTemplateId){
        return this.calculateRefundConditionsMap(null, seasonSessionId, null, venueTemplateId);
    }

    private Map<Long, SessionConditionsMap> calculateRefundConditionsMap(final Long seasonSessionId,
                                                                         final List<Long> seasonSessions,
                                                                         final Long venueTemplateId) {
        return this.calculateRefundConditionsMap(null, seasonSessionId, seasonSessions, venueTemplateId);
    }

    private Map<Long, SessionConditionsMap> calculateRefundConditionsMap(final Map<Long, SessionConditionsMap> currentMap,
                                                                         final Long seasonSessionId, final Long venueTemplateId){
        return this.calculateRefundConditionsMap(currentMap, seasonSessionId,null, venueTemplateId);
    }

    @MySQLRead
    public void updateRefundConditions(final Long eventId, final Long seasonSessionId,
                                       final SessionRefundConditionsDTO inputDto) {

        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId,seasonSessionId);
        SessionRefundConditions currentEntity = getSessionRefundConditions(seasonSessionId);
        SessionRefundConditions newEntity = SessionRefundConditionsConverter.convert(inputDto);

        validationService.validate(currentEntity, newEntity, session);

        if(isTrue(newEntity.getSeasonPackAutomaticCalculateConditions())){
            Map<Long, SessionConditionsMap> refundConditionsMap = calculateRefundConditionsMap(seasonSessionId,
                    session.getVenueTemplateId().longValue());
            newEntity.setSeasonPassRefundConditions(refundConditionsMap);
        }

        SessionRefundConditions updatedEntity = SessionRefundConditionsConverter.mergeToEntity(newEntity, currentEntity);

        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(seasonSessionId);
        sessionConfig.setSessionRefundConditions(updatedEntity);
        sessionConfigCouchDao.upsert(seasonSessionId.toString(), sessionConfig);
    }

}

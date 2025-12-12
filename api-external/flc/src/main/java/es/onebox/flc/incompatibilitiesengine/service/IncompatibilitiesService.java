package es.onebox.flc.incompatibilitiesengine.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.common.datasources.ms.entity.MsEntityDatasource;
import es.onebox.common.datasources.ms.entity.dto.ExternalMgmtConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.SessionSearchFilter;
import es.onebox.common.datasources.ms.event.request.UpdateSessionRequest;
import es.onebox.common.datasources.ms.event.request.UpdateSessionsRequest;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.flc.incompatibilitiesengine.dto.LoginData;
import es.onebox.flc.utils.AuthenticationUtils;
import es.onebox.hazelcast.core.service.HazelcastLockService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IncompatibilitiesService {

    public static final String ENTITY_ID = "entityId";
    private static final String PUBLISH_CACHE = "publishcache_";
    private static final String ID = "id";
    private static final String EVENT_ID = "eventId";
    private final MsEntityDatasource msEntityDatasource;
    private final MsEventRepository msEventRepository;
    private final String user;
    private final String password;
    private CacheRepository cacheRepository;
    private HazelcastLockService hazelcastLockService;
    private static final int LOCK_TTL = 10000;
    private static final int EXPIRE_TIME = 60;
    private static final int ATTEMPTS_LIMIT = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(IncompatibilitiesService.class);

    @Autowired
    public IncompatibilitiesService (MsEntityDatasource msEntityDatasource,
                                     MsEventRepository msEventRepository,
                                     CacheRepository cacheRepository,
                                     HazelcastLockService hazelcastLockService,
                                     @Value("${incompatibilities-engine.user}") String user,
                                     @Value("${incompatibilities-engine.password}") String password){
        this.msEntityDatasource = msEntityDatasource;
        this.user = user;
        this.password = password;
        this.msEventRepository = msEventRepository;
        this.cacheRepository = cacheRepository;
        this.hazelcastLockService = hazelcastLockService;
    }


    public LoginData getLoginData() {
        Integer entityId = (Integer) AuthenticationUtils.getAttribute(ENTITY_ID);
        List<ExternalMgmtConfigDTO> externalMgmtConfigDTOList =
                msEntityDatasource.getExternalMgmtConfig(entityId.longValue());
        ExternalMgmtConfigDTO externalMgmtConfigDTO =
                externalMgmtConfigDTOList.stream()
                        .filter(elem -> elem.getEndpointType().equals(7))
                        .findAny().orElse(null);

        if(externalMgmtConfigDTO == null){
            throw new OneboxRestException(ApiExternalErrorCode.NOT_FOUND);
        }

        LoginData loginData = new LoginData();
        loginData.setUrl(externalMgmtConfigDTO.getEndpointUrl());
        loginData.setUser(Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8)));
        loginData.setPassword(Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));

        return loginData;
    }

    public void changePublishState(List<Long> sessionIds, Long sequenceNumber, String unpublishReason,
                                   boolean publishState) {
        if(publishState){
            LOGGER.info("Publishing session with ids {} and sequence number {}", sessionIds.toString(), sequenceNumber);
        }
        else{
            LOGGER.info("Unpublishing:\nsessionIds: {}.\nSequence number: {}", sessionIds.toString(), sequenceNumber);
        }

        if (CollectionUtils.isEmpty(sessionIds) || sequenceNumber == null) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }

        Integer entityId = (Integer) AuthenticationUtils.getAttribute(ENTITY_ID);

        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setId(sessionIds);
        sessionSearchFilter.setEntityId(entityId.longValue());

        List<String> sessionFields = new ArrayList<>();
        sessionFields.add(ID);
        sessionFields.add(EVENT_ID);
        sessionSearchFilter.setFields(sessionFields);

        SessionsDTO sessions = msEventRepository.getSessions(sessionSearchFilter);

        if (sessionIds.size() != sessions.getData().size()) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.SESSION_NOT_ACCESSIBLE_FROM_ENTITY);
        }

        Map<Long, List<SessionDTO>> sessionsByEventId =
                sessions.getData().stream().collect(Collectors.groupingBy(SessionDTO::getEventId));

        for (Map.Entry<Long, List<SessionDTO>> entry : sessionsByEventId.entrySet()) {
            Long eventId = entry.getKey();
            UpdateSessionsRequest updateSessionsRequest = createUpdateSessionsRequest(entry.getValue(),
                    unpublishReason,  publishState);
            changeSessionsPublishStateWithRetries(eventId, sequenceNumber, updateSessionsRequest);
        }
    }

    private void updateSessions(Long eventId, Long sequenceNumber, UpdateSessionsRequest updateSessionsRequest) {
        if(canChangePublishState(eventId, sequenceNumber)){
            msEventRepository.updateSessions(eventId, updateSessionsRequest);
        }
    }

    private void changeSessionsPublishStateWithRetries(Long eventId, Long sequenceNumber,
                                                       UpdateSessionsRequest updateSessionsRequest) {

        int attempts = 0;
        Boolean updated = false;

        do {
            try {
                hazelcastLockService.lockedExecution(()->
                                updateSessions(eventId, sequenceNumber,updateSessionsRequest),
                        PUBLISH_CACHE + eventId, LOCK_TTL);
                updated = true;
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
            attempts++;

        } while (Boolean.FALSE.equals(updated) && attempts < ATTEMPTS_LIMIT);

        if (Boolean.FALSE.equals(updated)) {
            LOGGER.error("Error Changing session publish state, session Id: {}. sn: {}.",
                    eventId, sequenceNumber);
        }
    }

    private UpdateSessionsRequest createUpdateSessionsRequest(List<SessionDTO> sessions, String unpublishReason,
                                                              boolean publishState) {
        UpdateSessionsRequest updateSessionsRequest = new UpdateSessionsRequest();

        UpdateSessionRequest updateSessionRequest = new UpdateSessionRequest();
        updateSessionRequest.setEnableChannels(publishState);
        updateSessionRequest.setEnableSales(publishState);
        updateSessionRequest.setUnpublishReason(unpublishReason);

        updateSessionsRequest.setValue(updateSessionRequest);
        updateSessionsRequest.setIds(new ArrayList<>());

        for (SessionDTO session : sessions) {
            updateSessionsRequest.getIds().add(session.getId());
        }

        return updateSessionsRequest;
    }

    private boolean canChangePublishState(Long eventId, Long sequenceNumber) {

        boolean result = false;
        String eventKey = PUBLISH_CACHE + eventId;
        Long cachedSequence = cacheRepository.get(eventKey, Long.class);

        LOGGER.info("[SESSION PUBLISH] eventId: {} comparing priority of operation: {} <= {}",
                eventId, cachedSequence, sequenceNumber);

        if (cachedSequence == null || cachedSequence <= sequenceNumber) {
            cacheRepository.set(eventKey, sequenceNumber, EXPIRE_TIME, TimeUnit.SECONDS, null);
            result = true;
        }

        return result;
    }

}

package es.onebox.event.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@CouchRepository(
        prefixKey = SessionConfigCouchDao.SESSION_CONFIG,
        bucket = SessionConfigCouchDao.ONEBOX_OPERATIVE,
        scope = SessionConfigCouchDao.SESSIONS_SCOPE,
        collection = SessionConfigCouchDao.CONFIG_COLLECTION
)
public class SessionConfigCouchDao extends AbstractCouchDao<SessionConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SESSION_CONFIG = "sessionConfig";
    public static final String SESSIONS_SCOPE = "sessions";
    public static final String CONFIG_COLLECTION = "configs";

    public SessionConfig getOrInitSessionConfig(Long sessionId) {
        SessionConfig sessionConfig = super.get(sessionId.toString());
        if (sessionConfig == null) {
            sessionConfig = new SessionConfig();
            sessionConfig.setSessionId(sessionId.intValue());
        }
        return sessionConfig;
    }

    public List<SessionConfig> bulkGet(List<Long> sessionIds) {
        return bulkGet(getSessionConfigKeys(sessionIds));
    }

    private static List<Key> getSessionConfigKeys(List<Long> sessionIds) {
        return sessionIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(sId)});
            return key;
        }).collect(Collectors.toList());
    }

    public List<SessionConfig> getSessionConfigsByEvent(Long eventId) {
        final String OBXOP_SESSIONS_SECMKT = quoteClause(ONEBOX_OPERATIVE) + "." + quoteClause(SESSIONS_SCOPE) + "." + quoteClause("configs");

        String n1ckelQuery = """
                SELECT sc.*
                FROM %s AS sc
                WHERE sc.eventId is not null AND sc.eventId = $eventId
                """
                .formatted(OBXOP_SESSIONS_SECMKT);

        Map<String, Object> params = new HashMap<>();
        params.put("eventId", eventId);

        return queryList(n1ckelQuery, params, SessionConfig.class);
    }

    public SessionDynamicPriceConfig findDynamicPriceBySessionId(Long sessionId) {
        SessionConfig sessionConfig = get(sessionId.toString());
        return sessionConfig != null ? sessionConfig.getSessionDynamicPriceConfig() : null;
    }

}

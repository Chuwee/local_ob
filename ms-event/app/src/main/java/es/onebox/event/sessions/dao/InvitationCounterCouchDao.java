package es.onebox.event.sessions.dao;

import static es.onebox.event.sessions.dao.InvitationCounterCouchDao.INVITATION_COLLECTION;
import static es.onebox.event.sessions.dao.InvitationCounterCouchDao.INVITATION_COUNTER;
import static es.onebox.event.sessions.dao.InvitationCounterCouchDao.ONEBOX_OPERATIVE;
import static es.onebox.event.sessions.dao.InvitationCounterCouchDao.SESSIONS_SCOPE;

import org.springframework.stereotype.Repository;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCounterCouchDao;

@Repository
@CouchRepository(prefixKey = INVITATION_COUNTER, bucket = ONEBOX_OPERATIVE, scope = SESSIONS_SCOPE, collection = INVITATION_COLLECTION)
public class InvitationCounterCouchDao extends AbstractCounterCouchDao<Long> {

	public static final String ONEBOX_OPERATIVE = "onebox-operative";
	public static final String INVITATION_COUNTER = "invitationCounter";
	public static final String SESSIONS_SCOPE = "sessions";
	public static final String INVITATION_COLLECTION = "invitation-counters";

	public Long get(Integer sessionId, Integer priceZoneId) {
		return super.get(buildKey(sessionId, priceZoneId));
	}

	public Long insert(Integer sessionId, Integer priceZoneId, long amount) {
		return super.createCounter(buildKey(sessionId, priceZoneId), amount);
	}

	public Long increment(Integer sessionId, Integer priceZoneId, long amount) {
		return super.incrementCounter(buildKey(sessionId, priceZoneId), amount);
	}

	public Long decrement(Integer sessionId, Integer priceZoneId, long amount) {
		return super.decrementCounter(buildKey(sessionId, priceZoneId), amount);
	}

	private String buildKey(Integer sessionId, Integer priceZoneId) {
		return sessionId + "_" + priceZoneId;
	}
}
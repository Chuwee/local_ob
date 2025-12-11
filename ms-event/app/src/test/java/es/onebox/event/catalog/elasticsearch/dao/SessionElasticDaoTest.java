package es.onebox.event.catalog.elasticsearch.dao;

import es.onebox.elasticsearch.dao.test.BaseElasticsearchTest;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

public class SessionElasticDaoTest extends BaseElasticsearchTest<SessionElasticDao, SessionData> {

    @InjectMocks
    private SessionElasticDao sessionElasticDao;

    @Override
    protected String getIndexFile() {
        return "/dao/eventdata_index.json";
    }

    @Override
    protected String getDataFile() {
        return "/dao/eventdata.json";
    }

    @Override
    protected SessionElasticDao elasticsearchDao() {
        return sessionElasticDao;
    }

    @Test
    public void findByID() {
        Long eventId = 15890L;
        Long sessionId = 885968L;

        SessionData sessionData = sessionElasticDao.get(sessionId, eventId);

        Assertions.assertEquals(sessionId, sessionData.getSession().getSessionId());
        Assertions.assertEquals(eventId, sessionData.getSession().getEventId());
        Assertions.assertEquals("Session test", sessionData.getSession().getSessionName());
        Assertions.assertEquals("Session Event test", sessionData.getSession().getEventName());

    }

    @Test
    public void getSessions() {
        Long eventId = 15890L;

        List<Long> sessionData = sessionElasticDao.getSessions(eventId);

        Assertions.assertEquals(2, sessionData.size());
        Assertions.assertEquals(Long.valueOf(885968), sessionData.get(0));
        Assertions.assertEquals(Long.valueOf(885969), sessionData.get(1));
    }

}

package es.onebox.event.catalog.elasticsearch.dao;

import es.onebox.elasticsearch.dao.test.BaseElasticsearchTest;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

public class EnabledChannelSessionElasticDaoTest extends BaseElasticsearchTest<ChannelSessionElasticDao, ChannelSessionData> {

    @InjectMocks
    private ChannelSessionElasticDao channelSessionElasticDao;

    @Override
    protected String getIndexFile() {
        return "/dao/eventdata_index.json";
    }

    @Override
    protected String getDataFile() {
        return "/dao/eventdata.json";
    }

    @Override
    protected ChannelSessionElasticDao elasticsearchDao() {
        return channelSessionElasticDao;
    }

    @Test
    public void findByID() {
        Long sessionId = 885968L;
        Long eventId = 15890L;
        Long channelId = 1133L;

        List<ChannelSessionData> channelSessions = channelSessionElasticDao.getBySessionId(sessionId, eventId);

        Assertions.assertEquals(1, channelSessions.size());
        Assertions.assertEquals(eventId, channelSessions.get(0).getChannelSession().getEventId());
        Assertions.assertEquals(channelId, channelSessions.get(0).getChannelSession().getChannelId());
    }

}

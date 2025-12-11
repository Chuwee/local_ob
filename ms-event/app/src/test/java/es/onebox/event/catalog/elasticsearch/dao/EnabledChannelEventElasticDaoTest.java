package es.onebox.event.catalog.elasticsearch.dao;

import es.onebox.elasticsearch.dao.test.BaseElasticsearchTest;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class EnabledChannelEventElasticDaoTest extends BaseElasticsearchTest<ChannelEventElasticDao, ChannelEventData> {

    @InjectMocks
    private ChannelEventElasticDao channelEventElasticDao;

    @Override
    protected String getIndexFile() {
        return "/dao/eventdata_index.json";
    }

    @Override
    protected String getDataFile() {
        return "/dao/eventdata.json";
    }

    @Override
    protected ChannelEventElasticDao elasticsearchDao() {
        return channelEventElasticDao;
    }

    @Test
    public void findByID() {
        Long eventId = 15890L;
        Long channelId = 1133L;

        ChannelEventData channelEventData = channelEventElasticDao.get(channelId, eventId);

        Assertions.assertEquals(eventId, channelEventData.getChannelEvent().getEventId());
        Assertions.assertEquals(channelId, channelEventData.getChannelEvent().getChannelId());
        Assertions.assertEquals("Entradas.com - Taquilla", channelEventData.getChannelEvent().getChannelName());

    }

}

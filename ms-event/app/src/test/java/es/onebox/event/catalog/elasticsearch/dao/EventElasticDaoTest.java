package es.onebox.event.catalog.elasticsearch.dao;

import es.onebox.elasticsearch.dao.test.BaseElasticsearchTest;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class EventElasticDaoTest extends BaseElasticsearchTest<EventElasticDao, EventData> {

    @InjectMocks
    private EventElasticDao eventDataElasticDao;

    @Override
    protected String getIndexFile() {
        return "/dao/eventdata_index.json";
    }

    @Override
    protected String getDataFile() {
        return "/dao/eventdata.json";
    }

    @Override
    protected EventElasticDao elasticsearchDao() {
        return eventDataElasticDao;
    }

    @Test
    public void findByID() {
        Long eventId = 15890L;

        EventData eventData = eventDataElasticDao.get(eventId);

        Assertions.assertEquals(eventId, eventData.getEvent().getEventId());
        Assertions.assertEquals("Event test", eventData.getEvent().getEventName());

    }

}

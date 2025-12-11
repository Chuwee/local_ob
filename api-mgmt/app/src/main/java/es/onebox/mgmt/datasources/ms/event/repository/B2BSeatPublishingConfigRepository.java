package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.B2BSeatPublishingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class B2BSeatPublishingConfigRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public B2BSeatPublishingConfigRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public B2BSeatPublishingConfig getConfig(Long eventId, Long channelId, Long venueTemplateId) { return msEventDatasource.getSeatPublishingConfig(eventId, channelId, venueTemplateId); }

    public void updateConfig(Long eventId, Long channelId, Long venueTemplateId, B2BSeatPublishingConfig b2BSeatPublishingConfig) { msEventDatasource.updateSeatPublishingConfig(eventId, channelId, venueTemplateId, b2BSeatPublishingConfig); }
}

package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.converter.B2BPublishingConfigConverter;
import es.onebox.event.events.dao.B2BPublishingConfigCouchDao;
import es.onebox.event.events.domain.B2BSeatPublishingConfig;
import es.onebox.event.events.dto.B2BSeatPublishingConfigDTO;
import es.onebox.event.exception.MsEventErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class B2BPublishingConfigService {

    private final B2BPublishingConfigCouchDao b2BPublishingConfigCouchDao;

    @Autowired
    public B2BPublishingConfigService(B2BPublishingConfigCouchDao b2BPublishingConfigCouchDao) {
        this.b2BPublishingConfigCouchDao = b2BPublishingConfigCouchDao;
    }

    public B2BSeatPublishingConfigDTO getConfig(Long eventId, Long channelId, Long venueTemplateId) {

        B2BSeatPublishingConfig b2BSeatPublishingConfig =  b2BPublishingConfigCouchDao.get(eventId.toString(), channelId.toString(), venueTemplateId.toString());
        if (b2BSeatPublishingConfig == null) {
            throw new OneboxRestException(MsEventErrorCode.CONFIG_NOT_FOUND);
        }

        return B2BPublishingConfigConverter.fromMsEvent(b2BSeatPublishingConfig);
    }

    public void updateConfig(Long eventId, Long channelId, Long venueTemplateId, B2BSeatPublishingConfigDTO b2BSeatPublishingConfigDTO) {

        B2BSeatPublishingConfig b2BSeatPublishingConfig = B2BPublishingConfigConverter.toMsEvent(eventId, channelId, venueTemplateId, b2BSeatPublishingConfigDTO);
        String documentId = createDocumentId(eventId, channelId, venueTemplateId);

        b2BPublishingConfigCouchDao.upsert(documentId, b2BSeatPublishingConfig);
    }

    private String createDocumentId(Long eventId, Long channelId, Long venueTemplateId) {
        return eventId + "_" + channelId + "_" + venueTemplateId;
    }
}

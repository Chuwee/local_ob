package es.onebox.event.common.services;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.events.converter.EventCommunicationElementConverter;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.EventLanguageDao;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.utils.EventCommunicationElementUtils;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonCommunicationElementService {

    public static final int DEFAULT_COMELEMENT_POSITION = 1;
    public static final int EVENT_TITLE_COMELEMENT_TAG_ID = 1;

    private final EventCommunicationElementDao eventCommunicationElementDao;

    private final EventLanguageDao eventLanguageDao;
    private final StaticDataContainer staticDataContainer;
    private final S3BinaryRepository s3OneboxRepository;

    @Autowired
    public CommonCommunicationElementService(EventCommunicationElementDao eventCommunicationElementDao,
                                             EventLanguageDao eventLanguageDao,
                                             StaticDataContainer staticDataContainer,
                                             S3BinaryRepository s3OneboxRepository) {
        this.eventCommunicationElementDao = eventCommunicationElementDao;
        this.eventLanguageDao = eventLanguageDao;
        this.staticDataContainer = staticDataContainer;
        this.s3OneboxRepository = s3OneboxRepository;
    }

    public List<EventCommunicationElementDTO> findCommunicationElements(Long eventId, EventCommunicationElementFilter filter,
                                                                        EventRecord event) {
        if (StringUtils.isNotBlank(filter.getLanguage())) {
            filter.setLanguageId(staticDataContainer.getLanguageByCode(filter.getLanguage()));
        }
        List<CpanelElementosComEventoRecord> records =
                eventCommunicationElementDao.findCommunicationElements(eventId, null, null, filter);

        return EventCommunicationElementConverter.fromRecords(records, event, staticDataContainer);
    }

    public void updateCommunicationElements(Long eventId, List<EventCommunicationElementDTO> elements,
                                            EventRecord event) {

        List<CpanelElementosComEventoRecord> records =
                eventCommunicationElementDao.findCommunicationElements(eventId, null, null, null);

        for (EventCommunicationElementDTO element : elements) {
            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());
            CpanelElementosComEventoRecord record = EventCommunicationElementUtils.checkAndGetElement(element, languageId, records);

            if (record == null) {
                CpanelElementosComEventoRecord newRecord = new CpanelElementosComEventoRecord();
                newRecord.setIdevento(eventId.intValue());
                newRecord.setIdtag(element.getTagId());
                newRecord.setIdioma(languageId);
                newRecord.setPosition(CommonUtils.ifNull(element.getPosition(), DEFAULT_COMELEMENT_POSITION));
                newRecord.setDestino(1);
                newRecord.setAlttext(element.getAltText());
                record = eventCommunicationElementDao.insert(newRecord);
            }

            EventTagType tagType = EventTagType.getTagTypeById(element.getTagId());
            if (tagType.isImage()) {
                String filename = EventCommunicationElementUtils.uploadImage(s3OneboxRepository, record, element,
                        S3URLResolver.S3ImageType.EVENT_IMAGE, event.getOperatorId(), event.getIdentidad(),
                        eventId, null, false);
                record.setValor(filename);
                record.setAlttext(element.getAltText());
                if (element.getPosition() != null) {
                    record.setPosition(element.getPosition());
                }
            } else {
                record.setValor(element.getValue());
            }
            eventCommunicationElementDao.update(record);
        }
    }

    public void createDefaultEventCommunicationElements(CpanelEventoRecord event) {
        List<EventLanguageRecord> languages = eventLanguageDao.findByEventId(event.getIdevento().longValue());
        languages.forEach(language -> {
            CpanelElementosComEventoRecord elemCom = new CpanelElementosComEventoRecord();
            elemCom.setIdevento(event.getIdevento());
            elemCom.setValor(event.getNombre());
            elemCom.setIdioma(language.getId().intValue());
            elemCom.setIdtag(EVENT_TITLE_COMELEMENT_TAG_ID);
            elemCom.setDestino(1);
            elemCom.setPosition(DEFAULT_COMELEMENT_POSITION);
            eventCommunicationElementDao.insert(elemCom);
        });
    }
}

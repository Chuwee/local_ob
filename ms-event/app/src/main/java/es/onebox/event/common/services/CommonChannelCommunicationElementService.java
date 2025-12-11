package es.onebox.event.common.services;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.events.converter.ChannelEventCommunicationElementConverter;
import es.onebox.event.events.dao.ChannelEventCommunicationElementDao;
import es.onebox.event.events.dto.ChannelEventImageConfigDTO;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.enums.ImageOrigin;
import es.onebox.event.events.request.ChannelEventCommunicationElementFilter;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoCanalRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;

@Service
public class CommonChannelCommunicationElementService {

    public static final int DEFAULT_COMELEMENT_POSITION = 1;

    private final ChannelEventCommunicationElementDao channelEventCommunicationElementDao;
    private final ItemDescSequenceDao itemDescSequenceDao;

    private final StaticDataContainer staticDataContainer;
    private final S3BinaryRepository s3OneboxRepository;

    @Autowired
    public CommonChannelCommunicationElementService(ChannelEventCommunicationElementDao channelEventCommunicationElementDao,
                                                    ItemDescSequenceDao itemDescSequenceDao, StaticDataContainer staticDataContainer,
                                                    S3BinaryRepository s3OneboxRepository) {
        this.channelEventCommunicationElementDao = channelEventCommunicationElementDao;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.staticDataContainer = staticDataContainer;
        this.s3OneboxRepository = s3OneboxRepository;
    }

    public List<EventCommunicationElementDTO> findCommunicationElements(EventChannelRecord channelEvent, Long sessionId, ChannelEventCommunicationElementFilter filter) {
        if (StringUtils.isNotBlank(filter.getLanguage())) {
            filter.setLanguageId(staticDataContainer.getLanguageByCode(filter.getLanguage()));
        }
        List<CpanelElementosComEventoCanalRecord> records = channelEventCommunicationElementDao.findCommunicationElements(channelEvent.getId(), sessionId, filter);
        return ChannelEventCommunicationElementConverter.fromRecords(records, channelEvent, staticDataContainer);
    }

    public void updateChannelCommunicationElements(EventChannelRecord channelEvent, Long sessionId, List<EventCommunicationElementDTO> elements) {
        List<CpanelElementosComEventoCanalRecord> existingRecords =
                channelEventCommunicationElementDao.findCommunicationElements(channelEvent.getId(), sessionId, null);

        for (EventCommunicationElementDTO element : elements) {
            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());

            CpanelElementosComEventoCanalRecord record = checkAndGetElement(element, languageId, sessionId, existingRecords);

            if (record == null) {
                record = buildNewElementRecord(channelEvent, sessionId, element, languageId);
                channelEventCommunicationElementDao.insert(record);
            } else {
                updateElementRecord(record, element, channelEvent);
                channelEventCommunicationElementDao.update(record);
            }
        }
    }

    private CpanelElementosComEventoCanalRecord buildNewElementRecord(EventChannelRecord channelEvent, Long sessionId,
                                                                      EventCommunicationElementDTO element, Integer languageId) {
        CpanelElementosComEventoCanalRecord record = new CpanelElementosComEventoCanalRecord();
        record.setIdelemento(itemDescSequenceDao.insertNewRecord());
        record.setIdcanaleevento(channelEvent.getId().intValue());

        if (sessionId != null) {
            record.setIdsesion(sessionId.intValue());
        }

        record.setIdtag(element.getTagId());
        record.setIdioma(languageId);
        record.setPosition(CommonUtils.ifNull(element.getPosition(), DEFAULT_COMELEMENT_POSITION));

        setValor(record, element, channelEvent);

        return record;
    }

    private void updateElementRecord(CpanelElementosComEventoCanalRecord record,
                                     EventCommunicationElementDTO element, EventChannelRecord channelEvent) {
        setValor(record, element, channelEvent);

        if (element.getPosition() != null) {
            record.setPosition(element.getPosition());
        }
    }

    private void setValor(CpanelElementosComEventoCanalRecord record,
                          EventCommunicationElementDTO element, EventChannelRecord channelEvent) {
        EventTagType tagType = EventTagType.getTagTypeById(element.getTagId());

        if (tagType.isImage()) {
            String filename = CommunicationElementsUtils.uploadImage(s3OneboxRepository, record, element,
                    channelEvent.getOperatorId(), false);
            record.setValor(filename);
        } else {
            record.setValor(element.getValue());
        }
    }

    private CpanelElementosComEventoCanalRecord checkAndGetElement(EventCommunicationElementDTO element, Integer languageId, Long sessionId, List<CpanelElementosComEventoCanalRecord> records) {
        return checkAndGetElementsStream(element, languageId, sessionId, records).findFirst().orElse(null);
    }

    private Stream<CpanelElementosComEventoCanalRecord> checkAndGetElementsStream(EventCommunicationElementDTO element, Integer languageId, Long sessionId, List<CpanelElementosComEventoCanalRecord> records) {
        if (element.getTagId() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "tag id is mandatory", null);
        }
        if (element.getLanguage() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "language code id is mandatory", null);
        }
        if (languageId == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER_FORMAT, "language code invalid", null);
        }

        return records.stream().
                filter(e -> e.getIdtag().equals(element.getTagId()) &&
                        e.getIdioma().equals(languageId) &&
                        (element.getPosition() == null || e.getPosition().equals(element.getPosition())) &&
                        ((sessionId == null && e.getIdsesion() == null) || (sessionId != null && sessionId.equals(e.getIdsesion() != null ? e.getIdsesion().longValue() : null)))
                );
    }

    public List<ChannelEventImageConfigDTO> buildChannelEventImageConfig(EventChannelRecord channelEvent, SessionsDTO sessions) {
        ChannelEventCommunicationElementFilter filter = new ChannelEventCommunicationElementFilter();
        filter.setIncludeAllSessions(true);
        List<CpanelElementosComEventoCanalRecord> records = channelEventCommunicationElementDao.findCommunicationElements(channelEvent.getId(), null, filter);

        records = records.stream()
                .filter(record -> record.getValor() != null)
                .collect(Collectors.toList());

        if(CommonUtils.isEmpty(records)) {
            return sessions.getData().stream().map(session -> {
                ChannelEventImageConfigDTO config = new ChannelEventImageConfigDTO();
                config.setSessionId(session.getId());
                config.setImageOrigin(ImageOrigin.EVENT);
                return config;
            }).collect(Collectors.toList());
        }
        Boolean hasChannelEventImages = records.stream().anyMatch(record -> record.getIdsesion() == null);
        Set<Integer> recordSessionIds = records.stream().map(CpanelElementosComEventoCanalRecord::getIdsesion).collect(Collectors.toSet());
        return sessions.getData().stream().map(session -> {
            ChannelEventImageConfigDTO config = new ChannelEventImageConfigDTO();
            config.setSessionId(session.getId());
            if (recordSessionIds.contains(session.getId().intValue())) {
                config.setImageOrigin(ImageOrigin.SESSION);
            } else {
                config.setImageOrigin(hasChannelEventImages ? ImageOrigin.CHANNEL_EVENT : ImageOrigin.EVENT);
            }
            return config;
        }).collect(Collectors.toList());
    }
}

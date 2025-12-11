package es.onebox.event.sessions.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.converter.EventCommunicationElementConverter;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.service.EventCommunicationElementService;
import es.onebox.event.events.utils.EventCommunicationElementUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.UpdateSessionCommunicationElementsBulkDTO;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class SessionCommunicationElementsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionCommunicationElementsService.class);

    private final S3BinaryRepository s3OneboxRepository;
    private final StaticDataContainer staticDataContainer;
    private final EventCommunicationElementDao communicationElementDao;
    private final SessionTicketCommunicationElementService sessionTicketCommunicationElementService;
    private SessionValidationHelper sessionValidationHelper;

    @Autowired
    public SessionCommunicationElementsService(S3BinaryRepository s3OneboxRepository,
                                               StaticDataContainer staticDataContainer,
                                               EventCommunicationElementDao communicationElementDao,
                                               SessionTicketCommunicationElementService sessionTicketCommunicationElementService,
                                               SessionValidationHelper sessionValidationHelper) {
        this.s3OneboxRepository = s3OneboxRepository;
        this.staticDataContainer = staticDataContainer;
        this.communicationElementDao = communicationElementDao;
        this.sessionTicketCommunicationElementService = sessionTicketCommunicationElementService;
        this.sessionValidationHelper = sessionValidationHelper;
    }

    @MySQLRead
    public List<EventCommunicationElementDTO> findCommunicationElements(Long eventId, Long sessionId,
                                                                        EventCommunicationElementFilter filter) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        List<CpanelElementosComEventoRecord> records =
                communicationElementDao.findCommunicationElements(null, Collections.singleton(sessionId.intValue()), null, filter);

        return EventCommunicationElementConverter.fromRecords(records, session, staticDataContainer);
    }

    @MySQLWrite
    public void updateCommunicationElements(Long eventId, Long sessionId, List<EventCommunicationElementDTO> elements) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);

        List<CpanelElementosComEventoRecord> records =
                communicationElementDao.findCommunicationElements(null, Collections.singleton(sessionId.intValue()), null, null);

        for (EventCommunicationElementDTO element : elements) {
            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());
            CpanelElementosComEventoRecord record = EventCommunicationElementUtils.checkAndGetElement(element, languageId, records);

            if (record == null) {
                CpanelElementosComEventoRecord newRecord = createNewComElemRecord(element, sessionId, languageId);
                record = communicationElementDao.insert(newRecord);
            }

            updateEventCommunicationRecord(element, record, eventId, session);
            communicationElementDao.update(record);
        }
    }

    @MySQLWrite
    public void updateCommunicationElementsBulk(Long eventId, UpdateSessionCommunicationElementsBulkDTO data) {
        Map<Integer, SessionRecord> sessions = sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, data.getIds());

        Set<Integer> sessionIds = data.getIds().stream().map(Long::intValue).collect(Collectors.toSet());
        List<CpanelElementosComEventoRecord> records = communicationElementDao
                .findCommunicationElements(null, sessionIds, null, null);

        for (EventCommunicationElementDTO element : data.getValues()) {
            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());
            List<CpanelElementosComEventoRecord> elementRecords = EventCommunicationElementUtils.checkAndGetElements(element, languageId, records);
            List<Long> sessionIdsWithoutElement = obtainSessionIdsWithoutElement(elementRecords, data.getIds());

            if (!sessionIdsWithoutElement.isEmpty()) {
                List<CpanelElementosComEventoRecord> newElements =
                        sessionIdsWithoutElement.stream()
                                .map(id -> createNewComElemRecord(element, id, languageId))
                                .map(r -> updateEventCommunicationRecord(element, r, eventId, sessions.get(r.getIdsesion())))
                                .collect(Collectors.toList());
                try {
                    communicationElementDao.bulkInsertRecordsWithRetries(newElements, 10);
                }catch (PessimisticLockingFailureException e) {
                    LOGGER.error("EventId: {} - SessionId: {} - Error updating web communication element.",
                            eventId, sessionIds.stream().map(String::valueOf).collect(Collectors.joining (",")), e);
                    throw new OneboxRestException(MsEventErrorCode.COMMUNICATION_ELEMENT_BULK_UPDATE_LOCKED, e);
                }
            }
            elementRecords.forEach(r -> updateEventCommunicationRecord(element, r, eventId, sessions.get(r.getIdsesion())));
            try {
                communicationElementDao.bulkUpdateRecordsWithRetries(elementRecords, 10);
            }catch (PessimisticLockingFailureException e) {
                LOGGER.error("EventId: {} - SessionId: {} - Error updating web communication element.",
                        eventId, sessionIds.stream().map(String::valueOf).collect(Collectors.joining (",")), e);
                throw new OneboxRestException(MsEventErrorCode.COMMUNICATION_ELEMENT_BULK_UPDATE_LOCKED, e);
            }
        }
    }

    @MySQLWrite
    public void deleteCommunicationElementsBulk(Long eventId, String language, List<Long> sessionIds) {
        Map<Integer, SessionRecord> sessions = sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, sessionIds);
        Set<Integer> sessionIdsSet = sessionIds.stream().map(Long::intValue).collect(Collectors.toSet());
        List<CpanelElementosComEventoRecord> records = communicationElementDao
                .findCommunicationElements(null, sessionIdsSet, null, null);


        Integer languageCode = staticDataContainer.getLanguageByCode(language);
        records = records.stream().filter(r -> r.getIdioma().equals(languageCode))
                .filter(r -> EventTagType.getTagTypeById(r.getIdtag()).isImage()).collect(Collectors.toList());

        for (CpanelElementosComEventoRecord r : records) {
            EventCommunicationElementDTO element = new EventCommunicationElementDTO();
            element.setTagId(r.getIdtag());
            element.setLanguage(language);
            element.setImageBinary(Optional.empty());
            updateEventCommunicationRecord(element, r, eventId, sessions.get(r.getIdsesion()));
            communicationElementDao.update(r);
        }
    }

    private CpanelElementosComEventoRecord updateEventCommunicationRecord(EventCommunicationElementDTO element,
                                                                          CpanelElementosComEventoRecord record, Long eventId,
                                                                          SessionRecord session) {
        EventTagType tagType = EventTagType.getTagTypeById(element.getTagId());
        if (tagType.isImage()) {
            String filename = EventCommunicationElementUtils.uploadImage(s3OneboxRepository,
                    record, element, S3URLResolver.S3ImageType.SESSION_IMAGE, session.getOperatorId(),
                    session.getEntityId(), eventId, session.getIdsesion().longValue(), false);
            record.setValor(filename);
            record.setAlttext(element.getAltText());
            if (element.getPosition() != null) {
                record.setPosition(element.getPosition());
            }
        } else {
            record.setValor(element.getValue());
        }
        return record;
    }

    private CpanelElementosComEventoRecord createNewComElemRecord(EventCommunicationElementDTO element, Long sessionId, Integer languageId) {
        CpanelElementosComEventoRecord newRecord = new CpanelElementosComEventoRecord();
        newRecord.setIdsesion(sessionId.intValue());
        newRecord.setIdtag(element.getTagId());
        newRecord.setIdioma(languageId);
        newRecord.setPosition(CommonUtils.ifNull(element.getPosition(), EventCommunicationElementService.DEFAULT_COMELEMENT_POSITION));
        newRecord.setDestino(1);
        newRecord.setAlttext(element.getAltText());
        return newRecord;
    }

    private List<Long> obtainSessionIdsWithoutElement(List<CpanelElementosComEventoRecord> elementRecords, List<Long> ids) {
        List<Long> elementRecordsSessionIds = elementRecords.stream()
                .map(CpanelElementosComEventoRecord::getIdsesion).map(Integer::longValue).collect(Collectors.toList());
        return ids.stream().filter(id -> !elementRecordsSessionIds.contains(id))
                .collect(Collectors.toList());
    }

    public void cloneCommunicationElements(Long eventId, Long sessionId, Long newSessionId) {
        List<EventCommunicationElementDTO> sessionComElements = findCommunicationElements(eventId, sessionId, null);
        for (EventCommunicationElementDTO sessionComElement : sessionComElements) {
            if (EventTagType.isImage(EventTagType.getTagTypeById(sessionComElement.getTagId()))) {
                try {
                    URL url = new URL(sessionComElement.getValue());
                    BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
                    byte[] imageData = IOUtils.toByteArray(bis);
                    sessionComElement.setImageBinary(Optional.of(new String(Base64.getEncoder().encode(imageData))));
                } catch (IOException e) {
                    LOGGER.warn("Error cloning session com-element for target id: " + newSessionId, e);
                }
            }
        }
        updateCommunicationElements(eventId, newSessionId, sessionComElements);

        SessionRecord sourceSession = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        SessionRecord targetSession = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, newSessionId);

        sessionTicketCommunicationElementService.cloneTicketElements(
                sourceSession,
                targetSession,
                TicketCommunicationElementCategory.PDF);
        sessionTicketCommunicationElementService.cloneTicketElements(
                sourceSession,
                targetSession,
                TicketCommunicationElementCategory.TICKET_OFFICE);
        sessionTicketCommunicationElementService.clonePassbookElements(eventId, sessionId, newSessionId);
    }
}

package es.onebox.event.sessions.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.services.CommonTicketCommunicationElementService;
import es.onebox.event.communicationelements.dto.CommunicationElementDTO;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.PassbookCommunicationElementBulkDTO;
import es.onebox.event.sessions.dto.TicketCommunicationElementBulkDTO;
import es.onebox.jooq.annotation.MySQLWrite;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class SessionTicketCommunicationElementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTicketCommunicationElementService.class);

    @Autowired
    private StaticDataContainer staticDataContainer;
    @Autowired
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Autowired
    private SessionValidationHelper sessionValidationHelper;
    @Autowired
    private CommonTicketCommunicationElementService commonTicketCommunicationElementService;

    public void updateSessionPassbookCommunicationElements(Long eventId, Long sessionId, HashSet<PassbookCommunicationElementDTO> communicationElements) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        commonTicketCommunicationElementService.updateSessionPassbookCommElements(communicationElements, session);
    }

    public void updateSessionPassbookCommunicationElementsBulk(Long eventId, PassbookCommunicationElementBulkDTO dto) {
        Map<Integer, SessionRecord> sessions = sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, dto.getIds());
        sessions.values().forEach(v ->
                commonTicketCommunicationElementService.updateSessionPassbookCommElements(dto.getValues(), v));
    }

    public List<PassbookCommunicationElementDTO> getSessionPassbookCommunicationElements(Long eventId, Long sessionId,
                                                                                         PassbookCommunicationElementFilter filter) {
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        return commonTicketCommunicationElementService.getSessionPassbookCommunicationElements(filter, sessionId);
    }

    public void deleteSessionPassbookCommunicationElement(Long sessionId, PassbookCommunicationElementTagType tag, String language) {
        commonTicketCommunicationElementService.deleteSessionPassbookCommElement(tag, language, sessionId);
    }

    public void deleteSessionPassbookCommunicationElementBulk(Long eventId, List<Long> sessionIds, PassbookCommunicationElementTagType tag,
                                                              String language) {
        sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, sessionIds);
        sessionIds.forEach(sessionId ->
                commonTicketCommunicationElementService.deleteSessionPassbookCommElement(tag, language, sessionId));
    }
    public void deleteSessionPassbookImageCommunicationElementsBulk(Long eventId, List<Long> sessionIds, String language) {
        sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, sessionIds);
        sessionIds.forEach(sessionId ->
                commonTicketCommunicationElementService.deleteAllSessionPassbookImages(language, sessionId));
    }

    public List<TicketCommunicationElementDTO> getSessionCommunicationElements(Long eventId, Long sessionId,
                                                                               TicketCommunicationElementCategory type,
                                                                               TicketCommunicationElementFilter filter){
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        return commonTicketCommunicationElementService.getSessionCommunicationElements(session, type, filter);
    }

    @MySQLWrite
    public void updateSessionCommunicationElements(Long eventId, Long sessionId, TicketCommunicationElementCategory type,
                                                   Set<TicketCommunicationElementDTO> elements){
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        commonTicketCommunicationElementService.updateSessionCommunicationElements(session, type, elements);
    }

    @MySQLWrite
    public void updateSessionCommunicationElementsBulk(Long eventId, TicketCommunicationElementCategory type,
                                                       TicketCommunicationElementBulkDTO dto){
        Map<Integer, SessionRecord> sessions = sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, dto.getIds());
        sessions.values().forEach(v ->
                commonTicketCommunicationElementService.updateTicketCommElementsWithRetries(
                        v, type, dto.getValues(), 10)
        );
    }

    @MySQLWrite
    public void deleteSessionCommunicationElements(Long eventId, Long sessionId, TicketCommunicationElementCategory type,
                                                   String language, TicketCommunicationElementTagType tag){
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        commonTicketCommunicationElementService.deleteSessionCommunicationElements(session, type, language, tag);
    }

    public void deleteSessionCommunicationElementBulk(Long eventId, List<Long> sessionIds, TicketCommunicationElementCategory type,
                                                          String language, TicketCommunicationElementTagType tag) {
        Map<Integer, SessionRecord> sessionRecords =  sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, sessionIds);
        commonTicketCommunicationElementService.deleteSessionCommunicationElementsBulk(sessionRecords.values(), type, language, tag);
    }

    @MySQLWrite
    public void deleteAllImageSessionCommunicationElementsBulk(Long eventId, List<Long> sessionIds, TicketCommunicationElementCategory type,
                                                               String language) {
        Integer languageId = Optional.ofNullable(staticDataContainer.getLanguageByCode(language)).orElseThrow(() ->
                ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));
        Map<Integer, SessionRecord> sessionRecords =  sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, sessionIds);
        sessionRecords.values().forEach(sessionRecord ->
                commonTicketCommunicationElementService.deleteAllSessionCommunicationImageElements(sessionRecord, type, languageId));
    }

    @MySQLWrite
    public void cloneTicketElements(SessionRecord sourceSession,
                                    SessionRecord targetSession,
                                    TicketCommunicationElementCategory ticketType){
        List<TicketCommunicationElementDTO> ticketElements =
                commonTicketCommunicationElementService.getSessionCommunicationElements(sourceSession, ticketType,null);

        if(CollectionUtils.isNotEmpty(ticketElements)) {
            ticketElements.stream()
                    .filter(ticketElement -> ticketElement.getTag().isImage())
                    .forEach(ticketImage -> {
                        try { cloneImageBinary(ticketImage);}
                        catch (IOException ioe) {
                            LOGGER.warn("Error cloning session " + ticketType + " image for target id: " + targetSession.getSessionId(), ioe);
                        }
                    });

            commonTicketCommunicationElementService.updateSessionCommunicationElements(
                    targetSession,
                    ticketType,
                    Set.copyOf(ticketElements)
            );
        }
    }

    @MySQLWrite
    public void clonePassbookElements(Long eventId, Long sessionId, Long newSessionId){
        List<PassbookCommunicationElementDTO> passbookElements = getSessionPassbookCommunicationElements(eventId, sessionId, null);

        if(CollectionUtils.isNotEmpty(passbookElements)) {
            passbookElements.stream()
                    .filter(passbookElement -> passbookElement.getTag().isImage())
                    .forEach(passbookImage -> {
                        try { cloneImageBinary(passbookImage); }
                        catch (IOException ioe) {
                            LOGGER.warn("Error cloning session " + passbookImage.getTag() + " image for target id: " + newSessionId, ioe);
                        }
                    });

            updateSessionPassbookCommunicationElements(
                    eventId,
                    newSessionId,
                    new HashSet<>(Set.copyOf(passbookElements))
            );
        }
    }

    private void cloneImageBinary(CommunicationElementDTO image) throws IOException{
        URL url = new URL(image.getValue());
        BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
        byte[] imageData = IOUtils.toByteArray(bis);
        image.setImageBinary(new String(Base64.getEncoder().encode(imageData)));
    }
}

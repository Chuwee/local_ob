package es.onebox.mgmt.sessions;

import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.repository.SessionTicketContentsRepository;
import es.onebox.mgmt.events.converter.TicketContentsConverter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePrinterDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePrinterFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentTextFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentTextPDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsTextListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentsImagePassbookListDTO;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.sessions.dto.SessionTicketContentTextPassbookDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsTextPassbookListDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkPDFDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateTextsBulkDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionTicketContentsBulk;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SessionTicketContentsService {

    @Autowired
    private ValidationService validationService;
    @Autowired
    private MasterdataService masterdataService;
    @Autowired
    private SessionTicketContentsRepository sessionTicketContentsRepository;

    public void updateSessionPassbookContentsTexts(Long eventId, Long sessionId, SessionTicketContentsTextPassbookListDTO contents) {
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> ticketCommElems = TicketContentsConverter.fromTicketTextContent(contents, languages, event);
        sessionTicketContentsRepository.updateCommunicationElements(eventId, sessionId, ticketCommElems, TicketCommunicationElementCategory.PASSBOOK);
    }

    public SessionTicketContentsTextPassbookListDTO getSessionPassbookContentsTexts(Long eventId, Long sessionId, EventTicketContentTextPassbookFilter filter) {
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.sessionTicketContentsRepository.findCommunicationElements(eventId, sessionId, msFilter, TicketCommunicationElementCategory.PASSBOOK);
        SessionTicketContentsTextPassbookListDTO result = TicketContentsConverter.fromSessionMsTicketPassbookTextContent(response);
        result.sort(Comparator.comparing(SessionTicketContentTextPassbookDTO::getLanguage));
        return result;
    }

    public TicketContentsImagePassbookListDTO getSessionPassbookContentsImages(Long eventId, Long sessionId, TicketContentImagePassbookFilter filter) {
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.sessionTicketContentsRepository.findCommunicationElements(eventId, sessionId, msFilter, TicketCommunicationElementCategory.PASSBOOK);
        TicketContentsImagePassbookListDTO result = TicketContentsConverter.fromMsTicketPassbookImageContent(response);
        result.sort(Comparator.comparing(TicketContentImagePassbookDTO::getLanguage));
        return result;
    }

    public void updateSessionPassbookContentsImages(Long eventId, Long sessionId, TicketContentsImagePassbookListDTO contents) {
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketImageContent(contents, languages, event);
        this.sessionTicketContentsRepository.updateCommunicationElements(eventId, sessionId, commElements, TicketCommunicationElementCategory.PASSBOOK);
    }

    public void deleteSessionTicketContentPassbookImage(Long eventId, Long sessionId, String language, TicketContentImagePassbookType type) {
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteCommunicationElements(eventId, sessionId, languageCode, type.getTag(), TicketCommunicationElementCategory.PASSBOOK);
    }

    public void deleteSessionTicketContentBulkPassbookImage(Long eventId, List<Long> sessionIds, String language, TicketContentImagePassbookType type) {
        Event event = validationService.getAndCheckEvent(eventId);
        sessionIds.forEach(id -> validationService.getAndCheckOnlySession(eventId, id));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteCommunicationElementBulk(eventId, sessionIds, languageCode, type.getTag(), TicketCommunicationElementCategory.PASSBOOK);
    }
    public void deleteSessionTicketContentBulkPassbookImages(Long eventId, List<Long> sessionIds, String language) {
        Event event = validationService.getAndCheckEvent(eventId);
        sessionIds.forEach(id -> validationService.getAndCheckOnlySession(eventId, id));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteImageCommunicationElementsBulk(eventId, sessionIds, languageCode, TicketCommunicationElementCategory.PASSBOOK);
    }

    public SessionTicketContentsTextListDTO getSessionTicketTextContents(Long eventId, Long sessionId, SessionTicketContentTextFilter filter, TicketCommunicationElementCategory type){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.sessionTicketContentsRepository.findCommunicationElements(eventId, sessionId, msFilter, type);
        SessionTicketContentsTextListDTO result = TicketContentsConverter.fromSessionMsTicketTextContent(response);
        result.sort(Comparator.comparing(SessionTicketContentTextPDFDTO::getLanguage));

        return result;
    }

    public void updateSessionTicketTextContents(Long eventId, Long sessionId, SessionTicketContentsTextListDTO contents, TicketCommunicationElementCategory type){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> ticketCommElems = TicketContentsConverter.fromTicketTextContent(contents, languages, event);
        sessionTicketContentsRepository.updateCommunicationElements(eventId, sessionId, ticketCommElems, type);
    }

    public SessionTicketContentsImagePDFListDTO getSessionTicketPDFImageContents(Long eventId, Long sessionId, SessionTicketContentImagePDFFilter filter){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.sessionTicketContentsRepository.findCommunicationElements(eventId, sessionId, msFilter, TicketCommunicationElementCategory.PDF);
        SessionTicketContentsImagePDFListDTO result = TicketContentsConverter.fromSessionMsTicketPdfImageContent(response);
        result.sort(Comparator.comparing(SessionTicketContentImagePDFDTO::getLanguage));

        return result;
    }

    public void updateSessionTicketPDFImageContents(Long eventId, Long sessionId, SessionTicketContentsImagePDFListDTO contents){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> ticketCommElems = TicketContentsConverter.fromTicketPdfImageContent(contents, languages, event);
        sessionTicketContentsRepository.updateCommunicationElements(eventId, sessionId, ticketCommElems, TicketCommunicationElementCategory.PDF);
    }

    public void deleteTicketPDFImageContents(Long eventId, Long sessionId, String language, TicketContentImagePDFType type){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        sessionTicketContentsRepository.deleteCommunicationElements(eventId, sessionId, languageCode, type.getTag(), TicketCommunicationElementCategory.PDF);
    }

    public void deleteSessionTicketContentBulkPDFImage(Long eventId, List<Long> sessionIds, String language, TicketContentImagePDFType type) {
        Event event = validationService.getAndCheckEvent(eventId);
        sessionIds.forEach(id -> validationService.getAndCheckOnlySession(eventId, id));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteCommunicationElementBulk(eventId, sessionIds, languageCode, type.getTag(), TicketCommunicationElementCategory.PDF);
    }

    public void deleteSessionTicketContentBulkPDFImages(Long eventId, List<Long> sessionIds, String language) {
        Event event = validationService.getAndCheckEvent(eventId);
        sessionIds.forEach(id -> validationService.getAndCheckOnlySession(eventId, id));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteImageCommunicationElementsBulk(eventId, sessionIds, languageCode, TicketCommunicationElementCategory.PDF);
    }

    public SessionTicketContentsImagePrinterListDTO getSessionTicketPrinterImageContents(Long eventId, Long sessionId, SessionTicketContentImagePrinterFilter filter){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.sessionTicketContentsRepository.findCommunicationElements(eventId, sessionId, msFilter, TicketCommunicationElementCategory.TICKET_OFFICE);
        SessionTicketContentsImagePrinterListDTO result = TicketContentsConverter.fromSessionMsTicketPrinterImageContent(response);
        result.sort(Comparator.comparing(SessionTicketContentImagePrinterDTO::getLanguage));

        return result;
    }

    public void updateSessionTicketPrinterImageContents(Long eventId, Long sessionId, SessionTicketContentsImagePrinterListDTO contents){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> ticketCommElems = TicketContentsConverter.fromTicketImageContent(contents, languages, event);
        sessionTicketContentsRepository.updateCommunicationElements(eventId, sessionId, ticketCommElems, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    public void deleteTicketPrinterImageContents(Long eventId, Long sessionId, String language, TicketContentImagePrinterType type){
        Event event = validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckOnlySession(eventId, sessionId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        sessionTicketContentsRepository.deleteCommunicationElements(eventId, sessionId, languageCode, type.getTag(), TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    public void deleteSessionTicketContentBulkPrinterImage(Long eventId, List<Long> sessionIds, String language, TicketContentImagePrinterType type) {
        Event event = validationService.getAndCheckEvent(eventId);
        sessionIds.forEach(id -> validationService.getAndCheckOnlySession(eventId, id));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteCommunicationElementBulk(eventId, sessionIds, languageCode, type.getTag(), TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    public void deleteSessionTicketContentBulkPrinterImages(Long eventId, List<Long> sessionIds, String language) {
        Event event = validationService.getAndCheckEvent(eventId);
        sessionIds.forEach(id -> validationService.getAndCheckOnlySession(eventId, id));
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.sessionTicketContentsRepository.deleteImageCommunicationElementsBulk(eventId, sessionIds, languageCode, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    public void updateSessionTicketTextContentsBulk(Long eventId, TicketCommunicationElementCategory type,
                                                    SessionTicketContentsUpdateTextsBulkDTO<?> dtoIn){
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        UpdateSessionTicketContentsBulk dtoOut = TicketContentsConverter.fromTicketTextContent(dtoIn, languages, event);
        sessionTicketContentsRepository.updateCommunicationElementsBulk(eventId, dtoOut, type);
    }

    public void updateSessionTicketImageContentsBulk(Long eventId, TicketCommunicationElementCategory type,
                                                     SessionTicketContentsUpdateImagesBulkDTO<?> dtoIn){
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        UpdateSessionTicketContentsBulk dtoOut = TicketContentsConverter.fromTicketImageContent(dtoIn, languages, event);
        sessionTicketContentsRepository.updateCommunicationElementsBulk(eventId, dtoOut, type);
    }

    public void updateSessionTicketPdfImageContentsBulk(Long eventId, TicketCommunicationElementCategory type,
                                                        SessionTicketContentsUpdateImagesBulkPDFDTO dtoIn){
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        UpdateSessionTicketContentsBulk dtoOut = TicketContentsConverter.fromTicketPdfImageContent(dtoIn, languages, event);
        sessionTicketContentsRepository.updateCommunicationElementsBulk(eventId, dtoOut, type);
    }
}

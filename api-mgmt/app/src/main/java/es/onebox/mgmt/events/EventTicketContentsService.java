package es.onebox.mgmt.events;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.repository.EventTicketContentsRepository;
import es.onebox.mgmt.events.converter.TicketContentsConverter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePrinterDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePrinterFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPassbookDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPassbookListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentsImagePassbookListDTO;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EventTicketContentsService {

    private final EventTicketContentsRepository eventTicketContentsRepository;
    private final MasterdataService masterdataService;
    private final ValidationService validationService;

    @Autowired
    public EventTicketContentsService(EventTicketContentsRepository eventTicketContentsRepository,
            MasterdataService masterdataService,ValidationService validationService) {
        this.eventTicketContentsRepository = eventTicketContentsRepository;
        this.masterdataService = masterdataService;
        this.validationService = validationService;
    }

    public EventTicketContentsTextPDFListDTO getEventTicketContentsTexts(final Long eventId, EventTicketContentTextPDFFilter filter, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            category = mapSourceOfContents(category);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.eventTicketContentsRepository.findCommunicationElements(eventId, msFilter, category);
        EventTicketContentsTextPDFListDTO result = TicketContentsConverter.fromMsTicketTextContent(response);
        result.sort(Comparator.comparing(EventTicketContentTextPDFDTO::getLanguage));
        return result;
    }

    public void updateEventTicketContentsTexts(final Long eventId, EventTicketContentsTextPDFListDTO contents, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketTextContent(contents, languages, event);
        this.eventTicketContentsRepository.updateCommunicationElements(eventId, commElements, category);
    }

    public EventTicketContentsImagePDFListDTO getEventTicketContentsPDFImages(Long eventId, EventTicketContentImagePDFFilter filter, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            category = mapSourceOfContents(category);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.eventTicketContentsRepository.findCommunicationElements(eventId, msFilter, category);
        EventTicketContentsImagePDFListDTO result = TicketContentsConverter.fromMsTicketPdfImageContent(response);
        result.sort(Comparator.comparing(EventTicketContentImagePDFDTO::getLanguage));
        return result;
    }

    public void updateEventTicketContentsPDFImages(final Long eventId, EventTicketContentsImagePDFListDTO contents, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketPdfImageContent(contents, languages, event);
        this.eventTicketContentsRepository.updateCommunicationElements(eventId, commElements, category);
    }

    public void deleteEventTicketContentPDFImage(final Long eventId, String language, TicketContentImagePDFType type, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.eventTicketContentsRepository.deleteCommunicationElementImage(eventId, languageCode, type.getTag(), category);
    }

    public EventTicketContentsImagePrinterListDTO getEventTicketContentsPrinterImages(Long eventId, EventTicketContentImagePrinterFilter filter, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            category = mapSourceOfContents(category);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.eventTicketContentsRepository.findCommunicationElements(eventId, msFilter, category);
        EventTicketContentsImagePrinterListDTO result = TicketContentsConverter.fromMsTicketPrinterImageContent(response);
        result.sort(Comparator.comparing(EventTicketContentImagePrinterDTO::getLanguage));
        return result;
    }

    public void updateEventTicketContentsPrinterImages(Long eventId, EventTicketContentsImagePrinterListDTO contents, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketImageContent(contents, languages, event);
        this.eventTicketContentsRepository.updateCommunicationElements(eventId, commElements, category);
    }

    public void deleteEventTicketContentPrinterImage(Long eventId, String language, TicketContentImagePrinterType type, TicketCommunicationElementCategory category) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (isInvitationAndUsesTicketContent(category, event)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.eventTicketContentsRepository.deleteCommunicationElementImage(eventId, languageCode, type.getTag(), category);
    }

    public TicketContentsImagePassbookListDTO getEventPassbookContentsImages(Long eventId, TicketContentImagePassbookFilter filter) {
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.eventTicketContentsRepository.findCommunicationElements(eventId, msFilter, TicketCommunicationElementCategory.PASSBOOK);
        TicketContentsImagePassbookListDTO result = TicketContentsConverter.fromMsTicketPassbookImageContent(response);
        result.sort(Comparator.comparing(TicketContentImagePassbookDTO::getLanguage));
        return result;
    }

    public EventTicketContentsTextPassbookListDTO getEventPassbookContentsTexts(Long eventId, EventTicketContentTextPassbookFilter filter) {
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<TicketCommunicationElement> response = this.eventTicketContentsRepository.findCommunicationElements(eventId, msFilter, TicketCommunicationElementCategory.PASSBOOK);
        EventTicketContentsTextPassbookListDTO result = TicketContentsConverter.fromMsTicketPassbookTextContent(response);
        result.sort(Comparator.comparing(EventTicketContentTextPassbookDTO::getLanguage));
        return result;
    }

    public void updateEventPassbookContentsImages(Long eventId, TicketContentsImagePassbookListDTO contents) {
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketImageContent(contents, languages, event);
        this.eventTicketContentsRepository.updateCommunicationElements(eventId, commElements, TicketCommunicationElementCategory.PASSBOOK);
    }

    public void updateEventPassbookContentsTexts(final Long eventId, EventTicketContentsTextPassbookListDTO contents) {
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketTextContent(contents, languages, event);
        this.eventTicketContentsRepository.updateCommunicationElements(eventId, commElements, TicketCommunicationElementCategory.PASSBOOK);
    }

    public void deleteEventTicketContentPassbookImage(Long eventId, String language, TicketContentImagePassbookType type) {
        Event event = validationService.getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.eventTicketContentsRepository.deleteCommunicationElementImage(eventId, languageCode, type.getTag(), TicketCommunicationElementCategory.PASSBOOK);
    }

    private static boolean isInvitationAndUsesTicketContent(TicketCommunicationElementCategory category, Event event) {
        return (TicketCommunicationElementCategory.INVITATION_PDF.equals(category)
                || TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE.equals(category))
                && event.getInvitationUseTicketTemplate();
    }

    private static TicketCommunicationElementCategory mapSourceOfContents(TicketCommunicationElementCategory category) {
        switch (category) {
            case INVITATION_PDF:
                return TicketCommunicationElementCategory.PDF;
            case INVITATION_TICKET_OFFICE:
                return TicketCommunicationElementCategory.TICKET_OFFICE;
            default:
                break;
        }
        return category;
    }
}

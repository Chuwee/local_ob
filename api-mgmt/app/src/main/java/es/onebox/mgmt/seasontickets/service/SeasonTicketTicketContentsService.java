package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketTicketContentsRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SeasonTicketTicketContentsService {

    private final SeasonTicketTicketContentsRepository seasonTicketTicketContentsRepository;
    private final MasterdataService masterdataService;
    private final SeasonTicketService seasonTicketService;

    @Autowired
    public SeasonTicketTicketContentsService(SeasonTicketTicketContentsRepository seasonTicketTicketContentsRepository,
                                             MasterdataService masterdataService,
                                             SeasonTicketService seasonTicketService) {
        this.seasonTicketTicketContentsRepository = seasonTicketTicketContentsRepository;
        this.masterdataService = masterdataService;
        this.seasonTicketService = seasonTicketService;
    }

    public EventTicketContentsTextPDFListDTO getSeasonTicketTicketContentsTexts(final Long seasonTicketId, EventTicketContentTextPDFFilter filter, TicketCommunicationElementCategory category) {
        SeasonTicket st = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, st)) {
            category = mapSourceOfContents(category);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, st.getLanguages());
        List<TicketCommunicationElement> response = this.seasonTicketTicketContentsRepository.findCommunicationElements(seasonTicketId, msFilter, category);
        EventTicketContentsTextPDFListDTO result = TicketContentsConverter.fromMsTicketTextContent(response);
        result.sort(Comparator.comparing(EventTicketContentTextPDFDTO::getLanguage));
        return result;
    }

    public void updateSeasonTicketTicketContentsTexts(final Long seasonTicketId, EventTicketContentsTextPDFListDTO contents, TicketCommunicationElementCategory category) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, seasonTicket)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketTextContent(contents, languages, seasonTicket);
        this.seasonTicketTicketContentsRepository.updateCommunicationElements(seasonTicketId, commElements, category);
    }

    public EventTicketContentsImagePDFListDTO getSeasonTicketTicketContentsPDFImages(Long seasonTicketId, EventTicketContentImagePDFFilter filter, TicketCommunicationElementCategory category) {
        SeasonTicket st = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, st)) {
            category = mapSourceOfContents(category);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, st.getLanguages());
        List<TicketCommunicationElement> response = this.seasonTicketTicketContentsRepository.findCommunicationElements(seasonTicketId, msFilter, category);
        EventTicketContentsImagePDFListDTO result = TicketContentsConverter.fromMsTicketPdfImageContent(response);
        result.sort(Comparator.comparing(EventTicketContentImagePDFDTO::getLanguage));
        return result;
    }

    public void updateSeasonTicketTicketContentsPDFImages(final Long seasonTicketId, EventTicketContentsImagePDFListDTO contents, TicketCommunicationElementCategory category) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, seasonTicket)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketPdfImageContent(contents, languages, seasonTicket);
        this.seasonTicketTicketContentsRepository.updateCommunicationElements(seasonTicketId, commElements, category);
    }

    public void deleteSeasonTicketTicketContentPDFImage(final Long seasonTicketId, String language, TicketContentImagePDFType type, TicketCommunicationElementCategory category) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, seasonTicket)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(seasonTicket.getLanguages(), languages, language);
        this.seasonTicketTicketContentsRepository.deleteCommunicationElementImage(seasonTicketId, languageCode, type.getTag(), category);
    }

    public EventTicketContentsImagePrinterListDTO getSeasonTicketTicketContentsPrinterImages(Long seasonTicketId, EventTicketContentImagePrinterFilter filter, TicketCommunicationElementCategory category) {
        SeasonTicket st = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, st)) {
            category = mapSourceOfContents(category);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, st.getLanguages());
        List<TicketCommunicationElement> response = this.seasonTicketTicketContentsRepository.findCommunicationElements(seasonTicketId, msFilter, category);
        EventTicketContentsImagePrinterListDTO result = TicketContentsConverter.fromMsTicketPrinterImageContent(response);
        result.sort(Comparator.comparing(EventTicketContentImagePrinterDTO::getLanguage));
        return result;
    }

    public void updateSeasonTicketTicketContentsPrinterImages(Long seasonTicketId, EventTicketContentsImagePrinterListDTO contents, TicketCommunicationElementCategory category) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, seasonTicket)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketImageContent(contents, languages, seasonTicket);
        this.seasonTicketTicketContentsRepository.updateCommunicationElements(seasonTicketId, commElements, category);
    }

    public void deleteSeasonTicketTicketContentPrinterImage(Long seasonTicketId, String language, TicketContentImagePrinterType type, TicketCommunicationElementCategory category) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (isInvitationAndUsesTicketContent(category, seasonTicket)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.COMMUNICATION_ELEMENTS_HIERARCHY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(seasonTicket.getLanguages(), languages, language);
        this.seasonTicketTicketContentsRepository.deleteCommunicationElementImage(seasonTicketId, languageCode, type.getTag(), category);
    }


    public EventTicketContentsTextPassbookListDTO getSeasonTicketPassbookContentsTexts(Long seasonTicketId, EventTicketContentTextPassbookFilter filter) {
        SeasonTicket st = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, st.getLanguages());
        List<TicketCommunicationElement> response = this.seasonTicketTicketContentsRepository.findCommunicationElements(seasonTicketId, msFilter, TicketCommunicationElementCategory.PASSBOOK);
        EventTicketContentsTextPassbookListDTO result = TicketContentsConverter.fromMsTicketPassbookTextContent(response);
        result.sort(Comparator.comparing(EventTicketContentTextPassbookDTO::getLanguage));
        return result;
    }

    public void updateSeasonTicketPassbookContentsTexts(Long seasonTicketId, EventTicketContentsTextPassbookListDTO contents) {
        SeasonTicket st = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketTextContent(contents, languages, st);
        this.seasonTicketTicketContentsRepository.updateCommunicationElements(seasonTicketId, commElements, TicketCommunicationElementCategory.PASSBOOK);
    }

    public TicketContentsImagePassbookListDTO getSeasonTicketTicketContentsPassbookImages(Long seasonTicketId, TicketContentImagePassbookFilter filter) {
        SeasonTicket st = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        CommunicationElementFilter<String> msFilter = TicketContentsConverter.convertCommElementFilter(filter, languages, st.getLanguages());
        List<TicketCommunicationElement> response = this.seasonTicketTicketContentsRepository.findCommunicationElements(seasonTicketId, msFilter, TicketCommunicationElementCategory.PASSBOOK);
        TicketContentsImagePassbookListDTO result = TicketContentsConverter.fromMsTicketPassbookImageContent(response);
        result.sort(Comparator.comparing(TicketContentImagePassbookDTO::getLanguage));
        return result;
    }

    public void updateSeasonTicketTicketContentsPassbookImages(Long seasonTicketId, TicketContentsImagePassbookListDTO contents) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<TicketCommunicationElement> commElements = TicketContentsConverter.fromTicketImageContent(contents, languages, seasonTicket);
        this.seasonTicketTicketContentsRepository.updateCommunicationElements(seasonTicketId, commElements, TicketCommunicationElementCategory.PASSBOOK);
    }

    public void deleteSeasonTicketTicketContentPassbookImage(Long seasonTicketId, String language, TicketContentImagePassbookType type) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(seasonTicket.getLanguages(), languages, language);
        this.seasonTicketTicketContentsRepository.deleteCommunicationElementImage(seasonTicketId, languageCode, type.getTag(), TicketCommunicationElementCategory.PASSBOOK);
    }

    private static boolean isInvitationAndUsesTicketContent(TicketCommunicationElementCategory category, SeasonTicket seasonTicket) {
        return (TicketCommunicationElementCategory.INVITATION_PDF.equals(category)
                || TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE.equals(category))
                && seasonTicket.getInvitationUseTicketTemplate();
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

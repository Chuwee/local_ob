package es.onebox.mgmt.pricetype;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.datasources.common.dto.PriceTypeCommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.pricetype.converter.PriceTypeTicketContentsConverter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketCommunicationElement;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePASSBOOKDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePASSBOOKFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePDFDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePDFFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePrinterDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePrinterFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextPASSBOOKDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextPASSBOOKFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePASSBOOKListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePDFListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsTextListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsTextPASSBOOKListDTO;
import es.onebox.mgmt.pricetype.repository.PriceTypeTicketContentsRepository;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PriceTypeTicketContentsService {

    private final PriceTypeTicketContentsRepository priceTypeTicketContentsRepository;
    private final MasterdataService masterdataService;
    private final ValidationService validationService;

    @Autowired
    public PriceTypeTicketContentsService(PriceTypeTicketContentsRepository priceTypeTicketContentsRepository,
                                          MasterdataService masterdataService,  ValidationService validationService) {
        this.priceTypeTicketContentsRepository = priceTypeTicketContentsRepository;
        this.masterdataService = masterdataService;
        this.validationService = validationService;
    }

    public PriceTypeTicketContentsTextListDTO getPriceTypeTicketContentsTexts(final Long venueTemplateId,
                                                                              final Long priceTypeId,
                                                                              PriceTypeTicketContentTextFilter filter) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PriceTypeCommunicationElementFilter msFilter = PriceTypeTicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<PriceTypeTicketCommunicationElement> response = this.priceTypeTicketContentsRepository.findPriceTypePdfCommunicationElements(venueTemplateId, priceTypeId, msFilter);
        PriceTypeTicketContentsTextListDTO result = PriceTypeTicketContentsConverter.fromMsTicketTextContent(response);
        result.sort(Comparator.comparing(PriceTypeTicketContentTextDTO::getLanguage));
        return result;
    }

    public void updatePriceTypeTicketContentsTexts(final Long venueTemplateId, final Long priceTypeId,
                                                   PriceTypeTicketContentsTextListDTO contents) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<PriceTypeTicketCommunicationElement> commElements = PriceTypeTicketContentsConverter.fromTicketTextContent(contents, languages, event);
        this.priceTypeTicketContentsRepository.updatePriceTypePdfCommunicationElements(venueTemplateId, priceTypeId, commElements);
    }

    public PriceTypeTicketContentsImagePDFListDTO getPriceTypeTicketContentsPDFImages(Long venueTemplateId, Long priceTypeId,
                                                                                      PriceTypeTicketContentImagePDFFilter filter) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PriceTypeCommunicationElementFilter msFilter = PriceTypeTicketContentsConverter.convertCommElementFilter(filter, languages,
                event.getLanguages());
        List<PriceTypeTicketCommunicationElement> response = this.priceTypeTicketContentsRepository.findPriceTypePdfCommunicationElements(venueTemplateId, priceTypeId, msFilter);
        PriceTypeTicketContentsImagePDFListDTO result = PriceTypeTicketContentsConverter.fromMsTicketPdfImageContent(response);
        result.sort(Comparator.comparing(PriceTypeTicketContentImagePDFDTO::getLanguage));
        return result;
    }

    public void updatePriceTypeTicketContentsPDFImages(final Long venueTemplateId, Long priceTypeId,
                                                       PriceTypeTicketContentsImagePDFListDTO contents) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<PriceTypeTicketCommunicationElement> commElements = PriceTypeTicketContentsConverter.fromTicketImageContent(
                contents, languages, event);
        this.priceTypeTicketContentsRepository.updatePriceTypePdfCommunicationElements(venueTemplateId, priceTypeId,
                commElements);
    }

    public void deletePriceTypeTicketContentPDFImage(final Long venueTemplateId, Long priceTypeId, String language,
                                                     TicketContentImagePDFType type) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.priceTypeTicketContentsRepository.deletePriceTypePdfCommunicationElementImage(venueTemplateId, priceTypeId,
                languageCode, type.getTag());
    }


    public PriceTypeTicketContentsTextListDTO getPriceTypeTicketPrinterContentsTexts(final Long venueTemplateId,
                                                                                     final Long priceTypeId,
                                                                                     PriceTypeTicketContentTextFilter filter) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PriceTypeCommunicationElementFilter msFilter = PriceTypeTicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<PriceTypeTicketCommunicationElement> response = this.priceTypeTicketContentsRepository.findPriceTypePrinterCommunicationElements(venueTemplateId, priceTypeId, msFilter);
        PriceTypeTicketContentsTextListDTO result = PriceTypeTicketContentsConverter.fromMsTicketTextPrinterContent(response);
        result.sort(Comparator.comparing(PriceTypeTicketContentTextDTO::getLanguage));
        return result;
    }

    public void updatePriceTypeTicketPrinterContentsTexts(final Long venueTemplateId, final Long priceTypeId,
                                                          PriceTypeTicketContentsTextListDTO contents) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<PriceTypeTicketCommunicationElement> commElements = PriceTypeTicketContentsConverter.fromTicketTextContent(contents, languages, event);
        this.priceTypeTicketContentsRepository.updatePriceTypePrinterCommunicationElements(venueTemplateId, priceTypeId, commElements);
    }

    public PriceTypeTicketContentsImagePrinterListDTO getPriceTypeTicketContentsPrinterImages(Long venueTemplateId,
                                                                                              Long priceTypeId,
                                                                                              PriceTypeTicketContentImagePrinterFilter filter) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PriceTypeCommunicationElementFilter msFilter = PriceTypeTicketContentsConverter.convertCommElementFilter(filter, languages,
                event.getLanguages());
        List<PriceTypeTicketCommunicationElement> response = this.priceTypeTicketContentsRepository.findPriceTypePrinterCommunicationElements(venueTemplateId, priceTypeId, msFilter);
        PriceTypeTicketContentsImagePrinterListDTO result = PriceTypeTicketContentsConverter.fromMsTicketPrinterImageContent(response);
        result.sort(Comparator.comparing(PriceTypeTicketContentImagePrinterDTO::getLanguage));
        return result;
    }

    public void updatePriceTypeTicketContentsPRINTERImages(final Long venueTemplateId, Long priceTypeId,
                                                           PriceTypeTicketContentsImagePrinterListDTO contents) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<PriceTypeTicketCommunicationElement> commElements = PriceTypeTicketContentsConverter.fromTicketImageContent(contents, languages, event);
        this.priceTypeTicketContentsRepository.updatePriceTypePrinterCommunicationElements(venueTemplateId, priceTypeId, commElements);
    }


    public void deletePriceTypeTicketContentPRINTERImage(final Long venueTemplateId, Long priceTypeId, String language,
                                                         TicketContentImagePrinterType type) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.priceTypeTicketContentsRepository.deletePriceTypePrinterCommunicationElementImage(venueTemplateId, priceTypeId, languageCode, type.getTag());
    }

    private Event getAndCheckVenueTemplate(Long venueTemplateId) {
        VenueTemplate venueTemplate = priceTypeTicketContentsRepository.getVenueTemplate(venueTemplateId);
        if (venueTemplate == null) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        }
        if (venueTemplate.getEventId() == null) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        Event event = validationService.getAndCheckEvent(venueTemplate.getEventId());
        return event;
    }

    private void getAndCheckPriceType(Long venueTemplateId, Long priceTypeId) {
        List<PriceType> priceTypes = priceTypeTicketContentsRepository.getPriceTypes(venueTemplateId);
        if (priceTypes == null || priceTypes.isEmpty()) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.PRICE_TYPE_NOT_FOUND);
        }
        long priceTypeCount = priceTypes.stream().filter(pt -> pt.getId().equals(priceTypeId)).count();
        if (priceTypeCount == 0) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.PRICE_TYPE_NOT_FOUND);
        }
    }

    public PriceTypeTicketContentsTextPASSBOOKListDTO getPriceTypeTicketPassbookContentsTexts(final Long venueTemplateId, final Long priceTypeId, PriceTypeTicketContentTextPASSBOOKFilter filter, TicketCommunicationElementCategory category) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PriceTypeCommunicationElementFilter msFilter = PriceTypeTicketContentsConverter.convertCommElementFilter(filter, languages, event.getLanguages());
        List<PriceTypeTicketCommunicationElement> response = this.priceTypeTicketContentsRepository.findPriceTypePassbookCommunicationElements(venueTemplateId, priceTypeId, msFilter);
        PriceTypeTicketContentsTextPASSBOOKListDTO result = PriceTypeTicketContentsConverter.fromMsTicketTextPassbookContent(response);
        result.sort(Comparator.comparing(PriceTypeTicketContentTextPASSBOOKDTO::getLanguage));
        return result;
    }

    public void updatePriceTypeTicketPassbookContentsTexts(final Long venueTemplateId, final Long priceTypeId, PriceTypeTicketContentsTextPASSBOOKListDTO contents, TicketCommunicationElementCategory category) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<PriceTypeTicketCommunicationElement> commElements = PriceTypeTicketContentsConverter.fromTicketTextContent(contents, languages, event);
        this.priceTypeTicketContentsRepository.updatePriceTypePassbookCommunicationElements(venueTemplateId, priceTypeId, commElements);
    }

    public PriceTypeTicketContentsImagePASSBOOKListDTO getPriceTypeTicketContentsPASSBOOKImages(Long venueTemplateId, Long priceTypeId, PriceTypeTicketContentImagePASSBOOKFilter filter, TicketCommunicationElementCategory category) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PriceTypeCommunicationElementFilter msFilter = PriceTypeTicketContentsConverter.convertCommElementFilter(filter, languages,
                event.getLanguages());
        List<PriceTypeTicketCommunicationElement> response = this.priceTypeTicketContentsRepository.findPriceTypePassbookCommunicationElements(venueTemplateId, priceTypeId, msFilter);
        PriceTypeTicketContentsImagePASSBOOKListDTO result = PriceTypeTicketContentsConverter.fromMsTicketPassbookImageContent(response);
        result.sort(Comparator.comparing(PriceTypeTicketContentImagePASSBOOKDTO::getLanguage));
        return result;
    }

    public void updatePriceTypeTicketContentsPASSBOOKImages(final Long venueTemplateId, Long priceTypeId, PriceTypeTicketContentsImagePASSBOOKListDTO contents, TicketCommunicationElementCategory category) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Set<PriceTypeTicketCommunicationElement> commElements = PriceTypeTicketContentsConverter.fromTicketImageContent(contents, languages, event);
        this.priceTypeTicketContentsRepository.updatePriceTypePassbookCommunicationElements(venueTemplateId, priceTypeId, commElements);
    }

    public void deletePriceTypeTicketContentPASSBOOKImage(final Long venueTemplateId, Long priceTypeId, String language, TicketContentImagePassbookType type, TicketCommunicationElementCategory category) {
        Event event = getAndCheckVenueTemplate(venueTemplateId);
        getAndCheckPriceType(venueTemplateId, priceTypeId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String languageCode = ChannelContentsUtils.checkElementLanguage(event.getLanguages(), languages, language);
        this.priceTypeTicketContentsRepository.deletePriceTypePassbookCommunicationElementImage(venueTemplateId, priceTypeId, languageCode, type.getTag());
    }

    public List<IdNameDTO> getChangedPriceTypeTicketContents(Long venueTemplateId) {
        getAndCheckVenueTemplate(venueTemplateId);
        return  this.priceTypeTicketContentsRepository.findChangedPriceTypeTicketContents(venueTemplateId);
    }

}

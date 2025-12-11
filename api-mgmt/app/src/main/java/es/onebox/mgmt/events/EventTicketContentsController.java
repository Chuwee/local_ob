package es.onebox.mgmt.events;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePrinterFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPassbookListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentsImagePassbookListDTO;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/events/{eventId}/ticket-contents")
public class EventTicketContentsController {

    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String AUDIT_COLLECTION = "TICKETCOMMUNICATIONELEMENTS";
    private static final String AUDIT_SUBCOLLECTION_PDF = "TICKETCOMMUNICATIONELEMENTS_PDF";
    private static final String AUDIT_SUBCOLLECTION_PRINTER = "TICKETCOMMUNICATIONELEMENTS_PRINTER";
    private static final String AUDIT_SUBCOLLECTION_PASSBOOK = "TICKETCOMMUNICATIONELEMENTS_PASSBOOK";


    private final EventTicketContentsService eventTicketContentsService;

    @Autowired
    public EventTicketContentsController(EventTicketContentsService eventTicketTemplateService) {
        this.eventTicketContentsService = eventTicketTemplateService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/texts")
    public EventTicketContentsTextPDFListDTO getTicketsPDFTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                       @BindUsingJackson @Valid EventTicketContentTextPDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketContentsService.getEventTicketContentsTexts(eventId, filter, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @NotEmpty @RequestBody EventTicketContentsTextPDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketContentsService.updateEventTicketContentsTexts(eventId, contents, TicketCommunicationElementCategory.PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/images")
    public EventTicketContentsImagePDFListDTO getTicketsPDFImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @BindUsingJackson @Valid EventTicketContentImagePDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketContentsService.getEventTicketContentsPDFImages(eventId, filter, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @NotEmpty @RequestBody EventTicketContentsImagePDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketContentsService.updateEventTicketContentsPDFImages(eventId, contents, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PDF/images/languages/{language}/types/{type}")
    public void deleteTicketPDFContentsImage(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePDFType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_DELETE);
        this.eventTicketContentsService.deleteEventTicketContentPDFImage(eventId, language, type, TicketCommunicationElementCategory.PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/texts")
    public EventTicketContentsTextPDFListDTO getTicketsPrinterTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                           @BindUsingJackson @Valid EventTicketContentTextPDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketContentsService.getEventTicketContentsTexts(eventId, filter, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrintersTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @NotEmpty @RequestBody EventTicketContentsTextPDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketContentsService.updateEventTicketContentsTexts(eventId, contents, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/images")
    public EventTicketContentsImagePrinterListDTO getTicketsPrinterImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @BindUsingJackson @Valid EventTicketContentImagePrinterFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketContentsService.getEventTicketContentsPrinterImages(eventId, filter, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @NotEmpty @RequestBody EventTicketContentsImagePrinterListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketContentsService.updateEventTicketContentsPrinterImages(eventId, contents, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PRINTER/images/languages/{language}/types/{type}")
    public void deleteTicketPrinterContentsImage(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                 @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePrinterType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_DELETE);
        this.eventTicketContentsService.deleteEventTicketContentPrinterImage(eventId, language, type, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK/texts")
    public EventTicketContentsTextPassbookListDTO getTicketsPassbookTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                                 @BindUsingJackson @Valid EventTicketContentTextPassbookFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketContentsService.getEventPassbookContentsTexts(eventId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PASSBOOK/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePassbookTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                           @Valid @NotEmpty @RequestBody EventTicketContentsTextPassbookListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketContentsService.updateEventPassbookContentsTexts(eventId, contents);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK/images")
    public TicketContentsImagePassbookListDTO getTicketsPassbookImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                              @BindUsingJackson @Valid TicketContentImagePassbookFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketContentsService.getEventPassbookContentsImages(eventId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PASSBOOK/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPassbookImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @Valid @NotEmpty @RequestBody TicketContentsImagePassbookListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketContentsService.updateEventPassbookContentsImages(eventId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PASSBOOK/images/languages/{language}/types/{type}")
    public void deleteTicketPassbookContentsImage(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                  @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePassbookType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.eventTicketContentsService.deleteEventTicketContentPassbookImage(eventId, language, type);
    }
}

package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePrinterFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentTextFilter;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsTextListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookFilter;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentsImagePassbookListDTO;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsTextPassbookListDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkPDFDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkPassbookDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkPrinterDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateTextsBulkPDFDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateTextsBulkPassbookDTO;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/events/{eventId}/sessions")
public class SessionTicketContentsController {

    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String SESSION_ID_MUST_BE_ABOVE_0 = "Session Id must be above 0";

    private static final String AUDIT_COLLECTION = "TICKET_COM_ELEMENTS";
    private static final String AUDIT_SUBCOLLECTION_PASSBOOK = "TICKET_COM_ELEMENTS_PASSBOOK";
    private static final String AUDIT_SUBCOLLECTION_PDF = "TICKET_COM_ELEMENTS_PDF";
    private static final String AUDIT_SUBCOLLECTION_PRINTER = "TICKET_COM_ELEMENTS_PRINTER";

    @Autowired
    private SessionTicketContentsService sessionTicketContentsService;


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/ticket-contents/PASSBOOK/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePassbookTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                           @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                           @Valid @RequestBody SessionTicketContentsTextPassbookListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.sessionTicketContentsService.updateSessionPassbookContentsTexts(eventId, sessionId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/ticket-contents/PASSBOOK/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePassbookTextContentsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                           @Valid @RequestBody SessionTicketContentsUpdateTextsBulkPassbookDTO dto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.sessionTicketContentsService.updateSessionTicketTextContentsBulk(eventId, TicketCommunicationElementCategory.PASSBOOK, dto);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-contents/PASSBOOK/texts")
    public SessionTicketContentsTextPassbookListDTO getTicketsPassbookTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                                 @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                                 @BindUsingJackson @Valid EventTicketContentTextPassbookFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.sessionTicketContentsService.getSessionPassbookContentsTexts(eventId, sessionId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-contents/PASSBOOK/images")
    public TicketContentsImagePassbookListDTO getTicketsPassbookImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                              @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                              @BindUsingJackson @Valid TicketContentImagePassbookFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.sessionTicketContentsService.getSessionPassbookContentsImages(eventId, sessionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/ticket-contents/PASSBOOK/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPassbookImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                  @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                  @Valid @NotEmpty @RequestBody TicketContentsImagePassbookListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.sessionTicketContentsService.updateSessionPassbookContentsImages(eventId, sessionId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/ticket-contents/PASSBOOK/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPassbookImageContentsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                               @Valid @RequestBody SessionTicketContentsUpdateImagesBulkPassbookDTO dto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.sessionTicketContentsService.updateSessionTicketImageContentsBulk(eventId,TicketCommunicationElementCategory.PASSBOOK, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{sessionId}/ticket-contents/PASSBOOK/images/languages/{language}/types/{type}")
    public void deleteTicketPassbookContentsImage(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                  @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                  @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePassbookType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentPassbookImage(eventId, sessionId, language, type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/ticket-contents/PASSBOOK/images/languages/{language}/types/{type}")
    public void deleteTicketPassbookContentsImageBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @PathVariable TicketContentImagePassbookType type,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentBulkPassbookImage(eventId, sessionIds, language, type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/ticket-contents/PASSBOOK/images/languages/{language}")
    public void deleteTicketPassbookContentsImagesBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentBulkPassbookImages(eventId, sessionIds, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-contents/PDF/texts")
    public SessionTicketContentsTextListDTO getTicketPDFTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                     @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                     @BindUsingJackson @Valid SessionTicketContentTextFilter filter){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF,AuditTag.AUDIT_ACTION_SEARCH);

        return sessionTicketContentsService.getSessionTicketTextContents(eventId, sessionId, filter, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/ticket-contents/PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                            @Valid @NotEmpty @RequestBody SessionTicketContentsTextListDTO contents){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketTextContents(eventId, sessionId, contents, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/ticket-contents/PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFTextContentsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                     @Valid @RequestBody SessionTicketContentsUpdateTextsBulkPDFDTO body){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketTextContentsBulk(eventId, TicketCommunicationElementCategory.PDF, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-contents/PDF/images")
    public SessionTicketContentsImagePDFListDTO getTicketPDFImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                          @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                          @BindUsingJackson @Valid SessionTicketContentImagePDFFilter filter){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF);

        return sessionTicketContentsService.getSessionTicketPDFImageContents(eventId, sessionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/ticket-contents/PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                            @Valid @NotEmpty @RequestBody SessionTicketContentsImagePDFListDTO contents){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketPDFImageContents(eventId, sessionId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/ticket-contents/PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFImageContentsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                 @Valid @RequestBody SessionTicketContentsUpdateImagesBulkPDFDTO body){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketPdfImageContentsBulk(eventId, TicketCommunicationElementCategory.PDF, body);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{sessionId}/ticket-contents/PDF/images/languages/{language}/types/{type}")
    public void deleteTicketPDFImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                             @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                             @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePDFType type){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_DELETE);
        sessionTicketContentsService.deleteTicketPDFImageContents(eventId, sessionId, language, type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/ticket-contents/PDF/images/languages/{language}/types/{type}")
    public void deleteTicketPDFImageContentsImageBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @PathVariable TicketContentImagePDFType type,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentBulkPDFImage(eventId, sessionIds, language, type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/ticket-contents/PDF/images/languages/{language}")
    public void deleteTicketPDFImageContentsImagesBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentBulkPDFImages(eventId, sessionIds, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-contents/PRINTER/texts")
    public SessionTicketContentsTextListDTO getTicketPrinterTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                            @BindUsingJackson @Valid SessionTicketContentTextFilter filter){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER);

        return sessionTicketContentsService.getSessionTicketTextContents(eventId, sessionId, filter, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/ticket-contents/PRINTER/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterTextContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                            @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                            @Valid @NotEmpty @RequestBody SessionTicketContentsTextListDTO contents){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketTextContents(eventId, sessionId, contents, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/ticket-contents/PRINTER/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterTextContentsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                     @Valid @RequestBody SessionTicketContentsUpdateTextsBulkPDFDTO body){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketTextContentsBulk(eventId, TicketCommunicationElementCategory.TICKET_OFFICE, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/ticket-contents/PRINTER/images")
    public SessionTicketContentsImagePrinterListDTO getTicketPrinterImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                                              @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                                              @BindUsingJackson @Valid SessionTicketContentImagePrinterFilter filter){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER);

        return sessionTicketContentsService.getSessionTicketPrinterImageContents(eventId, sessionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{sessionId}/ticket-contents/PRINTER/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                             @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                             @Valid @NotEmpty @RequestBody SessionTicketContentsImagePrinterListDTO contents){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketPrinterImageContents(eventId, sessionId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/ticket-contents/PRINTER/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterImageContentsBulk(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                 @Valid @RequestBody SessionTicketContentsUpdateImagesBulkPrinterDTO body){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTicketContentsService.updateSessionTicketImageContentsBulk(eventId, TicketCommunicationElementCategory.TICKET_OFFICE, body);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{sessionId}/ticket-contents/PRINTER/images/languages/{language}/types/{type}")
    public void deleteTicketPrinterImageContents(@PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
                                                 @PathVariable @Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long sessionId,
                                                 @PathVariable @LanguageIETF String language, @PathVariable  TicketContentImagePrinterType type){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_DELETE);
        sessionTicketContentsService.deleteTicketPrinterImageContents(eventId, sessionId, language, type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/ticket-contents/PRINTER/images/languages/{language}/types/{type}")
    public void deleteTicketPrinterImageContentsImageBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @PathVariable TicketContentImagePrinterType type,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentBulkPrinterImage(eventId, sessionIds, language, type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/ticket-contents/PRINTER/images/languages/{language}")
    public void deleteTicketPrinterImageContentsImagesBulk(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @LanguageIETF String language,
            @RequestParam(value = "session_id") @NotEmpty @NotNull List<@Min(value = 1, message = SESSION_ID_MUST_BE_ABOVE_0) Long> sessionIds) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_DELETE);
        this.sessionTicketContentsService.deleteSessionTicketContentBulkPrinterImages(eventId, sessionIds, language);
    }
}

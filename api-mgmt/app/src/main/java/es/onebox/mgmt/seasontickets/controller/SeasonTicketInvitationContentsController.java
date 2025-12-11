package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePrinterFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPDFFilter;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPDFListDTO;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.seasontickets.service.SeasonTicketTicketContentsService;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping(value = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/ticket-invitation-contents")
public class SeasonTicketInvitationContentsController {

    private static final String SEASON_TICKET_ID_MUST_BE_ABOVE_0 = "Season ticket Id must be above 0";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_INVITATION_COMMUNICATION_ELEMENTS";
    private static final String AUDIT_SUBCOLLECTION_PDF = "SEASON_TICKET_INVITATION_COMMUNICATION_ELEMENTS_PDF";
    private static final String AUDIT_SUBCOLLECTION_PRINTER = "SEASON_TICKET_INVITATION_COMMUNICATION_ELEMENTS_PRINTER";

    private final SeasonTicketTicketContentsService seasonTicketContentsService;

    @Autowired
    public SeasonTicketInvitationContentsController(SeasonTicketTicketContentsService seasonTicketTemplateService) {
        this.seasonTicketContentsService = seasonTicketTemplateService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/texts")
    public EventTicketContentsTextPDFListDTO getInvitationPDFTextContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
                                                                          @BindUsingJackson @Valid EventTicketContentTextPDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketContentsService.getSeasonTicketTicketContentsTexts(seasonTicketId, filter, TicketCommunicationElementCategory.INVITATION_PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateInvitationPDFTextContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @Valid  @RequestBody EventTicketContentsTextPDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketContentsService.updateSeasonTicketTicketContentsTexts(seasonTicketId, contents, TicketCommunicationElementCategory.INVITATION_PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/images")
    public EventTicketContentsImagePDFListDTO getInvitationPDFImageContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @BindUsingJackson @Valid EventTicketContentImagePDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketContentsService.getSeasonTicketTicketContentsPDFImages(seasonTicketId, filter, TicketCommunicationElementCategory.INVITATION_PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateInvitationPDFImageContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @Valid  @RequestBody EventTicketContentsImagePDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketContentsService.updateSeasonTicketTicketContentsPDFImages(seasonTicketId, contents, TicketCommunicationElementCategory.INVITATION_PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PDF/images/languages/{language}/types/{type}")
    public void deleteInvitationPDFContentsImage(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
                                                 @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePDFType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_DELETE);
        this.seasonTicketContentsService.deleteSeasonTicketTicketContentPDFImage(seasonTicketId, language, type, TicketCommunicationElementCategory.INVITATION_PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/texts")
    public EventTicketContentsTextPDFListDTO getTicketsPrinterTextContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
                                                                           @BindUsingJackson @Valid EventTicketContentTextPDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketContentsService.getSeasonTicketTicketContentsTexts(seasonTicketId, filter, TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrintersTextContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @Valid @RequestBody EventTicketContentsTextPDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketContentsService.updateSeasonTicketTicketContentsTexts(seasonTicketId, contents, TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/images")
    public EventTicketContentsImagePrinterListDTO getTicketsPrinterImageContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @BindUsingJackson @Valid EventTicketContentImagePrinterFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketContentsService.getSeasonTicketTicketContentsPrinterImages(seasonTicketId, filter, TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterImageContents(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @Valid @RequestBody EventTicketContentsImagePrinterListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketContentsService.updateSeasonTicketTicketContentsPrinterImages(seasonTicketId, contents, TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PRINTER/images/languages/{language}/types/{type}")
    public void deleteTicketPrinterContentsImage(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePrinterType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_DELETE);
        this.seasonTicketContentsService.deleteSeasonTicketTicketContentPrinterImage(seasonTicketId, language, type, TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE);
    }
}

package es.onebox.mgmt.tickettemplates;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketTemplateContentImageType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentImageFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentImageListDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralListDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextListDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
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

@RestController
@Validated
@RequestMapping(value = TicketTemplateContentsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketTemplateContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/ticket-templates/{ticketTemplateId}/ticket-contents";

    private final TicketTemplateContentsService ticketTemplateContentsService;
    private static final String AUDIT_COLLECTION = "TICKET_TEMPLATE_CONTENTS";

    @Autowired
    public TicketTemplateContentsController(TicketTemplateContentsService ticketTemplateContentsService) {
        this.ticketTemplateContentsService = ticketTemplateContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/texts")
    public TicketTemplateContentTextListDTO getTicketContentsTexts(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid TicketTemplateContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return ticketTemplateContentsService.getTicketContentTexts(ticketTemplateId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/texts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTicketContentsTexts(@PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid @RequestBody TicketTemplateContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        ticketTemplateContentsService.updateTicketContentTexts(ticketTemplateId, contents);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/literals")
    public TicketTemplateContentLiteralListDTO getTicketContentsLiterals(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid TicketTemplateContentLiteralFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return ticketTemplateContentsService.getTicketContentLiterals(ticketTemplateId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/literals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTicketContentsLiterals(@PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
                                          @Valid @RequestBody TicketTemplateContentLiteralListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        ticketTemplateContentsService.updateTicketContentLiterals(ticketTemplateId, contents);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/images")
    public ChannelContentImageListDTO<TicketTemplateContentImageType> getPDFChannelContentsImages(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid TicketTemplateContentImageFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return ticketTemplateContentsService.getTicketContentImages(ticketTemplateId, TicketTemplateFormat.PDF, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePDFChannelContentsImages(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid @RequestBody TicketTemplateContentImageListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        ChannelContentsUtils.validateTicketTemplateContents(contents, TicketTemplateFormat.PDF);

        ticketTemplateContentsService.updateTicketContentImages(ticketTemplateId, TicketTemplateFormat.PDF, contents.getImages());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PDF/images/languages/{language}/types/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePDFChannelContentsImage(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @PathVariable @LanguageIETF String language,
            @PathVariable TicketTemplateContentImageType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        ticketTemplateContentsService.deleteTicketContentImages(ticketTemplateId, TicketTemplateFormat.PDF, language, type);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/images")
    public ChannelContentImageListDTO<TicketTemplateContentImageType> getPrinterChannelContentsImages(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid TicketTemplateContentImageFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return ticketTemplateContentsService.getTicketContentImages(ticketTemplateId, TicketTemplateFormat.PRINTER, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrinterChannelContentsImages(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @Valid @RequestBody TicketTemplateContentImageListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        ChannelContentsUtils.validateTicketTemplateContents(contents, TicketTemplateFormat.PRINTER);

        ticketTemplateContentsService.updateTicketContentImages(ticketTemplateId, TicketTemplateFormat.PRINTER, contents.getImages());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PRINTER/images/languages/{language}/types/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrinterChannelContentsImage(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
            @PathVariable @LanguageIETF String language,
            @PathVariable TicketTemplateContentImageType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        ticketTemplateContentsService.deleteTicketContentImages(ticketTemplateId, TicketTemplateFormat.PRINTER, language, type);
    }

}

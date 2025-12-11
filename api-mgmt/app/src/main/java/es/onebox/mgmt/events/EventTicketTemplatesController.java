package es.onebox.mgmt.events;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventTicketTemplateDTO;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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

@RestController
@RequestMapping(EventTicketTemplatesController.BASE_URI)
public class EventTicketTemplatesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/ticket-templates";

    private static final String AUDIT_COLLECTION = "EVENT_TICKET_TEMPLATES";
    private static final String AUDIT_SUBCOLLECTION_PREVIEW = "PREVIEW";
    private static final String AUDIT_SUBCOLLECTION_PASSBOOK = "PASSBOOK";
    private static final String AUDIT_SUBCOLLECTION_PASSBOOK_PREVIEW = "PASSBOOK_PREVIEW";

    private final EventTicketTemplatesService eventTicketTemplatesService;

    @Autowired
    public EventTicketTemplatesController(EventTicketTemplatesService eventTicketTemplatesService) {
        this.eventTicketTemplatesService = eventTicketTemplatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<EventTicketTemplateDTO> getTicketsTemplates(@PathVariable Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.eventTicketTemplatesService.getEventTicketTemplates(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{ticketType}/{templateFormat}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveTicketsTemplates(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                     @PathVariable EventTicketTemplateType ticketType,
                                     @PathVariable TicketTemplateFormatPath templateFormat,
                                     @RequestBody IdDTO templateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketTemplatesService.saveEventTicketTemplate(eventId, ticketType, templateFormat, templateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{ticketType}/PASSBOOK")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void savePassbookTicketsTemplates(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                             @PathVariable EventTicketTemplateType ticketType,
                                             @Valid @RequestBody CodeDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventTicketTemplatesService.saveEventTicketTemplatePassbook(eventId, ticketType, body.getCode());
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{ticketType}/preview")
    public TicketPreviewDTO getTicketPdfPreview(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                @PathVariable EventTicketTemplateType ticketType,
                                                @RequestParam(required = false) String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PREVIEW, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketTemplatesService.getTicketPdfPreview(eventId, ticketType, language);
    }


    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/passbook/preview")
    public TicketPrintResultDTO getTicketPassbookPreview(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                         @RequestParam(required = false) String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK_PREVIEW, AuditTag.AUDIT_ACTION_GET);
        return this.eventTicketTemplatesService.getTicketPassbookPreview(eventId, language);
    }
}

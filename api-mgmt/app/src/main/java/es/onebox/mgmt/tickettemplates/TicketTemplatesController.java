package es.onebox.mgmt.tickettemplates;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.tickettemplates.dto.CloneTicketTemplateRequestDTO;
import es.onebox.mgmt.tickettemplates.dto.CreateTicketTemplateRequestDTO;
import es.onebox.mgmt.tickettemplates.dto.SearchTicketTemplatesResponse;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDesignDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateSearchFilter;
import es.onebox.mgmt.tickettemplates.dto.UpdateTicketTemplateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = TicketTemplatesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketTemplatesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/ticket-templates";

    private static final String AUDIT_COLLECTION = "TICKET_TEMPLATES";

    @Autowired
    private TicketTemplatesService ticketTemplatesService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{ticketTemplateId}")
    public TicketTemplateDTO getTicketTemplate(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return ticketTemplatesService.get(ticketTemplateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public SearchTicketTemplatesResponse getTicketTemplates(@BindUsingJackson @Valid TicketTemplateSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return ticketTemplatesService.search(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{ticketTemplateId}/preview")
    public TicketPreviewDTO getTicketPdfPreview(@PathVariable Long ticketTemplateId,
                                                @RequestParam(required = false) String language) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return ticketTemplatesService.getTicketPdfPreview(ticketTemplateId, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createTicketTemplate(@RequestBody CreateTicketTemplateRequestDTO ticketTemplateData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        Long ticketTemplateId = ticketTemplatesService.create(ticketTemplateData.getName(),
                ticketTemplateData.getEntityId(), ticketTemplateData.getDesignId());
        return new ResponseEntity<>(new IdDTO(ticketTemplateId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/{ticketTemplateId}/clone")
    public IdDTO clone(@PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId,
                       @Valid @RequestBody CloneTicketTemplateRequestDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);
        return this.ticketTemplatesService.clone(ticketTemplateId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{ticketTemplateId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTicketTemplate(@PathVariable Long ticketTemplateId,
                                                             @RequestBody UpdateTicketTemplateRequestDTO ticketTemplateData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (ticketTemplateData.getId() != null && !ticketTemplateData.getId().equals(ticketTemplateId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "ticketTemplateId is different between pathVariable and requestBody", null);
        }
        ticketTemplatesService.update(ticketTemplateId, ticketTemplateData);

    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{ticketTemplateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicketTemplate(@PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long ticketTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        ticketTemplatesService.delete(ticketTemplateId);

    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/designs")
    public List<TicketTemplateDesignDTO> getTicketTemplateDesigns() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return ticketTemplatesService.findDesigns();
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/printers")
    public List<String> getTicketTemplatePrinters() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return ticketTemplatesService.findPrinters();
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/paper-types")
    public List<String> getTicketTemplatePaperTypes() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return ticketTemplatesService.findPaperTypes();
    }

}

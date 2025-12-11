package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.exception.ApiMgmtPassbookErrorCode;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketTicketTemplateDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketTicketTemplatesService;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
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

import java.io.IOException;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/ticket-templates")
public class SeasonTicketTicketTemplatesController {

    private final SeasonTicketTicketTemplatesService seasonTicketTicketTemplatesService;

    private static final String AUDIT_COLLECTION = "SEASON_TICKET_TICKET_TEMPLATES";

    @Autowired
    public SeasonTicketTicketTemplatesController(SeasonTicketTicketTemplatesService seasonTicketTicketTemplatesService) {
        this.seasonTicketTicketTemplatesService = seasonTicketTicketTemplatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<SeasonTicketTicketTemplateDTO> getTicketsTemplates(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.seasonTicketTicketTemplatesService.getSeasonTicketTicketTemplates(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{ticketType}/{templateFormat}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveTicketsTemplates(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                     @PathVariable EventTicketTemplateType ticketType,
                                     @PathVariable TicketTemplateFormat templateFormat,
                                     @RequestBody IdDTO templateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketTicketTemplatesService.saveSeasonTicketTicketTemplate(seasonTicketId, ticketType, templateFormat, templateId);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{ticketType}/PASSBOOK")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void savePassbookTicketsTemplates(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                             @PathVariable EventTicketTemplateType ticketType,
                                             @RequestBody CodeDTO codeDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        if (CommonUtils.isBlank(codeDTO.getCode())) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_DATA_MANDATORY);
        }
        this.seasonTicketTicketTemplatesService.saveTicketTemplatePassbook(seasonTicketId, ticketType, codeDTO.getCode());
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{ticketType}/preview")
    public TicketPreviewDTO getTicketPdfPreview(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                @PathVariable EventTicketTemplateType ticketType,
                                                @RequestParam(required = false) String language) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketTicketTemplatesService.getTicketPdfPreview(seasonTicketId, ticketType, language);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/passbook/preview")
    public TicketPrintResultDTO getTicketPassbookPreview(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                         @RequestParam(required = false) String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketTicketTemplatesService.getTicketPassbookPreview(seasonTicketId, language);
    }
}

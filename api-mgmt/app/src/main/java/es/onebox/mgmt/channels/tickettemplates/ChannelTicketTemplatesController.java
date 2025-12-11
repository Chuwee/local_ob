package es.onebox.mgmt.channels.tickettemplates;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTemplateTicketType;
import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTicketTemplateDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(ChannelTicketTemplatesController.BASE_URI)
public class ChannelTicketTemplatesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/ticket-templates";

    private static final String AUDIT_COLLECTION = "CHANNEL_TICKET_TEMPLATES";

    @Autowired
    private ChannelTicketTemplatesService service;

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/{ticketType}/PASSBOOK")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void savePassbookTicketsTemplates(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                             @PathVariable ChannelTemplateTicketType ticketType,
                                             @RequestBody CodeDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateChannelPassbookTicketTemplates(channelId, ticketType, body.getCode());
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<ChannelTicketTemplateDTO> getTicketsTemplates(@PathVariable Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.service.getChannelTicketTemplates(channelId);
    }

}

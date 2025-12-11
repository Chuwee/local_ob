package es.onebox.mgmt.events.eventchannel;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventTicketTemplateDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(EventChannelTicketTemplatesController.BASE_URI)
public class EventChannelTicketTemplatesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/channels/{channelId}/ticket-templates";

    private static final String AUDIT_COLLECTION = "EVENT_CHANNEL_TICKET_TEMPLATES";

    private final EventChannelTicketTemplatesService eventChannelsTicketTemplatesService;

    @Autowired
    public EventChannelTicketTemplatesController(EventChannelTicketTemplatesService eventChannelTicketTemplatesService) {
        this.eventChannelsTicketTemplatesService = eventChannelTicketTemplatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<EventTicketTemplateDTO> getTicketsTemplates(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.eventChannelsTicketTemplatesService.getEventChannelTicketTemplates(eventId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping( "/{ticketType}/{templateFormat}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveTicketsTemplates(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                     @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                     @PathVariable EventTicketTemplateType ticketType,
                                     @PathVariable TicketTemplateFormatPath templateFormat,
                                     @RequestBody IdDTO templateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventChannelsTicketTemplatesService.saveEventChannelTicketTemplate(eventId, channelId, ticketType, templateFormat, templateId);
    }
}

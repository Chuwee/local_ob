package es.onebox.mgmt.events.eventchannel.commissions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(
        value = EventChannelCommissionsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EventChannelCommissionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/channels/{channelId}";

    private static final String AUDIT_COLLECTION = "EVENTS_CHANNELS";

    private final EventChannelCommissionsService eventChannelCommissionsService;

    @Autowired
    public EventChannelCommissionsController(EventChannelCommissionsService eventChannelCommissionsService) {
        this.eventChannelCommissionsService = eventChannelCommissionsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/channel-commissions")
    public List<CommissionDTO> getChannelCommissions(@PathVariable Long eventId, @PathVariable Long channelId,
                                                     @Valid @RequestParam(value = "type", required = false) List<CommissionTypeDTO> types) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelCommissionsService.getChannelCommissions(eventId, channelId, types);
    }
}

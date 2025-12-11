package es.onebox.mgmt.events.eventchannel.surcharges;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(
        value = EventChannelSurchargesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EventChannelSurchargesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/channels/{channelId}";

    private static final String AUDIT_COLLECTION = "EVENTS_CHANNELS";

    private final EventChannelSurchargesService eventChannelSurchargesService;

    @Autowired
    public EventChannelSurchargesController(EventChannelSurchargesService eventChannelSurchargesService) {
        this.eventChannelSurchargesService = eventChannelSurchargesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/surcharges")
    public List<EventSurchargeDTO> getEventChannelSurcharges(@PathVariable Long eventId, @PathVariable Long channelId,
                                                             @Valid @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelSurchargesService.getEventChannelSurcharges(eventId, channelId, types);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/surcharges")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createEventChannelSurcharges(@PathVariable Long eventId, @PathVariable Long channelId,
                                                                     @Valid @RequestBody EventSurchargeListDTO surcharges) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        eventChannelSurchargesService.createEventChannelSurcharges(eventId, channelId, surcharges);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channel-surcharges")
    public List<SaleRequestSurchargeDTO> getChannelSurcharges(@PathVariable Long eventId, @PathVariable Long channelId,
                                                              @Valid @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelSurchargesService.getChannelSurcharges(eventId, channelId, types);
    }
}

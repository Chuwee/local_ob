package es.onebox.mgmt.events.eventchannel.b2b.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.EventsController;
import es.onebox.mgmt.events.eventchannel.b2b.dto.B2BSeatPublishingConfigDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.B2BSeatPublishingConfigRequestDTO;
import es.onebox.mgmt.events.eventchannel.b2b.service.B2BPublishingConfigService;
import jakarta.validation.Valid;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(B2BPublishingConfigController.BASE_URI)
public class B2BPublishingConfigController {

    public static final String BASE_URI = EventsController.BASE_URI + "/{eventId}/channels/{channelId}/b2b/venue-templates/{venueTemplateId}";
    private static final String AUDIT_COLLECTION = "EVENTS_CHANNELS_B2B";


    private final B2BPublishingConfigService b2BPublishingConfigService;

    @Autowired
    public B2BPublishingConfigController(B2BPublishingConfigService b2BPublishingConfigService) {
        this.b2BPublishingConfigService = b2BPublishingConfigService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR})
    @GetMapping("/publishing-config")
    public B2BSeatPublishingConfigDTO getConfig(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                @PathVariable @Min(value = 1, message = "venueTemplateId must be above 0") Long venueTemplateId) {

        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return b2BPublishingConfigService.getConfig(eventId, channelId, venueTemplateId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR})
    @PutMapping("/publishing-config")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateConfig(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                             @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                             @PathVariable @Min(value = 1, message = "venueTemplateId must be above 0") Long venueTemplateId,
                             @RequestBody @Valid B2BSeatPublishingConfigRequestDTO newConfig) {

        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        b2BPublishingConfigService.updateConfig(eventId, channelId, venueTemplateId, newConfig);
    }
}

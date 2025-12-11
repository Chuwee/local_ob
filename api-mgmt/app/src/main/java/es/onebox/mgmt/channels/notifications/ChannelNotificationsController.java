package es.onebox.mgmt.channels.notifications;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailServerDTO;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTemplatesDTO;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTestDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelNotificationsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelNotificationsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/notifications";
    private static final String AUDIT_COLLECTION = "CHANNEL_NOTIFICATIONS";

    private final ChannelEmailTemplateService channelEmailTemplateService;

    private final ChannelEmailServerService channelEmailServerService;

    @Autowired
    public ChannelNotificationsController(ChannelEmailTemplateService channelEmailTemplateService,
                                          ChannelEmailServerService channelEmailServerService) {
        this.channelEmailTemplateService = channelEmailTemplateService;
        this.channelEmailServerService = channelEmailServerService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/email/templates")
    public ChannelEmailTemplatesDTO getEmailTemplates(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelEmailTemplateService.getEmailTemplates(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/email/templates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmailTemplates(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @RequestBody @Valid ChannelEmailTemplatesDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelEmailTemplateService.updateEmailTemplates(channelId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/email/server")
    public ChannelEmailServerDTO getEmailServer(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelEmailServerService.getConfiguration(channelId);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/email/server")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmailServer(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                  @RequestBody ChannelEmailServerDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelEmailServerService.updateConfiguration(channelId,request);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/email/server/test")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void testEmailServer(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                @Valid @RequestBody ChannelEmailTestDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelEmailServerService.test(channelId, body);
    }
}

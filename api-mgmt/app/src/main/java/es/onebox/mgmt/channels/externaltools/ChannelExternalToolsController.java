package es.onebox.mgmt.channels.externaltools;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsNamesDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelExternalToolsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelExternalToolsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/external-tools";

    private static final String AUDIT_COLLECTION = "CHANNEL_EXTERNAL_TOOLS";

    private final ChannelExternalToolsService channelExternalToolsService;

    @Autowired
    public ChannelExternalToolsController(ChannelExternalToolsService channelExternalToolsService) {
        this.channelExternalToolsService = channelExternalToolsService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public ChannelExternalToolsDTO getExternalTools(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelExternalToolsService.getById(channelId);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{toolName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExternalTool(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                    @PathVariable ChannelExternalToolsNamesDTO toolName,
                                   @RequestBody @Valid ChannelExternalToolDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelExternalToolsService.updateByChannelId(channelId, request, toolName);
    }

    @Secured({ROLE_OPR_MGR})
    @PostMapping("/{toolName}/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetExternalTool(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                  @PathVariable ChannelExternalToolsNamesDTO toolName) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelExternalToolsService.resetExternalTool(channelId, toolName);
    }

}
